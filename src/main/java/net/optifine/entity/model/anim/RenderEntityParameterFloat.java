package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.optifine.expr.IExpressionFloat;

public enum RenderEntityParameterFloat implements IExpressionFloat
{
    LIMB_SWING("limb_swing"),
    LIMB_SWING_SPEED("limb_speed"),
    AGE("age"),
    HEAD_YAW("head_yaw"),
    HEAD_PITCH("head_pitch"),
    HEALTH("health"),
    HURT_TIME("hurt_time"),
    IDLE_TIME("idle_time"),
    MAX_HEALTH("max_health"),
    MOVE_FORWARD("move_forward"),
    MOVE_STRAFING("move_strafing"),
    PARTIAL_TICKS("partial_ticks"),
    POS_X("pos_x"),
    POS_Y("pos_y"),
    POS_Z("pos_z"),
    REVENGE_TIME("revenge_time"),
    SWING_PROGRESS("swing_progress");

    private String name;
    private EntityRendererManager renderManager;
    private static final RenderEntityParameterFloat[] VALUES = values();

    private RenderEntityParameterFloat(String name)
    {
        this.name = name;
        this.renderManager = Minecraft.getInstance().getRenderManager();
    }

    public String getName()
    {
        return this.name;
    }

    public float eval()
    {
        EntityRenderer entityrenderer = this.renderManager.renderRender;

        if (entityrenderer == null)
        {
            return 0.0F;
        }
        else
        {
            if (entityrenderer instanceof LivingRenderer)
            {
                LivingRenderer livingrenderer = (LivingRenderer)entityrenderer;

                switch (this)
                {
                    case LIMB_SWING:
                        return livingrenderer.renderLimbSwing;

                    case LIMB_SWING_SPEED:
                        return livingrenderer.renderLimbSwingAmount;

                    case AGE:
                        return livingrenderer.renderAgeInTicks;

                    case HEAD_YAW:
                        return livingrenderer.renderHeadYaw;

                    case HEAD_PITCH:
                        return livingrenderer.renderHeadPitch;

                    default:
                        LivingEntity livingentity = livingrenderer.renderEntity;

                        if (livingentity == null)
                        {
                            return 0.0F;
                        }

                        switch (this)
                        {
                            case HEALTH:
                                return livingentity.getHealth();

                            case HURT_TIME:
                                return (float)livingentity.hurtTime;

                            case IDLE_TIME:
                                return (float)livingentity.getIdleTime();

                            case MAX_HEALTH:
                                return livingentity.getMaxHealth();

                            case MOVE_FORWARD:
                                return livingentity.moveForward;

                            case MOVE_STRAFING:
                                return livingentity.moveStrafing;

                            case POS_X:
                                return (float)livingentity.getPosX();

                            case POS_Y:
                                return (float)livingentity.getPosY();

                            case POS_Z:
                                return (float)livingentity.getPosZ();

                            case REVENGE_TIME:
                                return (float)livingentity.getRevengeTimer();

                            case SWING_PROGRESS:
                                return livingentity.getSwingProgress(livingrenderer.renderPartialTicks);
                        }
                }
            }

            return 0.0F;
        }
    }

    public static RenderEntityParameterFloat parse(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            for (int i = 0; i < VALUES.length; ++i)
            {
                RenderEntityParameterFloat renderentityparameterfloat = VALUES[i];

                if (renderentityparameterfloat.getName().equals(str))
                {
                    return renderentityparameterfloat;
                }
            }

            return null;
        }
    }
}
