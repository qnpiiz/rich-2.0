package net.minecraft.particles;

import com.mojang.serialization.Codec;

public abstract class ParticleType<T extends IParticleData>
{
    private final boolean alwaysShow;
    private final IParticleData.IDeserializer<T> deserializer;

    protected ParticleType(boolean alwaysShow, IParticleData.IDeserializer<T> deserializer)
    {
        this.alwaysShow = alwaysShow;
        this.deserializer = deserializer;
    }

    public boolean getAlwaysShow()
    {
        return this.alwaysShow;
    }

    public IParticleData.IDeserializer<T> getDeserializer()
    {
        return this.deserializer;
    }

    public abstract Codec<T> func_230522_e_();
}
