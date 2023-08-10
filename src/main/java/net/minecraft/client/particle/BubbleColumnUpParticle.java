package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;

public class BubbleColumnUpParticle extends SpriteTexturedParticle
{
    private BubbleColumnUpParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z);
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.motionX = motionX * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
        this.motionY = motionY * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
        this.motionZ = motionZ * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
        this.maxAge = (int)(40.0D / (Math.random() * 0.8D + 0.2D));
    }

    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY += 0.005D;

        if (this.maxAge-- <= 0)
        {
            this.setExpired();
        }
        else
        {
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.85F;
            this.motionY *= (double)0.85F;
            this.motionZ *= (double)0.85F;

            if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).isTagged(FluidTags.WATER))
            {
                this.setExpired();
            }
        }
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
            BubbleColumnUpParticle bubblecolumnupparticle = new BubbleColumnUpParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            bubblecolumnupparticle.selectSpriteRandomly(this.spriteSet);
            return bubblecolumnupparticle;
        }
    }
}
