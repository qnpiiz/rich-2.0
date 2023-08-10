package net.minecraft.data;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

public class ItemModelProvider
{
    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> field_240073_a_;

    public ItemModelProvider(BiConsumer<ResourceLocation, Supplier<JsonElement>> p_i232519_1_)
    {
        this.field_240073_a_ = p_i232519_1_;
    }

    private void func_240076_a_(Item p_240076_1_, ModelsUtil p_240076_2_)
    {
        p_240076_2_.func_240234_a_(ModelsResourceUtil.func_240219_a_(p_240076_1_), ModelTextures.func_240352_b_(p_240076_1_), this.field_240073_a_);
    }

    private void func_240077_a_(Item p_240077_1_, String p_240077_2_, ModelsUtil p_240077_3_)
    {
        p_240077_3_.func_240234_a_(ModelsResourceUtil.func_240220_a_(p_240077_1_, p_240077_2_), ModelTextures.func_240376_j_(ModelTextures.func_240344_a_(p_240077_1_, p_240077_2_)), this.field_240073_a_);
    }

    private void func_240075_a_(Item p_240075_1_, Item p_240075_2_, ModelsUtil p_240075_3_)
    {
        p_240075_3_.func_240234_a_(ModelsResourceUtil.func_240219_a_(p_240075_1_), ModelTextures.func_240352_b_(p_240075_2_), this.field_240073_a_);
    }

    public void func_240074_a_()
    {
        this.func_240076_a_(Items.ACACIA_BOAT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.APPLE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.ARMOR_STAND, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.ARROW, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BAKED_POTATO, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BAMBOO, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.BEEF, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BEETROOT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BEETROOT_SOUP, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BIRCH_BOAT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BLACK_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BLAZE_POWDER, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BLAZE_ROD, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.BLUE_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BONE_MEAL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BOOK, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BOWL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BREAD, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BRICK, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BROWN_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.BUCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CARROT_ON_A_STICK, StockModelShapes.HANDHELD_ROD);
        this.func_240076_a_(Items.WARPED_FUNGUS_ON_A_STICK, StockModelShapes.HANDHELD_ROD);
        this.func_240076_a_(Items.CHAINMAIL_BOOTS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CHAINMAIL_CHESTPLATE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CHAINMAIL_HELMET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CHAINMAIL_LEGGINGS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CHARCOAL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CHEST_MINECART, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CHICKEN, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CHORUS_FRUIT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CLAY_BALL, StockModelShapes.GENERATED);

        for (int i = 1; i < 64; ++i)
        {
            this.func_240077_a_(Items.CLOCK, String.format("_%02d", i), StockModelShapes.GENERATED);
        }

        this.func_240076_a_(Items.COAL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COD_BUCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COMMAND_BLOCK_MINECART, StockModelShapes.GENERATED);

        for (int j = 0; j < 32; ++j)
        {
            if (j != 16)
            {
                this.func_240077_a_(Items.COMPASS, String.format("_%02d", j), StockModelShapes.GENERATED);
            }
        }

        this.func_240076_a_(Items.COOKED_BEEF, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COOKED_CHICKEN, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COOKED_COD, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COOKED_MUTTON, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COOKED_PORKCHOP, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COOKED_RABBIT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COOKED_SALMON, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.COOKIE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CREEPER_BANNER_PATTERN, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.CYAN_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DARK_OAK_BOAT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DIAMOND, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DIAMOND_AXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.DIAMOND_BOOTS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DIAMOND_CHESTPLATE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DIAMOND_HELMET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DIAMOND_HOE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.DIAMOND_HORSE_ARMOR, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DIAMOND_LEGGINGS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DIAMOND_PICKAXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.DIAMOND_SHOVEL, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.DIAMOND_SWORD, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.DRAGON_BREATH, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.DRIED_KELP, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.EGG, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.EMERALD, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.ENCHANTED_BOOK, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.ENDER_EYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.ENDER_PEARL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.END_CRYSTAL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.EXPERIENCE_BOTTLE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.FERMENTED_SPIDER_EYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.FIREWORK_ROCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.FIRE_CHARGE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.FLINT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.FLINT_AND_STEEL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.FLOWER_BANNER_PATTERN, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.FURNACE_MINECART, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GHAST_TEAR, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GLASS_BOTTLE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GLISTERING_MELON_SLICE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GLOBE_BANNER_PATTERN, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GLOWSTONE_DUST, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLDEN_APPLE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLDEN_AXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.GOLDEN_BOOTS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLDEN_CARROT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLDEN_CHESTPLATE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLDEN_HELMET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLDEN_HOE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.GOLDEN_HORSE_ARMOR, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLDEN_LEGGINGS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLDEN_PICKAXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.GOLDEN_SHOVEL, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.GOLDEN_SWORD, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.GOLD_INGOT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GOLD_NUGGET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GRAY_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GREEN_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.GUNPOWDER, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.HEART_OF_THE_SEA, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.HONEYCOMB, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.HONEY_BOTTLE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.HOPPER_MINECART, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.INK_SAC, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.IRON_AXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.IRON_BOOTS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.IRON_CHESTPLATE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.IRON_HELMET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.IRON_HOE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.IRON_HORSE_ARMOR, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.IRON_INGOT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.IRON_LEGGINGS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.IRON_NUGGET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.IRON_PICKAXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.IRON_SHOVEL, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.IRON_SWORD, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.ITEM_FRAME, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.JUNGLE_BOAT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.KNOWLEDGE_BOOK, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.LAPIS_LAZULI, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.LAVA_BUCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.LEATHER, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.LEATHER_HORSE_ARMOR, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.LIGHT_BLUE_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.LIGHT_GRAY_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.LIME_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MAGENTA_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MAGMA_CREAM, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MAP, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MELON_SLICE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MILK_BUCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MINECART, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MOJANG_BANNER_PATTERN, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSHROOM_STEW, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_11, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_13, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_BLOCKS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_CAT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_CHIRP, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_FAR, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_MALL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_MELLOHI, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_PIGSTEP, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_STAL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_STRAD, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_WAIT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUSIC_DISC_WARD, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.MUTTON, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NAME_TAG, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NAUTILUS_SHELL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NETHERITE_AXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.NETHERITE_BOOTS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NETHERITE_CHESTPLATE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NETHERITE_HELMET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NETHERITE_HOE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.NETHERITE_INGOT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NETHERITE_LEGGINGS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NETHERITE_PICKAXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.NETHERITE_SCRAP, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NETHERITE_SHOVEL, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.NETHERITE_SWORD, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.NETHER_BRICK, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.NETHER_STAR, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.OAK_BOAT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.ORANGE_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PAINTING, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PAPER, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PHANTOM_MEMBRANE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PIGLIN_BANNER_PATTERN, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PINK_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.POISONOUS_POTATO, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.POPPED_CHORUS_FRUIT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PORKCHOP, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PRISMARINE_CRYSTALS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PRISMARINE_SHARD, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PUFFERFISH, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PUFFERFISH_BUCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PUMPKIN_PIE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.PURPLE_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.QUARTZ, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.RABBIT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.RABBIT_FOOT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.RABBIT_HIDE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.RABBIT_STEW, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.RED_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.ROTTEN_FLESH, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SADDLE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SALMON, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SALMON_BUCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SCUTE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SHEARS, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SHULKER_SHELL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SKULL_BANNER_PATTERN, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SLIME_BALL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SNOWBALL, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SPECTRAL_ARROW, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SPIDER_EYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SPRUCE_BOAT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.STICK, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.STONE_AXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.STONE_HOE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.STONE_PICKAXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.STONE_SHOVEL, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.STONE_SWORD, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.SUGAR, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.SUSPICIOUS_STEW, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.TNT_MINECART, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.TOTEM_OF_UNDYING, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.TRIDENT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.TROPICAL_FISH, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.TROPICAL_FISH_BUCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.TURTLE_HELMET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.WATER_BUCKET, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.WHEAT, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.WHITE_DYE, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.WOODEN_AXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.WOODEN_HOE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.WOODEN_PICKAXE, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.WOODEN_SHOVEL, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.WOODEN_SWORD, StockModelShapes.HANDHELD);
        this.func_240076_a_(Items.WRITABLE_BOOK, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.WRITTEN_BOOK, StockModelShapes.GENERATED);
        this.func_240076_a_(Items.YELLOW_DYE, StockModelShapes.GENERATED);
        this.func_240075_a_(Items.DEBUG_STICK, Items.STICK, StockModelShapes.HANDHELD);
        this.func_240075_a_(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, StockModelShapes.GENERATED);
    }
}
