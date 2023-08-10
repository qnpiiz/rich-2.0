package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.realms.IErrorConsumer;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LongRunningTask implements IErrorConsumer, Runnable
{
    public static final Logger field_238124_a_ = LogManager.getLogger();
    protected RealmsLongRunningMcoTaskScreen field_224993_a;

    protected static void func_238125_a_(int p_238125_0_)
    {
        try
        {
            Thread.sleep((long)(p_238125_0_ * 1000));
        }
        catch (InterruptedException interruptedexception)
        {
            field_238124_a_.error("", (Throwable)interruptedexception);
        }
    }

    public static void func_238127_a_(Screen p_238127_0_)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() ->
        {
            minecraft.displayGuiScreen(p_238127_0_);
        });
    }

    public void func_224987_a(RealmsLongRunningMcoTaskScreen p_224987_1_)
    {
        this.field_224993_a = p_224987_1_;
    }

    public void func_230434_a_(ITextComponent p_230434_1_)
    {
        this.field_224993_a.func_230434_a_(p_230434_1_);
    }

    public void func_224989_b(ITextComponent p_224989_1_)
    {
        this.field_224993_a.func_224234_b(p_224989_1_);
    }

    public boolean func_224988_a()
    {
        return this.field_224993_a.func_224235_b();
    }

    public void func_224990_b()
    {
    }

    public void func_224991_c()
    {
    }

    public void func_224992_d()
    {
    }
}
