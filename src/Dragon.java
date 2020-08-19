import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Dragon extends MoverEntity implements ClickEntity {

    public static int dragonLimit = 10;
    public static int dragonCount;

    public Dragon(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }


    protected void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> dragonTarget =
                world.findNearest(this.getPosition(), Miners.class);
        long nextPeriod = this.getActionPeriod();

        if (dragonTarget.isPresent()) {
            Point tgtPos = dragonTarget.get().getPosition();

            if (this.move(world, dragonTarget.get(), scheduler)) {
                ((Miners)dragonTarget.get()).burn(world, imageStore, scheduler);
                nextPeriod += this.getActionPeriod();
            }
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                nextPeriod);
    }

    public boolean move(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        }
        else {
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

    public void addToWorld(WorldModel world,
                           ImageStore imageStore,
                           EventScheduler scheduler){
        world.addEntity(this);
        this.scheduleActions(scheduler, world, imageStore);
    }

    public void freeze(WorldModel world,
                       ImageStore imageStore,
                       EventScheduler scheduler) {
        Point tgtPos = getPosition();
        world.removeEntity(this);
    }

}
