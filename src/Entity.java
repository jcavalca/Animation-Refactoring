import processing.core.PImage;

public interface Entity {

    Point getPosition();

    void setPosition(Point position);

    int getAnimationPeriod();

    void nextImage();

    PImage getCurrentImage();

}
