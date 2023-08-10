package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Structure<C extends IFeatureConfig>
{
    public static final BiMap < String, Structure<? >> field_236365_a_ = HashBiMap.create();
    private static final Map < Structure<?>, GenerationStage.Decoration > field_236385_u_ = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Structure<VillageConfig> field_236366_b_ = func_236394_a_("Pillager_Outpost", new PillagerOutpostStructure(VillageConfig.field_236533_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<MineshaftConfig> field_236367_c_ = func_236394_a_("Mineshaft", new MineshaftStructure(MineshaftConfig.field_236541_a_), GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
    public static final Structure<NoFeatureConfig> field_236368_d_ = func_236394_a_("Mansion", new WoodlandMansionStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<NoFeatureConfig> field_236369_e_ = func_236394_a_("Jungle_Pyramid", new JunglePyramidStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<NoFeatureConfig> field_236370_f_ = func_236394_a_("Desert_Pyramid", new DesertPyramidStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<NoFeatureConfig> field_236371_g_ = func_236394_a_("Igloo", new IglooStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<RuinedPortalFeature> field_236372_h_ = func_236394_a_("Ruined_Portal", new RuinedPortalStructure(RuinedPortalFeature.field_236627_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<ShipwreckConfig> field_236373_i_ = func_236394_a_("Shipwreck", new ShipwreckStructure(ShipwreckConfig.field_236634_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final SwampHutStructure field_236374_j_ = func_236394_a_("Swamp_Hut", new SwampHutStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<NoFeatureConfig> field_236375_k_ = func_236394_a_("Stronghold", new StrongholdStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.STRONGHOLDS);
    public static final Structure<NoFeatureConfig> field_236376_l_ = func_236394_a_("Monument", new OceanMonumentStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<OceanRuinConfig> field_236377_m_ = func_236394_a_("Ocean_Ruin", new OceanRuinStructure(OceanRuinConfig.field_236561_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<NoFeatureConfig> field_236378_n_ = func_236394_a_("Fortress", new FortressStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.UNDERGROUND_DECORATION);
    public static final Structure<NoFeatureConfig> field_236379_o_ = func_236394_a_("EndCity", new EndCityStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<ProbabilityConfig> field_236380_p_ = func_236394_a_("Buried_Treasure", new BuriedTreasureStructure(ProbabilityConfig.field_236576_b_), GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
    public static final Structure<VillageConfig> field_236381_q_ = func_236394_a_("Village", new VillageStructure(VillageConfig.field_236533_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final Structure<NoFeatureConfig> field_236382_r_ = func_236394_a_("Nether_Fossil", new NetherFossilStructure(NoFeatureConfig.field_236558_a_), GenerationStage.Decoration.UNDERGROUND_DECORATION);
    public static final Structure<VillageConfig> field_236383_s_ = func_236394_a_("Bastion_Remnant", new BastionRemantsStructure(VillageConfig.field_236533_a_), GenerationStage.Decoration.SURFACE_STRUCTURES);
    public static final List < Structure<? >> field_236384_t_ = ImmutableList.of(field_236366_b_, field_236381_q_, field_236382_r_);
    private static final ResourceLocation field_242783_w = new ResourceLocation("jigsaw");
    private static final Map<ResourceLocation, ResourceLocation> field_242784_x = ImmutableMap.<ResourceLocation, ResourceLocation>builder().put(new ResourceLocation("nvi"), field_242783_w).put(new ResourceLocation("pcp"), field_242783_w).put(new ResourceLocation("bastionremnant"), field_242783_w).put(new ResourceLocation("runtime"), field_242783_w).build();
    private final Codec<StructureFeature<C, Structure<C>>> field_236386_w_;

    private static < F extends Structure<? >> F func_236394_a_(String p_236394_0_, F p_236394_1_, GenerationStage.Decoration p_236394_2_)
    {
        field_236365_a_.put(p_236394_0_.toLowerCase(Locale.ROOT), p_236394_1_);
        field_236385_u_.put(p_236394_1_, p_236394_2_);
        return Registry.register(Registry.STRUCTURE_FEATURE, p_236394_0_.toLowerCase(Locale.ROOT), p_236394_1_);
    }

    public Structure(Codec<C> p_i231997_1_)
    {
        this.field_236386_w_ = p_i231997_1_.fieldOf("config").xmap((p_236395_1_) ->
        {
            return new StructureFeature<>(this, p_236395_1_);
        }, (p_236390_0_) ->
        {
            return p_236390_0_.field_236269_c_;
        }).codec();
    }

    public GenerationStage.Decoration func_236396_f_()
    {
        return field_236385_u_.get(this);
    }

    public static void func_236397_g_()
    {
    }

    @Nullable
    public static StructureStart<?> func_236393_a_(TemplateManager p_236393_0_, CompoundNBT p_236393_1_, long p_236393_2_)
    {
        String s = p_236393_1_.getString("id");

        if ("INVALID".equals(s))
        {
            return StructureStart.DUMMY;
        }
        else
        {
            Structure<?> structure = Registry.STRUCTURE_FEATURE.getOrDefault(new ResourceLocation(s.toLowerCase(Locale.ROOT)));

            if (structure == null)
            {
                LOGGER.error("Unknown feature id: {}", (Object)s);
                return null;
            }
            else
            {
                int i = p_236393_1_.getInt("ChunkX");
                int j = p_236393_1_.getInt("ChunkZ");
                int k = p_236393_1_.getInt("references");
                MutableBoundingBox mutableboundingbox = p_236393_1_.contains("BB") ? new MutableBoundingBox(p_236393_1_.getIntArray("BB")) : MutableBoundingBox.getNewBoundingBox();
                ListNBT listnbt = p_236393_1_.getList("Children", 10);

                try
                {
                    StructureStart<?> structurestart = structure.func_236387_a_(i, j, mutableboundingbox, k, p_236393_2_);

                    for (int l = 0; l < listnbt.size(); ++l)
                    {
                        CompoundNBT compoundnbt = listnbt.getCompound(l);
                        String s1 = compoundnbt.getString("id").toLowerCase(Locale.ROOT);
                        ResourceLocation resourcelocation = new ResourceLocation(s1);
                        ResourceLocation resourcelocation1 = field_242784_x.getOrDefault(resourcelocation, resourcelocation);
                        IStructurePieceType istructurepiecetype = Registry.STRUCTURE_PIECE.getOrDefault(resourcelocation1);

                        if (istructurepiecetype == null)
                        {
                            LOGGER.error("Unknown structure piece id: {}", (Object)resourcelocation1);
                        }
                        else
                        {
                            try
                            {
                                StructurePiece structurepiece = istructurepiecetype.load(p_236393_0_, compoundnbt);
                                structurestart.getComponents().add(structurepiece);
                            }
                            catch (Exception exception)
                            {
                                LOGGER.error("Exception loading structure piece with id {}", resourcelocation1, exception);
                            }
                        }
                    }

                    return structurestart;
                }
                catch (Exception exception1)
                {
                    LOGGER.error("Failed Start with id {}", s, exception1);
                    return null;
                }
            }
        }
    }

    public Codec<StructureFeature<C, Structure<C>>> func_236398_h_()
    {
        return this.field_236386_w_;
    }

    public StructureFeature < C, ? extends Structure<C >> func_236391_a_(C p_236391_1_)
    {
        return new StructureFeature<>(this, p_236391_1_);
    }

    @Nullable
    public BlockPos func_236388_a_(IWorldReader p_236388_1_, StructureManager p_236388_2_, BlockPos p_236388_3_, int p_236388_4_, boolean p_236388_5_, long p_236388_6_, StructureSeparationSettings p_236388_8_)
    {
        int i = p_236388_8_.func_236668_a_();
        int j = p_236388_3_.getX() >> 4;
        int k = p_236388_3_.getZ() >> 4;
        int l = 0;

        for (SharedSeedRandom sharedseedrandom = new SharedSeedRandom(); l <= p_236388_4_; ++l)
        {
            for (int i1 = -l; i1 <= l; ++i1)
            {
                boolean flag = i1 == -l || i1 == l;

                for (int j1 = -l; j1 <= l; ++j1)
                {
                    boolean flag1 = j1 == -l || j1 == l;

                    if (flag || flag1)
                    {
                        int k1 = j + i * i1;
                        int l1 = k + i * j1;
                        ChunkPos chunkpos = this.func_236392_a_(p_236388_8_, p_236388_6_, sharedseedrandom, k1, l1);
                        IChunk ichunk = p_236388_1_.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_STARTS);
                        StructureStart<?> structurestart = p_236388_2_.func_235013_a_(SectionPos.from(ichunk.getPos(), 0), this, ichunk);

                        if (structurestart != null && structurestart.isValid())
                        {
                            if (p_236388_5_ && structurestart.isRefCountBelowMax())
                            {
                                structurestart.incrementRefCount();
                                return structurestart.getPos();
                            }

                            if (!p_236388_5_)
                            {
                                return structurestart.getPos();
                            }
                        }

                        if (l == 0)
                        {
                            break;
                        }
                    }
                }

                if (l == 0)
                {
                    break;
                }
            }
        }

        return null;
    }

    protected boolean func_230365_b_()
    {
        return true;
    }

    public final ChunkPos func_236392_a_(StructureSeparationSettings p_236392_1_, long p_236392_2_, SharedSeedRandom p_236392_4_, int p_236392_5_, int p_236392_6_)
    {
        int i = p_236392_1_.func_236668_a_();
        int j = p_236392_1_.func_236671_b_();
        int k = Math.floorDiv(p_236392_5_, i);
        int l = Math.floorDiv(p_236392_6_, i);
        p_236392_4_.setLargeFeatureSeedWithSalt(p_236392_2_, k, l, p_236392_1_.func_236673_c_());
        int i1;
        int j1;

        if (this.func_230365_b_())
        {
            i1 = p_236392_4_.nextInt(i - j);
            j1 = p_236392_4_.nextInt(i - j);
        }
        else
        {
            i1 = (p_236392_4_.nextInt(i - j) + p_236392_4_.nextInt(i - j)) / 2;
            j1 = (p_236392_4_.nextInt(i - j) + p_236392_4_.nextInt(i - j)) / 2;
        }

        return new ChunkPos(k * i + i1, l * i + j1);
    }

    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, C p_230363_10_)
    {
        return true;
    }

    private StructureStart<C> func_236387_a_(int p_236387_1_, int p_236387_2_, MutableBoundingBox p_236387_3_, int p_236387_4_, long p_236387_5_)
    {
        return this.getStartFactory().create(this, p_236387_1_, p_236387_2_, p_236387_3_, p_236387_4_, p_236387_5_);
    }

    public StructureStart<?> func_242785_a(DynamicRegistries p_242785_1_, ChunkGenerator p_242785_2_, BiomeProvider p_242785_3_, TemplateManager p_242785_4_, long p_242785_5_, ChunkPos p_242785_7_, Biome p_242785_8_, int p_242785_9_, SharedSeedRandom p_242785_10_, StructureSeparationSettings p_242785_11_, C p_242785_12_)
    {
        ChunkPos chunkpos = this.func_236392_a_(p_242785_11_, p_242785_5_, p_242785_10_, p_242785_7_.x, p_242785_7_.z);

        if (p_242785_7_.x == chunkpos.x && p_242785_7_.z == chunkpos.z && this.func_230363_a_(p_242785_2_, p_242785_3_, p_242785_5_, p_242785_10_, p_242785_7_.x, p_242785_7_.z, p_242785_8_, chunkpos, p_242785_12_))
        {
            StructureStart<C> structurestart = this.func_236387_a_(p_242785_7_.x, p_242785_7_.z, MutableBoundingBox.getNewBoundingBox(), p_242785_9_, p_242785_5_);
            structurestart.func_230364_a_(p_242785_1_, p_242785_2_, p_242785_4_, p_242785_7_.x, p_242785_7_.z, p_242785_8_, p_242785_12_);

            if (structurestart.isValid())
            {
                return structurestart;
            }
        }

        return StructureStart.DUMMY;
    }

    public abstract Structure.IStartFactory<C> getStartFactory();

    public String getStructureName()
    {
        return field_236365_a_.inverse().get(this);
    }

    public List<MobSpawnInfo.Spawners> getSpawnList()
    {
        return ImmutableList.of();
    }

    public List<MobSpawnInfo.Spawners> getCreatureSpawnList()
    {
        return ImmutableList.of();
    }

    public interface IStartFactory<C extends IFeatureConfig>
    {
        StructureStart<C> create(Structure<C> p_create_1_, int p_create_2_, int p_create_3_, MutableBoundingBox p_create_4_, int p_create_5_, long p_create_6_);
    }
}
