import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class Factory {

    public static Blacksmith createBlacksmith(
            String id, Point position, List<PImage> images)
    {
        return new Blacksmith(id, position, images
                );
    }

    public static Miner_Full createMinerFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Miner_Full(id, position, images,
                resourceLimit, actionPeriod,
                animationPeriod);
    }

    public static Miner_Not_Full createMinerNotFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Miner_Not_Full(id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod);
    }

    public static Obstacle createObstacle(
            String id, Point position, List<PImage> images)
    {
        return new Obstacle(id, position, images);
    }

    public static Ore createOre(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Ore(id, position, images,
                actionPeriod);
    }

    public static Ore_Blob createOreBlob(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Ore_Blob( id, position, images,
                actionPeriod, animationPeriod);
    }

    public static Quake createQuake(
            Point position, List<PImage> images)
    {
        return new Quake(Functions.QUAKE_ID, position, images,
                Functions.QUAKE_ACTION_PERIOD, Functions.QUAKE_ANIMATION_PERIOD);
    }

    public static Vein createVein(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Vein(id, position, images,
                actionPeriod);
    }

    public static Volcano createVolcano(
            Point position, List<PImage> images)
    {
        return new Volcano(Functions.VOLCANO_ID, position, images,
                Functions.VOLCANO_ACTION_PERIOD, Functions.VOLCANO_ANIMATION_PERIOD);
    }

    public static Dragon createDragon(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Dragon( id, position, images,
                actionPeriod, animationPeriod);
    }

    public static Fire createFire(
            Point position, List<PImage> images)
    {
        return new Fire(Functions.FIRE_ID, position, images,
                Functions.FIRE_ACTION_PERIOD, Functions.FIRE_ANIMATION_PERIOD);
    }

}
