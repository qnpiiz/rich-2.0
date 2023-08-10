package net.minecraft.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HoneyBlock extends BreakableBlock
{
    protected static final VoxelShape SHAPES = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    public HoneyBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    private static boolean hasSlideEffects(Entity entity)
    {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TNTEntity || entity instanceof BoatEntity;
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPES;
    }

    /**
     * Block's chance to react to a living entity falling on it.
     */
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        entityIn.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);

        if (!worldIn.isRemote)
        {
            worldIn.setEntityState(entityIn, (byte)54);
        }

        if (entityIn.onLivingFall(fallDistance, 0.2F))
        {
            entityIn.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (this.isSliding(pos, entityIn))
        {
            this.triggerSlideDownBlock(entityIn, pos);
            this.setSlideVelocity(entityIn);
            this.slideEffects(worldIn, entityIn);
        }

        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    private boolean isSliding(BlockPos pos, Entity entity)
    {
        if (entity.isOnGround())
        {
            return false;
        }
        else if (entity.getPosY() > (double)pos.getY() + 0.9375D - 1.0E-7D)
        {
            return false;
        }
        else if (entity.getMotion().y >= -0.08D)
        {
            return false;
        }
        else
        {
            double d0 = Math.abs((double)pos.getX() + 0.5D - entity.getPosX());
            double d1 = Math.abs((double)pos.getZ() + 0.5D - entity.getPosZ());
            double d2 = 0.4375D + (double)(entity.getWidth() / 2.0F);
            return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
        }
    }

    private void triggerSlideDownBlock(Entity entity, BlockPos pos)
    {
        if (entity instanceof ServerPlayerEntity && entity.world.getGameTime() % 20L == 0L)
        {
            CriteriaTriggers.SLIDE_DOWN_BLOCK.test((ServerPlayerEntity)entity, entity.world.getBlockState(pos));
        }
    }

    private void setSlideVelocity(Entity entity)
    {
        Vector3d vector3d = entity.getMotion();

        if (vector3d.y < -0.13D)
        {
            double d0 = -0.05D / vector3d.y;
            entity.setMotion(new Vector3d(vector3d.x * d0, -0.05D, vector3d.z * d0));
        }
        else
        {
            entity.setMotion(new Vector3d(vector3d.x, -0.05D, vector3d.z));
        }

        entity.fallDistance = 0.0F;
    }

    private void slideEffects(World world, Entity entity)
    {
        if (hasSlideEffects(entity))
        {
            if (world.rand.nextInt(5) == 0)
            {
                entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            }

            if (!world.isRemote && world.rand.nextInt(5) == 0)
            {
                world.setEntityState(entity, (byte)53);
            }
        }
    }

    public static void entitySlideParticles(Entity entity)
    {
        slideParticles(entity, 5);
    }

    public static void livingSlideParticles(Entity entity)
    {
        slideParticles(entity, 10);
    }

    private static void slideParticles(Entity entity, int particleCount)
    {
        if (entity.world.isRemote)
        {
            BlockState blockstate = Blocks.HONEY_BLOCK.getDefaultState();

            for (int i = 0; i < particleCount; ++i)
            {
                entity.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate), entity.getPosX(), entity.getPosY(), entity.getPosZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
