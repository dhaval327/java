import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Octupus extends AbstractOctopus{

    public static final String OCTOPUS_ID = "octopus";
    public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    public Octupus(Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(OCTOPUS_ID, position, images,0, actionPeriod ,animationPeriod );
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
    }

    public void moveUp(WorldModel world){
        world.moveEntity(this, new Point(this.position.x, this.position.y - 1));
    }

    public void moveDown(WorldModel world){
        world.moveEntity(this, new Point(this.position.x, this.position.y + 1));
    }

    public void moveLeft(WorldModel world){
        world.moveEntity(this, new Point(this.position.x - 1, this.position.y));
    }

    public void moveRight(WorldModel world){
        world.moveEntity(this, new Point(this.position.x + 1, this.position.y));
    }


}
