package fun.rich.feature.impl.hud;

import fun.rich.Rich;
import fun.rich.draggable.component.impl.DraggableWaterMark;
import fun.rich.event.EventTarget;
import fun.rich.event.events.impl.render.EventRender2D;
import fun.rich.feature.Feature;
import fun.rich.feature.impl.FeatureCategory;
import fun.rich.ui.settings.impl.BooleanSetting;
import fun.rich.utils.render.GLUtils;

public class Hud extends Feature {

    public static BooleanSetting waterMark;
    private static final float scale = 2;

    public Hud() {
        super("Hud", FeatureCategory.Hud);

        waterMark = new BooleanSetting("WaterMark", true, () -> true);
        addSettings(waterMark);
    }

    @EventTarget
    public void onRender(EventRender2D event) {
        if (waterMark.getBoolValue()) {
            DraggableWaterMark dwm = (DraggableWaterMark) Rich.instance.draggableHUD.getDraggableComponentByClass(DraggableWaterMark.class);
            dwm.setWidth(180);
            dwm.setHeight(25);
            GLUtils.INSTANCE.rescale(scale);
            mc.rubik_30.drawStringWithFade("RichClient", dwm.getX() + 3, dwm.getY() + 3, event.getMatrixStack());
            mc.rubik_18.drawStringWithFade("Version "+Rich.VERSION, dwm.getX() + 4, dwm.getY() + 19, event.getMatrixStack());

            GLUtils.INSTANCE.rescaleMC();
        }
    }
}
