import processing.core.PImage;

import java.util.*;

final class WorldModel
{
   private int numRows;
   private int numCols;
   private Background background[][];
   private Entity occupancy[][];
   private Set<Entity> entities;

   private static final String VEIN_KEY = "vein";
   private static final int VEIN_NUM_PROPERTIES = 5;
   private static final int VEIN_ID = 1;
   private static final int VEIN_COL = 2;
   private static final int VEIN_ROW = 3;
   private static final int VEIN_ACTION_PERIOD = 4;

   private static final String SMITH_KEY = "blacksmith";
   private static final int SMITH_NUM_PROPERTIES = 4;
   private static final int SMITH_ID = 1;
   private static final int SMITH_COL = 2;
   private static final int SMITH_ROW = 3;

   private static final String ORE_KEY = "ore";
   private static final int ORE_REACH = 1;
   private static final int ORE_NUM_PROPERTIES = 5;
   private static final int ORE_ID = 1;
   private static final int ORE_COL = 2;
   private static final int ORE_ROW = 3;
   private static final int ORE_ACTION_PERIOD = 4;

   private static final String OBSTACLE_KEY = "obstacle";
   private static final int OBSTACLE_NUM_PROPERTIES = 4;
   private static final int OBSTACLE_ID = 1;
   private static final int OBSTACLE_COL = 2;
   private static final int OBSTACLE_ROW = 3;

   private static final String FISH_KEY = "fish";
   private static final int FISH_NUM_PROPERTIES = 4;
   private static final int FISH_ID = 1;
   private static final int FISH_COL = 2;
   private static final int FISH_ROW = 3;

   private static final String MINER_KEY = "miner";
   private static final int MINER_NUM_PROPERTIES = 7;
   private static final int MINER_ID = 1;
   private static final int MINER_COL = 2;
   private static final int MINER_ROW = 3;
   private static final int MINER_LIMIT = 4;
   private static final int MINER_ACTION_PERIOD = 5;
   private static final int MINER_ANIMATION_PERIOD = 6;

   private static final String OCTOPUS_KEY = "octopus";
   private static final int OCTOPUS_NUM_PROPERTIES = 6;
   private static final int OCTOPUS_ID = 1;
   private static final int OCTOPUS_COL = 2;
   private static final int OCTOPUS_ROW = 3;
   private static final int OCTOPUS_ACTION_PERIOD = 4;
   private static final int OCTOPUS_ANIMATION_PERIOD = 5;

   private static final String SHARK_KEY = "shark";
   private static final int SHARK_NUM_PROPERTIES = 6;
   private static final int SHARK_ID = 1;
   private static final int SHARK_COL = 2;
   private static final int SHARK_ROW = 3;
   private static final int SHARK_ACTION_PERIOD = 4;
   private static final int SHARK_ANIMATION_PERIOD = 5;

   private static final String BGND_KEY = "background";
   private static final int BGND_NUM_PROPERTIES = 4;
   private static final int BGND_ID = 1;
   private static final int BGND_COL = 2;
   private static final int BGND_ROW = 3;

   private static final int PROPERTY_KEY = 0;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public boolean neighbors(Point p1, Point p2)
   {
      return p1.x+1 == p2.x && p1.y == p2.y ||
              p1.x-1 == p2.x && p1.y == p2.y ||
              p1.x == p2.x && p1.y+1 == p2.y ||
              p1.x == p2.x && p1.y-1 == p2.y;
   }

   public Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++)
      {
         for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) && !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public void load(Scanner in, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), imageStore))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   public boolean processLine(String line, ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return parseBackground(properties, imageStore);
            case OBSTACLE_KEY:
               return parseObstacle(properties, imageStore);
            case OCTOPUS_KEY:
               return parseOctopus(properties, imageStore);
            case SHARK_KEY:
               return parseShark(properties, imageStore);
            case FISH_KEY:
               return parseFish(properties, imageStore);
         }
      }

      return false;
   }

   public boolean parseBackground(String [] properties, ImageStore imageStore)
   {
      if (properties.length == BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                 Integer.parseInt(properties[BGND_ROW]));
         String id = properties[BGND_ID];
         setBackground(this, pt, new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == BGND_NUM_PROPERTIES;
   }

   public boolean parseFish(String [] properties, ImageStore imageStore)
   {
      if (properties.length == FISH_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[FISH_COL]),
                 Integer.parseInt(properties[FISH_ROW]));
         Entity entity = new Obstacle(properties[FISH_ID],
                 pt, imageStore.getImageList(FISH_KEY));
         tryAddEntity(this, entity);
      }

      return properties.length == FISH_NUM_PROPERTIES;
   }

   public boolean parseOctopus(String [] properties, ImageStore imageStore) {
      if (properties.length == OCTOPUS_NUM_PROPERTIES) {
         Point pt = new Point(Integer.parseInt(properties[OCTOPUS_COL]),
                 Integer.parseInt(properties[OCTOPUS_ROW]));
         Entity entity = new Octupus(
                 pt,imageStore.getImageList(OCTOPUS_KEY),
                 Integer.parseInt(properties[OCTOPUS_ACTION_PERIOD]),
                 Integer.parseInt(properties[OCTOPUS_ANIMATION_PERIOD])
                 );
         tryAddEntity(this, entity);
      }

      return properties.length == OCTOPUS_NUM_PROPERTIES;
   }

   public boolean parseShark(String [] properties, ImageStore imageStore) {
      if (properties.length == SHARK_NUM_PROPERTIES) {
         Point pt = new Point(Integer.parseInt(properties[SHARK_COL]),
                 Integer.parseInt(properties[SHARK_ROW]));
         Entity entity = new Shark(SHARK_KEY, pt, Integer.parseInt(properties[SHARK_ACTION_PERIOD]),
                 Integer.parseInt(properties[SHARK_ANIMATION_PERIOD]), imageStore.getImageList(SHARK_KEY)
         );
         tryAddEntity(this, entity);
      }

      return properties.length == OCTOPUS_NUM_PROPERTIES;
   }

   public boolean parseObstacle(String [] properties, ImageStore imageStore)
   {
      if (properties.length == OBSTACLE_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[OBSTACLE_COL]),
                 Integer.parseInt(properties[OBSTACLE_ROW]));
         Entity entity = new Obstacle(properties[OBSTACLE_ID],
                 pt, imageStore.getImageList(OBSTACLE_KEY));
         tryAddEntity(this, entity);
      }

      return properties.length == OBSTACLE_NUM_PROPERTIES;
   }


   public void tryAddEntity(WorldModel world, Entity entity)
   {
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < this.numRows &&
              pos.x >= 0 && pos.x < this.numCols;
   }

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) && getOccupancyCell(pos) != null;
   }


   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         if(!isOccupied(pos)) {
            setOccupancyCell(oldPos, null);
            removeEntityAt(pos);
            setOccupancyCell(pos, entity);
            entity.setPosition(pos);
         }
      }
   }

   public void removeEntity(Entity entity)
   {

      removeEntityAt(entity.getPosition());
   }

   public void removeEntityAt(Point pos)
   {
      if (this.withinBounds(pos)
              && this.getOccupancyCell(pos) != null)
      {
         Entity entity = this.getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         //entity.getposition() = new Point(-1, -1);
         entity.setPosition(new Point(-1, -1));
         this.entities.remove(entity);
         this.setOccupancyCell(pos, null);
      }
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(Functions.getCurrentImage(getBackgroundCell(this, pos)));
      }
      else
      {
         return Optional.empty();
      }
   }

   public void setBackground(WorldModel world, Point pos, Background background)
   {
      if (withinBounds(pos))
      {
         setBackgroundCell(world, pos, background);
      }
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public Point findNewPoint(){
      int x, y;
      Point newPos;
      while(true) {
         x = (int) (Math.random() * getNumCols());
         y = (int) (Math.random() * getNumRows());
         newPos = new Point(x, y);
         if(!isOccupied(newPos)){
            break;
         }
      }
      return newPos;
   }

   public void shuffleObstacles(){
      for (Entity entity: entities) {
         if(entity instanceof Obstacle){
            int x = (int)(Math.random()*numCols);
            int y = (int)(Math.random()*numRows);
            Point newPos = new Point(x, y);
            if(!isOccupied(newPos)) {
               moveEntity(entity, newPos);
            }
         }
      }

   }


   public Entity getOccupancyCell(Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   public void setOccupancyCell(Point pos, Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   public Background getBackgroundCell(WorldModel world, Point pos)
   {

      return world.background[pos.y][pos.x];
   }

   public void setBackgroundCell(WorldModel world, Point pos,
                                 Background background)
   {
      world.background[pos.y][pos.x] = background;
   }

   public Set<Entity> getEntities() {
      return entities;
   }

   public int getNumRows() {
      return numRows;
   }

   public int getNumCols() {
      return numCols;
   }

   public Background[][] getBackground() {
      return background;
   }

   public Entity[][] getOccupancy() {
      return occupancy;
   }
}
