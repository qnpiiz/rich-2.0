package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class RealmsBackupInfoScreen extends RealmsScreen
{
    private final Screen field_224047_c;
    private final Backup field_224049_e;
    private RealmsBackupInfoScreen.BackupInfoList field_224051_g;

    public RealmsBackupInfoScreen(Screen p_i232197_1_, Backup p_i232197_2_)
    {
        this.field_224047_c = p_i232197_1_;
        this.field_224049_e = p_i232197_2_;
    }

    public void tick()
    {
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, DialogTexts.GUI_BACK, (p_237731_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224047_c);
        }));
        this.field_224051_g = new RealmsBackupInfoScreen.BackupInfoList(this.mc);
        this.addListener(this.field_224051_g);
        this.setListenerDefault(this.field_224051_g);
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.displayGuiScreen(this.field_224047_c);
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, "Changes from last backup", this.width / 2, 10, 16777215);
        this.field_224051_g.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private ITextComponent func_237733_a_(String p_237733_1_, String p_237733_2_)
    {
        String s = p_237733_1_.toLowerCase(Locale.ROOT);

        if (s.contains("game") && s.contains("mode"))
        {
            return this.func_237735_b_(p_237733_2_);
        }
        else
        {
            return (ITextComponent)(s.contains("game") && s.contains("difficulty") ? this.func_237732_a_(p_237733_2_) : new StringTextComponent(p_237733_2_));
        }
    }

    private ITextComponent func_237732_a_(String p_237732_1_)
    {
        try
        {
            return RealmsSlotOptionsScreen.field_238035_a_[Integer.parseInt(p_237732_1_)];
        }
        catch (Exception exception)
        {
            return new StringTextComponent("UNKNOWN");
        }
    }

    private ITextComponent func_237735_b_(String p_237735_1_)
    {
        try
        {
            return RealmsSlotOptionsScreen.field_238036_b_[Integer.parseInt(p_237735_1_)];
        }
        catch (Exception exception)
        {
            return new StringTextComponent("UNKNOWN");
        }
    }

    class BackupInfoEntry extends ExtendedList.AbstractListEntry<RealmsBackupInfoScreen.BackupInfoEntry>
    {
        private final String field_237738_b_;
        private final String field_237739_c_;

        public BackupInfoEntry(String p_i232199_2_, String p_i232199_3_)
        {
            this.field_237738_b_ = p_i232199_2_;
            this.field_237739_c_ = p_i232199_3_;
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            FontRenderer fontrenderer = RealmsBackupInfoScreen.this.mc.fontRenderer;
            AbstractGui.drawString(p_230432_1_, fontrenderer, this.field_237738_b_, p_230432_4_, p_230432_3_, 10526880);
            AbstractGui.drawString(p_230432_1_, fontrenderer, RealmsBackupInfoScreen.this.func_237733_a_(this.field_237738_b_, this.field_237739_c_), p_230432_4_, p_230432_3_ + 12, 16777215);
        }
    }

    class BackupInfoList extends ExtendedList<RealmsBackupInfoScreen.BackupInfoEntry>
    {
        public BackupInfoList(Minecraft p_i232198_2_)
        {
            super(p_i232198_2_, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
            this.setRenderSelection(false);

            if (RealmsBackupInfoScreen.this.field_224049_e.field_230557_e_ != null)
            {
                RealmsBackupInfoScreen.this.field_224049_e.field_230557_e_.forEach((p_237736_1_, p_237736_2_) ->
                {
                    this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoEntry(p_237736_1_, p_237736_2_));
                });
            }
        }
    }
}
