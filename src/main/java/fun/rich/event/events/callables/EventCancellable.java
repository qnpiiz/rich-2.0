package fun.rich.event.events.callables;

import fun.rich.event.events.Cancellable;
import fun.rich.event.events.Event;

public abstract class EventCancellable implements Event, Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        this.cancelled = state;
    }
}
