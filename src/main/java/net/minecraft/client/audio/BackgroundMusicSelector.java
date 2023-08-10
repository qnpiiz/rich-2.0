package net.minecraft.client.audio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SoundEvent;

public class BackgroundMusicSelector
{
    public static final Codec<BackgroundMusicSelector> CODEC = RecordCodecBuilder.create((selectorInstance) ->
    {
        return selectorInstance.group(SoundEvent.CODEC.fieldOf("sound").forGetter((selector) -> {
            return selector.soundEvent;
        }), Codec.INT.fieldOf("min_delay").forGetter((selector) -> {
            return selector.minDelay;
        }), Codec.INT.fieldOf("max_delay").forGetter((selector) -> {
            return selector.maxDelay;
        }), Codec.BOOL.fieldOf("replace_current_music").forGetter((selector) -> {
            return selector.replaceCurrentMusic;
        })).apply(selectorInstance, BackgroundMusicSelector::new);
    });
    private final SoundEvent soundEvent;
    private final int minDelay;
    private final int maxDelay;
    private final boolean replaceCurrentMusic;

    public BackgroundMusicSelector(SoundEvent soundEvent, int minDelay, int maxDelay, boolean replaceCurrentMusic)
    {
        this.soundEvent = soundEvent;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.replaceCurrentMusic = replaceCurrentMusic;
    }

    public SoundEvent getSoundEvent()
    {
        return this.soundEvent;
    }

    public int getMinDelay()
    {
        return this.minDelay;
    }

    public int getMaxDelay()
    {
        return this.maxDelay;
    }

    public boolean shouldReplaceCurrentMusic()
    {
        return this.replaceCurrentMusic;
    }
}
