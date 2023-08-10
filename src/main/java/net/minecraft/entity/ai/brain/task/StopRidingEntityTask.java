package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class StopRidingEntityTask<E extends LivingEntity, T extends Entity> extends Task<E>
{
    private final int field_233890_b_;
    private final BiPredicate<E, Entity> field_233891_c_;

    public StopRidingEntityTask(int p_i231515_1_, BiPredicate<E, Entity> p_i231515_2_)
    {
        super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryModuleStatus.REGISTERED));
        this.field_233890_b_ = p_i231515_1_;
        this.field_233891_c_ = p_i231515_2_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        Entity entity = owner.getRidingEntity();
        Entity entity1 = owner.getBrain().getMemory(MemoryModuleType.RIDE_TARGET).orElse((Entity)null);

        if (entity == null && entity1 == null)
        {
            return false;
        }
        else
        {
            Entity entity2 = entity == null ? entity1 : entity;
            return !this.func_233892_a_(owner, entity2) || this.field_233891_c_.test(owner, entity2);
        }
    }

    private boolean func_233892_a_(E p_233892_1_, Entity p_233892_2_)
    {
        return p_233892_2_.isAlive() && p_233892_2_.isEntityInRange(p_233892_1_, (double)this.field_233890_b_) && p_233892_2_.world == p_233892_1_.world;
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        entityIn.stopRiding();
        entityIn.getBrain().removeMemory(MemoryModuleType.RIDE_TARGET);
    }
}
