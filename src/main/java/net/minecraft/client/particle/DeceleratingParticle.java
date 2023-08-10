package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;

public abstract class DeceleratingParticle extends SpriteTexturedParticle
{
    protected DeceleratingParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX = this.motionX * (double)0.01F + motionX;
        this.motionY = this.motionY * (double)0.01F + motionY;
        this.motionZ = this.motionZ * (double)0.01F + motionZ;
        this.posX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
        this.posY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
        this.posZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
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
            this.move(this.motionX, this.motionY, this.motionZ);
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
