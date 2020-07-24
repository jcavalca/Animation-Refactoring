public class Animation implements Action{
    private final ActionEntity entity;
    private final int repeatCount;

    public Animation(
            ActionEntity entity,
            int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(
            EventScheduler scheduler)
    {
        entity.nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity,
                    entity.createAnimationAction(
                            Math.max(repeatCount - 1,
                                    0)),
                    ((AnimPeriod)entity).getAnimationPeriod());
        }
    }

}
