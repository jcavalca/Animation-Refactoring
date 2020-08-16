import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Volcano extends AnimEntity {

    private int dragonCount;

    public Volcano(
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
        if (randNumber > 95 && Dragon.dragonCount < Dragon.dragonLimit){
            Point location = this.getPosition();
            Dragon dragon = Factory.createDragon("dragon", location,
                    Functions.DRAGON_ACTION_PERIOD,
                    Functions.DRAGON_ANIMATION_PERIOD,
                    imageStore.getImageList(Functions.DRAGON_KEY));
            dragon.addToWorld(world, imageStore, scheduler);
            Dragon.dragonCount++;
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
                Functions.VOLCANO_ANIMATION_REPEAT_COUNT),
                this.getAnimationPeriod());
    }

    public void addToWorld(WorldModel world,
                           ImageStore imageStore,
                           EventScheduler scheduler){
                    world.addEntity(this);
                    this.scheduleActions(scheduler, world, imageStore);
                }


}
