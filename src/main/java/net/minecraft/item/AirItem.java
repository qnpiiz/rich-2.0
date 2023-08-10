package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class AirItem extends Item
{
    private final Block block;

    public AirItem(Block blockIn, Item.Properties properties)
    {
        super(properties);
        this.block = blockIn;
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getTranslationKey()
    {
        return this.block.getTranslationKey();
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        this.block.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
