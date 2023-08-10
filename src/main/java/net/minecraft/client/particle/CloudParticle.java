package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;

public class CloudParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteSetWithAge;

    private CloudParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteSetWithAge)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.spriteSetWithAge = spriteSetWithAge;
        float f = 2.5F;
        this.motionX *= (double)0.1F;
        this.motionY *= (double)0.1F;
        this.motionZ *= (double)0.1F;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;
        float f1 = 1.0F - (float)(Math.random() * (double)0.3F);
        this.particleRed = f1;
        this.particleGreen = f1;
        this.particleBlue = f1;
        this.particleScale *= 1.875F;
        int i = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
        this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
        this.canCollide = false;
        this.selectSpriteWithAge(spriteSetWithAge);
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            this.selectSpriteWithAge(this.spriteSetWithAge);
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.96F;
            this.motionY *= (double)0.96F;
            this.motionZ *= (double)0.96F;
            PlayerEntity playerentity = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 2.0D, false);

            if (playerentity != null)
            {
                double d0 = playerentity.getPosY();

                if (this.posY > d0)
                {
                    this.posY += (d0 - this.posY) * 0.2D;
                    this.motionY += (playerentity.getMotion().y - this.motionY) * 0.2D;
                    this.setPosition(this.posX, this.posY, this.posZ);
                }
            }

            if (this.onGround)
            {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }
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
            return new CloudParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    public static class SneezeFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public SneezeFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            Particle particle = new CloudParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setColor(200.0F, 50.0F, 120.0F);
            particle.setAlphaF(0.4F);
            return particle;
        }
    }
}
