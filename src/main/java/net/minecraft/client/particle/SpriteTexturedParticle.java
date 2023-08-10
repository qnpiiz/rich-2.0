package net.minecraft.client.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;

public abstract class SpriteTexturedParticle extends TexturedParticle
{
    protected TextureAtlasSprite sprite;

    protected SpriteTexturedParticle(ClientWorld world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    protected SpriteTexturedParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
    }

    protected void setSprite(TextureAtlasSprite sprite)
    {
        this.sprite = sprite;
    }

    protected float getMinU()
    {
        return this.sprite.getMinU();
    }

    protected float getMaxU()
    {
        return this.sprite.getMaxU();
    }

    protected float getMinV()
    {
        return this.sprite.getMinV();
    }

    protected float getMaxV()
    {
        return this.sprite.getMaxV();
    }

    public void selectSpriteRandomly(IAnimatedSprite sprite)
    {
        this.setSprite(sprite.get(this.rand));
    }

    public void selectSpriteWithAge(IAnimatedSprite sprite)
    {
        this.setSprite(sprite.get(this.age, this.maxAge));
    }
}
