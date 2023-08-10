package net.optifine.shaders.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.Config;
import net.optifine.Lang;
import net.optifine.gui.GuiButtonOF;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderEnumShaderOptions;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersTex;
import net.optifine.shaders.config.EnumShaderOption;

public class GuiShaders extends GuiScreenOF
{
    protected Screen parentGui;
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderEnumShaderOptions());
    private int updateTimer = -1;
    private GuiSlotShaders shaderList;
    private boolean saved = false;
    private static float[] QUALITY_MULTIPLIERS = new float[] {0.5F, 0.6F, 0.6666667F, 0.75F, 0.8333333F, 0.9F, 1.0F, 1.1666666F, 1.3333334F, 1.5F, 1.6666666F, 1.8F, 2.0F};
    private static String[] QUALITY_MULTIPLIER_NAMES = new String[] {"0.5x", "0.6x", "0.66x", "0.75x", "0.83x", "0.9x", "1x", "1.16x", "1.33x", "1.5x", "1.66x", "1.8x", "2x"};
    private static float QUALITY_MULTIPLIER_DEFAULT = 1.0F;
    private static float[] HAND_DEPTH_VALUES = new float[] {0.0625F, 0.125F, 0.25F};
    private static String[] HAND_DEPTH_NAMES = new String[] {"0.5x", "1x", "2x"};
    private static float HAND_DEPTH_DEFAULT = 0.125F;
    public static final int EnumOS_UNKNOWN = 0;
    public static final int EnumOS_WINDOWS = 1;
    public static final int EnumOS_OSX = 2;
    public static final int EnumOS_SOLARIS = 3;
    public static final int EnumOS_LINUX = 4;

    public GuiShaders(Screen par1GuiScreen, GameSettings par2GameSettings)
    {
        super(new StringTextComponent(I18n.format("of.options.shadersTitle")));
        this.parentGui = par1GuiScreen;
    }

    public void init()
    {
        if (Shaders.shadersConfig == null)
        {
            Shaders.loadConfig();
        }

        int i = 120;
        int j = 20;
        int k = this.width - i - 10;
        int l = 30;
        int i1 = 20;
        int j1 = this.width - i - 20;
        this.shaderList = new GuiSlotShaders(this, j1, this.height, l, this.height - 50, 16);
        this.children.add(this.shaderList);
        this.addButton(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, k, 0 * i1 + l, i, j));
        this.addButton(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, k, 1 * i1 + l, i, j));
        this.addButton(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, k, 2 * i1 + l, i, j));
        this.addButton(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, k, 3 * i1 + l, i, j));
        this.addButton(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, k, 4 * i1 + l, i, j));
        this.addButton(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, k, 5 * i1 + l, i, j));
        this.addButton(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_HAND_LIGHT, k, 6 * i1 + l, i, j));
        this.addButton(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, k, 7 * i1 + l, i, j));
        int k1 = Math.min(150, j1 / 2 - 10);
        int l1 = j1 / 4 - k1 / 2;
        int i2 = this.height - 25;
        this.addButton(new GuiButtonOF(201, l1, i2, k1 - 22 + 1, j, Lang.get("of.options.shaders.shadersFolder")));
        this.addButton(new GuiButtonDownloadShaders(210, l1 + k1 - 22 - 1, i2));
        this.addButton(new GuiButtonOF(202, j1 / 4 * 3 - k1 / 2, this.height - 25, k1, j, I18n.format("gui.done")));
        this.addButton(new GuiButtonOF(203, k, this.height - 25, i, j, Lang.get("of.options.shaders.shaderOptions")));
        this.setListener(this.shaderList);
        this.updateButtons();
    }

    public void updateButtons()
    {
        boolean flag = Config.isShaders();

        for (Widget widget : this.buttonList)
        {
            if (widget instanceof GuiButtonOF)
            {
                GuiButtonOF guibuttonof = (GuiButtonOF)widget;

                if (guibuttonof.id != 201 && guibuttonof.id != 202 && guibuttonof.id != 210 && guibuttonof.id != EnumShaderOption.ANTIALIASING.ordinal())
                {
                    guibuttonof.active = flag;
                }
            }
        }
    }

    protected void actionPerformed(Widget button)
    {
        this.actionPerformed(button, false);
    }

    protected void actionPerformedRightClick(Widget button)
    {
        this.actionPerformed(button, true);
    }

    private void actionPerformed(Widget guiElement, boolean rightClick)
    {
        if (guiElement.active)
        {
            if (guiElement instanceof GuiButtonEnumShaderOption)
            {
                GuiButtonEnumShaderOption guibuttonenumshaderoption = (GuiButtonEnumShaderOption)guiElement;

                switch (guibuttonenumshaderoption.getEnumShaderOption())
                {
                    case ANTIALIASING:
                        Shaders.nextAntialiasingLevel(!rightClick);

                        if (hasShiftDown())
                        {
                            Shaders.configAntialiasingLevel = 0;
                        }

                        Shaders.uninit();
                        break;

                    case NORMAL_MAP:
                        Shaders.configNormalMap = !Shaders.configNormalMap;

                        if (hasShiftDown())
                        {
                            Shaders.configNormalMap = true;
                        }

                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;

                    case SPECULAR_MAP:
                        Shaders.configSpecularMap = !Shaders.configSpecularMap;

                        if (hasShiftDown())
                        {
                            Shaders.configSpecularMap = true;
                        }

                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;

                    case RENDER_RES_MUL:
                        Shaders.configRenderResMul = this.getNextValue(Shaders.configRenderResMul, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_DEFAULT, !rightClick, hasShiftDown());
                        Shaders.uninit();
                        Shaders.scheduleResize();
                        break;

                    case SHADOW_RES_MUL:
                        Shaders.configShadowResMul = this.getNextValue(Shaders.configShadowResMul, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_DEFAULT, !rightClick, hasShiftDown());
                        Shaders.uninit();
                        Shaders.scheduleResizeShadow();
                        break;

                    case HAND_DEPTH_MUL:
                        Shaders.configHandDepthMul = this.getNextValue(Shaders.configHandDepthMul, HAND_DEPTH_VALUES, HAND_DEPTH_DEFAULT, !rightClick, hasShiftDown());
                        Shaders.uninit();
                        break;

                    case OLD_HAND_LIGHT:
                        Shaders.configOldHandLight.nextValue(!rightClick);

                        if (hasShiftDown())
                        {
                            Shaders.configOldHandLight.resetValue();
                        }

                        Shaders.uninit();
                        break;

                    case OLD_LIGHTING:
                        Shaders.configOldLighting.nextValue(!rightClick);

                        if (hasShiftDown())
                        {
                            Shaders.configOldLighting.resetValue();
                        }

                        Shaders.updateBlockLightLevel();
                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;

                    case TWEAK_BLOCK_DAMAGE:
                        Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                        break;

                    case CLOUD_SHADOW:
                        Shaders.configCloudShadow = !Shaders.configCloudShadow;
                        break;

                    case TEX_MIN_FIL_B:
                        Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                        Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
                        guibuttonenumshaderoption.setMessage("Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB]);
                        ShadersTex.updateTextureMinMagFilter();
                        break;

                    case TEX_MAG_FIL_N:
                        Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                        guibuttonenumshaderoption.setMessage("Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN]);
                        ShadersTex.updateTextureMinMagFilter();
                        break;

                    case TEX_MAG_FIL_S:
                        Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                        guibuttonenumshaderoption.setMessage("Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS]);
                        ShadersTex.updateTextureMinMagFilter();
                        break;

                    case SHADOW_CLIP_FRUSTRUM:
                        Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                        guibuttonenumshaderoption.setMessage("ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum));
                        ShadersTex.updateTextureMinMagFilter();
                }

                guibuttonenumshaderoption.updateButtonText();
            }
            else if (!rightClick)
            {
                if (guiElement instanceof GuiButtonOF)
                {
                    GuiButtonOF guibuttonof = (GuiButtonOF)guiElement;

                    switch (guibuttonof.id)
                    {
                        case 201:
                            switch (getOSType())
                            {
                                case 1:
                                    String s = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderPacksDir.getAbsolutePath());

                                    try
                                    {
                                        Runtime.getRuntime().exec(s);
                                        return;
                                    }
                                    catch (IOException ioexception)
                                    {
                                        ioexception.printStackTrace();
                                        break;
                                    }

                                case 2:
                                    try
                                    {
                                        Runtime.getRuntime().exec(new String[] {"/usr/bin/open", Shaders.shaderPacksDir.getAbsolutePath()});
                                        return;
                                    }
                                    catch (IOException ioexception1)
                                    {
                                        ioexception1.printStackTrace();
                                    }
                            }

                            boolean flag = false;

                            try
                            {
                                URI uri1 = (new File(this.mc.gameDir, "shaderpacks")).toURI();
                                Util.getOSType().openURI(uri1);
                            }
                            catch (Throwable throwable1)
                            {
                                throwable1.printStackTrace();
                                flag = true;
                            }

                            if (flag)
                            {
                                Config.dbg("Opening via system class!");
                                Util.getOSType().openURI("file://" + Shaders.shaderPacksDir.getAbsolutePath());
                            }

                            break;

                        case 202:
                            Shaders.storeConfig();
                            this.saved = true;
                            this.mc.displayGuiScreen(this.parentGui);
                            break;

                        case 203:
                            GuiShaderOptions guishaderoptions = new GuiShaderOptions(this, Config.getGameSettings());
                            Config.getMinecraft().displayGuiScreen(guishaderoptions);

                        case 204:
                        case 205:
                        case 206:
                        case 207:
                        case 208:
                        case 209:
                        default:
                            break;

                        case 210:
                            try
                            {
                                URI uri = new URI("http://optifine.net/shaderPacks");
                                Util.getOSType().openURI(uri);
                            }
                            catch (Throwable throwable1)
                            {
                                throwable1.printStackTrace();
                            }
                    }
                }
            }
        }
    }

    public void onClose()
    {
        if (!this.saved)
        {
            Shaders.storeConfig();
            this.saved = true;
        }

        super.onClose();
    }

    public void render(MatrixStack matrixStackIn, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStackIn);
        this.shaderList.render(matrixStackIn, mouseX, mouseY, partialTicks);

        if (this.updateTimer <= 0)
        {
            this.shaderList.updateList();
            this.updateTimer += 20;
        }

        drawCenteredString(matrixStackIn, this.fontRenderer, this.title, this.width / 2, 15, 16777215);
        String s = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
        int i = this.fontRenderer.getStringWidth(s);

        if (i < this.width - 5)
        {
            drawCenteredString(matrixStackIn, this.fontRenderer, s, this.width / 2, this.height - 40, 8421504);
        }
        else
        {
            drawString(matrixStackIn, this.fontRenderer, s, 5, this.height - 40, 8421504);
        }

        super.render(matrixStackIn, mouseX, mouseY, partialTicks);
        this.tooltipManager.drawTooltips(matrixStackIn, mouseX, mouseY, this.buttonList);
    }

    public void tick()
    {
        super.tick();
        --this.updateTimer;
    }

    public Minecraft getMc()
    {
        return this.mc;
    }

    public void drawCenteredString(MatrixStack matrixStackIn, String text, int x, int y, int color)
    {
        drawCenteredString(matrixStackIn, this.fontRenderer, text, x, y, color);
    }

    public static String toStringOnOff(boolean value)
    {
        String s = Lang.getOn();
        String s1 = Lang.getOff();
        return value ? s : s1;
    }

    public static String toStringAa(int value)
    {
        if (value == 2)
        {
            return "FXAA 2x";
        }
        else
        {
            return value == 4 ? "FXAA 4x" : Lang.getOff();
        }
    }

    public static String toStringValue(float val, float[] values, String[] names)
    {
        int i = getValueIndex(val, values);
        return names[i];
    }

    private float getNextValue(float val, float[] values, float valDef, boolean forward, boolean reset)
    {
        if (reset)
        {
            return valDef;
        }
        else
        {
            int i = getValueIndex(val, values);

            if (forward)
            {
                ++i;

                if (i >= values.length)
                {
                    i = 0;
                }
            }
            else
            {
                --i;

                if (i < 0)
                {
                    i = values.length - 1;
                }
            }

            return values[i];
        }
    }

    public static int getValueIndex(float val, float[] values)
    {
        for (int i = 0; i < values.length; ++i)
        {
            float f = values[i];

            if (f >= val)
            {
                return i;
            }
        }

        return values.length - 1;
    }

    public static String toStringQuality(float val)
    {
        return toStringValue(val, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
    }

    public static String toStringHandDepth(float val)
    {
        return toStringValue(val, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
    }

    public static int getOSType()
    {
        String s = System.getProperty("os.name").toLowerCase();

        if (s.contains("win"))
        {
            return 1;
        }
        else if (s.contains("mac"))
        {
            return 2;
        }
        else if (s.contains("solaris"))
        {
            return 3;
        }
        else if (s.contains("sunos"))
        {
            return 3;
        }
        else if (s.contains("linux"))
        {
            return 4;
        }
        else
        {
            return s.contains("unix") ? 4 : 0;
        }
    }
}
