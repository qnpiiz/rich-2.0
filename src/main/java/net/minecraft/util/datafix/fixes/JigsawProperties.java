package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class JigsawProperties extends NamedEntityFix
{
    public JigsawProperties(Schema p_i231457_1_, boolean p_i231457_2_)
    {
        super(p_i231457_1_, p_i231457_2_, "JigsawPropertiesFix", TypeReferences.BLOCK_ENTITY, "minecraft:jigsaw");
    }

    private static Dynamic<?> func_233289_a_(Dynamic<?> p_233289_0_)
    {
        String s = p_233289_0_.get("attachement_type").asString("minecraft:empty");
        String s1 = p_233289_0_.get("target_pool").asString("minecraft:empty");
        return p_233289_0_.set("name", p_233289_0_.createString(s)).set("target", p_233289_0_.createString(s)).remove("attachement_type").set("pool", p_233289_0_.createString(s1)).remove("target_pool");
    }

    protected Typed<?> fix(Typed<?> p_207419_1_)
    {
        return p_207419_1_.update(DSL.remainderFinder(), JigsawProperties::func_233289_a_);
    }
}
