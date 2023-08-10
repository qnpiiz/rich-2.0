package fun.rich.event.events.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.MainWindow;
import fun.rich.event.events.Event;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public class EventRender2D implements Event {
    MainWindow resolution;
    MatrixStack matrixStack;
}
