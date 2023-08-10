package net.minecraft.client.audio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class SoundEngine
{
    /** The marker used for logging */
    private static final Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<ResourceLocation> UNABLE_TO_PLAY = Sets.newHashSet();

    /** A reference to the sound handler. */
    private final SoundHandler sndHandler;

    /** Reference to the GameSettings object. */
    private final GameSettings options;

    /** Set to true when the SoundManager has been initialised. */
    private boolean loaded;
    private final SoundSystem sndSystem = new SoundSystem();
    private final Listener listener = this.sndSystem.getListener();
    private final AudioStreamManager audioStreamManager;
    private final SoundEngineExecutor executor = new SoundEngineExecutor();
    private final ChannelManager channelManager = new ChannelManager(this.sndSystem, this.executor);

    /** A counter for how long the sound manager has been running */
    private int ticks;
    private final Map<ISound, ChannelManager.Entry> playingSoundsChannel = Maps.newHashMap();
    private final Multimap<SoundCategory, ISound> categorySounds = HashMultimap.create();
    private final List<ITickableSound> tickableSounds = Lists.newArrayList();
    private final Map<ISound, Integer> delayedSounds = Maps.newHashMap();
    private final Map<ISound, Integer> playingSoundsStopTime = Maps.newHashMap();
    private final List<ISoundEventListener> listeners = Lists.newArrayList();
    private final List<ITickableSound> tickableSoundsToPlayOnNextTick = Lists.newArrayList();
    private final List<Sound> soundsToPreload = Lists.newArrayList();

    public SoundEngine(SoundHandler sndHandlerIn, GameSettings optionsIn, IResourceManager resourceManagerIn)
    {
        this.sndHandler = sndHandlerIn;
        this.options = optionsIn;
        this.audioStreamManager = new AudioStreamManager(resourceManagerIn);
    }

    public void reload()
    {
        UNABLE_TO_PLAY.clear();

        for (SoundEvent soundevent : Registry.SOUND_EVENT)
        {
            ResourceLocation resourcelocation = soundevent.getName();

            if (this.sndHandler.getAccessor(resourcelocation) == null)
            {
                LOGGER.warn("Missing sound for event: {}", (Object)Registry.SOUND_EVENT.getKey(soundevent));
                UNABLE_TO_PLAY.add(resourcelocation);
            }
        }

        this.unload();
        this.load();
    }

    /**
     * Tries to add the paulscode library and the relevant codecs. If it fails, the master volume  will be set to zero.
     */
    private synchronized void load()
    {
        if (!this.loaded)
        {
            try
            {
                this.sndSystem.init();
                this.listener.init();
                this.listener.setGain(this.options.getSoundLevel(SoundCategory.MASTER));
                this.audioStreamManager.preload(this.soundsToPreload).thenRun(this.soundsToPreload::clear);
                this.loaded = true;
                LOGGER.info(LOG_MARKER, "Sound engine started");
            }
            catch (RuntimeException runtimeexception)
            {
                LOGGER.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeexception);
            }
        }
    }

    private float getVolume(@Nullable SoundCategory category)
    {
        return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
    }

    public void setVolume(SoundCategory category, float volume)
    {
        if (this.loaded)
        {
            if (category == SoundCategory.MASTER)
            {
                this.listener.setGain(volume);
            }
            else
            {
                this.playingSoundsChannel.forEach((sound, channelEntry) ->
                {
                    float f = this.getClampedVolume(sound);
                    channelEntry.runOnSoundExecutor((source) -> {
                        if (f <= 0.0F)
                        {
                            source.stop();
                        }
                        else {
                            source.setGain(f);
                        }
                    });
                });
            }
        }
    }

    /**
     * Cleans up the Sound System
     */
    public void unload()
    {
        if (this.loaded)
        {
            this.stopAllSounds();
            this.audioStreamManager.clearAudioBufferCache();
            this.sndSystem.unload();
            this.loaded = false;
        }
    }

    public void stop(ISound sound)
    {
        if (this.loaded)
        {
            ChannelManager.Entry channelmanager$entry = this.playingSoundsChannel.get(sound);

            if (channelmanager$entry != null)
            {
                channelmanager$entry.runOnSoundExecutor(SoundSource::stop);
            }
        }
    }

    /**
     * Stops all currently playing sounds
     */
    public void stopAllSounds()
    {
        if (this.loaded)
        {
            this.executor.restart();
            this.playingSoundsChannel.values().forEach((channelEntry) ->
            {
                channelEntry.runOnSoundExecutor(SoundSource::stop);
            });
            this.playingSoundsChannel.clear();
            this.channelManager.releaseAll();
            this.delayedSounds.clear();
            this.tickableSounds.clear();
            this.categorySounds.clear();
            this.playingSoundsStopTime.clear();
            this.tickableSoundsToPlayOnNextTick.clear();
        }
    }

    public void addListener(ISoundEventListener listener)
    {
        this.listeners.add(listener);
    }

    public void removeListener(ISoundEventListener listener)
    {
        this.listeners.remove(listener);
    }

    public void tick(boolean isGamePaused)
    {
        if (!isGamePaused)
        {
            this.tickNonPaused();
        }

        this.channelManager.tick();
    }

    private void tickNonPaused()
    {
        ++this.ticks;
        this.tickableSoundsToPlayOnNextTick.stream().filter(ISound::shouldPlaySound).forEach(this::play);
        this.tickableSoundsToPlayOnNextTick.clear();

        for (ITickableSound itickablesound : this.tickableSounds)
        {
            if (!itickablesound.shouldPlaySound())
            {
                this.stop(itickablesound);
            }

            itickablesound.tick();

            if (itickablesound.isDonePlaying())
            {
                this.stop(itickablesound);
            }
            else
            {
                float f = this.getClampedVolume(itickablesound);
                float f1 = this.getClampedPitch(itickablesound);
                Vector3d vector3d = new Vector3d(itickablesound.getX(), itickablesound.getY(), itickablesound.getZ());
                ChannelManager.Entry channelmanager$entry = this.playingSoundsChannel.get(itickablesound);

                if (channelmanager$entry != null)
                {
                    channelmanager$entry.runOnSoundExecutor((source) ->
                    {
                        source.setGain(f);
                        source.setPitch(f1);
                        source.updateSource(vector3d);
                    });
                }
            }
        }

        Iterator<Entry<ISound, ChannelManager.Entry>> iterator = this.playingSoundsChannel.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry<ISound, ChannelManager.Entry> entry = iterator.next();
            ChannelManager.Entry channelmanager$entry1 = entry.getValue();
            ISound isound = entry.getKey();
            float f2 = this.options.getSoundLevel(isound.getCategory());

            if (f2 <= 0.0F)
            {
                channelmanager$entry1.runOnSoundExecutor(SoundSource::stop);
                iterator.remove();
            }
            else if (channelmanager$entry1.isReleased())
            {
                int i = this.playingSoundsStopTime.get(isound);

                if (i <= this.ticks)
                {
                    if (canRepeatAndHasDelay(isound))
                    {
                        this.delayedSounds.put(isound, this.ticks + isound.getRepeatDelay());
                    }

                    iterator.remove();
                    LOGGER.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", (Object)channelmanager$entry1);
                    this.playingSoundsStopTime.remove(isound);

                    try
                    {
                        this.categorySounds.remove(isound.getCategory(), isound);
                    }
                    catch (RuntimeException runtimeexception)
                    {
                    }

                    if (isound instanceof ITickableSound)
                    {
                        this.tickableSounds.remove(isound);
                    }
                }
            }
        }

        Iterator<Entry<ISound, Integer>> iterator1 = this.delayedSounds.entrySet().iterator();

        while (iterator1.hasNext())
        {
            Entry<ISound, Integer> entry1 = iterator1.next();

            if (this.ticks >= entry1.getValue())
            {
                ISound isound1 = entry1.getKey();

                if (isound1 instanceof ITickableSound)
                {
                    ((ITickableSound)isound1).tick();
                }

                this.play(isound1);
                iterator1.remove();
            }
        }
    }

    private static boolean hasRepeatDelay(ISound sound)
    {
        return sound.getRepeatDelay() > 0;
    }

    private static boolean canRepeatAndHasDelay(ISound sound)
    {
        return sound.canRepeat() && hasRepeatDelay(sound);
    }

    private static boolean canRepeatAndHasNoDelay(ISound sound)
    {
        return sound.canRepeat() && !hasRepeatDelay(sound);
    }

    public boolean isPlaying(ISound soundIn)
    {
        if (!this.loaded)
        {
            return false;
        }
        else
        {
            return this.playingSoundsStopTime.containsKey(soundIn) && this.playingSoundsStopTime.get(soundIn) <= this.ticks ? true : this.playingSoundsChannel.containsKey(soundIn);
        }
    }

    public void play(ISound p_sound)
    {
        if (this.loaded)
        {
            if (p_sound.shouldPlaySound())
            {
                SoundEventAccessor soundeventaccessor = p_sound.createAccessor(this.sndHandler);
                ResourceLocation resourcelocation = p_sound.getSoundLocation();

                if (soundeventaccessor == null)
                {
                    if (UNABLE_TO_PLAY.add(resourcelocation))
                    {
                        LOGGER.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", (Object)resourcelocation);
                    }
                }
                else
                {
                    Sound sound = p_sound.getSound();

                    if (sound == SoundHandler.MISSING_SOUND)
                    {
                        if (UNABLE_TO_PLAY.add(resourcelocation))
                        {
                            LOGGER.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", (Object)resourcelocation);
                        }
                    }
                    else
                    {
                        float f = p_sound.getVolume();
                        float f1 = Math.max(f, 1.0F) * (float)sound.getAttenuationDistance();
                        SoundCategory soundcategory = p_sound.getCategory();
                        float f2 = this.getClampedVolume(p_sound);
                        float f3 = this.getClampedPitch(p_sound);
                        ISound.AttenuationType isound$attenuationtype = p_sound.getAttenuationType();
                        boolean flag = p_sound.isGlobal();

                        if (f2 == 0.0F && !p_sound.canBeSilent())
                        {
                            LOGGER.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", (Object)sound.getSoundLocation());
                        }
                        else
                        {
                            Vector3d vector3d = new Vector3d(p_sound.getX(), p_sound.getY(), p_sound.getZ());

                            if (!this.listeners.isEmpty())
                            {
                                boolean flag1 = flag || isound$attenuationtype == ISound.AttenuationType.NONE || this.listener.getClientLocation().squareDistanceTo(vector3d) < (double)(f1 * f1);

                                if (flag1)
                                {
                                    for (ISoundEventListener isoundeventlistener : this.listeners)
                                    {
                                        isoundeventlistener.onPlaySound(p_sound, soundeventaccessor);
                                    }
                                }
                                else
                                {
                                    LOGGER.debug(LOG_MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", (Object)resourcelocation);
                                }
                            }

                            if (this.listener.getGain() <= 0.0F)
                            {
                                LOGGER.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)resourcelocation);
                            }
                            else
                            {
                                boolean flag2 = canRepeatAndHasNoDelay(p_sound);
                                boolean flag3 = sound.isStreaming();
                                CompletableFuture<ChannelManager.Entry> completablefuture = this.channelManager.requestSoundEntry(sound.isStreaming() ? SoundSystem.Mode.STREAMING : SoundSystem.Mode.STATIC);
                                ChannelManager.Entry channelmanager$entry = completablefuture.join();

                                if (channelmanager$entry == null)
                                {
                                    LOGGER.warn("Failed to create new sound handle");
                                }
                                else
                                {
                                    LOGGER.debug(LOG_MARKER, "Playing sound {} for event {}", sound.getSoundLocation(), resourcelocation);
                                    this.playingSoundsStopTime.put(p_sound, this.ticks + 20);
                                    this.playingSoundsChannel.put(p_sound, channelmanager$entry);
                                    this.categorySounds.put(soundcategory, p_sound);
                                    channelmanager$entry.runOnSoundExecutor((source) ->
                                    {
                                        source.setPitch(f3);
                                        source.setGain(f2);

                                        if (isound$attenuationtype == ISound.AttenuationType.LINEAR)
                                        {
                                            source.setLinearAttenuation(f1);
                                        }
                                        else {
                                            source.setNoAttenuation();
                                        }

                                        source.setLooping(flag2 && !flag3);
                                        source.updateSource(vector3d);
                                        source.setRelative(flag);
                                    });

                                    if (!flag3)
                                    {
                                        this.audioStreamManager.createResource(sound.getSoundAsOggLocation()).thenAccept((audioBuffer) ->
                                        {
                                            channelmanager$entry.runOnSoundExecutor((source) -> {
                                                source.bindBuffer(audioBuffer);
                                                source.play();
                                            });
                                        });
                                    }
                                    else
                                    {
                                        this.audioStreamManager.createStreamingResource(sound.getSoundAsOggLocation(), flag2).thenAccept((audioStream) ->
                                        {
                                            channelmanager$entry.runOnSoundExecutor((source) -> {
                                                source.playStreamableSounds(audioStream);
                                                source.play();
                                            });
                                        });
                                    }

                                    if (p_sound instanceof ITickableSound)
                                    {
                                        this.tickableSounds.add((ITickableSound)p_sound);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void playOnNextTick(ITickableSound tickableSound)
    {
        this.tickableSoundsToPlayOnNextTick.add(tickableSound);
    }

    public void enqueuePreload(Sound soundIn)
    {
        this.soundsToPreload.add(soundIn);
    }

    private float getClampedPitch(ISound soundIn)
    {
        return MathHelper.clamp(soundIn.getPitch(), 0.5F, 2.0F);
    }

    private float getClampedVolume(ISound soundIn)
    {
        return MathHelper.clamp(soundIn.getVolume() * this.getVolume(soundIn.getCategory()), 0.0F, 1.0F);
    }

    /**
     * Pauses all currently playing sounds
     */
    public void pause()
    {
        if (this.loaded)
        {
            this.channelManager.runForAllSoundSources((sourceStream) ->
            {
                sourceStream.forEach(SoundSource::pause);
            });
        }
    }

    /**
     * Resumes playing all currently playing sounds (after pauseAllSounds)
     */
    public void resume()
    {
        if (this.loaded)
        {
            this.channelManager.runForAllSoundSources((sourceStream) ->
            {
                sourceStream.forEach(SoundSource::resume);
            });
        }
    }

    /**
     * Adds a sound to play in n tick
     */
    public void playDelayed(ISound sound, int delay)
    {
        this.delayedSounds.put(sound, this.ticks + delay);
    }

    public void updateListener(ActiveRenderInfo renderInfo)
    {
        if (this.loaded && renderInfo.isValid())
        {
            Vector3d vector3d = renderInfo.getProjectedView();
            Vector3f vector3f = renderInfo.getViewVector();
            Vector3f vector3f1 = renderInfo.getUpVector();
            this.executor.execute(() ->
            {
                this.listener.setPosition(vector3d);
                this.listener.setOrientation(vector3f, vector3f1);
            });
        }
    }

    public void stop(@Nullable ResourceLocation soundName, @Nullable SoundCategory category)
    {
        if (category != null)
        {
            for (ISound isound : this.categorySounds.get(category))
            {
                if (soundName == null || isound.getSoundLocation().equals(soundName))
                {
                    this.stop(isound);
                }
            }
        }
        else if (soundName == null)
        {
            this.stopAllSounds();
        }
        else
        {
            for (ISound isound1 : this.playingSoundsChannel.keySet())
            {
                if (isound1.getSoundLocation().equals(soundName))
                {
                    this.stop(isound1);
                }
            }
        }
    }

    public String getDebugString()
    {
        return this.sndSystem.getDebugString();
    }
}
