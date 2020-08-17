import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Ice extends AnimEntity implements clickEntity {

    public Ice(
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
        Random rand = new Random();
        int randNumber = rand.nextInt(100);
        if (randNumber > 97 && DragonIce.dragonCount < DragonIce.dragonLimit){
            Point location = this.getPosition();
            DragonIce dragonIce = Factory.createDragonIce("dragonIce", location,
                    Functions.DRAGON_ICE_ACTION_PERIOD,
                    Functions.DRAGON_ICE_ANIMATION_PERIOD,
                    imageStore.getImageList(Functions.DRAGON_ICE_KEY));
            dragonIce.addToWorld(world, imageStore, scheduler);
            DragonIce.dragonCount++;
        }
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
                Functions.ICE_ANIMATION_REPEAT_COUNT),
                this.getAnimationPeriod());
    }

    public void addToWorld(WorldModel world,
                           ImageStore imageStore,
                           EventScheduler scheduler){
                    world.addEntity(this);
                    this.scheduleActions(scheduler, world, imageStore);
                }


}
