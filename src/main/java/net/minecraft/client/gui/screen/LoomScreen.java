package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class LoomScreen extends ContainerScreen<LoomContainer>
{
    private static final ResourceLocation LOOM_GUI_TEXTURES = new ResourceLocation("textures/gui/container/loom.png");
    private static final int field_214114_l = (BannerPattern.BANNER_PATTERNS_COUNT - BannerPattern.BANNERS_WITH_ITEMS - 1 + 4 - 1) / 4;
    private final ModelRenderer modelRender;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> field_230155_n_;
    private ItemStack bannerStack = ItemStack.EMPTY;
    private ItemStack dyeStack = ItemStack.EMPTY;
    private ItemStack patternStack = ItemStack.EMPTY;
    private boolean displayPatternsIn;
    private boolean field_214124_v;
    private boolean field_214125_w;
    private float field_214126_x;
    private boolean isScrolling;
    private int indexStarting = 1;

    public LoomScreen(LoomContainer container, PlayerInventory playerInventory, ITextComponent textComponent)
    {
        super(container, playerInventory, textComponent);
        this.modelRender = BannerTileEntityRenderer.getModelRender();
        container.func_217020_a(this::containerChange);
        this.titleY -= 2;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        this.renderBackground(matrixStack);
        this.mc.getTextureManager().bindTexture(LOOM_GUI_TEXTURES);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        Slot slot = this.container.getBannerSlot();
        Slot slot1 = this.container.getDyeSlot();
        Slot slot2 = this.container.getPatternSlot();
        Slot slot3 = this.container.getOutputSlot();

        if (!slot.getHasStack())
        {
            this.blit(matrixStack, i + slot.xPos, j + slot.yPos, this.xSize, 0, 16, 16);
        }

        if (!slot1.getHasStack())
        {
            this.blit(matrixStack, i + slot1.xPos, j + slot1.yPos, this.xSize + 16, 0, 16, 16);
        }

        if (!slot2.getHasStack())
        {
            this.blit(matrixStack, i + slot2.xPos, j + slot2.yPos, this.xSize + 32, 0, 16, 16);
        }

        int k = (int)(41.0F * this.field_214126_x);
        this.blit(matrixStack, i + 119, j + 13 + k, 232 + (this.displayPatternsIn ? 0 : 12), 0, 12, 15);
        RenderHelper.setupGuiFlatDiffuseLighting();

        if (this.field_230155_n_ != null && !this.field_214125_w)
        {
            IRenderTypeBuffer.Impl irendertypebuffer$impl = this.mc.getRenderTypeBuffers().getBufferSource();
            matrixStack.push();
            matrixStack.translate((double)(i + 139), (double)(j + 52), 0.0D);
            matrixStack.scale(24.0F, -24.0F, 1.0F);
            matrixStack.translate(0.5D, 0.5D, 0.5D);
            float f = 0.6666667F;
            matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
            this.modelRender.rotateAngleX = 0.0F;
            this.modelRender.rotationPointY = -32.0F;
            BannerTileEntityRenderer.func_230180_a_(matrixStack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, this.modelRender, ModelBakery.LOCATION_BANNER_BASE, true, this.field_230155_n_);
            matrixStack.pop();
            irendertypebuffer$impl.finish();
        }
        else if (this.field_214125_w)
        {
            this.blit(matrixStack, i + slot3.xPos - 2, j + slot3.yPos - 2, this.xSize, 17, 17, 16);
        }

        if (this.displayPatternsIn)
        {
            int j2 = i + 60;
            int l2 = j + 13;
            int l = this.indexStarting + 16;

            for (int i1 = this.indexStarting; i1 < l && i1 < BannerPattern.BANNER_PATTERNS_COUNT - BannerPattern.BANNERS_WITH_ITEMS; ++i1)
            {
                int j1 = i1 - this.indexStarting;
                int k1 = j2 + j1 % 4 * 14;
                int l1 = l2 + j1 / 4 * 14;
                this.mc.getTextureManager().bindTexture(LOOM_GUI_TEXTURES);
                int i2 = this.ySize;

                if (i1 == this.container.func_217023_e())
                {
                    i2 += 14;
                }
                else if (x >= k1 && y >= l1 && x < k1 + 14 && y < l1 + 14)
                {
                    i2 += 28;
                }

                this.blit(matrixStack, k1, l1, 0, i2, 14, 14);
                this.func_228190_b_(i1, k1, l1);
            }
        }
        else if (this.field_214124_v)
        {
            int k2 = i + 60;
            int i3 = j + 13;
            this.mc.getTextureManager().bindTexture(LOOM_GUI_TEXTURES);
            this.blit(matrixStack, k2, i3, 0, this.ySize, 14, 14);
            int j3 = this.container.func_217023_e();
            this.func_228190_b_(j3, k2, i3);
        }

        RenderHelper.setupGui3DDiffuseLighting();
    }

    private void func_228190_b_(int p_228190_1_, int p_228190_2_, int p_228190_3_)
    {
        ItemStack itemstack = new ItemStack(Items.GRAY_BANNER);
        CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("BlockEntityTag");
        ListNBT listnbt = (new BannerPattern.Builder()).setPatternWithColor(BannerPattern.BASE, DyeColor.GRAY).setPatternWithColor(BannerPattern.values()[p_228190_1_], DyeColor.WHITE).buildNBT();
        compoundnbt.put("Patterns", listnbt);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.push();
        matrixstack.translate((double)((float)p_228190_2_ + 0.5F), (double)(p_228190_3_ + 16), 0.0D);
        matrixstack.scale(6.0F, -6.0F, 1.0F);
        matrixstack.translate(0.5D, 0.5D, 0.0D);
        matrixstack.translate(0.5D, 0.5D, 0.5D);
        float f = 0.6666667F;
        matrixstack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = this.mc.getRenderTypeBuffers().getBufferSource();
        this.modelRender.rotateAngleX = 0.0F;
        this.modelRender.rotationPointY = -32.0F;
        List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.getPatternColorData(DyeColor.GRAY, BannerTileEntity.getPatternData(itemstack));
        BannerTileEntityRenderer.func_230180_a_(matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, this.modelRender, ModelBakery.LOCATION_BANNER_BASE, true, list);
        matrixstack.pop();
        irendertypebuffer$impl.finish();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        this.isScrolling = false;

        if (this.displayPatternsIn)
        {
            int i = this.guiLeft + 60;
            int j = this.guiTop + 13;
            int k = this.indexStarting + 16;

            for (int l = this.indexStarting; l < k; ++l)
            {
                int i1 = l - this.indexStarting;
                double d0 = mouseX - (double)(i + i1 % 4 * 14);
                double d1 = mouseY - (double)(j + i1 / 4 * 14);

                if (d0 >= 0.0D && d1 >= 0.0D && d0 < 14.0D && d1 < 14.0D && this.container.enchantItem(this.mc.player, l))
                {
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                    this.mc.playerController.sendEnchantPacket((this.container).windowId, l);
                    return true;
                }
            }

            i = this.guiLeft + 119;
            j = this.guiTop + 9;

            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 56))
            {
                this.isScrolling = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (this.isScrolling && this.displayPatternsIn)
        {
            int i = this.guiTop + 13;
            int j = i + 56;
            this.field_214126_x = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.field_214126_x = MathHelper.clamp(this.field_214126_x, 0.0F, 1.0F);
            int k = field_214114_l - 4;
            int l = (int)((double)(this.field_214126_x * (float)k) + 0.5D);

            if (l < 0)
            {
                l = 0;
            }

            this.indexStarting = 1 + l * 4;
            return true;
        }
        else
        {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (this.displayPatternsIn)
        {
            int i = field_214114_l - 4;
            this.field_214126_x = (float)((double)this.field_214126_x - delta / (double)i);
            this.field_214126_x = MathHelper.clamp(this.field_214126_x, 0.0F, 1.0F);
            this.indexStarting = 1 + (int)((double)(this.field_214126_x * (float)i) + 0.5D) * 4;
        }

        return true;
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton)
    {
        return mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
    }

    private void containerChange()
    {
        ItemStack itemstack = this.container.getOutputSlot().getStack();

        if (itemstack.isEmpty())
        {
            this.field_230155_n_ = null;
        }
        else
        {
            this.field_230155_n_ = BannerTileEntity.getPatternColorData(((BannerItem)itemstack.getItem()).getColor(), BannerTileEntity.getPatternData(itemstack));
        }

        ItemStack itemstack1 = this.container.getBannerSlot().getStack();
        ItemStack itemstack2 = this.container.getDyeSlot().getStack();
        ItemStack itemstack3 = this.container.getPatternSlot().getStack();
        CompoundNBT compoundnbt = itemstack1.getOrCreateChildTag("BlockEntityTag");
        this.field_214125_w = compoundnbt.contains("Patterns", 9) && !itemstack1.isEmpty() && compoundnbt.getList("Patterns", 10).size() >= 6;

        if (this.field_214125_w)
        {
            this.field_230155_n_ = null;
        }

        if (!ItemStack.areItemStacksEqual(itemstack1, this.bannerStack) || !ItemStack.areItemStacksEqual(itemstack2, this.dyeStack) || !ItemStack.areItemStacksEqual(itemstack3, this.patternStack))
        {
            this.displayPatternsIn = !itemstack1.isEmpty() && !itemstack2.isEmpty() && itemstack3.isEmpty() && !this.field_214125_w;
            this.field_214124_v = !this.field_214125_w && !itemstack3.isEmpty() && !itemstack1.isEmpty() && !itemstack2.isEmpty();
        }

        this.bannerStack = itemstack1.copy();
        this.dyeStack = itemstack2.copy();
        this.patternStack = itemstack3.copy();
    }
}
