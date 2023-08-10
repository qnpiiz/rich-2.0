package net.minecraft.client.particle;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.IItemProvider;

public class BarrierParticle extends SpriteTexturedParticle
{
    private BarrierParticle(ClientWorld world, double x, double y, double z, IItemProvider itemProvider)
    {
        super(world, x, y, z);
        this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(itemProvider));
        this.particleGravity = 0.0F;
        this.maxAge = 80;
        this.canCollide = false;
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.TERRAIN_SHEET;
    }

    public float getScale(float scaleFactor)
    {
        return 0.5F;
    }

    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new BarrierParticle(worldIn, x, y, z, Blocks.BARRIER.asItem());
        }
    }
}
