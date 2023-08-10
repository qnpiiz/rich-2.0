package fun.rich.ui.clickgui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.MathHelper;

@Getter
@Setter
public final class ScreenHelper {

    private double x;
    private double y;

    public ScreenHelper(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void interpolate(double targetX, double targetY, double smoothing) {
        this.x = animate(targetX, this.x, smoothing);
        this.y = animate(targetY, this.y, smoothing);
    }

    public void animate(double newX, double newY) {
        this.x = animate(this.x, newX, 1.0);
        this.y = animate(this.y, newY, 1.0);
    }
    public static double animate(final double target, double current, double speed) {
        return MathHelper.lerp(current, target, speed);
    }
}
