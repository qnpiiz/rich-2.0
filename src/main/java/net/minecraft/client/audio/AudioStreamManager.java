package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class AudioStreamManager
{
    private final IResourceManager resourceManager;
    private final Map<ResourceLocation, CompletableFuture<AudioStreamBuffer>> bufferCache = Maps.newHashMap();

    public AudioStreamManager(IResourceManager resourceManagerIn)
    {
        this.resourceManager = resourceManagerIn;
    }

    public CompletableFuture<AudioStreamBuffer> createResource(ResourceLocation soundIDIn)
    {
        return this.bufferCache.computeIfAbsent(soundIDIn, (soundID) ->
        {
            return CompletableFuture.supplyAsync(() -> {
                try (
                        IResource iresource = this.resourceManager.getResource(soundID);
                        InputStream inputstream = iresource.getInputStream();
                        OggAudioStream oggaudiostream = new OggAudioStream(inputstream);
                    )
                {
                    ByteBuffer bytebuffer = oggaudiostream.readOggSound();
                    return new AudioStreamBuffer(bytebuffer, oggaudiostream.getAudioFormat());
                }
                catch (IOException ioexception)
                {
                    throw new CompletionException(ioexception);
                }
            }, Util.getServerExecutor());
        });
    }

    public CompletableFuture<IAudioStream> createStreamingResource(ResourceLocation resourceLocation, boolean isWrapper)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try {
                IResource iresource = this.resourceManager.getResource(resourceLocation);
                InputStream inputstream = iresource.getInputStream();
                return (IAudioStream)(isWrapper ? new OggAudioStreamWrapper(OggAudioStream::new, inputstream) : new OggAudioStream(inputstream));
            }
            catch (IOException ioexception)
            {
                throw new CompletionException(ioexception);
            }
        }, Util.getServerExecutor());
    }

    public void clearAudioBufferCache()
    {
        this.bufferCache.values().forEach((audioStreamBuffer) ->
        {
            audioStreamBuffer.thenAccept(AudioStreamBuffer::deleteBuffer);
        });
        this.bufferCache.clear();
    }

    public CompletableFuture<?> preload(Collection<Sound> sounds)
    {
        return CompletableFuture.allOf(sounds.stream().map((sound) ->
        {
            return this.createResource(sound.getSoundAsOggLocation());
        }).toArray((arrayLength) ->
        {
            return new CompletableFuture[arrayLength];
        }));
    }
}
