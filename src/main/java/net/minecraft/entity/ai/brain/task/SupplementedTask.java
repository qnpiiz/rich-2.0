package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class SupplementedTask<E extends LivingEntity> extends Task<E>
{
    private final Predicate<E> field_233940_b_;
    private final Task <? super E > field_233941_c_;
    private final boolean field_233942_d_;

    public SupplementedTask(Map < MemoryModuleType<?>, MemoryModuleStatus > p_i231528_1_, Predicate<E> p_i231528_2_, Task <? super E > p_i231528_3_, boolean p_i231528_4_)
    {
        super(func_233943_a_(p_i231528_1_, p_i231528_3_.requiredMemoryState));
        this.field_233940_b_ = p_i231528_2_;
        this.field_233941_c_ = p_i231528_3_;
        this.field_233942_d_ = p_i231528_4_;
    }

    private static Map < MemoryModuleType<?>, MemoryModuleStatus > func_233943_a_(Map < MemoryModuleType<?>, MemoryModuleStatus > p_233943_0_, Map < MemoryModuleType<?>, MemoryModuleStatus > p_233943_1_)
    {
        Map < MemoryModuleType<?>, MemoryModuleStatus > map = Maps.newHashMap();
        map.putAll(p_233943_0_);
        map.putAll(p_233943_1_);
        return map;
    }

    public SupplementedTask(Predicate<E> p_i231529_1_, Task <? super E > p_i231529_2_)
    {
        this(ImmutableMap.of(), p_i231529_1_, p_i231529_2_, false);
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        return this.field_233940_b_.test(owner) && this.field_233941_c_.shouldExecute(worldIn, owner);
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        return this.field_233942_d_ && this.field_233940_b_.test(entityIn) && this.field_233941_c_.shouldContinueExecuting(worldIn, entityIn, gameTimeIn);
    }

    protected boolean isTimedOut(long gameTime)
    {
        return false;
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        this.field_233941_c_.startExecuting(worldIn, entityIn, gameTimeIn);
    }

    protected void updateTask(ServerWorld worldIn, E owner, long gameTime)
    {
        this.field_233941_c_.updateTask(worldIn, owner, gameTime);
    }

    protected void resetTask(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        this.field_233941_c_.resetTask(worldIn, entityIn, gameTimeIn);
    }

    public String toString()
    {
        return "RunIf: " + this.field_233941_c_;
    }
}
