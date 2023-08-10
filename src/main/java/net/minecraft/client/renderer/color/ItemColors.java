package net.minecraft.client.renderer.color;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IBlockDisplayReader;

public class ItemColors
{
    private final ObjectIntIdentityMap<IItemColor> colors = new ObjectIntIdentityMap<>(32);

    public static ItemColors init(BlockColors colors)
    {
        ItemColors itemcolors = new ItemColors();
        itemcolors.register((stack, color) ->
        {
            return color > 0 ? -1 : ((IDyeableArmorItem)stack.getItem()).getColor(stack);
        }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
        itemcolors.register((stack, color) ->
        {
            return GrassColors.get(0.5D, 1.0D);
        }, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
        itemcolors.register((stack, color) ->
        {
            if (color != 1)
            {
                return -1;
            }
            else {
                CompoundNBT compoundnbt = stack.getChildTag("Explosion");
                int[] aint = compoundnbt != null && compoundnbt.contains("Colors", 11) ? compoundnbt.getIntArray("Colors") : null;

                if (aint != null && aint.length != 0)
                {
                    if (aint.length == 1)
                    {
                        return aint[0];
                    }
                    else
                    {
                        int i = 0;
                        int j = 0;
                        int k = 0;

                        for (int l : aint)
                        {
                            i += (l & 16711680) >> 16;
                            j += (l & 65280) >> 8;
                            k += (l & 255) >> 0;
                        }

                        i = i / aint.length;
                        j = j / aint.length;
                        k = k / aint.length;
                        return i << 16 | j << 8 | k;
                    }
                }
                else {
                    return 9079434;
                }
            }
        }, Items.FIREWORK_STAR);
        itemcolors.register((stack, color) ->
        {
            return color > 0 ? -1 : PotionUtils.getColor(stack);
        }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

        for (SpawnEggItem spawneggitem : SpawnEggItem.getEggs())
        {
            itemcolors.register((stack, color) ->
            {
                return spawneggitem.getColor(color);
            }, spawneggitem);
        }

        itemcolors.register((stack, color) ->
        {
            BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().getDefaultState();
            return colors.getColor(blockstate, (IBlockDisplayReader)null, (BlockPos)null, color);
        }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
        itemcolors.register((stack, color) ->
        {
            return color == 0 ? PotionUtils.getColor(stack) : -1;
        }, Items.TIPPED_ARROW);
        itemcolors.register((stack, color) ->
        {
            return color == 0 ? -1 : FilledMapItem.getColor(stack);
        }, Items.FILLED_MAP);
        return itemcolors;
    }

    public int getColor(ItemStack stack, int tintIndex)
    {
        IItemColor iitemcolor = this.colors.getByValue(Registry.ITEM.getId(stack.getItem()));
        return iitemcolor == null ? -1 : iitemcolor.getColor(stack, tintIndex);
    }

    public void register(IItemColor itemColor, IItemProvider... itemsIn)
    {
        for (IItemProvider iitemprovider : itemsIn)
        {
            this.colors.put(itemColor, Item.getIdFromItem(iitemprovider.asItem()));
        }
    }
}
