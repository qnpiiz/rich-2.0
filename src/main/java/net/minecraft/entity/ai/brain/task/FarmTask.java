package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class FarmTask extends Task<VillagerEntity>
{
    @Nullable
    private BlockPos field_220422_a;
    private long taskCooldown;
    private int idleTime;
    private final List<BlockPos> farmableBlocks = Lists.newArrayList();

    public FarmTask()
    {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT));
    }

    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner)
    {
        if (!worldIn.getGameRules().getBoolean(GameRules.MOB_GRIEFING))
        {
            return false;
        }
        else if (owner.getVillagerData().getProfession() != VillagerProfession.FARMER)
        {
            return false;
        }
        else
        {
            BlockPos.Mutable blockpos$mutable = owner.getPosition().toMutable();
            this.farmableBlocks.clear();

            for (int i = -1; i <= 1; ++i)
            {
                for (int j = -1; j <= 1; ++j)
                {
                    for (int k = -1; k <= 1; ++k)
                    {
                        blockpos$mutable.setPos(owner.getPosX() + (double)i, owner.getPosY() + (double)j, owner.getPosZ() + (double)k);

                        if (this.isValidPosForFarming(blockpos$mutable, worldIn))
                        {
                            this.farmableBlocks.add(new BlockPos(blockpos$mutable));
                        }
                    }
                }
            }

            this.field_220422_a = this.getNextPosForFarming(worldIn);
            return this.field_220422_a != null;
        }
    }

    @Nullable
    private BlockPos getNextPosForFarming(ServerWorld serverWorldIn)
    {
        return this.farmableBlocks.isEmpty() ? null : this.farmableBlocks.get(serverWorldIn.getRandom().nextInt(this.farmableBlocks.size()));
    }

    private boolean isValidPosForFarming(BlockPos pos, ServerWorld serverWorldIn)
    {
        BlockState blockstate = serverWorldIn.getBlockState(pos);
        Block block = blockstate.getBlock();
        Block block1 = serverWorldIn.getBlockState(pos.down()).getBlock();
        return block instanceof CropsBlock && ((CropsBlock)block).isMaxAge(blockstate) || blockstate.isAir() && block1 instanceof FarmlandBlock;
    }

    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        if (gameTimeIn > this.taskCooldown && this.field_220422_a != null)
        {
            entityIn.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(this.field_220422_a));
            entityIn.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosWrapper(this.field_220422_a), 0.5F, 1));
        }
    }

    protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        entityIn.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
        entityIn.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
        this.idleTime = 0;
        this.taskCooldown = gameTimeIn + 40L;
    }

    protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime)
    {
        if (this.field_220422_a == null || this.field_220422_a.withinDistance(owner.getPositionVec(), 1.0D))
        {
            if (this.field_220422_a != null && gameTime > this.taskCooldown)
            {
                BlockState blockstate = worldIn.getBlockState(this.field_220422_a);
                Block block = blockstate.getBlock();
                Block block1 = worldIn.getBlockState(this.field_220422_a.down()).getBlock();

                if (block instanceof CropsBlock && ((CropsBlock)block).isMaxAge(blockstate))
                {
                    worldIn.destroyBlock(this.field_220422_a, true, owner);
                }

                if (blockstate.isAir() && block1 instanceof FarmlandBlock && owner.isFarmItemInInventory())
                {
                    Inventory inventory = owner.getVillagerInventory();

                    for (int i = 0; i < inventory.getSizeInventory(); ++i)
                    {
                        ItemStack itemstack = inventory.getStackInSlot(i);
                        boolean flag = false;

                        if (!itemstack.isEmpty())
                        {
                            if (itemstack.getItem() == Items.WHEAT_SEEDS)
                            {
                                worldIn.setBlockState(this.field_220422_a, Blocks.WHEAT.getDefaultState(), 3);
                                flag = true;
                            }
                            else if (itemstack.getItem() == Items.POTATO)
                            {
                                worldIn.setBlockState(this.field_220422_a, Blocks.POTATOES.getDefaultState(), 3);
                                flag = true;
                            }
                            else if (itemstack.getItem() == Items.CARROT)
                            {
                                worldIn.setBlockState(this.field_220422_a, Blocks.CARROTS.getDefaultState(), 3);
                                flag = true;
                            }
                            else if (itemstack.getItem() == Items.BEETROOT_SEEDS)
                            {
                                worldIn.setBlockState(this.field_220422_a, Blocks.BEETROOTS.getDefaultState(), 3);
                                flag = true;
                            }
                        }

                        if (flag)
                        {
                            worldIn.playSound((PlayerEntity)null, (double)this.field_220422_a.getX(), (double)this.field_220422_a.getY(), (double)this.field_220422_a.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            itemstack.shrink(1);

                            if (itemstack.isEmpty())
                            {
                                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                            }

                            break;
                        }
                    }
                }

                if (block instanceof CropsBlock && !((CropsBlock)block).isMaxAge(blockstate))
                {
                    this.farmableBlocks.remove(this.field_220422_a);
                    this.field_220422_a = this.getNextPosForFarming(worldIn);

                    if (this.field_220422_a != null)
                    {
                        this.taskCooldown = gameTime + 20L;
                        owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosWrapper(this.field_220422_a), 0.5F, 1));
                        owner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(this.field_220422_a));
                    }
                }
            }

            ++this.idleTime;
        }
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        return this.idleTime < 200;
    }
}
