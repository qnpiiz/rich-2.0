package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class Gossip extends NamedEntityFix
{
    public Gossip(Schema p_i231455_1_, String p_i231455_2_)
    {
        super(p_i231455_1_, false, "Gossip for for " + p_i231455_2_, TypeReferences.ENTITY, p_i231455_2_);
    }

    protected Typed<?> fix(Typed<?> p_207419_1_)
    {
        return p_207419_1_.update(DSL.remainderFinder(), (p_233255_0_) ->
        {
            return p_233255_0_.update("Gossips", (p_233257_0_) -> {
                return DataFixUtils.orElse(p_233257_0_.asStreamOpt().result().map((p_233256_0_) -> {
                    return p_233256_0_.map((p_233258_0_) -> {
                        return AbstractUUIDFix.func_233064_c_(p_233258_0_, "Target", "Target").orElse(p_233258_0_);
                    });
                }).map(p_233257_0_::createList), p_233257_0_);
            });
        });
    }
}
