public abstract class Action {

    protected final ActionEntity entity;

    public Action(ActionEntity entity) {
        this.entity = entity;
    }

    abstract void executeAction(EventScheduler scheduler);

}
