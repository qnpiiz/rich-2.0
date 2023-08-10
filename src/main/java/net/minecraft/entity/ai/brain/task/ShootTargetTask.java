package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class ShootTargetTask<E extends MobEntity & ICrossbowUser, T extends LivingEntity> extends Task<E>
{
    private int field_233885_b_;
    private ShootTargetTask.Status field_233886_c_ = ShootTargetTask.Status.UNCHARGED;

    public ShootTargetTask()
    {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT), 1200);
    }

    protected boolean shouldExecute(ServerWorld worldIn, E owner)
    {
        LivingEntity livingentity = func_233887_a_(owner);
        return owner.canEquip(Items.CROSSBOW) && BrainUtil.isMobVisible(owner, livingentity) && BrainUtil.canFireAtTarget(owner, livingentity, 0);
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        return entityIn.getBrain().hasMemory(MemoryModuleType.ATTACK_TARGET) && this.shouldExecute(worldIn, entityIn);
    }

    protected void updateTask(ServerWorld worldIn, E owner, long gameTime)
    {
        LivingEntity livingentity = func_233887_a_(owner);
        this.func_233889_b_(owner, livingentity);
        this.func_233888_a_(owner, livingentity);
    }

    protected void resetTask(ServerWorld worldIn, E entityIn, long gameTimeIn)
    {
        if (entityIn.isHandActive())
        {
            entityIn.resetActiveHand();
        }

        if (entityIn.canEquip(Items.CROSSBOW))
        {
            entityIn.setCharging(false);
            CrossbowItem.setCharged(entityIn.getActiveItemStack(), false);
        }
    }

    private void func_233888_a_(E p_233888_1_, LivingEntity p_233888_2_)
    {
        if (this.field_233886_c_ == ShootTargetTask.Status.UNCHARGED)
        {
            p_233888_1_.setActiveHand(ProjectileHelper.getHandWith(p_233888_1_, Items.CROSSBOW));
            this.field_233886_c_ = ShootTargetTask.Status.CHARGING;
            p_233888_1_.setCharging(true);
        }
        else if (this.field_233886_c_ == ShootTargetTask.Status.CHARGING)
        {
            if (!p_233888_1_.isHandActive())
            {
                this.field_233886_c_ = ShootTargetTask.Status.UNCHARGED;
            }

            int i = p_233888_1_.getItemInUseMaxCount();
            ItemStack itemstack = p_233888_1_.getActiveItemStack();

            if (i >= CrossbowItem.getChargeTime(itemstack))
            {
                p_233888_1_.stopActiveHand();
                this.field_233886_c_ = ShootTargetTask.Status.CHARGED;
                this.field_233885_b_ = 20 + p_233888_1_.getRNG().nextInt(20);
                p_233888_1_.setCharging(false);
            }
        }
        else if (this.field_233886_c_ == ShootTargetTask.Status.CHARGED)
        {
            --this.field_233885_b_;

            if (this.field_233885_b_ == 0)
            {
                this.field_233886_c_ = ShootTargetTask.Status.READY_TO_ATTACK;
            }
        }
        else if (this.field_233886_c_ == ShootTargetTask.Status.READY_TO_ATTACK)
        {
            p_233888_1_.attackEntityWithRangedAttack(p_233888_2_, 1.0F);
            ItemStack itemstack1 = p_233888_1_.getHeldItem(ProjectileHelper.getHandWith(p_233888_1_, Items.CROSSBOW));
            CrossbowItem.setCharged(itemstack1, false);
            this.field_233886_c_ = ShootTargetTask.Status.UNCHARGED;
        }
    }

    private void func_233889_b_(MobEntity p_233889_1_, LivingEntity p_233889_2_)
    {
        p_233889_1_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_233889_2_, true));
    }

    private static LivingEntity func_233887_a_(LivingEntity p_233887_0_)
    {
        return p_233887_0_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    static enum Status
    {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;
    }
}
