package net.minecraft.network.play.server;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.IOException;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.ChunkSection;

public class SMultiBlockChangePacket implements IPacket<IClientPlayNetHandler>
{
    private SectionPos field_244305_a;
    private short[] field_244306_b;
    private BlockState[] field_244307_c;
    private boolean field_244308_d;

    public SMultiBlockChangePacket()
    {
    }

    public SMultiBlockChangePacket(SectionPos p_i242085_1_, ShortSet p_i242085_2_, ChunkSection p_i242085_3_, boolean p_i242085_4_)
    {
        this.field_244305_a = p_i242085_1_;
        this.field_244308_d = p_i242085_4_;
        this.func_244309_a(p_i242085_2_.size());
        int i = 0;

        for (short short1 : p_i242085_2_)
        {
            this.field_244306_b[i] = short1;
            this.field_244307_c[i] = p_i242085_3_.getBlockState(SectionPos.func_243641_a(short1), SectionPos.func_243642_b(short1), SectionPos.func_243643_c(short1));
            ++i;
        }
    }

    private void func_244309_a(int p_244309_1_)
    {
        this.field_244306_b = new short[p_244309_1_];
        this.field_244307_c = new BlockState[p_244309_1_];
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_244305_a = SectionPos.from(buf.readLong());
        this.field_244308_d = buf.readBoolean();
        int i = buf.readVarInt();
        this.func_244309_a(i);

        for (int j = 0; j < this.field_244306_b.length; ++j)
        {
            long k = buf.readVarLong();
            this.field_244306_b[j] = (short)((int)(k & 4095L));
            this.field_244307_c[j] = Block.BLOCK_STATE_IDS.getByValue((int)(k >>> 12));
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeLong(this.field_244305_a.asLong());
        buf.writeBoolean(this.field_244308_d);
        buf.writeVarInt(this.field_244306_b.length);

        for (int i = 0; i < this.field_244306_b.length; ++i)
        {
            buf.writeVarLong((long)(Block.getStateId(this.field_244307_c[i]) << 12 | this.field_244306_b[i]));
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleMultiBlockChange(this);
    }

    public void func_244310_a(BiConsumer<BlockPos, BlockState> p_244310_1_)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i < this.field_244306_b.length; ++i)
        {
            short short1 = this.field_244306_b[i];
            blockpos$mutable.setPos(this.field_244305_a.func_243644_d(short1), this.field_244305_a.func_243645_e(short1), this.field_244305_a.func_243646_f(short1));
            p_244310_1_.accept(blockpos$mutable, this.field_244307_c[i]);
        }
    }

    public boolean func_244311_b()
    {
        return this.field_244308_d;
    }
}
