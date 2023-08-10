package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.optifine.expr.IExpressionBool;

public enum RenderEntityParameterBool implements IExpressionBool
{
    IS_ALIVE("is_alive"),
    IS_BURNING("is_burning"),
    IS_CHILD("is_child"),
    IS_GLOWING("is_glowing"),
    IS_HURT("is_hurt"),
    IS_IN_LAVA("is_in_lava"),
    IS_IN_WATER("is_in_water"),
    IS_INVISIBLE("is_invisible"),
    IS_ON_GROUND("is_on_ground"),
    IS_RIDDEN("is_ridden"),
    IS_RIDING("is_riding"),
    IS_SNEAKING("is_sneaking"),
    IS_SPRINTING("is_sprinting"),
    IS_WET("is_wet");

    private String name;
    private EntityRendererManager renderManager;
    private static final RenderEntityParameterBool[] VALUES = values();

    private RenderEntityParameterBool(String name)
    {
        this.name = name;
        this.renderManager = Minecraft.getInstance().getRenderManager();
    }

    public String getName()
    {
        return this.name;
    }

    public boolean eval()
    {
        EntityRenderer entityrenderer = this.renderManager.renderRender;

        if (entityrenderer == null)
        {
            return false;
        }
        else
        {
            if (entityrenderer instanceof LivingRenderer)
            {
                LivingRenderer livingrenderer = (LivingRenderer)entityrenderer;
                LivingEntity livingentity = livingrenderer.renderEntity;

                if (livingentity == null)
                {
                    return false;
                }

                switch (this)
                {
                    case IS_ALIVE:
                        return livingentity.isAlive();

                    case IS_BURNING:
                        return livingentity.isBurning();

                    case IS_CHILD:
                        return livingentity.isChild();

                    case IS_GLOWING:
                        return livingentity.isGlowing();

                    case IS_HURT:
                        return livingentity.hurtTime > 0;

                    case IS_IN_LAVA:
                        return livingentity.isInLava();

                    case IS_IN_WATER:
                        return livingentity.isInWater();

                    case IS_INVISIBLE:
                        return livingentity.isInvisible();

                    case IS_ON_GROUND:
                        return livingentity.isOnGround();

                    case IS_RIDDEN:
                        return livingentity.isBeingRidden();

                    case IS_RIDING:
                        return livingentity.isPassenger();

                    case IS_SNEAKING:
                        return livingentity.isCrouching();

                    case IS_SPRINTING:
                        return livingentity.isSprinting();

                    case IS_WET:
                        return livingentity.isWet();
                }
            }

            return false;
        }
    }

    public static RenderEntityParameterBool parse(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            for (int i = 0; i < VALUES.length; ++i)
            {
                RenderEntityParameterBool renderentityparameterbool = VALUES[i];

                if (renderentityparameterbool.getName().equals(str))
                {
                    return renderentityparameterbool;
                }
            }

            return null;
        }
    }
}
