package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler extends ReloadListener<SoundHandler.Loader>
{
    public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    private static final TypeToken<Map<String, SoundList>> TYPE = new TypeToken<Map<String, SoundList>>()
    {
    };
    private final Map<ResourceLocation, SoundEventAccessor> soundRegistry = Maps.newHashMap();
    private final SoundEngine sndManager;

    public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn)
    {
        this.sndManager = new SoundEngine(this, gameSettingsIn, manager);
    }

    /**
     * Performs any reloading that can be done off-thread, such as file IO
     */
    protected SoundHandler.Loader prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        SoundHandler.Loader soundhandler$loader = new SoundHandler.Loader();
        profilerIn.startTick();

        for (String s : resourceManagerIn.getResourceNamespaces())
        {
            profilerIn.startSection(s);

            try
            {
                for (IResource iresource : resourceManagerIn.getAllResources(new ResourceLocation(s, "sounds.json")))
                {
                    profilerIn.startSection(iresource.getPackName());

                    try (
                            InputStream inputstream = iresource.getInputStream();
                            Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
                        )
                    {
                        profilerIn.startSection("parse");
                        Map<String, SoundList> map = JSONUtils.fromJSONUnlenient(GSON, reader, TYPE);
                        profilerIn.endStartSection("register");

                        for (Entry<String, SoundList> entry : map.entrySet())
                        {
                            soundhandler$loader.registerSoundEvent(new ResourceLocation(s, entry.getKey()), entry.getValue(), resourceManagerIn);
                        }

                        profilerIn.endSection();
                    }
                    catch (RuntimeException runtimeexception)
                    {
                        LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", iresource.getPackName(), runtimeexception);
                    }

                    profilerIn.endSection();
                }
            }
            catch (IOException ioexception)
            {
            }

            profilerIn.endSection();
        }

        profilerIn.endTick();
        return soundhandler$loader;
    }

    protected void apply(SoundHandler.Loader objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        objectIn.preloadSounds(this.soundRegistry, this.sndManager);

        for (ResourceLocation resourcelocation : this.soundRegistry.keySet())
        {
            SoundEventAccessor soundeventaccessor = this.soundRegistry.get(resourcelocation);

            if (soundeventaccessor.getSubtitle() instanceof TranslationTextComponent)
            {
                String s = ((TranslationTextComponent)soundeventaccessor.getSubtitle()).getKey();

                if (!I18n.hasKey(s))
                {
                    LOGGER.debug("Missing subtitle {} for event: {}", s, resourcelocation);
                }
            }
        }

        if (LOGGER.isDebugEnabled())
        {
            for (ResourceLocation resourcelocation1 : this.soundRegistry.keySet())
            {
                if (!Registry.SOUND_EVENT.containsKey(resourcelocation1))
                {
                    LOGGER.debug("Not having sound event for: {}", (Object)resourcelocation1);
                }
            }
        }

        this.sndManager.reload();
    }

    private static boolean isValidSound(Sound sound, ResourceLocation soundLocation, IResourceManager resourceManager)
    {
        ResourceLocation resourcelocation = sound.getSoundAsOggLocation();

        if (!resourceManager.hasResource(resourcelocation))
        {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", resourcelocation, soundLocation);
            return false;
        }
        else
        {
            return true;
        }
    }

    @Nullable
    public SoundEventAccessor getAccessor(ResourceLocation location)
    {
        return this.soundRegistry.get(location);
    }

    public Collection<ResourceLocation> getAvailableSounds()
    {
        return this.soundRegistry.keySet();
    }

    public void playOnNextTick(ITickableSound tickableSound)
    {
        this.sndManager.playOnNextTick(tickableSound);
    }

    /**
     * Play a sound
     */
    public void play(ISound sound)
    {
        this.sndManager.play(sound);
    }

    /**
     * Plays the sound in n ticks
     */
    public void playDelayed(ISound sound, int delay)
    {
        this.sndManager.playDelayed(sound, delay);
    }

    public void updateListener(ActiveRenderInfo activeRenderInfo)
    {
        this.sndManager.updateListener(activeRenderInfo);
    }

    public void pause()
    {
        this.sndManager.pause();
    }

    public void stop()
    {
        this.sndManager.stopAllSounds();
    }

    public void unloadSounds()
    {
        this.sndManager.unload();
    }

    public void tick(boolean isGamePaused)
    {
        this.sndManager.tick(isGamePaused);
    }

    public void resume()
    {
        this.sndManager.resume();
    }

    public void setSoundLevel(SoundCategory category, float volume)
    {
        if (category == SoundCategory.MASTER && volume <= 0.0F)
        {
            this.stop();
        }

        this.sndManager.setVolume(category, volume);
    }

    public void stop(ISound soundIn)
    {
        this.sndManager.stop(soundIn);
    }

    public boolean isPlaying(ISound sound)
    {
        return this.sndManager.isPlaying(sound);
    }

    public void addListener(ISoundEventListener listener)
    {
        this.sndManager.addListener(listener);
    }

    public void removeListener(ISoundEventListener listener)
    {
        this.sndManager.removeListener(listener);
    }

    public void stop(@Nullable ResourceLocation id, @Nullable SoundCategory category)
    {
        this.sndManager.stop(id, category);
    }

    public String getDebugString()
    {
        return this.sndManager.getDebugString();
    }

    public static class Loader
    {
        private final Map<ResourceLocation, SoundEventAccessor> soundRegistry = Maps.newHashMap();

        protected Loader()
        {
        }

        private void registerSoundEvent(ResourceLocation soundLocation, SoundList soundList, IResourceManager resourceManager)
        {
            SoundEventAccessor soundeventaccessor = this.soundRegistry.get(soundLocation);
            boolean flag = soundeventaccessor == null;

            if (flag || soundList.canReplaceExisting())
            {
                if (!flag)
                {
                    SoundHandler.LOGGER.debug("Replaced sound event location {}", (Object)soundLocation);
                }

                soundeventaccessor = new SoundEventAccessor(soundLocation, soundList.getSubtitle());
                this.soundRegistry.put(soundLocation, soundeventaccessor);
            }

            for (final Sound sound : soundList.getSounds())
            {
                final ResourceLocation resourcelocation = sound.getSoundLocation();
                ISoundEventAccessor<Sound> isoundeventaccessor;

                switch (sound.getType())
                {
                    case FILE:
                        if (!SoundHandler.isValidSound(sound, soundLocation, resourceManager))
                        {
                            continue;
                        }

                        isoundeventaccessor = sound;
                        break;

                    case SOUND_EVENT:
                        isoundeventaccessor = new ISoundEventAccessor<Sound>()
                        {
                            public int getWeight()
                            {
                                SoundEventAccessor soundeventaccessor1 = Loader.this.soundRegistry.get(resourcelocation);
                                return soundeventaccessor1 == null ? 0 : soundeventaccessor1.getWeight();
                            }
                            public Sound cloneEntry()
                            {
                                SoundEventAccessor soundeventaccessor1 = Loader.this.soundRegistry.get(resourcelocation);

                                if (soundeventaccessor1 == null)
                                {
                                    return SoundHandler.MISSING_SOUND;
                                }
                                else
                                {
                                    Sound sound1 = soundeventaccessor1.cloneEntry();
                                    return new Sound(sound1.getSoundLocation().toString(), sound1.getVolume() * sound.getVolume(), sound1.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, sound1.isStreaming() || sound.isStreaming(), sound1.shouldPreload(), sound1.getAttenuationDistance());
                                }
                            }
                            public void enqueuePreload(SoundEngine engine)
                            {
                                SoundEventAccessor soundeventaccessor1 = Loader.this.soundRegistry.get(resourcelocation);

                                if (soundeventaccessor1 != null)
                                {
                                    soundeventaccessor1.enqueuePreload(engine);
                                }
                            }
                        };

                        break;
                    default:
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
                }

                soundeventaccessor.addSound(isoundeventaccessor);
            }
        }

        public void preloadSounds(Map<ResourceLocation, SoundEventAccessor> soundRegistry, SoundEngine soundManager)
        {
            soundRegistry.clear();

            for (Entry<ResourceLocation, SoundEventAccessor> entry : this.soundRegistry.entrySet())
            {
                soundRegistry.put(entry.getKey(), entry.getValue());
                entry.getValue().enqueuePreload(soundManager);
            }
        }
    }
}
