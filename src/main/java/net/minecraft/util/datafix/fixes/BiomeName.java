package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class BiomeName extends DataFix
{
    private final String field_233377_a_;
    private final Map<String, String> field_233378_b_;

    public BiomeName(Schema p_i231463_1_, boolean p_i231463_2_, String p_i231463_3_, Map<String, String> p_i231463_4_)
    {
        super(p_i231463_1_, p_i231463_2_);
        this.field_233378_b_ = p_i231463_4_;
        this.field_233377_a_ = p_i231463_3_;
    }

    protected TypeRewriteRule makeRule()
    {
        Type<Pair<String, String>> type = DSL.named(TypeReferences.BIOME.typeName(), NamespacedSchema.func_233457_a_());

        if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.BIOME)))
        {
            throw new IllegalStateException("Biome type is not what was expected.");
        }
        else
        {
            return this.fixTypeEverywhere(this.field_233377_a_, type, (p_233380_1_) ->
            {
                return (p_233379_1_) -> {
                    return p_233379_1_.mapSecond((p_233381_1_) -> {
                        return this.field_233378_b_.getOrDefault(p_233381_1_, p_233381_1_);
                    });
                };
            });
        }
    }
}
