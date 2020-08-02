import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class Miner_Full extends MoverEntity {
    private final int resourceLimit;

    public Miner_Full(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int actionPeriod,
            int animationPeriod) {

        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    protected void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> fullTarget =
                world.findNearest(this.getPosition(), Blacksmith.class);

        if (fullTarget.isPresent() && this.move(world,
                fullTarget.get(), scheduler)) {
            transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.getActionPeriod());
        }
    }

    private void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        Miner_Not_Full miner = Factory.createMinerNotFull(this.getId(), this.resourceLimit,
                this.getPosition(), this.getActionPeriod(),
                this.getAnimationPeriod(),
                this.getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    protected boolean move(
            WorldModel world,
            Entity target,
            EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

}
