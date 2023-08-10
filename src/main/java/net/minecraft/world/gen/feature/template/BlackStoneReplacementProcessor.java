package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlackStoneReplacementProcessor extends StructureProcessor
{
    public static final Codec<BlackStoneReplacementProcessor> field_237057_a_;
    public static final BlackStoneReplacementProcessor field_237058_b_ = new BlackStoneReplacementProcessor();
    private final Map<Block, Block> field_237059_c_ = Util.make(Maps.newHashMap(), (p_237060_0_) ->
    {
        p_237060_0_.put(Blocks.COBBLESTONE, Blocks.BLACKSTONE);
        p_237060_0_.put(Blocks.MOSSY_COBBLESTONE, Blocks.BLACKSTONE);
        p_237060_0_.put(Blocks.STONE, Blocks.POLISHED_BLACKSTONE);
        p_237060_0_.put(Blocks.STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        p_237060_0_.put(Blocks.MOSSY_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        p_237060_0_.put(Blocks.COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
        p_237060_0_.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
        p_237060_0_.put(Blocks.STONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
        p_237060_0_.put(Blocks.STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        p_237060_0_.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        p_237060_0_.put(Blocks.COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
        p_237060_0_.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
        p_237060_0_.put(Blocks.SMOOTH_STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        p_237060_0_.put(Blocks.STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        p_237060_0_.put(Blocks.STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        p_237060_0_.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        p_237060_0_.put(Blocks.STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        p_237060_0_.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        p_237060_0_.put(Blocks.COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
        p_237060_0_.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
        p_237060_0_.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE);
        p_237060_0_.put(Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        p_237060_0_.put(Blocks.IRON_BARS, Blocks.CHAIN);
    });

    private BlackStoneReplacementProcessor()
    {
    }

    public Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_)
    {
        Block block = this.field_237059_c_.get(p_230386_5_.state.getBlock());

        if (block == null)
        {
            return p_230386_5_;
        }
        else
        {
            BlockState blockstate = p_230386_5_.state;
            BlockState blockstate1 = block.getDefaultState();

            if (blockstate.hasProperty(StairsBlock.FACING))
            {
                blockstate1 = blockstate1.with(StairsBlock.FACING, blockstate.get(StairsBlock.FACING));
            }

            if (blockstate.hasProperty(StairsBlock.HALF))
            {
                blockstate1 = blockstate1.with(StairsBlock.HALF, blockstate.get(StairsBlock.HALF));
            }

            if (blockstate.hasProperty(SlabBlock.TYPE))
            {
                blockstate1 = blockstate1.with(SlabBlock.TYPE, blockstate.get(SlabBlock.TYPE));
            }

            return new Template.BlockInfo(p_230386_5_.pos, blockstate1, p_230386_5_.nbt);
        }
    }

    protected IStructureProcessorType<?> getType()
    {
        return IStructureProcessorType.field_237136_h_;
    }

    static
    {
        field_237057_a_ = Codec.unit(() ->
        {
            return field_237058_b_;
        });
    }
}
