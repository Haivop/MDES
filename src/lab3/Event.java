package lab3;

public record Event (double time, Runnable action)
        implements Comparable<Event> {
    @Override
    public int compareTo(Event o) {
        int cmp = Double.compare(time, o.time);
        if (cmp != 0) return cmp;
        return Long.compare(action.hashCode(), o.action.hashCode());
    }
}
