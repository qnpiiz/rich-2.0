package net.minecraft.client.gui.widget.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class KeyBindingList extends AbstractOptionList<KeyBindingList.Entry>
{
    private final ControlsScreen controlsScreen;
    private int maxListLabelWidth;

    public KeyBindingList(ControlsScreen controls, Minecraft mcIn)
    {
        super(mcIn, controls.width + 45, controls.height, 43, controls.height - 32, 20);
        this.controlsScreen = controls;
        KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        Arrays.sort((Object[])akeybinding);
        String s = null;

        for (KeyBinding keybinding : akeybinding)
        {
            String s1 = keybinding.getKeyCategory();

            if (!s1.equals(s))
            {
                s = s1;
                this.addEntry(new KeyBindingList.CategoryEntry(new TranslationTextComponent(s1)));
            }

            ITextComponent itextcomponent = new TranslationTextComponent(keybinding.getKeyDescription());
            int i = mcIn.fontRenderer.getStringPropertyWidth(itextcomponent);

            if (i > this.maxListLabelWidth)
            {
                this.maxListLabelWidth = i;
            }

            this.addEntry(new KeyBindingList.KeyEntry(keybinding, itextcomponent));
        }
    }

    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 15;
    }

    public int getRowWidth()
    {
        return super.getRowWidth() + 32;
    }

    public class CategoryEntry extends KeyBindingList.Entry
    {
        private final ITextComponent labelText;
        private final int labelWidth;

        public CategoryEntry(ITextComponent p_i232280_2_)
        {
            this.labelText = p_i232280_2_;
            this.labelWidth = KeyBindingList.this.minecraft.fontRenderer.getStringPropertyWidth(this.labelText);
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            KeyBindingList.this.minecraft.fontRenderer.func_243248_b(p_230432_1_, this.labelText, (float)(KeyBindingList.this.minecraft.currentScreen.width / 2 - this.labelWidth / 2), (float)(p_230432_3_ + p_230432_6_ - 9 - 1), 16777215);
        }

        public boolean changeFocus(boolean focus)
        {
            return false;
        }

        public List <? extends IGuiEventListener > getEventListeners()
        {
            return Collections.emptyList();
        }
    }

    public abstract static class Entry extends AbstractOptionList.Entry<KeyBindingList.Entry>
    {
    }

    public class KeyEntry extends KeyBindingList.Entry
    {
        private final KeyBinding keybinding;
        private final ITextComponent keyDesc;
        private final Button btnChangeKeyBinding;
        private final Button btnReset;

        private KeyEntry(final KeyBinding p_i232281_2_, final ITextComponent p_i232281_3_)
        {
            this.keybinding = p_i232281_2_;
            this.keyDesc = p_i232281_3_;
            this.btnChangeKeyBinding = new Button(0, 0, 75, 20, p_i232281_3_, (p_214386_2_) ->
            {
                KeyBindingList.this.controlsScreen.buttonId = p_i232281_2_;
            })
            {
                protected IFormattableTextComponent getNarrationMessage()
                {
                    return p_i232281_2_.isInvalid() ? new TranslationTextComponent("narrator.controls.unbound", p_i232281_3_) : new TranslationTextComponent("narrator.controls.bound", p_i232281_3_, super.getNarrationMessage());
                }
            };
            this.btnReset = new Button(0, 0, 50, 20, new TranslationTextComponent("controls.reset"), (p_214387_2_) ->
            {
                KeyBindingList.this.minecraft.gameSettings.setKeyBindingCode(p_i232281_2_, p_i232281_2_.getDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
            })
            {
                protected IFormattableTextComponent getNarrationMessage()
                {
                    return new TranslationTextComponent("narrator.controls.reset", p_i232281_3_);
                }
            };
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            boolean flag = KeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            KeyBindingList.this.minecraft.fontRenderer.func_243248_b(p_230432_1_, this.keyDesc, (float)(p_230432_4_ + 90 - KeyBindingList.this.maxListLabelWidth), (float)(p_230432_3_ + p_230432_6_ / 2 - 9 / 2), 16777215);
            this.btnReset.x = p_230432_4_ + 190;
            this.btnReset.y = p_230432_3_;
            this.btnReset.active = !this.keybinding.isDefault();
            this.btnReset.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
            this.btnChangeKeyBinding.x = p_230432_4_ + 105;
            this.btnChangeKeyBinding.y = p_230432_3_;
            this.btnChangeKeyBinding.setMessage(this.keybinding.func_238171_j_());
            boolean flag1 = false;

            if (!this.keybinding.isInvalid())
            {
                for (KeyBinding keybinding : KeyBindingList.this.minecraft.gameSettings.keyBindings)
                {
                    if (keybinding != this.keybinding && this.keybinding.conflicts(keybinding))
                    {
                        flag1 = true;
                        break;
                    }
                }
            }

            if (flag)
            {
                this.btnChangeKeyBinding.setMessage((new StringTextComponent("> ")).append(this.btnChangeKeyBinding.getMessage().deepCopy().mergeStyle(TextFormatting.YELLOW)).appendString(" <").mergeStyle(TextFormatting.YELLOW));
            }
            else if (flag1)
            {
                this.btnChangeKeyBinding.setMessage(this.btnChangeKeyBinding.getMessage().deepCopy().mergeStyle(TextFormatting.RED));
            }

            this.btnChangeKeyBinding.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
        }

        public List <? extends IGuiEventListener > getEventListeners()
        {
            return ImmutableList.of(this.btnChangeKeyBinding, this.btnReset);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (this.btnChangeKeyBinding.mouseClicked(mouseX, mouseY, button))
            {
                return true;
            }
            else
            {
                return this.btnReset.mouseClicked(mouseX, mouseY, button);
            }
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button)
        {
            return this.btnChangeKeyBinding.mouseReleased(mouseX, mouseY, button) || this.btnReset.mouseReleased(mouseX, mouseY, button);
        }
    }
}
