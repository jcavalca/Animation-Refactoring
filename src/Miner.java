public interface Miner extends Entity, ActionEntity{

    boolean move(WorldModel world,
                 Entity target,
                 EventScheduler scheduler);

    Point nextPositionMiner(
            WorldModel world, Point destPos);

}
