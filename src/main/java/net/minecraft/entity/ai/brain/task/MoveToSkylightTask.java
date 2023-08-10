package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class MoveToSkylightTask extends Task<LivingEntity>
{
    private final float speed;

    public MoveToSkylightTask(float speed)
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
        this.speed = speed;
    }

    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        Optional<Vector3d> optional = Optional.ofNullable(this.findSkylightPosition(worldIn, entityIn));

        if (optional.isPresent())
        {
            entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220492_1_) ->
            {
                return new WalkTarget(p_220492_1_, this.speed, 0);
            }));
        }
    }

    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner)
    {
        return !worldIn.canSeeSky(owner.getPosition());
    }

    @Nullable
    private Vector3d findSkylightPosition(ServerWorld world, LivingEntity walker)
    {
        Random random = walker.getRNG();
        BlockPos blockpos = walker.getPosition();

        for (int i = 0; i < 10; ++i)
        {
            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (func_226306_a_(world, walker, blockpos1))
            {
                return Vector3d.copyCenteredHorizontally(blockpos1);
            }
        }

        return null;
    }

    public static boolean func_226306_a_(ServerWorld world, LivingEntity walker, BlockPos p_226306_2_)
    {
        return world.canSeeSky(p_226306_2_) && (double)world.getHeight(Heightmap.Type.MOTION_BLOCKING, p_226306_2_).getY() <= walker.getPosY();
    }
}
