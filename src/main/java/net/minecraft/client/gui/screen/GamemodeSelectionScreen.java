package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;

public class GamemodeSelectionScreen extends Screen
{
    private static final ResourceLocation field_238703_a_ = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
    private static final int field_238704_b_ = GamemodeSelectionScreen.Mode.values().length * 30 - 5;
    private static final ITextComponent field_238705_c_ = new TranslationTextComponent("debug.gamemodes.select_next", (new TranslationTextComponent("debug.gamemodes.press_f4")).mergeStyle(TextFormatting.AQUA));
    private final Optional<GamemodeSelectionScreen.Mode> field_238706_p_;
    private Optional<GamemodeSelectionScreen.Mode> field_238707_q_ = Optional.empty();
    private int field_238708_r_;
    private int field_238709_s_;
    private boolean field_238710_t_;
    private final List<GamemodeSelectionScreen.SelectorWidget> field_238711_u_ = Lists.newArrayList();

    public GamemodeSelectionScreen()
    {
        super(NarratorChatListener.EMPTY);
        this.field_238706_p_ = GamemodeSelectionScreen.Mode.func_238731_b_(this.func_241608_k_());
    }

    private GameType func_241608_k_()
    {
        GameType gametype = Minecraft.getInstance().playerController.getCurrentGameType();
        GameType gametype1 = Minecraft.getInstance().playerController.func_241822_k();

        if (gametype1 == GameType.NOT_SET)
        {
            if (gametype == GameType.CREATIVE)
            {
                gametype1 = GameType.SURVIVAL;
            }
            else
            {
                gametype1 = GameType.CREATIVE;
            }
        }

        return gametype1;
    }

    protected void init()
    {
        super.init();
        this.field_238707_q_ = this.field_238706_p_.isPresent() ? this.field_238706_p_ : GamemodeSelectionScreen.Mode.func_238731_b_(this.mc.playerController.getCurrentGameType());

        for (int i = 0; i < GamemodeSelectionScreen.Mode.field_238721_e_.length; ++i)
        {
            GamemodeSelectionScreen.Mode gamemodeselectionscreen$mode = GamemodeSelectionScreen.Mode.field_238721_e_[i];
            this.field_238711_u_.add(new GamemodeSelectionScreen.SelectorWidget(gamemodeselectionscreen$mode, this.width / 2 - field_238704_b_ / 2 + i * 30, this.height / 2 - 30));
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (!this.func_238718_l_())
        {
            matrixStack.push();
            RenderSystem.enableBlend();
            this.mc.getTextureManager().bindTexture(field_238703_a_);
            int i = this.width / 2 - 62;
            int j = this.height / 2 - 30 - 27;
            blit(matrixStack, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
            matrixStack.pop();
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.field_238707_q_.ifPresent((p_238712_2_) ->
            {
                drawCenteredString(matrixStack, this.font, p_238712_2_.func_238725_a_(), this.width / 2, this.height / 2 - 30 - 20, -1);
            });
            drawCenteredString(matrixStack, this.font, field_238705_c_, this.width / 2, this.height / 2 + 5, 16777215);

            if (!this.field_238710_t_)
            {
                this.field_238708_r_ = mouseX;
                this.field_238709_s_ = mouseY;
                this.field_238710_t_ = true;
            }

            boolean flag = this.field_238708_r_ == mouseX && this.field_238709_s_ == mouseY;

            for (GamemodeSelectionScreen.SelectorWidget gamemodeselectionscreen$selectorwidget : this.field_238711_u_)
            {
                gamemodeselectionscreen$selectorwidget.render(matrixStack, mouseX, mouseY, partialTicks);
                this.field_238707_q_.ifPresent((p_238714_1_) ->
                {
                    gamemodeselectionscreen$selectorwidget.func_238741_e_(p_238714_1_ == gamemodeselectionscreen$selectorwidget.field_238736_b_);
                });

                if (!flag && gamemodeselectionscreen$selectorwidget.isHovered())
                {
                    this.field_238707_q_ = Optional.of(gamemodeselectionscreen$selectorwidget.field_238736_b_);
                }
            }
        }
    }

    private void func_238717_j_()
    {
        func_238713_a_(this.mc, this.field_238707_q_);
    }

    private static void func_238713_a_(Minecraft p_238713_0_, Optional<GamemodeSelectionScreen.Mode> p_238713_1_)
    {
        if (p_238713_0_.playerController != null && p_238713_0_.player != null && p_238713_1_.isPresent())
        {
            Optional<GamemodeSelectionScreen.Mode> optional = GamemodeSelectionScreen.Mode.func_238731_b_(p_238713_0_.playerController.getCurrentGameType());
            GamemodeSelectionScreen.Mode gamemodeselectionscreen$mode = p_238713_1_.get();

            if (optional.isPresent() && p_238713_0_.player.hasPermissionLevel(2) && gamemodeselectionscreen$mode != optional.get())
            {
                p_238713_0_.player.sendChatMessage(gamemodeselectionscreen$mode.func_238730_b_());
            }
        }
    }

    private boolean func_238718_l_()
    {
        if (!InputMappings.isKeyDown(this.mc.getMainWindow().getHandle(), 292))
        {
            this.func_238717_j_();
            this.mc.displayGuiScreen((Screen)null);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 293 && this.field_238707_q_.isPresent())
        {
            this.field_238710_t_ = false;
            this.field_238707_q_ = this.field_238707_q_.get().func_238733_c_();
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public boolean isPauseScreen()
    {
        return false;
    }

    static enum Mode
    {
        CREATIVE(new TranslationTextComponent("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
        SURVIVAL(new TranslationTextComponent("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
        ADVENTURE(new TranslationTextComponent("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
        SPECTATOR(new TranslationTextComponent("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

        protected static final GamemodeSelectionScreen.Mode[] field_238721_e_ = values();
        final ITextComponent field_238722_f_;
        final String field_238723_g_;
        final ItemStack field_238724_h_;

        private Mode(ITextComponent p_i232285_3_, String p_i232285_4_, ItemStack p_i232285_5_)
        {
            this.field_238722_f_ = p_i232285_3_;
            this.field_238723_g_ = p_i232285_4_;
            this.field_238724_h_ = p_i232285_5_;
        }

        private void func_238729_a_(ItemRenderer p_238729_1_, int p_238729_2_, int p_238729_3_)
        {
            p_238729_1_.renderItemAndEffectIntoGUI(this.field_238724_h_, p_238729_2_, p_238729_3_);
        }

        private ITextComponent func_238725_a_()
        {
            return this.field_238722_f_;
        }

        private String func_238730_b_()
        {
            return this.field_238723_g_;
        }

        private Optional<GamemodeSelectionScreen.Mode> func_238733_c_()
        {
            switch (this)
            {
                case CREATIVE:
                    return Optional.of(SURVIVAL);

                case SURVIVAL:
                    return Optional.of(ADVENTURE);

                case ADVENTURE:
                    return Optional.of(SPECTATOR);

                default:
                    return Optional.of(CREATIVE);
            }
        }

        private static Optional<GamemodeSelectionScreen.Mode> func_238731_b_(GameType p_238731_0_)
        {
            switch (p_238731_0_)
            {
                case SPECTATOR:
                    return Optional.of(SPECTATOR);

                case SURVIVAL:
                    return Optional.of(SURVIVAL);

                case CREATIVE:
                    return Optional.of(CREATIVE);

                case ADVENTURE:
                    return Optional.of(ADVENTURE);

                default:
                    return Optional.empty();
            }
        }
    }

    public class SelectorWidget extends Widget
    {
        private final GamemodeSelectionScreen.Mode field_238736_b_;
        private boolean field_238737_c_;

        public SelectorWidget(GamemodeSelectionScreen.Mode p_i232286_2_, int p_i232286_3_, int p_i232286_4_)
        {
            super(p_i232286_3_, p_i232286_4_, 25, 25, p_i232286_2_.func_238725_a_());
            this.field_238736_b_ = p_i232286_2_;
        }

        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            Minecraft minecraft = Minecraft.getInstance();
            this.func_238738_a_(matrixStack, minecraft.getTextureManager());
            this.field_238736_b_.func_238729_a_(GamemodeSelectionScreen.this.itemRenderer, this.x + 5, this.y + 5);

            if (this.field_238737_c_)
            {
                this.func_238740_b_(matrixStack, minecraft.getTextureManager());
            }
        }

        public boolean isHovered()
        {
            return super.isHovered() || this.field_238737_c_;
        }

        public void func_238741_e_(boolean p_238741_1_)
        {
            this.field_238737_c_ = p_238741_1_;
            this.narrate();
        }

        private void func_238738_a_(MatrixStack p_238738_1_, TextureManager p_238738_2_)
        {
            p_238738_2_.bindTexture(GamemodeSelectionScreen.field_238703_a_);
            p_238738_1_.push();
            p_238738_1_.translate((double)this.x, (double)this.y, 0.0D);
            blit(p_238738_1_, 0, 0, 0.0F, 75.0F, 25, 25, 128, 128);
            p_238738_1_.pop();
        }

        private void func_238740_b_(MatrixStack p_238740_1_, TextureManager p_238740_2_)
        {
            p_238740_2_.bindTexture(GamemodeSelectionScreen.field_238703_a_);
            p_238740_1_.push();
            p_238740_1_.translate((double)this.x, (double)this.y, 0.0D);
            blit(p_238740_1_, 0, 0, 25.0F, 75.0F, 25, 25, 128, 128);
            p_238740_1_.pop();
        }
    }
}
