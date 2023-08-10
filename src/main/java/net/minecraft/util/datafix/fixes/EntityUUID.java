package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.TypeReferences;

public class EntityUUID extends AbstractUUIDFix
{
    private static final Set<String> field_233204_c_ = Sets.newHashSet();
    private static final Set<String> field_233205_d_ = Sets.newHashSet();
    private static final Set<String> field_233206_e_ = Sets.newHashSet();
    private static final Set<String> field_233207_f_ = Sets.newHashSet();
    private static final Set<String> field_233208_g_ = Sets.newHashSet();
    private static final Set<String> field_233209_h_ = Sets.newHashSet();

    public EntityUUID(Schema p_i231452_1_)
    {
        super(p_i231452_1_, TypeReferences.ENTITY);
    }

    protected TypeRewriteRule makeRule()
    {
        return this.fixTypeEverywhereTyped("EntityUUIDFixes", this.getInputSchema().getType(this.reference), (p_233210_1_) ->
        {
            p_233210_1_ = p_233210_1_.update(DSL.remainderFinder(), EntityUUID::func_233214_c_);

            for (String s : field_233204_c_)
            {
                p_233210_1_ = this.func_233053_a_(p_233210_1_, s, EntityUUID::func_233226_l_);
            }

            for (String s1 : field_233205_d_)
            {
                p_233210_1_ = this.func_233053_a_(p_233210_1_, s1, EntityUUID::func_233226_l_);
            }

            for (String s2 : field_233206_e_)
            {
                p_233210_1_ = this.func_233053_a_(p_233210_1_, s2, EntityUUID::func_233227_m_);
            }

            for (String s3 : field_233207_f_)
            {
                p_233210_1_ = this.func_233053_a_(p_233210_1_, s3, EntityUUID::func_233228_n_);
            }

            for (String s4 : field_233208_g_)
            {
                p_233210_1_ = this.func_233053_a_(p_233210_1_, s4, EntityUUID::func_233212_b_);
            }

            for (String s5 : field_233209_h_)
            {
                p_233210_1_ = this.func_233053_a_(p_233210_1_, s5, EntityUUID::func_233229_o_);
            }

            p_233210_1_ = this.func_233053_a_(p_233210_1_, "minecraft:bee", EntityUUID::func_233225_k_);
            p_233210_1_ = this.func_233053_a_(p_233210_1_, "minecraft:zombified_piglin", EntityUUID::func_233225_k_);
            p_233210_1_ = this.func_233053_a_(p_233210_1_, "minecraft:fox", EntityUUID::func_233224_j_);
            p_233210_1_ = this.func_233053_a_(p_233210_1_, "minecraft:item", EntityUUID::func_233223_i_);
            p_233210_1_ = this.func_233053_a_(p_233210_1_, "minecraft:shulker_bullet", EntityUUID::func_233222_h_);
            p_233210_1_ = this.func_233053_a_(p_233210_1_, "minecraft:area_effect_cloud", EntityUUID::func_233221_g_);
            p_233210_1_ = this.func_233053_a_(p_233210_1_, "minecraft:zombie_villager", EntityUUID::func_233220_f_);
            p_233210_1_ = this.func_233053_a_(p_233210_1_, "minecraft:evoker_fangs", EntityUUID::func_233218_e_);
            return this.func_233053_a_(p_233210_1_, "minecraft:piglin", EntityUUID::func_233216_d_);
        });
    }

    private static Dynamic<?> func_233216_d_(Dynamic<?> p_233216_0_)
    {
        return p_233216_0_.update("Brain", (p_233235_0_) ->
        {
            return p_233235_0_.update("memories", (p_233236_0_) -> {
                return p_233236_0_.update("minecraft:angry_at", (p_233237_0_) -> {
                    return func_233058_a_(p_233237_0_, "value", "value").orElseGet(() -> {
                        LOGGER.warn("angry_at has no value.");
                        return p_233237_0_;
                    });
                });
            });
        });
    }

    private static Dynamic<?> func_233218_e_(Dynamic<?> p_233218_0_)
    {
        return func_233064_c_(p_233218_0_, "OwnerUUID", "Owner").orElse(p_233218_0_);
    }

    private static Dynamic<?> func_233220_f_(Dynamic<?> p_233220_0_)
    {
        return func_233064_c_(p_233220_0_, "ConversionPlayer", "ConversionPlayer").orElse(p_233220_0_);
    }

    private static Dynamic<?> func_233221_g_(Dynamic<?> p_233221_0_)
    {
        return func_233064_c_(p_233221_0_, "OwnerUUID", "Owner").orElse(p_233221_0_);
    }

    private static Dynamic<?> func_233222_h_(Dynamic<?> p_233222_0_)
    {
        p_233222_0_ = func_233062_b_(p_233222_0_, "Owner", "Owner").orElse(p_233222_0_);
        return func_233062_b_(p_233222_0_, "Target", "Target").orElse(p_233222_0_);
    }

    private static Dynamic<?> func_233223_i_(Dynamic<?> p_233223_0_)
    {
        p_233223_0_ = func_233062_b_(p_233223_0_, "Owner", "Owner").orElse(p_233223_0_);
        return func_233062_b_(p_233223_0_, "Thrower", "Thrower").orElse(p_233223_0_);
    }

    private static Dynamic<?> func_233224_j_(Dynamic<?> p_233224_0_)
    {
        Optional < Dynamic<? >> optional = p_233224_0_.get("TrustedUUIDs").result().map((p_233219_1_) ->
        {
            return p_233224_0_.createList(p_233219_1_.asStream().map((p_233233_0_) -> {
                return func_233054_a_(p_233233_0_).orElseGet(() -> {
                    LOGGER.warn("Trusted contained invalid data.");
                    return p_233233_0_;
                });
            }));
        });
        return DataFixUtils.orElse(optional.map((p_233217_1_) ->
        {
            return p_233224_0_.remove("TrustedUUIDs").set("Trusted", p_233217_1_);
        }), p_233224_0_);
    }

    private static Dynamic<?> func_233225_k_(Dynamic<?> p_233225_0_)
    {
        return func_233058_a_(p_233225_0_, "HurtBy", "HurtBy").orElse(p_233225_0_);
    }

    private static Dynamic<?> func_233226_l_(Dynamic<?> p_233226_0_)
    {
        Dynamic<?> dynamic = func_233227_m_(p_233226_0_);
        return func_233058_a_(dynamic, "OwnerUUID", "Owner").orElse(dynamic);
    }

    private static Dynamic<?> func_233227_m_(Dynamic<?> p_233227_0_)
    {
        Dynamic<?> dynamic = func_233228_n_(p_233227_0_);
        return func_233064_c_(dynamic, "LoveCause", "LoveCause").orElse(dynamic);
    }

    private static Dynamic<?> func_233228_n_(Dynamic<?> p_233228_0_)
    {
        return func_233212_b_(p_233228_0_).update("Leash", (p_233232_0_) ->
        {
            return func_233064_c_(p_233232_0_, "UUID", "UUID").orElse(p_233232_0_);
        });
    }

    public static Dynamic<?> func_233212_b_(Dynamic<?> p_233212_0_)
    {
        return p_233212_0_.update("Attributes", (p_233213_1_) ->
        {
            return p_233212_0_.createList(p_233213_1_.asStream().map((p_233230_0_) -> {
                return p_233230_0_.update("Modifiers", (p_233215_1_) -> {
                    return p_233230_0_.createList(p_233215_1_.asStream().map((p_233231_0_) -> {
                        return func_233064_c_(p_233231_0_, "UUID", "UUID").orElse(p_233231_0_);
                    }));
                });
            }));
        });
    }

    private static Dynamic<?> func_233229_o_(Dynamic<?> p_233229_0_)
    {
        return DataFixUtils.orElse(p_233229_0_.get("OwnerUUID").result().map((p_233211_1_) ->
        {
            return p_233229_0_.remove("OwnerUUID").set("Owner", p_233211_1_);
        }), p_233229_0_);
    }

    public static Dynamic<?> func_233214_c_(Dynamic<?> p_233214_0_)
    {
        return func_233064_c_(p_233214_0_, "UUID", "UUID").orElse(p_233214_0_);
    }

    static
    {
        field_233204_c_.add("minecraft:donkey");
        field_233204_c_.add("minecraft:horse");
        field_233204_c_.add("minecraft:llama");
        field_233204_c_.add("minecraft:mule");
        field_233204_c_.add("minecraft:skeleton_horse");
        field_233204_c_.add("minecraft:trader_llama");
        field_233204_c_.add("minecraft:zombie_horse");
        field_233205_d_.add("minecraft:cat");
        field_233205_d_.add("minecraft:parrot");
        field_233205_d_.add("minecraft:wolf");
        field_233206_e_.add("minecraft:bee");
        field_233206_e_.add("minecraft:chicken");
        field_233206_e_.add("minecraft:cow");
        field_233206_e_.add("minecraft:fox");
        field_233206_e_.add("minecraft:mooshroom");
        field_233206_e_.add("minecraft:ocelot");
        field_233206_e_.add("minecraft:panda");
        field_233206_e_.add("minecraft:pig");
        field_233206_e_.add("minecraft:polar_bear");
        field_233206_e_.add("minecraft:rabbit");
        field_233206_e_.add("minecraft:sheep");
        field_233206_e_.add("minecraft:turtle");
        field_233206_e_.add("minecraft:hoglin");
        field_233207_f_.add("minecraft:bat");
        field_233207_f_.add("minecraft:blaze");
        field_233207_f_.add("minecraft:cave_spider");
        field_233207_f_.add("minecraft:cod");
        field_233207_f_.add("minecraft:creeper");
        field_233207_f_.add("minecraft:dolphin");
        field_233207_f_.add("minecraft:drowned");
        field_233207_f_.add("minecraft:elder_guardian");
        field_233207_f_.add("minecraft:ender_dragon");
        field_233207_f_.add("minecraft:enderman");
        field_233207_f_.add("minecraft:endermite");
        field_233207_f_.add("minecraft:evoker");
        field_233207_f_.add("minecraft:ghast");
        field_233207_f_.add("minecraft:giant");
        field_233207_f_.add("minecraft:guardian");
        field_233207_f_.add("minecraft:husk");
        field_233207_f_.add("minecraft:illusioner");
        field_233207_f_.add("minecraft:magma_cube");
        field_233207_f_.add("minecraft:pufferfish");
        field_233207_f_.add("minecraft:zombified_piglin");
        field_233207_f_.add("minecraft:salmon");
        field_233207_f_.add("minecraft:shulker");
        field_233207_f_.add("minecraft:silverfish");
        field_233207_f_.add("minecraft:skeleton");
        field_233207_f_.add("minecraft:slime");
        field_233207_f_.add("minecraft:snow_golem");
        field_233207_f_.add("minecraft:spider");
        field_233207_f_.add("minecraft:squid");
        field_233207_f_.add("minecraft:stray");
        field_233207_f_.add("minecraft:tropical_fish");
        field_233207_f_.add("minecraft:vex");
        field_233207_f_.add("minecraft:villager");
        field_233207_f_.add("minecraft:iron_golem");
        field_233207_f_.add("minecraft:vindicator");
        field_233207_f_.add("minecraft:pillager");
        field_233207_f_.add("minecraft:wandering_trader");
        field_233207_f_.add("minecraft:witch");
        field_233207_f_.add("minecraft:wither");
        field_233207_f_.add("minecraft:wither_skeleton");
        field_233207_f_.add("minecraft:zombie");
        field_233207_f_.add("minecraft:zombie_villager");
        field_233207_f_.add("minecraft:phantom");
        field_233207_f_.add("minecraft:ravager");
        field_233207_f_.add("minecraft:piglin");
        field_233208_g_.add("minecraft:armor_stand");
        field_233209_h_.add("minecraft:arrow");
        field_233209_h_.add("minecraft:dragon_fireball");
        field_233209_h_.add("minecraft:firework_rocket");
        field_233209_h_.add("minecraft:fireball");
        field_233209_h_.add("minecraft:llama_spit");
        field_233209_h_.add("minecraft:small_fireball");
        field_233209_h_.add("minecraft:snowball");
        field_233209_h_.add("minecraft:spectral_arrow");
        field_233209_h_.add("minecraft:egg");
        field_233209_h_.add("minecraft:ender_pearl");
        field_233209_h_.add("minecraft:experience_bottle");
        field_233209_h_.add("minecraft:potion");
        field_233209_h_.add("minecraft:trident");
        field_233209_h_.add("minecraft:wither_skull");
    }
}
