import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class Obstacle implements Entity {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private final int imageIndex;

    public Obstacle(
            String id,
            Point position,
            List<PImage> images
            ) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex);
    }

    public Point getPosition() {
        return position;
    }

}
