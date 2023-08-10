package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;

public class CritParticle extends SpriteTexturedParticle
{
    private CritParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX *= (double)0.1F;
        this.motionY *= (double)0.1F;
        this.motionZ *= (double)0.1F;
        this.motionX += motionX * 0.4D;
        this.motionY += motionY * 0.4D;
        this.motionZ += motionZ * 0.4D;
        float f = (float)(Math.random() * (double)0.3F + (double)0.6F);
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleScale *= 0.75F;
        this.maxAge = Math.max((int)(6.0D / (Math.random() * 0.8D + 0.6D)), 1);
        this.canCollide = false;
        this.tick();
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
            this.move(this.motionX, this.motionY, this.motionZ);
            this.particleGreen = (float)((double)this.particleGreen * 0.96D);
            this.particleBlue = (float)((double)this.particleBlue * 0.9D);
            this.motionX *= (double)0.7F;
            this.motionY *= (double)0.7F;
            this.motionZ *= (double)0.7F;
            this.motionY -= (double)0.02F;

            if (this.onGround)
            {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }
        }
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class DamageIndicatorFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public DamageIndicatorFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            CritParticle critparticle = new CritParticle(worldIn, x, y, z, xSpeed, ySpeed + 1.0D, zSpeed);
            critparticle.setMaxAge(20);
            critparticle.selectSpriteRandomly(this.spriteSet);
            return critparticle;
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
            CritParticle critparticle = new CritParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            critparticle.selectSpriteRandomly(this.spriteSet);
            return critparticle;
        }
    }

    public static class MagicFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public MagicFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            CritParticle critparticle = new CritParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            critparticle.particleRed *= 0.3F;
            critparticle.particleGreen *= 0.8F;
            critparticle.selectSpriteRandomly(this.spriteSet);
            return critparticle;
        }
    }
}
