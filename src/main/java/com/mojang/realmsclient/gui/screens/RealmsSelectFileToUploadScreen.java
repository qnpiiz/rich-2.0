package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.WorldSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSelectFileToUploadScreen extends RealmsScreen
{
    private static final Logger field_224547_a = LogManager.getLogger();
    private static final ITextComponent field_243147_b = new TranslationTextComponent("selectWorld.world");
    private static final ITextComponent field_243148_c = new TranslationTextComponent("selectWorld.conversion");
    private static final ITextComponent field_243149_p = (new TranslationTextComponent("mco.upload.hardcore")).mergeStyle(TextFormatting.DARK_RED);
    private static final ITextComponent field_243150_q = new TranslationTextComponent("selectWorld.cheats");
    private static final DateFormat field_224552_f = new SimpleDateFormat();
    private final RealmsResetWorldScreen field_224548_b;
    private final long field_224549_c;
    private final int field_224550_d;
    private Button field_224551_e;
    private List<WorldSummary> field_224553_g = Lists.newArrayList();
    private int field_224554_h = -1;
    private RealmsSelectFileToUploadScreen.WorldSelectionList field_224555_i;
    private RealmsLabel field_224559_m;
    private RealmsLabel field_224560_n;
    private RealmsLabel field_224561_o;
    private final Runnable field_237967_A_;

    public RealmsSelectFileToUploadScreen(long p_i232219_1_, int p_i232219_3_, RealmsResetWorldScreen p_i232219_4_, Runnable p_i232219_5_)
    {
        this.field_224548_b = p_i232219_4_;
        this.field_224549_c = p_i232219_1_;
        this.field_224550_d = p_i232219_3_;
        this.field_237967_A_ = p_i232219_5_;
    }

    private void func_224541_a() throws Exception
    {
        this.field_224553_g = this.mc.getSaveLoader().getSaveList().stream().sorted((p_237970_0_, p_237970_1_) ->
        {
            if (p_237970_0_.getLastTimePlayed() < p_237970_1_.getLastTimePlayed())
            {
                return 1;
            }
            else {
                return p_237970_0_.getLastTimePlayed() > p_237970_1_.getLastTimePlayed() ? -1 : p_237970_0_.getFileName().compareTo(p_237970_1_.getFileName());
            }
        }).collect(Collectors.toList());

        for (WorldSummary worldsummary : this.field_224553_g)
        {
            this.field_224555_i.func_237986_a_(worldsummary);
        }
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224555_i = new RealmsSelectFileToUploadScreen.WorldSelectionList();

        try
        {
            this.func_224541_a();
        }
        catch (Exception exception)
        {
            field_224547_a.error("Couldn't load level list", (Throwable)exception);
            this.mc.displayGuiScreen(new RealmsGenericErrorScreen(new StringTextComponent("Unable to load worlds"), ITextComponent.getTextComponentOrEmpty(exception.getMessage()), this.field_224548_b));
            return;
        }

        this.addListener(this.field_224555_i);
        this.field_224551_e = this.addButton(new Button(this.width / 2 - 154, this.height - 32, 153, 20, new TranslationTextComponent("mco.upload.button.name"), (p_237976_1_) ->
        {
            this.func_224544_b();
        }));
        this.field_224551_e.active = this.field_224554_h >= 0 && this.field_224554_h < this.field_224553_g.size();
        this.addButton(new Button(this.width / 2 + 6, this.height - 32, 153, 20, DialogTexts.GUI_BACK, (p_237973_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224548_b);
        }));
        this.field_224559_m = this.addListener(new RealmsLabel(new TranslationTextComponent("mco.upload.select.world.title"), this.width / 2, 13, 16777215));
        this.field_224560_n = this.addListener(new RealmsLabel(new TranslationTextComponent("mco.upload.select.world.subtitle"), this.width / 2, func_239562_k_(-1), 10526880));

        if (this.field_224553_g.isEmpty())
        {
            this.field_224561_o = this.addListener(new RealmsLabel(new TranslationTextComponent("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 16777215));
        }
        else
        {
            this.field_224561_o = null;
        }

        this.func_231411_u_();
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    private void func_224544_b()
    {
        if (this.field_224554_h != -1 && !this.field_224553_g.get(this.field_224554_h).isHardcoreModeEnabled())
        {
            WorldSummary worldsummary = this.field_224553_g.get(this.field_224554_h);
            this.mc.displayGuiScreen(new RealmsUploadScreen(this.field_224549_c, this.field_224550_d, this.field_224548_b, worldsummary, this.field_237967_A_));
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.field_224555_i.render(matrixStack, mouseX, mouseY, partialTicks);
        this.field_224559_m.func_239560_a_(this, matrixStack);
        this.field_224560_n.func_239560_a_(this, matrixStack);

        if (this.field_224561_o != null)
        {
            this.field_224561_o.func_239560_a_(this, matrixStack);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.displayGuiScreen(this.field_224548_b);
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private static ITextComponent func_237977_c_(WorldSummary p_237977_0_)
    {
        return p_237977_0_.getEnumGameType().getDisplayName();
    }

    private static String func_237979_d_(WorldSummary p_237979_0_)
    {
        return field_224552_f.format(new Date(p_237979_0_.getLastTimePlayed()));
    }

    class WorldSelectionEntry extends ExtendedList.AbstractListEntry<RealmsSelectFileToUploadScreen.WorldSelectionEntry>
    {
        private final WorldSummary field_223759_a;
        private final String field_243160_c;
        private final String field_243161_d;
        private final ITextComponent field_243162_e;

        public WorldSelectionEntry(WorldSummary p_i232220_2_)
        {
            this.field_223759_a = p_i232220_2_;
            this.field_243160_c = p_i232220_2_.getDisplayName();
            this.field_243161_d = p_i232220_2_.getFileName() + " (" + RealmsSelectFileToUploadScreen.func_237979_d_(p_i232220_2_) + ")";

            if (p_i232220_2_.requiresConversion())
            {
                this.field_243162_e = RealmsSelectFileToUploadScreen.field_243148_c;
            }
            else
            {
                ITextComponent itextcomponent;

                if (p_i232220_2_.isHardcoreModeEnabled())
                {
                    itextcomponent = RealmsSelectFileToUploadScreen.field_243149_p;
                }
                else
                {
                    itextcomponent = RealmsSelectFileToUploadScreen.func_237977_c_(p_i232220_2_);
                }

                if (p_i232220_2_.getCheatsEnabled())
                {
                    itextcomponent = itextcomponent.deepCopy().appendString(", ").append(RealmsSelectFileToUploadScreen.field_243150_q);
                }

                this.field_243162_e = itextcomponent;
            }
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            this.func_237985_a_(p_230432_1_, this.field_223759_a, p_230432_2_, p_230432_4_, p_230432_3_);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            RealmsSelectFileToUploadScreen.this.field_224555_i.func_231400_a_(RealmsSelectFileToUploadScreen.this.field_224553_g.indexOf(this.field_223759_a));
            return true;
        }

        protected void func_237985_a_(MatrixStack p_237985_1_, WorldSummary p_237985_2_, int p_237985_3_, int p_237985_4_, int p_237985_5_)
        {
            String s;

            if (this.field_243160_c.isEmpty())
            {
                s = RealmsSelectFileToUploadScreen.field_243147_b + " " + (p_237985_3_ + 1);
            }
            else
            {
                s = this.field_243160_c;
            }

            RealmsSelectFileToUploadScreen.this.font.drawString(p_237985_1_, s, (float)(p_237985_4_ + 2), (float)(p_237985_5_ + 1), 16777215);
            RealmsSelectFileToUploadScreen.this.font.drawString(p_237985_1_, this.field_243161_d, (float)(p_237985_4_ + 2), (float)(p_237985_5_ + 12), 8421504);
            RealmsSelectFileToUploadScreen.this.font.func_243248_b(p_237985_1_, this.field_243162_e, (float)(p_237985_4_ + 2), (float)(p_237985_5_ + 12 + 10), 8421504);
        }
    }

    class WorldSelectionList extends RealmsObjectSelectionList<RealmsSelectFileToUploadScreen.WorldSelectionEntry>
    {
        public WorldSelectionList()
        {
            super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.func_239562_k_(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
        }

        public void func_237986_a_(WorldSummary p_237986_1_)
        {
            this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldSelectionEntry(p_237986_1_));
        }

        public int getMaxPosition()
        {
            return RealmsSelectFileToUploadScreen.this.field_224553_g.size() * 36;
        }

        public boolean isFocused()
        {
            return RealmsSelectFileToUploadScreen.this.getListener() == this;
        }

        public void renderBackground(MatrixStack p_230433_1_)
        {
            RealmsSelectFileToUploadScreen.this.renderBackground(p_230433_1_);
        }

        public void func_231400_a_(int p_231400_1_)
        {
            this.func_239561_k_(p_231400_1_);

            if (p_231400_1_ != -1)
            {
                WorldSummary worldsummary = RealmsSelectFileToUploadScreen.this.field_224553_g.get(p_231400_1_);
                String s = I18n.format("narrator.select.list.position", p_231400_1_ + 1, RealmsSelectFileToUploadScreen.this.field_224553_g.size());
                String s1 = RealmsNarratorHelper.func_239552_b_(Arrays.asList(worldsummary.getDisplayName(), RealmsSelectFileToUploadScreen.func_237979_d_(worldsummary), RealmsSelectFileToUploadScreen.func_237977_c_(worldsummary).getString(), s));
                RealmsNarratorHelper.func_239550_a_(I18n.format("narrator.select", s1));
            }
        }

        public void setSelected(@Nullable RealmsSelectFileToUploadScreen.WorldSelectionEntry entry)
        {
            super.setSelected(entry);
            RealmsSelectFileToUploadScreen.this.field_224554_h = this.getEventListeners().indexOf(entry);
            RealmsSelectFileToUploadScreen.this.field_224551_e.active = RealmsSelectFileToUploadScreen.this.field_224554_h >= 0 && RealmsSelectFileToUploadScreen.this.field_224554_h < this.getItemCount() && !RealmsSelectFileToUploadScreen.this.field_224553_g.get(RealmsSelectFileToUploadScreen.this.field_224554_h).isHardcoreModeEnabled();
        }
    }
}
