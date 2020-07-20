public class Activity implements Action{

    private final ActionEntity entity;
    private final WorldModel world;
    private final ImageStore imageStore;
    private final int repeatCount;

    public Activity(
            ActionEntity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(
            EventScheduler scheduler)
    {
        entity.executeActivity(world, imageStore, scheduler);

    }

}
