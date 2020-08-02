import processing.core.PImage;

import java.util.List;

public abstract class Entity {

    protected final String id;
    protected Point position;
    protected final List<PImage> images;
    protected int imageIndex;

    public Entity(String id,
                        Point position,
                        List<PImage> images){
        this.id = id;
        this.position = position;
        this.images = images;
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex);
    }

    public Point getPosition() {
        return position;
    }

}
