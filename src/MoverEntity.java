import processing.core.PImage;

import java.util.List;

public abstract class MoverEntity extends AnimEntity {

    public MoverEntity(String id,
                       Point position,
                       List<PImage> images,
                       int actionPeriod,
                       int animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod);
    }

    // Methods

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent(this,
                this.createAnimationAction(0),
                this.getAnimationPeriod());
    }

    protected Point nextPosition(
            WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }

    // Abstract Methods
    public abstract boolean move(WorldModel world,
                          Entity target,
                          EventScheduler scheduler);

}
