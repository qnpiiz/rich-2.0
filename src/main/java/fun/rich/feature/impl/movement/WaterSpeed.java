package fun.rich.feature.impl.movement;

import fun.rich.event.events.impl.player.EventUpdate;
import fun.rich.feature.Feature;
import fun.rich.feature.impl.FeatureCategory;
import fun.rich.ui.settings.impl.NumberSetting;
import net.minecraft.potion.Effects;
import fun.rich.event.EventTarget;
import fun.rich.ui.settings.impl.BooleanSetting;
import fun.rich.utils.math.MovementUtils;

public class WaterSpeed extends Feature {

    private final NumberSetting speed;
    private final BooleanSetting speedCheck;

    public WaterSpeed() {
        super("WaterSpeed", FeatureCategory.Movement);
        speed = new NumberSetting("Speed Amount", 0.4f, 0.1F, 4, 0.01F, () -> true);
        speedCheck = new BooleanSetting("Speed Potion Check", false, () -> true);

        addSettings(speedCheck, speed);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!mc.player.isPotionActive(Effects.SPEED) && speedCheck.getBoolValue())
            return;

        if (mc.player.isInWater() || mc.player.isInLava())
            MovementUtils.setSpeed(speed.getNumberValue());
    }
}
