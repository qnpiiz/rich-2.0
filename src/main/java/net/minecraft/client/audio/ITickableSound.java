package net.minecraft.client.audio;

public interface ITickableSound extends ISound
{
    boolean isDonePlaying();

    void tick();
}
