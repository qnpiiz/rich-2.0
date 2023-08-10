package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class VillagerFollowRange extends NamedEntityFix
{
    public VillagerFollowRange(Schema p_i231467_1_)
    {
        super(p_i231467_1_, false, "Villager Follow Range Fix", TypeReferences.ENTITY, "minecraft:villager");
    }

    protected Typed<?> fix(Typed<?> p_207419_1_)
    {
        return p_207419_1_.update(DSL.remainderFinder(), VillagerFollowRange::func_233409_a_);
    }

    private static Dynamic<?> func_233409_a_(Dynamic<?> p_233409_0_)
    {
        return p_233409_0_.update("Attributes", (p_233410_1_) ->
        {
            return p_233409_0_.createList(p_233410_1_.asStream().map((p_233411_0_) -> {
                return p_233411_0_.get("Name").asString("").equals("generic.follow_range") && p_233411_0_.get("Base").asDouble(0.0D) == 16.0D ? p_233411_0_.set("Base", p_233411_0_.createDouble(48.0D)) : p_233411_0_;
            }));
        });
    }
}
