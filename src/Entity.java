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
    public void nextImage() {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public void executeMinerFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                Functions.findNearest(world, this.position, EntityKind.BLACKSMITH);

        if (fullTarget.isPresent() && this.moveToFull(world,
                fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else {
            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }


    public void executeMinerNotFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget =
                Functions.findNearest(world, this.position, EntityKind.ORE);

        if (!notFullTarget.isPresent() || !this.moveToNotFull(world,
                notFullTarget.get(),
                scheduler)
                || !transformNotFull(world, scheduler, imageStore))
        {
            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeOreActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = this.position;

        Functions.removeEntity(world, this);
        Functions.unscheduleAllEvents(scheduler, this);

        Entity blob = Functions.createOreBlob(this.id + Functions.BLOB_ID_SUFFIX, pos,
                this.actionPeriod / Functions.BLOB_PERIOD_SCALE,
                Functions.BLOB_ANIMATION_MIN + Functions.rand.nextInt(
                        Functions.BLOB_ANIMATION_MAX
                                - Functions.BLOB_ANIMATION_MIN),
                Functions.getImageList(imageStore, Functions.BLOB_KEY));

        Functions.addEntity(world, blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

    public void executeOreBlobActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> blobTarget =
                Functions.findNearest(world, this.position, EntityKind.VEIN);
        long nextPeriod = this.actionPeriod;

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().position;

            if (this.moveToOreBlob(world, blobTarget.get(), scheduler)) {
                Entity quake = Functions.createQuake(tgtPos,
                        Functions.getImageList(imageStore, Functions.QUAKE_KEY));

                Functions.addEntity(world, quake);
                nextPeriod += this.actionPeriod;
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }

        Functions.scheduleEvent(scheduler, this,
                Functions.createActivityAction(this, world, imageStore),
                nextPeriod);
    }


    public void executeQuakeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Functions.unscheduleAllEvents(scheduler, this);
        Functions.removeEntity(world, this);
    }

    public void executeVeinActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Point> openPt = Functions.findOpenAround(world, this.position);

        if (openPt.isPresent()) {
            Entity ore = Functions.createOre(Functions.ORE_ID_PREFIX + this.id, openPt.get(),
                    Functions.ORE_CORRUPT_MIN + Functions.rand.nextInt(
                            Functions.ORE_CORRUPT_MAX - Functions.ORE_CORRUPT_MIN),
                    Functions.getImageList(imageStore, Functions.ORE_KEY));
            Functions.addEntity(world, ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        Functions.scheduleEvent(scheduler, this,
                Functions.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        switch (this.kind) {
            case MINER_FULL:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        this.getAnimationPeriod());
                break;

            case MINER_NOT_FULL:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        this.getAnimationPeriod());
                break;

            case ORE:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                break;

            case ORE_BLOB:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        this.getAnimationPeriod());
                break;

            case QUAKE:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this,
                        Functions.QUAKE_ANIMATION_REPEAT_COUNT),
                        this.getAnimationPeriod());
                break;

            case VEIN:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                break;

            default:
        }
    }

    public boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.resourceCount >= this.resourceLimit) {
            Entity miner = Functions.createMinerFull(this.id, this.resourceLimit,
                    this.position, this.actionPeriod,
                    this.animationPeriod,
                    this.images);

            Functions.removeEntity(world, this);
            Functions.unscheduleAllEvents(scheduler, this);

            Functions.addEntity(world, miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }
    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = Functions.createMinerNotFull(this.id, this.resourceLimit,
                this.position, this.actionPeriod,
                this.animationPeriod,
                this.images);

        Functions.removeEntity(world, this);
        Functions.unscheduleAllEvents(scheduler, this);

        Functions.addEntity(world, miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    public boolean moveToNotFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Functions.adjacent(this.position, target.position)) {
            this.resourceCount += 1;
            Functions.removeEntity(world, target);
            Functions.unscheduleAllEvents(scheduler, target);

            return true;
        }
        else {
            Point nextPos = this.nextPositionMiner(world, target.position);

            if (!this.position.equals(nextPos)) {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }
    public  boolean moveToFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Functions.adjacent(this.position, target.position)) {
            return true;
        }
        else {
            Point nextPos = this.nextPositionMiner(world, target.position);

            if (!this.position.equals(nextPos)) {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToOreBlob(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Functions.adjacent(this.position, target.position)) {
            Functions.removeEntity(world, target);
            Functions.unscheduleAllEvents(scheduler, target);
            return true;
        }
        else {
            Point nextPos = Functions.nextPositionOreBlob(this, world, target.position);

            if (!this.position.equals(nextPos)) {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }
    public Point nextPositionMiner(
            WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || Functions.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || Functions.isOccupied(world, newPos)) {
                newPos = this.position;
            }
        }

        return newPos;
    }


}
