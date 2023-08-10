package net.minecraft.client.util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public class NativeUtil
{
    public static void crash()
    {
        MemoryUtil.memSet(0L, 0, 1L);
    }

    public static double getTime()
    {
        return GLFW.glfwGetTime();
    }
}
