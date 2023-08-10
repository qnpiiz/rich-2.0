package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V2501 extends NamespacedSchema
{
    public V2501(int p_i231472_1_, Schema p_i231472_2_)
    {
        super(p_i231472_1_, p_i231472_2_);
    }

    private static void func_233461_a_(Schema p_233461_0_, Map<String, Supplier<TypeTemplate>> p_233461_1_, String p_233461_2_)
    {
        p_233461_0_.register(p_233461_1_, p_233461_2_, () ->
        {
            return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_233461_0_)), "RecipesUsed", DSL.compoundList(TypeReferences.RECIPE.in(p_233461_0_), DSL.constType(DSL.intType())));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_)
    {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
        func_233461_a_(p_registerBlockEntities_1_, map, "minecraft:furnace");
        func_233461_a_(p_registerBlockEntities_1_, map, "minecraft:smoker");
        func_233461_a_(p_registerBlockEntities_1_, map, "minecraft:blast_furnace");
        return map;
    }
}
