import processing.core.PImage;

import java.util.List;

public abstract class AnimEntity extends ActionEntity{

    private final int animationPeriod;

    public AnimEntity(String id,
                      Point position,
                      List<PImage> images,
                      int actionPeriod,
                      int animationPeriod){
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
    }
    protected int getAnimationPeriod() {
        return animationPeriod;
    }

    protected void nextImage() {
        super.setImageIndex((super.getImageIndex() + 1) % super.getImages().size());
    }

    protected Action createAnimationAction(int repeatCount) {
        return new Animation(this,
                repeatCount);
    }

}
