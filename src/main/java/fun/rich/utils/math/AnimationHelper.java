package fun.rich.utils.math;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

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

    public static float move(float from, float to, float minstep, float maxstep, float factor) {
        float f = (to - from) * MathHelper.clamp(factor,0,1);

        if (f < 0)
            f = MathHelper.clamp(f, -maxstep, -minstep);
        else
            f = MathHelper.clamp(f, minstep, maxstep);

        if (Math.abs(f) > Math.abs(to - from))
            return to;

        return from + f;
    }
}
