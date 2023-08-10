package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerListScreen extends Screen
{
    private static final ITextComponent field_243288_a = new TranslationTextComponent("addServer.enterIp");
    private Button field_195170_a;
    private final ServerData serverData;
    private TextFieldWidget ipEdit;
    private final BooleanConsumer field_213027_d;
    private final Screen previousScreen;

    public ServerListScreen(Screen previousScreen, BooleanConsumer p_i225926_2_, ServerData serverData)
    {
        super(new TranslationTextComponent("selectServer.direct"));
        this.previousScreen = previousScreen;
        this.serverData = serverData;
        this.field_213027_d = p_i225926_2_;
    }

    public void tick()
    {
        this.ipEdit.tick();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.getListener() != this.ipEdit || keyCode != 257 && keyCode != 335)
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        else
        {
            this.func_195167_h();
            return true;
        }
    }

    protected void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_195170_a = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, new TranslationTextComponent("selectServer.select"), (p_213026_1_) ->
        {
            this.func_195167_h();
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, DialogTexts.GUI_CANCEL, (p_213025_1_) ->
        {
            this.field_213027_d.accept(false);
        }));
        this.ipEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 116, 200, 20, new TranslationTextComponent("addServer.enterIp"));
        this.ipEdit.setMaxStringLength(128);
        this.ipEdit.setFocused2(true);
        this.ipEdit.setText(this.mc.gameSettings.lastServer);
        this.ipEdit.setResponder((p_213024_1_) ->
        {
            this.func_195168_i();
        });
        this.children.add(this.ipEdit);
        this.setFocusedDefault(this.ipEdit);
        this.func_195168_i();
    }

    public void resize(Minecraft minecraft, int width, int height)
    {
        String s = this.ipEdit.getText();
        this.init(minecraft, width, height);
        this.ipEdit.setText(s);
    }

    private void func_195167_h()
    {
        this.serverData.serverIP = this.ipEdit.getText();
        this.field_213027_d.accept(true);
    }

    public void closeScreen()
    {
        this.mc.displayGuiScreen(this.previousScreen);
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.ipEdit.getText();
        this.mc.gameSettings.saveOptions();
    }

    private void func_195168_i()
    {
        String s = this.ipEdit.getText();
        this.field_195170_a.active = !s.isEmpty() && s.split(":").length > 0 && s.indexOf(32) == -1;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
        drawString(matrixStack, this.font, field_243288_a, this.width / 2 - 100, 100, 10526880);
        this.ipEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
