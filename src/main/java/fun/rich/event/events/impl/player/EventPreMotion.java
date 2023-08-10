package fun.rich.event.events.impl.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import fun.rich.event.events.Event;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class EventPreMotion implements Event {
    float yaw, pitch;
    double x, y, z;
    boolean onGround;
}
