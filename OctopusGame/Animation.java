public class Animation implements Action {

    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Animation(Entity entity, int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public static Animation createAnimationAction(Entity entity, int repeatCount)
    {
        return new Animation(entity, repeatCount);
    }

    public void executeAnimationAction( EventScheduler scheduler)
    {
        ((AbstractAnimationEntity)this.entity).nextImage();

        if (this.repeatCount != 1)
        {
            scheduler.scheduleEvent(this.entity, new Animation(entity, Math.max(repeatCount - 1, 0)), ((AbstractAnimationEntity)entity).getAnimationPeriod());
        }
    }

    public void executeAction(EventScheduler scheduler)
    {
        ((AbstractAnimationEntity)this.entity).nextImage();

        if (this.repeatCount != 1)
        {
            scheduler.scheduleEvent(this.entity, createAnimationAction(this.entity, Math.max(this.repeatCount - 1, 0)),
                    ((AbstractAnimationEntity)this.entity).getAnimationPeriod());
        }
    }

    public WorldModel getWorld() {
        return world;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public Entity getEntity() {
        return entity;
    }
}
