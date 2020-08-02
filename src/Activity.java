public class Activity extends Action{

    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(
            ActionEntity entity,
            WorldModel world,
            ImageStore imageStore)
    {
        super(entity);
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(
            EventScheduler scheduler)
    {
        entity.executeActivity(world, imageStore, scheduler);

    }

}
