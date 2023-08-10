package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class PortalParticle extends SpriteTexturedParticle
{
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;

    protected PortalParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.portalPosX = this.posX;
        this.portalPosY = this.posY;
        this.portalPosZ = this.posZ;
        this.particleScale = 0.1F * (this.rand.nextFloat() * 0.2F + 0.5F);
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = f * 0.9F;
        this.particleGreen = f * 0.3F;
        this.particleBlue = f;
        this.maxAge = (int)(Math.random() * 10.0D) + 40;
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double x, double y, double z)
    {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    public float getScale(float scaleFactor)
    {
        float f = ((float)this.age + scaleFactor) / (float)this.maxAge;
        f = 1.0F - f;
        f = f * f;
        f = 1.0F - f;
        return this.particleScale * f;
    }

    public int getBrightnessForRender(float partialTick)
    {
        int i = super.getBrightnessForRender(partialTick);
        float f = (float)this.age / (float)this.maxAge;
        f = f * f;
        f = f * f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k = k + (int)(f * 15.0F * 16.0F);

        if (k > 240)
        {
            k = 240;
        }

        return j | k << 16;
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
            float f = (float)this.age / (float)this.maxAge;
            float f1 = -f + f * f * 2.0F;
            float f2 = 1.0F - f1;
            this.posX = this.portalPosX + this.motionX * (double)f2;
            this.posY = this.portalPosY + this.motionY * (double)f2 + (double)(1.0F - f);
            this.posZ = this.portalPosZ + this.motionZ * (double)f2;
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
            PortalParticle portalparticle = new PortalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            portalparticle.selectSpriteRandomly(this.spriteSet);
            return portalparticle;
        }
    }
}
