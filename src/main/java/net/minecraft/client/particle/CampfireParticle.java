package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class CampfireParticle extends SpriteTexturedParticle
{
    private CampfireParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, boolean longLivingEmber)
    {
        super(world, x, y, z);
        this.multiplyParticleScaleBy(3.0F);
        this.setSize(0.25F, 0.25F);

        if (longLivingEmber)
        {
            this.maxAge = this.rand.nextInt(50) + 280;
        }
        else
        {
            this.maxAge = this.rand.nextInt(50) + 80;
        }

        this.particleGravity = 3.0E-6F;
        this.motionX = motionX;
        this.motionY = motionY + (double)(this.rand.nextFloat() / 500.0F);
        this.motionZ = motionZ;
    }

    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.age++ < this.maxAge && !(this.particleAlpha <= 0.0F))
        {
            this.motionX += (double)(this.rand.nextFloat() / 5000.0F * (float)(this.rand.nextBoolean() ? 1 : -1));
            this.motionZ += (double)(this.rand.nextFloat() / 5000.0F * (float)(this.rand.nextBoolean() ? 1 : -1));
            this.motionY -= (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.age >= this.maxAge - 60 && this.particleAlpha > 0.01F)
            {
                this.particleAlpha -= 0.015F;
            }
        }
        else
        {
            this.setExpired();
        }
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class CozySmokeFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public CozySmokeFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            CampfireParticle campfireparticle = new CampfireParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, false);
            campfireparticle.setAlphaF(0.9F);
            campfireparticle.selectSpriteRandomly(this.spriteSet);
            return campfireparticle;
        }
    }

    public static class SignalSmokeFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public SignalSmokeFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            CampfireParticle campfireparticle = new CampfireParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, true);
            campfireparticle.setAlphaF(0.95F);
            campfireparticle.selectSpriteRandomly(this.spriteSet);
            return campfireparticle;
        }
    }
}
