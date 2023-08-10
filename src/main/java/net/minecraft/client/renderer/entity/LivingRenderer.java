package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.optifine.Config;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LivingRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M>
{
    private static final Logger LOGGER = LogManager.getLogger();
    public M entityModel;
    protected final List<LayerRenderer<T, M>> layerRenderers = Lists.newArrayList();
    public LivingEntity renderEntity;
    public float renderLimbSwing;
    public float renderLimbSwingAmount;
    public float renderAgeInTicks;
    public float renderHeadYaw;
    public float renderHeadPitch;
    public float renderPartialTicks;
    public static final boolean animateModelLiving = Boolean.getBoolean("animate.model.living");

    public LivingRenderer(EntityRendererManager rendererManager, M entityModelIn, float shadowSizeIn)
    {
        super(rendererManager);
        this.entityModel = entityModelIn;
        this.shadowSize = shadowSizeIn;
    }

    public final boolean addLayer(LayerRenderer<T, M> layer)
    {
        return this.layerRenderers.add(layer);
    }

    public M getEntityModel()
    {
        return this.entityModel;
    }

    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        if (!Reflector.RenderLivingEvent_Pre_Constructor.exists() || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Pre_Constructor, entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn))
        {
            if (animateModelLiving)
            {
                entityIn.limbSwingAmount = 1.0F;
            }

            matrixStackIn.push();
            this.entityModel.swingProgress = this.getSwingProgress(entityIn, partialTicks);
            this.entityModel.isSitting = entityIn.isPassenger();

            if (Reflector.IForgeEntity_shouldRiderSit.exists())
            {
                this.entityModel.isSitting = entityIn.isPassenger() && entityIn.getRidingEntity() != null && Reflector.callBoolean(entityIn.getRidingEntity(), Reflector.IForgeEntity_shouldRiderSit);
            }

            this.entityModel.isChild = entityIn.isChild();
            float f = MathHelper.interpolateAngle(partialTicks, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
            float f1 = MathHelper.interpolateAngle(partialTicks, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
            float f2 = f1 - f;

            if (this.entityModel.isSitting && entityIn.getRidingEntity() instanceof LivingEntity)
            {
                LivingEntity livingentity = (LivingEntity)entityIn.getRidingEntity();
                f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
                f2 = f1 - f;
                float f3 = MathHelper.wrapDegrees(f2);

                if (f3 < -85.0F)
                {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F)
                {
                    f3 = 85.0F;
                }

                f = f1 - f3;

                if (f3 * f3 > 2500.0F)
                {
                    f += f3 * 0.2F;
                }

                f2 = f1 - f;
            }

            float f7 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);

            if (entityIn.getPose() == Pose.SLEEPING)
            {
                Direction direction = entityIn.getBedDirection();

                if (direction != null)
                {
                    float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
                    matrixStackIn.translate((double)((float)(-direction.getXOffset()) * f4), 0.0D, (double)((float)(-direction.getZOffset()) * f4));
                }
            }

            float f8 = this.handleRotationFloat(entityIn, partialTicks);
            this.applyRotations(entityIn, matrixStackIn, f8, f, partialTicks);
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
            matrixStackIn.translate(0.0D, (double) - 1.501F, 0.0D);
            float f9 = 0.0F;
            float f5 = 0.0F;

            if (!entityIn.isPassenger() && entityIn.isAlive())
            {
                f9 = MathHelper.lerp(partialTicks, entityIn.prevLimbSwingAmount, entityIn.limbSwingAmount);
                f5 = entityIn.limbSwing - entityIn.limbSwingAmount * (1.0F - partialTicks);

                if (entityIn.isChild())
                {
                    f5 *= 3.0F;
                }

                if (f9 > 1.0F)
                {
                    f9 = 1.0F;
                }
            }

            this.entityModel.setLivingAnimations(entityIn, f5, f9, partialTicks);
            this.entityModel.setRotationAngles(entityIn, f5, f9, f8, f2, f7);

            if (CustomEntityModels.isActive())
            {
                this.renderEntity = entityIn;
                this.renderLimbSwing = f5;
                this.renderLimbSwingAmount = f9;
                this.renderAgeInTicks = f8;
                this.renderHeadYaw = f2;
                this.renderHeadPitch = f7;
                this.renderPartialTicks = partialTicks;
            }

            boolean flag = Config.isShaders();
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag1 = this.isVisible(entityIn);
            boolean flag2 = !flag1 && !entityIn.isInvisibleToPlayer(minecraft.player);
            boolean flag3 = minecraft.isEntityGlowing(entityIn);
            RenderType rendertype = this.func_230496_a_(entityIn, flag1, flag2, flag3);

            if (rendertype != null)
            {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
                float f6 = this.getOverlayProgress(entityIn, partialTicks);

                if (flag)
                {
                    if (entityIn.hurtTime > 0 || entityIn.deathTime > 0)
                    {
                        Shaders.setEntityColor(1.0F, 0.0F, 0.0F, 0.3F);
                    }

                    if (f6 > 0.0F)
                    {
                        Shaders.setEntityColor(f6, f6, f6, 0.5F);
                    }
                }

                int i = getPackedOverlay(entityIn, f6);
                this.entityModel.render(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag2 ? 0.15F : 1.0F);
            }

            if (!entityIn.isSpectator())
            {
                for (LayerRenderer<T, M> layerrenderer : this.layerRenderers)
                {
                    layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f9, partialTicks, f8, f2, f7);
                }
            }

            if (Config.isShaders())
            {
                Shaders.setEntityColor(0.0F, 0.0F, 0.0F, 0.0F);
            }

            if (CustomEntityModels.isActive())
            {
                this.renderEntity = null;
            }

            matrixStackIn.pop();
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

            if (Reflector.RenderLivingEvent_Post_Constructor.exists())
            {
                Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Post_Constructor, entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }
        }
    }

    @Nullable
    protected RenderType func_230496_a_(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_)
    {
        ResourceLocation resourcelocation = this.getEntityTexture(p_230496_1_);

        if (this.getLocationTextureCustom() != null)
        {
            resourcelocation = this.getLocationTextureCustom();
        }

        if (p_230496_3_)
        {
            return RenderType.getItemEntityTranslucentCull(resourcelocation);
        }
        else if (p_230496_2_)
        {
            return this.entityModel.getRenderType(resourcelocation);
        }
        else if (p_230496_1_.isGlowing() && !Config.getMinecraft().worldRenderer.isRenderEntityOutlines())
        {
            return this.entityModel.getRenderType(resourcelocation);
        }
        else
        {
            return p_230496_4_ ? RenderType.getOutline(resourcelocation) : null;
        }
    }

    public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn)
    {
        return OverlayTexture.getPackedUV(OverlayTexture.getU(uIn), OverlayTexture.getV(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
    }

    protected boolean isVisible(T livingEntityIn)
    {
        return !livingEntityIn.isInvisible();
    }

    private static float getFacingAngle(Direction facingIn)
    {
        switch (facingIn)
        {
            case SOUTH:
                return 90.0F;

            case WEST:
                return 0.0F;

            case NORTH:
                return 270.0F;

            case EAST:
                return 180.0F;

            default:
                return 0.0F;
        }
    }

    protected boolean func_230495_a_(T p_230495_1_)
    {
        return false;
    }

    protected void applyRotations(T entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        if (this.func_230495_a_(entityLiving))
        {
            rotationYaw += (float)(Math.cos((double)entityLiving.ticksExisted * 3.25D) * Math.PI * (double)0.4F);
        }

        Pose pose = entityLiving.getPose();

        if (pose != Pose.SLEEPING)
        {
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
        }

        if (entityLiving.deathTime > 0)
        {
            float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt(f);

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
        }
        else if (entityLiving.isSpinAttacking())
        {
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.rotationPitch));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(((float)entityLiving.ticksExisted + partialTicks) * -75.0F));
        }
        else if (pose == Pose.SLEEPING)
        {
            Direction direction = entityLiving.getBedDirection();
            float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(this.getDeathMaxRotation(entityLiving)));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270.0F));
        }
        else if (entityLiving.hasCustomName() || entityLiving instanceof PlayerEntity)
        {
            String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName().getString());

            if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity) || ((PlayerEntity)entityLiving).isWearing(PlayerModelPart.CAPE)))
            {
                matrixStackIn.translate(0.0D, (double)(entityLiving.getHeight() + 0.1F), 0.0D);
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
            }
        }
    }

    /**
     * Returns where in the swing animation the living entity is (from 0 to 1).  Args : entity, partialTickTime
     */
    protected float getSwingProgress(T livingBase, float partialTickTime)
    {
        return livingBase.getSwingProgress(partialTickTime);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(T livingBase, float partialTicks)
    {
        return (float)livingBase.ticksExisted + partialTicks;
    }

    protected float getDeathMaxRotation(T entityLivingBaseIn)
    {
        return 90.0F;
    }

    protected float getOverlayProgress(T livingEntityIn, float partialTicks)
    {
        return 0.0F;
    }

    protected void preRenderCallback(T entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime)
    {
    }

    protected boolean canRenderName(T entity)
    {
        double d0 = this.renderManager.squareDistanceTo(entity);
        float f = entity.isDiscrete() ? 32.0F : 64.0F;

        if (d0 >= (double)(f * f))
        {
            return false;
        }
        else
        {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity clientplayerentity = minecraft.player;
            boolean flag = !entity.isInvisibleToPlayer(clientplayerentity);

            if (entity != clientplayerentity)
            {
                Team team = entity.getTeam();
                Team team1 = clientplayerentity.getTeam();

                if (team != null)
                {
                    Team.Visible team$visible = team.getNameTagVisibility();

                    switch (team$visible)
                    {
                        case ALWAYS:
                            return flag;

                        case NEVER:
                            return false;

                        case HIDE_FOR_OTHER_TEAMS:
                            return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);

                        case HIDE_FOR_OWN_TEAM:
                            return team1 == null ? flag : !team.isSameTeam(team1) && flag;

                        default:
                            return true;
                    }
                }
            }

            return Minecraft.isGuiEnabled() && entity != minecraft.getRenderViewEntity() && flag && !entity.isBeingRidden();
        }
    }

    public List<LayerRenderer<T, M>> getLayerRenderers()
    {
        return this.layerRenderers;
    }
}
