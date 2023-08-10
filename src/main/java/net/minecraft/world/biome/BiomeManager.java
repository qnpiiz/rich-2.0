package net.minecraft.world.biome;

import com.google.common.hash.Hashing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiomeManager
{
    private final BiomeManager.IBiomeReader reader;
    private final long seed;
    private final IBiomeMagnifier magnifier;

    public BiomeManager(BiomeManager.IBiomeReader readerIn, long seedIn, IBiomeMagnifier magnifierIn)
    {
        this.reader = readerIn;
        this.seed = seedIn;
        this.magnifier = magnifierIn;
    }

    public static long getHashedSeed(long seed)
    {
        return Hashing.sha256().hashLong(seed).asLong();
    }

    public BiomeManager copyWithProvider(BiomeProvider newProvider)
    {
        return new BiomeManager(newProvider, this.seed, this.magnifier);
    }

    public Biome getBiome(BlockPos posIn)
    {
        return this.magnifier.getBiome(this.seed, posIn.getX(), posIn.getY(), posIn.getZ(), this.reader);
    }

    public Biome getBiomeAtPosition(double x, double y, double z)
    {
        int i = MathHelper.floor(x) >> 2;
        int j = MathHelper.floor(y) >> 2;
        int k = MathHelper.floor(z) >> 2;
        return this.getBiomeAtPosition(i, j, k);
    }

    public Biome getBiomeAtPosition(BlockPos pos)
    {
        int i = pos.getX() >> 2;
        int j = pos.getY() >> 2;
        int k = pos.getZ() >> 2;
        return this.getBiomeAtPosition(i, j, k);
    }

    public Biome getBiomeAtPosition(int x, int y, int z)
    {
        return this.reader.getNoiseBiome(x, y, z);
    }

    public interface IBiomeReader
    {
        Biome getNoiseBiome(int x, int y, int z);
    }
}
