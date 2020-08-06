public abstract class Action {

    protected final ActionEntity entity;

    public Action(ActionEntity entity) {
        this.entity = entity;
    }

    public abstract void executeAction(EventScheduler scheduler);

}
