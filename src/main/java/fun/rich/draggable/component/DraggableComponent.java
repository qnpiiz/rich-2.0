package fun.rich.draggable.component;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.MathHelper;

@Getter
@Setter
public class DraggableComponent {

    private final String name;
    private int x, y, width, height;

    private boolean drag;
    private int prevX, prevY;

    public DraggableComponent(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(int mouseX, int mouseY) {
        if (drag) {
            x = (mouseX - prevX);
            y = (mouseY - prevY);
        }
    }

    public void click(int mouseX, int mouseY) {
        if (MathHelper.isMouseHoveringOnRect(x, y, width, height, mouseX, mouseY)) {
            drag = true;
            prevX = mouseX - x;
            prevY = mouseY - y;
        }
    }

    public void release() {
        drag = false;
    }

    public boolean allowDraw() {
        return true;
    }
}
