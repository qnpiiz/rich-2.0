package net.optifine.shaders.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.Config;
import net.optifine.Lang;
import net.optifine.gui.SlotGui;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.util.ResUtils;

class GuiSlotShaders extends SlotGui
{
    private ArrayList shaderslist;
    private int selectedIndex;
    private long lastClicked = Long.MIN_VALUE;
    private long lastClickedCached = 0L;
    final GuiShaders shadersGui;

    public GuiSlotShaders(GuiShaders par1GuiShaders, int width, int height, int top, int bottom, int slotHeight)
    {
        super(par1GuiShaders.getMc(), width, height, top, bottom, slotHeight);
        this.shadersGui = par1GuiShaders;
        this.updateList();
        this.yo = 0.0D;
        int i = this.selectedIndex * slotHeight;
        int j = (bottom - top) / 2;

        if (i > j)
        {
            this.scroll(i - j);
        }
    }

    public int getRowWidth()
    {
        return this.width - 20;
    }

    public void updateList()
    {
        this.shaderslist = Shaders.listOfShaders();
        this.selectedIndex = 0;
        int i = 0;

        for (int j = this.shaderslist.size(); i < j; ++i)
        {
            if (((String)this.shaderslist.get(i)).equals(Shaders.currentShaderName))
            {
                this.selectedIndex = i;
                break;
            }
        }
    }

    protected int getItemCount()
    {
        return this.shaderslist.size();
    }

    protected boolean selectItem(int index, int buttons, double x, double y)
    {
        if (index == this.selectedIndex && this.lastClicked == this.lastClickedCached)
        {
            return false;
        }
        else
        {
            String s = (String)this.shaderslist.get(index);
            IShaderPack ishaderpack = Shaders.getShaderPack(s);

            if (!this.checkCompatible(ishaderpack, index))
            {
                return false;
            }
            else
            {
                this.selectIndex(index);
                return true;
            }
        }
    }

    private void selectIndex(int index)
    {
        this.selectedIndex = index;
        this.lastClickedCached = this.lastClicked;
        Shaders.setShaderPack((String)this.shaderslist.get(index));
        Shaders.uninit();
        this.shadersGui.updateButtons();
    }

    private boolean checkCompatible(IShaderPack sp, int index)
    {
        if (sp == null)
        {
            return true;
        }
        else
        {
            InputStream inputstream = sp.getResourceAsStream("/shaders/shaders.properties");
            Properties properties = ResUtils.readProperties(inputstream, "Shaders");

            if (properties == null)
            {
                return true;
            }
            else
            {
                String s = "version.1.16.5";
                String s1 = properties.getProperty(s);

                if (s1 == null)
                {
                    return true;
                }
                else
                {
                    s1 = s1.trim();
                    String s2 = "G8";
                    int i = Config.compareRelease(s2, s1);

                    if (i >= 0)
                    {
                        return true;
                    }
                    else
                    {
                        String s3 = ("HD_U_" + s1).replace('_', ' ');
                        String s4 = I18n.format("of.message.shaders.nv1", s3);
                        String s5 = I18n.format("of.message.shaders.nv2");
                        BooleanConsumer booleanconsumer = (result) ->
                        {
                            if (result)
                            {
                                this.selectIndex(index);
                            }

                            this.minecraft.displayGuiScreen(this.shadersGui);
                        };
                        ConfirmScreen confirmscreen = new ConfirmScreen(booleanconsumer, new StringTextComponent(s4), new StringTextComponent(s5));
                        this.minecraft.displayGuiScreen(confirmscreen);
                        return false;
                    }
                }
            }
        }
    }

    protected boolean isSelectedItem(int index)
    {
        return index == this.selectedIndex;
    }

    protected int getScrollbarPosition()
    {
        return this.width - 6;
    }

    public int getItemHeight()
    {
        return this.getItemCount() * 18;
    }

    protected void renderBackground()
    {
    }

    protected void renderItem(MatrixStack matrixStackIn, int index, int posX, int posY, int contentY, int mouseX, int mouseY, float partialTicks)
    {
        String s = (String)this.shaderslist.get(index);

        if (s.equals("OFF"))
        {
            s = Lang.get("of.options.shaders.packNone");
        }
        else if (s.equals("(internal)"))
        {
            s = Lang.get("of.options.shaders.packDefault");
        }

        this.shadersGui.drawCenteredString(matrixStackIn, s, this.width / 2, posY + 1, 14737632);
    }

    public int getSelectedIndex()
    {
        return this.selectedIndex;
    }

    public boolean mouseScrolled(double x, double y, double amount)
    {
        return super.mouseScrolled(x, y, amount * 3.0D);
    }
}
