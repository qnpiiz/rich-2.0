package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class WaterWakeParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteWithAge;

    private WaterWakeParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.spriteWithAge = spriteWithAge;
        this.motionX *= (double)0.3F;
        this.motionY = Math.random() * (double)0.2F + (double)0.1F;
        this.motionZ *= (double)0.3F;
        this.setSize(0.01F, 0.01F);
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.selectSpriteWithAge(spriteWithAge);
        this.particleGravity = 0.0F;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int i = 60 - this.maxAge;

        if (this.maxAge-- <= 0)
        {
            this.setExpired();
        }
        else
        {
            this.motionY -= (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.98F;
            this.motionY *= (double)0.98F;
            this.motionZ *= (double)0.98F;
            float f = (float)i * 0.001F;
            this.setSize(f, f);
            this.setSprite(this.spriteWithAge.get(i % 4, 4));
        }
    }

    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new WaterWakeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
