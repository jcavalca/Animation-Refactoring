import processing.core.PImage;

import java.util.List;

public abstract class ActionEntity extends Entity{

    protected final int actionPeriod;

    public ActionEntity(String id,
                        Point position,
                        List<PImage> images,
                        int actionPeriod){
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    public Action createActivityAction(
            WorldModel world, ImageStore imageStore)
    {
        return new Activity( this, world, imageStore);
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod);

    }

    // Abstract Methods

    abstract void executeActivity( WorldModel world,
                     ImageStore imageStore,
                     EventScheduler scheduler);


}
