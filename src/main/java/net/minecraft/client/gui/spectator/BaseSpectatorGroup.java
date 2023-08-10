package net.minecraft.client.gui.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayer;
import net.minecraft.client.gui.spectator.categories.TeleportToTeam;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BaseSpectatorGroup implements ISpectatorMenuView
{
    private static final ITextComponent field_243476_a = new TranslationTextComponent("spectatorMenu.root.prompt");
    private final List<ISpectatorMenuObject> items = Lists.newArrayList();

    public BaseSpectatorGroup()
    {
        this.items.add(new TeleportToPlayer());
        this.items.add(new TeleportToTeam());
    }

    public List<ISpectatorMenuObject> getItems()
    {
        return this.items;
    }

    public ITextComponent getPrompt()
    {
        return field_243476_a;
    }
}
