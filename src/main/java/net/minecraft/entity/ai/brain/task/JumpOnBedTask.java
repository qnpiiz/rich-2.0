package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class JumpOnBedTask extends Task<MobEntity>
{
    private final float speed;
    @Nullable
    private BlockPos bedPos;
    private int field_220472_c;
    private int field_220473_d;
    private int field_220474_e;

    public JumpOnBedTask(float speed)
    {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
        this.speed = speed;
    }

    protected boolean shouldExecute(ServerWorld worldIn, MobEntity owner)
    {
        return owner.isChild() && this.func_220469_b(worldIn, owner);
    }

    protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        super.startExecuting(worldIn, entityIn, gameTimeIn);
        this.getBed(entityIn).ifPresent((bedPos) ->
        {
            this.bedPos = bedPos;
            this.field_220472_c = 100;
            this.field_220473_d = 3 + worldIn.rand.nextInt(4);
            this.field_220474_e = 0;
            this.setWalkTarget(entityIn, bedPos);
        });
    }

    protected void resetTask(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        super.resetTask(worldIn, entityIn, gameTimeIn);
        this.bedPos = null;
        this.field_220472_c = 0;
        this.field_220473_d = 0;
        this.field_220474_e = 0;
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        return entityIn.isChild() && this.bedPos != null && this.isBed(worldIn, this.bedPos) && !this.func_220464_e(worldIn, entityIn) && !this.func_220462_f(worldIn, entityIn);
    }

    protected boolean isTimedOut(long gameTime)
    {
        return false;
    }

    protected void updateTask(ServerWorld worldIn, MobEntity owner, long gameTime)
    {
        if (!this.func_220468_c(worldIn, owner))
        {
            --this.field_220472_c;
        }
        else if (this.field_220474_e > 0)
        {
            --this.field_220474_e;
        }
        else
        {
            if (this.func_220465_d(worldIn, owner))
            {
                owner.getJumpController().setJumping();
                --this.field_220473_d;
                this.field_220474_e = 5;
            }
        }
    }

    private void setWalkTarget(MobEntity mob, BlockPos pos)
    {
        mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.speed, 0));
    }

    private boolean func_220469_b(ServerWorld world, MobEntity mob)
    {
        return this.func_220468_c(world, mob) || this.getBed(mob).isPresent();
    }

    private boolean func_220468_c(ServerWorld world, MobEntity mob)
    {
        BlockPos blockpos = mob.getPosition();
        BlockPos blockpos1 = blockpos.down();
        return this.isBed(world, blockpos) || this.isBed(world, blockpos1);
    }

    private boolean func_220465_d(ServerWorld world, MobEntity mob)
    {
        return this.isBed(world, mob.getPosition());
    }

    private boolean isBed(ServerWorld world, BlockPos pos)
    {
        return world.getBlockState(pos).isIn(BlockTags.BEDS);
    }

    private Optional<BlockPos> getBed(MobEntity p_220463_1_)
    {
        return p_220463_1_.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
    }

    private boolean func_220464_e(ServerWorld world, MobEntity mob)
    {
        return !this.func_220468_c(world, mob) && this.field_220472_c <= 0;
    }

    private boolean func_220462_f(ServerWorld world, MobEntity mob)
    {
        return this.func_220468_c(world, mob) && this.field_220473_d <= 0;
    }
}
