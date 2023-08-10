package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class UnderwaterParticle extends SpriteTexturedParticle
{
    private UnderwaterParticle(ClientWorld world, double x, double y, double z)
    {
        super(world, x, y - 0.125D, z);
        this.particleRed = 0.4F;
        this.particleGreen = 0.4F;
        this.particleBlue = 0.7F;
        this.setSize(0.01F, 0.01F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        this.canCollide = false;
    }

    private UnderwaterParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y - 0.125D, z, motionX, motionY, motionZ);
        this.setSize(0.01F, 0.01F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.6F;
        this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        this.canCollide = false;
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

        if (this.maxAge-- <= 0)
        {
            this.setExpired();
        }
        else
        {
            this.move(this.motionX, this.motionY, this.motionZ);
        }
    }

    public static class CrimsonSporeFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public CrimsonSporeFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            Random random = worldIn.rand;
            double d0 = random.nextGaussian() * (double)1.0E-6F;
            double d1 = random.nextGaussian() * (double)1.0E-4F;
            double d2 = random.nextGaussian() * (double)1.0E-6F;
            UnderwaterParticle underwaterparticle = new UnderwaterParticle(worldIn, x, y, z, d0, d1, d2);
            underwaterparticle.selectSpriteRandomly(this.spriteSet);
            underwaterparticle.setColor(0.9F, 0.4F, 0.5F);
            return underwaterparticle;
        }
    }

    public static class UnderwaterFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public UnderwaterFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            UnderwaterParticle underwaterparticle = new UnderwaterParticle(worldIn, x, y, z);
            underwaterparticle.selectSpriteRandomly(this.spriteSet);
            return underwaterparticle;
        }
    }

    public static class WarpedSporeFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public WarpedSporeFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            double d0 = (double)worldIn.rand.nextFloat() * -1.9D * (double)worldIn.rand.nextFloat() * 0.1D;
            UnderwaterParticle underwaterparticle = new UnderwaterParticle(worldIn, x, y, z, 0.0D, d0, 0.0D);
            underwaterparticle.selectSpriteRandomly(this.spriteSet);
            underwaterparticle.setColor(0.1F, 0.1F, 0.3F);
            underwaterparticle.setSize(0.001F, 0.001F);
            return underwaterparticle;
        }
    }
}
