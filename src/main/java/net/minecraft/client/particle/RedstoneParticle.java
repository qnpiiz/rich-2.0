package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.MathHelper;

public class RedstoneParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteWithAge;

    private RedstoneParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, RedstoneParticleData particleData, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.spriteWithAge = spriteWithAge;
        this.motionX *= (double)0.1F;
        this.motionY *= (double)0.1F;
        this.motionZ *= (double)0.1F;
        float f = (float)Math.random() * 0.4F + 0.6F;
        this.particleRed = ((float)(Math.random() * (double)0.2F) + 0.8F) * particleData.getRed() * f;
        this.particleGreen = ((float)(Math.random() * (double)0.2F) + 0.8F) * particleData.getGreen() * f;
        this.particleBlue = ((float)(Math.random() * (double)0.2F) + 0.8F) * particleData.getBlue() * f;
        this.particleScale *= 0.75F * particleData.getAlpha();
        int i = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.maxAge = (int)Math.max((float)i * particleData.getAlpha(), 1.0F);
        this.selectSpriteWithAge(spriteWithAge);
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public float getScale(float scaleFactor)
    {
        return this.particleScale * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
    }

    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        }
        else
        {
            this.selectSpriteWithAge(this.spriteWithAge);
            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.posY == this.prevPosY)
            {
                this.motionX *= 1.1D;
                this.motionZ *= 1.1D;
            }

            this.motionX *= (double)0.96F;
            this.motionY *= (double)0.96F;
            this.motionZ *= (double)0.96F;

            if (this.onGround)
            {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }
        }
    }

    public static class Factory implements IParticleFactory<RedstoneParticleData>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(RedstoneParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new RedstoneParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }
    }
}
