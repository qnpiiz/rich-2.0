package net.minecraft.client.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

public class ALUtils
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static String toALErrorString(int rawValue)
    {
        switch (rawValue)
        {
            case 40961:
                return "Invalid name parameter.";

            case 40962:
                return "Invalid enumerated parameter value.";

            case 40963:
                return "Invalid parameter parameter value.";

            case 40964:
                return "Invalid operation.";

            case 40965:
                return "Unable to allocate memory.";

            default:
                return "An unrecognized error occurred.";
        }
    }

    static boolean checkALError(String where)
    {
        int i = AL10.alGetError();

        if (i != 0)
        {
            LOGGER.error("{}: {}", where, toALErrorString(i));
            return true;
        }
        else
        {
            return false;
        }
    }

    private static String toALCErrorString(int errorCode)
    {
        switch (errorCode)
        {
            case 40961:
                return "Invalid device.";

            case 40962:
                return "Invalid context.";

            case 40963:
                return "Illegal enum.";

            case 40964:
                return "Invalid value.";

            case 40965:
                return "Unable to allocate memory.";

            default:
                return "An unrecognized error occurred.";
        }
    }

    static boolean checkALCError(long deviceHandle, String where)
    {
        int i = ALC10.alcGetError(deviceHandle);

        if (i != 0)
        {
            LOGGER.error("{}{}: {}", where, deviceHandle, toALCErrorString(i));
            return true;
        }
        else
        {
            return false;
        }
    }

    static int getFormat(AudioFormat audioFormat)
    {
        Encoding encoding = audioFormat.getEncoding();
        int i = audioFormat.getChannels();
        int j = audioFormat.getSampleSizeInBits();

        if (encoding.equals(Encoding.PCM_UNSIGNED) || encoding.equals(Encoding.PCM_SIGNED))
        {
            if (i == 1)
            {
                if (j == 8)
                {
                    return 4352;
                }

                if (j == 16)
                {
                    return 4353;
                }
            }
            else if (i == 2)
            {
                if (j == 8)
                {
                    return 4354;
                }

                if (j == 16)
                {
                    return 4355;
                }
            }
        }

        throw new IllegalArgumentException("Invalid audio format: " + audioFormat);
    }
}
