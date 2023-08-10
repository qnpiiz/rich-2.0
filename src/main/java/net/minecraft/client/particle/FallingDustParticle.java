package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FallingDustParticle extends SpriteTexturedParticle
{
    private final float rotSpeed;
    private final IAnimatedSprite spriteWithAge;

    private FallingDustParticle(ClientWorld world, double x, double y, double z, float red, float green, float blue, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z);
        this.spriteWithAge = spriteWithAge;
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        float f = 0.9F;
        this.particleScale *= 0.67499995F;
        int i = (int)(32.0D / (Math.random() * 0.8D + 0.2D));
        this.maxAge = (int)Math.max((float)i * 0.9F, 1.0F);
        this.selectSpriteWithAge(spriteWithAge);
        this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
        this.particleAngle = (float)Math.random() * ((float)Math.PI * 2F);
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
            this.prevParticleAngle = this.particleAngle;
            this.particleAngle += (float)Math.PI * this.rotSpeed * 2.0F;

            if (this.onGround)
            {
                this.prevParticleAngle = this.particleAngle = 0.0F;
            }

            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionY -= (double)0.003F;
            this.motionY = Math.max(this.motionY, (double) - 0.14F);
        }
    }

    public static class Factory implements IParticleFactory<BlockParticleData>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSetIn)
        {
            this.spriteSet = spriteSetIn;
        }

        @Nullable
        public Particle makeParticle(BlockParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            BlockState blockstate = typeIn.getBlockState();

            if (!blockstate.isAir() && blockstate.getRenderType() == BlockRenderType.INVISIBLE)
            {
                return null;
            }
            else
            {
                BlockPos blockpos = new BlockPos(x, y, z);
                int i = Minecraft.getInstance().getBlockColors().getColorOrMaterialColor(blockstate, worldIn, blockpos);

                if (blockstate.getBlock() instanceof FallingBlock)
                {
                    i = ((FallingBlock)blockstate.getBlock()).getDustColor(blockstate, worldIn, blockpos);
                }

                float f = (float)(i >> 16 & 255) / 255.0F;
                float f1 = (float)(i >> 8 & 255) / 255.0F;
                float f2 = (float)(i & 255) / 255.0F;
                return new FallingDustParticle(worldIn, x, y, z, f, f1, f2, this.spriteSet);
            }
        }
    }
}
