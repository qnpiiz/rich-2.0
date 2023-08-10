package net.minecraft.client.audio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;

public class SoundSource
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final int id;
    private final AtomicBoolean playing = new AtomicBoolean(true);
    private int defaultByteBufferCapacity = 16384;
    @Nullable
    private IAudioStream audioStream;

    @Nullable
    static SoundSource allocateNewSource()
    {
        int[] aint = new int[1];
        AL10.alGenSources(aint);
        return ALUtils.checkALError("Allocate new source") ? null : new SoundSource(aint[0]);
    }

    private SoundSource(int id)
    {
        this.id = id;
    }

    public void close()
    {
        if (this.playing.compareAndSet(true, false))
        {
            AL10.alSourceStop(this.id);
            ALUtils.checkALError("Stop");

            if (this.audioStream != null)
            {
                try
                {
                    this.audioStream.close();
                }
                catch (IOException ioexception)
                {
                    LOGGER.error("Failed to close audio stream", (Throwable)ioexception);
                }

                this.removeProcessedBuffers();
                this.audioStream = null;
            }

            AL10.alDeleteSources(new int[] {this.id});
            ALUtils.checkALError("Cleanup");
        }
    }

    public void play()
    {
        AL10.alSourcePlay(this.id);
    }

    private int getState()
    {
        return !this.playing.get() ? 4116 : AL10.alGetSourcei(this.id, 4112);
    }

    public void pause()
    {
        if (this.getState() == 4114)
        {
            AL10.alSourcePause(this.id);
        }
    }

    public void resume()
    {
        if (this.getState() == 4115)
        {
            AL10.alSourcePlay(this.id);
        }
    }

    public void stop()
    {
        if (this.playing.get())
        {
            AL10.alSourceStop(this.id);
            ALUtils.checkALError("Stop");
        }
    }

    public boolean isStopped()
    {
        return this.getState() == 4116;
    }

    public void updateSource(Vector3d source)
    {
        AL10.alSourcefv(this.id, 4100, new float[] {(float)source.x, (float)source.y, (float)source.z});
    }

    public void setPitch(float pitch)
    {
        AL10.alSourcef(this.id, 4099, pitch);
    }

    public void setLooping(boolean looping)
    {
        AL10.alSourcei(this.id, 4103, looping ? 1 : 0);
    }

    public void setGain(float volume)
    {
        AL10.alSourcef(this.id, 4106, volume);
    }

    public void setNoAttenuation()
    {
        AL10.alSourcei(this.id, 53248, 0);
    }

    public void setLinearAttenuation(float linearAttenuation)
    {
        AL10.alSourcei(this.id, 53248, 53251);
        AL10.alSourcef(this.id, 4131, linearAttenuation);
        AL10.alSourcef(this.id, 4129, 1.0F);
        AL10.alSourcef(this.id, 4128, 0.0F);
    }

    public void setRelative(boolean relative)
    {
        AL10.alSourcei(this.id, 514, relative ? 1 : 0);
    }

    public void bindBuffer(AudioStreamBuffer buffer)
    {
        buffer.getBuffer().ifPresent((bufferID) ->
        {
            AL10.alSourcei(this.id, 4105, bufferID);
        });
    }

    public void playStreamableSounds(IAudioStream audioStream)
    {
        this.audioStream = audioStream;
        AudioFormat audioformat = audioStream.getAudioFormat();
        this.defaultByteBufferCapacity = getSampleSize(audioformat, 1);
        this.readFromStream(4);
    }

    private static int getSampleSize(AudioFormat audioFormat, int sampleAmount)
    {
        return (int)((float)(sampleAmount * audioFormat.getSampleSizeInBits()) / 8.0F * (float)audioFormat.getChannels() * audioFormat.getSampleRate());
    }

    private void readFromStream(int readCount)
    {
        if (this.audioStream != null)
        {
            try
            {
                for (int i = 0; i < readCount; ++i)
                {
                    ByteBuffer bytebuffer = this.audioStream.readOggSoundWithCapacity(this.defaultByteBufferCapacity);

                    if (bytebuffer != null)
                    {
                        (new AudioStreamBuffer(bytebuffer, this.audioStream.getAudioFormat())).getUntrackedBuffer().ifPresent((bufferID) ->
                        {
                            AL10.alSourceQueueBuffers(this.id, new int[]{bufferID});
                        });
                    }
                }
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Failed to read from audio stream", (Throwable)ioexception);
            }
        }
    }

    public void tick()
    {
        if (this.audioStream != null)
        {
            int i = this.removeProcessedBuffers();
            this.readFromStream(i);
        }
    }

    private int removeProcessedBuffers()
    {
        int i = AL10.alGetSourcei(this.id, 4118);

        if (i > 0)
        {
            int[] aint = new int[i];
            AL10.alSourceUnqueueBuffers(this.id, aint);
            ALUtils.checkALError("Unqueue buffers");
            AL10.alDeleteBuffers(aint);
            ALUtils.checkALError("Remove processed buffers");
        }

        return i;
    }
}
