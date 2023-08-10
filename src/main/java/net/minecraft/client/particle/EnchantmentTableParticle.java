package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class EnchantmentTableParticle extends SpriteTexturedParticle
{
    private final double coordX;
    private final double coordY;
    private final double coordZ;

    private EnchantmentTableParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.coordX = x;
        this.coordY = y;
        this.coordZ = z;
        this.prevPosX = x + motionX;
        this.prevPosY = y + motionY;
        this.prevPosZ = z + motionZ;
        this.posX = this.prevPosX;
        this.posY = this.prevPosY;
        this.posZ = this.prevPosZ;
        this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.2F);
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = 0.9F * f;
        this.particleGreen = 0.9F * f;
        this.particleBlue = f;
        this.canCollide = false;
        this.maxAge = (int)(Math.random() * 10.0D) + 30;
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
            f = 1.0F - f;
            float f1 = 1.0F - f;
            f1 = f1 * f1;
            f1 = f1 * f1;
            this.posX = this.coordX + this.motionX * (double)f;
            this.posY = this.coordY + this.motionY * (double)f - (double)(f1 * 1.2F);
            this.posZ = this.coordZ + this.motionZ * (double)f;
        }
    }

    public static class EnchantmentTable implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public EnchantmentTable(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            EnchantmentTableParticle enchantmenttableparticle = new EnchantmentTableParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            enchantmenttableparticle.selectSpriteRandomly(this.spriteSet);
            return enchantmenttableparticle;
        }
    }

    public static class NautilusFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public NautilusFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            EnchantmentTableParticle enchantmenttableparticle = new EnchantmentTableParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            enchantmenttableparticle.selectSpriteRandomly(this.spriteSet);
            return enchantmenttableparticle;
        }
    }
}
