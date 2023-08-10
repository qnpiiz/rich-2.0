package net.minecraft.data;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.util.ResourceLocation;

public class StockModelShapes
{
    public static final ModelsUtil CUBE = makeBlockModel("cube", StockTextureAliases.PARTICLE, StockTextureAliases.NORTH, StockTextureAliases.SOUTH, StockTextureAliases.EAST, StockTextureAliases.WEST, StockTextureAliases.UP, StockTextureAliases.DOWN);
    public static final ModelsUtil CUBE_DIRECTIONAL = makeBlockModel("cube_directional", StockTextureAliases.PARTICLE, StockTextureAliases.NORTH, StockTextureAliases.SOUTH, StockTextureAliases.EAST, StockTextureAliases.WEST, StockTextureAliases.UP, StockTextureAliases.DOWN);
    public static final ModelsUtil CUBE_ALL = makeBlockModel("cube_all", StockTextureAliases.ALL);
    public static final ModelsUtil CUBE_MIRRORED_ALL = makeBlockModel("cube_mirrored_all", "_mirrored", StockTextureAliases.ALL);
    public static final ModelsUtil CUBE_COLUMN = makeBlockModel("cube_column", StockTextureAliases.END, StockTextureAliases.SIDE);
    public static final ModelsUtil CUBE_COLUMN_HORIZONTAL = makeBlockModel("cube_column_horizontal", "_horizontal", StockTextureAliases.END, StockTextureAliases.SIDE);
    public static final ModelsUtil CUBE_TOP = makeBlockModel("cube_top", StockTextureAliases.TOP, StockTextureAliases.SIDE);
    public static final ModelsUtil CUBE_BOTTOM_TOP = makeBlockModel("cube_bottom_top", StockTextureAliases.TOP, StockTextureAliases.BOTTOM, StockTextureAliases.SIDE);
    public static final ModelsUtil ORIENTABLE = makeBlockModel("orientable", StockTextureAliases.TOP, StockTextureAliases.FRONT, StockTextureAliases.SIDE);
    public static final ModelsUtil ORIENTABLE_WITH_BOTTOM = makeBlockModel("orientable_with_bottom", StockTextureAliases.TOP, StockTextureAliases.BOTTOM, StockTextureAliases.SIDE, StockTextureAliases.FRONT);
    public static final ModelsUtil ORIENTABLE_VERTICAL = makeBlockModel("orientable_vertical", "_vertical", StockTextureAliases.FRONT, StockTextureAliases.SIDE);
    public static final ModelsUtil BUTTON = makeBlockModel("button", StockTextureAliases.TEXTURE);
    public static final ModelsUtil BUTTON_PRESSED = makeBlockModel("button_pressed", "_pressed", StockTextureAliases.TEXTURE);
    public static final ModelsUtil BUTTON_INVENTORY = makeBlockModel("button_inventory", "_inventory", StockTextureAliases.TEXTURE);
    public static final ModelsUtil DOOR_BOTTOM = makeBlockModel("door_bottom", "_bottom", StockTextureAliases.TOP, StockTextureAliases.BOTTOM);
    public static final ModelsUtil DOOR_BOTTOM_RH = makeBlockModel("door_bottom_rh", "_bottom_hinge", StockTextureAliases.TOP, StockTextureAliases.BOTTOM);
    public static final ModelsUtil DOOR_TOP = makeBlockModel("door_top", "_top", StockTextureAliases.TOP, StockTextureAliases.BOTTOM);
    public static final ModelsUtil DOOR_TOP_RH = makeBlockModel("door_top_rh", "_top_hinge", StockTextureAliases.TOP, StockTextureAliases.BOTTOM);
    public static final ModelsUtil FENCE_POST = makeBlockModel("fence_post", "_post", StockTextureAliases.TEXTURE);
    public static final ModelsUtil FENCE_SIDE = makeBlockModel("fence_side", "_side", StockTextureAliases.TEXTURE);
    public static final ModelsUtil FENCE_INVENTORY = makeBlockModel("fence_inventory", "_inventory", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_WALL_POST = makeBlockModel("template_wall_post", "_post", StockTextureAliases.WALL);
    public static final ModelsUtil TEMPLATE_WALL_SIDE = makeBlockModel("template_wall_side", "_side", StockTextureAliases.WALL);
    public static final ModelsUtil TEMPLATE_WALL_SIDE_TALL = makeBlockModel("template_wall_side_tall", "_side_tall", StockTextureAliases.WALL);
    public static final ModelsUtil WALL_INVENTORY = makeBlockModel("wall_inventory", "_inventory", StockTextureAliases.WALL);
    public static final ModelsUtil TEMPLATE_FENCE_GATE = makeBlockModel("template_fence_gate", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_FENCE_GATE_OPEN = makeBlockModel("template_fence_gate_open", "_open", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_FENCE_GATE_WALL = makeBlockModel("template_fence_gate_wall", "_wall", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_FENCE_GATE_WALL_OPEN = makeBlockModel("template_fence_gate_wall_open", "_wall_open", StockTextureAliases.TEXTURE);
    public static final ModelsUtil PRESSURE_PLATE_UP = makeBlockModel("pressure_plate_up", StockTextureAliases.TEXTURE);
    public static final ModelsUtil PRESSURE_PLATE_DOWN = makeBlockModel("pressure_plate_down", "_down", StockTextureAliases.TEXTURE);
    public static final ModelsUtil PARTICLE = makeEmptyModel(StockTextureAliases.PARTICLE);
    public static final ModelsUtil SLAB = makeBlockModel("slab", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
    public static final ModelsUtil SLAB_TOP = makeBlockModel("slab_top", "_top", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
    public static final ModelsUtil LEAVES = makeBlockModel("leaves", StockTextureAliases.ALL);
    public static final ModelsUtil STAIRS = makeBlockModel("stairs", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
    public static final ModelsUtil INNER_STAIRS = makeBlockModel("inner_stairs", "_inner", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
    public static final ModelsUtil OUTER_STAIRS = makeBlockModel("outer_stairs", "_outer", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
    public static final ModelsUtil TEMPLATE_TRAPDOOR_TOP = makeBlockModel("template_trapdoor_top", "_top", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_TRAPDOOR_BOTTOM = makeBlockModel("template_trapdoor_bottom", "_bottom", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_TRAPDOOR_OPEN = makeBlockModel("template_trapdoor_open", "_open", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_ORIENTABLE_TRAPDOOR_TOP = makeBlockModel("template_orientable_trapdoor_top", "_top", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM = makeBlockModel("template_orientable_trapdoor_bottom", "_bottom", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN = makeBlockModel("template_orientable_trapdoor_open", "_open", StockTextureAliases.TEXTURE);
    public static final ModelsUtil CROSS = makeBlockModel("cross", StockTextureAliases.CROSS);
    public static final ModelsUtil TINTED_CROSS = makeBlockModel("tinted_cross", StockTextureAliases.CROSS);
    public static final ModelsUtil FLOWER_POT_CROSS = makeBlockModel("flower_pot_cross", StockTextureAliases.PLANT);
    public static final ModelsUtil TINTED_FLOWER_POT_CROSS = makeBlockModel("tinted_flower_pot_cross", StockTextureAliases.PLANT);
    public static final ModelsUtil RAIL_FLAT = makeBlockModel("rail_flat", StockTextureAliases.RAIL);
    public static final ModelsUtil RAIL_CURVED = makeBlockModel("rail_curved", "_corner", StockTextureAliases.RAIL);
    public static final ModelsUtil TEMPLATE_RAIL_RAISED_NE = makeBlockModel("template_rail_raised_ne", "_raised_ne", StockTextureAliases.RAIL);
    public static final ModelsUtil TEMPLATE_RAIL_RAISED_SW = makeBlockModel("template_rail_raised_sw", "_raised_sw", StockTextureAliases.RAIL);
    public static final ModelsUtil CARPET = makeBlockModel("carpet", StockTextureAliases.WOOL);
    public static final ModelsUtil CORAL_FAN = makeBlockModel("coral_fan", StockTextureAliases.FAN);
    public static final ModelsUtil CORAL_WALL_FAN = makeBlockModel("coral_wall_fan", StockTextureAliases.FAN);
    public static final ModelsUtil TEMPLATE_GLAZED_TERRACOTTA = makeBlockModel("template_glazed_terracotta", StockTextureAliases.PATTERN);
    public static final ModelsUtil TEMPLATE_CHORUS_FLOWER = makeBlockModel("template_chorus_flower", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_DAYLIGHT_SENSOR = makeBlockModel("template_daylight_detector", StockTextureAliases.TOP, StockTextureAliases.SIDE);
    public static final ModelsUtil TEMPLATE_GLASS_PANE_NOSIDE = makeBlockModel("template_glass_pane_noside", "_noside", StockTextureAliases.PANE);
    public static final ModelsUtil TEMPLATE_GLASS_PANE_NOSIDE_ALT = makeBlockModel("template_glass_pane_noside_alt", "_noside_alt", StockTextureAliases.PANE);
    public static final ModelsUtil TEMPLATE_GLASS_PANE_POST = makeBlockModel("template_glass_pane_post", "_post", StockTextureAliases.PANE, StockTextureAliases.EDGE);
    public static final ModelsUtil TEMPLATE_GLASS_PANE_SIDE = makeBlockModel("template_glass_pane_side", "_side", StockTextureAliases.PANE, StockTextureAliases.EDGE);
    public static final ModelsUtil TEMPLATE_GLASS_PANE_SIDE_ALT = makeBlockModel("template_glass_pane_side_alt", "_side_alt", StockTextureAliases.PANE, StockTextureAliases.EDGE);
    public static final ModelsUtil TEMPLATE_COMMAND_BLOCK = makeBlockModel("template_command_block", StockTextureAliases.FRONT, StockTextureAliases.BACK, StockTextureAliases.SIDE);
    public static final ModelsUtil TEMPLATE_ANVIL = makeBlockModel("template_anvil", StockTextureAliases.TOP);
    public static final ModelsUtil[] STEM_GROWTH_STAGES = IntStream.range(0, 8).mapToObj((growthStage) ->
    {
        return makeBlockModel("stem_growth" + growthStage, "_stage" + growthStage, StockTextureAliases.STEM);
    }).toArray((growthStages) ->
    {
        return new ModelsUtil[growthStages];
    });
    public static final ModelsUtil STEM_FRUIT = makeBlockModel("stem_fruit", StockTextureAliases.STEM, StockTextureAliases.UPPERSTEM);
    public static final ModelsUtil CROP = makeBlockModel("crop", StockTextureAliases.CROP);
    public static final ModelsUtil TEMPLATE_FARMLAND = makeBlockModel("template_farmland", StockTextureAliases.DIRT, StockTextureAliases.TOP);
    public static final ModelsUtil TEMPLATE_FIRE_FLOOR = makeBlockModel("template_fire_floor", StockTextureAliases.FIRE);
    public static final ModelsUtil TEMPLATE_FIRE_SIDE = makeBlockModel("template_fire_side", StockTextureAliases.FIRE);
    public static final ModelsUtil TEMPLATE_FIRE_SIDE_ALT = makeBlockModel("template_fire_side_alt", StockTextureAliases.FIRE);
    public static final ModelsUtil TEMPLATE_FIRE_UP = makeBlockModel("template_fire_up", StockTextureAliases.FIRE);
    public static final ModelsUtil TEMPLATE_FIRE_UP_ALT = makeBlockModel("template_fire_up_alt", StockTextureAliases.FIRE);
    public static final ModelsUtil TEMPLATE_CAMPFIRE = makeBlockModel("template_campfire", StockTextureAliases.FIRE, StockTextureAliases.LIT_LOG);
    public static final ModelsUtil TEMPLATE_LANTERN = makeBlockModel("template_lantern", StockTextureAliases.LANTERN);
    public static final ModelsUtil TEMPLATE_HANGING_LANTERN = makeBlockModel("template_hanging_lantern", "_hanging", StockTextureAliases.LANTERN);
    public static final ModelsUtil TEMPLATE_TORCH = makeBlockModel("template_torch", StockTextureAliases.TORCH);
    public static final ModelsUtil TEMPLATE_TORCH_WALL = makeBlockModel("template_torch_wall", StockTextureAliases.TORCH);
    public static final ModelsUtil TEMPLATE_PISTON = makeBlockModel("template_piston", StockTextureAliases.PLATFORM, StockTextureAliases.BOTTOM, StockTextureAliases.SIDE);
    public static final ModelsUtil TEMPLATE_PISTON_HEAD = makeBlockModel("template_piston_head", StockTextureAliases.PLATFORM, StockTextureAliases.SIDE, StockTextureAliases.UNSTICKY);
    public static final ModelsUtil TEMPLATE_PISTON_HEAD_SHORT = makeBlockModel("template_piston_head_short", StockTextureAliases.PLATFORM, StockTextureAliases.SIDE, StockTextureAliases.UNSTICKY);
    public static final ModelsUtil TEMPLATE_SEAGRASS = makeBlockModel("template_seagrass", StockTextureAliases.TEXTURE);
    public static final ModelsUtil TEMPLATE_TURTLE_EGG = makeBlockModel("template_turtle_egg", StockTextureAliases.ALL);
    public static final ModelsUtil TEMPLATE_TWO_TURTLE_EGGS = makeBlockModel("template_two_turtle_eggs", StockTextureAliases.ALL);
    public static final ModelsUtil TEMPLATE_THREE_TURTLE_EGGS = makeBlockModel("template_three_turtle_eggs", StockTextureAliases.ALL);
    public static final ModelsUtil TEMPLATE_FOUR_TURTLE_EGGS = makeBlockModel("template_four_turtle_eggs", StockTextureAliases.ALL);
    public static final ModelsUtil TEMPLATE_SINGLE_FACE = makeBlockModel("template_single_face", StockTextureAliases.TEXTURE);
    public static final ModelsUtil GENERATED = makeItemModel("generated", StockTextureAliases.LAYER_ZERO);
    public static final ModelsUtil HANDHELD = makeItemModel("handheld", StockTextureAliases.LAYER_ZERO);
    public static final ModelsUtil HANDHELD_ROD = makeItemModel("handheld_rod", StockTextureAliases.LAYER_ZERO);
    public static final ModelsUtil TEMPLATE_SHULKER_BOX = makeItemModel("template_shulker_box", StockTextureAliases.PARTICLE);
    public static final ModelsUtil TEMPLATE_BED = makeItemModel("template_bed", StockTextureAliases.PARTICLE);
    public static final ModelsUtil TEMPLATE_BANNER = makeItemModel("template_banner");
    public static final ModelsUtil TEMPLATE_SKULL = makeItemModel("template_skull");

    private static ModelsUtil makeEmptyModel(StockTextureAliases... textureAliases)
    {
        return new ModelsUtil(Optional.empty(), Optional.empty(), textureAliases);
    }

    private static ModelsUtil makeBlockModel(String name, StockTextureAliases... textureAliases)
    {
        return new ModelsUtil(Optional.of(new ResourceLocation("minecraft", "block/" + name)), Optional.empty(), textureAliases);
    }

    private static ModelsUtil makeItemModel(String name, StockTextureAliases... textureAliases)
    {
        return new ModelsUtil(Optional.of(new ResourceLocation("minecraft", "item/" + name)), Optional.empty(), textureAliases);
    }

    private static ModelsUtil makeBlockModel(String name, String append, StockTextureAliases... textureAliases)
    {
        return new ModelsUtil(Optional.of(new ResourceLocation("minecraft", "block/" + name)), Optional.of(append), textureAliases);
    }
}
