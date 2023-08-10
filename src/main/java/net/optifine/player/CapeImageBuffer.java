package net.optifine.player;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer implements Runnable
{
    private AbstractClientPlayerEntity player;
    private ResourceLocation resourceLocation;
    private boolean elytraOfCape;

    public CapeImageBuffer(AbstractClientPlayerEntity player, ResourceLocation resourceLocation)
    {
        this.player = player;
        this.resourceLocation = resourceLocation;
    }

    public void run()
    {
    }

    public NativeImage parseUserSkin(NativeImage imageRaw)
    {
        NativeImage nativeimage = CapeUtils.parseCape(imageRaw);
        this.elytraOfCape = CapeUtils.isElytraCape(imageRaw, nativeimage);
        return nativeimage;
    }

    public void skinAvailable()
    {
        if (this.player != null)
        {
            this.player.setLocationOfCape(this.resourceLocation);
            this.player.setElytraOfCape(this.elytraOfCape);
        }

        this.cleanup();
    }

    public void cleanup()
    {
        this.player = null;
    }

    public boolean isElytraOfCape()
    {
        return this.elytraOfCape;
    }
}
