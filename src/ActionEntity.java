import processing.core.PImage;

import java.util.List;

public abstract class ActionEntity extends Entity{

    private final int actionPeriod;

    public ActionEntity(String id,
                        Point position,
                        List<PImage> images,
                        int actionPeriod){
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    protected Action createActivityAction(
            WorldModel world, ImageStore imageStore)
    {
        return new Activity( this, world, imageStore);
    }

    protected void setPosition(Point position) {
       super.setPosition(position);
    }

    protected void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod);

    }

    // Abstract Methods

    protected abstract void executeActivity( WorldModel world,
                     ImageStore imageStore,
                     EventScheduler scheduler);

    // Getters/ Setters

    protected int getActionPeriod(){
        return actionPeriod;
    }
}
