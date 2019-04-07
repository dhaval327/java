import processing.core.PImage;

import java.util.List;


public interface Entity{

   Point getPosition();
   void setPosition(Point point);
   List<PImage> getImages();
   String getId();
   int getImageIndex();

}
