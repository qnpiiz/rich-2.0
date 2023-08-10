package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SmokeParticle extends RisingParticle
{
    protected SmokeParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, 0.1F, 0.1F, 0.1F, motionX, motionY, motionZ, scale, spriteWithAge, 0.3F, 8, 0.004D, true);
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
            return new SmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1.0F, this.spriteSet);
        }
    }
}
