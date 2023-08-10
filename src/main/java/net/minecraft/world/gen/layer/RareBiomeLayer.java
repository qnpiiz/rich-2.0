package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum RareBiomeLayer implements IC1Transformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int value)
    {
        return context.random(57) == 0 && value == 1 ? 129 : value;
    }
}
