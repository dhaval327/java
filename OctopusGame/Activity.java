public class Activity implements Action {

    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Activity(Entity entity, WorldModel world, ImageStore imageStore)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public static Activity createActivityAction(Entity entity, WorldModel world, ImageStore imageStore)
    {
        return new Activity(entity, world, imageStore);
    }

    public void executeActivityAction(EventScheduler scheduler)
    {
        ((AbstractMovingEntity)this.entity).executeActivity(world, imageStore, scheduler);
    }

    public void executeAction(EventScheduler scheduler)
    {
        executeActivityAction(scheduler);
    }

    public Entity getEntity() {
        return entity;
    }

    public ImageStore getImageStore() {
        return imageStore;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public WorldModel getWorld() {
        return world;
    }
}
