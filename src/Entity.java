import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public final class Entity
{
    private final EntityKind KIND;
    private final String ID;
    private Point position;
    private final List<PImage> IMAGES;
    private int imageIndex;
    private final int RESOURCELIMIT;
    private int resourceCount;
    private final int ACTIONPERIOD;
    private final int ANIMATIONPERIOD;

    public Entity(
            EntityKind KIND,
            String ID,
            Point position,
            List<PImage> IMAGES,
            int RESOURCELIMIT,
            int resourceCount,
            int ACTIONPERIOD,
            int ANIMATIONPERIOD)
    {
        this.KIND = KIND;
        this.ID = ID;
        this.position = position;
        this.IMAGES = IMAGES;
        this.imageIndex = 0;
        this.RESOURCELIMIT = RESOURCELIMIT;
        this.resourceCount = resourceCount;
        this.ACTIONPERIOD = ACTIONPERIOD;
        this.ANIMATIONPERIOD = ANIMATIONPERIOD;
    }

    public PImage getCurrentImage() {
        return this.IMAGES.get(this.imageIndex);
    }

    public int getANIMATIONPERIOD() {
        switch (KIND) {
            case MINER_FULL:
            case MINER_NOT_FULL:
            case ORE_BLOB:
            case QUAKE:
                return ANIMATIONPERIOD;
            default:
                throw new UnsupportedOperationException(
                        String.format("getAnimationPeriod not supported for %s",
                                KIND));
        }
    }
    public void nextImage() {
        imageIndex = (imageIndex + 1) % IMAGES.size();
    }

    public void executeMinerFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                world.findNearest(this.position, EntityKind.BLACKSMITH);

        if (fullTarget.isPresent() && this.moveToFull(world,
                fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.ACTIONPERIOD);
        }
    }


    public void executeMinerNotFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget =
                world.findNearest(this.position, EntityKind.ORE);

        if (!notFullTarget.isPresent() || !this.moveToNotFull(world,
                notFullTarget.get(),
                scheduler)
                || !transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.ACTIONPERIOD);
        }
    }

    public void executeOreActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = this.position;

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        Entity blob = Entity.createOreBlob(this.ID + Functions.BLOB_ID_SUFFIX, pos,
                this.ACTIONPERIOD / Functions.BLOB_PERIOD_SCALE,
                Functions.BLOB_ANIMATION_MIN + Functions.rand.nextInt(
                        Functions.BLOB_ANIMATION_MAX
                                - Functions.BLOB_ANIMATION_MIN),
                imageStore.getImageList(Functions.BLOB_KEY));

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

    public void executeOreBlobActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> blobTarget =
                world.findNearest(this.position, EntityKind.VEIN);
        long nextPeriod = this.ACTIONPERIOD;

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().position;

            if (this.moveToOreBlob(world, blobTarget.get(), scheduler)) {
                Entity quake = Entity.createQuake(tgtPos,
                        imageStore.getImageList(Functions.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.ACTIONPERIOD;
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                nextPeriod);
    }


    public void executeQuakeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    public void executeVeinActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(this.position);

        if (openPt.isPresent()) {
            Entity ore = Entity.createOre(Functions.ORE_ID_PREFIX + this.ID, openPt.get(),
                    Functions.ORE_CORRUPT_MIN + Functions.rand.nextInt(
                            Functions.ORE_CORRUPT_MAX - Functions.ORE_CORRUPT_MIN),
                    imageStore.getImageList(Functions.ORE_KEY));
            world.addEntity(ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.ACTIONPERIOD);
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        switch (this.KIND) {
            case MINER_FULL:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.ACTIONPERIOD);
                scheduler.scheduleEvent(this,
                        this.createAnimationAction( 0),
                        this.getANIMATIONPERIOD());
                break;

            case MINER_NOT_FULL:
                scheduler.scheduleEvent(this,
                        this.createActivityAction( world, imageStore),
                        this.ACTIONPERIOD);
                scheduler.scheduleEvent(this,
                        this.createAnimationAction(0),
                        this.getANIMATIONPERIOD());
                break;

            case ORE:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.ACTIONPERIOD);
                break;

            case ORE_BLOB:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.ACTIONPERIOD);
                scheduler.scheduleEvent(this,
                        this.createAnimationAction(0),
                        this.getANIMATIONPERIOD());
                break;

            case QUAKE:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.ACTIONPERIOD);
                scheduler.scheduleEvent(this, this.createAnimationAction(
                        Functions.QUAKE_ANIMATION_REPEAT_COUNT),
                        this.getANIMATIONPERIOD());
                break;

            case VEIN:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.ACTIONPERIOD);
                break;

            default:
        }
    }

    private boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.resourceCount >= this.RESOURCELIMIT) {
            Entity miner = Entity.createMinerFull(this.ID, this.RESOURCELIMIT,
                    this.position, this.ACTIONPERIOD,
                    this.ANIMATIONPERIOD,
                    this.IMAGES);

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }
    private void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = Entity.createMinerNotFull(this.ID, this.RESOURCELIMIT,
                this.position, this.ACTIONPERIOD,
                this.ANIMATIONPERIOD,
                this.IMAGES);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    private boolean moveToNotFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.position.adjacent(target.position)) {
            this.resourceCount += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else {
            Point nextPos = this.nextPositionMiner(world, target.position);

            if (!this.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
    private boolean moveToFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.position.adjacent(target.position)) {
            return true;
        }
        else {
            Point nextPos = this.nextPositionMiner(world, target.position);

            if (!this.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    private boolean moveToOreBlob(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.position.adjacent(target.position)) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
            Point nextPos = this.nextPositionOreBlob(world, target.position);

            if (!this.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
    private Point nextPositionMiner(
            WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.getX() - this.position.getX());
        Point newPos = new Point(this.position.getX() + horiz, this.position.getY());

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.getY() - this.position.getY());
            newPos = new Point(this.position.getX(), this.position.getY() + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = this.position;
            }
        }

        return newPos;
    }

    private Point nextPositionOreBlob(
             WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.getX() - this.position.getX());
        Point newPos = new Point(this.position.getX() + horiz, this.position.getY());

        Optional<Entity> occupant = world.getOccupant(newPos);

        if (horiz == 0 || (occupant.isPresent() && !(occupant.get().KIND
                == EntityKind.ORE)))
        {
            int vert = Integer.signum(destPos.getY() - this.position.getY());
            newPos = new Point(this.position.getX(), this.position.getY() + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 || (occupant.isPresent() && !(occupant.get().KIND
                    == EntityKind.ORE)))
            {
                newPos = this.position;
            }
        }

        return newPos;
    }

    public Action createAnimationAction(int repeatCount) {
        return new Action(ActionKind.ANIMATION, this, null, null,
                repeatCount);
    }

    private Action createActivityAction(
            WorldModel world, ImageStore imageStore)
    {
        return new Action(ActionKind.ACTIVITY, this, world, imageStore, 0);
    }

    public static Entity createBlacksmith(
            String id, Point position, List<PImage> images)
    {
        return new Entity(EntityKind.BLACKSMITH, id, position, images, 0, 0, 0,
                0);
    }

    private static Entity createMinerFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Entity(EntityKind.MINER_FULL, id, position, images,
                resourceLimit, resourceLimit, actionPeriod,
                animationPeriod);
    }

    public static Entity createMinerNotFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Entity(EntityKind.MINER_NOT_FULL, id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod);
    }

    public static Entity createObstacle(
            String id, Point position, List<PImage> images)
    {
        return new Entity(EntityKind.OBSTACLE, id, position, images, 0, 0, 0,
                0);
    }

    public static Entity createOre(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Entity(EntityKind.ORE, id, position, images, 0, 0,
                actionPeriod, 0);
    }

    private static Entity createOreBlob(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Entity(EntityKind.ORE_BLOB, id, position, images, 0, 0,
                actionPeriod, animationPeriod);
    }

    private static Entity createQuake(
            Point position, List<PImage> images)
    {
        return new Entity(EntityKind.QUAKE, Functions.QUAKE_ID, position, images, 0, 0,
                Functions.QUAKE_ACTION_PERIOD, Functions.QUAKE_ANIMATION_PERIOD);
    }

    public static Entity createVein(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Entity(EntityKind.VEIN, id, position, images, 0, 0,
                actionPeriod, 0);
    }

    // Getters and Setters Created!!

    public EntityKind getKIND() {
        return KIND;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}

