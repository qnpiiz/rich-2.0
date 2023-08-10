package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class OggAudioStream implements IAudioStream
{
    private long pointer;
    private final AudioFormat format;
    private final InputStream stream;
    private ByteBuffer buffer = MemoryUtil.memAlloc(8192);

    public OggAudioStream(InputStream oggInputStream) throws IOException
    {
        this.stream = oggInputStream;
        ((java.nio.Buffer)this.buffer).limit(0);

        try (MemoryStack memorystack = MemoryStack.stackPush())
        {
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);

            while (this.pointer == 0L)
            {
                if (!this.readToBuffer())
                {
                    throw new IOException("Failed to find Ogg header");
                }

                int i = this.buffer.position();
                ((java.nio.Buffer)this.buffer).position(0);
                this.pointer = STBVorbis.stb_vorbis_open_pushdata(this.buffer, intbuffer, intbuffer1, (STBVorbisAlloc)null);
                ((java.nio.Buffer)this.buffer).position(i);
                int j = intbuffer1.get(0);

                if (j == 1)
                {
                    this.clearInputBuffer();
                }
                else if (j != 0)
                {
                    throw new IOException("Failed to read Ogg file " + j);
                }
            }

            ((java.nio.Buffer)this.buffer).position(this.buffer.position() + intbuffer.get(0));
            STBVorbisInfo stbvorbisinfo = STBVorbisInfo.mallocStack(memorystack);
            STBVorbis.stb_vorbis_get_info(this.pointer, stbvorbisinfo);
            this.format = new AudioFormat((float)stbvorbisinfo.sample_rate(), 16, stbvorbisinfo.channels(), true, false);
        }
    }

    private boolean readToBuffer() throws IOException
    {
        int i = this.buffer.limit();
        int j = this.buffer.capacity() - i;

        if (j == 0)
        {
            return true;
        }
        else
        {
            byte[] abyte = new byte[j];
            int k = this.stream.read(abyte);

            if (k == -1)
            {
                return false;
            }
            else
            {
                int l = this.buffer.position();
                ((java.nio.Buffer)this.buffer).limit(i + k);
                ((java.nio.Buffer)this.buffer).position(i);
                this.buffer.put(abyte, 0, k);
                ((java.nio.Buffer)this.buffer).position(l);
                return true;
            }
        }
    }

    private void clearInputBuffer()
    {
        boolean flag = this.buffer.position() == 0;
        boolean flag1 = this.buffer.position() == this.buffer.limit();

        if (flag1 && !flag)
        {
            ((java.nio.Buffer)this.buffer).position(0);
            ((java.nio.Buffer)this.buffer).limit(0);
        }
        else
        {
            ByteBuffer bytebuffer = MemoryUtil.memAlloc(flag ? 2 * this.buffer.capacity() : this.buffer.capacity());
            bytebuffer.put(this.buffer);
            MemoryUtil.memFree(this.buffer);
            ((java.nio.Buffer)bytebuffer).flip();
            this.buffer = bytebuffer;
        }
    }

    private boolean readOgg(OggAudioStream.Buffer oggAudioBuffer) throws IOException
    {
        if (this.pointer == 0L)
        {
            return false;
        }
        else
        {
            try (MemoryStack memorystack = MemoryStack.stackPush())
            {
                PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
                IntBuffer intbuffer = memorystack.mallocInt(1);
                IntBuffer intbuffer1 = memorystack.mallocInt(1);

                while (true)
                {
                    int i = STBVorbis.stb_vorbis_decode_frame_pushdata(this.pointer, this.buffer, intbuffer, pointerbuffer, intbuffer1);
                    ((java.nio.Buffer)this.buffer).position(this.buffer.position() + i);
                    int j = STBVorbis.stb_vorbis_get_error(this.pointer);

                    if (j == 1)
                    {
                        this.clearInputBuffer();

                        if (!this.readToBuffer())
                        {
                            return false;
                        }
                    }
                    else
                    {
                        if (j != 0)
                        {
                            throw new IOException("Failed to read Ogg file " + j);
                        }

                        int k = intbuffer1.get(0);

                        if (k != 0)
                        {
                            int l = intbuffer.get(0);
                            PointerBuffer pointerbuffer1 = pointerbuffer.getPointerBuffer(l);

                            if (l != 1)
                            {
                                if (l == 2)
                                {
                                    this.copyFromDualChannels(pointerbuffer1.getFloatBuffer(0, k), pointerbuffer1.getFloatBuffer(1, k), oggAudioBuffer);
                                    return true;
                                }

                                throw new IllegalStateException("Invalid number of channels: " + l);
                            }

                            this.copyFromSingleChannel(pointerbuffer1.getFloatBuffer(0, k), oggAudioBuffer);
                            return true;
                        }
                    }
                }
            }
        }
    }

    private void copyFromSingleChannel(FloatBuffer floatBuffer, OggAudioStream.Buffer oggAudioBuffer)
    {
        while (floatBuffer.hasRemaining())
        {
            oggAudioBuffer.appendOggAudioBytes(floatBuffer.get());
        }
    }

    private void copyFromDualChannels(FloatBuffer soundChannel1, FloatBuffer soundChannel2, OggAudioStream.Buffer oggAudioBuffer)
    {
        while (soundChannel1.hasRemaining() && soundChannel2.hasRemaining())
        {
            oggAudioBuffer.appendOggAudioBytes(soundChannel1.get());
            oggAudioBuffer.appendOggAudioBytes(soundChannel2.get());
        }
    }

    public void close() throws IOException
    {
        if (this.pointer != 0L)
        {
            STBVorbis.stb_vorbis_close(this.pointer);
            this.pointer = 0L;
        }

        MemoryUtil.memFree(this.buffer);
        this.stream.close();
    }

    public AudioFormat getAudioFormat()
    {
        return this.format;
    }

    public ByteBuffer readOggSoundWithCapacity(int size) throws IOException
    {
        OggAudioStream.Buffer oggaudiostream$buffer = new OggAudioStream.Buffer(size + 8192);

        while (this.readOgg(oggaudiostream$buffer) && oggaudiostream$buffer.filledBytes < size)
        {
        }

        return oggaudiostream$buffer.mergeBuffers();
    }

    public ByteBuffer readOggSound() throws IOException
    {
        OggAudioStream.Buffer oggaudiostream$buffer = new OggAudioStream.Buffer(16384);

        while (this.readOgg(oggaudiostream$buffer))
        {
        }

        return oggaudiostream$buffer.mergeBuffers();
    }

    static class Buffer
    {
        private final List<ByteBuffer> storedBuffers = Lists.newArrayList();
        private final int bufferCapacity;
        private int filledBytes;
        private ByteBuffer currentBuffer;

        public Buffer(int capacity)
        {
            this.bufferCapacity = capacity + 1 & -2;
            this.createBuffer();
        }

        private void createBuffer()
        {
            this.currentBuffer = BufferUtils.createByteBuffer(this.bufferCapacity);
        }

        public void appendOggAudioBytes(float floatValue)
        {
            if (this.currentBuffer.remaining() == 0)
            {
                ((java.nio.Buffer)this.currentBuffer).flip();
                this.storedBuffers.add(this.currentBuffer);
                this.createBuffer();
            }

            int i = MathHelper.clamp((int)(floatValue * 32767.5F - 0.5F), -32768, 32767);
            this.currentBuffer.putShort((short)i);
            this.filledBytes += 2;
        }

        public ByteBuffer mergeBuffers()
        {
            ((java.nio.Buffer)this.currentBuffer).flip();

            if (this.storedBuffers.isEmpty())
            {
                return this.currentBuffer;
            }
            else
            {
                ByteBuffer bytebuffer = BufferUtils.createByteBuffer(this.filledBytes);
                this.storedBuffers.forEach(bytebuffer::put);
                bytebuffer.put(this.currentBuffer);
                ((java.nio.Buffer)bytebuffer).flip();
                return bytebuffer;
            }
        }
    }
}
