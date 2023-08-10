package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.structure.BastionRemnantsPieces;
import net.minecraft.world.gen.feature.structure.PillagerOutpostPools;
import net.minecraft.world.gen.feature.structure.VillagesPools;

public class JigsawPatternRegistry
{
    public static final RegistryKey<JigsawPattern> field_244091_a = RegistryKey.getOrCreateKey(Registry.JIGSAW_POOL_KEY, new ResourceLocation("empty"));
    private static final JigsawPattern field_244092_b = func_244094_a(new JigsawPattern(field_244091_a.getLocation(), field_244091_a.getLocation(), ImmutableList.of(), JigsawPattern.PlacementBehaviour.RIGID));

    public static JigsawPattern func_244094_a(JigsawPattern p_244094_0_)
    {
        return WorldGenRegistries.register(WorldGenRegistries.JIGSAW_POOL, p_244094_0_.getName(), p_244094_0_);
    }

    public static JigsawPattern func_244093_a()
    {
        BastionRemnantsPieces.func_236258_a_();
        PillagerOutpostPools.func_244089_a();
        VillagesPools.func_244194_a();
        return field_244092_b;
    }

    static
    {
        func_244093_a();
    }
}
