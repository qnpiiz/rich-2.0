package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureFeatures
{
    public static final StructureFeature < VillageConfig, ? extends Structure<VillageConfig >> field_244135_a = func_244162_a("pillager_outpost", Structure.field_236366_b_.func_236391_a_(new VillageConfig(() ->
    {
        return PillagerOutpostPools.field_244088_a;
    }, 7)));
    public static final StructureFeature < MineshaftConfig, ? extends Structure<MineshaftConfig >> field_244136_b = func_244162_a("mineshaft", Structure.field_236367_c_.func_236391_a_(new MineshaftConfig(0.004F, MineshaftStructure.Type.NORMAL)));
    public static final StructureFeature < MineshaftConfig, ? extends Structure<MineshaftConfig >> field_244137_c = func_244162_a("mineshaft_mesa", Structure.field_236367_c_.func_236391_a_(new MineshaftConfig(0.004F, MineshaftStructure.Type.MESA)));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244138_d = func_244162_a("mansion", Structure.field_236368_d_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244139_e = func_244162_a("jungle_pyramid", Structure.field_236369_e_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244140_f = func_244162_a("desert_pyramid", Structure.field_236370_f_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244141_g = func_244162_a("igloo", Structure.field_236371_g_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < ShipwreckConfig, ? extends Structure<ShipwreckConfig >> field_244142_h = func_244162_a("shipwreck", Structure.field_236373_i_.func_236391_a_(new ShipwreckConfig(false)));
    public static final StructureFeature < ShipwreckConfig, ? extends Structure<ShipwreckConfig >> field_244143_i = func_244162_a("shipwreck_beached", Structure.field_236373_i_.func_236391_a_(new ShipwreckConfig(true)));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244144_j = func_244162_a("swamp_hut", Structure.field_236374_j_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244145_k = func_244162_a("stronghold", Structure.field_236375_k_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244146_l = func_244162_a("monument", Structure.field_236376_l_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < OceanRuinConfig, ? extends Structure<OceanRuinConfig >> field_244147_m = func_244162_a("ocean_ruin_cold", Structure.field_236377_m_.func_236391_a_(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F)));
    public static final StructureFeature < OceanRuinConfig, ? extends Structure<OceanRuinConfig >> field_244148_n = func_244162_a("ocean_ruin_warm", Structure.field_236377_m_.func_236391_a_(new OceanRuinConfig(OceanRuinStructure.Type.WARM, 0.3F, 0.9F)));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244149_o = func_244162_a("fortress", Structure.field_236378_n_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244150_p = func_244162_a("nether_fossil", Structure.field_236382_r_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < NoFeatureConfig, ? extends Structure<NoFeatureConfig >> field_244151_q = func_244162_a("end_city", Structure.field_236379_o_.func_236391_a_(NoFeatureConfig.field_236559_b_));
    public static final StructureFeature < ProbabilityConfig, ? extends Structure<ProbabilityConfig >> field_244152_r = func_244162_a("buried_treasure", Structure.field_236380_p_.func_236391_a_(new ProbabilityConfig(0.01F)));
    public static final StructureFeature < VillageConfig, ? extends Structure<VillageConfig >> field_244153_s = func_244162_a("bastion_remnant", Structure.field_236383_s_.func_236391_a_(new VillageConfig(() ->
    {
        return BastionRemnantsPieces.field_243686_a;
    }, 6)));
    public static final StructureFeature < VillageConfig, ? extends Structure<VillageConfig >> field_244154_t = func_244162_a("village_plains", Structure.field_236381_q_.func_236391_a_(new VillageConfig(() ->
    {
        return PlainsVillagePools.field_244090_a;
    }, 6)));
    public static final StructureFeature < VillageConfig, ? extends Structure<VillageConfig >> field_244155_u = func_244162_a("village_desert", Structure.field_236381_q_.func_236391_a_(new VillageConfig(() ->
    {
        return DesertVillagePools.field_243774_a;
    }, 6)));
    public static final StructureFeature < VillageConfig, ? extends Structure<VillageConfig >> field_244156_v = func_244162_a("village_savanna", Structure.field_236381_q_.func_236391_a_(new VillageConfig(() ->
    {
        return SavannaVillagePools.field_244128_a;
    }, 6)));
    public static final StructureFeature < VillageConfig, ? extends Structure<VillageConfig >> field_244157_w = func_244162_a("village_snowy", Structure.field_236381_q_.func_236391_a_(new VillageConfig(() ->
    {
        return SnowyVillagePools.field_244129_a;
    }, 6)));
    public static final StructureFeature < VillageConfig, ? extends Structure<VillageConfig >> field_244158_x = func_244162_a("village_taiga", Structure.field_236381_q_.func_236391_a_(new VillageConfig(() ->
    {
        return TaigaVillagePools.field_244193_a;
    }, 6)));
    public static final StructureFeature < RuinedPortalFeature, ? extends Structure<RuinedPortalFeature >> field_244159_y = func_244162_a("ruined_portal", Structure.field_236372_h_.func_236391_a_(new RuinedPortalFeature(RuinedPortalStructure.Location.STANDARD)));
    public static final StructureFeature < RuinedPortalFeature, ? extends Structure<RuinedPortalFeature >> field_244160_z = func_244162_a("ruined_portal_desert", Structure.field_236372_h_.func_236391_a_(new RuinedPortalFeature(RuinedPortalStructure.Location.DESERT)));
    public static final StructureFeature < RuinedPortalFeature, ? extends Structure<RuinedPortalFeature >> field_244130_A = func_244162_a("ruined_portal_jungle", Structure.field_236372_h_.func_236391_a_(new RuinedPortalFeature(RuinedPortalStructure.Location.JUNGLE)));
    public static final StructureFeature < RuinedPortalFeature, ? extends Structure<RuinedPortalFeature >> field_244131_B = func_244162_a("ruined_portal_swamp", Structure.field_236372_h_.func_236391_a_(new RuinedPortalFeature(RuinedPortalStructure.Location.SWAMP)));
    public static final StructureFeature < RuinedPortalFeature, ? extends Structure<RuinedPortalFeature >> field_244132_C = func_244162_a("ruined_portal_mountain", Structure.field_236372_h_.func_236391_a_(new RuinedPortalFeature(RuinedPortalStructure.Location.MOUNTAIN)));
    public static final StructureFeature < RuinedPortalFeature, ? extends Structure<RuinedPortalFeature >> field_244133_D = func_244162_a("ruined_portal_ocean", Structure.field_236372_h_.func_236391_a_(new RuinedPortalFeature(RuinedPortalStructure.Location.OCEAN)));
    public static final StructureFeature < RuinedPortalFeature, ? extends Structure<RuinedPortalFeature >> field_244134_E = func_244162_a("ruined_portal_nether", Structure.field_236372_h_.func_236391_a_(new RuinedPortalFeature(RuinedPortalStructure.Location.NETHER)));

    private static <FC extends IFeatureConfig, F extends Structure<FC>> StructureFeature<FC, F> func_244162_a(String p_244162_0_, StructureFeature<FC, F> p_244162_1_)
    {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, p_244162_0_, p_244162_1_);
    }
}
