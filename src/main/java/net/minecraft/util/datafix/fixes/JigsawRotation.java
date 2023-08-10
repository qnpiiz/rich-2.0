package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class JigsawRotation extends DataFix
{
    private static final Map<String, String> field_233290_a_ = ImmutableMap.<String, String>builder().put("down", "down_south").put("up", "up_north").put("north", "north_up").put("south", "south_up").put("west", "west_up").put("east", "east_up").build();

    public JigsawRotation(Schema p_i231458_1_, boolean p_i231458_2_)
    {
        super(p_i231458_1_, p_i231458_2_);
    }

    private static Dynamic<?> func_233292_a_(Dynamic<?> p_233292_0_)
    {
        Optional<String> optional = p_233292_0_.get("Name").asString().result();
        return optional.equals(Optional.of("minecraft:jigsaw")) ? p_233292_0_.update("Properties", (p_233293_0_) ->
        {
            String s = p_233293_0_.get("facing").asString("north");
            return p_233293_0_.remove("facing").set("orientation", p_233293_0_.createString(field_233290_a_.getOrDefault(s, s)));
        }) : p_233292_0_;
    }

    protected TypeRewriteRule makeRule()
    {
        return this.fixTypeEverywhereTyped("jigsaw_rotation_fix", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), (p_233291_0_) ->
        {
            return p_233291_0_.update(DSL.remainderFinder(), JigsawRotation::func_233292_a_);
        });
    }
}
