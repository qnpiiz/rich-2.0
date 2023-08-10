package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class RealmsNotificationsScreen extends RealmsScreen
{
    private static final ResourceLocation field_237853_a_ = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
    private static final ResourceLocation field_237854_b_ = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
    private static final ResourceLocation field_237855_c_ = new ResourceLocation("realms", "textures/gui/realms/news_notification_mainscreen.png");
    private static final RealmsDataFetcher field_237856_p_ = new RealmsDataFetcher();
    private volatile int field_224266_b;
    private static boolean field_224267_c;
    private static boolean field_224268_d;
    private static boolean field_224269_e;
    private static boolean field_224270_f;

    public void init()
    {
        this.func_224261_a();
        this.mc.keyboardListener.enableRepeatEvents(true);
    }

    public void tick()
    {
        if ((!this.func_237858_g_() || !this.func_237859_j_() || !field_224269_e) && !field_237856_p_.func_225065_a())
        {
            field_237856_p_.func_225070_k();
        }
        else if (field_224269_e && this.func_237858_g_())
        {
            field_237856_p_.func_237710_c_();

            if (field_237856_p_.func_225083_a(RealmsDataFetcher.Task.PENDING_INVITE))
            {
                this.field_224266_b = field_237856_p_.func_225081_f();
            }

            if (field_237856_p_.func_225083_a(RealmsDataFetcher.Task.TRIAL_AVAILABLE))
            {
                field_224268_d = field_237856_p_.func_225071_g();
            }

            if (field_237856_p_.func_225083_a(RealmsDataFetcher.Task.UNREAD_NEWS))
            {
                field_224270_f = field_237856_p_.func_225059_i();
            }

            field_237856_p_.func_225072_c();
        }
    }

    private boolean func_237858_g_()
    {
        return this.mc.gameSettings.realmsNotifications;
    }

    private boolean func_237859_j_()
    {
        return this.mc.currentScreen instanceof MainMenuScreen;
    }

    private void func_224261_a()
    {
        if (!field_224267_c)
        {
            field_224267_c = true;
            (new Thread("Realms Notification Availability checker #1")
            {
                public void run()
                {
                    RealmsClient realmsclient = RealmsClient.func_224911_a();

                    try
                    {
                        RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.func_224939_i();

                        if (realmsclient$compatibleversionresponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE)
                        {
                            return;
                        }
                    }
                    catch (RealmsServiceException realmsserviceexception)
                    {
                        if (realmsserviceexception.field_224981_a != 401)
                        {
                            RealmsNotificationsScreen.field_224267_c = false;
                        }

                        return;
                    }

                    RealmsNotificationsScreen.field_224269_e = true;
                }
            }).start();
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (field_224269_e)
        {
            this.func_237857_a_(matrixStack, mouseX, mouseY);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void func_237857_a_(MatrixStack p_237857_1_, int p_237857_2_, int p_237857_3_)
    {
        int i = this.field_224266_b;
        int j = 24;
        int k = this.height / 4 + 48;
        int l = this.width / 2 + 80;
        int i1 = k + 48 + 2;
        int j1 = 0;

        if (field_224270_f)
        {
            this.mc.getTextureManager().bindTexture(field_237855_c_);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.4F, 0.4F, 0.4F);
            AbstractGui.blit(p_237857_1_, (int)((double)(l + 2 - j1) * 2.5D), (int)((double)i1 * 2.5D), 0.0F, 0.0F, 40, 40, 40, 40);
            RenderSystem.popMatrix();
            j1 += 14;
        }

        if (i != 0)
        {
            this.mc.getTextureManager().bindTexture(field_237853_a_);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(p_237857_1_, l - j1, i1 - 6, 0.0F, 0.0F, 15, 25, 31, 25);
            j1 += 16;
        }

        if (field_224268_d)
        {
            this.mc.getTextureManager().bindTexture(field_237854_b_);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = 0;

            if ((Util.milliTime() / 800L & 1L) == 1L)
            {
                k1 = 8;
            }

            AbstractGui.blit(p_237857_1_, l + 4 - j1, i1 + 4, 0.0F, (float)k1, 8, 8, 8, 16);
        }
    }

    public void onClose()
    {
        field_237856_p_.func_225070_k();
    }
}
