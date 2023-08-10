package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Monitor;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;

public class MonitorHandler
{
    private final Long2ObjectMap<Monitor> monitorsById = new Long2ObjectOpenHashMap<>();
    private final IMonitorFactory monitorFactory;

    public MonitorHandler(IMonitorFactory monitorFactory)
    {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        this.monitorFactory = monitorFactory;
        GLFW.glfwSetMonitorCallback(this::onMonitorUpdate);
        PointerBuffer pointerbuffer = GLFW.glfwGetMonitors();

        if (pointerbuffer != null)
        {
            for (int i = 0; i < pointerbuffer.limit(); ++i)
            {
                long j = pointerbuffer.get(i);
                this.monitorsById.put(j, monitorFactory.createMonitor(j));
            }
        }
    }

    private void onMonitorUpdate(long monitorID, int opCode)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (opCode == 262145)
        {
            this.monitorsById.put(monitorID, this.monitorFactory.createMonitor(monitorID));
        }
        else if (opCode == 262146)
        {
            this.monitorsById.remove(monitorID);
        }
    }

    @Nullable
    public Monitor getMonitor(long monitorID)
    {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return this.monitorsById.get(monitorID);
    }

    @Nullable
    public Monitor getMonitor(MainWindow window)
    {
        long i = GLFW.glfwGetWindowMonitor(window.getHandle());

        if (i != 0L)
        {
            return this.getMonitor(i);
        }
        else
        {
            int j = window.getWindowX();
            int k = j + window.getWidth();
            int l = window.getWindowY();
            int i1 = l + window.getHeight();
            int j1 = -1;
            Monitor monitor = null;

            for (Monitor monitor1 : this.monitorsById.values())
            {
                int k1 = monitor1.getVirtualPosX();
                int l1 = k1 + monitor1.getDefaultVideoMode().getWidth();
                int i2 = monitor1.getVirtualPosY();
                int j2 = i2 + monitor1.getDefaultVideoMode().getHeight();
                int k2 = clamp(j, k1, l1);
                int l2 = clamp(k, k1, l1);
                int i3 = clamp(l, i2, j2);
                int j3 = clamp(i1, i2, j2);
                int k3 = Math.max(0, l2 - k2);
                int l3 = Math.max(0, j3 - i3);
                int i4 = k3 * l3;

                if (i4 > j1)
                {
                    monitor = monitor1;
                    j1 = i4;
                }
            }

            return monitor;
        }
    }

    public static int clamp(int minValue, int value, int maxValue)
    {
        if (minValue < value)
        {
            return value;
        }
        else
        {
            return minValue > maxValue ? maxValue : minValue;
        }
    }

    public void close()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GLFWMonitorCallback glfwmonitorcallback = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);

        if (glfwmonitorcallback != null)
        {
            glfwmonitorcallback.free();
        }
    }
}
