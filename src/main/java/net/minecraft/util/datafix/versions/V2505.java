package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V2505 extends NamespacedSchema
{
    public V2505(int p_i231474_1_, Schema p_i231474_2_)
    {
        super(p_i231474_1_, p_i231474_2_);
    }

    protected static void func_233465_a_(Schema p_233465_0_, Map<String, Supplier<TypeTemplate>> p_233465_1_, String p_233465_2_)
    {
        p_233465_0_.register(p_233465_1_, p_233465_2_, () ->
        {
            return V0100.equipment(p_233465_0_);
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_)
    {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
        func_233465_a_(p_registerEntities_1_, map, "minecraft:piglin");
        return map;
    }
}
