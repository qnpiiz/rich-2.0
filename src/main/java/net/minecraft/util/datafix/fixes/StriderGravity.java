package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class StriderGravity extends NamedEntityFix
{
    public StriderGravity(Schema p_i231466_1_, boolean p_i231466_2_)
    {
        super(p_i231466_1_, p_i231466_2_, "StriderGravityFix", TypeReferences.ENTITY, "minecraft:strider");
    }

    public Dynamic<?> func_233403_a_(Dynamic<?> p_233403_1_)
    {
        return p_233403_1_.get("NoGravity").asBoolean(false) ? p_233403_1_.set("NoGravity", p_233403_1_.createBoolean(false)) : p_233403_1_;
    }

    protected Typed<?> fix(Typed<?> p_207419_1_)
    {
        return p_207419_1_.update(DSL.remainderFinder(), this::func_233403_a_);
    }
}
