package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;

public class SimpleAnimatedParticle extends SpriteTexturedParticle
{
    protected final IAnimatedSprite spriteWithAge;
    private final float yAccel;
    private float baseAirFriction = 0.91F;
    private float fadeTargetRed;
    private float fadeTargetGreen;
    private float fadeTargetBlue;
    private boolean fadingColor;

    protected SimpleAnimatedParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite spriteWithAge, float yAccel)
    {
        super(world, x, y, z);
        this.spriteWithAge = spriteWithAge;
        this.yAccel = yAccel;
    }

    public void setColor(int color)
    {
        float f = (float)((color & 16711680) >> 16) / 255.0F;
        float f1 = (float)((color & 65280) >> 8) / 255.0F;
        float f2 = (float)((color & 255) >> 0) / 255.0F;
        float f3 = 1.0F;
        this.setColor(f * 1.0F, f1 * 1.0F, f2 * 1.0F);
    }

    /**
     * sets a color for the particle to drift toward (20% closer each tick, never actually getting very close)
     */
    public void setColorFade(int rgb)
    {
        this.fadeTargetRed = (float)((rgb & 16711680) >> 16) / 255.0F;
        this.fadeTargetGreen = (float)((rgb & 65280) >> 8) / 255.0F;
        this.fadeTargetBlue = (float)((rgb & 255) >> 0) / 255.0F;
        this.fadingColor = true;
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

            if (this.age > this.maxAge / 2)
            {
                this.setAlphaF(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);

                if (this.fadingColor)
                {
                    this.particleRed += (this.fadeTargetRed - this.particleRed) * 0.2F;
                    this.particleGreen += (this.fadeTargetGreen - this.particleGreen) * 0.2F;
                    this.particleBlue += (this.fadeTargetBlue - this.particleBlue) * 0.2F;
                }
            }

            this.motionY += (double)this.yAccel;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)this.baseAirFriction;
            this.motionY *= (double)this.baseAirFriction;
            this.motionZ *= (double)this.baseAirFriction;

            if (this.onGround)
            {
                this.motionX *= (double)0.7F;
                this.motionZ *= (double)0.7F;
            }
        }
    }

    public int getBrightnessForRender(float partialTick)
    {
        return 15728880;
    }

    protected void setBaseAirFriction(float airFriction)
    {
        this.baseAirFriction = airFriction;
    }
}
