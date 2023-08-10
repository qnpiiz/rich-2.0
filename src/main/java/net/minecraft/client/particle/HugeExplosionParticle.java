package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;

public class HugeExplosionParticle extends MetaParticle
{
    private int timeSinceStart;
    private final int maximumTime = 8;

    private HugeExplosionParticle(ClientWorld world, double x, double y, double z)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public void tick()
    {
        for (int i = 0; i < 6; ++i)
        {
            double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            double d1 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            this.world.addParticle(ParticleTypes.EXPLOSION, d0, d1, d2, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0D, 0.0D);
        }

        ++this.timeSinceStart;

        if (this.timeSinceStart == this.maximumTime)
        {
            this.setExpired();
        }
    }

    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new HugeExplosionParticle(worldIn, x, y, z);
        }
    }
}
