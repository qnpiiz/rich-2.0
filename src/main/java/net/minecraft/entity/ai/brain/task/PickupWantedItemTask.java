package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.server.ServerWorld;

public class PickupWantedItemTask<E extends LivingEntity> extends Task<E>
{
    private final Predicate<E> field_233906_b_;
    private final int field_233907_c_;
    private final float field_233908_d_;

    public PickupWantedItemTask(float p_i231520_1_, boolean p_i231520_2_, int p_i231520_3_)
    {
        this((p_233910_0_) ->
        {
            return true;
        }, p_i231520_1_, p_i231520_2_, p_i231520_3_);
    }

    public PickupWantedItemTask(Predicate<E> p_i231521_1_, float p_i231521_2_, boolean p_i231521_3_, int p_i231521_4_)
    {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, p_i231521_3_ ? MemoryModuleStatus.REGISTERED : MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleStatus.VALUE_PRESENT));
        this.field_233906_b_ = p_i231521_1_;
        this.field_233907_c_ = p_i231521_4_;
        this.field_233908_d_ = p_i231521_2_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        return this.field_233906_b_.test(owner) && this.func_233909_a_(owner).isEntityInRange(owner, (double)this.field_233907_c_);
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        BrainUtil.setTargetEntity(entityIn, this.func_233909_a_(entityIn), this.field_233908_d_, 0);
    }

    private ItemEntity func_233909_a_(E p_233909_1_)
    {
        return p_233909_1_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
    }
}
