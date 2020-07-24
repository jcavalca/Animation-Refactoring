import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class Obstacle implements Entity {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int animationPeriod;

    public Obstacle(
            String id,
            Point position,
            List<PImage> images,
            int animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.animationPeriod = animationPeriod;
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex);
    }

    public int getAnimationPeriod() {
        return animationPeriod;
    }

    public void nextImage() {
        imageIndex = (imageIndex + 1) % images.size();
    }

//    public void scheduleActions(
//            EventScheduler scheduler,
//            WorldModel world,
//            ImageStore imageStore) {
//    }


    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
