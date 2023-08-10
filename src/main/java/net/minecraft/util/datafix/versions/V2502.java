package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V2502 extends NamespacedSchema
{
    public V2502(int p_i231473_1_, Schema p_i231473_2_)
    {
        super(p_i231473_1_, p_i231473_2_);
    }

    protected static void func_233463_a_(Schema p_233463_0_, Map<String, Supplier<TypeTemplate>> p_233463_1_, String p_233463_2_)
    {
        p_233463_0_.register(p_233463_1_, p_233463_2_, () ->
        {
            return V0100.equipment(p_233463_0_);
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_)
    {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
        func_233463_a_(p_registerEntities_1_, map, "minecraft:hoglin");
        return map;
    }
}
