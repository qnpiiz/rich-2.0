package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CUpdateRecipeBookStatusPacket implements IPacket<IServerPlayNetHandler>
{
    private RecipeBookCategory field_244314_a;
    private boolean field_244315_b;
    private boolean field_244316_c;

    public CUpdateRecipeBookStatusPacket()
    {
    }

    public CUpdateRecipeBookStatusPacket(RecipeBookCategory p_i242088_1_, boolean p_i242088_2_, boolean p_i242088_3_)
    {
        this.field_244314_a = p_i242088_1_;
        this.field_244315_b = p_i242088_2_;
        this.field_244316_c = p_i242088_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_244314_a = buf.readEnumValue(RecipeBookCategory.class);
        this.field_244315_b = buf.readBoolean();
        this.field_244316_c = buf.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.field_244314_a);
        buf.writeBoolean(this.field_244315_b);
        buf.writeBoolean(this.field_244316_c);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler)
    {
        handler.func_241831_a(this);
    }

    public RecipeBookCategory func_244317_b()
    {
        return this.field_244314_a;
    }

    public boolean func_244318_c()
    {
        return this.field_244315_b;
    }

    public boolean func_244319_d()
    {
        return this.field_244316_c;
    }
}
