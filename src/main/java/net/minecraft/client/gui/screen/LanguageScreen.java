package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nullable;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class LanguageScreen extends SettingsScreen
{
    private static final ITextComponent field_243292_c = (new StringTextComponent("(")).append(new TranslationTextComponent("options.languageWarning")).appendString(")").mergeStyle(TextFormatting.GRAY);

    /** The List GuiSlot object reference. */
    private LanguageScreen.List list;

    /** Reference to the LanguageManager object. */
    private final LanguageManager languageManager;
    private OptionButton field_211832_i;

    /** The button to confirm the current settings. */
    private Button confirmSettingsBtn;

    public LanguageScreen(Screen screen, GameSettings gameSettingsObj, LanguageManager manager)
    {
        super(screen, gameSettingsObj, new TranslationTextComponent("options.language"));
        this.languageManager = manager;
    }

    protected void init()
    {
        this.list = new LanguageScreen.List(this.mc);
        this.children.add(this.list);
        this.field_211832_i = this.addButton(new OptionButton(this.width / 2 - 155, this.height - 38, 150, 20, AbstractOption.FORCE_UNICODE_FONT, AbstractOption.FORCE_UNICODE_FONT.func_238152_c_(this.gameSettings), (p_213037_1_) ->
        {
            AbstractOption.FORCE_UNICODE_FONT.nextValue(this.gameSettings);
            this.gameSettings.saveOptions();
            p_213037_1_.setMessage(AbstractOption.FORCE_UNICODE_FONT.func_238152_c_(this.gameSettings));
            this.mc.updateWindowSize();
        }));
        this.confirmSettingsBtn = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 38, 150, 20, DialogTexts.GUI_DONE, (p_213036_1_) ->
        {
            LanguageScreen.List.LanguageEntry languagescreen$list$languageentry = this.list.getSelected();

            if (languagescreen$list$languageentry != null && !languagescreen$list$languageentry.field_214398_b.getCode().equals(this.languageManager.getCurrentLanguage().getCode()))
            {
                this.languageManager.setCurrentLanguage(languagescreen$list$languageentry.field_214398_b);
                this.gameSettings.language = languagescreen$list$languageentry.field_214398_b.getCode();
                this.mc.reloadResources();
                this.confirmSettingsBtn.setMessage(DialogTexts.GUI_DONE);
                this.field_211832_i.setMessage(AbstractOption.FORCE_UNICODE_FONT.func_238152_c_(this.gameSettings));
                this.gameSettings.saveOptions();
            }

            this.mc.displayGuiScreen(this.parentScreen);
        }));
        super.init();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.list.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 16, 16777215);
        drawCenteredString(matrixStack, this.font, field_243292_c, this.width / 2, this.height - 56, 8421504);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    class List extends ExtendedList<LanguageScreen.List.LanguageEntry>
    {
        public List(Minecraft mcIn)
        {
            super(mcIn, LanguageScreen.this.width, LanguageScreen.this.height, 32, LanguageScreen.this.height - 65 + 4, 18);

            for (Language language : LanguageScreen.this.languageManager.getLanguages())
            {
                LanguageScreen.List.LanguageEntry languagescreen$list$languageentry = new LanguageScreen.List.LanguageEntry(language);
                this.addEntry(languagescreen$list$languageentry);

                if (LanguageScreen.this.languageManager.getCurrentLanguage().getCode().equals(language.getCode()))
                {
                    this.setSelected(languagescreen$list$languageentry);
                }
            }

            if (this.getSelected() != null)
            {
                this.centerScrollOn(this.getSelected());
            }
        }

        protected int getScrollbarPosition()
        {
            return super.getScrollbarPosition() + 20;
        }

        public int getRowWidth()
        {
            return super.getRowWidth() + 50;
        }

        public void setSelected(@Nullable LanguageScreen.List.LanguageEntry entry)
        {
            super.setSelected(entry);

            if (entry != null)
            {
                NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", entry.field_214398_b)).getString());
            }
        }

        protected void renderBackground(MatrixStack p_230433_1_)
        {
            LanguageScreen.this.renderBackground(p_230433_1_);
        }

        protected boolean isFocused()
        {
            return LanguageScreen.this.getListener() == this;
        }

        public class LanguageEntry extends ExtendedList.AbstractListEntry<LanguageScreen.List.LanguageEntry>
        {
            private final Language field_214398_b;

            public LanguageEntry(Language p_i50494_2_)
            {
                this.field_214398_b = p_i50494_2_;
            }

            public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
            {
                String s = this.field_214398_b.toString();
                LanguageScreen.this.font.func_238406_a_(p_230432_1_, s, (float)(List.this.width / 2 - LanguageScreen.this.font.getStringWidth(s) / 2), (float)(p_230432_3_ + 1), 16777215, true);
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button)
            {
                if (button == 0)
                {
                    this.func_214395_a();
                    return true;
                }
                else
                {
                    return false;
                }
            }

            private void func_214395_a()
            {
                List.this.setSelected(this);
            }
        }
    }
}
