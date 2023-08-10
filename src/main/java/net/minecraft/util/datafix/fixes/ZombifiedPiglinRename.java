package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class ZombifiedPiglinRename extends TypedEntityRenameHelper
{
    public static final Map<String, String> field_233242_a_ = ImmutableMap.<String, String>builder().put("minecraft:zombie_pigman_spawn_egg", "minecraft:zombified_piglin_spawn_egg").build();

    public ZombifiedPiglinRename(Schema p_i231453_1_)
    {
        super("EntityZombifiedPiglinRenameFix", p_i231453_1_, true);
    }

    protected String rename(String name)
    {
        return Objects.equals("minecraft:zombie_pigman", name) ? "minecraft:zombified_piglin" : name;
    }
}
