import java.util.List;

import processing.core.PImage;

public class Quake extends AnimEntity {

    public Quake(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    protected void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent(this, this.createAnimationAction(
                Functions.QUAKE_ANIMATION_REPEAT_COUNT),
                this.getAnimationPeriod());
    }

}
