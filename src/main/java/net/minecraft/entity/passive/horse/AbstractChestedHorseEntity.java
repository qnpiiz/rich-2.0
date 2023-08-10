package net.minecraft.entity.passive.horse;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public abstract class AbstractChestedHorseEntity extends AbstractHorseEntity
{
    private static final DataParameter<Boolean> DATA_ID_CHEST = EntityDataManager.createKey(AbstractChestedHorseEntity.class, DataSerializers.BOOLEAN);

    protected AbstractChestedHorseEntity(EntityType <? extends AbstractChestedHorseEntity > type, World worldIn)
    {
        super(type, worldIn);
        this.canGallop = false;
    }

    protected void func_230273_eI_()
    {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(DATA_ID_CHEST, false);
    }

    public static AttributeModifierMap.MutableAttribute func_234234_eJ_()
    {
        return func_234237_fg_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.175F).createMutableAttribute(Attributes.HORSE_JUMP_STRENGTH, 0.5D);
    }

    public boolean hasChest()
    {
        return this.dataManager.get(DATA_ID_CHEST);
    }

    public void setChested(boolean chested)
    {
        this.dataManager.set(DATA_ID_CHEST, chested);
    }

    protected int getInventorySize()
    {
        return this.hasChest() ? 17 : super.getInventorySize();
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return super.getMountedYOffset() - 0.25D;
    }

    protected void dropInventory()
    {
        super.dropInventory();

        if (this.hasChest())
        {
            if (!this.world.isRemote)
            {
                this.entityDropItem(Blocks.CHEST);
            }

            this.setChested(false);
        }
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putBoolean("ChestedHorse", this.hasChest());

        if (this.hasChest())
        {
            ListNBT listnbt = new ListNBT();

            for (int i = 2; i < this.horseChest.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.horseChest.getStackInSlot(i);

                if (!itemstack.isEmpty())
                {
                    CompoundNBT compoundnbt = new CompoundNBT();
                    compoundnbt.putByte("Slot", (byte)i);
                    itemstack.write(compoundnbt);
                    listnbt.add(compoundnbt);
                }
            }

            compound.put("Items", listnbt);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setChested(compound.getBoolean("ChestedHorse"));

        if (this.hasChest())
        {
            ListNBT listnbt = compound.getList("Items", 10);
            this.initHorseChest();

            for (int i = 0; i < listnbt.size(); ++i)
            {
                CompoundNBT compoundnbt = listnbt.getCompound(i);
                int j = compoundnbt.getByte("Slot") & 255;

                if (j >= 2 && j < this.horseChest.getSizeInventory())
                {
                    this.horseChest.setInventorySlotContents(j, ItemStack.read(compoundnbt));
                }
            }
        }

        this.func_230275_fc_();
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn)
    {
        if (inventorySlot == 499)
        {
            if (this.hasChest() && itemStackIn.isEmpty())
            {
                this.setChested(false);
                this.initHorseChest();
                return true;
            }

            if (!this.hasChest() && itemStackIn.getItem() == Blocks.CHEST.asItem())
            {
                this.setChested(true);
                this.initHorseChest();
                return true;
            }
        }

        return super.replaceItemInInventory(inventorySlot, itemStackIn);
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_)
    {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);

        if (!this.isChild())
        {
            if (this.isTame() && p_230254_1_.isSecondaryUseActive())
            {
                this.openGUI(p_230254_1_);
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }

            if (this.isBeingRidden())
            {
                return super.func_230254_b_(p_230254_1_, p_230254_2_);
            }
        }

        if (!itemstack.isEmpty())
        {
            if (this.isBreedingItem(itemstack))
            {
                return this.func_241395_b_(p_230254_1_, itemstack);
            }

            if (!this.isTame())
            {
                this.makeMad();
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }

            if (!this.hasChest() && itemstack.getItem() == Blocks.CHEST.asItem())
            {
                this.setChested(true);
                this.playChestEquipSound();

                if (!p_230254_1_.abilities.isCreativeMode)
                {
                    itemstack.shrink(1);
                }

                this.initHorseChest();
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }

            if (!this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE)
            {
                this.openGUI(p_230254_1_);
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }
        }

        if (this.isChild())
        {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
        else
        {
            this.mountTo(p_230254_1_);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
    }

    protected void playChestEquipSound()
    {
        this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
    }

    public int getInventoryColumns()
    {
        return 5;
    }
}
