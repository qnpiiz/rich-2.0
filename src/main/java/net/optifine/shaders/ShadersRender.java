package net.optifine.shaders;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.chunk.Chunk;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlBlendState;
import net.optifine.render.GlCullState;
import net.optifine.render.ICamera;
import net.optifine.render.RenderTypes;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShadersRender
{
    private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");

    public static void setFrustrumPosition(ICamera frustum, double x, double y, double z)
    {
        frustum.setCameraPosition(x, y, z);
    }

    public static void beginTerrainSolid()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.fogEnabled = true;
            Shaders.useProgram(Shaders.ProgramTerrain);
            Shaders.setRenderStage(RenderStage.TERRAIN_SOLID);
        }
    }

    public static void beginTerrainCutoutMipped()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(Shaders.ProgramTerrain);
            Shaders.setRenderStage(RenderStage.TERRAIN_CUTOUT_MIPPED);
        }
    }

    public static void beginTerrainCutout()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(Shaders.ProgramTerrain);
            Shaders.setRenderStage(RenderStage.TERRAIN_CUTOUT);
        }
    }

    public static void endTerrain()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(Shaders.ProgramTexturedLit);
            Shaders.setRenderStage(RenderStage.NONE);
        }
    }

    public static void beginTranslucent()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(Shaders.ProgramWater);
            Shaders.setRenderStage(RenderStage.TERRAIN_TRANSLUCENT);
        }
    }

    public static void endTranslucent()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(Shaders.ProgramTexturedLit);
            Shaders.setRenderStage(RenderStage.NONE);
        }
    }

    public static void beginTripwire()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.setRenderStage(RenderStage.TRIPWIRE);
        }
    }

    public static void endTripwire()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.setRenderStage(RenderStage.NONE);
        }
    }

    public static void renderHand0(GameRenderer er, MatrixStack matrixStackIn, ActiveRenderInfo activeRenderInfo, float partialTicks)
    {
        if (!Shaders.isShadowPass)
        {
            boolean flag = Shaders.isItemToRenderMainTranslucent();
            boolean flag1 = Shaders.isItemToRenderOffTranslucent();

            if (!flag || !flag1)
            {
                Shaders.readCenterDepth();
                Shaders.beginHand(matrixStackIn, false);
                GL30.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                Shaders.setSkipRenderHands(flag, flag1);
                er.renderHand(matrixStackIn, activeRenderInfo, partialTicks, true, false, false);
                Shaders.endHand(matrixStackIn);
                Shaders.setHandsRendered(!flag, !flag1);
                Shaders.setSkipRenderHands(false, false);
            }
        }
    }

    public static void renderHand1(GameRenderer er, MatrixStack matrixStackIn, ActiveRenderInfo activeRenderInfo, float partialTicks)
    {
        if (!Shaders.isShadowPass && !Shaders.isBothHandsRendered())
        {
            Shaders.readCenterDepth();
            GlStateManager.enableBlend();
            Shaders.beginHand(matrixStackIn, true);
            GL30.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Shaders.setSkipRenderHands(Shaders.isHandRenderedMain(), Shaders.isHandRenderedOff());
            er.renderHand(matrixStackIn, activeRenderInfo, partialTicks, true, false, true);
            Shaders.endHand(matrixStackIn);
            Shaders.setHandsRendered(true, true);
            Shaders.setSkipRenderHands(false, false);
        }
    }

    public static void renderItemFP(FirstPersonRenderer itemRenderer, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, ClientPlayerEntity playerEntityIn, int combinedLightIn, boolean renderTranslucent)
    {
        Minecraft.getInstance().worldRenderer.renderedEntity = playerEntityIn;
        GlStateManager.depthMask(true);

        if (renderTranslucent)
        {
            GlStateManager.depthFunc(519);
            matrixStackIn.push();
            DrawBuffers drawbuffers = GlState.getDrawBuffers();
            GlState.setDrawBuffers(Shaders.drawBuffersNone);
            Shaders.renderItemKeepDepthMask = true;
            itemRenderer.renderItemInFirstPerson(partialTicks, matrixStackIn, bufferIn, playerEntityIn, combinedLightIn);
            Shaders.renderItemKeepDepthMask = false;
            GlState.setDrawBuffers(drawbuffers);
            matrixStackIn.pop();
        }

        GlStateManager.depthFunc(515);
        GL30.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        itemRenderer.renderItemInFirstPerson(partialTicks, matrixStackIn, bufferIn, playerEntityIn, combinedLightIn);
        Minecraft.getInstance().worldRenderer.renderedEntity = null;
    }

    public static void renderFPOverlay(GameRenderer er, MatrixStack matrixStackIn, ActiveRenderInfo activeRenderInfo, float partialTicks)
    {
        if (!Shaders.isShadowPass)
        {
            Shaders.beginFPOverlay();
            er.renderHand(matrixStackIn, activeRenderInfo, partialTicks, false, true, false);
            Shaders.endFPOverlay();
        }
    }

    public static void beginBlockDamage()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(Shaders.ProgramDamagedBlock);
            Shaders.setRenderStage(RenderStage.DESTROY);

            if (Shaders.ProgramDamagedBlock.getId() == Shaders.ProgramTerrain.getId())
            {
                GlState.setDrawBuffers(Shaders.drawBuffersColorAtt[0]);
                GlStateManager.depthMask(false);
            }
        }
    }

    public static void endBlockDamage()
    {
        if (Shaders.isRenderingWorld)
        {
            GlStateManager.depthMask(true);
            Shaders.useProgram(Shaders.ProgramTexturedLit);
            Shaders.setRenderStage(RenderStage.NONE);
        }
    }

    public static void beginOutline()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(Shaders.ProgramBasic);
            Shaders.setRenderStage(RenderStage.OUTLINE);
        }
    }

    public static void endOutline()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(Shaders.ProgramTexturedLit);
            Shaders.setRenderStage(RenderStage.NONE);
        }
    }

    public static void beginDebug()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.setRenderStage(RenderStage.DEBUG);
        }
    }

    public static void endDebug()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.setRenderStage(RenderStage.NONE);
        }
    }

    public static void renderShadowMap(GameRenderer entityRenderer, ActiveRenderInfo activeRenderInfo, int pass, float partialTicks, long finishTimeNano)
    {
        if (Shaders.hasShadowMap)
        {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getProfiler().endStartSection("shadow pass");
            WorldRenderer worldrenderer = minecraft.worldRenderer;
            Shaders.isShadowPass = true;
            Shaders.updateProjectionMatrix();
            Shaders.checkGLError("pre shadow");
            GL30.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL30.glMatrixMode(5888);
            GL11.glPushMatrix();
            minecraft.getProfiler().endStartSection("shadow clear");
            Shaders.sfb.bindFramebuffer();
            Shaders.checkGLError("shadow bind sfb");
            minecraft.getProfiler().endStartSection("shadow camera");
            updateActiveRenderInfo(activeRenderInfo, minecraft, partialTicks);
            MatrixStack matrixstack = new MatrixStack();
            Shaders.setCameraShadow(matrixstack, activeRenderInfo, partialTicks);
            Shaders.checkGLError("shadow camera");
            Shaders.dispatchComputes(Shaders.dfb, Shaders.ProgramShadow.getComputePrograms());
            Shaders.useProgram(Shaders.ProgramShadow);
            Shaders.sfb.setDrawBuffers();
            Shaders.checkGLError("shadow drawbuffers");
            GL30.glReadBuffer(0);
            Shaders.checkGLError("shadow readbuffer");
            Shaders.sfb.setDepthTexture();
            Shaders.sfb.setColorTextures(true);
            Shaders.checkFramebufferStatus("shadow fb");
            GL30.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            GL30.glClear(256);

            for (int i = 0; i < Shaders.usedShadowColorBuffers; ++i)
            {
                if (Shaders.shadowBuffersClear[i])
                {
                    Vector4f vector4f = Shaders.shadowBuffersClearColor[i];

                    if (vector4f != null)
                    {
                        GL30.glClearColor(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
                    }
                    else
                    {
                        GL30.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
                    }

                    GlState.setDrawBuffers(Shaders.drawBuffersColorAtt[i]);
                    GL30.glClear(16384);
                }
            }

            Shaders.sfb.setDrawBuffers();
            Shaders.checkGLError("shadow clear");
            minecraft.getProfiler().endStartSection("shadow frustum");
            ClippingHelper clippinghelper = new ClippingHelperDummy();
            minecraft.getProfiler().endStartSection("shadow culling");
            Vector3d vector3d = activeRenderInfo.getProjectedView();
            clippinghelper.setCameraPosition(vector3d.x, vector3d.y, vector3d.z);
            GlStateManager.shadeModel(7425);
            GlStateManager.enableDepthTest();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.lockCull(new GlCullState(false));
            GlStateManager.lockBlend(new GlBlendState(false));
            minecraft.getProfiler().endStartSection("shadow prepareterrain");
            minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            minecraft.getProfiler().endStartSection("shadow setupterrain");
            int j = minecraft.worldRenderer.getNextFrameCount();
            worldrenderer.setupTerrain(activeRenderInfo, clippinghelper, false, j, minecraft.player.isSpectator());
            minecraft.getProfiler().endStartSection("shadow updatechunks");
            minecraft.getProfiler().endStartSection("shadow terrain");
            double d0 = vector3d.getX();
            double d1 = vector3d.getY();
            double d2 = vector3d.getZ();
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();

            if (Shaders.isRenderShadowTerrain())
            {
                GlStateManager.disableAlphaTest();
                worldrenderer.renderBlockLayer(RenderTypes.SOLID, matrixstack, d0, d1, d2);
                Shaders.checkGLError("shadow terrain solid");
                GlStateManager.enableAlphaTest();
                worldrenderer.renderBlockLayer(RenderTypes.CUTOUT_MIPPED, matrixstack, d0, d1, d2);
                Shaders.checkGLError("shadow terrain cutoutmipped");
                minecraft.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
                worldrenderer.renderBlockLayer(RenderTypes.CUTOUT, matrixstack, d0, d1, d2);
                minecraft.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
                Shaders.checkGLError("shadow terrain cutout");
            }

            GlStateManager.shadeModel(7424);
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            minecraft.getProfiler().endStartSection("shadow entities");
            WorldRenderer worldrenderer1 = minecraft.worldRenderer;
            EntityRendererManager entityrenderermanager = minecraft.getRenderManager();
            IRenderTypeBuffer.Impl irendertypebuffer$impl = worldrenderer1.getRenderTypeTextures().getBufferSource();
            boolean flag = Shaders.isShadowPass && !minecraft.player.isSpectator();

            for (Object worldrenderer$localrenderinformationcontainer0 : Shaders.isRenderShadowEntities() ? worldrenderer1.getRenderInfosEntities() : Collections.EMPTY_LIST)
            {
                WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer = (WorldRenderer.LocalRenderInformationContainer) worldrenderer$localrenderinformationcontainer0;
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = worldrenderer$localrenderinformationcontainer.renderChunk;
                Chunk chunk = chunkrenderdispatcher$chunkrender.getChunk();

                for (Entity entity : chunk.getEntityLists()[chunkrenderdispatcher$chunkrender.getPosition().getY() / 16])
                {
                    if ((entityrenderermanager.shouldRender(entity, clippinghelper, d0, d1, d2) || entity.isRidingOrBeingRiddenBy(minecraft.player)) && (entity != activeRenderInfo.getRenderViewEntity() || flag || activeRenderInfo.isThirdPerson() || activeRenderInfo.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfo.getRenderViewEntity()).isSleeping()) && (!(entity instanceof ClientPlayerEntity) || activeRenderInfo.getRenderViewEntity() == entity))
                    {
                        worldrenderer1.renderedEntity = entity;
                        Shaders.nextEntity(entity);
                        worldrenderer1.renderEntity(entity, d0, d1, d2, partialTicks, matrixstack, irendertypebuffer$impl);
                        worldrenderer1.renderedEntity = null;
                    }
                }
            }

            worldrenderer1.checkMatrixStack(matrixstack);
            irendertypebuffer$impl.finish(RenderType.getEntitySolid(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
            irendertypebuffer$impl.finish(RenderType.getEntityCutout(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
            irendertypebuffer$impl.finish(RenderType.getEntityCutoutNoCull(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
            irendertypebuffer$impl.finish(RenderType.getEntitySmoothCutout(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
            Shaders.endEntities();
            Shaders.beginBlockEntities();
            SignTileEntityRenderer.updateTextRenderDistance();
            boolean flag1 = Reflector.IForgeTileEntity_getRenderBoundingBox.exists();
            ClippingHelper clippinghelper1 = clippinghelper;
            label100:

            for (Object worldrenderer$localrenderinformationcontainer10 : Shaders.isRenderShadowBlockEntities() ? worldrenderer1.getRenderInfosTileEntities() : Collections.EMPTY_LIST)
            {
                WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer1 = (WorldRenderer.LocalRenderInformationContainer) worldrenderer$localrenderinformationcontainer10;
                List<TileEntity> list = worldrenderer$localrenderinformationcontainer1.renderChunk.getCompiledChunk().getTileEntities();

                if (!list.isEmpty())
                {
                    Iterator iterator = list.iterator();

                    while (true)
                    {
                        TileEntity tileentity;
                        AxisAlignedBB axisalignedbb;

                        do
                        {
                            if (!iterator.hasNext())
                            {
                                continue label100;
                            }

                            tileentity = (TileEntity)iterator.next();

                            if (!flag1)
                            {
                                break;
                            }

                            axisalignedbb = (AxisAlignedBB)Reflector.call(tileentity, Reflector.IForgeTileEntity_getRenderBoundingBox);
                        }
                        while (axisalignedbb != null && !clippinghelper1.isBoundingBoxInFrustum(axisalignedbb));

                        Shaders.nextBlockEntity(tileentity);
                        BlockPos blockpos = tileentity.getPos();
                        matrixstack.push();
                        matrixstack.translate((double)blockpos.getX() - d0, (double)blockpos.getY() - d1, (double)blockpos.getZ() - d2);
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, matrixstack, irendertypebuffer$impl);
                        matrixstack.pop();
                    }
                }
            }

            worldrenderer1.checkMatrixStack(matrixstack);
            irendertypebuffer$impl.finish(RenderType.getSolid());
            irendertypebuffer$impl.finish(Atlases.getSolidBlockType());
            irendertypebuffer$impl.finish(Atlases.getCutoutBlockType());
            irendertypebuffer$impl.finish(Atlases.getBedType());
            irendertypebuffer$impl.finish(Atlases.getShulkerBoxType());
            irendertypebuffer$impl.finish(Atlases.getSignType());
            irendertypebuffer$impl.finish(Atlases.getChestType());
            irendertypebuffer$impl.finish();
            Shaders.endBlockEntities();
            Shaders.checkGLError("shadow entities");
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.unlockCull();
            GlStateManager.enableCull();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            GlStateManager.alphaFunc(516, 0.1F);

            if (Shaders.usedShadowDepthBuffers >= 2)
            {
                GlStateManager.activeTexture(33989);
                Shaders.checkGLError("pre copy shadow depth");
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
                Shaders.checkGLError("copy shadow depth");
                GlStateManager.activeTexture(33984);
            }

            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.shadeModel(7425);
            Shaders.checkGLError("shadow pre-translucent");
            Shaders.sfb.setDrawBuffers();
            Shaders.checkGLError("shadow drawbuffers pre-translucent");
            Shaders.checkFramebufferStatus("shadow pre-translucent");

            if (Shaders.isRenderShadowTranslucent())
            {
                minecraft.getProfiler().endStartSection("shadow translucent");
                worldrenderer.renderBlockLayer(RenderTypes.TRANSLUCENT, matrixstack, d0, d1, d2);
                Shaders.checkGLError("shadow translucent");
            }

            GlStateManager.unlockBlend();
            GlStateManager.shadeModel(7424);
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GL30.glFlush();
            Shaders.checkGLError("shadow flush");
            Shaders.isShadowPass = false;
            minecraft.getProfiler().endStartSection("shadow postprocess");

            if (Shaders.hasGlGenMipmap)
            {
                Shaders.sfb.generateDepthMipmaps(Shaders.shadowMipmapEnabled);
                Shaders.sfb.generateColorMipmaps(true, Shaders.shadowColorMipmapEnabled);
            }

            Shaders.checkGLError("shadow postprocess");

            if (Shaders.hasShadowcompPrograms)
            {
                Shaders.renderShadowComposites();
            }

            Shaders.dfb.bindFramebuffer();
            GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
            GlState.setDrawBuffers((DrawBuffers)null);
            minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            Shaders.useProgram(Shaders.ProgramTerrain);
            GL30.glMatrixMode(5888);
            GL11.glPopMatrix();
            GL30.glMatrixMode(5889);
            GL11.glPopMatrix();
            GL30.glMatrixMode(5888);
            Shaders.checkGLError("shadow end");
        }
    }

    public static void updateActiveRenderInfo(ActiveRenderInfo activeRenderInfo, Minecraft mc, float partialTicks)
    {
        activeRenderInfo.update(mc.world, (Entity)(mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity()), !mc.gameSettings.getPointOfView().func_243192_a(), mc.gameSettings.getPointOfView().func_243193_b(), partialTicks);
    }

    public static void preRenderChunkLayer(RenderType blockLayerIn)
    {
        if (blockLayerIn == RenderTypes.SOLID)
        {
            beginTerrainSolid();
        }

        if (blockLayerIn == RenderTypes.CUTOUT_MIPPED)
        {
            beginTerrainCutoutMipped();
        }

        if (blockLayerIn == RenderTypes.CUTOUT)
        {
            beginTerrainCutout();
        }

        if (blockLayerIn == RenderTypes.TRANSLUCENT)
        {
            beginTranslucent();
        }

        if (blockLayerIn == RenderType.getTripwire())
        {
            beginTripwire();
        }

        if (Shaders.isRenderBackFace(blockLayerIn))
        {
            GlStateManager.disableCull();
        }

        if (GLX.useVbo())
        {
            GL20.glEnableVertexAttribArray(Shaders.midBlockAttrib);
            GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
        }
    }

    public static void postRenderChunkLayer(RenderType blockLayerIn)
    {
        if (GLX.useVbo())
        {
            GL20.glDisableVertexAttribArray(Shaders.midBlockAttrib);
            GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
        }

        if (Shaders.isRenderBackFace(blockLayerIn))
        {
            GlStateManager.enableCull();
        }
    }

    public static void preRender(RenderType renderType, BufferBuilder buffer)
    {
        if (Shaders.isRenderingWorld)
        {
            if (!Shaders.isShadowPass)
            {
                if (renderType.isGlint())
                {
                    renderEnchantedGlintBegin();
                }
                else if (renderType.getName().equals("eyes"))
                {
                    Shaders.beginSpiderEyes();
                }
                else if (renderType.getName().equals("crumbling"))
                {
                    beginBlockDamage();
                }
                else if (renderType == RenderType.LINES)
                {
                    Shaders.beginLeash();
                }
            }
        }
    }

    public static void postRender(RenderType renderType, BufferBuilder buffer)
    {
        if (Shaders.isRenderingWorld)
        {
            if (!Shaders.isShadowPass)
            {
                if (renderType.isGlint())
                {
                    renderEnchantedGlintEnd();
                }
                else if (renderType.getName().equals("eyes"))
                {
                    Shaders.endSpiderEyes();
                }
                else if (renderType.getName().equals("crumbling"))
                {
                    endBlockDamage();
                }
                else if (renderType == RenderType.LINES)
                {
                    Shaders.endLeash();
                }
            }
        }
    }

    public static void setupArrayPointersVbo()
    {
        int i = 18;
        GL20.glVertexAttribPointer(Shaders.midBlockAttrib, 3, GL11.GL_BYTE, false, 72, 32L);
        GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, GL11.GL_FLOAT, false, 72, 36L);
        GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, GL11.GL_SHORT, false, 72, 44L);
        GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, GL11.GL_SHORT, false, 72, 52L);
    }

    public static void beaconBeamBegin()
    {
        Shaders.useProgram(Shaders.ProgramBeaconBeam);
    }

    public static void beaconBeamStartQuad1()
    {
    }

    public static void beaconBeamStartQuad2()
    {
    }

    public static void beaconBeamDraw1()
    {
    }

    public static void beaconBeamDraw2()
    {
        GlStateManager.disableBlend();
    }

    public static void renderEnchantedGlintBegin()
    {
        Shaders.useProgram(Shaders.ProgramArmorGlint);
    }

    public static void renderEnchantedGlintEnd()
    {
        if (Shaders.isRenderingWorld)
        {
            if (Shaders.isRenderingFirstPersonHand() && Shaders.isRenderBothHands())
            {
                Shaders.useProgram(Shaders.ProgramHand);
            }
            else
            {
                Shaders.useProgram(Shaders.ProgramEntities);
            }
        }
        else
        {
            Shaders.useProgram(Shaders.ProgramNone);
        }
    }

    public static boolean renderEndPortal(EndPortalTileEntity te, float partialTicks, float offset, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        if (!Shaders.isShadowPass && Shaders.activeProgram.getId() == 0)
        {
            return false;
        }
        else
        {
            GlStateManager.disableLighting();
            MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
            Matrix4f matrix4f = matrixstack$entry.getMatrix();
            Matrix3f matrix3f = matrixstack$entry.getNormal();
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntitySolid(END_PORTAL_TEXTURE));
            float f = 0.5F;
            float f1 = f * 0.15F;
            float f2 = f * 0.3F;
            float f3 = f * 0.4F;
            float f4 = 0.0F;
            float f5 = 0.2F;
            float f6 = (float)(System.currentTimeMillis() % 100000L) / 100000.0F;
            float f7 = 0.0F;
            float f8 = 0.0F;
            float f9 = 0.0F;

            if (te.shouldRenderFace(Direction.SOUTH))
            {
                Vector3i vector3i = Direction.SOUTH.getDirectionVec();
                float f10 = (float)vector3i.getX();
                float f11 = (float)vector3i.getY();
                float f12 = (float)vector3i.getZ();
                float f13 = matrix3f.getTransformX(f10, f11, f12);
                float f14 = matrix3f.getTransformY(f10, f11, f12);
                float f15 = matrix3f.getTransformZ(f10, f11, f12);
                ivertexbuilder.pos(matrix4f, f7, f8, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f4 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f13, f14, f15).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f4 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f13, f14, f15).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8 + 1.0F, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f5 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f13, f14, f15).endVertex();
                ivertexbuilder.pos(matrix4f, f7, f8 + 1.0F, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f5 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f13, f14, f15).endVertex();
            }

            if (te.shouldRenderFace(Direction.NORTH))
            {
                Vector3i vector3i1 = Direction.NORTH.getDirectionVec();
                float f16 = (float)vector3i1.getX();
                float f21 = (float)vector3i1.getY();
                float f26 = (float)vector3i1.getZ();
                float f31 = matrix3f.getTransformX(f16, f21, f26);
                float f36 = matrix3f.getTransformY(f16, f21, f26);
                float f41 = matrix3f.getTransformZ(f16, f21, f26);
                ivertexbuilder.pos(matrix4f, f7, f8 + 1.0F, f9).color(f1, f2, f3, 1.0F).tex(f5 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f31, f36, f41).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8 + 1.0F, f9).color(f1, f2, f3, 1.0F).tex(f5 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f31, f36, f41).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8, f9).color(f1, f2, f3, 1.0F).tex(f4 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f31, f36, f41).endVertex();
                ivertexbuilder.pos(matrix4f, f7, f8, f9).color(f1, f2, f3, 1.0F).tex(f4 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f31, f36, f41).endVertex();
            }

            if (te.shouldRenderFace(Direction.EAST))
            {
                Vector3i vector3i2 = Direction.EAST.getDirectionVec();
                float f17 = (float)vector3i2.getX();
                float f22 = (float)vector3i2.getY();
                float f27 = (float)vector3i2.getZ();
                float f32 = matrix3f.getTransformX(f17, f22, f27);
                float f37 = matrix3f.getTransformY(f17, f22, f27);
                float f42 = matrix3f.getTransformZ(f17, f22, f27);
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8 + 1.0F, f9).color(f1, f2, f3, 1.0F).tex(f5 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f32, f37, f42).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8 + 1.0F, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f5 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f32, f37, f42).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f4 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f32, f37, f42).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8, f9).color(f1, f2, f3, 1.0F).tex(f4 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f32, f37, f42).endVertex();
            }

            if (te.shouldRenderFace(Direction.WEST))
            {
                Vector3i vector3i3 = Direction.WEST.getDirectionVec();
                float f18 = (float)vector3i3.getX();
                float f23 = (float)vector3i3.getY();
                float f28 = (float)vector3i3.getZ();
                float f33 = matrix3f.getTransformX(f18, f23, f28);
                float f38 = matrix3f.getTransformY(f18, f23, f28);
                float f43 = matrix3f.getTransformZ(f18, f23, f28);
                ivertexbuilder.pos(matrix4f, f7, f8, f9).color(f1, f2, f3, 1.0F).tex(f4 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f33, f38, f43).endVertex();
                ivertexbuilder.pos(matrix4f, f7, f8, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f4 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f33, f38, f43).endVertex();
                ivertexbuilder.pos(matrix4f, f7, f8 + 1.0F, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f5 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f33, f38, f43).endVertex();
                ivertexbuilder.pos(matrix4f, f7, f8 + 1.0F, f9).color(f1, f2, f3, 1.0F).tex(f5 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f33, f38, f43).endVertex();
            }

            if (te.shouldRenderFace(Direction.DOWN))
            {
                Vector3i vector3i4 = Direction.DOWN.getDirectionVec();
                float f19 = (float)vector3i4.getX();
                float f24 = (float)vector3i4.getY();
                float f29 = (float)vector3i4.getZ();
                float f34 = matrix3f.getTransformX(f19, f24, f29);
                float f39 = matrix3f.getTransformY(f19, f24, f29);
                float f44 = matrix3f.getTransformZ(f19, f24, f29);
                ivertexbuilder.pos(matrix4f, f7, f8, f9).color(f1, f2, f3, 1.0F).tex(f4 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f34, f39, f44).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8, f9).color(f1, f2, f3, 1.0F).tex(f4 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f34, f39, f44).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f5 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f34, f39, f44).endVertex();
                ivertexbuilder.pos(matrix4f, f7, f8, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f5 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f34, f39, f44).endVertex();
            }

            if (te.shouldRenderFace(Direction.UP))
            {
                Vector3i vector3i5 = Direction.UP.getDirectionVec();
                float f20 = (float)vector3i5.getX();
                float f25 = (float)vector3i5.getY();
                float f30 = (float)vector3i5.getZ();
                float f35 = matrix3f.getTransformX(f20, f25, f30);
                float f40 = matrix3f.getTransformY(f20, f25, f30);
                float f45 = matrix3f.getTransformZ(f20, f25, f30);
                ivertexbuilder.pos(matrix4f, f7, f8 + offset, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f4 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f35, f40, f45).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8 + offset, f9 + 1.0F).color(f1, f2, f3, 1.0F).tex(f4 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f35, f40, f45).endVertex();
                ivertexbuilder.pos(matrix4f, f7 + 1.0F, f8 + offset, f9).color(f1, f2, f3, 1.0F).tex(f5 + f6, f5 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f35, f40, f45).endVertex();
                ivertexbuilder.pos(matrix4f, f7, f8 + offset, f9).color(f1, f2, f3, 1.0F).tex(f5 + f6, f4 + f6).overlay(combinedOverlayIn).lightmap(combinedLightIn).normal(f35, f40, f45).endVertex();
            }

            GlStateManager.enableLighting();
            return true;
        }
    }
}
