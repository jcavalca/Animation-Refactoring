import processing.core.PImage;

import java.util.List;

public class Fire extends AnimEntity implements clickEntity {

    public Fire(
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

        this.scheduleActions(scheduler, world, imageStore);
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
                Functions.FIRE_ANIMATION_REPEAT_COUNT),
                this.getAnimationPeriod());
    }

    public void addToWorld(WorldModel world,
                           ImageStore imageStore,
                           EventScheduler scheduler){
                    world.addEntity(this);
                    this.scheduleActions(scheduler, world, imageStore);
                }


}
