package fun.rich.feature.impl.movement;

import fun.rich.event.events.impl.player.EventUpdate;
import fun.rich.feature.impl.FeatureCategory;
import fun.rich.event.EventTarget;
import fun.rich.feature.Feature;
import fun.rich.utils.math.MovementUtils;

public class Sprint extends Feature {

    public Sprint() {
        super("Sprint", FeatureCategory.Movement);
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        if (mc.player.getFoodStats().getFoodLevel() / 2 > 3)
            mc.player.setSprinting(MovementUtils.isMoving());
    }

    @Override
    public void onDisable() {
        mc.player.setSprinting(false);
        super.onDisable();
    }
}
