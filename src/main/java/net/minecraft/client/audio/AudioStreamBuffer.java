package net.minecraft.client.audio;

import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.openal.AL10;

public class AudioStreamBuffer
{
    @Nullable
    private ByteBuffer inputBuffer;
    private final AudioFormat audioFormat;
    private boolean hasBuffer;
    private int buffer;

    public AudioStreamBuffer(ByteBuffer buffer, AudioFormat format)
    {
        this.inputBuffer = buffer;
        this.audioFormat = format;
    }

    OptionalInt getBuffer()
    {
        if (!this.hasBuffer)
        {
            if (this.inputBuffer == null)
            {
                return OptionalInt.empty();
            }

            int i = ALUtils.getFormat(this.audioFormat);
            int[] aint = new int[1];
            AL10.alGenBuffers(aint);

            if (ALUtils.checkALError("Creating buffer"))
            {
                return OptionalInt.empty();
            }

            AL10.alBufferData(aint[0], i, this.inputBuffer, (int)this.audioFormat.getSampleRate());

            if (ALUtils.checkALError("Assigning buffer data"))
            {
                return OptionalInt.empty();
            }

            this.buffer = aint[0];
            this.hasBuffer = true;
            this.inputBuffer = null;
        }

        return OptionalInt.of(this.buffer);
    }

    public void deleteBuffer()
    {
        if (this.hasBuffer)
        {
            AL10.alDeleteBuffers(new int[] {this.buffer});

            if (ALUtils.checkALError("Deleting stream buffers"))
            {
                return;
            }
        }

        this.hasBuffer = false;
    }

    public OptionalInt getUntrackedBuffer()
    {
        OptionalInt optionalint = this.getBuffer();
        this.hasBuffer = false;
        return optionalint;
    }
}
