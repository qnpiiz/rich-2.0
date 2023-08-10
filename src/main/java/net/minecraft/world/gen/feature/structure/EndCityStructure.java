package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class EndCityStructure extends Structure<NoFeatureConfig>
{
    public EndCityStructure(Codec<NoFeatureConfig> p_i231950_1_)
    {
        super(p_i231950_1_);
    }

    protected boolean func_230365_b_()
    {
        return false;
    }

    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_)
    {
        return getYPosForStructure(p_230363_6_, p_230363_7_, p_230363_1_) >= 60;
    }

    public Structure.IStartFactory<NoFeatureConfig> getStartFactory()
    {
        return EndCityStructure.Start::new;
    }

    private static int getYPosForStructure(int chunkX, int chunkY, ChunkGenerator generatorIn)
    {
        Random random = new Random((long)(chunkX + chunkY * 10387313));
        Rotation rotation = Rotation.randomRotation(random);
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

        int k = (chunkX << 4) + 7;
        int l = (chunkY << 4) + 7;
        int i1 = generatorIn.getNoiseHeightMinusOne(k, l, Heightmap.Type.WORLD_SURFACE_WG);
        int j1 = generatorIn.getNoiseHeightMinusOne(k, l + j, Heightmap.Type.WORLD_SURFACE_WG);
        int k1 = generatorIn.getNoiseHeightMinusOne(k + i, l, Heightmap.Type.WORLD_SURFACE_WG);
        int l1 = generatorIn.getNoiseHeightMinusOne(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG);
        return Math.min(Math.min(i1, j1), Math.min(k1, l1));
    }

    public static class Start extends StructureStart<NoFeatureConfig>
    {
        public Start(Structure<NoFeatureConfig> p_i225802_1_, int p_i225802_2_, int p_i225802_3_, MutableBoundingBox p_i225802_4_, int p_i225802_5_, long p_i225802_6_)
        {
            super(p_i225802_1_, p_i225802_2_, p_i225802_3_, p_i225802_4_, p_i225802_5_, p_i225802_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_)
        {
            Rotation rotation = Rotation.randomRotation(this.rand);
            int i = EndCityStructure.getYPosForStructure(p_230364_4_, p_230364_5_, p_230364_2_);

            if (i >= 60)
            {
                BlockPos blockpos = new BlockPos(p_230364_4_ * 16 + 8, i, p_230364_5_ * 16 + 8);
                EndCityPieces.startHouseTower(p_230364_3_, blockpos, rotation, this.components, this.rand);
                this.recalculateStructureSize();
            }
        }
    }
}
