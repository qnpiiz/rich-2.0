package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;

public class CUpdateSignPacket implements IPacket<IServerPlayNetHandler>
{
    private BlockPos pos;
    private String[] lines;

    public CUpdateSignPacket()
    {
    }

    public CUpdateSignPacket(BlockPos p_i232585_1_, String p_i232585_2_, String p_i232585_3_, String p_i232585_4_, String p_i232585_5_)
    {
        this.pos = p_i232585_1_;
        this.lines = new String[] {p_i232585_2_, p_i232585_3_, p_i232585_4_, p_i232585_5_};
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.pos = buf.readBlockPos();
        this.lines = new String[4];

        for (int i = 0; i < 4; ++i)
        {
            this.lines[i] = buf.readString(384);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.pos);

        for (int i = 0; i < 4; ++i)
        {
            buf.writeString(this.lines[i]);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler)
    {
        handler.processUpdateSign(this);
    }

    public BlockPos getPosition()
    {
        return this.pos;
    }

    public String[] getLines()
    {
        return this.lines;
    }
}
