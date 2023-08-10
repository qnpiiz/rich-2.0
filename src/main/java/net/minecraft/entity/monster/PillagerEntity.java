package net.minecraft.entity.monster;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;

public class PillagerEntity extends AbstractIllagerEntity implements ICrossbowUser
{
    private static final DataParameter<Boolean> DATA_CHARGING_STATE = EntityDataManager.createKey(PillagerEntity.class, DataSerializers.BOOLEAN);
    private final Inventory inventory = new Inventory(5);

    public PillagerEntity(EntityType <? extends PillagerEntity > type, World worldIn)
    {
        super(type, worldIn);
    }

    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new AbstractRaiderEntity.FindTargetGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0D, 8.0F));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 15.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    public static AttributeModifierMap.MutableAttribute func_234296_eI_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.35F).createMutableAttribute(Attributes.MAX_HEALTH, 24.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D);
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(DATA_CHARGING_STATE, false);
    }

    public boolean func_230280_a_(ShootableItem p_230280_1_)
    {
        return p_230280_1_ == Items.CROSSBOW;
    }

    public boolean isCharging()
    {
        return this.dataManager.get(DATA_CHARGING_STATE);
    }

    public void setCharging(boolean isCharging)
    {
        this.dataManager.set(DATA_CHARGING_STATE, isCharging);
    }

    public void func_230283_U__()
    {
        this.idleTime = 0;
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < this.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                listnbt.add(itemstack.write(new CompoundNBT()));
            }
        }

        compound.put("Inventory", listnbt);
    }

    public AbstractIllagerEntity.ArmPose getArmPose()
    {
        if (this.isCharging())
        {
            return AbstractIllagerEntity.ArmPose.CROSSBOW_CHARGE;
        }
        else if (this.canEquip(Items.CROSSBOW))
        {
            return AbstractIllagerEntity.ArmPose.CROSSBOW_HOLD;
        }
        else
        {
            return this.isAggressive() ? AbstractIllagerEntity.ArmPose.ATTACKING : AbstractIllagerEntity.ArmPose.NEUTRAL;
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        ListNBT listnbt = compound.getList("Inventory", 10);

        for (int i = 0; i < listnbt.size(); ++i)
        {
            ItemStack itemstack = ItemStack.read(listnbt.getCompound(i));

            if (!itemstack.isEmpty())
            {
                this.inventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn)
    {
        BlockState blockstate = worldIn.getBlockState(pos.down());
        return !blockstate.isIn(Blocks.GRASS_BLOCK) && !blockstate.isIn(Blocks.SAND) ? 0.5F - worldIn.getBrightness(pos) : 10.0F;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        this.setEquipmentBasedOnDifficulty(difficultyIn);
        this.setEnchantmentBasedOnDifficulty(difficultyIn);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    protected void func_241844_w(float p_241844_1_)
    {
        super.func_241844_w(p_241844_1_);

        if (this.rand.nextInt(300) == 0)
        {
            ItemStack itemstack = this.getHeldItemMainhand();

            if (itemstack.getItem() == Items.CROSSBOW)
            {
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
                map.putIfAbsent(Enchantments.PIERCING, 1);
                EnchantmentHelper.setEnchantments(map, itemstack);
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
            }
        }
    }

    /**
     * Returns whether this Entity is on the same team as the given Entity.
     */
    public boolean isOnSameTeam(Entity entityIn)
    {
        if (super.isOnSameTeam(entityIn))
        {
            return true;
        }
        else if (entityIn instanceof LivingEntity && ((LivingEntity)entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER)
        {
            return this.getTeam() == null && entityIn.getTeam() == null;
        }
        else
        {
            return false;
        }
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
    {
        this.func_234281_b_(this, 1.6F);
    }

    public void func_230284_a_(LivingEntity p_230284_1_, ItemStack p_230284_2_, ProjectileEntity p_230284_3_, float p_230284_4_)
    {
        this.func_234279_a_(this, p_230284_1_, p_230284_3_, p_230284_4_, 1.6F);
    }

    /**
     * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
     * better.
     */
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity)
    {
        ItemStack itemstack = itemEntity.getItem();

        if (itemstack.getItem() instanceof BannerItem)
        {
            super.updateEquipmentIfNeeded(itemEntity);
        }
        else
        {
            Item item = itemstack.getItem();

            if (this.func_213672_b(item))
            {
                this.triggerItemPickupTrigger(itemEntity);
                ItemStack itemstack1 = this.inventory.addItem(itemstack);

                if (itemstack1.isEmpty())
                {
                    itemEntity.remove();
                }
                else
                {
                    itemstack.setCount(itemstack1.getCount());
                }
            }
        }
    }

    private boolean func_213672_b(Item p_213672_1_)
    {
        return this.isRaidActive() && p_213672_1_ == Items.WHITE_BANNER;
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn)
    {
        if (super.replaceItemInInventory(inventorySlot, itemStackIn))
        {
            return true;
        }
        else
        {
            int i = inventorySlot - 300;

            if (i >= 0 && i < this.inventory.getSizeInventory())
            {
                this.inventory.setInventorySlotContents(i, itemStackIn);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public void applyWaveBonus(int wave, boolean p_213660_2_)
    {
        Raid raid = this.getRaid();
        boolean flag = this.rand.nextFloat() <= raid.getEnchantOdds();

        if (flag)
        {
            ItemStack itemstack = new ItemStack(Items.CROSSBOW);
            Map<Enchantment, Integer> map = Maps.newHashMap();

            if (wave > raid.getWaves(Difficulty.NORMAL))
            {
                map.put(Enchantments.QUICK_CHARGE, 2);
            }
            else if (wave > raid.getWaves(Difficulty.EASY))
            {
                map.put(Enchantments.QUICK_CHARGE, 1);
            }

            map.put(Enchantments.MULTISHOT, 1);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
        }
    }

    public SoundEvent getRaidLossSound()
    {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }
}
