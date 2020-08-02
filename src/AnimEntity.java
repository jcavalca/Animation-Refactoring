import processing.core.PImage;

import java.util.List;

public abstract class AnimEntity extends ActionEntity{

    protected final int animationPeriod;

    public AnimEntity(String id,
                      Point position,
                      List<PImage> images,
                      int actionPeriod,
                      int animationPeriod){
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
    }
    public int getAnimationPeriod() {
        return animationPeriod;
    }

    public void nextImage() {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public Action createAnimationAction(int repeatCount) {
        return new Animation(this,
                repeatCount);
    }

}
