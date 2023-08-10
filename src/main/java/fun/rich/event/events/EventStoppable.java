package fun.rich.event.events;

import lombok.Getter;

@Getter
public abstract class EventStoppable implements Event {

    private boolean stopped;

    public void stop() {
        this.stopped = true;
    }
}
