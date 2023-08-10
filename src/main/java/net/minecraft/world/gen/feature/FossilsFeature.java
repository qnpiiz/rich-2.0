package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FossilsFeature extends Feature<NoFeatureConfig>
{
    private static final ResourceLocation STRUCTURE_SPINE_01 = new ResourceLocation("fossil/spine_1");
    private static final ResourceLocation STRUCTURE_SPINE_02 = new ResourceLocation("fossil/spine_2");
    private static final ResourceLocation STRUCTURE_SPINE_03 = new ResourceLocation("fossil/spine_3");
    private static final ResourceLocation STRUCTURE_SPINE_04 = new ResourceLocation("fossil/spine_4");
    private static final ResourceLocation STRUCTURE_SPINE_01_COAL = new ResourceLocation("fossil/spine_1_coal");
    private static final ResourceLocation STRUCTURE_SPINE_02_COAL = new ResourceLocation("fossil/spine_2_coal");
    private static final ResourceLocation STRUCTURE_SPINE_03_COAL = new ResourceLocation("fossil/spine_3_coal");
    private static final ResourceLocation STRUCTURE_SPINE_04_COAL = new ResourceLocation("fossil/spine_4_coal");
    private static final ResourceLocation STRUCTURE_SKULL_01 = new ResourceLocation("fossil/skull_1");
    private static final ResourceLocation STRUCTURE_SKULL_02 = new ResourceLocation("fossil/skull_2");
    private static final ResourceLocation STRUCTURE_SKULL_03 = new ResourceLocation("fossil/skull_3");
    private static final ResourceLocation STRUCTURE_SKULL_04 = new ResourceLocation("fossil/skull_4");
    private static final ResourceLocation STRUCTURE_SKULL_01_COAL = new ResourceLocation("fossil/skull_1_coal");
    private static final ResourceLocation STRUCTURE_SKULL_02_COAL = new ResourceLocation("fossil/skull_2_coal");
    private static final ResourceLocation STRUCTURE_SKULL_03_COAL = new ResourceLocation("fossil/skull_3_coal");
    private static final ResourceLocation STRUCTURE_SKULL_04_COAL = new ResourceLocation("fossil/skull_4_coal");
    private static final ResourceLocation[] FOSSILS = new ResourceLocation[] {STRUCTURE_SPINE_01, STRUCTURE_SPINE_02, STRUCTURE_SPINE_03, STRUCTURE_SPINE_04, STRUCTURE_SKULL_01, STRUCTURE_SKULL_02, STRUCTURE_SKULL_03, STRUCTURE_SKULL_04};
    private static final ResourceLocation[] FOSSILS_COAL = new ResourceLocation[] {STRUCTURE_SPINE_01_COAL, STRUCTURE_SPINE_02_COAL, STRUCTURE_SPINE_03_COAL, STRUCTURE_SPINE_04_COAL, STRUCTURE_SKULL_01_COAL, STRUCTURE_SKULL_02_COAL, STRUCTURE_SKULL_03_COAL, STRUCTURE_SKULL_04_COAL};

    public FossilsFeature(Codec<NoFeatureConfig> p_i231955_1_)
    {
        super(p_i231955_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        Rotation rotation = Rotation.randomRotation(p_241855_3_);
        int i = p_241855_3_.nextInt(FOSSILS.length);
        TemplateManager templatemanager = p_241855_1_.getWorld().getServer().func_240792_aT_();
        Template template = templatemanager.getTemplateDefaulted(FOSSILS[i]);
        Template template1 = templatemanager.getTemplateDefaulted(FOSSILS_COAL[i]);
        ChunkPos chunkpos = new ChunkPos(p_241855_4_);
        MutableBoundingBox mutableboundingbox = new MutableBoundingBox(chunkpos.getXStart(), 0, chunkpos.getZStart(), chunkpos.getXEnd(), 256, chunkpos.getZEnd());
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(rotation).setBoundingBox(mutableboundingbox).setRandom(p_241855_3_).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
        BlockPos blockpos = template.transformedSize(rotation);
        int j = p_241855_3_.nextInt(16 - blockpos.getX());
        int k = p_241855_3_.nextInt(16 - blockpos.getZ());
        int l = 256;

        for (int i1 = 0; i1 < blockpos.getX(); ++i1)
        {
            for (int j1 = 0; j1 < blockpos.getZ(); ++j1)
            {
                l = Math.min(l, p_241855_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, p_241855_4_.getX() + i1 + j, p_241855_4_.getZ() + j1 + k));
            }
        }

        int k1 = Math.max(l - 15 - p_241855_3_.nextInt(10), 10);
        BlockPos blockpos1 = template.getZeroPositionWithTransform(p_241855_4_.add(j, k1, k), Mirror.NONE, rotation);
        IntegrityProcessor integrityprocessor = new IntegrityProcessor(0.9F);
        placementsettings.clearProcessors().addProcessor(integrityprocessor);
        template.func_237146_a_(p_241855_1_, blockpos1, blockpos1, placementsettings, p_241855_3_, 4);
        placementsettings.removeProcessor(integrityprocessor);
        IntegrityProcessor integrityprocessor1 = new IntegrityProcessor(0.1F);
        placementsettings.clearProcessors().addProcessor(integrityprocessor1);
        template1.func_237146_a_(p_241855_1_, blockpos1, blockpos1, placementsettings, p_241855_3_, 4);
        return true;
    }
}
