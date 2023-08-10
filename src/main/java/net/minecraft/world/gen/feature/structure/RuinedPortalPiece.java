package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.BlackStoneReplacementProcessor;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.BlockMosinessProcessor;
import net.minecraft.world.gen.feature.template.LavaSubmergingProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.RandomBlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleStructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuinedPortalPiece extends TemplateStructurePiece
{
    private static final Logger field_237003_d_ = LogManager.getLogger();
    private final ResourceLocation field_237004_e_;
    private final Rotation field_237005_f_;
    private final Mirror field_237006_g_;
    private final RuinedPortalPiece.Location field_237007_h_;
    private final RuinedPortalPiece.Serializer field_237008_i_;

    public RuinedPortalPiece(BlockPos p_i232111_1_, RuinedPortalPiece.Location p_i232111_2_, RuinedPortalPiece.Serializer p_i232111_3_, ResourceLocation p_i232111_4_, Template p_i232111_5_, Rotation p_i232111_6_, Mirror p_i232111_7_, BlockPos p_i232111_8_)
    {
        super(IStructurePieceType.RUINED_PORTAL, 0);
        this.templatePosition = p_i232111_1_;
        this.field_237004_e_ = p_i232111_4_;
        this.field_237005_f_ = p_i232111_6_;
        this.field_237006_g_ = p_i232111_7_;
        this.field_237007_h_ = p_i232111_2_;
        this.field_237008_i_ = p_i232111_3_;
        this.func_237014_a_(p_i232111_5_, p_i232111_8_);
    }

    public RuinedPortalPiece(TemplateManager p_i232110_1_, CompoundNBT p_i232110_2_)
    {
        super(IStructurePieceType.RUINED_PORTAL, p_i232110_2_);
        this.field_237004_e_ = new ResourceLocation(p_i232110_2_.getString("Template"));
        this.field_237005_f_ = Rotation.valueOf(p_i232110_2_.getString("Rotation"));
        this.field_237006_g_ = Mirror.valueOf(p_i232110_2_.getString("Mirror"));
        this.field_237007_h_ = RuinedPortalPiece.Location.func_237042_a_(p_i232110_2_.getString("VerticalPlacement"));
        this.field_237008_i_ = RuinedPortalPiece.Serializer.field_237024_a_.parse(new Dynamic<>(NBTDynamicOps.INSTANCE, p_i232110_2_.get("Properties"))).getOrThrow(true, field_237003_d_::error);
        Template template = p_i232110_1_.getTemplateDefaulted(this.field_237004_e_);
        this.func_237014_a_(template, new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2));
    }

    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected void readAdditional(CompoundNBT tagCompound)
    {
        super.readAdditional(tagCompound);
        tagCompound.putString("Template", this.field_237004_e_.toString());
        tagCompound.putString("Rotation", this.field_237005_f_.name());
        tagCompound.putString("Mirror", this.field_237006_g_.name());
        tagCompound.putString("VerticalPlacement", this.field_237007_h_.func_237040_a_());
        RuinedPortalPiece.Serializer.field_237024_a_.encodeStart(NBTDynamicOps.INSTANCE, this.field_237008_i_).resultOrPartial(field_237003_d_::error).ifPresent((p_237018_1_) ->
        {
            tagCompound.put("Properties", p_237018_1_);
        });
    }

    private void func_237014_a_(Template p_237014_1_, BlockPos p_237014_2_)
    {
        BlockIgnoreStructureProcessor blockignorestructureprocessor = this.field_237008_i_.field_237027_d_ ? BlockIgnoreStructureProcessor.STRUCTURE_BLOCK : BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK;
        List<RuleEntry> list = Lists.newArrayList();
        list.add(func_237011_a_(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
        list.add(this.func_237021_c_());

        if (!this.field_237008_i_.field_237025_b_)
        {
            list.add(func_237011_a_(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
        }

        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_237005_f_).setMirror(this.field_237006_g_).setCenterOffset(p_237014_2_).addProcessor(blockignorestructureprocessor).addProcessor(new RuleStructureProcessor(list)).addProcessor(new BlockMosinessProcessor(this.field_237008_i_.field_237026_c_)).addProcessor(new LavaSubmergingProcessor());

        if (this.field_237008_i_.field_237030_g_)
        {
            placementsettings.addProcessor(BlackStoneReplacementProcessor.field_237058_b_);
        }

        this.setup(p_237014_1_, this.templatePosition, placementsettings);
    }

    private RuleEntry func_237021_c_()
    {
        if (this.field_237007_h_ == RuinedPortalPiece.Location.ON_OCEAN_FLOOR)
        {
            return func_237012_a_(Blocks.LAVA, Blocks.MAGMA_BLOCK);
        }
        else
        {
            return this.field_237008_i_.field_237025_b_ ? func_237012_a_(Blocks.LAVA, Blocks.NETHERRACK) : func_237011_a_(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK);
        }
    }

    public boolean func_230383_a_(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_)
    {
        if (!p_230383_5_.isVecInside(this.templatePosition))
        {
            return true;
        }
        else
        {
            p_230383_5_.expandTo(this.template.getMutableBoundingBox(this.placeSettings, this.templatePosition));
            boolean flag = super.func_230383_a_(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_6_, p_230383_7_);
            this.func_237019_b_(p_230383_4_, p_230383_1_);
            this.func_237015_a_(p_230383_4_, p_230383_1_);

            if (this.field_237008_i_.field_237029_f_ || this.field_237008_i_.field_237028_e_)
            {
                BlockPos.getAllInBox(this.getBoundingBox()).forEach((p_237017_3_) ->
                {
                    if (this.field_237008_i_.field_237029_f_)
                    {
                        this.func_237016_a_(p_230383_4_, p_230383_1_, p_237017_3_);
                    }

                    if (this.field_237008_i_.field_237028_e_)
                    {
                        this.func_237020_b_(p_230383_4_, p_230383_1_, p_237017_3_);
                    }
                });
            }

            return flag;
        }
    }

    protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb)
    {
    }

    private void func_237016_a_(Random p_237016_1_, IWorld p_237016_2_, BlockPos p_237016_3_)
    {
        BlockState blockstate = p_237016_2_.getBlockState(p_237016_3_);

        if (!blockstate.isAir() && !blockstate.isIn(Blocks.VINE))
        {
            Direction direction = Direction.Plane.HORIZONTAL.random(p_237016_1_);
            BlockPos blockpos = p_237016_3_.offset(direction);
            BlockState blockstate1 = p_237016_2_.getBlockState(blockpos);

            if (blockstate1.isAir())
            {
                if (Block.doesSideFillSquare(blockstate.getCollisionShape(p_237016_2_, p_237016_3_), direction))
                {
                    BooleanProperty booleanproperty = VineBlock.getPropertyFor(direction.getOpposite());
                    p_237016_2_.setBlockState(blockpos, Blocks.VINE.getDefaultState().with(booleanproperty, Boolean.valueOf(true)), 3);
                }
            }
        }
    }

    private void func_237020_b_(Random p_237020_1_, IWorld p_237020_2_, BlockPos p_237020_3_)
    {
        if (p_237020_1_.nextFloat() < 0.5F && p_237020_2_.getBlockState(p_237020_3_).isIn(Blocks.NETHERRACK) && p_237020_2_.getBlockState(p_237020_3_.up()).isAir())
        {
            p_237020_2_.setBlockState(p_237020_3_.up(), Blocks.JUNGLE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, Boolean.valueOf(true)), 3);
        }
    }

    private void func_237015_a_(Random p_237015_1_, IWorld p_237015_2_)
    {
        for (int i = this.boundingBox.minX + 1; i < this.boundingBox.maxX; ++i)
        {
            for (int j = this.boundingBox.minZ + 1; j < this.boundingBox.maxZ; ++j)
            {
                BlockPos blockpos = new BlockPos(i, this.boundingBox.minY, j);

                if (p_237015_2_.getBlockState(blockpos).isIn(Blocks.NETHERRACK))
                {
                    this.func_237022_c_(p_237015_1_, p_237015_2_, blockpos.down());
                }
            }
        }
    }

    private void func_237022_c_(Random p_237022_1_, IWorld p_237022_2_, BlockPos p_237022_3_)
    {
        BlockPos.Mutable blockpos$mutable = p_237022_3_.toMutable();
        this.func_237023_d_(p_237022_1_, p_237022_2_, blockpos$mutable);
        int i = 8;

        while (i > 0 && p_237022_1_.nextFloat() < 0.5F)
        {
            blockpos$mutable.move(Direction.DOWN);
            --i;
            this.func_237023_d_(p_237022_1_, p_237022_2_, blockpos$mutable);
        }
    }

    private void func_237019_b_(Random p_237019_1_, IWorld p_237019_2_)
    {
        boolean flag = this.field_237007_h_ == RuinedPortalPiece.Location.ON_LAND_SURFACE || this.field_237007_h_ == RuinedPortalPiece.Location.ON_OCEAN_FLOOR;
        Vector3i vector3i = this.boundingBox.func_215126_f();
        int i = vector3i.getX();
        int j = vector3i.getZ();
        float[] afloat = new float[] {1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
        int k = afloat.length;
        int l = (this.boundingBox.getXSize() + this.boundingBox.getZSize()) / 2;
        int i1 = p_237019_1_.nextInt(Math.max(1, 8 - l / 2));
        int j1 = 3;
        BlockPos.Mutable blockpos$mutable = BlockPos.ZERO.toMutable();

        for (int k1 = i - k; k1 <= i + k; ++k1)
        {
            for (int l1 = j - k; l1 <= j + k; ++l1)
            {
                int i2 = Math.abs(k1 - i) + Math.abs(l1 - j);
                int j2 = Math.max(0, i2 + i1);

                if (j2 < k)
                {
                    float f = afloat[j2];

                    if (p_237019_1_.nextDouble() < (double)f)
                    {
                        int k2 = func_237009_a_(p_237019_2_, k1, l1, this.field_237007_h_);
                        int l2 = flag ? k2 : Math.min(this.boundingBox.minY, k2);
                        blockpos$mutable.setPos(k1, l2, l1);

                        if (Math.abs(l2 - this.boundingBox.minY) <= 3 && this.func_237010_a_(p_237019_2_, blockpos$mutable))
                        {
                            this.func_237023_d_(p_237019_1_, p_237019_2_, blockpos$mutable);

                            if (this.field_237008_i_.field_237028_e_)
                            {
                                this.func_237020_b_(p_237019_1_, p_237019_2_, blockpos$mutable);
                            }

                            this.func_237022_c_(p_237019_1_, p_237019_2_, blockpos$mutable.down());
                        }
                    }
                }
            }
        }
    }

    private boolean func_237010_a_(IWorld p_237010_1_, BlockPos p_237010_2_)
    {
        BlockState blockstate = p_237010_1_.getBlockState(p_237010_2_);
        return !blockstate.isIn(Blocks.AIR) && !blockstate.isIn(Blocks.OBSIDIAN) && !blockstate.isIn(Blocks.CHEST) && (this.field_237007_h_ == RuinedPortalPiece.Location.IN_NETHER || !blockstate.isIn(Blocks.LAVA));
    }

    private void func_237023_d_(Random p_237023_1_, IWorld p_237023_2_, BlockPos p_237023_3_)
    {
        if (!this.field_237008_i_.field_237025_b_ && p_237023_1_.nextFloat() < 0.07F)
        {
            p_237023_2_.setBlockState(p_237023_3_, Blocks.MAGMA_BLOCK.getDefaultState(), 3);
        }
        else
        {
            p_237023_2_.setBlockState(p_237023_3_, Blocks.NETHERRACK.getDefaultState(), 3);
        }
    }

    private static int func_237009_a_(IWorld p_237009_0_, int p_237009_1_, int p_237009_2_, RuinedPortalPiece.Location p_237009_3_)
    {
        return p_237009_0_.getHeight(func_237013_a_(p_237009_3_), p_237009_1_, p_237009_2_) - 1;
    }

    public static Heightmap.Type func_237013_a_(RuinedPortalPiece.Location p_237013_0_)
    {
        return p_237013_0_ == RuinedPortalPiece.Location.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
    }

    private static RuleEntry func_237011_a_(Block p_237011_0_, float p_237011_1_, Block p_237011_2_)
    {
        return new RuleEntry(new RandomBlockMatchRuleTest(p_237011_0_, p_237011_1_), AlwaysTrueRuleTest.INSTANCE, p_237011_2_.getDefaultState());
    }

    private static RuleEntry func_237012_a_(Block p_237012_0_, Block p_237012_1_)
    {
        return new RuleEntry(new BlockMatchRuleTest(p_237012_0_), AlwaysTrueRuleTest.INSTANCE, p_237012_1_.getDefaultState());
    }

    public static enum Location
    {
        ON_LAND_SURFACE("on_land_surface"),
        PARTLY_BURIED("partly_buried"),
        ON_OCEAN_FLOOR("on_ocean_floor"),
        IN_MOUNTAIN("in_mountain"),
        UNDERGROUND("underground"),
        IN_NETHER("in_nether");

        private static final Map<String, RuinedPortalPiece.Location> field_237038_g_ = Arrays.stream(values()).collect(Collectors.toMap(RuinedPortalPiece.Location::func_237040_a_, (p_237041_0_) -> {
            return p_237041_0_;
        }));
        private final String field_237039_h_;

        private Location(String p_i232113_3_)
        {
            this.field_237039_h_ = p_i232113_3_;
        }

        public String func_237040_a_()
        {
            return this.field_237039_h_;
        }

        public static RuinedPortalPiece.Location func_237042_a_(String p_237042_0_)
        {
            return field_237038_g_.get(p_237042_0_);
        }
    }

    public static class Serializer
    {
        public static final Codec<RuinedPortalPiece.Serializer> field_237024_a_ = RecordCodecBuilder.create((p_237031_0_) ->
        {
            return p_237031_0_.group(Codec.BOOL.fieldOf("cold").forGetter((p_237037_0_) -> {
                return p_237037_0_.field_237025_b_;
            }), Codec.FLOAT.fieldOf("mossiness").forGetter((p_237036_0_) -> {
                return p_237036_0_.field_237026_c_;
            }), Codec.BOOL.fieldOf("air_pocket").forGetter((p_237035_0_) -> {
                return p_237035_0_.field_237027_d_;
            }), Codec.BOOL.fieldOf("overgrown").forGetter((p_237034_0_) -> {
                return p_237034_0_.field_237028_e_;
            }), Codec.BOOL.fieldOf("vines").forGetter((p_237033_0_) -> {
                return p_237033_0_.field_237029_f_;
            }), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter((p_237032_0_) -> {
                return p_237032_0_.field_237030_g_;
            })).apply(p_237031_0_, RuinedPortalPiece.Serializer::new);
        });
        public boolean field_237025_b_;
        public float field_237026_c_ = 0.2F;
        public boolean field_237027_d_;
        public boolean field_237028_e_;
        public boolean field_237029_f_;
        public boolean field_237030_g_;

        public Serializer()
        {
        }

        public <T> Serializer(boolean p_i232112_1_, float p_i232112_2_, boolean p_i232112_3_, boolean p_i232112_4_, boolean p_i232112_5_, boolean p_i232112_6_)
        {
            this.field_237025_b_ = p_i232112_1_;
            this.field_237026_c_ = p_i232112_2_;
            this.field_237027_d_ = p_i232112_3_;
            this.field_237028_e_ = p_i232112_4_;
            this.field_237029_f_ = p_i232112_5_;
            this.field_237030_g_ = p_i232112_6_;
        }
    }
}
