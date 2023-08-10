package net.minecraft.client.gui.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.Util;

public class FilterManager
{
    private final Minecraft field_244642_a;
    private final Set<UUID> field_244643_b = Sets.newHashSet();
    private final SocialInteractionsService field_244755_c;
    private final Map<String, UUID> field_244796_d = Maps.newHashMap();

    public FilterManager(Minecraft p_i242141_1_, SocialInteractionsService p_i242141_2_)
    {
        this.field_244642_a = p_i242141_1_;
        this.field_244755_c = p_i242141_2_;
    }

    public void func_244646_a(UUID p_244646_1_)
    {
        this.field_244643_b.add(p_244646_1_);
    }

    public void func_244647_b(UUID p_244647_1_)
    {
        this.field_244643_b.remove(p_244647_1_);
    }

    public boolean func_244756_c(UUID p_244756_1_)
    {
        return this.func_244648_c(p_244756_1_) || this.func_244757_e(p_244756_1_);
    }

    public boolean func_244648_c(UUID p_244648_1_)
    {
        return this.field_244643_b.contains(p_244648_1_);
    }

    public boolean func_244757_e(UUID p_244757_1_)
    {
        return this.field_244755_c.isBlockedPlayer(p_244757_1_);
    }

    public Set<UUID> func_244644_a()
    {
        return this.field_244643_b;
    }

    public UUID func_244797_a(String p_244797_1_)
    {
        return this.field_244796_d.getOrDefault(p_244797_1_, Util.DUMMY_UUID);
    }

    public void func_244645_a(NetworkPlayerInfo p_244645_1_)
    {
        GameProfile gameprofile = p_244645_1_.getGameProfile();

        if (gameprofile.isComplete())
        {
            this.field_244796_d.put(gameprofile.getName(), gameprofile.getId());
        }

        Screen screen = this.field_244642_a.currentScreen;

        if (screen instanceof SocialInteractionsScreen)
        {
            SocialInteractionsScreen socialinteractionsscreen = (SocialInteractionsScreen)screen;
            socialinteractionsscreen.func_244683_a(p_244645_1_);
        }
    }

    public void func_244649_d(UUID p_244649_1_)
    {
        Screen screen = this.field_244642_a.currentScreen;

        if (screen instanceof SocialInteractionsScreen)
        {
            SocialInteractionsScreen socialinteractionsscreen = (SocialInteractionsScreen)screen;
            socialinteractionsscreen.func_244685_a(p_244649_1_);
        }
    }
}
