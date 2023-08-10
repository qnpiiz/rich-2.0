package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class RenderSkybox
{
    private final Minecraft mc;
    private final RenderSkyboxCube renderer;
    private float time;

    public RenderSkybox(RenderSkyboxCube rendererIn)
    {
        this.renderer = rendererIn;
        this.mc = Minecraft.getInstance();
    }

    public void render(float deltaT, float alpha)
    {
        this.time += deltaT;
        this.renderer.render(this.mc, MathHelper.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F, alpha);
    }
}
