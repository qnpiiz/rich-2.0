package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class BoneMealCropsTask extends Task<VillagerEntity>
{
    private long taskDelay;
    private long taskCooldown;
    private int grownObjects;
    private Optional<BlockPos> growableTarget = Optional.empty();

    public BoneMealCropsTask()
    {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
    }

    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner)
    {
        if (owner.ticksExisted % 10 == 0 && (this.taskCooldown == 0L || this.taskCooldown + 160L <= (long)owner.ticksExisted))
        {
            if (owner.getVillagerInventory().count(Items.BONE_MEAL) <= 0)
            {
                return false;
            }
            else
            {
                this.growableTarget = this.findGrowablePosition(worldIn, owner);
                return this.growableTarget.isPresent();
            }
        }
        else
        {
            return false;
        }
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        return this.grownObjects < 80 && this.growableTarget.isPresent();
    }

    private Optional<BlockPos> findGrowablePosition(ServerWorld world, VillagerEntity villager)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        Optional<BlockPos> optional = Optional.empty();
        int i = 0;

        for (int j = -1; j <= 1; ++j)
        {
            for (int k = -1; k <= 1; ++k)
            {
                for (int l = -1; l <= 1; ++l)
                {
                    blockpos$mutable.setAndOffset(villager.getPosition(), j, k, l);

                    if (this.isGrowable(blockpos$mutable, world))
                    {
                        ++i;

                        if (world.rand.nextInt(i) == 0)
                        {
                            optional = Optional.of(blockpos$mutable.toImmutable());
                        }
                    }
                }
            }
        }

        return optional;
    }

    private boolean isGrowable(BlockPos pos, ServerWorld world)
    {
        BlockState blockstate = world.getBlockState(pos);
        Block block = blockstate.getBlock();
        return block instanceof CropsBlock && !((CropsBlock)block).isMaxAge(blockstate);
    }

    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        this.updateMemory(entityIn);
        entityIn.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BONE_MEAL));
        this.taskDelay = gameTimeIn;
        this.grownObjects = 0;
    }

    private void updateMemory(VillagerEntity villager)
    {
        this.growableTarget.ifPresent((pos) ->
        {
            BlockPosWrapper blockposwrapper = new BlockPosWrapper(pos);
            villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, blockposwrapper);
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockposwrapper, 0.5F, 1));
        });
    }

    protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        entityIn.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        this.taskCooldown = (long)entityIn.ticksExisted;
    }

    protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime)
    {
        BlockPos blockpos = this.growableTarget.get();

        if (gameTime >= this.taskDelay && blockpos.withinDistance(owner.getPositionVec(), 1.0D))
        {
            ItemStack itemstack = ItemStack.EMPTY;
            Inventory inventory = owner.getVillagerInventory();
            int i = inventory.getSizeInventory();

            for (int j = 0; j < i; ++j)
            {
                ItemStack itemstack1 = inventory.getStackInSlot(j);

                if (itemstack1.getItem() == Items.BONE_MEAL)
                {
                    itemstack = itemstack1;
                    break;
                }
            }

            if (!itemstack.isEmpty() && BoneMealItem.applyBonemeal(itemstack, worldIn, blockpos))
            {
                worldIn.playEvent(2005, blockpos, 0);
                this.growableTarget = this.findGrowablePosition(worldIn, owner);
                this.updateMemory(owner);
                this.taskDelay = gameTime + 40L;
            }

            ++this.grownObjects;
        }
    }
}
