import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class DragonIce extends MoverEntity implements clickEntity {

    public static int dragonLimit = 3;
    public static int dragonCount;

    public DragonIce(
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
        Optional<Entity> dragonIceTarget =
                world.findNearest(this.getPosition(), Dragon.class);
        long nextPeriod = this.getActionPeriod();

        if (dragonIceTarget.isPresent()) {
            Point tgtPos = dragonIceTarget.get().getPosition();

            if (this.move(world, dragonIceTarget.get(), scheduler)) {
                ((Dragon)dragonIceTarget.get()).freeze(world, imageStore, scheduler);
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

}
