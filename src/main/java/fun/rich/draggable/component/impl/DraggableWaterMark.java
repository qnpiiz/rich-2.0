package fun.rich.draggable.component.impl;

import fun.rich.Rich;
import fun.rich.draggable.component.DraggableComponent;
import fun.rich.feature.impl.hud.Hud;

public class DraggableWaterMark extends DraggableComponent {

    public DraggableWaterMark() {
        super("WaterMark", 0, 1, 4, 1);
    }

    @Override
    public boolean allowDraw() {
        return Rich.instance.featureManager.getFeature(Hud.class).isEnabled() && Hud.waterMark.getBoolValue();
    }
}
