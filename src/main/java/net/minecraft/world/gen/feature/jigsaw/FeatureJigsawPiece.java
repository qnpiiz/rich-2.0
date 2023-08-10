package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FeatureJigsawPiece extends JigsawPiece
{
    public static final Codec<FeatureJigsawPiece> field_236816_a_ = RecordCodecBuilder.create((p_236817_0_) ->
    {
        return p_236817_0_.group(ConfiguredFeature.field_236264_b_.fieldOf("feature").forGetter((p_236818_0_) -> {
            return p_236818_0_.configuredFeature;
        }), func_236848_d_()).apply(p_236817_0_, FeatureJigsawPiece::new);
    });
    private final Supplier < ConfiguredFeature <? , ? >> configuredFeature;
    private final CompoundNBT nbt;

    protected FeatureJigsawPiece(Supplier < ConfiguredFeature <? , ? >> p_i242004_1_, JigsawPattern.PlacementBehaviour p_i242004_2_)
    {
        super(p_i242004_2_);
        this.configuredFeature = p_i242004_1_;
        this.nbt = this.writeNBT();
    }

    private CompoundNBT writeNBT()
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("name", "minecraft:bottom");
        compoundnbt.putString("final_state", "minecraft:air");
        compoundnbt.putString("pool", "minecraft:empty");
        compoundnbt.putString("target", "minecraft:empty");
        compoundnbt.putString("joint", JigsawTileEntity.OrientationType.ROLLABLE.getString());
        return compoundnbt;
    }

    public BlockPos getSize(TemplateManager p_214868_1_, Rotation p_214868_2_)
    {
        return BlockPos.ZERO;
    }

    public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand)
    {
        List<Template.BlockInfo> list = Lists.newArrayList();
        list.add(new Template.BlockInfo(pos, Blocks.JIGSAW.getDefaultState().with(JigsawBlock.ORIENTATION, JigsawOrientation.func_239641_a_(Direction.DOWN, Direction.SOUTH)), this.nbt));
        return list;
    }

    public MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn)
    {
        BlockPos blockpos = this.getSize(templateManagerIn, rotationIn);
        return new MutableBoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + blockpos.getX(), pos.getY() + blockpos.getY(), pos.getZ() + blockpos.getZ());
    }

    public boolean func_230378_a_(TemplateManager p_230378_1_, ISeedReader p_230378_2_, StructureManager p_230378_3_, ChunkGenerator p_230378_4_, BlockPos p_230378_5_, BlockPos p_230378_6_, Rotation p_230378_7_, MutableBoundingBox p_230378_8_, Random p_230378_9_, boolean p_230378_10_)
    {
        return this.configuredFeature.get().func_242765_a(p_230378_2_, p_230378_4_, p_230378_9_, p_230378_5_);
    }

    public IJigsawDeserializer<?> getType()
    {
        return IJigsawDeserializer.FEATURE_POOL_ELEMENT;
    }

    public String toString()
    {
        return "Feature[" + Registry.FEATURE.getKey(this.configuredFeature.get().func_242766_b()) + "]";
    }
}
