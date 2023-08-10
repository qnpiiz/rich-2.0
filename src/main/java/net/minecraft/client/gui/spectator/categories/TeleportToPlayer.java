package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;

public class TeleportToPlayer implements ISpectatorMenuView, ISpectatorMenuObject
{
    private static final Ordering<NetworkPlayerInfo> PROFILE_ORDER = Ordering.from((p_210243_0_, p_210243_1_) ->
    {
        return ComparisonChain.start().compare(p_210243_0_.getGameProfile().getId(), p_210243_1_.getGameProfile().getId()).result();
    });
    private static final ITextComponent field_243485_b = new TranslationTextComponent("spectatorMenu.teleport");
    private static final ITextComponent field_243486_c = new TranslationTextComponent("spectatorMenu.teleport.prompt");
    private final List<ISpectatorMenuObject> items = Lists.newArrayList();

    public TeleportToPlayer()
    {
        this(PROFILE_ORDER.sortedCopy(Minecraft.getInstance().getConnection().getPlayerInfoMap()));
    }

    public TeleportToPlayer(Collection<NetworkPlayerInfo> profiles)
    {
        for (NetworkPlayerInfo networkplayerinfo : PROFILE_ORDER.sortedCopy(profiles))
        {
            if (networkplayerinfo.getGameType() != GameType.SPECTATOR)
            {
                this.items.add(new PlayerMenuObject(networkplayerinfo.getGameProfile()));
            }
        }
    }

    public List<ISpectatorMenuObject> getItems()
    {
        return this.items;
    }

    public ITextComponent getPrompt()
    {
        return field_243486_c;
    }

    public void selectItem(SpectatorMenu menu)
    {
        menu.selectCategory(this);
    }

    public ITextComponent getSpectatorName()
    {
        return field_243485_b;
    }

    public void func_230485_a_(MatrixStack p_230485_1_, float p_230485_2_, int p_230485_3_)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(SpectatorGui.SPECTATOR_WIDGETS);
        AbstractGui.blit(p_230485_1_, 0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
    }

    public boolean isEnabled()
    {
        return !this.items.isEmpty();
    }
}
