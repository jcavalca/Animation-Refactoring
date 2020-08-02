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
                this.actionPeriod);
        scheduler.scheduleEvent(this,
                this.createAnimationAction(0),
                this.getAnimationPeriod());
    }

    public Point nextPosition(
            WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = this.position;
            }
        }

        return newPos;
    }

    // Abstract Methods
    abstract boolean move(WorldModel world,
                          Entity target,
                          EventScheduler scheduler);

}
