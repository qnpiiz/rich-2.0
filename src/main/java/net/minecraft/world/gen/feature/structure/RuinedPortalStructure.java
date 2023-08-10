package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class RuinedPortalStructure extends Structure<RuinedPortalFeature>
{
    private static final String[] field_236331_u_ = new String[] {"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] field_236332_v_ = new String[] {"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};

    public RuinedPortalStructure(Codec<RuinedPortalFeature> p_i231984_1_)
    {
        super(p_i231984_1_);
    }

    public Structure.IStartFactory<RuinedPortalFeature> getStartFactory()
    {
        return RuinedPortalStructure.Start::new;
    }

    private static boolean func_236337_b_(BlockPos p_236337_0_, Biome p_236337_1_)
    {
        return p_236337_1_.getTemperature(p_236337_0_) < 0.15F;
    }

    private static int func_236339_b_(Random p_236339_0_, ChunkGenerator p_236339_1_, RuinedPortalPiece.Location p_236339_2_, boolean p_236339_3_, int p_236339_4_, int p_236339_5_, MutableBoundingBox p_236339_6_)
    {
        int i;

        if (p_236339_2_ == RuinedPortalPiece.Location.IN_NETHER)
        {
            if (p_236339_3_)
            {
                i = func_236335_a_(p_236339_0_, 32, 100);
            }
            else if (p_236339_0_.nextFloat() < 0.5F)
            {
                i = func_236335_a_(p_236339_0_, 27, 29);
            }
            else
            {
                i = func_236335_a_(p_236339_0_, 29, 100);
            }
        }
        else if (p_236339_2_ == RuinedPortalPiece.Location.IN_MOUNTAIN)
        {
            int j = p_236339_4_ - p_236339_5_;
            i = func_236338_b_(p_236339_0_, 70, j);
        }
        else if (p_236339_2_ == RuinedPortalPiece.Location.UNDERGROUND)
        {
            int i1 = p_236339_4_ - p_236339_5_;
            i = func_236338_b_(p_236339_0_, 15, i1);
        }
        else if (p_236339_2_ == RuinedPortalPiece.Location.PARTLY_BURIED)
        {
            i = p_236339_4_ - p_236339_5_ + func_236335_a_(p_236339_0_, 2, 8);
        }
        else
        {
            i = p_236339_4_;
        }

        List<BlockPos> list1 = ImmutableList.of(new BlockPos(p_236339_6_.minX, 0, p_236339_6_.minZ), new BlockPos(p_236339_6_.maxX, 0, p_236339_6_.minZ), new BlockPos(p_236339_6_.minX, 0, p_236339_6_.maxZ), new BlockPos(p_236339_6_.maxX, 0, p_236339_6_.maxZ));
        List<IBlockReader> list = list1.stream().map((p_236333_1_) ->
        {
            return p_236339_1_.func_230348_a_(p_236333_1_.getX(), p_236333_1_.getZ());
        }).collect(Collectors.toList());
        Heightmap.Type heightmap$type = p_236339_2_ == RuinedPortalPiece.Location.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int k;

        for (k = i; k > 15; --k)
        {
            int l = 0;
            blockpos$mutable.setPos(0, k, 0);

            for (IBlockReader iblockreader : list)
            {
                BlockState blockstate = iblockreader.getBlockState(blockpos$mutable);

                if (blockstate != null && heightmap$type.getHeightLimitPredicate().test(blockstate))
                {
                    ++l;

                    if (l == 3)
                    {
                        return k;
                    }
                }
            }
        }

        return k;
    }

    private static int func_236335_a_(Random p_236335_0_, int p_236335_1_, int p_236335_2_)
    {
        return p_236335_0_.nextInt(p_236335_2_ - p_236335_1_ + 1) + p_236335_1_;
    }

    private static int func_236338_b_(Random p_236338_0_, int p_236338_1_, int p_236338_2_)
    {
        return p_236338_1_ < p_236338_2_ ? func_236335_a_(p_236338_0_, p_236338_1_, p_236338_2_) : p_236338_2_;
    }

    public static enum Location implements IStringSerializable
    {
        STANDARD("standard"),
        DESERT("desert"),
        JUNGLE("jungle"),
        SWAMP("swamp"),
        MOUNTAIN("mountain"),
        OCEAN("ocean"),
        NETHER("nether");

        public static final Codec<RuinedPortalStructure.Location> field_236342_h_ = IStringSerializable.createEnumCodec(RuinedPortalStructure.Location::values, RuinedPortalStructure.Location::func_236346_a_);
        private static final Map<String, RuinedPortalStructure.Location> field_236343_i_ = Arrays.stream(values()).collect(Collectors.toMap(RuinedPortalStructure.Location::func_236347_b_, (p_236345_0_) -> {
            return p_236345_0_;
        }));
        private final String field_236344_j_;

        private Location(String p_i231986_3_)
        {
            this.field_236344_j_ = p_i231986_3_;
        }

        public String func_236347_b_()
        {
            return this.field_236344_j_;
        }

        public static RuinedPortalStructure.Location func_236346_a_(String p_236346_0_)
        {
            return field_236343_i_.get(p_236346_0_);
        }

        public String getString()
        {
            return this.field_236344_j_;
        }
    }

    public static class Start extends StructureStart<RuinedPortalFeature>
    {
        protected Start(Structure<RuinedPortalFeature> p_i231985_1_, int p_i231985_2_, int p_i231985_3_, MutableBoundingBox p_i231985_4_, int p_i231985_5_, long p_i231985_6_)
        {
            super(p_i231985_1_, p_i231985_2_, p_i231985_3_, p_i231985_4_, p_i231985_5_, p_i231985_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, RuinedPortalFeature p_230364_7_)
        {
            RuinedPortalPiece.Serializer ruinedportalpiece$serializer = new RuinedPortalPiece.Serializer();
            RuinedPortalPiece.Location ruinedportalpiece$location;

            if (p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.DESERT)
            {
                ruinedportalpiece$location = RuinedPortalPiece.Location.PARTLY_BURIED;
                ruinedportalpiece$serializer.field_237027_d_ = false;
                ruinedportalpiece$serializer.field_237026_c_ = 0.0F;
            }
            else if (p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.JUNGLE)
            {
                ruinedportalpiece$location = RuinedPortalPiece.Location.ON_LAND_SURFACE;
                ruinedportalpiece$serializer.field_237027_d_ = this.rand.nextFloat() < 0.5F;
                ruinedportalpiece$serializer.field_237026_c_ = 0.8F;
                ruinedportalpiece$serializer.field_237028_e_ = true;
                ruinedportalpiece$serializer.field_237029_f_ = true;
            }
            else if (p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.SWAMP)
            {
                ruinedportalpiece$location = RuinedPortalPiece.Location.ON_OCEAN_FLOOR;
                ruinedportalpiece$serializer.field_237027_d_ = false;
                ruinedportalpiece$serializer.field_237026_c_ = 0.5F;
                ruinedportalpiece$serializer.field_237029_f_ = true;
            }
            else if (p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.MOUNTAIN)
            {
                boolean flag = this.rand.nextFloat() < 0.5F;
                ruinedportalpiece$location = flag ? RuinedPortalPiece.Location.IN_MOUNTAIN : RuinedPortalPiece.Location.ON_LAND_SURFACE;
                ruinedportalpiece$serializer.field_237027_d_ = flag || this.rand.nextFloat() < 0.5F;
            }
            else if (p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.OCEAN)
            {
                ruinedportalpiece$location = RuinedPortalPiece.Location.ON_OCEAN_FLOOR;
                ruinedportalpiece$serializer.field_237027_d_ = false;
                ruinedportalpiece$serializer.field_237026_c_ = 0.8F;
            }
            else if (p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.NETHER)
            {
                ruinedportalpiece$location = RuinedPortalPiece.Location.IN_NETHER;
                ruinedportalpiece$serializer.field_237027_d_ = this.rand.nextFloat() < 0.5F;
                ruinedportalpiece$serializer.field_237026_c_ = 0.0F;
                ruinedportalpiece$serializer.field_237030_g_ = true;
            }
            else
            {
                boolean flag1 = this.rand.nextFloat() < 0.5F;
                ruinedportalpiece$location = flag1 ? RuinedPortalPiece.Location.UNDERGROUND : RuinedPortalPiece.Location.ON_LAND_SURFACE;
                ruinedportalpiece$serializer.field_237027_d_ = flag1 || this.rand.nextFloat() < 0.5F;
            }

            ResourceLocation resourcelocation;

            if (this.rand.nextFloat() < 0.05F)
            {
                resourcelocation = new ResourceLocation(RuinedPortalStructure.field_236332_v_[this.rand.nextInt(RuinedPortalStructure.field_236332_v_.length)]);
            }
            else
            {
                resourcelocation = new ResourceLocation(RuinedPortalStructure.field_236331_u_[this.rand.nextInt(RuinedPortalStructure.field_236331_u_.length)]);
            }

            Template template = p_230364_3_.getTemplateDefaulted(resourcelocation);
            Rotation rotation = Util.getRandomObject(Rotation.values(), this.rand);
            Mirror mirror = this.rand.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
            BlockPos blockpos = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
            BlockPos blockpos1 = (new ChunkPos(p_230364_4_, p_230364_5_)).asBlockPos();
            MutableBoundingBox mutableboundingbox = template.func_237150_a_(blockpos1, rotation, blockpos, mirror);
            Vector3i vector3i = mutableboundingbox.func_215126_f();
            int i = vector3i.getX();
            int j = vector3i.getZ();
            int k = p_230364_2_.getHeight(i, j, RuinedPortalPiece.func_237013_a_(ruinedportalpiece$location)) - 1;
            int l = RuinedPortalStructure.func_236339_b_(this.rand, p_230364_2_, ruinedportalpiece$location, ruinedportalpiece$serializer.field_237027_d_, k, mutableboundingbox.getYSize(), mutableboundingbox);
            BlockPos blockpos2 = new BlockPos(blockpos1.getX(), l, blockpos1.getZ());

            if (p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.MOUNTAIN || p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.OCEAN || p_230364_7_.field_236628_b_ == RuinedPortalStructure.Location.STANDARD)
            {
                ruinedportalpiece$serializer.field_237025_b_ = RuinedPortalStructure.func_236337_b_(blockpos2, p_230364_6_);
            }

            this.components.add(new RuinedPortalPiece(blockpos2, ruinedportalpiece$location, ruinedportalpiece$serializer, resourcelocation, template, rotation, mirror, blockpos));
            this.recalculateStructureSize();
        }
    }
}
