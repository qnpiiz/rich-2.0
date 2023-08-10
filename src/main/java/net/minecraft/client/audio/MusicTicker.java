package net.minecraft.client.audio;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class MusicTicker
{
    private final Random random = new Random();
    private final Minecraft client;
    @Nullable
    private ISound currentMusic;
    private int timeUntilNextMusic = 100;

    public MusicTicker(Minecraft client)
    {
        this.client = client;
    }

    public void tick()
    {
        BackgroundMusicSelector backgroundmusicselector = this.client.getBackgroundMusicSelector();

        if (this.currentMusic != null)
        {
            if (!backgroundmusicselector.getSoundEvent().getName().equals(this.currentMusic.getSoundLocation()) && backgroundmusicselector.shouldReplaceCurrentMusic())
            {
                this.client.getSoundHandler().stop(this.currentMusic);
                this.timeUntilNextMusic = MathHelper.nextInt(this.random, 0, backgroundmusicselector.getMinDelay() / 2);
            }

            if (!this.client.getSoundHandler().isPlaying(this.currentMusic))
            {
                this.currentMusic = null;
                this.timeUntilNextMusic = Math.min(this.timeUntilNextMusic, MathHelper.nextInt(this.random, backgroundmusicselector.getMinDelay(), backgroundmusicselector.getMaxDelay()));
            }
        }

        this.timeUntilNextMusic = Math.min(this.timeUntilNextMusic, backgroundmusicselector.getMaxDelay());

        if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0)
        {
            this.selectRandomBackgroundMusic(backgroundmusicselector);
        }
    }

    public void selectRandomBackgroundMusic(BackgroundMusicSelector selector)
    {
        this.currentMusic = SimpleSound.music(selector.getSoundEvent());

        if (this.currentMusic.getSound() != SoundHandler.MISSING_SOUND)
        {
            this.client.getSoundHandler().play(this.currentMusic);
        }

        this.timeUntilNextMusic = Integer.MAX_VALUE;
    }

    public void stop()
    {
        if (this.currentMusic != null)
        {
            this.client.getSoundHandler().stop(this.currentMusic);
            this.currentMusic = null;
        }

        this.timeUntilNextMusic += 100;
    }

    public boolean isBackgroundMusicPlaying(BackgroundMusicSelector selector)
    {
        return this.currentMusic == null ? false : selector.getSoundEvent().getName().equals(this.currentMusic.getSoundLocation());
    }
}
