package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SoulParticle extends DeceleratingParticle
{
    private final IAnimatedSprite spriteWithAge;

    private SoulParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.spriteWithAge = spriteWithAge;
        this.multiplyParticleScaleBy(1.5F);
        this.selectSpriteWithAge(spriteWithAge);
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick()
    {
        super.tick();

        if (!this.isExpired)
        {
            this.selectSpriteWithAge(this.spriteWithAge);
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
            SoulParticle soulparticle = new SoulParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            soulparticle.setAlphaF(1.0F);
            return soulparticle;
        }
    }
}
