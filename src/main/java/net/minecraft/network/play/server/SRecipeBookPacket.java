package net.minecraft.network.play.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.crafting.RecipeBookStatus;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SRecipeBookPacket implements IPacket<IClientPlayNetHandler>
{
    private SRecipeBookPacket.State state;
    private List<ResourceLocation> recipes;
    private List<ResourceLocation> displayedRecipes;
    private RecipeBookStatus field_244301_d;

    public SRecipeBookPacket()
    {
    }

    public SRecipeBookPacket(SRecipeBookPacket.State p_i242083_1_, Collection<ResourceLocation> p_i242083_2_, Collection<ResourceLocation> p_i242083_3_, RecipeBookStatus p_i242083_4_)
    {
        this.state = p_i242083_1_;
        this.recipes = ImmutableList.copyOf(p_i242083_2_);
        this.displayedRecipes = ImmutableList.copyOf(p_i242083_3_);
        this.field_244301_d = p_i242083_4_;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleRecipeBook(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.state = buf.readEnumValue(SRecipeBookPacket.State.class);
        this.field_244301_d = RecipeBookStatus.func_242157_a(buf);
        int i = buf.readVarInt();
        this.recipes = Lists.newArrayList();

        for (int j = 0; j < i; ++j)
        {
            this.recipes.add(buf.readResourceLocation());
        }

        if (this.state == SRecipeBookPacket.State.INIT)
        {
            i = buf.readVarInt();
            this.displayedRecipes = Lists.newArrayList();

            for (int k = 0; k < i; ++k)
            {
                this.displayedRecipes.add(buf.readResourceLocation());
            }
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.state);
        this.field_244301_d.func_242161_b(buf);
        buf.writeVarInt(this.recipes.size());

        for (ResourceLocation resourcelocation : this.recipes)
        {
            buf.writeResourceLocation(resourcelocation);
        }

        if (this.state == SRecipeBookPacket.State.INIT)
        {
            buf.writeVarInt(this.displayedRecipes.size());

            for (ResourceLocation resourcelocation1 : this.displayedRecipes)
            {
                buf.writeResourceLocation(resourcelocation1);
            }
        }
    }

    public List<ResourceLocation> getRecipes()
    {
        return this.recipes;
    }

    public List<ResourceLocation> getDisplayedRecipes()
    {
        return this.displayedRecipes;
    }

    public RecipeBookStatus func_244302_d()
    {
        return this.field_244301_d;
    }

    public SRecipeBookPacket.State getState()
    {
        return this.state;
    }

    public static enum State
    {
        INIT,
        ADD,
        REMOVE;
    }
}
