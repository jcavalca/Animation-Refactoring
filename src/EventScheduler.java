import java.util.*;

public final class EventScheduler
{
    private final PriorityQueue<Event> EVENTQUEUE;
    private final Map<Entity, List<Event>> PENDINDEVENTS;
    private final double TIMESCALE;

    public EventScheduler(double TIMESCALE) {
        this.EVENTQUEUE = new PriorityQueue<>(new EventComparator());
        this.PENDINDEVENTS = new HashMap<>();
        this.TIMESCALE = TIMESCALE;
    }

    public void scheduleEvent(
            Entity entity,
            Action action,
            long afterPeriod)
    {
        long time = System.currentTimeMillis() + (long)(afterPeriod
                * this.TIMESCALE);
        Event event = new Event(action, time, entity);

        this.EVENTQUEUE.add(event);

        // update list of pending events for the given entity
        List<Event> pending = this.PENDINDEVENTS.getOrDefault(entity,
                new LinkedList<>());
        pending.add(event);
        this.PENDINDEVENTS.put(entity, pending);
    }

    public void unscheduleAllEvents(Entity entity)
    {
        List<Event> pending = this.PENDINDEVENTS.remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                this.EVENTQUEUE.remove(event);
            }
        }
    }

    private void removePendingEvent(
             Event event)
    {
        List<Event> pending = this.PENDINDEVENTS.get(event.getENTITY());

        if (pending != null) {
            pending.remove(event);
        }
    }

    public void updateOnTime(long time) {
        while (!this.EVENTQUEUE.isEmpty()
                && this.EVENTQUEUE.peek().getTIME() < time) {
            Event next = this.EVENTQUEUE.poll();

            this.removePendingEvent(next);

            next.getACTION().executeAction(this);
        }
    }
}
