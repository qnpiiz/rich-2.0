package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;

public class CJigsawBlockGeneratePacket implements IPacket<IServerPlayNetHandler>
{
    private BlockPos field_240841_a_;
    private int field_240842_b_;
    private boolean field_240843_c_;

    public CJigsawBlockGeneratePacket()
    {
    }

    public CJigsawBlockGeneratePacket(BlockPos p_i232583_1_, int p_i232583_2_, boolean p_i232583_3_)
    {
        this.field_240841_a_ = p_i232583_1_;
        this.field_240842_b_ = p_i232583_2_;
        this.field_240843_c_ = p_i232583_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_240841_a_ = buf.readBlockPos();
        this.field_240842_b_ = buf.readVarInt();
        this.field_240843_c_ = buf.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.field_240841_a_);
        buf.writeVarInt(this.field_240842_b_);
        buf.writeBoolean(this.field_240843_c_);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler)
    {
        handler.func_230549_a_(this);
    }

    public BlockPos func_240844_b_()
    {
        return this.field_240841_a_;
    }

    public int func_240845_c_()
    {
        return this.field_240842_b_;
    }

    public boolean func_240846_d_()
    {
        return this.field_240843_c_;
    }
}
