package fun.rich.event.events;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean state);
}
