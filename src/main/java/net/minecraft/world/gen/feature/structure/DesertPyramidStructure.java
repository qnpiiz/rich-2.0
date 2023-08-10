package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DesertPyramidStructure extends Structure<NoFeatureConfig>
{
    public DesertPyramidStructure(Codec<NoFeatureConfig> p_i231947_1_)
    {
        super(p_i231947_1_);
    }

    public Structure.IStartFactory<NoFeatureConfig> getStartFactory()
    {
        return DesertPyramidStructure.Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig>
    {
        public Start(Structure<NoFeatureConfig> p_i225801_1_, int p_i225801_2_, int p_i225801_3_, MutableBoundingBox p_i225801_4_, int p_i225801_5_, long p_i225801_6_)
        {
            super(p_i225801_1_, p_i225801_2_, p_i225801_3_, p_i225801_4_, p_i225801_5_, p_i225801_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_)
        {
            DesertPyramidPiece desertpyramidpiece = new DesertPyramidPiece(this.rand, p_230364_4_ * 16, p_230364_5_ * 16);
            this.components.add(desertpyramidpiece);
            this.recalculateStructureSize();
        }
    }
}
