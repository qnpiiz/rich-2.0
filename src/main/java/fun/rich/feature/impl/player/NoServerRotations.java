package fun.rich.feature.impl.player;

import fun.rich.event.events.impl.packet.EventReceivePacket;
import fun.rich.feature.impl.FeatureCategory;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import fun.rich.event.EventTarget;
import fun.rich.feature.Feature;

public class NoServerRotations extends Feature {

    public NoServerRotations() {
        super("NoServerRotations", FeatureCategory.Player);
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPlayerPositionLookPacket) {
            SPlayerPositionLookPacket packet = (SPlayerPositionLookPacket) event.getPacket();
            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
        }
    }
}
