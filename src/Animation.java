public class Animation extends Action{

    private final int repeatCount;

    public Animation(
            ActionEntity entity,
            int repeatCount)
    {
        super(entity);
        this.repeatCount = repeatCount;
    }

    public void executeAction(
            EventScheduler scheduler)
    {
        ((AnimEntity)entity).nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity,
                    ((AnimEntity)entity).createAnimationAction(
                            Math.max(repeatCount - 1,
                                    0)),
                    ((AnimEntity)entity).getAnimationPeriod());
        }
    }

}
