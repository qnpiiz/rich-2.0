package net.minecraft.client.renderer;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.CustomItems;
import net.optifine.EmissiveTextures;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.render.VertexBuilderWrapper;
import net.optifine.shaders.Shaders;

public class ItemRenderer implements IResourceManagerReloadListener
{
    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final Set<Item> ITEM_MODEL_BLACKLIST = Sets.newHashSet(Items.AIR);
    public float zLevel;
    private final ItemModelMesher itemModelMesher;
    private final TextureManager textureManager;
    private final ItemColors itemColors;
    public ModelManager modelManager = null;
    private static boolean renderItemGui = false;

    public ItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn)
    {
        this.textureManager = textureManagerIn;
        this.modelManager = modelManagerIn;

        if (Reflector.ItemModelMesherForge_Constructor.exists())
        {
            this.itemModelMesher = (ItemModelMesher)Reflector.newInstance(Reflector.ItemModelMesherForge_Constructor, this.modelManager);
        }
        else
        {
            this.itemModelMesher = new ItemModelMesher(modelManagerIn);
        }

        for (Item item : Registry.ITEM)
        {
            if (!ITEM_MODEL_BLACKLIST.contains(item))
            {
                this.itemModelMesher.register(item, new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory"));
            }
        }

        this.itemColors = itemColorsIn;
    }

    public ItemModelMesher getItemModelMesher()
    {
        return this.itemModelMesher;
    }

    public void renderModel(IBakedModel modelIn, ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn)
    {
        if (Config.isMultiTexture())
        {
            bufferIn.setRenderBlocks(true);
        }

        Random random = new Random();
        long i = 42L;

        for (Direction direction : Direction.VALUES)
        {
            random.setSeed(42L);
            this.renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, direction, random), stack, combinedLightIn, combinedOverlayIn);
        }

        random.setSeed(42L);
        this.renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, (Direction)null, random), stack, combinedLightIn, combinedOverlayIn);
    }

    public void renderItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn)
    {
        if (!itemStackIn.isEmpty())
        {
            matrixStackIn.push();
            boolean flag = transformTypeIn == ItemCameraTransforms.TransformType.GUI || transformTypeIn == ItemCameraTransforms.TransformType.GROUND || transformTypeIn == ItemCameraTransforms.TransformType.FIXED;

            if (itemStackIn.getItem() == Items.TRIDENT && flag)
            {
                modelIn = this.itemModelMesher.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            }

            if (Reflector.ForgeHooksClient_handleCameraTransforms.exists())
            {
                modelIn = (IBakedModel)Reflector.ForgeHooksClient_handleCameraTransforms.call(matrixStackIn, modelIn, transformTypeIn, leftHand);
            }
            else
            {
                modelIn.getItemCameraTransforms().getTransform(transformTypeIn).apply(leftHand, matrixStackIn);
            }

            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

            if (!modelIn.isBuiltInRenderer() && (itemStackIn.getItem() != Items.TRIDENT || flag))
            {
                boolean flag1;

                if (transformTypeIn != ItemCameraTransforms.TransformType.GUI && !transformTypeIn.isFirstPerson() && itemStackIn.getItem() instanceof BlockItem)
                {
                    Block block = ((BlockItem)itemStackIn.getItem()).getBlock();
                    flag1 = !(block instanceof BreakableBlock) && !(block instanceof StainedGlassPaneBlock);
                }
                else
                {
                    flag1 = true;
                }

                if (modelIn.isLayered())
                {
                    Reflector.ForgeHooksClient_drawItemLayered.call(this, modelIn, itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, flag1);
                }
                else
                {
                    RenderType rendertype = RenderTypeLookup.func_239219_a_(itemStackIn, flag1);
                    IVertexBuilder ivertexbuilder;

                    if (itemStackIn.getItem() == Items.COMPASS && itemStackIn.hasEffect())
                    {
                        matrixStackIn.push();
                        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();

                        if (transformTypeIn == ItemCameraTransforms.TransformType.GUI)
                        {
                            matrixstack$entry.getMatrix().mul(0.5F);
                        }
                        else if (transformTypeIn.isFirstPerson())
                        {
                            matrixstack$entry.getMatrix().mul(0.75F);
                        }

                        if (flag1)
                        {
                            ivertexbuilder = getDirectGlintVertexBuilder(bufferIn, rendertype, matrixstack$entry);
                        }
                        else
                        {
                            ivertexbuilder = getGlintVertexBuilder(bufferIn, rendertype, matrixstack$entry);
                        }

                        matrixStackIn.pop();
                    }
                    else if (flag1)
                    {
                        ivertexbuilder = getEntityGlintVertexBuilder(bufferIn, rendertype, true, itemStackIn.hasEffect());
                    }
                    else
                    {
                        ivertexbuilder = getBuffer(bufferIn, rendertype, true, itemStackIn.hasEffect());
                    }

                    if (Config.isCustomItems())
                    {
                        modelIn = CustomItems.getCustomItemModel(itemStackIn, modelIn, ItemOverrideList.lastModelLocation, false);
                        ItemOverrideList.lastModelLocation = null;
                    }

                    if (EmissiveTextures.isActive())
                    {
                        EmissiveTextures.beginRender();
                    }

                    this.renderModel(modelIn, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);

                    if (EmissiveTextures.isActive())
                    {
                        if (EmissiveTextures.hasEmissive())
                        {
                            EmissiveTextures.beginRenderEmissive();
                            IVertexBuilder ivertexbuilder1 = ivertexbuilder instanceof VertexBuilderWrapper ? ((VertexBuilderWrapper)ivertexbuilder).getVertexBuilder() : ivertexbuilder;
                            this.renderModel(modelIn, itemStackIn, LightTexture.MAX_BRIGHTNESS, combinedOverlayIn, matrixStackIn, ivertexbuilder1);
                            EmissiveTextures.endRenderEmissive();
                        }

                        EmissiveTextures.endRender();
                    }
                }
            }
            else if (Reflector.IForgeItem_getItemStackTileEntityRenderer.exists())
            {
                ItemStackTileEntityRenderer itemstacktileentityrenderer = (ItemStackTileEntityRenderer)Reflector.call(itemStackIn.getItem(), Reflector.IForgeItem_getItemStackTileEntityRenderer);
                itemstacktileentityrenderer.func_239207_a_(itemStackIn, transformTypeIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }
            else
            {
                ItemStackTileEntityRenderer.instance.func_239207_a_(itemStackIn, transformTypeIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }

            matrixStackIn.pop();
        }
    }

    public static IVertexBuilder getArmorVertexBuilder(IRenderTypeBuffer buffer, RenderType renderType, boolean noEntity, boolean withGlint)
    {
        if (Shaders.isShadowPass)
        {
            withGlint = false;
        }

        if (EmissiveTextures.isRenderEmissive())
        {
            withGlint = false;
        }

        return withGlint ? VertexBuilderUtils.newDelegate(buffer.getBuffer(noEntity ? RenderType.getArmorGlint() : RenderType.getArmorEntityGlint()), buffer.getBuffer(renderType)) : buffer.getBuffer(renderType);
    }

    public static IVertexBuilder getGlintVertexBuilder(IRenderTypeBuffer buffer, RenderType renderType, MatrixStack.Entry matrixEntry)
    {
        return VertexBuilderUtils.newDelegate(new MatrixApplyingVertexBuilder(buffer.getBuffer(RenderType.getGlint()), matrixEntry.getMatrix(), matrixEntry.getNormal()), buffer.getBuffer(renderType));
    }

    public static IVertexBuilder getDirectGlintVertexBuilder(IRenderTypeBuffer buffer, RenderType renderType, MatrixStack.Entry matrixEntry)
    {
        return VertexBuilderUtils.newDelegate(new MatrixApplyingVertexBuilder(buffer.getBuffer(RenderType.getGlintDirect()), matrixEntry.getMatrix(), matrixEntry.getNormal()), buffer.getBuffer(renderType));
    }

    public static IVertexBuilder getBuffer(IRenderTypeBuffer bufferIn, RenderType renderTypeIn, boolean isItemIn, boolean glintIn)
    {
        if (Shaders.isShadowPass)
        {
            glintIn = false;
        }

        if (EmissiveTextures.isRenderEmissive())
        {
            glintIn = false;
        }

        if (!glintIn)
        {
            return bufferIn.getBuffer(renderTypeIn);
        }
        else
        {
            return Minecraft.isFabulousGraphicsEnabled() && renderTypeIn == Atlases.getItemEntityTranslucentCullType() ? VertexBuilderUtils.newDelegate(bufferIn.getBuffer(RenderType.getGlintTranslucent()), bufferIn.getBuffer(renderTypeIn)) : VertexBuilderUtils.newDelegate(bufferIn.getBuffer(isItemIn ? RenderType.getGlint() : RenderType.getEntityGlint()), bufferIn.getBuffer(renderTypeIn));
        }
    }

    public static IVertexBuilder getEntityGlintVertexBuilder(IRenderTypeBuffer buffer, RenderType renderType, boolean noEntity, boolean withGlint)
    {
        if (Shaders.isShadowPass)
        {
            withGlint = false;
        }

        if (EmissiveTextures.isRenderEmissive())
        {
            withGlint = false;
        }

        return withGlint ? VertexBuilderUtils.newDelegate(buffer.getBuffer(noEntity ? RenderType.getGlintDirect() : RenderType.getEntityGlintDirect()), buffer.getBuffer(renderType)) : buffer.getBuffer(renderType);
    }

    private void renderQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn)
    {
        boolean flag = !itemStackIn.isEmpty();
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        boolean flag1 = EmissiveTextures.isActive();
        int i = quadsIn.size();

        for (int j = 0; j < i; ++j)
        {
            BakedQuad bakedquad = quadsIn.get(j);

            if (flag1)
            {
                bakedquad = EmissiveTextures.getEmissiveQuad(bakedquad);

                if (bakedquad == null)
                {
                    continue;
                }
            }

            int k = -1;

            if (flag && bakedquad.hasTintIndex())
            {
                k = this.itemColors.getColor(itemStackIn, bakedquad.getTintIndex());

                if (Config.isCustomColors())
                {
                    k = CustomColors.getColorFromItemStack(itemStackIn, bakedquad.getTintIndex(), k);
                }
            }

            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;

            if (Reflector.ForgeHooksClient.exists())
            {
                bufferIn.addVertexData(matrixstack$entry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn, true);
            }
            else
            {
                bufferIn.addQuad(matrixstack$entry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
            }
        }
    }

    public IBakedModel getItemModelWithOverrides(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entitylivingbaseIn)
    {
        Item item = stack.getItem();
        IBakedModel ibakedmodel;

        if (item == Items.TRIDENT)
        {
            ibakedmodel = this.itemModelMesher.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
        }
        else
        {
            ibakedmodel = this.itemModelMesher.getItemModel(stack);
        }

        ClientWorld clientworld = worldIn instanceof ClientWorld ? (ClientWorld)worldIn : null;
        ItemOverrideList.lastModelLocation = null;
        IBakedModel ibakedmodel1 = ibakedmodel.getOverrides().getOverrideModel(ibakedmodel, stack, clientworld, entitylivingbaseIn);

        if (Config.isCustomItems())
        {
            ibakedmodel1 = CustomItems.getCustomItemModel(stack, ibakedmodel1, ItemOverrideList.lastModelLocation, true);
        }

        return ibakedmodel1 == null ? this.itemModelMesher.getModelManager().getMissingModel() : ibakedmodel1;
    }

    public void renderItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn)
    {
        this.renderItem((LivingEntity)null, itemStackIn, transformTypeIn, false, matrixStackIn, bufferIn, (World)null, combinedLightIn, combinedOverlayIn);
    }

    public void renderItem(@Nullable LivingEntity livingEntityIn, ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, @Nullable World worldIn, int combinedLightIn, int combinedOverlayIn)
    {
        if (!itemStackIn.isEmpty())
        {
            IBakedModel ibakedmodel = this.getItemModelWithOverrides(itemStackIn, worldIn, livingEntityIn);
            this.renderItem(itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
        }
    }

    public void renderItemIntoGUI(ItemStack stack, double x, double y)
    {
        this.renderItemModelIntoGUI(stack, x, y, this.getItemModelWithOverrides(stack, (World)null, (LivingEntity)null));
    }

    protected void renderItemModelIntoGUI(ItemStack stack, double x, double y, IBakedModel bakedmodel)
    {
        renderItemGui = true;
        RenderSystem.pushMatrix();
        this.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        this.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 100.0F + this.zLevel);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        boolean flag = !bakedmodel.isSideLit();

        if (flag)
        {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }

        this.renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.finish();
        RenderSystem.enableDepthTest();

        if (flag)
        {
            RenderHelper.setupGui3DDiffuseLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        renderItemGui = false;
    }

    public void renderItemAndEffectIntoGUI(ItemStack stack, int xPosition, int yPosition)
    {
        this.renderItemIntoGUI(Minecraft.getInstance().player, stack, xPosition, yPosition);
    }

    public void renderItemAndEffectIntoGuiWithoutEntity(ItemStack stack, int x, int y)
    {
        this.renderItemIntoGUI((LivingEntity)null, stack, x, y);
    }

    public void renderItemAndEffectIntoGUI(LivingEntity entityIn, ItemStack itemIn, int x, int y)
    {
        this.renderItemIntoGUI(entityIn, itemIn, x, y);
    }

    private void renderItemIntoGUI(@Nullable LivingEntity livingEntity, ItemStack stack, int x, int y)
    {
        if (!stack.isEmpty())
        {
            this.zLevel += 50.0F;

            try
            {
                this.renderItemModelIntoGUI(stack, x, y, this.getItemModelWithOverrides(stack, (World)null, livingEntity));
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
                crashreportcategory.addDetail("Item Type", () ->
                {
                    return String.valueOf((Object)stack.getItem());
                });
                crashreportcategory.addDetail("Registry Name", () ->
                {
                    return String.valueOf(Reflector.call(stack.getItem(), Reflector.ForgeRegistryEntry_getRegistryName));
                });
                crashreportcategory.addDetail("Item Damage", () ->
                {
                    return String.valueOf(stack.getDamage());
                });
                crashreportcategory.addDetail("Item NBT", () ->
                {
                    return String.valueOf((Object)stack.getTag());
                });
                crashreportcategory.addDetail("Item Foil", () ->
                {
                    return String.valueOf(stack.hasEffect());
                });
                throw new ReportedException(crashreport);
            }

            this.zLevel -= 50.0F;
        }
    }

    public void renderItemOverlays(FontRenderer fr, ItemStack stack, double xPosition, double yPosition)
    {
        this.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, (String)null);
    }

    /**
     * Renders the stack size and/or damage bar for the given ItemStack.
     */
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, double xPosition, double yPosition, @Nullable String text)
    {
        if (!stack.isEmpty())
        {
            MatrixStack matrixstack = new MatrixStack();

            if (stack.getCount() != 1 || text != null)
            {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                matrixstack.translate(0.0D, 0.0D, (double)(this.zLevel + 200.0F));
                IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
                fr.renderString(s, (float)(xPosition + 19 - 2 - fr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215, true, matrixstack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
                irendertypebuffer$impl.finish();
            }

            if (ReflectorForge.isItemDamaged(stack))
            {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                float f = (float)stack.getDamage();
                float f1 = (float)stack.getMaxDamage();
                float f2 = Math.max(0.0F, (f1 - f) / f1);
                int i = Math.round(13.0F - f * 13.0F / f1);
                int j = MathHelper.hsvToRGB(f2 / 3.0F, 1.0F, 1.0F);

                if (Reflector.IForgeItem_getDurabilityForDisplay.exists() && Reflector.IForgeItem_getRGBDurabilityForDisplay.exists())
                {
                    double d0 = Reflector.callDouble(stack.getItem(), Reflector.IForgeItem_getDurabilityForDisplay, stack);
                    int k = Reflector.callInt(stack.getItem(), Reflector.IForgeItem_getRGBDurabilityForDisplay, stack);
                    i = Math.round(13.0F - (float)d0 * 13.0F);
                    j = k;
                }

                if (Config.isCustomColors())
                {
                    j = CustomColors.getDurabilityColor(f2, j);
                }

                this.draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                this.draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
            float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getInstance().getRenderPartialTicks());

            if (f3 > 0.0F)
            {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                this.draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    /**
     * Draw with the WorldRenderer
     */
    private void draw(BufferBuilder renderer, double x, double y, double width, double height, int red, int green, int blue, int alpha)
    {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.itemModelMesher.rebuildCache();
    }

    public IResourceType getResourceType()
    {
        return VanillaResourceType.MODELS;
    }
}
