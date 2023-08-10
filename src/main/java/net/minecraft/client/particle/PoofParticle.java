package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class PoofParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteWithAge;

    protected PoofParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z);
        this.spriteWithAge = spriteWithAge;
        this.motionX = motionX + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        this.motionY = motionY + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        this.motionZ = motionZ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        float f = this.rand.nextFloat() * 0.3F + 0.7F;
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleScale = 0.1F * (this.rand.nextFloat() * this.rand.nextFloat() * 6.0F + 1.0F);
        this.maxAge = (int)(16.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D)) + 2;
        this.selectSpriteWithAge(spriteWithAge);
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

        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        }
        else
        {
            this.selectSpriteWithAge(this.spriteWithAge);
            this.motionY += 0.004D;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.9F;
            this.motionY *= (double)0.9F;
            this.motionZ *= (double)0.9F;

            if (this.onGround)
            {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }
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
            return new PoofParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
