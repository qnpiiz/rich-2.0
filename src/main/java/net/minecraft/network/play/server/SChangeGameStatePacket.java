package net.minecraft.network.play.server;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SChangeGameStatePacket implements IPacket<IClientPlayNetHandler>
{
    public static final SChangeGameStatePacket.State field_241764_a_ = new SChangeGameStatePacket.State(0);
    public static final SChangeGameStatePacket.State field_241765_b_ = new SChangeGameStatePacket.State(1);
    public static final SChangeGameStatePacket.State field_241766_c_ = new SChangeGameStatePacket.State(2);
    public static final SChangeGameStatePacket.State field_241767_d_ = new SChangeGameStatePacket.State(3);
    public static final SChangeGameStatePacket.State field_241768_e_ = new SChangeGameStatePacket.State(4);
    public static final SChangeGameStatePacket.State field_241769_f_ = new SChangeGameStatePacket.State(5);
    public static final SChangeGameStatePacket.State field_241770_g_ = new SChangeGameStatePacket.State(6);
    public static final SChangeGameStatePacket.State field_241771_h_ = new SChangeGameStatePacket.State(7);
    public static final SChangeGameStatePacket.State field_241772_i_ = new SChangeGameStatePacket.State(8);
    public static final SChangeGameStatePacket.State field_241773_j_ = new SChangeGameStatePacket.State(9);
    public static final SChangeGameStatePacket.State field_241774_k_ = new SChangeGameStatePacket.State(10);
    public static final SChangeGameStatePacket.State field_241775_l_ = new SChangeGameStatePacket.State(11);
    private SChangeGameStatePacket.State state;
    private float value;

    public SChangeGameStatePacket()
    {
    }

    public SChangeGameStatePacket(SChangeGameStatePacket.State p_i241263_1_, float p_i241263_2_)
    {
        this.state = p_i241263_1_;
        this.value = p_i241263_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.state = SChangeGameStatePacket.State.field_241777_a_.get(buf.readUnsignedByte());
        this.value = buf.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.state.field_241778_b_);
        buf.writeFloat(this.value);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleChangeGameState(this);
    }

    public SChangeGameStatePacket.State func_241776_b_()
    {
        return this.state;
    }

    public float getValue()
    {
        return this.value;
    }

    public static class State
    {
        private static final Int2ObjectMap<SChangeGameStatePacket.State> field_241777_a_ = new Int2ObjectOpenHashMap<>();
        private final int field_241778_b_;

        public State(int p_i241264_1_)
        {
            this.field_241778_b_ = p_i241264_1_;
            field_241777_a_.put(p_i241264_1_, this);
        }
    }
}
