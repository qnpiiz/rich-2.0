package fun.rich.utils.math;

public class TimerHelper {

    private long ms = System.currentTimeMillis();

    public boolean hasReached(double milliseconds) {
        return ((System.currentTimeMillis() - ms) > milliseconds);
    }

    public void reset() {
        this.ms = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - ms;
    }
}
