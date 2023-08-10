package net.minecraft.client.audio;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class BackgroundMusicTracks
{
    public static final BackgroundMusicSelector MAIN_MENU_MUSIC = new BackgroundMusicSelector(SoundEvents.MUSIC_MENU, 20, 600, true);
    public static final BackgroundMusicSelector CREATIVE_MODE_MUSIC = new BackgroundMusicSelector(SoundEvents.MUSIC_CREATIVE, 12000, 24000, false);
    public static final BackgroundMusicSelector CREDITS_MUSIC = new BackgroundMusicSelector(SoundEvents.MUSIC_CREDITS, 0, 0, true);
    public static final BackgroundMusicSelector DRAGON_FIGHT_MUSIC = new BackgroundMusicSelector(SoundEvents.MUSIC_DRAGON, 0, 0, true);
    public static final BackgroundMusicSelector END_MUSIC = new BackgroundMusicSelector(SoundEvents.MUSIC_END, 6000, 24000, true);
    public static final BackgroundMusicSelector UNDER_WATER_MUSIC = getDefaultBackgroundMusicSelector(SoundEvents.MUSIC_UNDER_WATER);
    public static final BackgroundMusicSelector WORLD_MUSIC = getDefaultBackgroundMusicSelector(SoundEvents.MUSIC_GAME);

    public static BackgroundMusicSelector getDefaultBackgroundMusicSelector(SoundEvent soundEvent)
    {
        return new BackgroundMusicSelector(soundEvent, 12000, 24000, false);
    }
}
