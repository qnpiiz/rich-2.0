package fun.rich.draggable;

import com.google.common.collect.Lists;
import fun.rich.draggable.component.DraggableComponent;
import fun.rich.draggable.component.impl.DraggableWaterMark;
import lombok.Getter;

import java.util.List;

@Getter
public class DraggableHUD {

    private final DraggableScreen screen;
    private final List<DraggableComponent> components;

    public DraggableHUD() {
        screen = new DraggableScreen();
        components = Lists.newLinkedList();
        components.add(new DraggableWaterMark());
    }

    public DraggableComponent getDraggableComponentByClass(Class<? extends DraggableComponent> classs) {
        for (DraggableComponent draggableComponent : components) {
            if (draggableComponent.getClass() == classs) {
                return draggableComponent;
            }
        }

        return null;
    }
}
