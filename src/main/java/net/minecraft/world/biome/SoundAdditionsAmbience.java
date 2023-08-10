package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SoundEvent;

public class SoundAdditionsAmbience
{
    public static final Codec<SoundAdditionsAmbience> CODEC = RecordCodecBuilder.create((soundAdditionsCodecInstance) ->
    {
        return soundAdditionsCodecInstance.group(SoundEvent.CODEC.fieldOf("sound").forGetter((soundAdditions) -> {
            return soundAdditions.sound;
        }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((soundAdditions) -> {
            return soundAdditions.tickChance;
        })).apply(soundAdditionsCodecInstance, SoundAdditionsAmbience::new);
    });
    private SoundEvent sound;
    private double tickChance;

    public SoundAdditionsAmbience(SoundEvent sound, double tickChance)
    {
        this.sound = sound;
        this.tickChance = tickChance;
    }

    public SoundEvent getSound()
    {
        return this.sound;
    }

    public double getChancePerTick()
    {
        return this.tickChance;
    }
}
