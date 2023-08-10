package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import net.minecraft.util.datafix.TypeReferences;

public class AttributesFix extends DataFix
{
    private static final Map<String, String> field_233070_a_ = ImmutableMap.<String, String>builder().put("generic.maxHealth", "generic.max_health").put("Max Health", "generic.max_health").put("zombie.spawnReinforcements", "zombie.spawn_reinforcements").put("Spawn Reinforcements Chance", "zombie.spawn_reinforcements").put("horse.jumpStrength", "horse.jump_strength").put("Jump Strength", "horse.jump_strength").put("generic.followRange", "generic.follow_range").put("Follow Range", "generic.follow_range").put("generic.knockbackResistance", "generic.knockback_resistance").put("Knockback Resistance", "generic.knockback_resistance").put("generic.movementSpeed", "generic.movement_speed").put("Movement Speed", "generic.movement_speed").put("generic.flyingSpeed", "generic.flying_speed").put("Flying Speed", "generic.flying_speed").put("generic.attackDamage", "generic.attack_damage").put("generic.attackKnockback", "generic.attack_knockback").put("generic.attackSpeed", "generic.attack_speed").put("generic.armorToughness", "generic.armor_toughness").build();

    public AttributesFix(Schema p_i231445_1_)
    {
        super(p_i231445_1_, false);
    }

    protected TypeRewriteRule makeRule()
    {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<?> opticfinder = type.findField("tag");
        return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("Rename ItemStack Attributes", type, (p_233071_1_) ->
        {
            return p_233071_1_.updateTyped(opticfinder, AttributesFix::func_233072_a_);
        }), this.fixTypeEverywhereTyped("Rename Entity Attributes", this.getInputSchema().getType(TypeReferences.ENTITY), AttributesFix::func_233076_b_), this.fixTypeEverywhereTyped("Rename Player Attributes", this.getInputSchema().getType(TypeReferences.PLAYER), AttributesFix::func_233076_b_));
    }

    private static Dynamic<?> func_233073_a_(Dynamic<?> p_233073_0_)
    {
        return DataFixUtils.orElse(p_233073_0_.asString().result().map((p_233074_0_) ->
        {
            return field_233070_a_.getOrDefault(p_233074_0_, p_233074_0_);
        }).map(p_233073_0_::createString), p_233073_0_);
    }

    private static Typed<?> func_233072_a_(Typed<?> p_233072_0_)
    {
        return p_233072_0_.update(DSL.remainderFinder(), (p_233081_0_) ->
        {
            return p_233081_0_.update("AttributeModifiers", (p_233082_0_) -> {
                return DataFixUtils.orElse(p_233082_0_.asStreamOpt().result().map((p_233078_0_) -> {
                    return p_233078_0_.map((p_233083_0_) -> {
                        return p_233083_0_.update("AttributeName", AttributesFix::func_233073_a_);
                    });
                }).map(p_233082_0_::createList), p_233082_0_);
            });
        });
    }

    private static Typed<?> func_233076_b_(Typed<?> p_233076_0_)
    {
        return p_233076_0_.update(DSL.remainderFinder(), (p_233077_0_) ->
        {
            return p_233077_0_.update("Attributes", (p_233079_0_) -> {
                return DataFixUtils.orElse(p_233079_0_.asStreamOpt().result().map((p_233075_0_) -> {
                    return p_233075_0_.map((p_233080_0_) -> {
                        return p_233080_0_.update("Name", AttributesFix::func_233073_a_);
                    });
                }).map(p_233079_0_::createList), p_233079_0_);
            });
        });
    }
}
