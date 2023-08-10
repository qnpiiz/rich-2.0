package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class PlayerUUID extends AbstractUUIDFix
{
    public PlayerUUID(Schema p_i231461_1_)
    {
        super(p_i231461_1_, TypeReferences.PLAYER);
    }

    protected TypeRewriteRule makeRule()
    {
        return this.fixTypeEverywhereTyped("PlayerUUIDFix", this.getInputSchema().getType(this.reference), (p_233353_0_) ->
        {
            OpticFinder<?> opticfinder = p_233353_0_.getType().findField("RootVehicle");
            return p_233353_0_.updateTyped(opticfinder, opticfinder.type(), (p_233354_0_) -> {
                return p_233354_0_.update(DSL.remainderFinder(), (p_233356_0_) -> {
                    return func_233064_c_(p_233356_0_, "Attach", "Attach").orElse(p_233356_0_);
                });
            }).update(DSL.remainderFinder(), (p_233355_0_) -> {
                return EntityUUID.func_233214_c_(EntityUUID.func_233212_b_(p_233355_0_));
            });
        });
    }
}
