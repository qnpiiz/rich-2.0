package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArmorItem extends Item implements IArmorVanishable
{
    private static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final IDispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            return ArmorItem.func_226626_a_(source, stack) ? stack : super.dispenseStack(source, stack);
        }
    };
    protected final EquipmentSlotType slot;
    private final int damageReduceAmount;
    private final float toughness;
    protected final float knockbackResistance;
    protected final IArmorMaterial material;
    private final Multimap<Attribute, AttributeModifier> field_234656_m_;

    public static boolean func_226626_a_(IBlockSource blockSource, ItemStack stack)
    {
        BlockPos blockpos = blockSource.getBlockPos().offset(blockSource.getBlockState().get(DispenserBlock.FACING));
        List<LivingEntity> list = blockSource.getWorld().getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(blockpos), EntityPredicates.NOT_SPECTATING.and(new EntityPredicates.ArmoredMob(stack)));

        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            LivingEntity livingentity = list.get(0);
            EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(stack);
            ItemStack itemstack = stack.split(1);
            livingentity.setItemStackToSlot(equipmentslottype, itemstack);

            if (livingentity instanceof MobEntity)
            {
                ((MobEntity)livingentity).setDropChance(equipmentslottype, 2.0F);
                ((MobEntity)livingentity).enablePersistence();
            }

            return true;
        }
    }

    public ArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Item.Properties builderIn)
    {
        super(builderIn.defaultMaxDamage(materialIn.getDurability(slot)));
        this.material = materialIn;
        this.slot = slot;
        this.damageReduceAmount = materialIn.getDamageReductionAmount(slot);
        this.toughness = materialIn.getToughness();
        this.knockbackResistance = materialIn.getKnockbackResistance();
        DispenserBlock.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
        Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double)this.damageReduceAmount, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));

        if (materialIn == ArmorMaterial.NETHERITE)
        {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }

        this.field_234656_m_ = builder.build();
    }

    /**
     * Gets the equipment slot of this armor piece (formerly known as armor type)
     */
    public EquipmentSlotType getEquipmentSlot()
    {
        return this.slot;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return this.material.getEnchantability();
    }

    public IArmorMaterial getArmorMaterial()
    {
        return this.material;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return this.material.getRepairMaterial().test(repair) || super.getIsRepairable(toRepair, repair);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = playerIn.getItemStackFromSlot(equipmentslottype);

        if (itemstack1.isEmpty())
        {
            playerIn.setItemStackToSlot(equipmentslottype, itemstack.copy());
            itemstack.setCount(0);
            return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
        }
        else
        {
            return ActionResult.resultFail(itemstack);
        }
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot)
    {
        return equipmentSlot == this.slot ? this.field_234656_m_ : super.getAttributeModifiers(equipmentSlot);
    }

    public int getDamageReduceAmount()
    {
        return this.damageReduceAmount;
    }

    public float func_234657_f_()
    {
        return this.toughness;
    }
}
