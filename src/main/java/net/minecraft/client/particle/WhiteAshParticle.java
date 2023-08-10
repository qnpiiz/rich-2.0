package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class WhiteAshParticle extends RisingParticle
{
    protected WhiteAshParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, 0.1F, -0.1F, 0.1F, motionX, motionY, motionZ, scale, spriteWithAge, 0.0F, 20, -5.0E-4D, false);
        this.particleRed = 0.7294118F;
        this.particleGreen = 0.69411767F;
        this.particleBlue = 0.7607843F;
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
            Random random = worldIn.rand;
            double d0 = (double)random.nextFloat() * -1.9D * (double)random.nextFloat() * 0.1D;
            double d1 = (double)random.nextFloat() * -0.5D * (double)random.nextFloat() * 0.1D * 5.0D;
            double d2 = (double)random.nextFloat() * -1.9D * (double)random.nextFloat() * 0.1D;
            return new WhiteAshParticle(worldIn, x, y, z, d0, d1, d2, 1.0F, this.spriteSet);
        }
    }
}
