package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CEditBookPacket implements IPacket<IServerPlayNetHandler>
{
    private ItemStack stack;
    private boolean updateAll;
    private int field_244707_c;

    public CEditBookPacket()
    {
    }

    public CEditBookPacket(ItemStack p_i242143_1_, boolean p_i242143_2_, int p_i242143_3_)
    {
        this.stack = p_i242143_1_.copy();
        this.updateAll = p_i242143_2_;
        this.field_244707_c = p_i242143_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.stack = buf.readItemStack();
        this.updateAll = buf.readBoolean();
        this.field_244707_c = buf.readVarInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeItemStack(this.stack);
        buf.writeBoolean(this.updateAll);
        buf.writeVarInt(this.field_244707_c);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler)
    {
        handler.processEditBook(this);
    }

    /**
     * The client written book stack containing up to date nbt data.
     */
    public ItemStack getStack()
    {
        return this.stack;
    }

    /**
     * If true it updates author, title and pages. Otherwise just update pages.
     */
    public boolean shouldUpdateAll()
    {
        return this.updateAll;
    }

    public int func_244708_d()
    {
        return this.field_244707_c;
    }
}
