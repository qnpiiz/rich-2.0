package net.minecraft.realms;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;

public abstract class RealmsScreen extends Screen
{
    public RealmsScreen()
    {
        super(NarratorChatListener.EMPTY);
    }

    protected static int func_239562_k_(int p_239562_0_)
    {
        return 40 + p_239562_0_ * 13;
    }

    public void tick()
    {
        for (Widget widget : this.buttons)
        {
            if (widget instanceof IScreen)
            {
                ((IScreen)widget).tick();
            }
        }
    }

    public void func_231411_u_()
    {
        List<String> list = this.children.stream().filter(RealmsLabel.class::isInstance).map(RealmsLabel.class::cast).map(RealmsLabel::func_231399_a_).collect(Collectors.toList());
        RealmsNarratorHelper.func_239549_a_(list);
    }
}
