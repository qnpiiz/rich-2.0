package net.minecraft.client.gui.social;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.network.play.NetworkPlayerInfo;

public class FilterList extends AbstractOptionList<FilterListEntry>
{
    private final SocialInteractionsScreen field_244650_a;
    private final Minecraft field_244651_o;
    private final List<FilterListEntry> field_244652_p = Lists.newArrayList();
    @Nullable
    private String field_244653_q;

    public FilterList(SocialInteractionsScreen p_i242133_1_, Minecraft p_i242133_2_, int p_i242133_3_, int p_i242133_4_, int p_i242133_5_, int p_i242133_6_, int p_i242133_7_)
    {
        super(p_i242133_2_, p_i242133_3_, p_i242133_4_, p_i242133_5_, p_i242133_6_, p_i242133_7_);
        this.field_244650_a = p_i242133_1_;
        this.field_244651_o = p_i242133_2_;
        this.func_244605_b(false);
        this.func_244606_c(false);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        double d0 = this.field_244651_o.getMainWindow().getGuiScaleFactor();
        RenderSystem.enableScissor((int)((double)this.getRowLeft() * d0), (int)((double)(this.height - this.y1) * d0), (int)((double)(this.getScrollbarPosition() + 6) * d0), (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * d0));
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.disableScissor();
    }

    public void func_244759_a(Collection<UUID> p_244759_1_, double p_244759_2_)
    {
        this.field_244652_p.clear();

        for (UUID uuid : p_244759_1_)
        {
            NetworkPlayerInfo networkplayerinfo = this.field_244651_o.player.connection.getPlayerInfo(uuid);

            if (networkplayerinfo != null)
            {
                this.field_244652_p.add(new FilterListEntry(this.field_244651_o, this.field_244650_a, networkplayerinfo.getGameProfile().getId(), networkplayerinfo.getGameProfile().getName(), networkplayerinfo::getLocationSkin));
            }
        }

        this.func_244661_g();
        this.field_244652_p.sort((p_244655_0_, p_244655_1_) ->
        {
            return p_244655_0_.func_244636_b().compareToIgnoreCase(p_244655_1_.func_244636_b());
        });
        this.replaceEntries(this.field_244652_p);
        this.setScrollAmount(p_244759_2_);
    }

    private void func_244661_g()
    {
        if (this.field_244653_q != null)
        {
            this.field_244652_p.removeIf((p_244654_1_) ->
            {
                return !p_244654_1_.func_244636_b().toLowerCase(Locale.ROOT).contains(this.field_244653_q);
            });
            this.replaceEntries(this.field_244652_p);
        }
    }

    public void func_244658_a(String p_244658_1_)
    {
        this.field_244653_q = p_244658_1_;
    }

    public boolean func_244660_f()
    {
        return this.field_244652_p.isEmpty();
    }

    public void func_244657_a(NetworkPlayerInfo p_244657_1_, SocialInteractionsScreen.Mode p_244657_2_)
    {
        UUID uuid = p_244657_1_.getGameProfile().getId();

        for (FilterListEntry filterlistentry : this.field_244652_p)
        {
            if (filterlistentry.func_244640_c().equals(uuid))
            {
                filterlistentry.func_244641_c(false);
                return;
            }
        }

        if ((p_244657_2_ == SocialInteractionsScreen.Mode.ALL || this.field_244651_o.func_244599_aA().func_244756_c(uuid)) && (Strings.isNullOrEmpty(this.field_244653_q) || p_244657_1_.getGameProfile().getName().toLowerCase(Locale.ROOT).contains(this.field_244653_q)))
        {
            FilterListEntry filterlistentry1 = new FilterListEntry(this.field_244651_o, this.field_244650_a, p_244657_1_.getGameProfile().getId(), p_244657_1_.getGameProfile().getName(), p_244657_1_::getLocationSkin);
            this.addEntry(filterlistentry1);
            this.field_244652_p.add(filterlistentry1);
        }
    }

    public void func_244659_a(UUID p_244659_1_)
    {
        for (FilterListEntry filterlistentry : this.field_244652_p)
        {
            if (filterlistentry.func_244640_c().equals(p_244659_1_))
            {
                filterlistentry.func_244641_c(true);
                return;
            }
        }
    }
}
