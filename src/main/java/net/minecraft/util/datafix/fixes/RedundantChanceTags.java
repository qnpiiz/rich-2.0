package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import net.minecraft.util.datafix.TypeReferences;

public class RedundantChanceTags extends DataFix
{
    private static final Codec<List<Float>> field_241303_a_ = Codec.FLOAT.listOf();

    public RedundantChanceTags(Schema outputSchema, boolean changesType)
    {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule()
    {
        return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_210996_0_) ->
        {
            return p_210996_0_.update(DSL.remainderFinder(), (p_206334_0_) -> {
                if (func_241306_a_(p_206334_0_.get("HandDropChances"), 2))
                {
                    p_206334_0_ = p_206334_0_.remove("HandDropChances");
                }

                if (func_241306_a_(p_206334_0_.get("ArmorDropChances"), 4))
                {
                    p_206334_0_ = p_206334_0_.remove("ArmorDropChances");
                }

                return p_206334_0_;
            });
        });
    }

    private static boolean func_241306_a_(OptionalDynamic<?> p_241306_0_, int p_241306_1_)
    {
        return p_241306_0_.flatMap(field_241303_a_::parse).map((p_241304_1_) ->
        {
            return p_241304_1_.size() == p_241306_1_ && p_241304_1_.stream().allMatch((p_241307_0_) -> {
                return p_241307_0_ == 0.0F;
            });
        }).result().orElse(false);
    }
}
