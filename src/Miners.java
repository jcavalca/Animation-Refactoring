import processing.core.PImage;

import java.util.List;

public abstract class Miners extends MoverEntity{

    private int resourceLimit;

    public Miners(String id,
                       Point position,
                       List<PImage> images,
                       int actionPeriod,
                       int animationPeriod,
                       int resourceLimit){
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    // Abstract method
    protected abstract boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore
    );

    // Getter
    protected int getResourceLimit(){
        return resourceLimit;
    }

}
