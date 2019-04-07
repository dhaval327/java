import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractOctopus extends AbstractAnimationEntity{

    public AbstractOctopus(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod){
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
    }

    public int getAnimationPeriod() {
        return animationPeriod;
    }

    public void nextImage() {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public void destroyEntity(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

//        world.addEntity(zombie);
//        (zombie).scheduleActions(scheduler, world, imageStore);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this, 0), this.animationPeriod);

    }

//    protected abstract boolean checkInstance(Entity entity);
}