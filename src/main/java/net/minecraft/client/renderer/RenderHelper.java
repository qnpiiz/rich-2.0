package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class RenderHelper
{
    private static final Vector3f DEFAULT_LIGHTING = Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize);
    private static final Vector3f DIFFUSE_LIGHTING = Util.make(new Vector3f(-0.2F, 1.0F, 0.7F), Vector3f::normalize);
    private static final Vector3f GUI_FLAT_DIFFUSE_LIGHTING = Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize);
    private static final Vector3f GUI_3D_DIFFUSE_LIGHTING = Util.make(new Vector3f(-0.2F, -1.0F, 0.7F), Vector3f::normalize);

    public static void enableStandardItemLighting()
    {
        RenderSystem.enableLighting();
        RenderSystem.enableColorMaterial();
        RenderSystem.colorMaterial(1032, 5634);
    }

    /**
     * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
     */
    public static void disableStandardItemLighting()
    {
        RenderSystem.disableLighting();
        RenderSystem.disableColorMaterial();
    }

    public static void setupDiffuseGuiLighting(Matrix4f matrix)
    {
        RenderSystem.setupLevelDiffuseLighting(GUI_FLAT_DIFFUSE_LIGHTING, GUI_3D_DIFFUSE_LIGHTING, matrix);
    }

    public static void setupLevelDiffuseLighting(Matrix4f matrixIn)
    {
        RenderSystem.setupLevelDiffuseLighting(DEFAULT_LIGHTING, DIFFUSE_LIGHTING, matrixIn);
    }

    public static void setupGuiFlatDiffuseLighting()
    {
        RenderSystem.setupGuiFlatDiffuseLighting(DEFAULT_LIGHTING, DIFFUSE_LIGHTING);
    }

    public static void setupGui3DDiffuseLighting()
    {
        RenderSystem.setupGui3DDiffuseLighting(DEFAULT_LIGHTING, DIFFUSE_LIGHTING);
    }
}
