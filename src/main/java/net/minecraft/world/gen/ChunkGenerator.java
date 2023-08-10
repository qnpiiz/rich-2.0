package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.gen.settings.StructureSpreadSettings;
import net.minecraft.world.server.ServerWorld;

public abstract class ChunkGenerator
{
    public static final Codec<ChunkGenerator> field_235948_a_ = Registry.CHUNK_GENERATOR_CODEC.dispatchStable(ChunkGenerator::func_230347_a_, Function.identity());
    protected final BiomeProvider biomeProvider;
    protected final BiomeProvider field_235949_c_;
    private final DimensionStructuresSettings settings;
    private final long field_235950_e_;
    private final List<ChunkPos> field_235951_f_ = Lists.newArrayList();

    public ChunkGenerator(BiomeProvider p_i231888_1_, DimensionStructuresSettings p_i231888_2_)
    {
        this(p_i231888_1_, p_i231888_1_, p_i231888_2_, 0L);
    }

    public ChunkGenerator(BiomeProvider p_i231887_1_, BiomeProvider p_i231887_2_, DimensionStructuresSettings p_i231887_3_, long p_i231887_4_)
    {
        this.biomeProvider = p_i231887_1_;
        this.field_235949_c_ = p_i231887_2_;
        this.settings = p_i231887_3_;
        this.field_235950_e_ = p_i231887_4_;
    }

    private void func_235958_g_()
    {
        if (this.field_235951_f_.isEmpty())
        {
            StructureSpreadSettings structurespreadsettings = this.settings.func_236199_b_();

            if (structurespreadsettings != null && structurespreadsettings.func_236663_c_() != 0)
            {
                List<Biome> list = Lists.newArrayList();

                for (Biome biome : this.biomeProvider.getBiomes())
                {
                    if (biome.getGenerationSettings().hasStructure(Structure.field_236375_k_))
                    {
                        list.add(biome);
                    }
                }

                int k1 = structurespreadsettings.func_236660_a_();
                int l1 = structurespreadsettings.func_236663_c_();
                int i = structurespreadsettings.func_236662_b_();
                Random random = new Random();
                random.setSeed(this.field_235950_e_);
                double d0 = random.nextDouble() * Math.PI * 2.0D;
                int j = 0;
                int k = 0;

                for (int l = 0; l < l1; ++l)
                {
                    double d1 = (double)(4 * k1 + k1 * k * 6) + (random.nextDouble() - 0.5D) * (double)k1 * 2.5D;
                    int i1 = (int)Math.round(Math.cos(d0) * d1);
                    int j1 = (int)Math.round(Math.sin(d0) * d1);
                    BlockPos blockpos = this.biomeProvider.findBiomePosition((i1 << 4) + 8, 0, (j1 << 4) + 8, 112, list::contains, random);

                    if (blockpos != null)
                    {
                        i1 = blockpos.getX() >> 4;
                        j1 = blockpos.getZ() >> 4;
                    }

                    this.field_235951_f_.add(new ChunkPos(i1, j1));
                    d0 += (Math.PI * 2D) / (double)i;
                    ++j;

                    if (j == i)
                    {
                        ++k;
                        j = 0;
                        i = i + 2 * i / (k + 1);
                        i = Math.min(i, l1 - l);
                        d0 += random.nextDouble() * Math.PI * 2.0D;
                    }
                }
            }
        }
    }

    protected abstract Codec <? extends ChunkGenerator > func_230347_a_();

    public abstract ChunkGenerator func_230349_a_(long p_230349_1_);

    public void func_242706_a(Registry<Biome> p_242706_1_, IChunk p_242706_2_)
    {
        ChunkPos chunkpos = p_242706_2_.getPos();
        ((ChunkPrimer)p_242706_2_).setBiomes(new BiomeContainer(p_242706_1_, chunkpos, this.field_235949_c_));
    }

    public void func_230350_a_(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_)
    {
        BiomeManager biomemanager = p_230350_3_.copyWithProvider(this.biomeProvider);
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        int i = 8;
        ChunkPos chunkpos = p_230350_4_.getPos();
        int j = chunkpos.x;
        int k = chunkpos.z;
        BiomeGenerationSettings biomegenerationsettings = this.biomeProvider.getNoiseBiome(chunkpos.x << 2, 0, chunkpos.z << 2).getGenerationSettings();
        BitSet bitset = ((ChunkPrimer)p_230350_4_).getOrAddCarvingMask(p_230350_5_);

        for (int l = j - 8; l <= j + 8; ++l)
        {
            for (int i1 = k - 8; i1 <= k + 8; ++i1)
            {
                List < Supplier < ConfiguredCarver<? >>> list = biomegenerationsettings.getCarvers(p_230350_5_);
                ListIterator < Supplier < ConfiguredCarver<? >>> listiterator = list.listIterator();

                while (listiterator.hasNext())
                {
                    int j1 = listiterator.nextIndex();
                    ConfiguredCarver<?> configuredcarver = listiterator.next().get();
                    sharedseedrandom.setLargeFeatureSeed(p_230350_1_ + (long)j1, l, i1);

                    if (configuredcarver.shouldCarve(sharedseedrandom, l, i1))
                    {
                        configuredcarver.carveRegion(p_230350_4_, biomemanager::getBiome, sharedseedrandom, this.func_230356_f_(), l, i1, j, k, bitset);
                    }
                }
            }
        }
    }

    @Nullable
    public BlockPos func_235956_a_(ServerWorld p_235956_1_, Structure<?> p_235956_2_, BlockPos p_235956_3_, int p_235956_4_, boolean p_235956_5_)
    {
        if (!this.biomeProvider.hasStructure(p_235956_2_))
        {
            return null;
        }
        else if (p_235956_2_ == Structure.field_236375_k_)
        {
            this.func_235958_g_();
            BlockPos blockpos = null;
            double d0 = Double.MAX_VALUE;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for (ChunkPos chunkpos : this.field_235951_f_)
            {
                blockpos$mutable.setPos((chunkpos.x << 4) + 8, 32, (chunkpos.z << 4) + 8);
                double d1 = blockpos$mutable.distanceSq(p_235956_3_);

                if (blockpos == null)
                {
                    blockpos = new BlockPos(blockpos$mutable);
                    d0 = d1;
                }
                else if (d1 < d0)
                {
                    blockpos = new BlockPos(blockpos$mutable);
                    d0 = d1;
                }
            }

            return blockpos;
        }
        else
        {
            StructureSeparationSettings structureseparationsettings = this.settings.func_236197_a_(p_235956_2_);
            return structureseparationsettings == null ? null : p_235956_2_.func_236388_a_(p_235956_1_, p_235956_1_.func_241112_a_(), p_235956_3_, p_235956_4_, p_235956_5_, p_235956_1_.getSeed(), structureseparationsettings);
        }
    }

    public void func_230351_a_(WorldGenRegion p_230351_1_, StructureManager p_230351_2_)
    {
        int i = p_230351_1_.getMainChunkX();
        int j = p_230351_1_.getMainChunkZ();
        int k = i * 16;
        int l = j * 16;
        BlockPos blockpos = new BlockPos(k, 0, l);
        Biome biome = this.biomeProvider.getNoiseBiome((i << 2) + 2, 2, (j << 2) + 2);
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        long i1 = sharedseedrandom.setDecorationSeed(p_230351_1_.getSeed(), k, l);

        try
        {
            biome.generateFeatures(p_230351_2_, this, p_230351_1_, i1, sharedseedrandom, blockpos);
        }
        catch (Exception exception)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(exception, "Biome decoration");
            crashreport.makeCategory("Generation").addDetail("CenterX", i).addDetail("CenterZ", j).addDetail("Seed", i1).addDetail("Biome", biome);
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Generate the SURFACE part of a chunk
     */
    public abstract void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_);

    public void func_230354_a_(WorldGenRegion p_230354_1_)
    {
    }

    public DimensionStructuresSettings func_235957_b_()
    {
        return this.settings;
    }

    public int getGroundHeight()
    {
        return 64;
    }

    public BiomeProvider getBiomeProvider()
    {
        return this.field_235949_c_;
    }

    public int func_230355_e_()
    {
        return 256;
    }

    public List<MobSpawnInfo.Spawners> func_230353_a_(Biome p_230353_1_, StructureManager p_230353_2_, EntityClassification p_230353_3_, BlockPos p_230353_4_)
    {
        return p_230353_1_.getMobSpawnInfo().getSpawners(p_230353_3_);
    }

    public void func_242707_a(DynamicRegistries p_242707_1_, StructureManager p_242707_2_, IChunk p_242707_3_, TemplateManager p_242707_4_, long p_242707_5_)
    {
        ChunkPos chunkpos = p_242707_3_.getPos();
        Biome biome = this.biomeProvider.getNoiseBiome((chunkpos.x << 2) + 2, 0, (chunkpos.z << 2) + 2);
        this.func_242705_a(StructureFeatures.field_244145_k, p_242707_1_, p_242707_2_, p_242707_3_, p_242707_4_, p_242707_5_, chunkpos, biome);

        for (Supplier < StructureFeature <? , ? >> supplier : biome.getGenerationSettings().getStructures())
        {
            this.func_242705_a(supplier.get(), p_242707_1_, p_242707_2_, p_242707_3_, p_242707_4_, p_242707_5_, chunkpos, biome);
        }
    }

    private void func_242705_a(StructureFeature <? , ? > p_242705_1_, DynamicRegistries p_242705_2_, StructureManager p_242705_3_, IChunk p_242705_4_, TemplateManager p_242705_5_, long p_242705_6_, ChunkPos p_242705_8_, Biome p_242705_9_)
    {
        StructureStart<?> structurestart = p_242705_3_.func_235013_a_(SectionPos.from(p_242705_4_.getPos(), 0), p_242705_1_.field_236268_b_, p_242705_4_);
        int i = structurestart != null ? structurestart.getRefCount() : 0;
        StructureSeparationSettings structureseparationsettings = this.settings.func_236197_a_(p_242705_1_.field_236268_b_);

        if (structureseparationsettings != null)
        {
            StructureStart<?> structurestart1 = p_242705_1_.func_242771_a(p_242705_2_, this, this.biomeProvider, p_242705_5_, p_242705_6_, p_242705_8_, p_242705_9_, i, structureseparationsettings);
            p_242705_3_.func_235014_a_(SectionPos.from(p_242705_4_.getPos(), 0), p_242705_1_.field_236268_b_, structurestart1, p_242705_4_);
        }
    }

    public void func_235953_a_(ISeedReader p_235953_1_, StructureManager p_235953_2_, IChunk p_235953_3_)
    {
        int i = 8;
        int j = p_235953_3_.getPos().x;
        int k = p_235953_3_.getPos().z;
        int l = j << 4;
        int i1 = k << 4;
        SectionPos sectionpos = SectionPos.from(p_235953_3_.getPos(), 0);

        for (int j1 = j - 8; j1 <= j + 8; ++j1)
        {
            for (int k1 = k - 8; k1 <= k + 8; ++k1)
            {
                long l1 = ChunkPos.asLong(j1, k1);

                for (StructureStart<?> structurestart : p_235953_1_.getChunk(j1, k1).getStructureStarts().values())
                {
                    try
                    {
                        if (structurestart != StructureStart.DUMMY && structurestart.getBoundingBox().intersectsWith(l, i1, l + 15, i1 + 15))
                        {
                            p_235953_2_.func_235012_a_(sectionpos, structurestart.getStructure(), l1, p_235953_3_);
                            DebugPacketSender.sendStructureStart(p_235953_1_, structurestart);
                        }
                    }
                    catch (Exception exception)
                    {
                        CrashReport crashreport = CrashReport.makeCrashReport(exception, "Generating structure reference");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Structure");
                        crashreportcategory.addDetail("Id", () ->
                        {
                            return Registry.STRUCTURE_FEATURE.getKey(structurestart.getStructure()).toString();
                        });
                        crashreportcategory.addDetail("Name", () ->
                        {
                            return structurestart.getStructure().getStructureName();
                        });
                        crashreportcategory.addDetail("Class", () ->
                        {
                            return structurestart.getStructure().getClass().getCanonicalName();
                        });
                        throw new ReportedException(crashreport);
                    }
                }
            }
        }
    }

    public abstract void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_);

    public int func_230356_f_()
    {
        return 63;
    }

    public abstract int getHeight(int x, int z, Heightmap.Type heightmapType);

    public abstract IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_);

    public int getNoiseHeight(int x, int z, Heightmap.Type heightmapType)
    {
        return this.getHeight(x, z, heightmapType);
    }

    public int getNoiseHeightMinusOne(int x, int z, Heightmap.Type heightmapType)
    {
        return this.getHeight(x, z, heightmapType) - 1;
    }

    public boolean func_235952_a_(ChunkPos p_235952_1_)
    {
        this.func_235958_g_();
        return this.field_235951_f_.contains(p_235952_1_);
    }

    static
    {
        Registry.register(Registry.CHUNK_GENERATOR_CODEC, "noise", NoiseChunkGenerator.field_236079_d_);
        Registry.register(Registry.CHUNK_GENERATOR_CODEC, "flat", FlatChunkGenerator.field_236069_d_);
        Registry.register(Registry.CHUNK_GENERATOR_CODEC, "debug", DebugChunkGenerator.field_236066_e_);
    }
}
