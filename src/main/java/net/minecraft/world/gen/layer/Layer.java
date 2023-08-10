package net.minecraft.world.gen.layer;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final LazyArea field_215742_b;

    public Layer(IAreaFactory<LazyArea> lazyAreaFactoryIn)
    {
        this.field_215742_b = lazyAreaFactoryIn.make();
    }

    public Biome func_242936_a(Registry<Biome> p_242936_1_, int p_242936_2_, int p_242936_3_)
    {
        int i = this.field_215742_b.getValue(p_242936_2_, p_242936_3_);
        RegistryKey<Biome> registrykey = BiomeRegistry.getKeyFromID(i);

        if (registrykey == null)
        {
            throw new IllegalStateException("Unknown biome id emitted by layers: " + i);
        }
        else
        {
            Biome biome = p_242936_1_.getValueForKey(registrykey);

            if (biome == null)
            {
                if (SharedConstants.developmentMode)
                {
                    throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException("Unknown biome id: " + i));
                }
                else
                {
                    LOGGER.warn("Unknown biome id: ", (int)i);
                    return p_242936_1_.getValueForKey(BiomeRegistry.getKeyFromID(0));
                }
            }
            else
            {
                return biome;
            }
        }
    }
}
