package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;

public class BasicParticleType extends ParticleType<BasicParticleType> implements IParticleData
{
    private static final IParticleData.IDeserializer<BasicParticleType> DESERIALIZER = new IParticleData.IDeserializer<BasicParticleType>()
    {
        public BasicParticleType deserialize(ParticleType<BasicParticleType> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            return (BasicParticleType)particleTypeIn;
        }
        public BasicParticleType read(ParticleType<BasicParticleType> particleTypeIn, PacketBuffer buffer)
        {
            return (BasicParticleType)particleTypeIn;
        }
    };
    private final Codec<BasicParticleType> field_239825_b_ = Codec.unit(this::getType);

    protected BasicParticleType(boolean alwaysShow)
    {
        super(alwaysShow, DESERIALIZER);
    }

    public BasicParticleType getType()
    {
        return this;
    }

    public Codec<BasicParticleType> func_230522_e_()
    {
        return this.field_239825_b_;
    }

    public void write(PacketBuffer buffer)
    {
    }

    public String getParameters()
    {
        return Registry.PARTICLE_TYPE.getKey(this).toString();
    }
}
