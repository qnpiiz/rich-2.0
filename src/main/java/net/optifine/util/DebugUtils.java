package net.optifine.util;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class DebugUtils
{
    private static FloatBuffer floatBuffer16 = BufferUtils.createFloatBuffer(16);
    private static float[] floatArray16 = new float[16];

    public static String getGlModelView()
    {
        ((Buffer)floatBuffer16).clear();
        GL11.glGetFloatv(2982, floatBuffer16);
        floatBuffer16.get(floatArray16);
        float[] afloat = transposeMat4(floatArray16);
        return getMatrix4(afloat);
    }

    public static String getGlProjection()
    {
        ((Buffer)floatBuffer16).clear();
        GL11.glGetFloatv(2983, floatBuffer16);
        floatBuffer16.get(floatArray16);
        float[] afloat = transposeMat4(floatArray16);
        return getMatrix4(afloat);
    }

    private static float[] transposeMat4(float[] arr)
    {
        float[] afloat = new float[16];

        for (int i = 0; i < 4; ++i)
        {
            for (int j = 0; j < 4; ++j)
            {
                afloat[i * 4 + j] = arr[j * 4 + i];
            }
        }

        return afloat;
    }

    public static String getMatrix4(Matrix4f mat)
    {
        mat.write(floatArray16);
        return getMatrix4(floatArray16);
    }

    private static String getMatrix4(float[] fs)
    {
        StringBuffer stringbuffer = new StringBuffer();

        for (int i = 0; i < fs.length; ++i)
        {
            String s = String.format("%.2f", fs[i]);

            if (i > 0)
            {
                if (i % 4 == 0)
                {
                    stringbuffer.append("\n");
                }
                else
                {
                    stringbuffer.append(", ");
                }
            }

            s = StrUtils.fillLeft(s, 5, ' ');
            stringbuffer.append(s);
        }

        return stringbuffer.toString();
    }
}
