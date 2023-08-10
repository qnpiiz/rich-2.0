package net.minecraft.client;

import com.google.common.base.Charsets;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.util.text.TextProcessing;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

public class ClipboardHelper
{
    private final ByteBuffer buffer = BufferUtils.createByteBuffer(8192);

    public String getClipboardString(long window, GLFWErrorCallbackI errorCallback)
    {
        GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback(errorCallback);
        String s = GLFW.glfwGetClipboardString(window);
        s = s != null ? TextProcessing.func_238338_a_(s) : "";
        GLFWErrorCallback glfwerrorcallback1 = GLFW.glfwSetErrorCallback(glfwerrorcallback);

        if (glfwerrorcallback1 != null)
        {
            glfwerrorcallback1.free();
        }

        return s;
    }

    private static void copyToClipboard(long window, ByteBuffer clipboardBuffer, byte[] clipboardContent)
    {
        ((Buffer)clipboardBuffer).clear();
        clipboardBuffer.put(clipboardContent);
        clipboardBuffer.put((byte)0);
        ((Buffer)clipboardBuffer).flip();
        GLFW.glfwSetClipboardString(window, clipboardBuffer);
    }

    public void setClipboardString(long window, String string)
    {
        byte[] abyte = string.getBytes(Charsets.UTF_8);
        int i = abyte.length + 1;

        if (i < this.buffer.capacity())
        {
            copyToClipboard(window, this.buffer, abyte);
        }
        else
        {
            ByteBuffer bytebuffer = MemoryUtil.memAlloc(i);

            try
            {
                copyToClipboard(window, bytebuffer, abyte);
            }
            finally
            {
                MemoryUtil.memFree(bytebuffer);
            }
        }
    }
}
