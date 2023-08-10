package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SpellParticle extends SpriteTexturedParticle
{
    private static final Random RANDOM = new Random();
    private final IAnimatedSprite spriteWithAge;

    private SpellParticle(ClientWorld p_i232429_1_, double x, double y, double z, double p_i232429_8_, double motionY, double p_i232429_12_, IAnimatedSprite spriteWithAge)
    {
        super(p_i232429_1_, x, y, z, 0.5D - RANDOM.nextDouble(), motionY, 0.5D - RANDOM.nextDouble());
        this.spriteWithAge = spriteWithAge;
        this.motionY *= (double)0.2F;

        if (p_i232429_8_ == 0.0D && p_i232429_12_ == 0.0D)
        {
            this.motionX *= (double)0.1F;
            this.motionZ *= (double)0.1F;
        }

        this.particleScale *= 0.75F;
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.canCollide = false;
        this.selectSpriteWithAge(spriteWithAge);
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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

            if (this.posY == this.prevPosY)
            {
                this.motionX *= 1.1D;
                this.motionZ *= 1.1D;
            }

            this.motionX *= (double)0.96F;
            this.motionY *= (double)0.96F;
            this.motionZ *= (double)0.96F;

            if (this.onGround)
            {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }
        }
    }

    public static class AmbientMobFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public AmbientMobFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            Particle particle = new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setAlphaF(0.15F);
            particle.setColor((float)xSpeed, (float)ySpeed, (float)zSpeed);
            return particle;
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
            return new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    public static class InstantFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public InstantFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    public static class MobFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public MobFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            Particle particle = new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setColor((float)xSpeed, (float)ySpeed, (float)zSpeed);
            return particle;
        }
    }

    public static class WitchFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public WitchFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            SpellParticle spellparticle = new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            float f = worldIn.rand.nextFloat() * 0.5F + 0.35F;
            spellparticle.setColor(1.0F * f, 0.0F * f, 1.0F * f);
            return spellparticle;
        }
    }
}
