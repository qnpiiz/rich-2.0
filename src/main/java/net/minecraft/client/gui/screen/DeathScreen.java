package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import fun.rich.utils.other.ChatUtils;

public class DeathScreen extends Screen
{
    /**
     * The integer value containing the number of ticks that have passed since the player's death
     */
    private int enableButtonsTimer;
    private final ITextComponent causeOfDeath;
    private final boolean isHardcoreMode;
    private ITextComponent field_243285_p;

    public DeathScreen(@Nullable ITextComponent textComponent, boolean isHardcoreMode)
    {
        super(new TranslationTextComponent(isHardcoreMode ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.causeOfDeath = textComponent;
        this.isHardcoreMode = isHardcoreMode;
    }

    protected void init()
    {
        int x = mc.player.getPosition().getX();
        int y = mc.player.getPosition().getY();
        int z = mc.player.getPosition().getZ();
        ChatUtils.addChatMessage("Death Coordinates: X: " + x + " Y: " + y + " Z: " + z);

        this.enableButtonsTimer = 0;
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.isHardcoreMode ? new TranslationTextComponent("deathScreen.spectate") : new TranslationTextComponent("deathScreen.respawn"), (p_213021_1_) ->
        {
            this.mc.player.respawnPlayer();
            this.mc.displayGuiScreen((Screen)null);
        }));
        Button button = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96, 200, 20, new TranslationTextComponent("deathScreen.titleScreen"), (p_213020_1_) ->
        {
            if (this.isHardcoreMode)
            {
                this.func_228177_a_();
            }
            else {
                ConfirmScreen confirmscreen = new ConfirmScreen(this::confirmCallback, new TranslationTextComponent("deathScreen.quit.confirm"), StringTextComponent.EMPTY, new TranslationTextComponent("deathScreen.titleScreen"), new TranslationTextComponent("deathScreen.respawn"));
                this.mc.displayGuiScreen(confirmscreen);
                confirmscreen.setButtonDelay(20);
            }
        }));

        if (!this.isHardcoreMode && this.mc.getSession() == null)
        {
            button.active = false;
        }

        for (Widget widget : this.buttons)
        {
            widget.active = false;
        }

        this.field_243285_p = (new TranslationTextComponent("deathScreen.score")).appendString(": ").append((new StringTextComponent(Integer.toString(this.mc.player.getScore()))).mergeStyle(TextFormatting.YELLOW));
    }

    public boolean shouldCloseOnEsc()
    {
        return false;
    }

    private void confirmCallback(boolean p_213022_1_)
    {
        if (p_213022_1_)
        {
            this.func_228177_a_();
        }
        else
        {
            this.mc.player.respawnPlayer();
            this.mc.displayGuiScreen((Screen)null);
        }
    }

    private void func_228177_a_()
    {
        if (this.mc.world != null)
        {
            this.mc.world.sendQuittingDisconnectingPacket();
        }

        this.mc.unloadWorld(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
        this.mc.displayGuiScreen(new MainMenuScreen());
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, 1615855616, -1602211792);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2 / 2, 30, 16777215);
        RenderSystem.popMatrix();

        if (this.causeOfDeath != null)
        {
            drawCenteredString(matrixStack, this.font, this.causeOfDeath, this.width / 2, 85, 16777215);
        }

        drawCenteredString(matrixStack, this.font, this.field_243285_p, this.width / 2, 100, 16777215);

        if (this.causeOfDeath != null && mouseY > 85 && mouseY < 85 + 9)
        {
            Style style = this.func_238623_a_(mouseX);
            this.renderComponentHoverEffect(matrixStack, style, mouseX, mouseY);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Nullable
    private Style func_238623_a_(int p_238623_1_)
    {
        if (this.causeOfDeath == null)
        {
            return null;
        }
        else
        {
            int i = this.mc.fontRenderer.getStringPropertyWidth(this.causeOfDeath);
            int j = this.width / 2 - i / 2;
            int k = this.width / 2 + i / 2;
            return p_238623_1_ >= j && p_238623_1_ <= k ? this.mc.fontRenderer.getCharacterManager().func_238357_a_(this.causeOfDeath, p_238623_1_ - j) : null;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.causeOfDeath != null && mouseY > 85.0D && mouseY < (double)(85 + 9))
        {
            Style style = this.func_238623_a_((int)mouseX);

            if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL)
            {
                this.handleComponentClicked(style);
                return false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean isPauseScreen()
    {
        return false;
    }

    public void tick()
    {
        super.tick();
        ++this.enableButtonsTimer;

        if (this.enableButtonsTimer == 20)
        {
            for (Widget widget : this.buttons)
            {
                widget.active = true;
            }
        }
    }
}
