package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class NetherFossilStructure extends Structure<NoFeatureConfig>
{
    public NetherFossilStructure(Codec<NoFeatureConfig> p_i232105_1_)
    {
        super(p_i232105_1_);
    }

    public Structure.IStartFactory<NoFeatureConfig> getStartFactory()
    {
        return NetherFossilStructure.Start::new;
    }

    public static class Start extends MarginedStructureStart<NoFeatureConfig>
    {
        public Start(Structure<NoFeatureConfig> p_i232106_1_, int p_i232106_2_, int p_i232106_3_, MutableBoundingBox p_i232106_4_, int p_i232106_5_, long p_i232106_6_)
        {
            super(p_i232106_1_, p_i232106_2_, p_i232106_3_, p_i232106_4_, p_i232106_5_, p_i232106_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_)
        {
            ChunkPos chunkpos = new ChunkPos(p_230364_4_, p_230364_5_);
            int i = chunkpos.getXStart() + this.rand.nextInt(16);
            int j = chunkpos.getZStart() + this.rand.nextInt(16);
            int k = p_230364_2_.func_230356_f_();
            int l = k + this.rand.nextInt(p_230364_2_.func_230355_e_() - 2 - k);
            IBlockReader iblockreader = p_230364_2_.func_230348_a_(i, j);

            for (BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(i, l, j); l > k; --l)
            {
                BlockState blockstate = iblockreader.getBlockState(blockpos$mutable);
                blockpos$mutable.move(Direction.DOWN);
                BlockState blockstate1 = iblockreader.getBlockState(blockpos$mutable);

                if (blockstate.isAir() && (blockstate1.isIn(Blocks.SOUL_SAND) || blockstate1.isSolidSide(iblockreader, blockpos$mutable, Direction.UP)))
                {
                    break;
                }
            }

            if (l > k)
            {
                NetherFossilStructures.func_236994_a_(p_230364_3_, this.components, this.rand, new BlockPos(i, l, j));
                this.recalculateStructureSize();
            }
        }
    }
}
