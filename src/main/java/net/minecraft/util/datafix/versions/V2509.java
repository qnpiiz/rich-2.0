package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V2509 extends NamespacedSchema
{
    public V2509(int p_i231475_1_, Schema p_i231475_2_)
    {
        super(p_i231475_1_, p_i231475_2_);
    }

    protected static void func_233467_a_(Schema p_233467_0_, Map<String, Supplier<TypeTemplate>> p_233467_1_, String p_233467_2_)
    {
        p_233467_0_.register(p_233467_1_, p_233467_2_, () ->
        {
            return V0100.equipment(p_233467_0_);
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_)
    {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
        map.remove("minecraft:zombie_pigman");
        func_233467_a_(p_registerEntities_1_, map, "minecraft:zombified_piglin");
        return map;
    }
}
