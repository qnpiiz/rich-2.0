package net.minecraft.client.audio;

public interface ISoundEventListener
{
    void onPlaySound(ISound soundIn, SoundEventAccessor accessor);
}
