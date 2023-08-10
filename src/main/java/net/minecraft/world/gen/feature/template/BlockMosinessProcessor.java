package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockMosinessProcessor extends StructureProcessor
{
    public static final Codec<BlockMosinessProcessor> field_237062_a_ = Codec.FLOAT.fieldOf("mossiness").xmap(BlockMosinessProcessor::new, (p_237064_0_) ->
    {
        return p_237064_0_.field_237063_b_;
    }).codec();
    private final float field_237063_b_;

    public BlockMosinessProcessor(float p_i232115_1_)
    {
        this.field_237063_b_ = p_i232115_1_;
    }

    @Nullable
    public Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_)
    {
        Random random = p_230386_6_.getRandom(p_230386_5_.pos);
        BlockState blockstate = p_230386_5_.state;
        BlockPos blockpos = p_230386_5_.pos;
        BlockState blockstate1 = null;

        if (!blockstate.isIn(Blocks.STONE_BRICKS) && !blockstate.isIn(Blocks.STONE) && !blockstate.isIn(Blocks.CHISELED_STONE_BRICKS))
        {
            if (blockstate.isIn(BlockTags.STAIRS))
            {
                blockstate1 = this.func_237067_a_(random, p_230386_5_.state);
            }
            else if (blockstate.isIn(BlockTags.SLABS))
            {
                blockstate1 = this.func_237070_b_(random);
            }
            else if (blockstate.isIn(BlockTags.WALLS))
            {
                blockstate1 = this.func_237071_c_(random);
            }
            else if (blockstate.isIn(Blocks.OBSIDIAN))
            {
                blockstate1 = this.func_237072_d_(random);
            }
        }
        else
        {
            blockstate1 = this.func_237065_a_(random);
        }

        return blockstate1 != null ? new Template.BlockInfo(blockpos, blockstate1, p_230386_5_.nbt) : p_230386_5_;
    }

    @Nullable
    private BlockState func_237065_a_(Random p_237065_1_)
    {
        if (p_237065_1_.nextFloat() >= 0.5F)
        {
            return null;
        }
        else
        {
            BlockState[] ablockstate = new BlockState[] {Blocks.CRACKED_STONE_BRICKS.getDefaultState(), func_237066_a_(p_237065_1_, Blocks.STONE_BRICK_STAIRS)};
            BlockState[] ablockstate1 = new BlockState[] {Blocks.MOSSY_STONE_BRICKS.getDefaultState(), func_237066_a_(p_237065_1_, Blocks.MOSSY_STONE_BRICK_STAIRS)};
            return this.func_237069_a_(p_237065_1_, ablockstate, ablockstate1);
        }
    }

    @Nullable
    private BlockState func_237067_a_(Random p_237067_1_, BlockState p_237067_2_)
    {
        Direction direction = p_237067_2_.get(StairsBlock.FACING);
        Half half = p_237067_2_.get(StairsBlock.HALF);

        if (p_237067_1_.nextFloat() >= 0.5F)
        {
            return null;
        }
        else
        {
            BlockState[] ablockstate = new BlockState[] {Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_BRICK_SLAB.getDefaultState()};
            BlockState[] ablockstate1 = new BlockState[] {Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, direction).with(StairsBlock.HALF, half), Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState()};
            return this.func_237069_a_(p_237067_1_, ablockstate, ablockstate1);
        }
    }

    @Nullable
    private BlockState func_237070_b_(Random p_237070_1_)
    {
        return p_237070_1_.nextFloat() < this.field_237063_b_ ? Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState() : null;
    }

    @Nullable
    private BlockState func_237071_c_(Random p_237071_1_)
    {
        return p_237071_1_.nextFloat() < this.field_237063_b_ ? Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState() : null;
    }

    @Nullable
    private BlockState func_237072_d_(Random p_237072_1_)
    {
        return p_237072_1_.nextFloat() < 0.15F ? Blocks.CRYING_OBSIDIAN.getDefaultState() : null;
    }

    private static BlockState func_237066_a_(Random p_237066_0_, Block p_237066_1_)
    {
        return p_237066_1_.getDefaultState().with(StairsBlock.FACING, Direction.Plane.HORIZONTAL.random(p_237066_0_)).with(StairsBlock.HALF, Half.values()[p_237066_0_.nextInt(Half.values().length)]);
    }

    private BlockState func_237069_a_(Random p_237069_1_, BlockState[] p_237069_2_, BlockState[] p_237069_3_)
    {
        return p_237069_1_.nextFloat() < this.field_237063_b_ ? func_237068_a_(p_237069_1_, p_237069_3_) : func_237068_a_(p_237069_1_, p_237069_2_);
    }

    private static BlockState func_237068_a_(Random p_237068_0_, BlockState[] p_237068_1_)
    {
        return p_237068_1_[p_237068_0_.nextInt(p_237068_1_.length)];
    }

    protected IStructureProcessorType<?> getType()
    {
        return IStructureProcessorType.field_237135_g_;
    }
}
