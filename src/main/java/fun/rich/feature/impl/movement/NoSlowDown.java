package fun.rich.feature.impl.movement;

import fun.rich.event.events.impl.player.EventUpdate;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.util.Hand;
import fun.rich.event.EventTarget;
import fun.rich.feature.Feature;
import fun.rich.feature.impl.FeatureCategory;
import fun.rich.ui.settings.impl.ListSetting;

public class NoSlowDown extends Feature {

    public final ListSetting mode;

    public NoSlowDown() {
        // Прописывается в ClientPlayerEntity#livingTick
        super("NoSlow", FeatureCategory.Movement);
        mode = new ListSetting("NoSlow Mode", "Default", () -> true, "Default", "Matrix", "Grim");

        addSettings(mode);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.player.isHandActive()) {
            switch (mode.currentMode) {
                case "Matrix": {
                    if (mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (mc.player.ticksExisted % 2 == 0) {
                            mc.player.getMotion().x *= 0.35;
                            mc.player.getMotion().z *= 0.35;
                        }
                    } else if (mc.player.fallDistance > 0.2) {
                        mc.player.getMotion().x *= 0.9100000262260437;
                        mc.player.getMotion().z *= 0.9100000262260437;
                    }

                    break;
                }
                case "Grim": {
                    if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket((mc.player.inventory.currentItem+1) % 9));
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    }

                    break;
                }
                default: break;
            }
        }
    }
}
