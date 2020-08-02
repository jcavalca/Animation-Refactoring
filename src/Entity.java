import processing.core.PImage;

import java.util.List;

public abstract class Entity {

    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;

    public Entity(String id,
                        Point position,
                        List<PImage> images){
        this.id = id;
        this.position = position;
        this.images = images;
    }

    protected PImage getCurrentImage() {
        return this.images.get(this.imageIndex);
    }

    protected Point getPosition() {
        return position;
    }

    protected void setPosition(Point point){
        this.position = point;
    }

    protected int getImageIndex(){
        return imageIndex;
    }

    protected void setImageIndex(int newIndex){
        this.imageIndex = newIndex;
    }

    protected List<PImage> getImages(){
        return images;
    }

    protected String getId(){
        return id;
    }
}
