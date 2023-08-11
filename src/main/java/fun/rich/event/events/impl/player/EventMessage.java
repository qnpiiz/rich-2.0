package fun.rich.event.events.impl.player;

import fun.rich.event.events.callables.EventCancellable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public class EventMessage extends EventCancellable {
    String message;
}
