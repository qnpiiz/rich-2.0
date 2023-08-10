package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class SwapHandsFix extends DataFix
{
    private final String field_241315_a_;
    private final String field_241316_b_;
    private final String field_241317_c_;

    public SwapHandsFix(Schema p_i241231_1_, boolean p_i241231_2_, String p_i241231_3_, String p_i241231_4_, String p_i241231_5_)
    {
        super(p_i241231_1_, p_i241231_2_);
        this.field_241315_a_ = p_i241231_3_;
        this.field_241316_b_ = p_i241231_4_;
        this.field_241317_c_ = p_i241231_5_;
    }

    public TypeRewriteRule makeRule()
    {
        return this.fixTypeEverywhereTyped(this.field_241315_a_, this.getInputSchema().getType(TypeReferences.OPTIONS), (p_241318_1_) ->
        {
            return p_241318_1_.update(DSL.remainderFinder(), (p_241319_1_) -> {
                return DataFixUtils.orElse(p_241319_1_.get(this.field_241316_b_).result().map((p_241320_2_) -> {
                    return p_241319_1_.set(this.field_241317_c_, p_241320_2_).remove(this.field_241316_b_);
                }), p_241319_1_);
            });
        });
    }
}
