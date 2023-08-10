package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;

public class SquidInkParticle extends SimpleAnimatedParticle
{
    private SquidInkParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, spriteWithAge, 0.0F);
        this.particleScale = 0.5F;
        this.setAlphaF(1.0F);
        this.setColor(0.0F, 0.0F, 0.0F);
        this.maxAge = (int)((double)(this.particleScale * 12.0F) / (Math.random() * (double)0.8F + (double)0.2F));
        this.selectSpriteWithAge(spriteWithAge);
        this.canCollide = false;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.setBaseAirFriction(0.0F);
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

            if (this.age > this.maxAge / 2)
            {
                this.setAlphaF(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).isAir())
            {
                this.motionY -= (double)0.008F;
            }

            this.motionX *= (double)0.92F;
            this.motionY *= (double)0.92F;
            this.motionZ *= (double)0.92F;

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
            return new SquidInkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
