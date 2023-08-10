package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;

public class BastionRemantsStructure extends JigsawStructure
{
    public BastionRemantsStructure(Codec<VillageConfig> p_i231927_1_)
    {
        super(p_i231927_1_, 33, false, false);
    }

    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, VillageConfig p_230363_10_)
    {
        return p_230363_5_.nextInt(5) >= 2;
    }
}
