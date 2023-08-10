package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V2522 extends NamespacedSchema
{
    public V2522(int p_i231477_1_, Schema p_i231477_2_)
    {
        super(p_i231477_1_, p_i231477_2_);
    }

    protected static void func_233471_a_(Schema p_233471_0_, Map<String, Supplier<TypeTemplate>> p_233471_1_, String p_233471_2_)
    {
        p_233471_0_.register(p_233471_1_, p_233471_2_, () ->
        {
            return V0100.equipment(p_233471_0_);
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_)
    {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
        func_233471_a_(p_registerEntities_1_, map, "minecraft:zoglin");
        return map;
    }
}
