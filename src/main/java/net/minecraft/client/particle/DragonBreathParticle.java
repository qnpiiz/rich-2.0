package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;

public class DragonBreathParticle extends SpriteTexturedParticle
{
    private boolean hasHitGround;
    private final IAnimatedSprite spriteWithAge;

    private DragonBreathParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.particleRed = MathHelper.nextFloat(this.rand, 0.7176471F, 0.8745098F);
        this.particleGreen = MathHelper.nextFloat(this.rand, 0.0F, 0.0F);
        this.particleBlue = MathHelper.nextFloat(this.rand, 0.8235294F, 0.9764706F);
        this.particleScale *= 0.75F;
        this.maxAge = (int)(20.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D));
        this.hasHitGround = false;
        this.canCollide = false;
        this.spriteWithAge = spriteWithAge;
        this.selectSpriteWithAge(spriteWithAge);
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

            if (this.onGround)
            {
                this.motionY = 0.0D;
                this.hasHitGround = true;
            }

            if (this.hasHitGround)
            {
                this.motionY += 0.002D;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.posY == this.prevPosY)
            {
                this.motionX *= 1.1D;
                this.motionZ *= 1.1D;
            }

            this.motionX *= (double)0.96F;
            this.motionZ *= (double)0.96F;

            if (this.hasHitGround)
            {
                this.motionY *= (double)0.96F;
            }
        }
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public float getScale(float scaleFactor)
    {
        return this.particleScale * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
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
            return new DragonBreathParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
