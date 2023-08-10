package net.minecraft.item;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;

public class MerchantOffers extends ArrayList<MerchantOffer>
{
    public MerchantOffers()
    {
    }

    public MerchantOffers(CompoundNBT nbt)
    {
        ListNBT listnbt = nbt.getList("Recipes", 10);

        for (int i = 0; i < listnbt.size(); ++i)
        {
            this.add(new MerchantOffer(listnbt.getCompound(i)));
        }
    }

    @Nullable
    public MerchantOffer func_222197_a(ItemStack p_222197_1_, ItemStack p_222197_2_, int recipeIndex)
    {
        if (recipeIndex > 0 && recipeIndex < this.size())
        {
            MerchantOffer merchantoffer1 = this.get(recipeIndex);
            return merchantoffer1.matches(p_222197_1_, p_222197_2_) ? merchantoffer1 : null;
        }
        else
        {
            for (int i = 0; i < this.size(); ++i)
            {
                MerchantOffer merchantoffer = this.get(i);

                if (merchantoffer.matches(p_222197_1_, p_222197_2_))
                {
                    return merchantoffer;
                }
            }

            return null;
        }
    }

    public void write(PacketBuffer buffer)
    {
        buffer.writeByte((byte)(this.size() & 255));

        for (int i = 0; i < this.size(); ++i)
        {
            MerchantOffer merchantoffer = this.get(i);
            buffer.writeItemStack(merchantoffer.getBuyingStackFirst());
            buffer.writeItemStack(merchantoffer.getSellingStack());
            ItemStack itemstack = merchantoffer.getBuyingStackSecond();
            buffer.writeBoolean(!itemstack.isEmpty());

            if (!itemstack.isEmpty())
            {
                buffer.writeItemStack(itemstack);
            }

            buffer.writeBoolean(merchantoffer.hasNoUsesLeft());
            buffer.writeInt(merchantoffer.getUses());
            buffer.writeInt(merchantoffer.getMaxUses());
            buffer.writeInt(merchantoffer.getGivenExp());
            buffer.writeInt(merchantoffer.getSpecialPrice());
            buffer.writeFloat(merchantoffer.getPriceMultiplier());
            buffer.writeInt(merchantoffer.getDemand());
        }
    }

    public static MerchantOffers read(PacketBuffer buffer)
    {
        MerchantOffers merchantoffers = new MerchantOffers();
        int i = buffer.readByte() & 255;

        for (int j = 0; j < i; ++j)
        {
            ItemStack itemstack = buffer.readItemStack();
            ItemStack itemstack1 = buffer.readItemStack();
            ItemStack itemstack2 = ItemStack.EMPTY;

            if (buffer.readBoolean())
            {
                itemstack2 = buffer.readItemStack();
            }

            boolean flag = buffer.readBoolean();
            int k = buffer.readInt();
            int l = buffer.readInt();
            int i1 = buffer.readInt();
            int j1 = buffer.readInt();
            float f = buffer.readFloat();
            int k1 = buffer.readInt();
            MerchantOffer merchantoffer = new MerchantOffer(itemstack, itemstack2, itemstack1, k, l, i1, f, k1);

            if (flag)
            {
                merchantoffer.makeUnavailable();
            }

            merchantoffer.setSpecialPrice(j1);
            merchantoffers.add(merchantoffer);
        }

        return merchantoffers;
    }

    public CompoundNBT write()
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < this.size(); ++i)
        {
            MerchantOffer merchantoffer = this.get(i);
            listnbt.add(merchantoffer.write());
        }

        compoundnbt.put("Recipes", listnbt);
        return compoundnbt;
    }
}
