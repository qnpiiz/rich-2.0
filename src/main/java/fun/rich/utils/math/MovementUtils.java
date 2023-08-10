package fun.rich.utils.math;

import fun.rich.event.events.impl.player.EventMove;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class MovementUtils {
    
    private static final Minecraft mc = Minecraft.getInstance();
    public static final double WALK_SPEED = 0.221;

    public static void setMotion(double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0 && strafe == 0) {
            mc.player.getMotion().x = 0;
            mc.player.getMotion().z = 0;
        } else {
            if (forward != 0) {
                if (strafe > 0)
                    yaw += (float) (forward > 0 ? -45 : 45);
                else if (strafe < 0)
                    yaw += (float) (forward > 0 ? 45 : -45);

                strafe = 0;
                if (forward > 0)
                    forward = 1;
                else if (forward < 0)
                    forward = -1;
            }

            double sin = MathHelper.sin((float) Math.toRadians(yaw + 90));
            double cos = MathHelper.cos((float) Math.toRadians(yaw + 90));
            mc.player.getMotion().x = forward * speed * cos + strafe * speed * sin;
            mc.player.getMotion().z = forward * speed * sin - strafe * speed * cos;
        }
    }

    public static void setSpeed(double speed) {
        float f = mc.player.movementInput.moveForward;
        float f1 = mc.player.movementInput.moveStrafe;
        float f2 = mc.player.rotationYaw;

        if (f == 0.0F && f1 == 0.0F) {
            mc.player.getMotion().x = 0.0D;
            mc.player.getMotion().z = 0.0D;
        } else if (f != 0.0F) {
            if (f1 >= 1.0F) {
                f2 += (f > 0.0F ? -35 : 35);
                f1 = 0.0F;
            } else if (f1 <= -1.0F) {
                f2 += (f > 0.0F ? 35 : -35);
                f1 = 0.0F;
            }

            if (f > 0.0F)
                f = 1.0F;
            else if (f < 0.0F)
                f = -1.0F;
        }

        double d0 = Math.cos(Math.toRadians(f2 + 90.0F));
        double d1 = Math.sin(Math.toRadians(f2 + 90.0F));
        mc.player.getMotion().x = f * speed * d0 + f1 * speed * d1;
        mc.player.getMotion().z = f * speed * d1 - f1 * speed * d0;
    }

    public static float getMoveDirection() {
        double motionX = mc.player.getMotion().x;
        double motionZ = mc.player.getMotion().z;
        float direction = (float)(Math.atan2(motionX, motionZ) / Math.PI * 180.0D);
        return -direction;
    }

    public static boolean airBlockAboveHead() {
        AxisAlignedBB bb = new AxisAlignedBB(mc.player.getPosX() - 0.3, mc.player.getPosY() + (double)mc.player.getEyeHeight(), mc.player.getPosZ() + 0.3, mc.player.getPosX() + 0.3, mc.player.getPosY() + (!mc.player.isOnGround() ? 1.5 : 2.5), mc.player.getPosZ() - 0.3);
        return mc.world.getCollisionShapes(mc.player, bb).isParallel();
    }

    public static void setMotion(EventMove e, double speed, float pseudoYaw, double aa, double po4) {
        double forward = po4;
        double strafe = aa;
        float yaw = pseudoYaw;

        if (po4 != 0.0) {
            if (aa > 0.0)
                yaw = pseudoYaw + (float) (po4 > 0.0 ? -45 : 45);
            else if (aa < 0.0)
                yaw = pseudoYaw + (float) (po4 > 0.0 ? 45 : -45);

            strafe = 0.0;
            if (po4 > 0.0)
                forward = 1.0;
            else if (po4 < 0.0)
                forward = -1.0;
        }

        if (strafe > 0.0)
            strafe = 1.0;
        else if (strafe < 0.0)
            strafe = -1.0;

        double kak = Math.cos(Math.toRadians(yaw + 90.0f));
        double nety = Math.sin(Math.toRadians(yaw + 90.0f));
        e.setX(forward * speed * kak + strafe * speed * nety);
        e.setZ(forward * speed * nety - strafe * speed * kak);
    }

    public static void setEventSpeed(EventMove event, double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0 && strafe == 0) {
            event.setX(0);
            event.setZ(0);
        } else {
            if (forward != 0) {
                if (strafe > 0)
                    yaw += (forward > 0 ? -45 : 45);
                else if (strafe < 0)
                    yaw += (forward > 0 ? 45 : -45);

                strafe = 0;
                if (forward > 0)
                    forward = 1;
                else if (forward < 0)
                    forward = -1;
            }

            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(mc.player.getPosX() - 0.3, mc.player.getPosY() + mc.player.getEyeHeight(), mc.player.getPosZ() + 0.3, mc.player.getPosX() + 0.3, mc.player.getPosY() + (!mc.player.isOnGround() ? 1.5 : 2.5), mc.player.getPosZ() - 0.3);
        return mc.world.getCollisionShapes(mc.player, axisAlignedBB).isParallel();
    }

    public static void strafe() {
        if (mc.gameSettings.keyBindBack.isKeyDown())
            return;

        MovementUtils.strafe(MovementUtils.getSpeed());
    }

    public static float getSpeed() {
        return (float) Math.sqrt(mc.player.getMotion().x * mc.player.getMotion().x + mc.player.getMotion().z * mc.player.getMotion().z);
    }

    public static float getAllDirection() {
        float rotationYaw = mc.player.rotationYaw;
        float factor = 0f;

        if (mc.player.movementInput.moveForward > 0)
            factor = 1;
        if (mc.player.movementInput.moveForward < 0)
            factor = -1;

        if (factor == 0) {
            if (mc.player.movementInput.moveStrafe > 0)
                rotationYaw -= 90;

            if (mc.player.movementInput.moveStrafe < 0)
                rotationYaw += 90;
        } else {
            if (mc.player.movementInput.moveStrafe > 0)
                rotationYaw -= 45 * factor;

            if (mc.player.movementInput.moveStrafe < 0)
                rotationYaw += 45 * factor;
        }

        if (factor < 0)
            rotationYaw -= 180;

        return (float) Math.toRadians(rotationYaw);
    }

    public static void strafe(float speed) {
        if (!MovementUtils.isMoving())
            return;

        double yaw = MovementUtils.getAllDirection();
        mc.player.getMotion().x = -Math.sin(yaw) * (double) speed;
        mc.player.getMotion().z = Math.cos(yaw) * (double) speed;
    }

    public static boolean isMoving() {
        return mc.player.movementInput.moveStrafe != 0.0 || mc.player.movementInput.moveForward != 0.0;
    }
}
