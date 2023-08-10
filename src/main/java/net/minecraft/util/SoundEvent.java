package net.minecraft.util;

import com.mojang.serialization.Codec;

public class SoundEvent
{
    public static final Codec<SoundEvent> CODEC = ResourceLocation.CODEC.xmap(SoundEvent::new, (sound) ->
    {
        return sound.name;
    });
    private final ResourceLocation name;

    public SoundEvent(ResourceLocation name)
    {
        this.name = name;
    }

    public ResourceLocation getName()
    {
        return this.name;
    }
}
