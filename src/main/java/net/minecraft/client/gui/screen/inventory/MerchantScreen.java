package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MerchantScreen extends ContainerScreen<MerchantContainer>
{
    /** The GUI texture for the villager merchant GUI. */
    private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager2.png");
    private static final ITextComponent field_243351_B = new TranslationTextComponent("merchant.trades");
    private static final ITextComponent field_243352_C = new StringTextComponent(" - ");
    private static final ITextComponent field_243353_D = new TranslationTextComponent("merchant.deprecated");

    /**
     * The integer value corresponding to the currently selected merchant recipe.
     */
    private int selectedMerchantRecipe;
    private final MerchantScreen.TradeButton[] field_214138_m = new MerchantScreen.TradeButton[7];
    private int field_214139_n;
    private boolean field_214140_o;

    public MerchantScreen(MerchantContainer p_i51080_1_, PlayerInventory p_i51080_2_, ITextComponent p_i51080_3_)
    {
        super(p_i51080_1_, p_i51080_2_, p_i51080_3_);
        this.xSize = 276;
        this.playerInventoryTitleX = 107;
    }

    private void func_195391_j()
    {
        this.container.setCurrentRecipeIndex(this.selectedMerchantRecipe);
        this.container.func_217046_g(this.selectedMerchantRecipe);
        this.mc.getConnection().sendPacket(new CSelectTradePacket(this.selectedMerchantRecipe));
    }

    protected void init()
    {
        super.init();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        int k = j + 16 + 2;

        for (int l = 0; l < 7; ++l)
        {
            this.field_214138_m[l] = this.addButton(new MerchantScreen.TradeButton(i + 5, k, l, (p_214132_1_) ->
            {
                if (p_214132_1_ instanceof MerchantScreen.TradeButton)
                {
                    this.selectedMerchantRecipe = ((MerchantScreen.TradeButton)p_214132_1_).func_212937_a() + this.field_214139_n;
                    this.func_195391_j();
                }
            }));
            k += 20;
        }
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y)
    {
        int i = this.container.getMerchantLevel();

        if (i > 0 && i <= 5 && this.container.func_217042_i())
        {
            ITextComponent itextcomponent = this.title.deepCopy().append(field_243352_C).append(new TranslationTextComponent("merchant.level." + i));
            int j = this.font.getStringPropertyWidth(itextcomponent);
            int k = 49 + this.xSize / 2 - j / 2;
            this.font.func_243248_b(matrixStack, itextcomponent, (float)k, 6.0F, 4210752);
        }
        else
        {
            this.font.func_243248_b(matrixStack, this.title, (float)(49 + this.xSize / 2 - this.font.getStringPropertyWidth(this.title) / 2), 6.0F, 4210752);
        }

        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 4210752);
        int l = this.font.getStringPropertyWidth(field_243351_B);
        this.font.func_243248_b(matrixStack, field_243351_B, (float)(5 - l / 2 + 48), 6.0F, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        blit(matrixStack, i, j, this.getBlitOffset(), 0.0F, 0.0F, this.xSize, this.ySize, 256, 512);
        MerchantOffers merchantoffers = this.container.getOffers();

        if (!merchantoffers.isEmpty())
        {
            int k = this.selectedMerchantRecipe;

            if (k < 0 || k >= merchantoffers.size())
            {
                return;
            }

            MerchantOffer merchantoffer = merchantoffers.get(k);

            if (merchantoffer.hasNoUsesLeft())
            {
                this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                blit(matrixStack, this.guiLeft + 83 + 99, this.guiTop + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 256, 512);
            }
        }
    }

    private void func_238839_a_(MatrixStack p_238839_1_, int p_238839_2_, int p_238839_3_, MerchantOffer p_238839_4_)
    {
        this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
        int i = this.container.getMerchantLevel();
        int j = this.container.getXp();

        if (i < 5)
        {
            blit(p_238839_1_, p_238839_2_ + 136, p_238839_3_ + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 256, 512);
            int k = VillagerData.getExperiencePrevious(i);

            if (j >= k && VillagerData.canLevelUp(i))
            {
                int l = 100;
                float f = 100.0F / (float)(VillagerData.getExperienceNext(i) - k);
                int i1 = Math.min(MathHelper.floor(f * (float)(j - k)), 100);
                blit(p_238839_1_, p_238839_2_ + 136, p_238839_3_ + 16, this.getBlitOffset(), 0.0F, 191.0F, i1 + 1, 5, 256, 512);
                int j1 = this.container.getPendingExp();

                if (j1 > 0)
                {
                    int k1 = Math.min(MathHelper.floor((float)j1 * f), 100 - i1);
                    blit(p_238839_1_, p_238839_2_ + 136 + i1 + 1, p_238839_3_ + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, k1, 3, 256, 512);
                }
            }
        }
    }

    private void func_238840_a_(MatrixStack p_238840_1_, int p_238840_2_, int p_238840_3_, MerchantOffers p_238840_4_)
    {
        int i = p_238840_4_.size() + 1 - 7;

        if (i > 1)
        {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int i1 = Math.min(113, this.field_214139_n * k);

            if (this.field_214139_n == i - 1)
            {
                i1 = 113;
            }

            blit(p_238840_1_, p_238840_2_ + 94, p_238840_3_ + 18 + i1, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 256, 512);
        }
        else
        {
            blit(p_238840_1_, p_238840_2_ + 94, p_238840_3_ + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 256, 512);
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        MerchantOffers merchantoffers = this.container.getOffers();

        if (!merchantoffers.isEmpty())
        {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            RenderSystem.pushMatrix();
            RenderSystem.enableRescaleNormal();
            this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            this.func_238840_a_(matrixStack, i, j, merchantoffers);
            int i1 = 0;

            for (MerchantOffer merchantoffer : merchantoffers)
            {
                if (this.func_214135_a(merchantoffers.size()) && (i1 < this.field_214139_n || i1 >= 7 + this.field_214139_n))
                {
                    ++i1;
                }
                else
                {
                    ItemStack itemstack = merchantoffer.getBuyingStackFirst();
                    ItemStack itemstack1 = merchantoffer.getDiscountedBuyingStackFirst();
                    ItemStack itemstack2 = merchantoffer.getBuyingStackSecond();
                    ItemStack itemstack3 = merchantoffer.getSellingStack();
                    this.itemRenderer.zLevel = 100.0F;
                    int j1 = k + 2;
                    this.func_238841_a_(matrixStack, itemstack1, itemstack, l, j1);

                    if (!itemstack2.isEmpty())
                    {
                        this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(itemstack2, i + 5 + 35, j1);
                        this.itemRenderer.renderItemOverlays(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    this.func_238842_a_(matrixStack, merchantoffer, i, j1);
                    this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(itemstack3, i + 5 + 68, j1);
                    this.itemRenderer.renderItemOverlays(this.font, itemstack3, i + 5 + 68, j1);
                    this.itemRenderer.zLevel = 0.0F;
                    k += 20;
                    ++i1;
                }
            }

            int k1 = this.selectedMerchantRecipe;
            MerchantOffer merchantoffer1 = merchantoffers.get(k1);

            if (this.container.func_217042_i())
            {
                this.func_238839_a_(matrixStack, i, j, merchantoffer1);
            }

            if (merchantoffer1.hasNoUsesLeft() && this.isPointInRegion(186, 35, 22, 21, (double)mouseX, (double)mouseY) && this.container.func_223432_h())
            {
                this.renderTooltip(matrixStack, field_243353_D, mouseX, mouseY);
            }

            for (MerchantScreen.TradeButton merchantscreen$tradebutton : this.field_214138_m)
            {
                if (merchantscreen$tradebutton.isHovered())
                {
                    merchantscreen$tradebutton.renderToolTip(matrixStack, mouseX, mouseY);
                }

                merchantscreen$tradebutton.visible = merchantscreen$tradebutton.field_212938_a < this.container.getOffers().size();
            }

            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
        }

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    private void func_238842_a_(MatrixStack p_238842_1_, MerchantOffer p_238842_2_, int p_238842_3_, int p_238842_4_)
    {
        RenderSystem.enableBlend();
        this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);

        if (p_238842_2_.hasNoUsesLeft())
        {
            blit(p_238842_1_, p_238842_3_ + 5 + 35 + 20, p_238842_4_ + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 256, 512);
        }
        else
        {
            blit(p_238842_1_, p_238842_3_ + 5 + 35 + 20, p_238842_4_ + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 256, 512);
        }
    }

    private void func_238841_a_(MatrixStack p_238841_1_, ItemStack p_238841_2_, ItemStack p_238841_3_, int p_238841_4_, int p_238841_5_)
    {
        this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(p_238841_2_, p_238841_4_, p_238841_5_);

        if (p_238841_3_.getCount() == p_238841_2_.getCount())
        {
            this.itemRenderer.renderItemOverlays(this.font, p_238841_2_, p_238841_4_, p_238841_5_);
        }
        else
        {
            this.itemRenderer.renderItemOverlayIntoGUI(this.font, p_238841_3_, p_238841_4_, p_238841_5_, p_238841_3_.getCount() == 1 ? "1" : null);
            this.itemRenderer.renderItemOverlayIntoGUI(this.font, p_238841_2_, p_238841_4_ + 14, p_238841_5_, p_238841_2_.getCount() == 1 ? "1" : null);
            this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            this.setBlitOffset(this.getBlitOffset() + 300);
            blit(p_238841_1_, p_238841_4_ + 7, p_238841_5_ + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 256, 512);
            this.setBlitOffset(this.getBlitOffset() - 300);
        }
    }

    private boolean func_214135_a(int p_214135_1_)
    {
        return p_214135_1_ > 7;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        int i = this.container.getOffers().size();

        if (this.func_214135_a(i))
        {
            int j = i - 7;
            this.field_214139_n = (int)((double)this.field_214139_n - delta);
            this.field_214139_n = MathHelper.clamp(this.field_214139_n, 0, j);
        }

        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        int i = this.container.getOffers().size();

        if (this.field_214140_o)
        {
            int j = this.guiTop + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.field_214139_n = MathHelper.clamp((int)f, 0, l);
            return true;
        }
        else
        {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        this.field_214140_o = false;
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        if (this.func_214135_a(this.container.getOffers().size()) && mouseX > (double)(i + 94) && mouseX < (double)(i + 94 + 6) && mouseY > (double)(j + 18) && mouseY <= (double)(j + 18 + 139 + 1))
        {
            this.field_214140_o = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    class TradeButton extends Button
    {
        final int field_212938_a;

        public TradeButton(int p_i50601_2_, int p_i50601_3_, int p_i50601_4_, Button.IPressable p_i50601_5_)
        {
            super(p_i50601_2_, p_i50601_3_, 89, 20, StringTextComponent.EMPTY, p_i50601_5_);
            this.field_212938_a = p_i50601_4_;
            this.visible = false;
        }

        public int func_212937_a()
        {
            return this.field_212938_a;
        }

        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY)
        {
            if (this.isHovered && MerchantScreen.this.container.getOffers().size() > this.field_212938_a + MerchantScreen.this.field_214139_n)
            {
                if (mouseX < this.x + 20)
                {
                    ItemStack itemstack = MerchantScreen.this.container.getOffers().get(this.field_212938_a + MerchantScreen.this.field_214139_n).getDiscountedBuyingStackFirst();
                    MerchantScreen.this.renderTooltip(matrixStack, itemstack, mouseX, mouseY);
                }
                else if (mouseX < this.x + 50 && mouseX > this.x + 30)
                {
                    ItemStack itemstack2 = MerchantScreen.this.container.getOffers().get(this.field_212938_a + MerchantScreen.this.field_214139_n).getBuyingStackSecond();

                    if (!itemstack2.isEmpty())
                    {
                        MerchantScreen.this.renderTooltip(matrixStack, itemstack2, mouseX, mouseY);
                    }
                }
                else if (mouseX > this.x + 65)
                {
                    ItemStack itemstack1 = MerchantScreen.this.container.getOffers().get(this.field_212938_a + MerchantScreen.this.field_214139_n).getSellingStack();
                    MerchantScreen.this.renderTooltip(matrixStack, itemstack1, mouseX, mouseY);
                }
            }
        }
    }
}
