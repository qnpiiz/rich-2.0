package net.minecraft.client;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.GPUWarning;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.settings.SliderMultiplierOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.Config;
import net.optifine.config.IteratableOptionOF;
import net.optifine.config.SliderPercentageOptionOF;

public abstract class AbstractOption
{
    public static final SliderPercentageOption BIOME_BLEND_RADIUS = new SliderPercentageOption("options.biomeBlendRadius", 0.0D, 7.0D, 1.0F, (p_lambda$static$0_0_) ->
    {
        return (double)p_lambda$static$0_0_.biomeBlendRadius;
    }, (p_lambda$static$1_0_, p_lambda$static$1_1_) ->
    {
        p_lambda$static$1_0_.biomeBlendRadius = MathHelper.clamp((int)p_lambda$static$1_1_.doubleValue(), 0, 7);
        Minecraft.getInstance().worldRenderer.loadRenderers();
    }, (p_lambda$static$2_0_, p_lambda$static$2_1_) ->
    {
        double d0 = p_lambda$static$2_1_.get(p_lambda$static$2_0_);
        int i = (int)d0 * 2 + 1;
        return p_lambda$static$2_1_.getGenericValueComponent(new TranslationTextComponent("options.biomeBlendRadius." + i));
    });
    public static final SliderPercentageOption CHAT_HEIGHT_FOCUSED = new SliderPercentageOption("options.chat.height.focused", 0.0D, 1.0D, 0.0F, (p_lambda$static$3_0_) ->
    {
        return p_lambda$static$3_0_.chatHeightFocused;
    }, (p_lambda$static$4_0_, p_lambda$static$4_1_) ->
    {
        p_lambda$static$4_0_.chatHeightFocused = p_lambda$static$4_1_;
        Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
    }, (p_lambda$static$5_0_, p_lambda$static$5_1_) ->
    {
        double d0 = p_lambda$static$5_1_.normalizeValue(p_lambda$static$5_1_.get(p_lambda$static$5_0_));
        return p_lambda$static$5_1_.getPixelValueComponent(NewChatGui.calculateChatboxHeight(d0));
    });
    public static final SliderPercentageOption CHAT_HEIGHT_UNFOCUSED = new SliderPercentageOption("options.chat.height.unfocused", 0.0D, 1.0D, 0.0F, (p_lambda$static$6_0_) ->
    {
        return p_lambda$static$6_0_.chatHeightUnfocused;
    }, (p_lambda$static$7_0_, p_lambda$static$7_1_) ->
    {
        p_lambda$static$7_0_.chatHeightUnfocused = p_lambda$static$7_1_;
        Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
    }, (p_lambda$static$8_0_, p_lambda$static$8_1_) ->
    {
        double d0 = p_lambda$static$8_1_.normalizeValue(p_lambda$static$8_1_.get(p_lambda$static$8_0_));
        return p_lambda$static$8_1_.getPixelValueComponent(NewChatGui.calculateChatboxHeight(d0));
    });
    public static final SliderPercentageOption CHAT_OPACITY = new SliderPercentageOption("options.chat.opacity", 0.0D, 1.0D, 0.0F, (p_lambda$static$9_0_) ->
    {
        return p_lambda$static$9_0_.chatOpacity;
    }, (p_lambda$static$10_0_, p_lambda$static$10_1_) ->
    {
        p_lambda$static$10_0_.chatOpacity = p_lambda$static$10_1_;
        Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
    }, (p_lambda$static$11_0_, p_lambda$static$11_1_) ->
    {
        double d0 = p_lambda$static$11_1_.normalizeValue(p_lambda$static$11_1_.get(p_lambda$static$11_0_));
        return p_lambda$static$11_1_.getPercentValueComponent(d0 * 0.9D + 0.1D);
    });
    public static final SliderPercentageOption CHAT_SCALE = new SliderPercentageOption("options.chat.scale", 0.0D, 1.0D, 0.0F, (p_lambda$static$12_0_) ->
    {
        return p_lambda$static$12_0_.chatScale;
    }, (p_lambda$static$13_0_, p_lambda$static$13_1_) ->
    {
        p_lambda$static$13_0_.chatScale = p_lambda$static$13_1_;
        Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
    }, (p_lambda$static$14_0_, p_lambda$static$14_1_) ->
    {
        double d0 = p_lambda$static$14_1_.normalizeValue(p_lambda$static$14_1_.get(p_lambda$static$14_0_));
        return (ITextComponent)(d0 == 0.0D ? DialogTexts.getComposedOptionMessage(p_lambda$static$14_1_.getBaseMessageTranslation(), false) : p_lambda$static$14_1_.getPercentValueComponent(d0));
    });
    public static final SliderPercentageOption CHAT_WIDTH = new SliderPercentageOption("options.chat.width", 0.0D, 1.0D, 0.0F, (p_lambda$static$15_0_) ->
    {
        return p_lambda$static$15_0_.chatWidth / 4.0571431D;
    }, (p_lambda$static$16_0_, p_lambda$static$16_1_) ->
    {
        p_lambda$static$16_1_ = p_lambda$static$16_1_ * 4.0571431D;
        p_lambda$static$16_0_.chatWidth = p_lambda$static$16_1_;
        Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
    }, (p_lambda$static$17_0_, p_lambda$static$17_1_) ->
    {
        double d0 = p_lambda$static$17_1_.normalizeValue(p_lambda$static$17_1_.get(p_lambda$static$17_0_));
        return p_lambda$static$17_1_.getPixelValueComponent(NewChatGui.calculateChatboxWidth(d0 * 4.0571431D));
    });
    public static final SliderPercentageOption LINE_SPACING = new SliderPercentageOption("options.chat.line_spacing", 0.0D, 1.0D, 0.0F, (p_lambda$static$18_0_) ->
    {
        return p_lambda$static$18_0_.chatLineSpacing;
    }, (p_lambda$static$19_0_, p_lambda$static$19_1_) ->
    {
        p_lambda$static$19_0_.chatLineSpacing = p_lambda$static$19_1_;
    }, (p_lambda$static$20_0_, p_lambda$static$20_1_) ->
    {
        return p_lambda$static$20_1_.getPercentValueComponent(p_lambda$static$20_1_.normalizeValue(p_lambda$static$20_1_.get(p_lambda$static$20_0_)));
    });
    public static final SliderPercentageOption DELAY_INSTANT = new SliderPercentageOption("options.chat.delay_instant", 0.0D, 6.0D, 0.1F, (p_lambda$static$21_0_) ->
    {
        return p_lambda$static$21_0_.chatDelay;
    }, (p_lambda$static$22_0_, p_lambda$static$22_1_) ->
    {
        p_lambda$static$22_0_.chatDelay = p_lambda$static$22_1_;
    }, (p_lambda$static$23_0_, p_lambda$static$23_1_) ->
    {
        double d0 = p_lambda$static$23_1_.get(p_lambda$static$23_0_);
        return d0 <= 0.0D ? new TranslationTextComponent("options.chat.delay_none") : new TranslationTextComponent("options.chat.delay", String.format("%.1f", d0));
    });
    public static final SliderPercentageOption FOV = new SliderPercentageOption("options.fov", 30.0D, 110.0D, 1.0F, (p_lambda$static$24_0_) ->
    {
        return p_lambda$static$24_0_.fov;
    }, (p_lambda$static$25_0_, p_lambda$static$25_1_) ->
    {
        p_lambda$static$25_0_.fov = p_lambda$static$25_1_;
    }, (p_lambda$static$26_0_, p_lambda$static$26_1_) ->
    {
        double d0 = p_lambda$static$26_1_.get(p_lambda$static$26_0_);

        if (d0 == 70.0D)
        {
            return p_lambda$static$26_1_.getGenericValueComponent(new TranslationTextComponent("options.fov.min"));
        }
        else {
            return d0 == p_lambda$static$26_1_.getMaxValue() ? p_lambda$static$26_1_.getGenericValueComponent(new TranslationTextComponent("options.fov.max")) : p_lambda$static$26_1_.getMessageWithValue((int)d0);
        }
    });
    private static final ITextComponent FOV_EFFECT_SCALE_TOOLTIP = new TranslationTextComponent("options.fovEffectScale.tooltip");
    public static final SliderPercentageOption FOV_EFFECT_SCALE_SLIDER = new SliderPercentageOption("options.fovEffectScale", 0.0D, 1.0D, 0.0F, (p_lambda$static$27_0_) ->
    {
        return Math.pow((double)p_lambda$static$27_0_.fovScaleEffect, 2.0D);
    }, (p_lambda$static$28_0_, p_lambda$static$28_1_) ->
    {
        p_lambda$static$28_0_.fovScaleEffect = MathHelper.sqrt(p_lambda$static$28_1_);
    }, (p_lambda$static$29_0_, p_lambda$static$29_1_) ->
    {
        p_lambda$static$29_1_.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(FOV_EFFECT_SCALE_TOOLTIP, 200));
        double d0 = p_lambda$static$29_1_.normalizeValue(p_lambda$static$29_1_.get(p_lambda$static$29_0_));
        return d0 == 0.0D ? p_lambda$static$29_1_.getGenericValueComponent(new TranslationTextComponent("options.fovEffectScale.off")) : p_lambda$static$29_1_.getPercentValueComponent(d0);
    });
    private static final ITextComponent SCREEN_EFFECT_SCALE_TOOLTIP = new TranslationTextComponent("options.screenEffectScale.tooltip");
    public static final SliderPercentageOption SCREEN_EFFECT_SCALE_SLIDER = new SliderPercentageOption("options.screenEffectScale", 0.0D, 1.0D, 0.0F, (p_lambda$static$30_0_) ->
    {
        return (double)p_lambda$static$30_0_.screenEffectScale;
    }, (p_lambda$static$31_0_, p_lambda$static$31_1_) ->
    {
        p_lambda$static$31_0_.screenEffectScale = p_lambda$static$31_1_.floatValue();
    }, (p_lambda$static$32_0_, p_lambda$static$32_1_) ->
    {
        p_lambda$static$32_1_.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(SCREEN_EFFECT_SCALE_TOOLTIP, 200));
        double d0 = p_lambda$static$32_1_.normalizeValue(p_lambda$static$32_1_.get(p_lambda$static$32_0_));
        return d0 == 0.0D ? p_lambda$static$32_1_.getGenericValueComponent(new TranslationTextComponent("options.screenEffectScale.off")) : p_lambda$static$32_1_.getPercentValueComponent(d0);
    });
    public static final SliderPercentageOption FRAMERATE_LIMIT = new SliderPercentageOption("options.framerateLimit", 0.0D, 260.0D, 5.0F, (p_lambda$static$33_0_) ->
    {
        return p_lambda$static$33_0_.vsync ? 0: (double)p_lambda$static$33_0_.framerateLimit;
    }, (p_lambda$static$34_0_, p_lambda$static$34_1_) ->
    {
        p_lambda$static$34_0_.framerateLimit = (int)p_lambda$static$34_1_.doubleValue();
        p_lambda$static$34_0_.vsync = false;

        if (p_lambda$static$34_0_.framerateLimit <= 0)
        {
            p_lambda$static$34_0_.framerateLimit = 260;
            p_lambda$static$34_0_.vsync = true;
        }

        p_lambda$static$34_0_.updateVSync();
        Minecraft.getInstance().getMainWindow().setFramerateLimit(p_lambda$static$34_0_.framerateLimit);
    }, (p_lambda$static$35_0_, p_lambda$static$35_1_) ->
    {
        if (p_lambda$static$35_0_.vsync)
        {
            return p_lambda$static$35_1_.getGenericValueComponent(new TranslationTextComponent("of.options.framerateLimit.vsync"));
        }
        else {
            double d0 = p_lambda$static$35_1_.get(p_lambda$static$35_0_);
            return d0 == p_lambda$static$35_1_.getMaxValue() ? p_lambda$static$35_1_.getGenericValueComponent(new TranslationTextComponent("options.framerateLimit.max")) : p_lambda$static$35_1_.getGenericValueComponent(new TranslationTextComponent("options.framerate", (int)d0));
        }
    });
    public static final SliderPercentageOption GAMMA = new SliderPercentageOption("options.gamma", 0.0D, 1.0D, 0.0F, (p_lambda$static$36_0_) ->
    {
        return p_lambda$static$36_0_.gamma;
    }, (p_lambda$static$37_0_, p_lambda$static$37_1_) ->
    {
        p_lambda$static$37_0_.gamma = p_lambda$static$37_1_;
    }, (p_lambda$static$38_0_, p_lambda$static$38_1_) ->
    {
        double d0 = p_lambda$static$38_1_.normalizeValue(p_lambda$static$38_1_.get(p_lambda$static$38_0_));

        if (d0 == 0.0D)
        {
            return p_lambda$static$38_1_.getGenericValueComponent(new TranslationTextComponent("options.gamma.min"));
        }
        else {
            return d0 == 1.0D ? p_lambda$static$38_1_.getGenericValueComponent(new TranslationTextComponent("options.gamma.max")) : p_lambda$static$38_1_.getPercentageAddMessage((int)(d0 * 100.0D));
        }
    });
    public static final SliderPercentageOption MIPMAP_LEVELS = new SliderPercentageOption("options.mipmapLevels", 0.0D, 4.0D, 1.0F, (p_lambda$static$39_0_) ->
    {
        return (double)p_lambda$static$39_0_.mipmapLevels;
    }, (p_lambda$static$40_0_, p_lambda$static$40_1_) ->
    {
        p_lambda$static$40_0_.mipmapLevels = (int)p_lambda$static$40_1_.doubleValue();
        p_lambda$static$40_0_.updateMipmaps();
    }, (p_lambda$static$41_0_, p_lambda$static$41_1_) ->
    {
        double d0 = p_lambda$static$41_1_.get(p_lambda$static$41_0_);

        if (d0 >= 4.0D)
        {
            return p_lambda$static$41_1_.getGenericValueComponent(new TranslationTextComponent("of.general.max"));
        }
        else {
            return (ITextComponent)(d0 == 0.0D ? DialogTexts.getComposedOptionMessage(p_lambda$static$41_1_.getBaseMessageTranslation(), false) : p_lambda$static$41_1_.getMessageWithValue((int)d0));
        }
    });
    public static final SliderPercentageOption MOUSE_WHEEL_SENSITIVITY = new SliderMultiplierOption("options.mouseWheelSensitivity", 0.01D, 10.0D, 0.01F, (p_lambda$static$42_0_) ->
    {
        return p_lambda$static$42_0_.mouseWheelSensitivity;
    }, (p_lambda$static$43_0_, p_lambda$static$43_1_) ->
    {
        p_lambda$static$43_0_.mouseWheelSensitivity = p_lambda$static$43_1_;
    }, (p_lambda$static$44_0_, p_lambda$static$44_1_) ->
    {
        double d0 = p_lambda$static$44_1_.normalizeValue(p_lambda$static$44_1_.get(p_lambda$static$44_0_));
        return p_lambda$static$44_1_.getGenericValueComponent(new StringTextComponent(String.format("%.2f", p_lambda$static$44_1_.denormalizeValue(d0))));
    });
    public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", (p_lambda$static$45_0_) ->
    {
        return p_lambda$static$45_0_.rawMouseInput;
    }, (p_lambda$static$46_0_, p_lambda$static$46_1_) ->
    {
        p_lambda$static$46_0_.rawMouseInput = p_lambda$static$46_1_;
        MainWindow mainwindow = Minecraft.getInstance().getMainWindow();

        if (mainwindow != null)
        {
            mainwindow.setRawMouseInput(p_lambda$static$46_1_);
        }
    });
    public static final SliderPercentageOption RENDER_DISTANCE = new SliderPercentageOption("options.renderDistance", 2.0D, 16.0D, 1.0F, (p_lambda$static$47_0_) ->
    {
        return (double)p_lambda$static$47_0_.renderDistanceChunks;
    }, (p_lambda$static$48_0_, p_lambda$static$48_1_) ->
    {
        p_lambda$static$48_0_.renderDistanceChunks = (int)p_lambda$static$48_1_.doubleValue();
        Minecraft.getInstance().worldRenderer.setDisplayListEntitiesDirty();
    }, (p_lambda$static$49_0_, p_lambda$static$49_1_) ->
    {
        double d0 = p_lambda$static$49_1_.get(p_lambda$static$49_0_);
        return p_lambda$static$49_1_.getGenericValueComponent(new TranslationTextComponent("options.chunks", (int)d0));
    });
    public static final SliderPercentageOption ENTITY_DISTANCE_SCALING = new SliderPercentageOption("options.entityDistanceScaling", 0.5D, 5.0D, 0.25F, (p_lambda$static$50_0_) ->
    {
        return (double)p_lambda$static$50_0_.entityDistanceScaling;
    }, (p_lambda$static$51_0_, p_lambda$static$51_1_) ->
    {
        p_lambda$static$51_0_.entityDistanceScaling = (float)p_lambda$static$51_1_.doubleValue();
    }, (p_lambda$static$52_0_, p_lambda$static$52_1_) ->
    {
        double d0 = p_lambda$static$52_1_.get(p_lambda$static$52_0_);
        return p_lambda$static$52_1_.getPercentValueComponent(d0);
    });
    public static final SliderPercentageOption SENSITIVITY = new SliderPercentageOption("options.sensitivity", 0.0D, 1.0D, 0.0F, (p_lambda$static$53_0_) ->
    {
        return p_lambda$static$53_0_.mouseSensitivity;
    }, (p_lambda$static$54_0_, p_lambda$static$54_1_) ->
    {
        p_lambda$static$54_0_.mouseSensitivity = p_lambda$static$54_1_;
    }, (p_lambda$static$55_0_, p_lambda$static$55_1_) ->
    {
        double d0 = p_lambda$static$55_1_.normalizeValue(p_lambda$static$55_1_.get(p_lambda$static$55_0_));

        if (d0 == 0.0D)
        {
            return p_lambda$static$55_1_.getGenericValueComponent(new TranslationTextComponent("options.sensitivity.min"));
        }
        else {
            return d0 == 1.0D ? p_lambda$static$55_1_.getGenericValueComponent(new TranslationTextComponent("options.sensitivity.max")) : p_lambda$static$55_1_.getPercentValueComponent(2.0D * d0);
        }
    });
    public static final SliderPercentageOption ACCESSIBILITY_TEXT_BACKGROUND_OPACITY = new SliderPercentageOption("options.accessibility.text_background_opacity", 0.0D, 1.0D, 0.0F, (p_lambda$static$56_0_) ->
    {
        return p_lambda$static$56_0_.accessibilityTextBackgroundOpacity;
    }, (p_lambda$static$57_0_, p_lambda$static$57_1_) ->
    {
        p_lambda$static$57_0_.accessibilityTextBackgroundOpacity = p_lambda$static$57_1_;
        Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
    }, (p_lambda$static$58_0_, p_lambda$static$58_1_) ->
    {
        return p_lambda$static$58_1_.getPercentValueComponent(p_lambda$static$58_1_.normalizeValue(p_lambda$static$58_1_.get(p_lambda$static$58_0_)));
    });
    public static final IteratableOption AO = new IteratableOption("options.ao", (p_lambda$static$59_0_, p_lambda$static$59_1_) ->
    {
        p_lambda$static$59_0_.ambientOcclusionStatus = AmbientOcclusionStatus.getValue(p_lambda$static$59_0_.ambientOcclusionStatus.getId() + p_lambda$static$59_1_);
        Minecraft.getInstance().worldRenderer.loadRenderers();
    }, (p_lambda$static$60_0_, p_lambda$static$60_1_) ->
    {
        return p_lambda$static$60_1_.getGenericValueComponent(new TranslationTextComponent(p_lambda$static$60_0_.ambientOcclusionStatus.getResourceKey()));
    });
    public static final IteratableOption ATTACK_INDICATOR = new IteratableOption("options.attackIndicator", (p_lambda$static$61_0_, p_lambda$static$61_1_) ->
    {
        p_lambda$static$61_0_.attackIndicator = AttackIndicatorStatus.byId(p_lambda$static$61_0_.attackIndicator.getId() + p_lambda$static$61_1_);
    }, (p_lambda$static$62_0_, p_lambda$static$62_1_) ->
    {
        return p_lambda$static$62_1_.getGenericValueComponent(new TranslationTextComponent(p_lambda$static$62_0_.attackIndicator.getResourceKey()));
    });
    public static final IteratableOption CHAT_VISIBILITY = new IteratableOption("options.chat.visibility", (p_lambda$static$63_0_, p_lambda$static$63_1_) ->
    {
        p_lambda$static$63_0_.chatVisibility = ChatVisibility.getValue((p_lambda$static$63_0_.chatVisibility.getId() + p_lambda$static$63_1_) % 3);
    }, (p_lambda$static$64_0_, p_lambda$static$64_1_) ->
    {
        return p_lambda$static$64_1_.getGenericValueComponent(new TranslationTextComponent(p_lambda$static$64_0_.chatVisibility.getResourceKey()));
    });
    private static final ITextComponent FAST_GRAPHICS = new TranslationTextComponent("options.graphics.fast.tooltip");
    private static final ITextComponent FABULOUS_GRAPHICS = new TranslationTextComponent("options.graphics.fabulous.tooltip", (new TranslationTextComponent("options.graphics.fabulous")).mergeStyle(TextFormatting.ITALIC));
    private static final ITextComponent FANCY_GRAPHICS = new TranslationTextComponent("options.graphics.fancy.tooltip");
    public static final IteratableOption GRAPHICS = new IteratableOption("options.graphics", (p_lambda$static$65_0_, p_lambda$static$65_1_) ->
    {
        Minecraft minecraft = Minecraft.getInstance();
        GPUWarning gpuwarning = minecraft.getGPUWarning();

        if (p_lambda$static$65_0_.graphicFanciness == GraphicsFanciness.FANCY && gpuwarning.func_241695_b_())
        {
            gpuwarning.func_241697_d_();
        }
        else {
            p_lambda$static$65_0_.graphicFanciness = p_lambda$static$65_0_.graphicFanciness.func_238166_c_();

            if (p_lambda$static$65_0_.graphicFanciness == GraphicsFanciness.FABULOUS && (Config.isShaders() || !GLX.isUsingFBOs() || !GlStateManager.isFabulous() || gpuwarning.func_241701_h_()))
            {
                p_lambda$static$65_0_.graphicFanciness = GraphicsFanciness.FAST;
            }

            p_lambda$static$65_0_.updateRenderClouds();
            minecraft.worldRenderer.loadRenderers();
        }
    }, (p_lambda$static$66_0_, p_lambda$static$66_1_) ->
    {
        switch (p_lambda$static$66_0_.graphicFanciness)
        {
            case FAST:
                p_lambda$static$66_1_.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(FAST_GRAPHICS, 200));
                break;

            case FANCY:
                p_lambda$static$66_1_.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(FANCY_GRAPHICS, 200));
                break;

            case FABULOUS:
                p_lambda$static$66_1_.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(FABULOUS_GRAPHICS, 200));
        }

        IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(p_lambda$static$66_0_.graphicFanciness.func_238164_b_());
        return p_lambda$static$66_0_.graphicFanciness == GraphicsFanciness.FABULOUS ? p_lambda$static$66_1_.getGenericValueComponent(iformattabletextcomponent.mergeStyle(TextFormatting.ITALIC)) : p_lambda$static$66_1_.getGenericValueComponent(iformattabletextcomponent);
    });
    public static final IteratableOption GUI_SCALE = new IteratableOption("options.guiScale", (p_lambda$static$67_0_, p_lambda$static$67_1_) ->
    {
        p_lambda$static$67_0_.guiScale = MathHelper.normalizeAngle(p_lambda$static$67_0_.guiScale + p_lambda$static$67_1_, Minecraft.getInstance().getMainWindow().calcGuiScale(0, Minecraft.getInstance().getForceUnicodeFont()) + 1);
    }, (p_lambda$static$68_0_, p_lambda$static$68_1_) ->
    {
        return p_lambda$static$68_0_.guiScale == 0 ? p_lambda$static$68_1_.getGenericValueComponent(new TranslationTextComponent("options.guiScale.auto")) : p_lambda$static$68_1_.getMessageWithValue(p_lambda$static$68_0_.guiScale);
    });
    public static final IteratableOption MAIN_HAND = new IteratableOption("options.mainHand", (p_lambda$static$69_0_, p_lambda$static$69_1_) ->
    {
        p_lambda$static$69_0_.mainHand = p_lambda$static$69_0_.mainHand.opposite();
    }, (p_lambda$static$70_0_, p_lambda$static$70_1_) ->
    {
        return p_lambda$static$70_1_.getGenericValueComponent(p_lambda$static$70_0_.mainHand.getHandName());
    });
    public static final IteratableOption NARRATOR = new IteratableOption("options.narrator", (p_lambda$static$71_0_, p_lambda$static$71_1_) ->
    {
        if (NarratorChatListener.INSTANCE.isActive())
        {
            p_lambda$static$71_0_.narrator = NarratorStatus.byId(p_lambda$static$71_0_.narrator.getId() + p_lambda$static$71_1_);
        }
        else {
            p_lambda$static$71_0_.narrator = NarratorStatus.OFF;
        }

        NarratorChatListener.INSTANCE.announceMode(p_lambda$static$71_0_.narrator);
    }, (p_lambda$static$72_0_, p_lambda$static$72_1_) ->
    {
        return NarratorChatListener.INSTANCE.isActive() ? p_lambda$static$72_1_.getGenericValueComponent(p_lambda$static$72_0_.narrator.func_238233_b_()) : p_lambda$static$72_1_.getGenericValueComponent(new TranslationTextComponent("options.narrator.notavailable"));
    });
    public static final IteratableOption PARTICLES = new IteratableOption("options.particles", (p_lambda$static$73_0_, p_lambda$static$73_1_) ->
    {
        p_lambda$static$73_0_.particles = ParticleStatus.byId(p_lambda$static$73_0_.particles.getId() + p_lambda$static$73_1_);
    }, (p_lambda$static$74_0_, p_lambda$static$74_1_) ->
    {
        return p_lambda$static$74_1_.getGenericValueComponent(new TranslationTextComponent(p_lambda$static$74_0_.particles.getResourceKey()));
    });
    public static final IteratableOption RENDER_CLOUDS = new IteratableOption("options.renderClouds", (p_lambda$static$75_0_, p_lambda$static$75_1_) ->
    {
        p_lambda$static$75_0_.cloudOption = CloudOption.byId(p_lambda$static$75_0_.cloudOption.getId() + p_lambda$static$75_1_);

        if (Minecraft.isFabulousGraphicsEnabled())
        {
            Framebuffer framebuffer = Minecraft.getInstance().worldRenderer.func_239232_u_();

            if (framebuffer != null)
            {
                framebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
            }
        }
    }, (p_lambda$static$76_0_, p_lambda$static$76_1_) ->
    {
        return p_lambda$static$76_1_.getGenericValueComponent(new TranslationTextComponent(p_lambda$static$76_0_.cloudOption.getKey()));
    });
    public static final IteratableOption ACCESSIBILITY_TEXT_BACKGROUND = new IteratableOption("options.accessibility.text_background", (p_lambda$static$77_0_, p_lambda$static$77_1_) ->
    {
        p_lambda$static$77_0_.accessibilityTextBackground = !p_lambda$static$77_0_.accessibilityTextBackground;
    }, (p_lambda$static$78_0_, p_lambda$static$78_1_) ->
    {
        return p_lambda$static$78_1_.getGenericValueComponent(new TranslationTextComponent(p_lambda$static$78_0_.accessibilityTextBackground ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere"));
    });
    private static final ITextComponent field_244787_ad = new TranslationTextComponent("options.hideMatchedNames.tooltip");
    public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", (p_lambda$static$79_0_) ->
    {
        return p_lambda$static$79_0_.autoJump;
    }, (p_lambda$static$80_0_, p_lambda$static$80_1_) ->
    {
        p_lambda$static$80_0_.autoJump = p_lambda$static$80_1_;
    });
    public static final BooleanOption AUTO_SUGGEST_COMMANDS = new BooleanOption("options.autoSuggestCommands", (p_lambda$static$81_0_) ->
    {
        return p_lambda$static$81_0_.autoSuggestCommands;
    }, (p_lambda$static$82_0_, p_lambda$static$82_1_) ->
    {
        p_lambda$static$82_0_.autoSuggestCommands = p_lambda$static$82_1_;
    });
    public static final BooleanOption field_244786_G = new BooleanOption("options.hideMatchedNames", field_244787_ad, (p_lambda$static$83_0_) ->
    {
        return p_lambda$static$83_0_.field_244794_ae;
    }, (p_lambda$static$84_0_, p_lambda$static$84_1_) ->
    {
        p_lambda$static$84_0_.field_244794_ae = p_lambda$static$84_1_;
    });
    public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", (p_lambda$static$85_0_) ->
    {
        return p_lambda$static$85_0_.chatColor;
    }, (p_lambda$static$86_0_, p_lambda$static$86_1_) ->
    {
        p_lambda$static$86_0_.chatColor = p_lambda$static$86_1_;
    });
    public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", (p_lambda$static$87_0_) ->
    {
        return p_lambda$static$87_0_.chatLinks;
    }, (p_lambda$static$88_0_, p_lambda$static$88_1_) ->
    {
        p_lambda$static$88_0_.chatLinks = p_lambda$static$88_1_;
    });
    public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", (p_lambda$static$89_0_) ->
    {
        return p_lambda$static$89_0_.chatLinksPrompt;
    }, (p_lambda$static$90_0_, p_lambda$static$90_1_) ->
    {
        p_lambda$static$90_0_.chatLinksPrompt = p_lambda$static$90_1_;
    });
    public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", (p_lambda$static$91_0_) ->
    {
        return p_lambda$static$91_0_.discreteMouseScroll;
    }, (p_lambda$static$92_0_, p_lambda$static$92_1_) ->
    {
        p_lambda$static$92_0_.discreteMouseScroll = p_lambda$static$92_1_;
    });
    public static final BooleanOption VSYNC = new BooleanOption("options.vsync", (p_lambda$static$93_0_) ->
    {
        return p_lambda$static$93_0_.vsync;
    }, (p_lambda$static$94_0_, p_lambda$static$94_1_) ->
    {
        p_lambda$static$94_0_.vsync = p_lambda$static$94_1_;

        if (Minecraft.getInstance().getMainWindow() != null)
        {
            Minecraft.getInstance().getMainWindow().setVsync(p_lambda$static$94_0_.vsync);
        }
    });
    public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", (p_lambda$static$95_0_) ->
    {
        return p_lambda$static$95_0_.entityShadows;
    }, (p_lambda$static$96_0_, p_lambda$static$96_1_) ->
    {
        p_lambda$static$96_0_.entityShadows = p_lambda$static$96_1_;
    });
    public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", (p_lambda$static$97_0_) ->
    {
        return p_lambda$static$97_0_.forceUnicodeFont;
    }, (p_lambda$static$98_0_, p_lambda$static$98_1_) ->
    {
        p_lambda$static$98_0_.forceUnicodeFont = p_lambda$static$98_1_;
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.getMainWindow() != null)
        {
            minecraft.forceUnicodeFont(p_lambda$static$98_1_);
        }
    });
    public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", (p_lambda$static$99_0_) ->
    {
        return p_lambda$static$99_0_.invertMouse;
    }, (p_lambda$static$100_0_, p_lambda$static$100_1_) ->
    {
        p_lambda$static$100_0_.invertMouse = p_lambda$static$100_1_;
    });
    public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", (p_lambda$static$101_0_) ->
    {
        return p_lambda$static$101_0_.realmsNotifications;
    }, (p_lambda$static$102_0_, p_lambda$static$102_1_) ->
    {
        p_lambda$static$102_0_.realmsNotifications = p_lambda$static$102_1_;
    });
    public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", (p_lambda$static$103_0_) ->
    {
        return p_lambda$static$103_0_.reducedDebugInfo;
    }, (p_lambda$static$104_0_, p_lambda$static$104_1_) ->
    {
        p_lambda$static$104_0_.reducedDebugInfo = p_lambda$static$104_1_;
    });
    public static final BooleanOption SHOW_SUBTITLES = new BooleanOption("options.showSubtitles", (p_lambda$static$105_0_) ->
    {
        return p_lambda$static$105_0_.showSubtitles;
    }, (p_lambda$static$106_0_, p_lambda$static$106_1_) ->
    {
        p_lambda$static$106_0_.showSubtitles = p_lambda$static$106_1_;
    });
    public static final BooleanOption SNOOPER = new BooleanOption("options.snooper", (p_lambda$static$107_0_) ->
    {
        if (p_lambda$static$107_0_.snooper)
        {
        }

        return false;
    }, (p_lambda$static$108_0_, p_lambda$static$108_1_) ->
    {
        p_lambda$static$108_0_.snooper = p_lambda$static$108_1_;
    });
    public static final IteratableOption SNEAK = new IteratableOption("key.sneak", (p_lambda$static$109_0_, p_lambda$static$109_1_) ->
    {
        p_lambda$static$109_0_.toggleCrouch = !p_lambda$static$109_0_.toggleCrouch;
    }, (p_lambda$static$110_0_, p_lambda$static$110_1_) ->
    {
        return p_lambda$static$110_1_.getGenericValueComponent(new TranslationTextComponent(p_lambda$static$110_0_.toggleCrouch ? "options.key.toggle" : "options.key.hold"));
    });
    public static final IteratableOption SPRINT = new IteratableOption("key.sprint", (p_lambda$static$111_0_, p_lambda$static$111_1_) ->
    {
        p_lambda$static$111_0_.toggleSprint = !p_lambda$static$111_0_.toggleSprint;
    }, (p_lambda$static$112_0_, p_lambda$static$112_1_) ->
    {
        return p_lambda$static$112_1_.getGenericValueComponent(new TranslationTextComponent(p_lambda$static$112_0_.toggleSprint ? "options.key.toggle" : "options.key.hold"));
    });
    public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", (p_lambda$static$113_0_) ->
    {
        return p_lambda$static$113_0_.touchscreen;
    }, (p_lambda$static$114_0_, p_lambda$static$114_1_) ->
    {
        p_lambda$static$114_0_.touchscreen = p_lambda$static$114_1_;
    });
    public static final BooleanOption FULLSCREEN = new BooleanOption("options.fullscreen", (p_lambda$static$115_0_) ->
    {
        return p_lambda$static$115_0_.fullscreen;
    }, (p_lambda$static$116_0_, p_lambda$static$116_1_) ->
    {
        p_lambda$static$116_0_.fullscreen = p_lambda$static$116_1_;
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.getMainWindow() != null && minecraft.getMainWindow().isFullscreen() != p_lambda$static$116_0_.fullscreen)
        {
            minecraft.getMainWindow().toggleFullscreen();
            p_lambda$static$116_0_.fullscreen = minecraft.getMainWindow().isFullscreen();
        }
    });
    public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", (p_lambda$static$117_0_) ->
    {
        return p_lambda$static$117_0_.viewBobbing;
    }, (p_lambda$static$118_0_, p_lambda$static$118_1_) ->
    {
        p_lambda$static$118_0_.viewBobbing = p_lambda$static$118_1_;
    });
    private final ITextComponent translatedBaseMessage;
    private Optional<List<IReorderingProcessor>> optionValues = Optional.empty();
    private final String translationKey;
    public static final IteratableOption FOG_FANCY = new IteratableOptionOF("of.options.FOG_FANCY");
    public static final IteratableOption FOG_START = new IteratableOptionOF("of.options.FOG_START");
    public static final SliderPercentageOption MIPMAP_TYPE = new SliderPercentageOptionOF("of.options.MIPMAP_TYPE", 0.0D, 3.0D, 1.0F);
    public static final IteratableOption SMOOTH_FPS = new IteratableOptionOF("of.options.SMOOTH_FPS");
    public static final IteratableOption CLOUDS = new IteratableOptionOF("of.options.CLOUDS");
    public static final SliderPercentageOption CLOUD_HEIGHT = new SliderPercentageOptionOF("of.options.CLOUD_HEIGHT");
    public static final IteratableOption TREES = new IteratableOptionOF("of.options.TREES");
    public static final IteratableOption RAIN = new IteratableOptionOF("of.options.RAIN");
    public static final IteratableOption ANIMATED_WATER = new IteratableOptionOF("of.options.ANIMATED_WATER");
    public static final IteratableOption ANIMATED_LAVA = new IteratableOptionOF("of.options.ANIMATED_LAVA");
    public static final IteratableOption ANIMATED_FIRE = new IteratableOptionOF("of.options.ANIMATED_FIRE");
    public static final IteratableOption ANIMATED_PORTAL = new IteratableOptionOF("of.options.ANIMATED_PORTAL");
    public static final SliderPercentageOption AO_LEVEL = new SliderPercentageOptionOF("of.options.AO_LEVEL");
    public static final IteratableOption LAGOMETER = new IteratableOptionOF("of.options.LAGOMETER");
    public static final IteratableOption SHOW_FPS = new IteratableOptionOF("of.options.SHOW_FPS");
    public static final IteratableOption AUTOSAVE_TICKS = new IteratableOptionOF("of.options.AUTOSAVE_TICKS");
    public static final IteratableOption BETTER_GRASS = new IteratableOptionOF("of.options.BETTER_GRASS");
    public static final IteratableOption ANIMATED_REDSTONE = new IteratableOptionOF("of.options.ANIMATED_REDSTONE");
    public static final IteratableOption ANIMATED_EXPLOSION = new IteratableOptionOF("of.options.ANIMATED_EXPLOSION");
    public static final IteratableOption ANIMATED_FLAME = new IteratableOptionOF("of.options.ANIMATED_FLAME");
    public static final IteratableOption ANIMATED_SMOKE = new IteratableOptionOF("of.options.ANIMATED_SMOKE");
    public static final IteratableOption WEATHER = new IteratableOptionOF("of.options.WEATHER");
    public static final IteratableOption SKY = new IteratableOptionOF("of.options.SKY");
    public static final IteratableOption STARS = new IteratableOptionOF("of.options.STARS");
    public static final IteratableOption SUN_MOON = new IteratableOptionOF("of.options.SUN_MOON");
    public static final IteratableOption VIGNETTE = new IteratableOptionOF("of.options.VIGNETTE");
    public static final IteratableOption CHUNK_UPDATES = new IteratableOptionOF("of.options.CHUNK_UPDATES");
    public static final IteratableOption CHUNK_UPDATES_DYNAMIC = new IteratableOptionOF("of.options.CHUNK_UPDATES_DYNAMIC");
    public static final IteratableOption TIME = new IteratableOptionOF("of.options.TIME");
    public static final IteratableOption SMOOTH_WORLD = new IteratableOptionOF("of.options.SMOOTH_WORLD");
    public static final IteratableOption VOID_PARTICLES = new IteratableOptionOF("of.options.VOID_PARTICLES");
    public static final IteratableOption WATER_PARTICLES = new IteratableOptionOF("of.options.WATER_PARTICLES");
    public static final IteratableOption RAIN_SPLASH = new IteratableOptionOF("of.options.RAIN_SPLASH");
    public static final IteratableOption PORTAL_PARTICLES = new IteratableOptionOF("of.options.PORTAL_PARTICLES");
    public static final IteratableOption POTION_PARTICLES = new IteratableOptionOF("of.options.POTION_PARTICLES");
    public static final IteratableOption FIREWORK_PARTICLES = new IteratableOptionOF("of.options.FIREWORK_PARTICLES");
    public static final IteratableOption PROFILER = new IteratableOptionOF("of.options.PROFILER");
    public static final IteratableOption DRIPPING_WATER_LAVA = new IteratableOptionOF("of.options.DRIPPING_WATER_LAVA");
    public static final IteratableOption BETTER_SNOW = new IteratableOptionOF("of.options.BETTER_SNOW");
    public static final IteratableOption ANIMATED_TERRAIN = new IteratableOptionOF("of.options.ANIMATED_TERRAIN");
    public static final IteratableOption SWAMP_COLORS = new IteratableOptionOF("of.options.SWAMP_COLORS");
    public static final IteratableOption RANDOM_ENTITIES = new IteratableOptionOF("of.options.RANDOM_ENTITIES");
    public static final IteratableOption SMOOTH_BIOMES = new IteratableOptionOF("of.options.SMOOTH_BIOMES");
    public static final IteratableOption CUSTOM_FONTS = new IteratableOptionOF("of.options.CUSTOM_FONTS");
    public static final IteratableOption CUSTOM_COLORS = new IteratableOptionOF("of.options.CUSTOM_COLORS");
    public static final IteratableOption SHOW_CAPES = new IteratableOptionOF("of.options.SHOW_CAPES");
    public static final IteratableOption CONNECTED_TEXTURES = new IteratableOptionOF("of.options.CONNECTED_TEXTURES");
    public static final IteratableOption CUSTOM_ITEMS = new IteratableOptionOF("of.options.CUSTOM_ITEMS");
    public static final SliderPercentageOption AA_LEVEL = new SliderPercentageOptionOF("of.options.AA_LEVEL", 0.0D, 16.0D, new double[] {0.0D, 2.0D, 4.0D, 6.0D, 8.0D, 12.0D, 16.0D});
    public static final SliderPercentageOption AF_LEVEL = new SliderPercentageOptionOF("of.options.AF_LEVEL", 1.0D, 16.0D, new double[] {1.0D, 2.0D, 4.0D, 8.0D, 16.0D});
    public static final IteratableOption ANIMATED_TEXTURES = new IteratableOptionOF("of.options.ANIMATED_TEXTURES");
    public static final IteratableOption NATURAL_TEXTURES = new IteratableOptionOF("of.options.NATURAL_TEXTURES");
    public static final IteratableOption EMISSIVE_TEXTURES = new IteratableOptionOF("of.options.EMISSIVE_TEXTURES");
    public static final IteratableOption HELD_ITEM_TOOLTIPS = new IteratableOptionOF("of.options.HELD_ITEM_TOOLTIPS");
    public static final IteratableOption DROPPED_ITEMS = new IteratableOptionOF("of.options.DROPPED_ITEMS");
    public static final IteratableOption LAZY_CHUNK_LOADING = new IteratableOptionOF("of.options.LAZY_CHUNK_LOADING");
    public static final IteratableOption CUSTOM_SKY = new IteratableOptionOF("of.options.CUSTOM_SKY");
    public static final IteratableOption FAST_MATH = new IteratableOptionOF("of.options.FAST_MATH");
    public static final IteratableOption FAST_RENDER = new IteratableOptionOF("of.options.FAST_RENDER");
    public static final IteratableOption TRANSLUCENT_BLOCKS = new IteratableOptionOF("of.options.TRANSLUCENT_BLOCKS");
    public static final IteratableOption DYNAMIC_FOV = new IteratableOptionOF("of.options.DYNAMIC_FOV");
    public static final IteratableOption DYNAMIC_LIGHTS = new IteratableOptionOF("of.options.DYNAMIC_LIGHTS");
    public static final IteratableOption ALTERNATE_BLOCKS = new IteratableOptionOF("of.options.ALTERNATE_BLOCKS");
    public static final IteratableOption CUSTOM_ENTITY_MODELS = new IteratableOptionOF("of.options.CUSTOM_ENTITY_MODELS");
    public static final IteratableOption ADVANCED_TOOLTIPS = new IteratableOptionOF("of.options.ADVANCED_TOOLTIPS");
    public static final IteratableOption SCREENSHOT_SIZE = new IteratableOptionOF("of.options.SCREENSHOT_SIZE");
    public static final IteratableOption CUSTOM_GUIS = new IteratableOptionOF("of.options.CUSTOM_GUIS");
    public static final IteratableOption RENDER_REGIONS = new IteratableOptionOF("of.options.RENDER_REGIONS");
    public static final IteratableOption SHOW_GL_ERRORS = new IteratableOptionOF("of.options.SHOW_GL_ERRORS");
    public static final IteratableOption SMART_ANIMATIONS = new IteratableOptionOF("of.options.SMART_ANIMATIONS");
    public static final IteratableOption CHAT_BACKGROUND = new IteratableOptionOF("of.options.CHAT_BACKGROUND");
    public static final IteratableOption CHAT_SHADOW = new IteratableOptionOF("of.options.CHAT_SHADOW");

    public AbstractOption(String translationKeyIn)
    {
        this.translatedBaseMessage = new TranslationTextComponent(translationKeyIn);
        this.translationKey = translationKeyIn;
    }

    public abstract Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn);

    public ITextComponent getBaseMessageTranslation()
    {
        return this.translatedBaseMessage;
    }

    public void setOptionValues(List<IReorderingProcessor> values)
    {
        this.optionValues = Optional.of(values);
    }

    public Optional<List<IReorderingProcessor>> getOptionValues()
    {
        return this.optionValues;
    }

    protected ITextComponent getPixelValueComponent(int value)
    {
        return new TranslationTextComponent("options.pixel_value", this.getBaseMessageTranslation(), value);
    }

    protected ITextComponent getPercentValueComponent(double percentage)
    {
        return new TranslationTextComponent("options.percent_value", this.getBaseMessageTranslation(), (int)(percentage * 100.0D));
    }

    protected ITextComponent getPercentageAddMessage(int doubleIn)
    {
        return new TranslationTextComponent("options.percent_add_value", this.getBaseMessageTranslation(), doubleIn);
    }

    public ITextComponent getGenericValueComponent(ITextComponent valueMessage)
    {
        return new TranslationTextComponent("options.generic_value", this.getBaseMessageTranslation(), valueMessage);
    }

    public ITextComponent getMessageWithValue(int value)
    {
        return this.getGenericValueComponent(new StringTextComponent(Integer.toString(value)));
    }

    public String getResourceKey()
    {
        return this.translationKey;
    }
}
