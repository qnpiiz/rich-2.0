package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SpitParticle extends PoofParticle
{
    private SpitParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, motionX, motionY, motionZ, spriteWithAge);
        this.particleGravity = 0.5F;
    }

    public void tick()
    {
        super.tick();
        this.motionY -= 0.004D + 0.04D * (double)this.particleGravity;
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
            return new SpitParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
