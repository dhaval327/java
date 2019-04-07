import processing.core.PImage;

import java.util.List;
import java.util.Optional;


public class Shark extends AbstractChaseEntity {

    public Shark(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images)
    {
        super(id, position, images, 0, actionPeriod, animationPeriod, 0, 0);
    }

    public boolean checkInstance(Entity entity){
        return entity instanceof Octupus;
    }
}
