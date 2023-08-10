package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SwampHutStructure extends Structure<NoFeatureConfig>
{
    private static final List<MobSpawnInfo.Spawners> SPAWN_LIST = ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.WITCH, 1, 1, 1));
    private static final List<MobSpawnInfo.Spawners> CREATURE_SPAWN_LIST = ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.CAT, 1, 1, 1));

    public SwampHutStructure(Codec<NoFeatureConfig> p_i231998_1_)
    {
        super(p_i231998_1_);
    }

    public Structure.IStartFactory<NoFeatureConfig> getStartFactory()
    {
        return SwampHutStructure.Start::new;
    }

    public List<MobSpawnInfo.Spawners> getSpawnList()
    {
        return SPAWN_LIST;
    }

    public List<MobSpawnInfo.Spawners> getCreatureSpawnList()
    {
        return CREATURE_SPAWN_LIST;
    }

    public static class Start extends StructureStart<NoFeatureConfig>
    {
        public Start(Structure<NoFeatureConfig> p_i225819_1_, int p_i225819_2_, int p_i225819_3_, MutableBoundingBox boundingBox, int p_i225819_5_, long p_i225819_6_)
        {
            super(p_i225819_1_, p_i225819_2_, p_i225819_3_, boundingBox, p_i225819_5_, p_i225819_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_)
        {
            SwampHutPiece swamphutpiece = new SwampHutPiece(this.rand, p_230364_4_ * 16, p_230364_5_ * 16);
            this.components.add(swamphutpiece);
            this.recalculateStructureSize();
        }
    }
}
