import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public final class SharkGame extends PApplet {

    private static final String OCTOPUS_KEY = "octopus";
    private static final int OCTOPUS_ACTION_PERIOD = 400;
    private static final int OCTOPUS_ANIMATION_PERIOD = 100;

    private static final int TIMER_ACTION_PERIOD = 100;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;

    private static final int COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String LOAD_FILE_NAME = "gaia.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;
    private Octupus player;

    private int totalDx = 0;
    private int totalDy = 0;

    private long next_time;

    public void settings()
    {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup()
    {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(ROWS, COLS,
                createDefaultBackground(imageStore));
        this.view = new WorldView(ROWS, COLS, this, world,
                TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        this.player = new Octupus(new Point(0,0), imageStore.getImageList(OCTOPUS_KEY), OCTOPUS_ACTION_PERIOD, OCTOPUS_ANIMATION_PERIOD);
        world.addEntity(player);
//        world.addEntity(new Shark("shark", new Point(0,14), 5, 100, imageStore.getImageList("shark")));

        scheduleActions(world, scheduler, imageStore);

        next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw()
    {
        long time = System.currentTimeMillis();
        if (time >= next_time)
        {
            this.scheduler.updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
        }

        view.drawViewport();
    }

    public void keyPressed()
    {

        if (key == CODED)
        {

            switch (keyCode)
            {
                case UP:
                    player.moveUp(world);
                    break;
                case DOWN:
                    player.moveDown(world);
                    break;
                case LEFT:
                    player.moveLeft(world);
                    break;
                case RIGHT:
                    player.moveRight(world);
                    break;
            }
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore)
    {
        return new Background(DEFAULT_IMAGE_NAME,
                imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color)
    {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++)
        {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private static void loadImages(String filename, ImageStore imageStore,
                                   PApplet screen)
    {
        try
        {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, screen);
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public static void loadWorld(WorldModel world, String filename, ImageStore imageStore)
    {
        try
        {
            Scanner in = new Scanner(new File(filename));
            world.load(in, imageStore);
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public static void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.getEntities())
        {
            if(entity instanceof AbstractMovingEntity)
                ((AbstractMovingEntity)entity).scheduleActions(scheduler, world, imageStore);
        }
    }

    public static void parseCommandLine(String [] args)
    {
        for (String arg : args)
        {
            switch (arg)
            {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public void mouseClicked(){
        int x = (mouseX / TILE_WIDTH) + totalDx;
        int y = (mouseY / TILE_HEIGHT) + totalDy;

        Point pressed = new Point(x, y);
        if (!world.isOccupied(pressed)){
            Diver diver = new Diver("diver", pressed, 5, 100, imageStore.getImageList("diver"));
            world.addEntity(diver);
            diver.scheduleActions(scheduler, world, imageStore);
        }
        world.shuffleObstacles();

    }


    public static void main(String [] args)
    {
        parseCommandLine(args);
        PApplet.main(SharkGame.class);
    }

}
