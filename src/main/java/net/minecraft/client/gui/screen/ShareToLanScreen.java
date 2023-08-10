package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;

public class ShareToLanScreen extends Screen
{
    private static final ITextComponent field_243310_a = new TranslationTextComponent("selectWorld.allowCommands");
    private static final ITextComponent field_243311_b = new TranslationTextComponent("selectWorld.gameMode");
    private static final ITextComponent field_243312_c = new TranslationTextComponent("lanServer.otherPlayers");
    private final Screen lastScreen;
    private Button allowCheatsButton;
    private Button gameModeButton;
    private String gameMode = "survival";
    private boolean allowCheats;

    public ShareToLanScreen(Screen lastScreenIn)
    {
        super(new TranslationTextComponent("lanServer.title"));
        this.lastScreen = lastScreenIn;
    }

    protected void init()
    {
        this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslationTextComponent("lanServer.start"), (p_213082_1_) ->
        {
            this.mc.displayGuiScreen((Screen)null);
            int i = HTTPUtil.getSuitableLanPort();
            ITextComponent itextcomponent;

            if (this.mc.getIntegratedServer().shareToLAN(GameType.getByName(this.gameMode), this.allowCheats, i))
            {
                itextcomponent = new TranslationTextComponent("commands.publish.started", i);
            }
            else {
                itextcomponent = new TranslationTextComponent("commands.publish.failed");
            }

            this.mc.ingameGUI.getChatGUI().printChatMessage(itextcomponent);
            this.mc.setDefaultMinecraftTitle();
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, DialogTexts.GUI_CANCEL, (p_213085_1_) ->
        {
            this.mc.displayGuiScreen(this.lastScreen);
        }));
        this.gameModeButton = this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, StringTextComponent.EMPTY, (p_213084_1_) ->
        {
            if ("spectator".equals(this.gameMode))
            {
                this.gameMode = "creative";
            }
            else if ("creative".equals(this.gameMode))
            {
                this.gameMode = "adventure";
            }
            else if ("adventure".equals(this.gameMode))
            {
                this.gameMode = "survival";
            }
            else {
                this.gameMode = "spectator";
            }

            this.updateDisplayNames();
        }));
        this.allowCheatsButton = this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, field_243310_a, (p_213083_1_) ->
        {
            this.allowCheats = !this.allowCheats;
            this.updateDisplayNames();
        }));
        this.updateDisplayNames();
    }

    private void updateDisplayNames()
    {
        this.gameModeButton.setMessage(new TranslationTextComponent("options.generic_value", field_243311_b, new TranslationTextComponent("selectWorld.gameMode." + this.gameMode)));
        this.allowCheatsButton.setMessage(DialogTexts.getComposedOptionMessage(field_243310_a, this.allowCheats));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 50, 16777215);
        drawCenteredString(matrixStack, this.font, field_243312_c, this.width / 2, 82, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
