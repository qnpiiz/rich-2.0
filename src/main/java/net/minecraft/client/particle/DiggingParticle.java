package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;

public class DiggingParticle extends SpriteTexturedParticle
{
    private final BlockState sourceState;
    private BlockPos sourcePos;
    private final float u;
    private final float v;

    public DiggingParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, BlockState state)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.sourceState = state;
        this.setSprite(Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
        this.particleGravity = 1.0F;
        this.particleRed = 0.6F;
        this.particleGreen = 0.6F;
        this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
        this.u = this.rand.nextFloat() * 3.0F;
        this.v = this.rand.nextFloat() * 3.0F;
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.TERRAIN_SHEET;
    }

    /**
     * Sets the position of the block that this particle came from. Used for calculating texture and color multiplier.
     */
    public DiggingParticle setBlockPos(BlockPos pos)
    {
        this.sourcePos = pos;

        if (this.sourceState.isIn(Blocks.GRASS_BLOCK))
        {
            return this;
        }
        else
        {
            this.multiplyColor(pos);
            return this;
        }
    }

    public DiggingParticle init()
    {
        this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);

        if (this.sourceState.isIn(Blocks.GRASS_BLOCK))
        {
            return this;
        }
        else
        {
            this.multiplyColor(this.sourcePos);
            return this;
        }
    }

    protected void multiplyColor(@Nullable BlockPos pos)
    {
        int i = Minecraft.getInstance().getBlockColors().getColor(this.sourceState, this.world, pos, 0);
        this.particleRed *= (float)(i >> 16 & 255) / 255.0F;
        this.particleGreen *= (float)(i >> 8 & 255) / 255.0F;
        this.particleBlue *= (float)(i & 255) / 255.0F;
    }

    protected float getMinU()
    {
        return this.sprite.getInterpolatedU((double)((this.u + 1.0F) / 4.0F * 16.0F));
    }

    protected float getMaxU()
    {
        return this.sprite.getInterpolatedU((double)(this.u / 4.0F * 16.0F));
    }

    protected float getMinV()
    {
        return this.sprite.getInterpolatedV((double)(this.v / 4.0F * 16.0F));
    }

    protected float getMaxV()
    {
        return this.sprite.getInterpolatedV((double)((this.v + 1.0F) / 4.0F * 16.0F));
    }

    public int getBrightnessForRender(float partialTick)
    {
        int i = super.getBrightnessForRender(partialTick);
        int j = 0;

        if (this.world.isBlockLoaded(this.sourcePos))
        {
            j = WorldRenderer.getCombinedLight(this.world, this.sourcePos);
        }

        return i == 0 ? j : i;
    }

    public static class Factory implements IParticleFactory<BlockParticleData>
    {
        public Particle makeParticle(BlockParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            BlockState blockstate = typeIn.getBlockState();
            return !blockstate.isAir() && !blockstate.isIn(Blocks.MOVING_PISTON) ? (new DiggingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, blockstate)).init() : null;
        }
    }
}
