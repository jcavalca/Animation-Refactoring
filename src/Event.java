public final class Event
{
    private final Action ACTION;
    private final long TIME;
    private final Entity ENTITY;

    public Event(Action ACTION, long TIME, Entity entity) {
        this.ACTION = ACTION;
        this.TIME = TIME;
        this.ENTITY = entity;
    }

    // getters created


    public long getTIME() {
        return TIME;
    }

    public Action getACTION() {
        return ACTION;
    }

    public Entity getENTITY() {
        return ENTITY;
    }
}
