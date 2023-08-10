package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class RedstoneConnections extends DataFix
{
    public RedstoneConnections(Schema p_i231462_1_)
    {
        super(p_i231462_1_, false);
    }

    protected TypeRewriteRule makeRule()
    {
        Schema schema = this.getInputSchema();
        return this.fixTypeEverywhereTyped("RedstoneConnectionsFix", schema.getType(TypeReferences.BLOCK_STATE), (p_233367_1_) ->
        {
            return p_233367_1_.update(DSL.remainderFinder(), this::func_233368_a_);
        });
    }

    private <T> Dynamic<T> func_233368_a_(Dynamic<T> p_233368_1_)
    {
        boolean flag = p_233368_1_.get("Name").asString().result().filter("minecraft:redstone_wire"::equals).isPresent();
        return !flag ? p_233368_1_ : p_233368_1_.update("Properties", (p_233371_0_) ->
        {
            String s = p_233371_0_.get("east").asString("none");
            String s1 = p_233371_0_.get("west").asString("none");
            String s2 = p_233371_0_.get("north").asString("none");
            String s3 = p_233371_0_.get("south").asString("none");
            boolean flag1 = func_233369_a_(s) || func_233369_a_(s1);
            boolean flag2 = func_233369_a_(s2) || func_233369_a_(s3);
            String s4 = !func_233369_a_(s) && !flag2 ? "side" : s;
            String s5 = !func_233369_a_(s1) && !flag2 ? "side" : s1;
            String s6 = !func_233369_a_(s2) && !flag1 ? "side" : s2;
            String s7 = !func_233369_a_(s3) && !flag1 ? "side" : s3;
            return p_233371_0_.update("east", (p_233374_1_) -> {
                return p_233374_1_.createString(s4);
            }).update("west", (p_233373_1_) -> {
                return p_233373_1_.createString(s5);
            }).update("north", (p_233372_1_) -> {
                return p_233372_1_.createString(s6);
            }).update("south", (p_233370_1_) -> {
                return p_233370_1_.createString(s7);
            });
        });
    }

    private static boolean func_233369_a_(String p_233369_0_)
    {
        return !"none".equals(p_233369_0_);
    }
}
