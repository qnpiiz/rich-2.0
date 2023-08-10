package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.action.ConnectingToRealmsAction;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTermsScreen extends RealmsScreen
{
    private static final Logger field_224722_a = LogManager.getLogger();
    private static final ITextComponent field_243184_b = new TranslationTextComponent("mco.terms.title");
    private static final ITextComponent field_243185_c = new TranslationTextComponent("mco.terms.sentence.1");
    private static final ITextComponent field_243186_p = (new StringTextComponent(" ")).append((new TranslationTextComponent("mco.terms.sentence.2")).mergeStyle(Style.EMPTY.func_244282_c(true)));
    private final Screen field_224723_b;
    private final RealmsMainScreen field_224724_c;

    /**
     * The screen to display when OK is clicked on the disconnect screen.
     *  
     * Seems to be either null (integrated server) or an instance of either {@link MultiplayerScreen} (when connecting
     * to a server) or {@link com.mojang.realmsclient.gui.screens.RealmsTermsScreen} (when connecting to MCO server)
     */
    private final RealmsServer guiScreenServer;
    private boolean field_224727_f;
    private final String field_224728_g = "https://aka.ms/MinecraftRealmsTerms";

    public RealmsTermsScreen(Screen p_i232225_1_, RealmsMainScreen p_i232225_2_, RealmsServer p_i232225_3_)
    {
        this.field_224723_b = p_i232225_1_;
        this.field_224724_c = p_i232225_2_;
        this.guiScreenServer = p_i232225_3_;
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        int i = this.width / 4 - 2;
        this.addButton(new Button(this.width / 4, func_239562_k_(12), i, 20, new TranslationTextComponent("mco.terms.buttons.agree"), (p_238078_1_) ->
        {
            this.func_224721_a();
        }));
        this.addButton(new Button(this.width / 2 + 4, func_239562_k_(12), i, 20, new TranslationTextComponent("mco.terms.buttons.disagree"), (p_238077_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224723_b);
        }));
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.displayGuiScreen(this.field_224723_b);
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void func_224721_a()
    {
        RealmsClient realmsclient = RealmsClient.func_224911_a();

        try
        {
            realmsclient.func_224937_l();
            this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(this.field_224723_b, new ConnectingToRealmsAction(this.field_224724_c, this.field_224723_b, this.guiScreenServer, new ReentrantLock())));
        }
        catch (RealmsServiceException realmsserviceexception)
        {
            field_224722_a.error("Couldn't agree to TOS");
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.field_224727_f)
        {
            this.mc.keyboardListener.setClipboardString("https://aka.ms/MinecraftRealmsTerms");
            Util.getOSType().openURI("https://aka.ms/MinecraftRealmsTerms");
            return true;
        }
        else
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public String getNarrationMessage()
    {
        return super.getNarrationMessage() + ". " + field_243185_c.getString() + " " + field_243186_p.getString();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, field_243184_b, this.width / 2, 17, 16777215);
        this.font.func_243248_b(matrixStack, field_243185_c, (float)(this.width / 2 - 120), (float)func_239562_k_(5), 16777215);
        int i = this.font.getStringPropertyWidth(field_243185_c);
        int j = this.width / 2 - 121 + i;
        int k = func_239562_k_(5);
        int l = j + this.font.getStringPropertyWidth(field_243186_p) + 1;
        int i1 = k + 1 + 9;
        this.field_224727_f = j <= mouseX && mouseX <= l && k <= mouseY && mouseY <= i1;
        this.font.func_243248_b(matrixStack, field_243186_p, (float)(this.width / 2 - 120 + i), (float)func_239562_k_(5), this.field_224727_f ? 7107012 : 3368635);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
