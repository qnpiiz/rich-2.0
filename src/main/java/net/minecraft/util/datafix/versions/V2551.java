package net.minecraft.util.datafix.versions;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V2551 extends NamespacedSchema
{
    public V2551(int p_i231478_1_, Schema p_i231478_2_)
    {
        super(p_i231478_1_, p_i231478_2_);
    }

    public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_)
    {
        super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
        p_registerTypes_1_.registerType(false, TypeReferences.WORLD_GEN_SETTINGS, () ->
        {
            return DSL.fields("dimensions", DSL.compoundList(DSL.constType(func_233457_a_()), DSL.fields("generator", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL::remainder, "minecraft:flat", () -> {
                return DSL.optionalFields("settings", DSL.optionalFields("biome", TypeReferences.BIOME.in(p_registerTypes_1_), "layers", DSL.list(DSL.optionalFields("block", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))));
            }, "minecraft:noise", () -> {
                return DSL.optionalFields("biome_source", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:fixed", () -> {
                    return DSL.fields("biome", TypeReferences.BIOME.in(p_registerTypes_1_));
                }, "minecraft:multi_noise", () -> {
                    return DSL.list(DSL.fields("biome", TypeReferences.BIOME.in(p_registerTypes_1_)));
                }, "minecraft:checkerboard", () -> {
                    return DSL.fields("biomes", DSL.list(TypeReferences.BIOME.in(p_registerTypes_1_)));
                }, "minecraft:vanilla_layered", DSL::remainder, "minecraft:the_end", DSL::remainder)), "settings", DSL.or(DSL.constType(DSL.string()), DSL.optionalFields("default_block", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_), "default_fluid", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_))));
            })))));
        });
    }
}
