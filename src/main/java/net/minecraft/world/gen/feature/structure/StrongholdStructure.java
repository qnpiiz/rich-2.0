package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class StrongholdStructure extends Structure<NoFeatureConfig>
{
    public StrongholdStructure(Codec<NoFeatureConfig> p_i231996_1_)
    {
        super(p_i231996_1_);
    }

    public Structure.IStartFactory<NoFeatureConfig> getStartFactory()
    {
        return StrongholdStructure.Start::new;
    }

    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_)
    {
        return p_230363_1_.func_235952_a_(new ChunkPos(p_230363_6_, p_230363_7_));
    }

    public static class Start extends StructureStart<NoFeatureConfig>
    {
        private final long field_236364_e_;

        public Start(Structure<NoFeatureConfig> p_i225818_1_, int p_i225818_2_, int p_i225818_3_, MutableBoundingBox p_i225818_4_, int p_i225818_5_, long p_i225818_6_)
        {
            super(p_i225818_1_, p_i225818_2_, p_i225818_3_, p_i225818_4_, p_i225818_5_, p_i225818_6_);
            this.field_236364_e_ = p_i225818_6_;
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_)
        {
            int i = 0;
            StrongholdPieces.Stairs2 strongholdpieces$stairs2;

            do
            {
                this.components.clear();
                this.bounds = MutableBoundingBox.getNewBoundingBox();
                this.rand.setLargeFeatureSeed(this.field_236364_e_ + (long)(i++), p_230364_4_, p_230364_5_);
                StrongholdPieces.prepareStructurePieces();
                strongholdpieces$stairs2 = new StrongholdPieces.Stairs2(this.rand, (p_230364_4_ << 4) + 2, (p_230364_5_ << 4) + 2);
                this.components.add(strongholdpieces$stairs2);
                strongholdpieces$stairs2.buildComponent(strongholdpieces$stairs2, this.components, this.rand);
                List<StructurePiece> list = strongholdpieces$stairs2.pendingChildren;

                while (!list.isEmpty())
                {
                    int j = this.rand.nextInt(list.size());
                    StructurePiece structurepiece = list.remove(j);
                    structurepiece.buildComponent(strongholdpieces$stairs2, this.components, this.rand);
                }

                this.recalculateStructureSize();
                this.func_214628_a(p_230364_2_.func_230356_f_(), this.rand, 10);
            }
            while (this.components.isEmpty() || strongholdpieces$stairs2.strongholdPortalRoom == null);
        }
    }
}
