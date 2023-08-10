package net.minecraft.client.gui.widget.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;

public class OptionsRowList extends AbstractOptionList<OptionsRowList.Row>
{
    public OptionsRowList(Minecraft p_i51130_1_, int p_i51130_2_, int p_i51130_3_, int p_i51130_4_, int p_i51130_5_, int p_i51130_6_)
    {
        super(p_i51130_1_, p_i51130_2_, p_i51130_3_, p_i51130_4_, p_i51130_5_, p_i51130_6_);
        this.centerListVertically = false;
    }

    public int addOption(AbstractOption p_214333_1_)
    {
        return this.addEntry(OptionsRowList.Row.create(this.minecraft.gameSettings, this.width, p_214333_1_));
    }

    public void addOption(AbstractOption p_214334_1_, @Nullable AbstractOption p_214334_2_)
    {
        this.addEntry(OptionsRowList.Row.create(this.minecraft.gameSettings, this.width, p_214334_1_, p_214334_2_));
    }

    public void addOptions(AbstractOption[] p_214335_1_)
    {
        for (int i = 0; i < p_214335_1_.length; i += 2)
        {
            this.addOption(p_214335_1_[i], i < p_214335_1_.length - 1 ? p_214335_1_[i + 1] : null);
        }
    }

    public int getRowWidth()
    {
        return 400;
    }

    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 32;
    }

    @Nullable
    public Widget func_243271_b(AbstractOption p_243271_1_)
    {
        for (OptionsRowList.Row optionsrowlist$row : this.getEventListeners())
        {
            for (Widget widget : optionsrowlist$row.widgets)
            {
                if (widget instanceof OptionButton && ((OptionButton)widget).func_238517_a_() == p_243271_1_)
                {
                    return widget;
                }
            }
        }

        return null;
    }

    public Optional<Widget> func_238518_c_(double p_238518_1_, double p_238518_3_)
    {
        for (OptionsRowList.Row optionsrowlist$row : this.getEventListeners())
        {
            for (Widget widget : optionsrowlist$row.widgets)
            {
                if (widget.isMouseOver(p_238518_1_, p_238518_3_))
                {
                    return Optional.of(widget);
                }
            }
        }

        return Optional.empty();
    }

    public static class Row extends AbstractOptionList.Entry<OptionsRowList.Row>
    {
        private final List<Widget> widgets;

        private Row(List<Widget> widgetsIn)
        {
            this.widgets = widgetsIn;
        }

        public static OptionsRowList.Row create(GameSettings settings, int guiWidth, AbstractOption option)
        {
            return new OptionsRowList.Row(ImmutableList.of(option.createWidget(settings, guiWidth / 2 - 155, 0, 310)));
        }

        public static OptionsRowList.Row create(GameSettings settings, int guiWidth, AbstractOption leftOption, @Nullable AbstractOption rightOption)
        {
            Widget widget = leftOption.createWidget(settings, guiWidth / 2 - 155, 0, 150);
            return rightOption == null ? new OptionsRowList.Row(ImmutableList.of(widget)) : new OptionsRowList.Row(ImmutableList.of(widget, rightOption.createWidget(settings, guiWidth / 2 - 155 + 160, 0, 150)));
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            this.widgets.forEach((p_238519_5_) ->
            {
                p_238519_5_.y = p_230432_3_;
                p_238519_5_.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
            });
        }

        public List <? extends IGuiEventListener > getEventListeners()
        {
            return this.widgets;
        }
    }
}
