import processing.core.PImage;

import java.util.List;

public abstract class AbstractMovingEntity extends AbstractEntity {

    protected int actionPeriod;

    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    public AbstractMovingEntity(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod){
        super(id, position, images, imageIndex);
        this.actionPeriod = actionPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.actionPeriod);
    }
}
