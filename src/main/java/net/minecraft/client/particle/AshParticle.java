package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class AshParticle extends RisingParticle
{
    protected AshParticle(ClientWorld world, double x, double y, double z, double motionMultX, double motionMultY, double motionMultZ, float scale, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, 0.1F, -0.1F, 0.1F, motionMultX, motionMultY, motionMultZ, scale, spriteWithAge, 0.5F, 20, -0.004D, false);
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
            return new AshParticle(worldIn, x, y, z, 0.0D, 0.0D, 0.0D, 1.0F, this.spriteSet);
        }
    }
}
