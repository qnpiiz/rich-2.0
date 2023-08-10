package net.minecraft.client.audio;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;

public class OggAudioStreamWrapper implements IAudioStream
{
    private final OggAudioStreamWrapper.IFactory wrapperFactoryOGG;
    private IAudioStream audioStream;
    private final BufferedInputStream inputStream;

    public OggAudioStreamWrapper(OggAudioStreamWrapper.IFactory wrapperFactoryOGG, InputStream inputStream) throws IOException
    {
        this.wrapperFactoryOGG = wrapperFactoryOGG;
        this.inputStream = new BufferedInputStream(inputStream);
        this.inputStream.mark(Integer.MAX_VALUE);
        this.audioStream = wrapperFactoryOGG.create(new OggAudioStreamWrapper.Stream(this.inputStream));
    }

    public AudioFormat getAudioFormat()
    {
        return this.audioStream.getAudioFormat();
    }

    public ByteBuffer readOggSoundWithCapacity(int size) throws IOException
    {
        ByteBuffer bytebuffer = this.audioStream.readOggSoundWithCapacity(size);

        if (!bytebuffer.hasRemaining())
        {
            this.audioStream.close();
            this.inputStream.reset();
            this.audioStream = this.wrapperFactoryOGG.create(new OggAudioStreamWrapper.Stream(this.inputStream));
            bytebuffer = this.audioStream.readOggSoundWithCapacity(size);
        }

        return bytebuffer;
    }

    public void close() throws IOException
    {
        this.audioStream.close();
        this.inputStream.close();
    }

    @FunctionalInterface
    public interface IFactory
    {
        IAudioStream create(InputStream p_create_1_) throws IOException;
    }

    static class Stream extends FilterInputStream
    {
        private Stream(InputStream inputStream)
        {
            super(inputStream);
        }

        public void close()
        {
        }
    }
}
