public interface ActionEntity extends Entity{

    void executeActivity( WorldModel world,
                     ImageStore imageStore,
                     EventScheduler scheduler);
    Action createActivityAction(
            WorldModel world, ImageStore imageStore);

    Action createAnimationAction(int repeatCount);

        void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore);

}
