package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

public class RenderTypeLookup
{
    private static final Map<Block, RenderType> TYPES_BY_BLOCK = Util.make(Maps.newHashMap(), (p_228395_0_) ->
    {
        RenderType rendertype = RenderType.getTripwire();
        p_228395_0_.put(Blocks.TRIPWIRE, rendertype);
        RenderType rendertype1 = RenderType.getCutoutMipped();
        p_228395_0_.put(Blocks.GRASS_BLOCK, rendertype1);
        p_228395_0_.put(Blocks.IRON_BARS, rendertype1);
        p_228395_0_.put(Blocks.GLASS_PANE, rendertype1);
        p_228395_0_.put(Blocks.TRIPWIRE_HOOK, rendertype1);
        p_228395_0_.put(Blocks.HOPPER, rendertype1);
        p_228395_0_.put(Blocks.CHAIN, rendertype1);
        p_228395_0_.put(Blocks.JUNGLE_LEAVES, rendertype1);
        p_228395_0_.put(Blocks.OAK_LEAVES, rendertype1);
        p_228395_0_.put(Blocks.SPRUCE_LEAVES, rendertype1);
        p_228395_0_.put(Blocks.ACACIA_LEAVES, rendertype1);
        p_228395_0_.put(Blocks.BIRCH_LEAVES, rendertype1);
        p_228395_0_.put(Blocks.DARK_OAK_LEAVES, rendertype1);
        RenderType rendertype2 = RenderType.getCutout();
        p_228395_0_.put(Blocks.OAK_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.SPRUCE_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.BIRCH_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.JUNGLE_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.ACACIA_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.DARK_OAK_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.GLASS, rendertype2);
        p_228395_0_.put(Blocks.WHITE_BED, rendertype2);
        p_228395_0_.put(Blocks.ORANGE_BED, rendertype2);
        p_228395_0_.put(Blocks.MAGENTA_BED, rendertype2);
        p_228395_0_.put(Blocks.LIGHT_BLUE_BED, rendertype2);
        p_228395_0_.put(Blocks.YELLOW_BED, rendertype2);
        p_228395_0_.put(Blocks.LIME_BED, rendertype2);
        p_228395_0_.put(Blocks.PINK_BED, rendertype2);
        p_228395_0_.put(Blocks.GRAY_BED, rendertype2);
        p_228395_0_.put(Blocks.LIGHT_GRAY_BED, rendertype2);
        p_228395_0_.put(Blocks.CYAN_BED, rendertype2);
        p_228395_0_.put(Blocks.PURPLE_BED, rendertype2);
        p_228395_0_.put(Blocks.BLUE_BED, rendertype2);
        p_228395_0_.put(Blocks.BROWN_BED, rendertype2);
        p_228395_0_.put(Blocks.GREEN_BED, rendertype2);
        p_228395_0_.put(Blocks.RED_BED, rendertype2);
        p_228395_0_.put(Blocks.BLACK_BED, rendertype2);
        p_228395_0_.put(Blocks.POWERED_RAIL, rendertype2);
        p_228395_0_.put(Blocks.DETECTOR_RAIL, rendertype2);
        p_228395_0_.put(Blocks.COBWEB, rendertype2);
        p_228395_0_.put(Blocks.GRASS, rendertype2);
        p_228395_0_.put(Blocks.FERN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_BUSH, rendertype2);
        p_228395_0_.put(Blocks.SEAGRASS, rendertype2);
        p_228395_0_.put(Blocks.TALL_SEAGRASS, rendertype2);
        p_228395_0_.put(Blocks.DANDELION, rendertype2);
        p_228395_0_.put(Blocks.POPPY, rendertype2);
        p_228395_0_.put(Blocks.BLUE_ORCHID, rendertype2);
        p_228395_0_.put(Blocks.ALLIUM, rendertype2);
        p_228395_0_.put(Blocks.AZURE_BLUET, rendertype2);
        p_228395_0_.put(Blocks.RED_TULIP, rendertype2);
        p_228395_0_.put(Blocks.ORANGE_TULIP, rendertype2);
        p_228395_0_.put(Blocks.WHITE_TULIP, rendertype2);
        p_228395_0_.put(Blocks.PINK_TULIP, rendertype2);
        p_228395_0_.put(Blocks.OXEYE_DAISY, rendertype2);
        p_228395_0_.put(Blocks.CORNFLOWER, rendertype2);
        p_228395_0_.put(Blocks.WITHER_ROSE, rendertype2);
        p_228395_0_.put(Blocks.LILY_OF_THE_VALLEY, rendertype2);
        p_228395_0_.put(Blocks.BROWN_MUSHROOM, rendertype2);
        p_228395_0_.put(Blocks.RED_MUSHROOM, rendertype2);
        p_228395_0_.put(Blocks.TORCH, rendertype2);
        p_228395_0_.put(Blocks.WALL_TORCH, rendertype2);
        p_228395_0_.put(Blocks.SOUL_TORCH, rendertype2);
        p_228395_0_.put(Blocks.SOUL_WALL_TORCH, rendertype2);
        p_228395_0_.put(Blocks.FIRE, rendertype2);
        p_228395_0_.put(Blocks.SOUL_FIRE, rendertype2);
        p_228395_0_.put(Blocks.SPAWNER, rendertype2);
        p_228395_0_.put(Blocks.REDSTONE_WIRE, rendertype2);
        p_228395_0_.put(Blocks.WHEAT, rendertype2);
        p_228395_0_.put(Blocks.OAK_DOOR, rendertype2);
        p_228395_0_.put(Blocks.LADDER, rendertype2);
        p_228395_0_.put(Blocks.RAIL, rendertype2);
        p_228395_0_.put(Blocks.IRON_DOOR, rendertype2);
        p_228395_0_.put(Blocks.REDSTONE_TORCH, rendertype2);
        p_228395_0_.put(Blocks.REDSTONE_WALL_TORCH, rendertype2);
        p_228395_0_.put(Blocks.CACTUS, rendertype2);
        p_228395_0_.put(Blocks.SUGAR_CANE, rendertype2);
        p_228395_0_.put(Blocks.REPEATER, rendertype2);
        p_228395_0_.put(Blocks.OAK_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.SPRUCE_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.BIRCH_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.JUNGLE_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.ACACIA_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.DARK_OAK_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.CRIMSON_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.WARPED_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.ATTACHED_PUMPKIN_STEM, rendertype2);
        p_228395_0_.put(Blocks.ATTACHED_MELON_STEM, rendertype2);
        p_228395_0_.put(Blocks.PUMPKIN_STEM, rendertype2);
        p_228395_0_.put(Blocks.MELON_STEM, rendertype2);
        p_228395_0_.put(Blocks.VINE, rendertype2);
        p_228395_0_.put(Blocks.LILY_PAD, rendertype2);
        p_228395_0_.put(Blocks.NETHER_WART, rendertype2);
        p_228395_0_.put(Blocks.BREWING_STAND, rendertype2);
        p_228395_0_.put(Blocks.COCOA, rendertype2);
        p_228395_0_.put(Blocks.BEACON, rendertype2);
        p_228395_0_.put(Blocks.FLOWER_POT, rendertype2);
        p_228395_0_.put(Blocks.POTTED_OAK_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.POTTED_SPRUCE_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.POTTED_BIRCH_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.POTTED_JUNGLE_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.POTTED_ACACIA_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.POTTED_DARK_OAK_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.POTTED_FERN, rendertype2);
        p_228395_0_.put(Blocks.POTTED_DANDELION, rendertype2);
        p_228395_0_.put(Blocks.POTTED_POPPY, rendertype2);
        p_228395_0_.put(Blocks.POTTED_BLUE_ORCHID, rendertype2);
        p_228395_0_.put(Blocks.POTTED_ALLIUM, rendertype2);
        p_228395_0_.put(Blocks.POTTED_AZURE_BLUET, rendertype2);
        p_228395_0_.put(Blocks.POTTED_RED_TULIP, rendertype2);
        p_228395_0_.put(Blocks.POTTED_ORANGE_TULIP, rendertype2);
        p_228395_0_.put(Blocks.POTTED_WHITE_TULIP, rendertype2);
        p_228395_0_.put(Blocks.POTTED_PINK_TULIP, rendertype2);
        p_228395_0_.put(Blocks.POTTED_OXEYE_DAISY, rendertype2);
        p_228395_0_.put(Blocks.POTTED_CORNFLOWER, rendertype2);
        p_228395_0_.put(Blocks.POTTED_LILY_OF_THE_VALLEY, rendertype2);
        p_228395_0_.put(Blocks.POTTED_WITHER_ROSE, rendertype2);
        p_228395_0_.put(Blocks.POTTED_RED_MUSHROOM, rendertype2);
        p_228395_0_.put(Blocks.POTTED_BROWN_MUSHROOM, rendertype2);
        p_228395_0_.put(Blocks.POTTED_DEAD_BUSH, rendertype2);
        p_228395_0_.put(Blocks.POTTED_CACTUS, rendertype2);
        p_228395_0_.put(Blocks.CARROTS, rendertype2);
        p_228395_0_.put(Blocks.POTATOES, rendertype2);
        p_228395_0_.put(Blocks.COMPARATOR, rendertype2);
        p_228395_0_.put(Blocks.ACTIVATOR_RAIL, rendertype2);
        p_228395_0_.put(Blocks.IRON_TRAPDOOR, rendertype2);
        p_228395_0_.put(Blocks.SUNFLOWER, rendertype2);
        p_228395_0_.put(Blocks.LILAC, rendertype2);
        p_228395_0_.put(Blocks.ROSE_BUSH, rendertype2);
        p_228395_0_.put(Blocks.PEONY, rendertype2);
        p_228395_0_.put(Blocks.TALL_GRASS, rendertype2);
        p_228395_0_.put(Blocks.LARGE_FERN, rendertype2);
        p_228395_0_.put(Blocks.SPRUCE_DOOR, rendertype2);
        p_228395_0_.put(Blocks.BIRCH_DOOR, rendertype2);
        p_228395_0_.put(Blocks.JUNGLE_DOOR, rendertype2);
        p_228395_0_.put(Blocks.ACACIA_DOOR, rendertype2);
        p_228395_0_.put(Blocks.DARK_OAK_DOOR, rendertype2);
        p_228395_0_.put(Blocks.END_ROD, rendertype2);
        p_228395_0_.put(Blocks.CHORUS_PLANT, rendertype2);
        p_228395_0_.put(Blocks.CHORUS_FLOWER, rendertype2);
        p_228395_0_.put(Blocks.BEETROOTS, rendertype2);
        p_228395_0_.put(Blocks.KELP, rendertype2);
        p_228395_0_.put(Blocks.KELP_PLANT, rendertype2);
        p_228395_0_.put(Blocks.TURTLE_EGG, rendertype2);
        p_228395_0_.put(Blocks.DEAD_TUBE_CORAL, rendertype2);
        p_228395_0_.put(Blocks.DEAD_BRAIN_CORAL, rendertype2);
        p_228395_0_.put(Blocks.DEAD_BUBBLE_CORAL, rendertype2);
        p_228395_0_.put(Blocks.DEAD_FIRE_CORAL, rendertype2);
        p_228395_0_.put(Blocks.DEAD_HORN_CORAL, rendertype2);
        p_228395_0_.put(Blocks.TUBE_CORAL, rendertype2);
        p_228395_0_.put(Blocks.BRAIN_CORAL, rendertype2);
        p_228395_0_.put(Blocks.BUBBLE_CORAL, rendertype2);
        p_228395_0_.put(Blocks.FIRE_CORAL, rendertype2);
        p_228395_0_.put(Blocks.HORN_CORAL, rendertype2);
        p_228395_0_.put(Blocks.DEAD_TUBE_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_BRAIN_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_BUBBLE_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_FIRE_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_HORN_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.TUBE_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.BRAIN_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.BUBBLE_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.FIRE_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.HORN_CORAL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.TUBE_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.BRAIN_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.BUBBLE_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.FIRE_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.HORN_CORAL_WALL_FAN, rendertype2);
        p_228395_0_.put(Blocks.SEA_PICKLE, rendertype2);
        p_228395_0_.put(Blocks.CONDUIT, rendertype2);
        p_228395_0_.put(Blocks.BAMBOO_SAPLING, rendertype2);
        p_228395_0_.put(Blocks.BAMBOO, rendertype2);
        p_228395_0_.put(Blocks.POTTED_BAMBOO, rendertype2);
        p_228395_0_.put(Blocks.SCAFFOLDING, rendertype2);
        p_228395_0_.put(Blocks.STONECUTTER, rendertype2);
        p_228395_0_.put(Blocks.LANTERN, rendertype2);
        p_228395_0_.put(Blocks.SOUL_LANTERN, rendertype2);
        p_228395_0_.put(Blocks.CAMPFIRE, rendertype2);
        p_228395_0_.put(Blocks.SOUL_CAMPFIRE, rendertype2);
        p_228395_0_.put(Blocks.SWEET_BERRY_BUSH, rendertype2);
        p_228395_0_.put(Blocks.WEEPING_VINES, rendertype2);
        p_228395_0_.put(Blocks.WEEPING_VINES_PLANT, rendertype2);
        p_228395_0_.put(Blocks.TWISTING_VINES, rendertype2);
        p_228395_0_.put(Blocks.TWISTING_VINES_PLANT, rendertype2);
        p_228395_0_.put(Blocks.NETHER_SPROUTS, rendertype2);
        p_228395_0_.put(Blocks.CRIMSON_FUNGUS, rendertype2);
        p_228395_0_.put(Blocks.WARPED_FUNGUS, rendertype2);
        p_228395_0_.put(Blocks.CRIMSON_ROOTS, rendertype2);
        p_228395_0_.put(Blocks.WARPED_ROOTS, rendertype2);
        p_228395_0_.put(Blocks.POTTED_CRIMSON_FUNGUS, rendertype2);
        p_228395_0_.put(Blocks.POTTED_WARPED_FUNGUS, rendertype2);
        p_228395_0_.put(Blocks.POTTED_CRIMSON_ROOTS, rendertype2);
        p_228395_0_.put(Blocks.POTTED_WARPED_ROOTS, rendertype2);
        p_228395_0_.put(Blocks.CRIMSON_DOOR, rendertype2);
        p_228395_0_.put(Blocks.WARPED_DOOR, rendertype2);
        RenderType rendertype3 = RenderType.getTranslucent();
        p_228395_0_.put(Blocks.ICE, rendertype3);
        p_228395_0_.put(Blocks.NETHER_PORTAL, rendertype3);
        p_228395_0_.put(Blocks.WHITE_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.ORANGE_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.MAGENTA_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.LIGHT_BLUE_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.YELLOW_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.LIME_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.PINK_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.GRAY_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.LIGHT_GRAY_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.CYAN_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.PURPLE_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.BLUE_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.BROWN_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.GREEN_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.RED_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.BLACK_STAINED_GLASS, rendertype3);
        p_228395_0_.put(Blocks.WHITE_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.ORANGE_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.MAGENTA_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.YELLOW_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.LIME_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.PINK_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.GRAY_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.CYAN_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.PURPLE_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.BLUE_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.BROWN_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.GREEN_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.RED_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.BLACK_STAINED_GLASS_PANE, rendertype3);
        p_228395_0_.put(Blocks.SLIME_BLOCK, rendertype3);
        p_228395_0_.put(Blocks.HONEY_BLOCK, rendertype3);
        p_228395_0_.put(Blocks.FROSTED_ICE, rendertype3);
        p_228395_0_.put(Blocks.BUBBLE_COLUMN, rendertype3);
    });
    private static final Map<Fluid, RenderType> TYPES_BY_FLUID = Util.make(Maps.newHashMap(), (p_228392_0_) ->
    {
        RenderType rendertype = RenderType.getTranslucent();
        p_228392_0_.put(Fluids.FLOWING_WATER, rendertype);
        p_228392_0_.put(Fluids.WATER, rendertype);
    });
    private static boolean fancyGraphics;

    public static RenderType getChunkRenderType(BlockState blockStateIn)
    {
        Block block = blockStateIn.getBlock();

        if (block instanceof LeavesBlock)
        {
            return fancyGraphics ? RenderType.getCutoutMipped() : RenderType.getSolid();
        }
        else
        {
            RenderType rendertype = TYPES_BY_BLOCK.get(block);
            return rendertype != null ? rendertype : RenderType.getSolid();
        }
    }

    public static RenderType func_239221_b_(BlockState p_239221_0_)
    {
        Block block = p_239221_0_.getBlock();

        if (block instanceof LeavesBlock)
        {
            return fancyGraphics ? RenderType.getCutoutMipped() : RenderType.getSolid();
        }
        else
        {
            RenderType rendertype = TYPES_BY_BLOCK.get(block);

            if (rendertype != null)
            {
                return rendertype == RenderType.getTranslucent() ? RenderType.getTranslucentMovingBlock() : rendertype;
            }
            else
            {
                return RenderType.getSolid();
            }
        }
    }

    public static RenderType func_239220_a_(BlockState p_239220_0_, boolean p_239220_1_)
    {
        RenderType rendertype = getChunkRenderType(p_239220_0_);

        if (rendertype == RenderType.getTranslucent())
        {
            if (!Minecraft.isFabulousGraphicsEnabled())
            {
                return Atlases.getTranslucentCullBlockType();
            }
            else
            {
                return p_239220_1_ ? Atlases.getTranslucentCullBlockType() : Atlases.getItemEntityTranslucentCullType();
            }
        }
        else
        {
            return Atlases.getCutoutBlockType();
        }
    }

    public static RenderType func_239219_a_(ItemStack p_239219_0_, boolean p_239219_1_)
    {
        Item item = p_239219_0_.getItem();

        if (item instanceof BlockItem)
        {
            Block block = ((BlockItem)item).getBlock();
            return func_239220_a_(block.getDefaultState(), p_239219_1_);
        }
        else
        {
            return p_239219_1_ ? Atlases.getTranslucentCullBlockType() : Atlases.getItemEntityTranslucentCullType();
        }
    }

    public static RenderType getRenderType(FluidState fluidStateIn)
    {
        RenderType rendertype = TYPES_BY_FLUID.get(fluidStateIn.getFluid());
        return rendertype != null ? rendertype : RenderType.getSolid();
    }

    public static void setFancyGraphics(boolean fancyIn)
    {
        fancyGraphics = fancyIn;
    }
}
