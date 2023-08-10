package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class WoodlandMansionStructure extends Structure<NoFeatureConfig>
{
    public WoodlandMansionStructure(Codec<NoFeatureConfig> p_i232005_1_)
    {
        super(p_i232005_1_);
    }

    protected boolean func_230365_b_()
    {
        return false;
    }

    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_)
    {
        for (Biome biome : p_230363_2_.getBiomes(p_230363_6_ * 16 + 9, p_230363_1_.func_230356_f_(), p_230363_7_ * 16 + 9, 32))
        {
            if (!biome.getGenerationSettings().hasStructure(this))
            {
                return false;
            }
        }

        return true;
    }

    public Structure.IStartFactory<NoFeatureConfig> getStartFactory()
    {
        return WoodlandMansionStructure.Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig>
    {
        public Start(Structure<NoFeatureConfig> p_i225823_1_, int p_i225823_2_, int p_i225823_3_, MutableBoundingBox p_i225823_4_, int p_i225823_5_, long p_i225823_6_)
        {
            super(p_i225823_1_, p_i225823_2_, p_i225823_3_, p_i225823_4_, p_i225823_5_, p_i225823_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_)
        {
            Rotation rotation = Rotation.randomRotation(this.rand);
            int i = 5;
            int j = 5;

            if (rotation == Rotation.CLOCKWISE_90)
            {
                i = -5;
            }
            else if (rotation == Rotation.CLOCKWISE_180)
            {
                i = -5;
                j = -5;
            }
            else if (rotation == Rotation.COUNTERCLOCKWISE_90)
            {
                j = -5;
            }

            int k = (p_230364_4_ << 4) + 7;
            int l = (p_230364_5_ << 4) + 7;
            int i1 = p_230364_2_.getNoiseHeightMinusOne(k, l, Heightmap.Type.WORLD_SURFACE_WG);
            int j1 = p_230364_2_.getNoiseHeightMinusOne(k, l + j, Heightmap.Type.WORLD_SURFACE_WG);
            int k1 = p_230364_2_.getNoiseHeightMinusOne(k + i, l, Heightmap.Type.WORLD_SURFACE_WG);
            int l1 = p_230364_2_.getNoiseHeightMinusOne(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG);
            int i2 = Math.min(Math.min(i1, j1), Math.min(k1, l1));

            if (i2 >= 60)
            {
                BlockPos blockpos = new BlockPos(p_230364_4_ * 16 + 8, i2 + 1, p_230364_5_ * 16 + 8);
                List<WoodlandMansionPieces.MansionTemplate> list = Lists.newLinkedList();
                WoodlandMansionPieces.generateMansion(p_230364_3_, blockpos, rotation, list, this.rand);
                this.components.addAll(list);
                this.recalculateStructureSize();
            }
        }

        public void func_230366_a_(ISeedReader p_230366_1_, StructureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, MutableBoundingBox p_230366_5_, ChunkPos p_230366_6_)
        {
            super.func_230366_a_(p_230366_1_, p_230366_2_, p_230366_3_, p_230366_4_, p_230366_5_, p_230366_6_);
            int i = this.bounds.minY;

            for (int j = p_230366_5_.minX; j <= p_230366_5_.maxX; ++j)
            {
                for (int k = p_230366_5_.minZ; k <= p_230366_5_.maxZ; ++k)
                {
                    BlockPos blockpos = new BlockPos(j, i, k);

                    if (!p_230366_1_.isAirBlock(blockpos) && this.bounds.isVecInside(blockpos))
                    {
                        boolean flag = false;

                        for (StructurePiece structurepiece : this.components)
                        {
                            if (structurepiece.getBoundingBox().isVecInside(blockpos))
                            {
                                flag = true;
                                break;
                            }
                        }

                        if (flag)
                        {
                            for (int l = i - 1; l > 1; --l)
                            {
                                BlockPos blockpos1 = new BlockPos(j, l, k);

                                if (!p_230366_1_.isAirBlock(blockpos1) && !p_230366_1_.getBlockState(blockpos1).getMaterial().isLiquid())
                                {
                                    break;
                                }

                                p_230366_1_.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }
        }
    }
}
