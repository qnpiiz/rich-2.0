package fun.rich.feature.impl.hud;

import fun.rich.Rich;
import org.lwjgl.glfw.GLFW;
import fun.rich.feature.Feature;
import fun.rich.feature.impl.FeatureCategory;
import fun.rich.ui.settings.impl.ColorSetting;
import fun.rich.ui.settings.impl.ListSetting;
import fun.rich.ui.settings.impl.NumberSetting;

import java.awt.*;

public class ClickGUI extends Feature {

    public static ListSetting backGroundColor = new ListSetting("Background Color", "Static", () -> true, "Astolfo", "Rainbow", "Static");
    public static ColorSetting color;
    public static ColorSetting bgcolor;
    public static NumberSetting speed = new NumberSetting("Speed", 35, 10, 100, 1, () -> true);
    public static NumberSetting glowRadius2 = new NumberSetting("Glow Radius", 10, 5, 55, 1, () -> true);

    public ClickGUI() {
        super("ClickGUI", FeatureCategory.Hud);
        setBind(GLFW.GLFW_KEY_RIGHT_SHIFT);

        color = new ColorSetting("Gui Color", new Color(34, 179, 255, 255).getRGB(), () -> true);
        bgcolor = new ColorSetting("Color One", new Color(34, 179, 255, 255).getRGB(), () -> backGroundColor.currentMode.equals("Static"));
        addSettings(color, glowRadius2, speed, backGroundColor, bgcolor);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Rich.instance.clickGui);
        Rich.instance.featureManager.getFeature(ClickGUI.class).setEnabled(false);
        super.onEnable();
    }
}
