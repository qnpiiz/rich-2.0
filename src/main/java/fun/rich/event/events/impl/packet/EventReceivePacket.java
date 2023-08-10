package fun.rich.event.events.impl.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.IPacket;
import fun.rich.event.events.callables.EventCancellable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public class EventReceivePacket extends EventCancellable {
    IPacket<?> packet;
}
