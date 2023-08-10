package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolItem extends TieredItem implements IVanishable
{
    private final Set<Block> effectiveBlocks;
    protected final float efficiency;
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> toolAttributes;

    protected ToolItem(float attackDamageIn, float attackSpeedIn, IItemTier tier, Set<Block> effectiveBlocksIn, Item.Properties builderIn)
    {
        super(tier, builderIn);
        this.effectiveBlocks = effectiveBlocksIn;
        this.efficiency = tier.getEfficiency();
        this.attackDamage = attackDamageIn + tier.getAttackDamage();
        Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)attackSpeedIn, AttributeModifier.Operation.ADDITION));
        this.toolAttributes = builder.build();
    }

    public float getDestroySpeed(ItemStack stack, BlockState state)
    {
        return this.effectiveBlocks.contains(state.getBlock()) ? this.efficiency : 1.0F;
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        stack.damageItem(2, attacker, (entity) ->
        {
            entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        });
        return true;
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving)
    {
        if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F)
        {
            stack.damageItem(1, entityLiving, (entity) ->
            {
                entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
            });
        }

        return true;
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot)
    {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.toolAttributes : super.getAttributeModifiers(equipmentSlot);
    }

    public float getAttackDamage()
    {
        return this.attackDamage;
    }
}
