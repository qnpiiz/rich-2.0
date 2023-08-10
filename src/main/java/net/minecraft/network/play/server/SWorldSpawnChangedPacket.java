package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class SWorldSpawnChangedPacket implements IPacket<IClientPlayNetHandler>
{
    private BlockPos field_240831_a_;
    private float field_244312_b;

    public SWorldSpawnChangedPacket()
    {
    }

    public SWorldSpawnChangedPacket(BlockPos p_i242086_1_, float p_i242086_2_)
    {
        this.field_240831_a_ = p_i242086_1_;
        this.field_244312_b = p_i242086_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_240831_a_ = buf.readBlockPos();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.field_240831_a_);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.func_230488_a_(this);
    }

    public BlockPos func_240832_b_()
    {
        return this.field_240831_a_;
    }

    public float func_244313_c()
    {
        return this.field_244312_b;
    }
}
