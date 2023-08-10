package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.LongRunningTask;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.IErrorConsumer;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsLongRunningMcoTaskScreen extends RealmsScreen implements IErrorConsumer
{
    private static final Logger field_224238_b = LogManager.getLogger();
    private final Screen field_224241_e;
    private volatile ITextComponent field_224243_g = StringTextComponent.EMPTY;
    @Nullable
    private volatile ITextComponent field_224245_i;
    private volatile boolean field_224246_j;
    private int field_224247_k;
    private final LongRunningTask field_224248_l;
    private final int field_224249_m = 212;
    public static final String[] field_224237_a = new String[] {"\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _"};

    public RealmsLongRunningMcoTaskScreen(Screen p_i232209_1_, LongRunningTask p_i232209_2_)
    {
        this.field_224241_e = p_i232209_1_;
        this.field_224248_l = p_i232209_2_;
        p_i232209_2_.func_224987_a(this);
        Thread thread = new Thread(p_i232209_2_, "Realms-long-running-task");
        thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(field_224238_b));
        thread.start();
    }

    public void tick()
    {
        super.tick();
        RealmsNarratorHelper.func_239553_b_(this.field_224243_g.getString());
        ++this.field_224247_k;
        this.field_224248_l.func_224990_b();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.func_224236_c();
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void init()
    {
        this.field_224248_l.func_224991_c();
        this.addButton(new Button(this.width / 2 - 106, func_239562_k_(12), 212, 20, DialogTexts.GUI_CANCEL, (p_237852_1_) ->
        {
            this.func_224236_c();
        }));
    }

    private void func_224236_c()
    {
        this.field_224246_j = true;
        this.field_224248_l.func_224992_d();
        this.mc.displayGuiScreen(this.field_224241_e);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.field_224243_g, this.width / 2, func_239562_k_(3), 16777215);
        ITextComponent itextcomponent = this.field_224245_i;

        if (itextcomponent == null)
        {
            drawCenteredString(matrixStack, this.font, field_224237_a[this.field_224247_k % field_224237_a.length], this.width / 2, func_239562_k_(8), 8421504);
        }
        else
        {
            drawCenteredString(matrixStack, this.font, itextcomponent, this.width / 2, func_239562_k_(8), 16711680);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void func_230434_a_(ITextComponent p_230434_1_)
    {
        this.field_224245_i = p_230434_1_;
        RealmsNarratorHelper.func_239550_a_(p_230434_1_.getString());
        this.func_237850_a_();
        this.addButton(new Button(this.width / 2 - 106, this.height / 4 + 120 + 12, 200, 20, DialogTexts.GUI_BACK, (p_237851_1_) ->
        {
            this.func_224236_c();
        }));
    }

    private void func_237850_a_()
    {
        Set<IGuiEventListener> set = Sets.newHashSet(this.buttons);
        this.children.removeIf(set::contains);
        this.buttons.clear();
    }

    public void func_224234_b(ITextComponent p_224234_1_)
    {
        this.field_224243_g = p_224234_1_;
    }

    public boolean func_224235_b()
    {
        return this.field_224246_j;
    }
}
