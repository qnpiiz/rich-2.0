package fun.rich.utils.math;

import net.minecraft.client.Minecraft;

public class AnimationHelper {

    public static float animation(double animation, double target, double speedTarget) {
        double diff = (target - animation) / Math.max((float) Minecraft.getDebugFPS(), 5) * 15;

        if (diff > 0) {
            diff = Math.max(speedTarget, diff);
            diff = Math.min(target - animation, diff);
        } else if (diff < 0) {
            diff = Math.min(-speedTarget, diff);
            diff = Math.max(target - animation, diff);
        }

        return (float) (animation + diff);
    }
}
