package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class JigsawStructure extends Structure<VillageConfig>
{
    private final int field_242774_u;
    private final boolean field_242775_v;
    private final boolean field_242776_w;

    public JigsawStructure(Codec<VillageConfig> p_i241978_1_, int p_i241978_2_, boolean p_i241978_3_, boolean p_i241978_4_)
    {
        super(p_i241978_1_);
        this.field_242774_u = p_i241978_2_;
        this.field_242775_v = p_i241978_3_;
        this.field_242776_w = p_i241978_4_;
    }

    public Structure.IStartFactory<VillageConfig> getStartFactory()
    {
        return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) ->
        {
            return new JigsawStructure.Start(this, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_);
        };
    }

    public static class Start extends MarginedStructureStart<VillageConfig>
    {
        private final JigsawStructure field_242781_e;

        public Start(JigsawStructure p_i241979_1_, int p_i241979_2_, int p_i241979_3_, MutableBoundingBox p_i241979_4_, int p_i241979_5_, long p_i241979_6_)
        {
            super(p_i241979_1_, p_i241979_2_, p_i241979_3_, p_i241979_4_, p_i241979_5_, p_i241979_6_);
            this.field_242781_e = p_i241979_1_;
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, VillageConfig p_230364_7_)
        {
            BlockPos blockpos = new BlockPos(p_230364_4_ * 16, this.field_242781_e.field_242774_u, p_230364_5_ * 16);
            JigsawPatternRegistry.func_244093_a();
            JigsawManager.func_242837_a(p_230364_1_, p_230364_7_, AbstractVillagePiece::new, p_230364_2_, p_230364_3_, blockpos, this.components, this.rand, this.field_242781_e.field_242775_v, this.field_242781_e.field_242776_w);
            this.recalculateStructureSize();
        }
    }
}
