package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class AlertScreen extends Screen
{
    private final Runnable field_201552_h;
    protected final ITextComponent field_201550_f;
    private IBidiRenderer field_243274_p = IBidiRenderer.field_243257_a;
    protected final ITextComponent field_201551_g;
    private int buttonDelay;

    public AlertScreen(Runnable p_i48623_1_, ITextComponent p_i48623_2_, ITextComponent p_i48623_3_)
    {
        this(p_i48623_1_, p_i48623_2_, p_i48623_3_, DialogTexts.GUI_BACK);
    }

    public AlertScreen(Runnable p_i232268_1_, ITextComponent p_i232268_2_, ITextComponent p_i232268_3_, ITextComponent p_i232268_4_)
    {
        super(p_i232268_2_);
        this.field_201552_h = p_i232268_1_;
        this.field_201550_f = p_i232268_3_;
        this.field_201551_g = p_i232268_4_;
    }

    protected void init()
    {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.field_201551_g, (p_212983_1_) ->
        {
            this.field_201552_h.run();
        }));
        this.field_243274_p = IBidiRenderer.func_243258_a(this.font, this.field_201550_f, this.width - 50);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 70, 16777215);
        this.field_243274_p.func_241863_a(matrixStack, this.width / 2, 90);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void tick()
    {
        super.tick();

        if (--this.buttonDelay == 0)
        {
            for (Widget widget : this.buttons)
            {
                widget.active = true;
            }
        }
    }
}
