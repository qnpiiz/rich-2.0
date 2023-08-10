package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.RangedInteger;
import net.minecraft.world.server.ServerWorld;

public class RunSometimesTask<E extends LivingEntity> extends Task<E>
{
    private boolean field_233944_b_;
    private boolean field_233945_c_;
    private final RangedInteger field_233946_d_;
    private final Task <? super E > field_233947_e_;
    private int field_233948_f_;

    public RunSometimesTask(Task <? super E > p_i231530_1_, RangedInteger p_i231530_2_)
    {
        this(p_i231530_1_, false, p_i231530_2_);
    }

    public RunSometimesTask(Task <? super E > p_i231531_1_, boolean p_i231531_2_, RangedInteger p_i231531_3_)
    {
        super(p_i231531_1_.requiredMemoryState);
        this.field_233947_e_ = p_i231531_1_;
        this.field_233944_b_ = !p_i231531_2_;
        this.field_233946_d_ = p_i231531_3_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        if (!this.field_233947_e_.shouldExecute(worldIn, owner))
        {
            return false;
        }
        else
        {
            if (this.field_233944_b_)
            {
                this.func_233949_a_(worldIn);
                this.field_233944_b_ = false;
            }

            if (this.field_233948_f_ > 0)
            {
                --this.field_233948_f_;
            }

            return !this.field_233945_c_ && this.field_233948_f_ == 0;
        }
    }

    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        this.field_233947_e_.startExecuting(worldIn, entityIn, gameTimeIn);
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        return this.field_233947_e_.shouldContinueExecuting(worldIn, entityIn, gameTimeIn);
    }

    protected void updateTask(ServerWorld worldIn, E owner, long gameTime)
    {
        this.field_233947_e_.updateTask(worldIn, owner, gameTime);
        this.field_233945_c_ = this.field_233947_e_.getStatus() == Task.Status.RUNNING;
    }

    protected void resetTask(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        this.func_233949_a_(worldIn);
        this.field_233947_e_.resetTask(worldIn, entityIn, gameTimeIn);
    }

    private void func_233949_a_(ServerWorld p_233949_1_)
    {
        this.field_233948_f_ = this.field_233946_d_.getRandomWithinRange(p_233949_1_.rand);
    }

    protected boolean isTimedOut(long gameTime)
    {
        return false;
    }

    public String toString()
    {
        return "RunSometimes: " + this.field_233947_e_;
    }
}
