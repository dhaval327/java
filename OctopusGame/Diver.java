import processing.core.PImage;

import java.util.List;


public class Diver extends AbstractChaseEntity {

    public Diver(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images)
    {
        super(id, position, images, 0, actionPeriod, animationPeriod, 0, 0);
    }

    public boolean checkInstance(Entity entity){
        return entity instanceof Shark;
    }
}
