public final class Event
{
    private final Action action;
    private final long time;
    private final Entity entity;

    public Event(Action ACTION, long time, Entity entity) {
        this.action = ACTION;
        this.time = time;
        this.entity = entity;
    }

    // getters created


    public long getTime() {
        return time;
    }

    public Action getAction() {
        return action;
    }

    public Entity getEntity() {
        return entity;
    }
}
