package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class RemoveGolemGossip extends NamedEntityFix
{
    public RemoveGolemGossip(Schema p_i241901_1_, boolean p_i241901_2_)
    {
        super(p_i241901_1_, p_i241901_2_, "Remove Golem Gossip Fix", TypeReferences.ENTITY, "minecraft:villager");
    }

    protected Typed<?> fix(Typed<?> p_207419_1_)
    {
        return p_207419_1_.update(DSL.remainderFinder(), RemoveGolemGossip::func_242266_a);
    }

    private static Dynamic<?> func_242266_a(Dynamic<?> p_242266_0_)
    {
        return p_242266_0_.update("Gossips", (p_242267_1_) ->
        {
            return p_242266_0_.createList(p_242267_1_.asStream().filter((p_242268_0_) -> {
                return !p_242268_0_.get("Type").asString("").equals("golem");
            }));
        });
    }
}
