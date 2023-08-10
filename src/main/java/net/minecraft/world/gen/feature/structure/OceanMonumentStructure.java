package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanMonumentStructure extends Structure<NoFeatureConfig>
{
    private static final List<MobSpawnInfo.Spawners> MONUMENT_ENEMIES = ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.GUARDIAN, 1, 2, 4));

    public OceanMonumentStructure(Codec<NoFeatureConfig> p_i231975_1_)
    {
        super(p_i231975_1_);
    }

    protected boolean func_230365_b_()
    {
        return false;
    }

    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_)
    {
        for (Biome biome : p_230363_2_.getBiomes(p_230363_6_ * 16 + 9, p_230363_1_.func_230356_f_(), p_230363_7_ * 16 + 9, 16))
        {
            if (!biome.getGenerationSettings().hasStructure(this))
            {
                return false;
            }
        }

        for (Biome biome1 : p_230363_2_.getBiomes(p_230363_6_ * 16 + 9, p_230363_1_.func_230356_f_(), p_230363_7_ * 16 + 9, 29))
        {
            if (biome1.getCategory() != Biome.Category.OCEAN && biome1.getCategory() != Biome.Category.RIVER)
            {
                return false;
            }
        }

        return true;
    }

    public Structure.IStartFactory<NoFeatureConfig> getStartFactory()
    {
        return OceanMonumentStructure.Start::new;
    }

    public List<MobSpawnInfo.Spawners> getSpawnList()
    {
        return MONUMENT_ENEMIES;
    }

    public static class Start extends StructureStart<NoFeatureConfig>
    {
        private boolean wasCreated;

        public Start(Structure<NoFeatureConfig> p_i225814_1_, int p_i225814_2_, int p_i225814_3_, MutableBoundingBox p_i225814_4_, int p_i225814_5_, long p_i225814_6_)
        {
            super(p_i225814_1_, p_i225814_2_, p_i225814_3_, p_i225814_4_, p_i225814_5_, p_i225814_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_)
        {
            this.generateStart(p_230364_4_, p_230364_5_);
        }

        private void generateStart(int chunkX, int chunkZ)
        {
            int i = chunkX * 16 - 29;
            int j = chunkZ * 16 - 29;
            Direction direction = Direction.Plane.HORIZONTAL.random(this.rand);
            this.components.add(new OceanMonumentPieces.MonumentBuilding(this.rand, i, j, direction));
            this.recalculateStructureSize();
            this.wasCreated = true;
        }

        public void func_230366_a_(ISeedReader p_230366_1_, StructureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, MutableBoundingBox p_230366_5_, ChunkPos p_230366_6_)
        {
            if (!this.wasCreated)
            {
                this.components.clear();
                this.generateStart(this.getChunkPosX(), this.getChunkPosZ());
            }

            super.func_230366_a_(p_230366_1_, p_230366_2_, p_230366_3_, p_230366_4_, p_230366_5_, p_230366_6_);
        }
    }
}
