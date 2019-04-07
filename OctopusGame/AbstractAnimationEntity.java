import processing.core.PImage;

import java.util.List;

public abstract class AbstractAnimationEntity extends AbstractMovingEntity {

    protected int animationPeriod;

    public AbstractAnimationEntity(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod){
        super(id, position, images, imageIndex, actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    public int getAnimationPeriod() {
        return animationPeriod;
    }

    public void nextImage() {
        imageIndex = (imageIndex + 1) % images.size();
    }
}
