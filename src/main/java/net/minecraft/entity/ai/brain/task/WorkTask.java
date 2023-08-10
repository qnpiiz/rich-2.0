package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class WorkTask extends Task<CreatureEntity>
{
    private final MemoryModuleType<GlobalPos> field_220565_a;
    private long field_220566_b;
    private final int field_220567_c;
    private float field_242305_e;

    public WorkTask(MemoryModuleType<GlobalPos> p_i241909_1_, float p_i241909_2_, int p_i241909_3_)
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, p_i241909_1_, MemoryModuleStatus.VALUE_PRESENT));
        this.field_220565_a = p_i241909_1_;
        this.field_242305_e = p_i241909_2_;
        this.field_220567_c = p_i241909_3_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner)
    {
        Optional<GlobalPos> optional = owner.getBrain().getMemory(this.field_220565_a);
        return optional.isPresent() && worldIn.getDimensionKey() == optional.get().getDimension() && optional.get().getPos().withinDistance(owner.getPositionVec(), (double)this.field_220567_c);
    }

    protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn)
    {
        if (gameTimeIn > this.field_220566_b)
        {
            Optional<Vector3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(entityIn, 8, 6));
            entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220564_1_) ->
            {
                return new WalkTarget(p_220564_1_, this.field_242305_e, 1);
            }));
            this.field_220566_b = gameTimeIn + 180L;
        }
    }
}
