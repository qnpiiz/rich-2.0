package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Set;
import net.minecraft.util.datafix.TypeReferences;

public class WallProperty extends DataFix
{
    private static final Set<String> field_233415_a_ = ImmutableSet.of("minecraft:andesite_wall", "minecraft:brick_wall", "minecraft:cobblestone_wall", "minecraft:diorite_wall", "minecraft:end_stone_brick_wall", "minecraft:granite_wall", "minecraft:mossy_cobblestone_wall", "minecraft:mossy_stone_brick_wall", "minecraft:nether_brick_wall", "minecraft:prismarine_wall", "minecraft:red_nether_brick_wall", "minecraft:red_sandstone_wall", "minecraft:sandstone_wall", "minecraft:stone_brick_wall");

    public WallProperty(Schema p_i231468_1_, boolean p_i231468_2_)
    {
        super(p_i231468_1_, p_i231468_2_);
    }

    public TypeRewriteRule makeRule()
    {
        return this.fixTypeEverywhereTyped("WallPropertyFix", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), (p_233416_0_) ->
        {
            return p_233416_0_.update(DSL.remainderFinder(), WallProperty::func_233417_a_);
        });
    }

    private static String func_233419_a_(String p_233419_0_)
    {
        return "true".equals(p_233419_0_) ? "low" : "none";
    }

    private static <T> Dynamic<T> func_233418_a_(Dynamic<T> p_233418_0_, String p_233418_1_)
    {
        return p_233418_0_.update(p_233418_1_, (p_233421_0_) ->
        {
            return DataFixUtils.orElse(p_233421_0_.asString().result().map(WallProperty::func_233419_a_).map(p_233421_0_::createString), p_233421_0_);
        });
    }

    private static <T> Dynamic<T> func_233417_a_(Dynamic<T> p_233417_0_)
    {
        boolean flag = p_233417_0_.get("Name").asString().result().filter(field_233415_a_::contains).isPresent();
        return !flag ? p_233417_0_ : p_233417_0_.update("Properties", (p_233420_0_) ->
        {
            Dynamic<?> dynamic = func_233418_a_(p_233420_0_, "east");
            dynamic = func_233418_a_(dynamic, "west");
            dynamic = func_233418_a_(dynamic, "north");
            return func_233418_a_(dynamic, "south");
        });
    }
}
