package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;

public class CMarkRecipeSeenPacket implements IPacket<IServerPlayNetHandler>
{
    private ResourceLocation field_244320_a;

    public CMarkRecipeSeenPacket()
    {
    }

    public CMarkRecipeSeenPacket(IRecipe<?> p_i242089_1_)
    {
        this.field_244320_a = p_i242089_1_.getId();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_244320_a = buf.readResourceLocation();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeResourceLocation(this.field_244320_a);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler)
    {
        handler.handleRecipeBookUpdate(this);
    }

    public ResourceLocation func_244321_b()
    {
        return this.field_244320_a;
    }
}
