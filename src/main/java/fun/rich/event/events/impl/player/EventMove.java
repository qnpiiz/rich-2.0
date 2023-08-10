package fun.rich.event.events.impl.player;

import fun.rich.event.events.callables.EventCancellable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class EventMove extends EventCancellable {
    double x, y, z;
}
