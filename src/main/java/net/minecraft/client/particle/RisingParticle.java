package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class RisingParticle extends SpriteTexturedParticle
{
    private final IAnimatedSprite spriteWithAge;
    private final double yAccel;

    protected RisingParticle(ClientWorld world, double x, double y, double z, float defaultMotionMultX, float defaultMotionMultY, float defaultMotionMultZ, double motionX, double motionY, double motionZ, float scale, IAnimatedSprite spriteWithAge, float colorMult, int maxAge, double yAccel, boolean canCollide)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.yAccel = yAccel;
        this.spriteWithAge = spriteWithAge;
        this.motionX *= (double)defaultMotionMultX;
        this.motionY *= (double)defaultMotionMultY;
        this.motionZ *= (double)defaultMotionMultZ;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;
        float f = world.rand.nextFloat() * colorMult;
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleScale *= 0.75F * scale;
        this.maxAge = (int)((double)maxAge / ((double)world.rand.nextFloat() * 0.8D + 0.2D));
        this.maxAge = (int)((float)this.maxAge * scale);
        this.maxAge = Math.max(this.maxAge, 1);
        this.selectSpriteWithAge(spriteWithAge);
        this.canCollide = canCollide;
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
            this.selectSpriteWithAge(this.spriteWithAge);
            this.motionY += this.yAccel;
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
}
