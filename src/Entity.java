import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public final class Entity
{
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public int resourceLimit;
    public int resourceCount;
    public int actionPeriod;
    public int animationPeriod;

    public Entity(
            EntityKind kind,
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod)
    {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public static PImage getCurrentImage(Object entity) {
        if (entity instanceof Background) {
            return ((Background)entity).images.get(
                    ((Background)entity).imageIndex);
        }
        else if (entity instanceof Entity) {
            return ((Entity)entity).images.get(((Entity)entity).imageIndex);
        }
        else {
            throw new UnsupportedOperationException(
                    String.format("getCurrentImage not supported for %s",
                            entity));
        }
    }

    public int getAnimationPeriod() {
        switch (kind) {
            case MINER_FULL:
            case MINER_NOT_FULL:
            case ORE_BLOB:
            case QUAKE:
                return animationPeriod;
            default:
                throw new UnsupportedOperationException(
                        String.format("getAnimationPeriod not supported for %s",
                                kind));
        }
    }
    public  void nextImage() {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public static void executeMinerFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                Functions.findNearest(world, entity.position, EntityKind.BLACKSMITH);

        if (fullTarget.isPresent() && Functions.moveToFull(entity, world,
                fullTarget.get(), scheduler))
        {
            Functions.transformFull(entity, world, scheduler, imageStore);
        }
        else {
            Functions.scheduleEvent(scheduler, entity,
                    Functions.createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }


    public static void executeMinerNotFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget =
                Functions.findNearest(world, entity.position, EntityKind.ORE);

        if (!notFullTarget.isPresent() || !Functions.moveToNotFull(entity, world,
                notFullTarget.get(),
                scheduler)
                || !Functions.transformNotFull(entity, world, scheduler, imageStore))
        {
            Functions.scheduleEvent(scheduler, entity,
                    Functions.createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    public static void executeOreActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = entity.position;

        Functions.removeEntity(world, entity);
        Functions.unscheduleAllEvents(scheduler, entity);

        Entity blob = Functions.createOreBlob(entity.id + Functions.BLOB_ID_SUFFIX, pos,
                entity.actionPeriod / Functions.BLOB_PERIOD_SCALE,
                Functions.BLOB_ANIMATION_MIN + Functions.rand.nextInt(
                        Functions.BLOB_ANIMATION_MAX
                                - Functions.BLOB_ANIMATION_MIN),
                Functions.getImageList(imageStore, Functions.BLOB_KEY));

        Functions.addEntity(world, blob);
        Functions.scheduleActions(blob, scheduler, world, imageStore);
    }

    public static void executeOreBlobActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> blobTarget =
                Functions.findNearest(world, entity.position, EntityKind.VEIN);
        long nextPeriod = entity.actionPeriod;

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().position;

            if (Functions.moveToOreBlob(entity, world, blobTarget.get(), scheduler)) {
                Entity quake = Functions.createQuake(tgtPos,
                        Functions.getImageList(imageStore, Functions.QUAKE_KEY));

                Functions.addEntity(world, quake);
                nextPeriod += entity.actionPeriod;
                Functions.scheduleActions(quake, scheduler, world, imageStore);
            }
        }

        Functions.scheduleEvent(scheduler, entity,
                Functions.createActivityAction(entity, world, imageStore),
                nextPeriod);
    }


    public static void executeQuakeActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Functions.unscheduleAllEvents(scheduler, entity);
        Functions.removeEntity(world, entity);
    }

    public static void executeVeinActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Point> openPt = Functions.findOpenAround(world, entity.position);

        if (openPt.isPresent()) {
            Entity ore = Functions.createOre(Functions.ORE_ID_PREFIX + entity.id, openPt.get(),
                    Functions.ORE_CORRUPT_MIN + Functions.rand.nextInt(
                            Functions.ORE_CORRUPT_MAX - Functions.ORE_CORRUPT_MIN),
                    Functions.getImageList(imageStore, Functions.ORE_KEY));
            Functions.addEntity(world, ore);
            Functions.scheduleActions(ore, scheduler, world, imageStore);
        }

        Functions.scheduleEvent(scheduler, entity,
                Functions.createActivityAction(entity, world, imageStore),
                entity.actionPeriod);
    }
}
