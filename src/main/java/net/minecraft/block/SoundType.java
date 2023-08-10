package net.minecraft.block;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class SoundType
{
    public static final SoundType WOOD = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WOOD_BREAK, SoundEvents.BLOCK_WOOD_STEP, SoundEvents.BLOCK_WOOD_PLACE, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_WOOD_FALL);
    public static final SoundType GROUND = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GRAVEL_BREAK, SoundEvents.BLOCK_GRAVEL_STEP, SoundEvents.BLOCK_GRAVEL_PLACE, SoundEvents.BLOCK_GRAVEL_HIT, SoundEvents.BLOCK_GRAVEL_FALL);
    public static final SoundType PLANT = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GRASS_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.BLOCK_GRASS_PLACE, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL);
    public static final SoundType LILY_PADS = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GRASS_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL);
    public static final SoundType STONE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);
    public static final SoundType METAL = new SoundType(1.0F, 1.5F, SoundEvents.BLOCK_METAL_BREAK, SoundEvents.BLOCK_METAL_STEP, SoundEvents.BLOCK_METAL_PLACE, SoundEvents.BLOCK_METAL_HIT, SoundEvents.BLOCK_METAL_FALL);
    public static final SoundType GLASS = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GLASS_BREAK, SoundEvents.BLOCK_GLASS_STEP, SoundEvents.BLOCK_GLASS_PLACE, SoundEvents.BLOCK_GLASS_HIT, SoundEvents.BLOCK_GLASS_FALL);
    public static final SoundType CLOTH = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WOOL_BREAK, SoundEvents.BLOCK_WOOL_STEP, SoundEvents.BLOCK_WOOL_PLACE, SoundEvents.BLOCK_WOOL_HIT, SoundEvents.BLOCK_WOOL_FALL);
    public static final SoundType SAND = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SAND_BREAK, SoundEvents.BLOCK_SAND_STEP, SoundEvents.BLOCK_SAND_PLACE, SoundEvents.BLOCK_SAND_HIT, SoundEvents.BLOCK_SAND_FALL);
    public static final SoundType SNOW = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SNOW_BREAK, SoundEvents.BLOCK_SNOW_STEP, SoundEvents.BLOCK_SNOW_PLACE, SoundEvents.BLOCK_SNOW_HIT, SoundEvents.BLOCK_SNOW_FALL);
    public static final SoundType LADDER = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_LADDER_BREAK, SoundEvents.BLOCK_LADDER_STEP, SoundEvents.BLOCK_LADDER_PLACE, SoundEvents.BLOCK_LADDER_HIT, SoundEvents.BLOCK_LADDER_FALL);
    public static final SoundType ANVIL = new SoundType(0.3F, 1.0F, SoundEvents.BLOCK_ANVIL_BREAK, SoundEvents.BLOCK_ANVIL_STEP, SoundEvents.BLOCK_ANVIL_PLACE, SoundEvents.BLOCK_ANVIL_HIT, SoundEvents.BLOCK_ANVIL_FALL);
    public static final SoundType SLIME = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundEvents.BLOCK_SLIME_BLOCK_STEP, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundEvents.BLOCK_SLIME_BLOCK_HIT, SoundEvents.BLOCK_SLIME_BLOCK_FALL);
    public static final SoundType HONEY = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundEvents.BLOCK_HONEY_BLOCK_HIT, SoundEvents.BLOCK_HONEY_BLOCK_FALL);
    public static final SoundType WET_GRASS = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WET_GRASS_BREAK, SoundEvents.BLOCK_WET_GRASS_STEP, SoundEvents.BLOCK_WET_GRASS_PLACE, SoundEvents.BLOCK_WET_GRASS_HIT, SoundEvents.BLOCK_WET_GRASS_FALL);
    public static final SoundType CORAL = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_CORAL_BLOCK_BREAK, SoundEvents.BLOCK_CORAL_BLOCK_STEP, SoundEvents.BLOCK_CORAL_BLOCK_PLACE, SoundEvents.BLOCK_CORAL_BLOCK_HIT, SoundEvents.BLOCK_CORAL_BLOCK_FALL);
    public static final SoundType BAMBOO = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_BAMBOO_BREAK, SoundEvents.BLOCK_BAMBOO_STEP, SoundEvents.BLOCK_BAMBOO_PLACE, SoundEvents.BLOCK_BAMBOO_HIT, SoundEvents.BLOCK_BAMBOO_FALL);
    public static final SoundType BAMBOO_SAPLING = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_BAMBOO_SAPLING_BREAK, SoundEvents.BLOCK_BAMBOO_STEP, SoundEvents.BLOCK_BAMBOO_SAPLING_PLACE, SoundEvents.BLOCK_BAMBOO_SAPLING_HIT, SoundEvents.BLOCK_BAMBOO_FALL);
    public static final SoundType SCAFFOLDING = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SCAFFOLDING_BREAK, SoundEvents.BLOCK_SCAFFOLDING_STEP, SoundEvents.BLOCK_SCAFFOLDING_PLACE, SoundEvents.BLOCK_SCAFFOLDING_HIT, SoundEvents.BLOCK_SCAFFOLDING_FALL);
    public static final SoundType SWEET_BERRY_BUSH = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SWEET_BERRY_BUSH_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PLACE, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL);
    public static final SoundType CROP = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_CROP_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.ITEM_CROP_PLANT, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL);
    public static final SoundType STEM = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WOOD_BREAK, SoundEvents.BLOCK_WOOD_STEP, SoundEvents.ITEM_CROP_PLANT, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_WOOD_FALL);
    public static final SoundType VINE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GRASS_BREAK, SoundEvents.BLOCK_VINE_STEP, SoundEvents.BLOCK_GRASS_PLACE, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL);
    public static final SoundType NETHER_WART = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NETHER_WART_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.ITEM_NETHER_WART_PLANT, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);
    public static final SoundType LANTERN = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_LANTERN_BREAK, SoundEvents.BLOCK_LANTERN_STEP, SoundEvents.BLOCK_LANTERN_PLACE, SoundEvents.BLOCK_LANTERN_HIT, SoundEvents.BLOCK_LANTERN_FALL);
    public static final SoundType HYPHAE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_STEM_BREAK, SoundEvents.BLOCK_STEM_STEP, SoundEvents.BLOCK_STEM_PLACE, SoundEvents.BLOCK_STEM_HIT, SoundEvents.BLOCK_STEM_FALL);
    public static final SoundType NYLIUM = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NYLIUM_BREAK, SoundEvents.BLOCK_NYLIUM_STEP, SoundEvents.BLOCK_NYLIUM_PLACE, SoundEvents.BLOCK_NYLIUM_HIT, SoundEvents.BLOCK_NYLIUM_FALL);
    public static final SoundType FUNGUS = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_FUNGUS_BREAK, SoundEvents.BLOCK_FUNGUS_STEP, SoundEvents.BLOCK_FUNGUS_PLACE, SoundEvents.BLOCK_FUNGUS_HIT, SoundEvents.BLOCK_FUNGUS_FALL);
    public static final SoundType ROOT = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_ROOTS_BREAK, SoundEvents.BLOCK_ROOTS_STEP, SoundEvents.BLOCK_ROOTS_PLACE, SoundEvents.BLOCK_ROOTS_HIT, SoundEvents.BLOCK_ROOTS_FALL);
    public static final SoundType SHROOMLIGHT = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SHROOMLIGHT_BREAK, SoundEvents.BLOCK_SHROOMLIGHT_STEP, SoundEvents.BLOCK_SHROOMLIGHT_PLACE, SoundEvents.BLOCK_SHROOMLIGHT_HIT, SoundEvents.BLOCK_SHROOMLIGHT_FALL);
    public static final SoundType NETHER_VINE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WEEPING_VINES_BREAK, SoundEvents.BLOCK_WEEPING_VINES_STEP, SoundEvents.BLOCK_WEEPING_VINES_PLACE, SoundEvents.BLOCK_WEEPING_VINES_HIT, SoundEvents.BLOCK_WEEPING_VINES_FALL);
    public static final SoundType NETHER_VINE_LOWER_PITCH = new SoundType(1.0F, 0.5F, SoundEvents.BLOCK_WEEPING_VINES_BREAK, SoundEvents.BLOCK_WEEPING_VINES_STEP, SoundEvents.BLOCK_WEEPING_VINES_PLACE, SoundEvents.BLOCK_WEEPING_VINES_HIT, SoundEvents.BLOCK_WEEPING_VINES_FALL);
    public static final SoundType SOUL_SAND = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SOUL_SAND_BREAK, SoundEvents.BLOCK_SOUL_SAND_STEP, SoundEvents.BLOCK_SOUL_SAND_PLACE, SoundEvents.BLOCK_SOUL_SAND_HIT, SoundEvents.BLOCK_SOUL_SAND_FALL);
    public static final SoundType SOUL_SOIL = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_SOUL_SOIL_BREAK, SoundEvents.BLOCK_SOUL_SOIL_STEP, SoundEvents.BLOCK_SOUL_SOIL_PLACE, SoundEvents.BLOCK_SOUL_SOIL_HIT, SoundEvents.BLOCK_SOUL_SOIL_FALL);
    public static final SoundType BASALT = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_BASALT_BREAK, SoundEvents.BLOCK_BASALT_STEP, SoundEvents.BLOCK_BASALT_PLACE, SoundEvents.BLOCK_BASALT_HIT, SoundEvents.BLOCK_BASALT_FALL);
    public static final SoundType WART = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_WART_BLOCK_BREAK, SoundEvents.BLOCK_WART_BLOCK_STEP, SoundEvents.BLOCK_WART_BLOCK_PLACE, SoundEvents.BLOCK_WART_BLOCK_HIT, SoundEvents.BLOCK_WART_BLOCK_FALL);
    public static final SoundType NETHERRACK = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NETHERRACK_BREAK, SoundEvents.BLOCK_NETHERRACK_STEP, SoundEvents.BLOCK_NETHERRACK_PLACE, SoundEvents.BLOCK_NETHERRACK_HIT, SoundEvents.BLOCK_NETHERRACK_FALL);
    public static final SoundType NETHER_BRICK = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NETHER_BRICKS_BREAK, SoundEvents.BLOCK_NETHER_BRICKS_STEP, SoundEvents.BLOCK_NETHER_BRICKS_PLACE, SoundEvents.BLOCK_NETHER_BRICKS_HIT, SoundEvents.BLOCK_NETHER_BRICKS_FALL);
    public static final SoundType NETHER_SPROUT = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NETHER_SPROUT_BREAK, SoundEvents.BLOCK_NETHER_SPROUT_STEP, SoundEvents.BLOCK_NETHER_SPROUT_PLACE, SoundEvents.BLOCK_NETHER_SPROUT_HIT, SoundEvents.BLOCK_NETHER_SPROUT_FALL);
    public static final SoundType NETHER_ORE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NETHER_ORE_BREAK, SoundEvents.BLOCK_NETHER_ORE_STEP, SoundEvents.BLOCK_NETHER_ORE_PLACE, SoundEvents.BLOCK_NETHER_ORE_HIT, SoundEvents.BLOCK_NETHER_ORE_FALL);
    public static final SoundType BONE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_BONE_BLOCK_BREAK, SoundEvents.BLOCK_BONE_BLOCK_STEP, SoundEvents.BLOCK_BONE_BLOCK_PLACE, SoundEvents.BLOCK_BONE_BLOCK_HIT, SoundEvents.BLOCK_BONE_BLOCK_FALL);
    public static final SoundType NETHERITE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, SoundEvents.BLOCK_NETHERITE_BLOCK_STEP, SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, SoundEvents.BLOCK_NETHERITE_BLOCK_HIT, SoundEvents.BLOCK_NETHERITE_BLOCK_FALL);
    public static final SoundType ANCIENT_DEBRIS = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_ANCIENT_DEBRIS_BREAK, SoundEvents.BLOCK_ANCIENT_DEBRIS_STEP, SoundEvents.BLOCK_ANCIENT_DEBRIS_PLACE, SoundEvents.BLOCK_ANCIENT_DEBRIS_HIT, SoundEvents.BLOCK_ANCIENT_DEBRIS_FALL);
    public static final SoundType LODESTONE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_LODESTONE_BREAK, SoundEvents.BLOCK_LODESTONE_STEP, SoundEvents.BLOCK_LODESTONE_PLACE, SoundEvents.BLOCK_LODESTONE_HIT, SoundEvents.BLOCK_LODESTONE_FALL);
    public static final SoundType CHAIN = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_CHAIN_BREAK, SoundEvents.BLOCK_CHAIN_STEP, SoundEvents.BLOCK_CHAIN_PLACE, SoundEvents.BLOCK_CHAIN_HIT, SoundEvents.BLOCK_CHAIN_FALL);
    public static final SoundType NETHER_GOLD = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_NETHER_GOLD_ORE_BREAK, SoundEvents.BLOCK_NETHER_GOLD_ORE_STEP, SoundEvents.BLOCK_NETHER_GOLD_ORE_PLACE, SoundEvents.BLOCK_NETHER_GOLD_ORE_HIT, SoundEvents.BLOCK_NETHER_GOLD_ORE_FALL);
    public static final SoundType GILDED_BLACKSTONE = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK, SoundEvents.BLOCK_GILDED_BLACKSTONE_STEP, SoundEvents.BLOCK_GILDED_BLACKSTONE_PLACE, SoundEvents.BLOCK_GILDED_BLACKSTONE_HIT, SoundEvents.BLOCK_GILDED_BLACKSTONE_FALL);
    public final float volume;
    public final float pitch;
    private final SoundEvent breakSound;
    private final SoundEvent stepSound;
    private final SoundEvent placeSound;
    private final SoundEvent hitSound;
    private final SoundEvent fallSound;

    public SoundType(float volumeIn, float pitchIn, SoundEvent breakSoundIn, SoundEvent stepSoundIn, SoundEvent placeSoundIn, SoundEvent hitSoundIn, SoundEvent fallSoundIn)
    {
        this.volume = volumeIn;
        this.pitch = pitchIn;
        this.breakSound = breakSoundIn;
        this.stepSound = stepSoundIn;
        this.placeSound = placeSoundIn;
        this.hitSound = hitSoundIn;
        this.fallSound = fallSoundIn;
    }

    public float getVolume()
    {
        return this.volume;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public SoundEvent getBreakSound()
    {
        return this.breakSound;
    }

    public SoundEvent getStepSound()
    {
        return this.stepSound;
    }

    public SoundEvent getPlaceSound()
    {
        return this.placeSound;
    }

    public SoundEvent getHitSound()
    {
        return this.hitSound;
    }

    public SoundEvent getFallSound()
    {
        return this.fallSound;
    }
}
