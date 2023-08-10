package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;

public class EndSpikeFeature extends Feature<EndSpikeFeatureConfig>
{
    private static final LoadingCache<Long, List<EndSpikeFeature.EndSpike>> LOADING_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new EndSpikeFeature.EndSpikeCacheLoader());

    public EndSpikeFeature(Codec<EndSpikeFeatureConfig> p_i231994_1_)
    {
        super(p_i231994_1_);
    }

    public static List<EndSpikeFeature.EndSpike> func_236356_a_(ISeedReader p_236356_0_)
    {
        Random random = new Random(p_236356_0_.getSeed());
        long i = random.nextLong() & 65535L;
        return LOADING_CACHE.getUnchecked(i);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, EndSpikeFeatureConfig p_241855_5_)
    {
        List<EndSpikeFeature.EndSpike> list = p_241855_5_.getSpikes();

        if (list.isEmpty())
        {
            list = func_236356_a_(p_241855_1_);
        }

        for (EndSpikeFeature.EndSpike endspikefeature$endspike : list)
        {
            if (endspikefeature$endspike.doesStartInChunk(p_241855_4_))
            {
                this.placeSpike(p_241855_1_, p_241855_3_, p_241855_5_, endspikefeature$endspike);
            }
        }

        return true;
    }

    /**
     * Places the End Spike in the world. Also generates the obsidian tower.
     */
    private void placeSpike(IServerWorld worldIn, Random rand, EndSpikeFeatureConfig config, EndSpikeFeature.EndSpike spike)
    {
        int i = spike.getRadius();

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(spike.getCenterX() - i, 0, spike.getCenterZ() - i), new BlockPos(spike.getCenterX() + i, spike.getHeight() + 10, spike.getCenterZ() + i)))
        {
            if (blockpos.distanceSq((double)spike.getCenterX(), (double)blockpos.getY(), (double)spike.getCenterZ(), false) <= (double)(i * i + 1) && blockpos.getY() < spike.getHeight())
            {
                this.setBlockState(worldIn, blockpos, Blocks.OBSIDIAN.getDefaultState());
            }
            else if (blockpos.getY() > 65)
            {
                this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
            }
        }

        if (spike.isGuarded())
        {
            int j1 = -2;
            int k1 = 2;
            int j = 3;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for (int k = -2; k <= 2; ++k)
            {
                for (int l = -2; l <= 2; ++l)
                {
                    for (int i1 = 0; i1 <= 3; ++i1)
                    {
                        boolean flag = MathHelper.abs(k) == 2;
                        boolean flag1 = MathHelper.abs(l) == 2;
                        boolean flag2 = i1 == 3;

                        if (flag || flag1 || flag2)
                        {
                            boolean flag3 = k == -2 || k == 2 || flag2;
                            boolean flag4 = l == -2 || l == 2 || flag2;
                            BlockState blockstate = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(flag3 && l != -2)).with(PaneBlock.SOUTH, Boolean.valueOf(flag3 && l != 2)).with(PaneBlock.WEST, Boolean.valueOf(flag4 && k != -2)).with(PaneBlock.EAST, Boolean.valueOf(flag4 && k != 2));
                            this.setBlockState(worldIn, blockpos$mutable.setPos(spike.getCenterX() + k, spike.getHeight() + i1, spike.getCenterZ() + l), blockstate);
                        }
                    }
                }
            }
        }

        EnderCrystalEntity endercrystalentity = EntityType.END_CRYSTAL.create(worldIn.getWorld());
        endercrystalentity.setBeamTarget(config.getCrystalBeamTarget());
        endercrystalentity.setInvulnerable(config.isCrystalInvulnerable());
        endercrystalentity.setLocationAndAngles((double)spike.getCenterX() + 0.5D, (double)(spike.getHeight() + 1), (double)spike.getCenterZ() + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
        worldIn.addEntity(endercrystalentity);
        this.setBlockState(worldIn, new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ()), Blocks.BEDROCK.getDefaultState());
    }

    public static class EndSpike
    {
        public static final Codec<EndSpikeFeature.EndSpike> field_236357_a_ = RecordCodecBuilder.create((p_236359_0_) ->
        {
            return p_236359_0_.group(Codec.INT.fieldOf("centerX").orElse(0).forGetter((p_236363_0_) -> {
                return p_236363_0_.centerX;
            }), Codec.INT.fieldOf("centerZ").orElse(0).forGetter((p_236362_0_) -> {
                return p_236362_0_.centerZ;
            }), Codec.INT.fieldOf("radius").orElse(0).forGetter((p_236361_0_) -> {
                return p_236361_0_.radius;
            }), Codec.INT.fieldOf("height").orElse(0).forGetter((p_236360_0_) -> {
                return p_236360_0_.height;
            }), Codec.BOOL.fieldOf("guarded").orElse(false).forGetter((p_236358_0_) -> {
                return p_236358_0_.guarded;
            })).apply(p_236359_0_, EndSpikeFeature.EndSpike::new);
        });
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final AxisAlignedBB topBoundingBox;

        public EndSpike(int centerXIn, int centerZIn, int radiusIn, int heightIn, boolean guardedIn)
        {
            this.centerX = centerXIn;
            this.centerZ = centerZIn;
            this.radius = radiusIn;
            this.height = heightIn;
            this.guarded = guardedIn;
            this.topBoundingBox = new AxisAlignedBB((double)(centerXIn - radiusIn), 0.0D, (double)(centerZIn - radiusIn), (double)(centerXIn + radiusIn), 256.0D, (double)(centerZIn + radiusIn));
        }

        public boolean doesStartInChunk(BlockPos pos)
        {
            return pos.getX() >> 4 == this.centerX >> 4 && pos.getZ() >> 4 == this.centerZ >> 4;
        }

        public int getCenterX()
        {
            return this.centerX;
        }

        public int getCenterZ()
        {
            return this.centerZ;
        }

        public int getRadius()
        {
            return this.radius;
        }

        public int getHeight()
        {
            return this.height;
        }

        public boolean isGuarded()
        {
            return this.guarded;
        }

        public AxisAlignedBB getTopBoundingBox()
        {
            return this.topBoundingBox;
        }
    }

    static class EndSpikeCacheLoader extends CacheLoader<Long, List<EndSpikeFeature.EndSpike>>
    {
        private EndSpikeCacheLoader()
        {
        }

        public List<EndSpikeFeature.EndSpike> load(Long p_load_1_)
        {
            List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
            Collections.shuffle(list, new Random(p_load_1_));
            List<EndSpikeFeature.EndSpike> list1 = Lists.newArrayList();

            for (int i = 0; i < 10; ++i)
            {
                int j = MathHelper.floor(42.0D * Math.cos(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
                int k = MathHelper.floor(42.0D * Math.sin(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
                int l = list.get(i);
                int i1 = 2 + l / 3;
                int j1 = 76 + l * 3;
                boolean flag = l == 1 || l == 2;
                list1.add(new EndSpikeFeature.EndSpike(j, k, i1, j1, flag));
            }

            return list1;
        }
    }
}
