package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PointOfInterestDebugRenderer implements DebugRenderer.IDebugRenderer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft client;
    private final Map<BlockPos, PointOfInterestDebugRenderer.POIInfo> field_217713_c = Maps.newHashMap();
    private final Map<UUID, PointOfInterestDebugRenderer.BrainInfo> field_239313_d_ = Maps.newHashMap();
    @Nullable
    private UUID field_217716_f;

    public PointOfInterestDebugRenderer(Minecraft client)
    {
        this.client = client;
    }

    public void clear()
    {
        this.field_217713_c.clear();
        this.field_239313_d_.clear();
        this.field_217716_f = null;
    }

    public void func_217691_a(PointOfInterestDebugRenderer.POIInfo p_217691_1_)
    {
        this.field_217713_c.put(p_217691_1_.field_217755_a, p_217691_1_);
    }

    public void func_217698_a(BlockPos p_217698_1_)
    {
        this.field_217713_c.remove(p_217698_1_);
    }

    public void func_217706_a(BlockPos p_217706_1_, int p_217706_2_)
    {
        PointOfInterestDebugRenderer.POIInfo pointofinterestdebugrenderer$poiinfo = this.field_217713_c.get(p_217706_1_);

        if (pointofinterestdebugrenderer$poiinfo == null)
        {
            LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + p_217706_1_);
        }
        else
        {
            pointofinterestdebugrenderer$poiinfo.field_217757_c = p_217706_2_;
        }
    }

    public void func_217692_a(PointOfInterestDebugRenderer.BrainInfo p_217692_1_)
    {
        this.field_239313_d_.put(p_217692_1_.field_217747_a, p_217692_1_);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ)
    {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.func_239331_b_();
        this.func_229035_a_(camX, camY, camZ);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();

        if (!this.client.player.isSpectator())
        {
            this.func_217710_d();
        }
    }

    private void func_239331_b_()
    {
        this.field_239313_d_.entrySet().removeIf((p_239330_1_) ->
        {
            Entity entity = this.client.world.getEntityByID((p_239330_1_.getValue()).field_217748_b);
            return entity == null || entity.removed;
        });
    }

    private void func_229035_a_(double p_229035_1_, double p_229035_3_, double p_229035_5_)
    {
        BlockPos blockpos = new BlockPos(p_229035_1_, p_229035_3_, p_229035_5_);
        this.field_239313_d_.values().forEach((p_222924_7_) ->
        {
            if (this.func_217694_d(p_222924_7_))
            {
                this.func_229038_b_(p_222924_7_, p_229035_1_, p_229035_3_, p_229035_5_);
            }
        });

        for (BlockPos blockpos1 : this.field_217713_c.keySet())
        {
            if (blockpos.withinDistance(blockpos1, 30.0D))
            {
                func_217699_b(blockpos1);
            }
        }

        this.field_217713_c.values().forEach((p_239324_2_) ->
        {
            if (blockpos.withinDistance(p_239324_2_.field_217755_a, 30.0D))
            {
                this.func_217705_b(p_239324_2_);
            }
        });
        this.func_222915_d().forEach((p_239325_2_, p_239325_3_) ->
        {
            if (blockpos.withinDistance(p_239325_2_, 30.0D))
            {
                this.func_222921_a(p_239325_2_, p_239325_3_);
            }
        });
    }

    private static void func_217699_b(BlockPos p_217699_0_)
    {
        float f = 0.05F;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderBox(p_217699_0_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
    }

    private void func_222921_a(BlockPos p_222921_1_, List<String> p_222921_2_)
    {
        float f = 0.05F;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderBox(p_222921_1_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
        func_222923_a("" + p_222921_2_, p_222921_1_, 0, -256);
        func_222923_a("Ghost POI", p_222921_1_, 1, -65536);
    }

    private void func_217705_b(PointOfInterestDebugRenderer.POIInfo p_217705_1_)
    {
        int i = 0;
        Set<String> set = this.func_217696_c(p_217705_1_);

        if (set.size() < 4)
        {
            func_217695_a("Owners: " + set, p_217705_1_, i, -256);
        }
        else
        {
            func_217695_a("" + set.size() + " ticket holders", p_217705_1_, i, -256);
        }

        ++i;
        Set<String> set1 = this.func_239342_d_(p_217705_1_);

        if (set1.size() < 4)
        {
            func_217695_a("Candidates: " + set1, p_217705_1_, i, -23296);
        }
        else
        {
            func_217695_a("" + set1.size() + " potential owners", p_217705_1_, i, -23296);
        }

        ++i;
        func_217695_a("Free tickets: " + p_217705_1_.field_217757_c, p_217705_1_, i, -256);
        ++i;
        func_217695_a(p_217705_1_.field_217756_b, p_217705_1_, i, -1);
    }

    private void func_229037_a_(PointOfInterestDebugRenderer.BrainInfo p_229037_1_, double p_229037_2_, double p_229037_4_, double p_229037_6_)
    {
        if (p_229037_1_.field_222930_g != null)
        {
            PathfindingDebugRenderer.func_229032_a_(p_229037_1_.field_222930_g, 0.5F, false, false, p_229037_2_, p_229037_4_, p_229037_6_);
        }
    }

    private void func_229038_b_(PointOfInterestDebugRenderer.BrainInfo p_229038_1_, double p_229038_2_, double p_229038_4_, double p_229038_6_)
    {
        boolean flag = this.func_217703_c(p_229038_1_);
        int i = 0;
        func_217693_a(p_229038_1_.field_217750_d, i, p_229038_1_.field_217749_c, -1, 0.03F);
        ++i;

        if (flag)
        {
            func_217693_a(p_229038_1_.field_217750_d, i, p_229038_1_.field_222928_d + " " + p_229038_1_.field_222929_e + " xp", -1, 0.02F);
            ++i;
        }

        if (flag)
        {
            int j = p_229038_1_.field_239349_f_ < p_229038_1_.field_239350_g_ ? -23296 : -1;
            func_217693_a(p_229038_1_.field_217750_d, i, "health: " + String.format("%.1f", p_229038_1_.field_239349_f_) + " / " + String.format("%.1f", p_229038_1_.field_239350_g_), j, 0.02F);
            ++i;
        }

        if (flag && !p_229038_1_.field_223455_g.equals(""))
        {
            func_217693_a(p_229038_1_.field_217750_d, i, p_229038_1_.field_223455_g, -98404, 0.02F);
            ++i;
        }

        if (flag)
        {
            for (String s : p_229038_1_.field_217752_f)
            {
                func_217693_a(p_229038_1_.field_217750_d, i, s, -16711681, 0.02F);
                ++i;
            }
        }

        if (flag)
        {
            for (String s1 : p_229038_1_.field_217751_e)
            {
                func_217693_a(p_229038_1_.field_217750_d, i, s1, -16711936, 0.02F);
                ++i;
            }
        }

        if (p_229038_1_.field_223456_i)
        {
            func_217693_a(p_229038_1_.field_217750_d, i, "Wants Golem", -23296, 0.02F);
            ++i;
        }

        if (flag)
        {
            for (String s2 : p_229038_1_.field_223457_m)
            {
                if (s2.startsWith(p_229038_1_.field_217749_c))
                {
                    func_217693_a(p_229038_1_.field_217750_d, i, s2, -1, 0.02F);
                }
                else
                {
                    func_217693_a(p_229038_1_.field_217750_d, i, s2, -23296, 0.02F);
                }

                ++i;
            }
        }

        if (flag)
        {
            for (String s3 : Lists.reverse(p_229038_1_.field_217753_g))
            {
                func_217693_a(p_229038_1_.field_217750_d, i, s3, -3355444, 0.02F);
                ++i;
            }
        }

        if (flag)
        {
            this.func_229037_a_(p_229038_1_, p_229038_2_, p_229038_4_, p_229038_6_);
        }
    }

    private static void func_217695_a(String p_217695_0_, PointOfInterestDebugRenderer.POIInfo p_217695_1_, int p_217695_2_, int p_217695_3_)
    {
        BlockPos blockpos = p_217695_1_.field_217755_a;
        func_222923_a(p_217695_0_, blockpos, p_217695_2_, p_217695_3_);
    }

    private static void func_222923_a(String p_222923_0_, BlockPos p_222923_1_, int p_222923_2_, int p_222923_3_)
    {
        double d0 = 1.3D;
        double d1 = 0.2D;
        double d2 = (double)p_222923_1_.getX() + 0.5D;
        double d3 = (double)p_222923_1_.getY() + 1.3D + (double)p_222923_2_ * 0.2D;
        double d4 = (double)p_222923_1_.getZ() + 0.5D;
        DebugRenderer.renderText(p_222923_0_, d2, d3, d4, p_222923_3_, 0.02F, true, 0.0F, true);
    }

    private static void func_217693_a(IPosition p_217693_0_, int p_217693_1_, String p_217693_2_, int p_217693_3_, float p_217693_4_)
    {
        double d0 = 2.4D;
        double d1 = 0.25D;
        BlockPos blockpos = new BlockPos(p_217693_0_);
        double d2 = (double)blockpos.getX() + 0.5D;
        double d3 = p_217693_0_.getY() + 2.4D + (double)p_217693_1_ * 0.25D;
        double d4 = (double)blockpos.getZ() + 0.5D;
        float f = 0.5F;
        DebugRenderer.renderText(p_217693_2_, d2, d3, d4, p_217693_3_, p_217693_4_, false, 0.5F, true);
    }

    private Set<String> func_217696_c(PointOfInterestDebugRenderer.POIInfo p_217696_1_)
    {
        return this.func_239340_c_(p_217696_1_.field_217755_a).stream().map(RandomObjectDescriptor::getRandomObjectDescriptor).collect(Collectors.toSet());
    }

    private Set<String> func_239342_d_(PointOfInterestDebugRenderer.POIInfo p_239342_1_)
    {
        return this.func_239343_d_(p_239342_1_.field_217755_a).stream().map(RandomObjectDescriptor::getRandomObjectDescriptor).collect(Collectors.toSet());
    }

    private boolean func_217703_c(PointOfInterestDebugRenderer.BrainInfo p_217703_1_)
    {
        return Objects.equals(this.field_217716_f, p_217703_1_.field_217747_a);
    }

    private boolean func_217694_d(PointOfInterestDebugRenderer.BrainInfo p_217694_1_)
    {
        PlayerEntity playerentity = this.client.player;
        BlockPos blockpos = new BlockPos(playerentity.getPosX(), p_217694_1_.field_217750_d.getY(), playerentity.getPosZ());
        BlockPos blockpos1 = new BlockPos(p_217694_1_.field_217750_d);
        return blockpos.withinDistance(blockpos1, 30.0D);
    }

    private Collection<UUID> func_239340_c_(BlockPos p_239340_1_)
    {
        return this.field_239313_d_.values().stream().filter((p_239336_1_) ->
        {
            return p_239336_1_.func_217744_a(p_239340_1_);
        }).map(PointOfInterestDebugRenderer.BrainInfo::func_217746_a).collect(Collectors.toSet());
    }

    private Collection<UUID> func_239343_d_(BlockPos p_239343_1_)
    {
        return this.field_239313_d_.values().stream().filter((p_239323_1_) ->
        {
            return p_239323_1_.func_239365_b_(p_239343_1_);
        }).map(PointOfInterestDebugRenderer.BrainInfo::func_217746_a).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> func_222915_d()
    {
        Map<BlockPos, List<String>> map = Maps.newHashMap();

        for (PointOfInterestDebugRenderer.BrainInfo pointofinterestdebugrenderer$braininfo : this.field_239313_d_.values())
        {
            for (BlockPos blockpos : Iterables.concat(pointofinterestdebugrenderer$braininfo.field_217754_h, pointofinterestdebugrenderer$braininfo.field_239360_q_))
            {
                if (!this.field_217713_c.containsKey(blockpos))
                {
                    map.computeIfAbsent(blockpos, (p_241729_0_) ->
                    {
                        return Lists.newArrayList();
                    }).add(pointofinterestdebugrenderer$braininfo.field_217749_c);
                }
            }
        }

        return map;
    }

    private void func_217710_d()
    {
        DebugRenderer.getTargetEntity(this.client.getRenderViewEntity(), 8).ifPresent((p_239317_1_) ->
        {
            this.field_217716_f = p_239317_1_.getUniqueID();
        });
    }

    public static class BrainInfo
    {
        public final UUID field_217747_a;
        public final int field_217748_b;
        public final String field_217749_c;
        public final String field_222928_d;
        public final int field_222929_e;
        public final float field_239349_f_;
        public final float field_239350_g_;
        public final IPosition field_217750_d;
        public final String field_223455_g;
        public final Path field_222930_g;
        public final boolean field_223456_i;
        public final List<String> field_217751_e = Lists.newArrayList();
        public final List<String> field_217752_f = Lists.newArrayList();
        public final List<String> field_217753_g = Lists.newArrayList();
        public final List<String> field_223457_m = Lists.newArrayList();
        public final Set<BlockPos> field_217754_h = Sets.newHashSet();
        public final Set<BlockPos> field_239360_q_ = Sets.newHashSet();

        public BrainInfo(UUID p_i241202_1_, int p_i241202_2_, String p_i241202_3_, String p_i241202_4_, int p_i241202_5_, float p_i241202_6_, float p_i241202_7_, IPosition p_i241202_8_, String p_i241202_9_, @Nullable Path p_i241202_10_, boolean p_i241202_11_)
        {
            this.field_217747_a = p_i241202_1_;
            this.field_217748_b = p_i241202_2_;
            this.field_217749_c = p_i241202_3_;
            this.field_222928_d = p_i241202_4_;
            this.field_222929_e = p_i241202_5_;
            this.field_239349_f_ = p_i241202_6_;
            this.field_239350_g_ = p_i241202_7_;
            this.field_217750_d = p_i241202_8_;
            this.field_223455_g = p_i241202_9_;
            this.field_222930_g = p_i241202_10_;
            this.field_223456_i = p_i241202_11_;
        }

        private boolean func_217744_a(BlockPos p_217744_1_)
        {
            return this.field_217754_h.stream().anyMatch(p_217744_1_::equals);
        }

        private boolean func_239365_b_(BlockPos p_239365_1_)
        {
            return this.field_239360_q_.contains(p_239365_1_);
        }

        public UUID func_217746_a()
        {
            return this.field_217747_a;
        }
    }

    public static class POIInfo
    {
        public final BlockPos field_217755_a;
        public String field_217756_b;
        public int field_217757_c;

        public POIInfo(BlockPos p_i50886_1_, String p_i50886_2_, int p_i50886_3_)
        {
            this.field_217755_a = p_i50886_1_;
            this.field_217756_b = p_i50886_2_;
            this.field_217757_c = p_i50886_3_;
        }
    }
}
