public final class Event
{
    private final Action action;
    private final long time;
    private final Entity entity;

    public Event(Action action, long time, Entity entity) {
        this.action = action;
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
