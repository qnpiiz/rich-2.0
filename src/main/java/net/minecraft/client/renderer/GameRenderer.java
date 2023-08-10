package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.DownloadTerrainScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.optifine.Config;
import net.optifine.GlErrors;
import net.optifine.Lagometer;
import net.optifine.RandomEntities;
import net.optifine.gui.GuiChatOF;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorResolver;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.TimedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRenderer implements IResourceManagerReloadListener, AutoCloseable
{
    private static final ResourceLocation field_243496_c = new ResourceLocation("textures/misc/nausea.png");
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft mc;
    private final IResourceManager resourceManager;
    private final Random random = new Random();
    private float farPlaneDistance;
    public final FirstPersonRenderer itemRenderer;
    private final MapItemRenderer mapItemRenderer;
    private final RenderTypeBuffers renderTypeBuffers;
    private int rendererUpdateCount;
    private float fovModifierHand;
    private float fovModifierHandPrev;
    private float bossColorModifier;
    private float bossColorModifierPrev;
    private boolean renderHand = true;
    private boolean drawBlockOutline = true;
    private long timeWorldIcon;
    private long prevFrameTime = Util.milliTime();
    private final LightTexture lightmapTexture;
    private final OverlayTexture overlayTexture = new OverlayTexture();
    private boolean debugView;
    private float cameraZoom = 1.0F;
    private float cameraYaw;
    private float cameraPitch;
    @Nullable
    private ItemStack itemActivationItem;
    private int itemActivationTicks;
    private float itemActivationOffX;
    private float itemActivationOffY;
    @Nullable
    private ShaderGroup shaderGroup;
    private static final ResourceLocation[] SHADERS_TEXTURES = new ResourceLocation[] {new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
    public static final int SHADER_COUNT = SHADERS_TEXTURES.length;
    private int shaderIndex = SHADER_COUNT;
    private boolean useShader;
    private final ActiveRenderInfo activeRender = new ActiveRenderInfo();
    private boolean initialized = false;
    private World updatedWorld = null;
    private float clipDistance = 128.0F;
    private long lastServerTime = 0L;
    private int lastServerTicks = 0;
    private int serverWaitTime = 0;
    private int serverWaitTimeCurrent = 0;
    private float avgServerTimeDiff = 0.0F;
    private float avgServerTickDiff = 0.0F;
    private ShaderGroup[] fxaaShaders = new ShaderGroup[10];
    private boolean guiLoadingVisible = false;

    public GameRenderer(Minecraft mcIn, IResourceManager resourceManagerIn, RenderTypeBuffers renderTypeBuffersIn)
    {
        this.mc = mcIn;
        this.resourceManager = resourceManagerIn;
        this.itemRenderer = mcIn.getFirstPersonRenderer();
        this.mapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
        this.lightmapTexture = new LightTexture(this, mcIn);
        this.renderTypeBuffers = renderTypeBuffersIn;
        this.shaderGroup = null;
    }

    public void close()
    {
        this.lightmapTexture.close();
        this.mapItemRenderer.close();
        this.overlayTexture.close();
        this.stopUseShader();
    }

    public void stopUseShader()
    {
        if (this.shaderGroup != null)
        {
            this.shaderGroup.close();
        }

        this.shaderGroup = null;
        this.shaderIndex = SHADER_COUNT;
    }

    public void switchUseShader()
    {
        this.useShader = !this.useShader;
    }

    /**
     * What shader to use when spectating this entity
     */
    public void loadEntityShader(@Nullable Entity entityIn)
    {
        if (this.shaderGroup != null)
        {
            this.shaderGroup.close();
        }

        this.shaderGroup = null;

        if (entityIn instanceof CreeperEntity)
        {
            this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
        }
        else if (entityIn instanceof SpiderEntity)
        {
            this.loadShader(new ResourceLocation("shaders/post/spider.json"));
        }
        else if (entityIn instanceof EndermanEntity)
        {
            this.loadShader(new ResourceLocation("shaders/post/invert.json"));
        }
        else if (Reflector.ForgeHooksClient_loadEntityShader.exists())
        {
            Reflector.call(Reflector.ForgeHooksClient_loadEntityShader, entityIn, this);
        }
    }

    private void loadShader(ResourceLocation resourceLocationIn)
    {
        if (GLX.isUsingFBOs())
        {
            if (this.shaderGroup != null)
            {
                this.shaderGroup.close();
            }

            try
            {
                this.shaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
                this.shaderGroup.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
                this.useShader = true;
            }
            catch (IOException ioexception)
            {
                LOGGER.warn("Failed to load shader: {}", resourceLocationIn, ioexception);
                this.shaderIndex = SHADER_COUNT;
                this.useShader = false;
            }
            catch (JsonSyntaxException jsonsyntaxexception)
            {
                LOGGER.warn("Failed to parse shader: {}", resourceLocationIn, jsonsyntaxexception);
                this.shaderIndex = SHADER_COUNT;
                this.useShader = false;
            }
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        if (this.shaderGroup != null)
        {
            this.shaderGroup.close();
        }

        this.shaderGroup = null;

        if (this.shaderIndex == SHADER_COUNT)
        {
            this.loadEntityShader(this.mc.getRenderViewEntity());
        }
        else
        {
            this.loadShader(SHADERS_TEXTURES[this.shaderIndex]);
        }
    }

    /**
     * Updates the entity renderer
     */
    public void tick()
    {
        this.updateFovModifierHand();
        this.lightmapTexture.tick();

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.player);
        }

        this.activeRender.interpolateHeight();
        ++this.rendererUpdateCount;
        this.itemRenderer.tick();
        this.mc.worldRenderer.addRainParticles(this.activeRender);
        this.bossColorModifierPrev = this.bossColorModifier;

        if (this.mc.ingameGUI.getBossOverlay().shouldDarkenSky())
        {
            this.bossColorModifier += 0.05F;

            if (this.bossColorModifier > 1.0F)
            {
                this.bossColorModifier = 1.0F;
            }
        }
        else if (this.bossColorModifier > 0.0F)
        {
            this.bossColorModifier -= 0.0125F;
        }

        if (this.itemActivationTicks > 0)
        {
            --this.itemActivationTicks;

            if (this.itemActivationTicks == 0)
            {
                this.itemActivationItem = null;
            }
        }
    }

    @Nullable
    public ShaderGroup getShaderGroup()
    {
        return this.shaderGroup;
    }

    public void updateShaderGroupSize(int width, int height)
    {
        if (this.shaderGroup != null)
        {
            this.shaderGroup.createBindFramebuffers(width, height);
        }

        this.mc.worldRenderer.createBindEntityOutlineFbs(width, height);
    }

    /**
     * Gets the block or object that is being moused over.
     */
    public void getMouseOver(float partialTicks)
    {
        Entity entity = this.mc.getRenderViewEntity();

        if (entity != null && this.mc.world != null)
        {
            this.mc.getProfiler().startSection("pick");
            this.mc.pointedEntity = null;
            double d0 = (double)this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = entity.pick(d0, partialTicks, false);
            Vector3d vector3d = entity.getEyePosition(partialTicks);
            boolean flag = false;
            int i = 3;
            double d1 = d0;

            if (this.mc.playerController.extendedReach())
            {
                d1 = 6.0D;
                d0 = d1;
            }
            else
            {
                if (d0 > 3.0D)
                {
                    flag = true;
                }

                d0 = d0;
            }

            d1 = d1 * d1;

            if (this.mc.objectMouseOver != null)
            {
                d1 = this.mc.objectMouseOver.getHitVec().squareDistanceTo(vector3d);
            }

            Vector3d vector3d1 = entity.getLook(1.0F);
            Vector3d vector3d2 = vector3d.add(vector3d1.x * d0, vector3d1.y * d0, vector3d1.z * d0);
            float f = 1.0F;
            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vector3d1.scale(d0)).grow(1.0D, 1.0D, 1.0D);
            EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vector3d, vector3d2, axisalignedbb, (p_lambda$getMouseOver$0_0_) ->
            {
                return !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith();
            }, d1);

            if (entityraytraceresult != null)
            {
                Entity entity1 = entityraytraceresult.getEntity();
                Vector3d vector3d3 = entityraytraceresult.getHitVec();
                double d2 = vector3d.squareDistanceTo(vector3d3);

                if (flag && d2 > 9.0D)
                {
                    this.mc.objectMouseOver = BlockRayTraceResult.createMiss(vector3d3, Direction.getFacingFromVector(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos(vector3d3));
                }
                else if (d2 < d1 || this.mc.objectMouseOver == null)
                {
                    this.mc.objectMouseOver = entityraytraceresult;

                    if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrameEntity)
                    {
                        this.mc.pointedEntity = entity1;
                    }
                }
            }

            this.mc.getProfiler().endSection();
        }
    }

    /**
     * Update FOV modifier hand
     */
    private void updateFovModifierHand()
    {
        float f = 1.0F;

        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayerEntity)
        {
            AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)this.mc.getRenderViewEntity();
            f = abstractclientplayerentity.getFovModifier();
        }

        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand += (f - this.fovModifierHand) * 0.5F;

        if (this.fovModifierHand > 1.5F)
        {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F)
        {
            this.fovModifierHand = 0.1F;
        }
    }

    public double getFOVModifier(ActiveRenderInfo activeRenderInfoIn, float partialTicks, boolean useFOVSetting)
    {
        if (this.debugView)
        {
            return 90.0D;
        }
        else
        {
            double d0 = 70.0D;

            if (useFOVSetting)
            {
                d0 = this.mc.gameSettings.fov;

                if (Config.isDynamicFov())
                {
                    d0 *= (double)MathHelper.lerp(partialTicks, this.fovModifierHandPrev, this.fovModifierHand);
                }
            }

            boolean flag = false;

            if (this.mc.currentScreen == null)
            {
                flag = this.mc.gameSettings.ofKeyBindZoom.isKeyDown();
            }

            if (flag)
            {
                if (!Config.zoomMode)
                {
                    Config.zoomMode = true;
                    Config.zoomSmoothCamera = this.mc.gameSettings.smoothCamera;
                    this.mc.gameSettings.smoothCamera = true;
                    this.mc.worldRenderer.setDisplayListEntitiesDirty();
                }

                if (Config.zoomMode)
                {
                    d0 /= 4.0D;
                }
            }
            else if (Config.zoomMode)
            {
                Config.zoomMode = false;
                this.mc.gameSettings.smoothCamera = Config.zoomSmoothCamera;
                this.mc.worldRenderer.setDisplayListEntitiesDirty();
            }

            if (activeRenderInfoIn.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfoIn.getRenderViewEntity()).getShouldBeDead())
            {
                float f = Math.min((float)((LivingEntity)activeRenderInfoIn.getRenderViewEntity()).deathTime + partialTicks, 20.0F);
                d0 /= (double)((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
            }

            FluidState fluidstate = activeRenderInfoIn.getFluidState();

            if (!fluidstate.isEmpty())
            {
                d0 = d0 * 60.0D / 70.0D;
            }

            return Reflector.ForgeHooksClient_getFOVModifier.exists() ? Reflector.callDouble(Reflector.ForgeHooksClient_getFOVModifier, this, activeRenderInfoIn, partialTicks, d0) : d0;
        }
    }

    private void hurtCameraEffect(MatrixStack matrixStackIn, float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof LivingEntity)
        {
            LivingEntity livingentity = (LivingEntity)this.mc.getRenderViewEntity();
            float f = (float)livingentity.hurtTime - partialTicks;

            if (livingentity.getShouldBeDead())
            {
                float f1 = Math.min((float)livingentity.deathTime + partialTicks, 20.0F);
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(40.0F - 8000.0F / (f1 + 200.0F)));
            }

            if (f < 0.0F)
            {
                return;
            }

            f = f / (float)livingentity.maxHurtTime;
            f = MathHelper.sin(f * f * f * f * (float)Math.PI);
            float f2 = livingentity.attackedAtYaw;
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f2));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-f * 14.0F));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f2));
        }
    }

    private void applyBobbing(MatrixStack matrixStackIn, float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof PlayerEntity)
        {
            PlayerEntity playerentity = (PlayerEntity)this.mc.getRenderViewEntity();
            float f = playerentity.distanceWalkedModified - playerentity.prevDistanceWalkedModified;
            float f1 = -(playerentity.distanceWalkedModified + f * partialTicks);
            float f2 = MathHelper.lerp(partialTicks, playerentity.prevCameraYaw, playerentity.cameraYaw);
            matrixStackIn.translate((double)(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5F), (double)(-Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2)), 0.0D);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F));
        }
    }

    private void renderHand(MatrixStack matrixStackIn, ActiveRenderInfo activeRenderInfoIn, float partialTicks)
    {
        this.renderHand(matrixStackIn, activeRenderInfoIn, partialTicks, true, true, false);
    }

    public void renderHand(MatrixStack p_renderHand_1_, ActiveRenderInfo p_renderHand_2_, float p_renderHand_3_, boolean p_renderHand_4_, boolean p_renderHand_5_, boolean p_renderHand_6_)
    {
        if (!this.debugView)
        {
            Shaders.beginRenderFirstPersonHand(p_renderHand_6_);
            this.resetProjectionMatrix(this.getProjectionMatrix(p_renderHand_2_, p_renderHand_3_, false));
            MatrixStack.Entry matrixstack$entry = p_renderHand_1_.getLast();
            matrixstack$entry.getMatrix().setIdentity();
            matrixstack$entry.getNormal().setIdentity();
            boolean flag = false;

            if (p_renderHand_4_)
            {
                p_renderHand_1_.push();
                this.hurtCameraEffect(p_renderHand_1_, p_renderHand_3_);

                if (this.mc.gameSettings.viewBobbing)
                {
                    this.applyBobbing(p_renderHand_1_, p_renderHand_3_);
                }

                flag = this.mc.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)this.mc.getRenderViewEntity()).isSleeping();

                if (this.mc.gameSettings.getPointOfView().func_243192_a() && !flag && !this.mc.gameSettings.hideGUI && this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR)
                {
                    this.lightmapTexture.enableLightmap();

                    if (Config.isShaders())
                    {
                        ShadersRender.renderItemFP(this.itemRenderer, p_renderHand_3_, p_renderHand_1_, this.renderTypeBuffers.getBufferSource(), this.mc.player, this.mc.getRenderManager().getPackedLight(this.mc.player, p_renderHand_3_), p_renderHand_6_);
                    }
                    else
                    {
                        this.itemRenderer.renderItemInFirstPerson(p_renderHand_3_, p_renderHand_1_, this.renderTypeBuffers.getBufferSource(), this.mc.player, this.mc.getRenderManager().getPackedLight(this.mc.player, p_renderHand_3_));
                    }

                    this.lightmapTexture.disableLightmap();
                }

                p_renderHand_1_.pop();
            }

            Shaders.endRenderFirstPersonHand();

            if (!p_renderHand_5_)
            {
                return;
            }

            this.lightmapTexture.disableLightmap();

            if (this.mc.gameSettings.getPointOfView().func_243192_a() && !flag)
            {
                OverlayRenderer.renderOverlays(this.mc, p_renderHand_1_);
                this.hurtCameraEffect(p_renderHand_1_, p_renderHand_3_);
            }

            if (this.mc.gameSettings.viewBobbing)
            {
                this.applyBobbing(p_renderHand_1_, p_renderHand_3_);
            }
        }
    }

    public void resetProjectionMatrix(Matrix4f matrixIn)
    {
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrixIn);
        RenderSystem.matrixMode(5888);
    }

    public Matrix4f getProjectionMatrix(ActiveRenderInfo activeRenderInfoIn, float partialTicks, boolean useFovSetting)
    {
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.getLast().getMatrix().setIdentity();

        if (Config.isShaders() && Shaders.isRenderingFirstPersonHand())
        {
            Shaders.applyHandDepth(matrixstack);
        }

        this.clipDistance = this.farPlaneDistance * 2.0F;

        if (this.clipDistance < 173.0F)
        {
            this.clipDistance = 173.0F;
        }

        if (this.cameraZoom != 1.0F)
        {
            matrixstack.translate((double)this.cameraYaw, (double)(-this.cameraPitch), 0.0D);
            matrixstack.scale(this.cameraZoom, this.cameraZoom, 1.0F);
        }

        matrixstack.getLast().getMatrix().mul(Matrix4f.perspective(this.getFOVModifier(activeRenderInfoIn, partialTicks, useFovSetting), (float)this.mc.getMainWindow().getFramebufferWidth() / (float)this.mc.getMainWindow().getFramebufferHeight(), 0.05F, this.clipDistance));
        return matrixstack.getLast().getMatrix();
    }

    public static float getNightVisionBrightness(LivingEntity livingEntityIn, float entitylivingbaseIn)
    {
        int i = livingEntityIn.getActivePotionEffect(Effects.NIGHT_VISION).getDuration();
        return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - entitylivingbaseIn) * (float)Math.PI * 0.2F) * 0.3F;
    }

    public void updateCameraAndRender(float partialTicks, long nanoTime, boolean renderWorldIn)
    {
        this.frameInit();

        if (!this.mc.isGameFocused() && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !this.mc.mouseHelper.isRightDown()))
        {
            if (Util.milliTime() - this.prevFrameTime > 500L)
            {
                this.mc.displayInGameMenu(false);
            }
        }
        else
        {
            this.prevFrameTime = Util.milliTime();
        }

        if (!this.mc.skipRenderWorld)
        {
            int i = (int)(this.mc.mouseHelper.getMouseX() * (double)this.mc.getMainWindow().getScaledWidth() / (double)this.mc.getMainWindow().getWidth());
            int j = (int)(this.mc.mouseHelper.getMouseY() * (double)this.mc.getMainWindow().getScaledHeight() / (double)this.mc.getMainWindow().getHeight());

            if (renderWorldIn && this.mc.world != null && !Config.isReloadingResources())
            {
                this.mc.getProfiler().startSection("level");
                this.renderWorld(partialTicks, nanoTime, new MatrixStack());

                if (this.mc.isSingleplayer() && this.timeWorldIcon < Util.milliTime() - 1000L)
                {
                    this.timeWorldIcon = Util.milliTime();

                    if (!this.mc.getIntegratedServer().isWorldIconSet())
                    {
                        this.createWorldIcon();
                    }
                }

                this.mc.worldRenderer.renderEntityOutlineFramebuffer();

                if (this.shaderGroup != null && this.useShader)
                {
                    RenderSystem.disableBlend();
                    RenderSystem.disableDepthTest();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.enableTexture();
                    RenderSystem.matrixMode(5890);
                    RenderSystem.pushMatrix();
                    RenderSystem.loadIdentity();
                    this.shaderGroup.render(partialTicks);
                    RenderSystem.popMatrix();
                    RenderSystem.enableTexture();
                }

                this.mc.getFramebuffer().bindFramebuffer(true);
            }
            else
            {
                RenderSystem.viewport(0, 0, this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
            }

            MainWindow mainwindow = this.mc.getMainWindow();
            RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
            RenderSystem.matrixMode(5889);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, (double)mainwindow.getFramebufferWidth() / mainwindow.getGuiScaleFactor(), (double)mainwindow.getFramebufferHeight() / mainwindow.getGuiScaleFactor(), 0.0D, 1000.0D, 3000.0D);
            RenderSystem.matrixMode(5888);
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
            RenderHelper.setupGui3DDiffuseLighting();
            MatrixStack matrixstack = new MatrixStack();

            if (this.lightmapTexture.isCustom())
            {
                this.lightmapTexture.setAllowed(false);
            }

            if (renderWorldIn && this.mc.world != null)
            {
                this.mc.getProfiler().endStartSection("gui");

                if (this.mc.player != null)
                {
                    float f = MathHelper.lerp(partialTicks, this.mc.player.prevTimeInPortal, this.mc.player.timeInPortal);

                    if (f > 0.0F && this.mc.player.isPotionActive(Effects.NAUSEA) && this.mc.gameSettings.screenEffectScale < 1.0F)
                    {
                        this.func_243497_c(f * (1.0F - this.mc.gameSettings.screenEffectScale));
                    }
                }

                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null)
                {
                    RenderSystem.defaultAlphaFunc();
                    this.renderItemActivation(this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight(), partialTicks);
                    this.mc.ingameGUI.renderIngameGui(matrixstack, partialTicks);

                    if (this.mc.gameSettings.ofShowFps && !this.mc.gameSettings.showDebugInfo)
                    {
                        Config.drawFps(matrixstack);
                    }

                    if (this.mc.gameSettings.showDebugInfo)
                    {
                        Lagometer.showLagometer(matrixstack, (int)this.mc.getMainWindow().getGuiScaleFactor());
                    }

                    RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
                }

                this.mc.getProfiler().endSection();
            }

            if (this.guiLoadingVisible != (this.mc.loadingGui != null))
            {
                if (this.mc.loadingGui != null)
                {
                    ResourceLoadProgressGui.loadLogoTexture(this.mc);

                    if (this.mc.loadingGui instanceof ResourceLoadProgressGui)
                    {
                        ResourceLoadProgressGui resourceloadprogressgui = (ResourceLoadProgressGui)this.mc.loadingGui;
                        resourceloadprogressgui.update();
                    }
                }

                this.guiLoadingVisible = this.mc.loadingGui != null;
            }

            if (this.mc.loadingGui != null)
            {
                try
                {
                    this.mc.loadingGui.render(matrixstack, i, j, this.mc.getTickLength());
                }
                catch (Throwable throwable1)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Rendering overlay");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Overlay render details");
                    crashreportcategory.addDetail("Overlay name", () ->
                    {
                        return this.mc.loadingGui.getClass().getCanonicalName();
                    });
                    throw new ReportedException(crashreport);
                }
            }
            else if (this.mc.currentScreen != null)
            {
                try
                {
                    if (Reflector.ForgeHooksClient_drawScreen.exists())
                    {
                        Reflector.callVoid(Reflector.ForgeHooksClient_drawScreen, this.mc.currentScreen, matrixstack, i, j, this.mc.getTickLength());
                    }
                    else
                    {
                        this.mc.currentScreen.render(matrixstack, i, j, this.mc.getTickLength());
                    }
                }
                catch (Throwable throwable1)
                {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Rendering screen");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Screen render details");
                    crashreportcategory1.addDetail("Screen name", () ->
                    {
                        return this.mc.currentScreen.getClass().getCanonicalName();
                    });
                    crashreportcategory1.addDetail("Mouse location", () ->
                    {
                        return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.mc.mouseHelper.getMouseX(), this.mc.mouseHelper.getMouseY());
                    });
                    crashreportcategory1.addDetail("Screen size", () ->
                    {
                        return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight(), this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), this.mc.getMainWindow().getGuiScaleFactor());
                    });
                    throw new ReportedException(crashreport1);
                }
            }

            this.lightmapTexture.setAllowed(true);
        }

        this.frameFinish();
        this.waitForServerThread();
        MemoryMonitor.update();
        Lagometer.updateLagometer();

        if (this.mc.gameSettings.ofProfiler)
        {
            this.mc.gameSettings.showDebugProfilerChart = true;
        }
    }

    private void createWorldIcon()
    {
        if (this.mc.worldRenderer.getRenderedChunks() > 10 && this.mc.worldRenderer.hasNoChunkUpdates() && !this.mc.getIntegratedServer().isWorldIconSet())
        {
            NativeImage nativeimage = ScreenShotHelper.createScreenshot(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), this.mc.getFramebuffer());
            Util.getRenderingService().execute(() ->
            {
                int i = nativeimage.getWidth();
                int j = nativeimage.getHeight();
                int k = 0;
                int l = 0;

                if (i > j)
                {
                    k = (i - j) / 2;
                    i = j;
                }
                else {
                    l = (j - i) / 2;
                    j = i;
                }

                try (NativeImage nativeimage1 = new NativeImage(64, 64, false))
                {
                    nativeimage.resizeSubRectTo(k, l, i, j, nativeimage1);
                    nativeimage1.write(this.mc.getIntegratedServer().getWorldIconFile());
                }
                catch (IOException ioexception1)
                {
                    LOGGER.warn("Couldn't save auto screenshot", (Throwable)ioexception1);
                }
                finally {
                    nativeimage.close();
                }
            });
        }
    }

    private boolean isDrawBlockOutline()
    {
        if (!this.drawBlockOutline)
        {
            return false;
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            boolean flag = entity instanceof PlayerEntity && !this.mc.gameSettings.hideGUI;

            if (flag && !((PlayerEntity)entity).abilities.allowEdit)
            {
                ItemStack itemstack = ((LivingEntity)entity).getHeldItemMainhand();
                RayTraceResult raytraceresult = this.mc.objectMouseOver;

                if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK)
                {
                    BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
                    BlockState blockstate = this.mc.world.getBlockState(blockpos);

                    if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR)
                    {
                        flag = blockstate.getContainer(this.mc.world, blockpos) != null;
                    }
                    else
                    {
                        CachedBlockInfo cachedblockinfo = new CachedBlockInfo(this.mc.world, blockpos, false);
                        flag = !itemstack.isEmpty() && (itemstack.canDestroy(this.mc.world.getTags(), cachedblockinfo) || itemstack.canPlaceOn(this.mc.world.getTags(), cachedblockinfo));
                    }
                }
            }

            return flag;
        }
    }

    public void setup(float partialTicks, MatrixStack matrixStackIn) {
        ActiveRenderInfo activerenderinfo = this.activeRender;
        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);

        if (Config.isFogFancy())
        {
            this.farPlaneDistance *= 0.95F;
        }

        if (Config.isFogFast())
        {
            this.farPlaneDistance *= 0.83F;
        }

        MatrixStack matrixstack = new MatrixStack();
        matrixstack.getLast().getMatrix().mul(this.getProjectionMatrix(activerenderinfo, partialTicks, true));
        MatrixStack matrixstack1 = matrixstack;

        if (Shaders.isEffectsModelView())
        {
            matrixstack = matrixStackIn;
        }

        this.hurtCameraEffect(matrixstack, partialTicks);

        if (this.mc.gameSettings.viewBobbing)
        {
            this.applyBobbing(matrixstack, partialTicks);
        }

        float f = MathHelper.lerp(partialTicks, this.mc.player.prevTimeInPortal, this.mc.player.timeInPortal) * this.mc.gameSettings.screenEffectScale * this.mc.gameSettings.screenEffectScale;

        if (f > 0.0F)
        {
            int i = this.mc.player.isPotionActive(Effects.NAUSEA) ? 7 : 20;
            float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
            f1 = f1 * f1;
            Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQRT_2 / 2.0F, MathHelper.SQRT_2 / 2.0F);
            matrixstack.rotate(vector3f.rotationDegrees(((float)this.rendererUpdateCount + partialTicks) * (float)i));
            matrixstack.scale(1.0F / f1, 1.0F, 1.0F);
            float f2 = -((float)this.rendererUpdateCount + partialTicks) * (float)i;
            matrixstack.rotate(vector3f.rotationDegrees(f2));
        }

        if (Shaders.isEffectsModelView())
        {
            matrixstack = matrixstack1;
        }

        Matrix4f matrix4f = matrixstack.getLast().getMatrix();
        this.resetProjectionMatrix(matrix4f);
        activerenderinfo.update(this.mc.world, (Entity)(this.mc.getRenderViewEntity() == null ? this.mc.player : this.mc.getRenderViewEntity()), !this.mc.gameSettings.getPointOfView().func_243192_a(), this.mc.gameSettings.getPointOfView().func_243193_b(), partialTicks);

        if (Reflector.ForgeHooksClient_onCameraSetup.exists())
        {
            Object object = Reflector.ForgeHooksClient_onCameraSetup.call(this, activerenderinfo, partialTicks);
            float f4 = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_getYaw);
            float f5 = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_getPitch);
            float f3 = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_getRoll);
            activerenderinfo.setAnglesInternal(f4, f5);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f3));
        }

        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(activerenderinfo.getPitch()));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(activerenderinfo.getYaw() + 180.0F));
    }

    public void setupOverlayRendering()
    {
        MainWindow scaledresolution = mc.getMainWindow();
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
    }

    public void renderWorld(float partialTicks, long finishTimeNano, MatrixStack matrixStackIn)
    {
        this.lightmapTexture.updateLightmap(partialTicks);

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.player);
        }

        this.getMouseOver(partialTicks);

        if (Config.isShaders())
        {
            Shaders.beginRender(this.mc, this.activeRender, partialTicks, finishTimeNano);
        }

        this.mc.getProfiler().startSection("center");
        boolean flag = Config.isShaders();

        if (flag)
        {
            Shaders.beginRenderPass(partialTicks, finishTimeNano);
        }

        boolean flag1 = this.isDrawBlockOutline();
        this.mc.getProfiler().endStartSection("camera");
        ActiveRenderInfo activerenderinfo = this.activeRender;
        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);

        if (Config.isFogFancy())
        {
            this.farPlaneDistance *= 0.95F;
        }

        if (Config.isFogFast())
        {
            this.farPlaneDistance *= 0.83F;
        }

        MatrixStack matrixstack = new MatrixStack();
        matrixstack.getLast().getMatrix().mul(this.getProjectionMatrix(activerenderinfo, partialTicks, true));
        MatrixStack matrixstack1 = matrixstack;

        if (Shaders.isEffectsModelView())
        {
            matrixstack = matrixStackIn;
        }

        this.hurtCameraEffect(matrixstack, partialTicks);

        if (this.mc.gameSettings.viewBobbing)
        {
            this.applyBobbing(matrixstack, partialTicks);
        }

        float f = MathHelper.lerp(partialTicks, this.mc.player.prevTimeInPortal, this.mc.player.timeInPortal) * this.mc.gameSettings.screenEffectScale * this.mc.gameSettings.screenEffectScale;

        if (f > 0.0F)
        {
            int i = this.mc.player.isPotionActive(Effects.NAUSEA) ? 7 : 20;
            float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
            f1 = f1 * f1;
            Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQRT_2 / 2.0F, MathHelper.SQRT_2 / 2.0F);
            matrixstack.rotate(vector3f.rotationDegrees(((float)this.rendererUpdateCount + partialTicks) * (float)i));
            matrixstack.scale(1.0F / f1, 1.0F, 1.0F);
            float f2 = -((float)this.rendererUpdateCount + partialTicks) * (float)i;
            matrixstack.rotate(vector3f.rotationDegrees(f2));
        }

        if (Shaders.isEffectsModelView())
        {
            matrixstack = matrixstack1;
        }

        Matrix4f matrix4f = matrixstack.getLast().getMatrix();
        this.resetProjectionMatrix(matrix4f);
        activerenderinfo.update(this.mc.world, (Entity)(this.mc.getRenderViewEntity() == null ? this.mc.player : this.mc.getRenderViewEntity()), !this.mc.gameSettings.getPointOfView().func_243192_a(), this.mc.gameSettings.getPointOfView().func_243193_b(), partialTicks);

        if (Reflector.ForgeHooksClient_onCameraSetup.exists())
        {
            Object object = Reflector.ForgeHooksClient_onCameraSetup.call(this, activerenderinfo, partialTicks);
            float f4 = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_getYaw);
            float f5 = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_getPitch);
            float f3 = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_getRoll);
            activerenderinfo.setAnglesInternal(f4, f5);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f3));
        }

        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(activerenderinfo.getPitch()));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(activerenderinfo.getYaw() + 180.0F));
        this.mc.worldRenderer.updateCameraAndRender(matrixStackIn, partialTicks, finishTimeNano, flag1, activerenderinfo, this, this.lightmapTexture, matrix4f);

        if (Reflector.ForgeHooksClient_dispatchRenderLast.exists())
        {
            this.mc.getProfiler().endStartSection("forge_render_last");
            Reflector.callVoid(Reflector.ForgeHooksClient_dispatchRenderLast, this.mc.worldRenderer, matrixStackIn, partialTicks, matrix4f, finishTimeNano);
        }

        this.mc.getProfiler().endStartSection("hand");

        if (this.renderHand && !Shaders.isShadowPass)
        {
            if (flag)
            {
                ShadersRender.renderHand1(this, matrixStackIn, activerenderinfo, partialTicks);
                Shaders.renderCompositeFinal();
            }

            RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);

            if (flag)
            {
                ShadersRender.renderFPOverlay(this, matrixStackIn, activerenderinfo, partialTicks);
            }
            else
            {
                this.renderHand(matrixStackIn, activerenderinfo, partialTicks);
            }
        }

        if (flag)
        {
            Shaders.endRender();
        }

        this.mc.getProfiler().endSection();
    }

    public void resetData()
    {
        this.itemActivationItem = null;
        this.mapItemRenderer.clearLoadedMaps();
        this.activeRender.clear();
    }

    public MapItemRenderer getMapItemRenderer()
    {
        return this.mapItemRenderer;
    }

    private void waitForServerThread()
    {
        this.serverWaitTimeCurrent = 0;

        if (Config.isSmoothWorld() && Config.isSingleProcessor())
        {
            if (this.mc.isIntegratedServerRunning())
            {
                IntegratedServer integratedserver = this.mc.getIntegratedServer();

                if (integratedserver != null)
                {
                    boolean flag = this.mc.isGamePaused();

                    if (!flag && !(this.mc.currentScreen instanceof DownloadTerrainScreen))
                    {
                        if (this.serverWaitTime > 0)
                        {
                            Lagometer.timerServer.start();
                            Config.sleep((long)this.serverWaitTime);
                            Lagometer.timerServer.end();
                            this.serverWaitTimeCurrent = this.serverWaitTime;
                        }

                        long i = System.nanoTime() / 1000000L;

                        if (this.lastServerTime != 0L && this.lastServerTicks != 0)
                        {
                            long j = i - this.lastServerTime;

                            if (j < 0L)
                            {
                                this.lastServerTime = i;
                                j = 0L;
                            }

                            if (j >= 50L)
                            {
                                this.lastServerTime = i;
                                int k = integratedserver.getTickCounter();
                                int l = k - this.lastServerTicks;

                                if (l < 0)
                                {
                                    this.lastServerTicks = k;
                                    l = 0;
                                }

                                if (l < 1 && this.serverWaitTime < 100)
                                {
                                    this.serverWaitTime += 2;
                                }

                                if (l > 1 && this.serverWaitTime > 0)
                                {
                                    --this.serverWaitTime;
                                }

                                this.lastServerTicks = k;
                            }
                        }
                        else
                        {
                            this.lastServerTime = i;
                            this.lastServerTicks = integratedserver.getTickCounter();
                            this.avgServerTickDiff = 1.0F;
                            this.avgServerTimeDiff = 50.0F;
                        }
                    }
                    else
                    {
                        if (this.mc.currentScreen instanceof DownloadTerrainScreen)
                        {
                            Config.sleep(20L);
                        }

                        this.lastServerTime = 0L;
                        this.lastServerTicks = 0;
                    }
                }
            }
        }
        else
        {
            this.lastServerTime = 0L;
            this.lastServerTicks = 0;
        }
    }

    private void frameInit()
    {
        Config.frameStart();
        GlErrors.frameStart();

        if (!this.initialized)
        {
            ReflectorResolver.resolve();

            if (Config.getBitsOs() == 64 && Config.getBitsJre() == 32)
            {
                Config.setNotify64BitJava(true);
            }

            this.initialized = true;
        }

        World world = this.mc.world;

        if (world != null)
        {
            if (Config.getNewRelease() != null)
            {
                String s = "HD_U".replace("HD_U", "HD Ultra").replace("L", "Light");
                String s1 = s + " " + Config.getNewRelease();
                StringTextComponent stringtextcomponent = new StringTextComponent(I18n.format("of.message.newVersion", "\u00a7n" + s1 + "\u00a7r"));
                stringtextcomponent.setStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://optifine.net/downloads")));
                this.mc.ingameGUI.getChatGUI().printChatMessage(stringtextcomponent);
                Config.setNewRelease((String)null);
            }

            if (Config.isNotify64BitJava())
            {
                Config.setNotify64BitJava(false);
                StringTextComponent stringtextcomponent1 = new StringTextComponent(I18n.format("of.message.java64Bit"));
                this.mc.ingameGUI.getChatGUI().printChatMessage(stringtextcomponent1);
            }
        }

        if (this.mc.currentScreen instanceof MainMenuScreen)
        {
            this.updateMainMenu((MainMenuScreen)this.mc.currentScreen);
        }

        if (this.updatedWorld != world)
        {
            RandomEntities.worldChanged(this.updatedWorld, world);
            Config.updateThreadPriorities();
            this.lastServerTime = 0L;
            this.lastServerTicks = 0;
            this.updatedWorld = world;
        }

        if (!this.setFxaaShader(Shaders.configAntialiasingLevel))
        {
            Shaders.configAntialiasingLevel = 0;
        }

        if (this.mc.currentScreen != null && this.mc.currentScreen.getClass() == ChatScreen.class)
        {
            this.mc.displayGuiScreen(new GuiChatOF((ChatScreen)this.mc.currentScreen));
        }
    }

    private void frameFinish()
    {
        if (this.mc.world != null && Config.isShowGlErrors() && TimedEvent.isActive("CheckGlErrorFrameFinish", 10000L))
        {
            int i = GlStateManager.getError();

            if (i != 0 && GlErrors.isEnabled(i))
            {
                String s = Config.getGlErrorString(i);
                StringTextComponent stringtextcomponent = new StringTextComponent(I18n.format("of.message.openglError", i, s));
                this.mc.ingameGUI.getChatGUI().printChatMessage(stringtextcomponent);
            }
        }
    }

    private void updateMainMenu(MainMenuScreen p_updateMainMenu_1_)
    {
        try
        {
            String s = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int i = calendar.get(5);
            int j = calendar.get(2) + 1;

            if (i == 8 && j == 4)
            {
                s = "Happy birthday, OptiFine!";
            }

            if (i == 14 && j == 8)
            {
                s = "Happy birthday, sp614x!";
            }

            if (s == null)
            {
                return;
            }

            Reflector.setFieldValue(p_updateMainMenu_1_, Reflector.GuiMainMenu_splashText, s);
        }
        catch (Throwable throwable)
        {
        }
    }

    public boolean setFxaaShader(int p_setFxaaShader_1_)
    {
        if (!GLX.isUsingFBOs())
        {
            return false;
        }
        else if (this.shaderGroup != null && this.shaderGroup != this.fxaaShaders[2] && this.shaderGroup != this.fxaaShaders[4])
        {
            return true;
        }
        else if (p_setFxaaShader_1_ != 2 && p_setFxaaShader_1_ != 4)
        {
            if (this.shaderGroup == null)
            {
                return true;
            }
            else
            {
                this.shaderGroup.close();
                this.shaderGroup = null;
                return true;
            }
        }
        else if (this.shaderGroup != null && this.shaderGroup == this.fxaaShaders[p_setFxaaShader_1_])
        {
            return true;
        }
        else if (this.mc.world == null)
        {
            return true;
        }
        else
        {
            this.loadShader(new ResourceLocation("shaders/post/fxaa_of_" + p_setFxaaShader_1_ + "x.json"));
            this.fxaaShaders[p_setFxaaShader_1_] = this.shaderGroup;
            return this.useShader;
        }
    }

    public IResourceType getResourceType()
    {
        return VanillaResourceType.SHADERS;
    }

    public void displayItemActivation(ItemStack stack)
    {
        this.itemActivationItem = stack;
        this.itemActivationTicks = 40;
        this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
        this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
    }

    public void renderItemActivation(int widthsp, int heightScaled, float partialTicks)
    {
        if (this.itemActivationItem != null && this.itemActivationTicks > 0)
        {
            int i = 40 - this.itemActivationTicks;
            float f = ((float)i + partialTicks) / 40.0F;
            float f1 = f * f;
            float f2 = f * f1;
            float f3 = 10.25F * f2 * f1 - 24.95F * f1 * f1 + 25.5F * f2 - 13.8F * f1 + 4.0F * f;
            float f4 = f3 * (float)Math.PI;
            float f5 = this.itemActivationOffX * (float)(widthsp / 4);
            float f6 = this.itemActivationOffY * (float)(heightScaled / 4);
            RenderSystem.enableAlphaTest();
            RenderSystem.pushMatrix();
            RenderSystem.pushLightingAttributes();
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            MatrixStack matrixstack = new MatrixStack();
            matrixstack.push();
            matrixstack.translate((double)((float)(widthsp / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F))), (double)((float)(heightScaled / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F))), -50.0D);
            float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
            matrixstack.scale(f7, -f7, f7);
            matrixstack.rotate(Vector3f.YP.rotationDegrees(900.0F * MathHelper.abs(MathHelper.sin(f4))));
            matrixstack.rotate(Vector3f.XP.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
            matrixstack.rotate(Vector3f.ZP.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
            IRenderTypeBuffer.Impl irendertypebuffer$impl = this.renderTypeBuffers.getBufferSource();
            this.mc.getItemRenderer().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED, 15728880, OverlayTexture.NO_OVERLAY, matrixstack, irendertypebuffer$impl);
            matrixstack.pop();
            irendertypebuffer$impl.finish();
            RenderSystem.popAttributes();
            RenderSystem.popMatrix();
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
        }
    }

    private void func_243497_c(float p_243497_1_)
    {
        int i = this.mc.getMainWindow().getScaledWidth();
        int j = this.mc.getMainWindow().getScaledHeight();
        double d0 = MathHelper.lerp((double)p_243497_1_, 2.0D, 1.0D);
        float f = 0.2F * p_243497_1_;
        float f1 = 0.4F * p_243497_1_;
        float f2 = 0.2F * p_243497_1_;
        double d1 = (double)i * d0;
        double d2 = (double)j * d0;
        double d3 = ((double)i - d1) / 2.0D;
        double d4 = ((double)j - d2) / 2.0D;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.color4f(f, f1, f2, 1.0F);
        this.mc.getTextureManager().bindTexture(field_243496_c);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(d3, d4 + d2, -90.0D).tex(0.0F, 1.0F).endVertex();
        bufferbuilder.pos(d3 + d1, d4 + d2, -90.0D).tex(1.0F, 1.0F).endVertex();
        bufferbuilder.pos(d3 + d1, d4, -90.0D).tex(1.0F, 0.0F).endVertex();
        bufferbuilder.pos(d3, d4, -90.0D).tex(0.0F, 0.0F).endVertex();
        tessellator.draw();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public float getBossColorModifier(float partialTicks)
    {
        return MathHelper.lerp(partialTicks, this.bossColorModifierPrev, this.bossColorModifier);
    }

    public float getFarPlaneDistance()
    {
        return this.farPlaneDistance;
    }

    public ActiveRenderInfo getActiveRenderInfo()
    {
        return this.activeRender;
    }

    public LightTexture getLightTexture()
    {
        return this.lightmapTexture;
    }

    public OverlayTexture getOverlayTexture()
    {
        return this.overlayTexture;
    }
}
