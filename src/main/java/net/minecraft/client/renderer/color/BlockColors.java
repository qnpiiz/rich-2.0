package net.minecraft.client.renderer.color;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.Property;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;

public class BlockColors
{
    private final ObjectIntIdentityMap<IBlockColor> colors = new ObjectIntIdentityMap<>(32);
    private final Map < Block, Set < Property<? >>> colorStates = Maps.newHashMap();

    public static BlockColors init()
    {
        BlockColors blockcolors = new BlockColors();
        blockcolors.register((state, reader, pos, color) ->
        {
            return reader != null && pos != null ? BiomeColors.getGrassColor(reader, state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos) : -1;
        }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        blockcolors.addColorState(DoublePlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        blockcolors.register((state, reader, pos, color) ->
        {
            return reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : GrassColors.get(0.5D, 1.0D);
        }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
        blockcolors.register((state, reader, pos, color) ->
        {
            return FoliageColors.getSpruce();
        }, Blocks.SPRUCE_LEAVES);
        blockcolors.register((state, reader, pos, color) ->
        {
            return FoliageColors.getBirch();
        }, Blocks.BIRCH_LEAVES);
        blockcolors.register((state, reader, pos, color) ->
        {
            return reader != null && pos != null ? BiomeColors.getFoliageColor(reader, pos) : FoliageColors.getDefault();
        }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
        blockcolors.register((state, reader, pos, color) ->
        {
            return reader != null && pos != null ? BiomeColors.getWaterColor(reader, pos) : -1;
        }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
        blockcolors.register((state, reader, pos, color) ->
        {
            return RedstoneWireBlock.getRGBByPower(state.get(RedstoneWireBlock.POWER));
        }, Blocks.REDSTONE_WIRE);
        blockcolors.addColorState(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
        blockcolors.register((state, reader, pos, color) ->
        {
            return reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : -1;
        }, Blocks.SUGAR_CANE);
        blockcolors.register((state, reader, pos, color) ->
        {
            return 14731036;
        }, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        blockcolors.register((state, reader, pos, color) ->
        {
            int i = state.get(StemBlock.AGE);
            int j = i * 32;
            int k = 255 - i * 8;
            int l = i * 4;
            return j << 16 | k << 8 | l;
        }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        blockcolors.addColorState(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        blockcolors.register((state, reader, pos, color) ->
        {
            return reader != null && pos != null ? 2129968 : 7455580;
        }, Blocks.LILY_PAD);
        return blockcolors;
    }

    public int getColorOrMaterialColor(BlockState state, World worldIn, BlockPos blockPosIn)
    {
        IBlockColor iblockcolor = this.colors.getByValue(Registry.BLOCK.getId(state.getBlock()));

        if (iblockcolor != null)
        {
            return iblockcolor.getColor(state, (IBlockDisplayReader)null, (BlockPos)null, 0);
        }
        else
        {
            MaterialColor materialcolor = state.getMaterialColor(worldIn, blockPosIn);
            return materialcolor != null ? materialcolor.colorValue : -1;
        }
    }

    public int getColor(BlockState blockStateIn, @Nullable IBlockDisplayReader lightReaderIn, @Nullable BlockPos blockPosIn, int tintIndexIn)
    {
        IBlockColor iblockcolor = this.colors.getByValue(Registry.BLOCK.getId(blockStateIn.getBlock()));
        return iblockcolor == null ? -1 : iblockcolor.getColor(blockStateIn, lightReaderIn, blockPosIn, tintIndexIn);
    }

    public void register(IBlockColor blockColor, Block... blocksIn)
    {
        for (Block block : blocksIn)
        {
            this.colors.put(blockColor, Registry.BLOCK.getId(block));
        }
    }

    private void addColorStates(Set < Property<? >> propertiesIn, Block... blocksIn)
    {
        for (Block block : blocksIn)
        {
            this.colorStates.put(block, propertiesIn);
        }
    }

    private void addColorState(Property<?> propertyIn, Block... blocksIn)
    {
        this.addColorStates(ImmutableSet.of(propertyIn), blocksIn);
    }

    public Set < Property<? >> getColorProperties(Block blockIn)
    {
        return this.colorStates.getOrDefault(blockIn, ImmutableSet.of());
    }
}
