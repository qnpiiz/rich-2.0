package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;

public abstract class WithNarratorSettingsScreen extends SettingsScreen
{
    private final AbstractOption[] field_243313_c;
    @Nullable
    private Widget field_243314_p;
    private OptionsRowList field_243315_q;

    public WithNarratorSettingsScreen(Screen p_i242058_1_, GameSettings p_i242058_2_, ITextComponent p_i242058_3_, AbstractOption[] p_i242058_4_)
    {
        super(p_i242058_1_, p_i242058_2_, p_i242058_3_);
        this.field_243313_c = p_i242058_4_;
    }

    protected void init()
    {
        this.field_243315_q = new OptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25);
        this.field_243315_q.addOptions(this.field_243313_c);
        this.children.add(this.field_243315_q);
        this.func_244718_c();
        this.field_243314_p = this.field_243315_q.func_243271_b(AbstractOption.NARRATOR);

        if (this.field_243314_p != null)
        {
            this.field_243314_p.active = NarratorChatListener.INSTANCE.isActive();
        }
    }

    protected void func_244718_c()
    {
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE, (p_243316_1_) ->
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.field_243315_q.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> list = func_243293_a(this.field_243315_q, mouseX, mouseY);

        if (list != null)
        {
            this.renderTooltip(matrixStack, list, mouseX, mouseY);
        }
    }

    public void func_243317_i()
    {
        if (this.field_243314_p != null)
        {
            this.field_243314_p.setMessage(AbstractOption.NARRATOR.getName(this.gameSettings));
        }
    }
}
