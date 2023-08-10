package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class CUpdateJigsawBlockPacket implements IPacket<IServerPlayNetHandler>
{
    private BlockPos field_218790_a;
    private ResourceLocation field_240847_b_;
    private ResourceLocation field_240848_c_;
    private ResourceLocation field_240849_d_;
    private String field_218793_d;
    private JigsawTileEntity.OrientationType field_240850_f_;

    public CUpdateJigsawBlockPacket()
    {
    }

    public CUpdateJigsawBlockPacket(BlockPos p_i232584_1_, ResourceLocation p_i232584_2_, ResourceLocation p_i232584_3_, ResourceLocation p_i232584_4_, String p_i232584_5_, JigsawTileEntity.OrientationType p_i232584_6_)
    {
        this.field_218790_a = p_i232584_1_;
        this.field_240847_b_ = p_i232584_2_;
        this.field_240848_c_ = p_i232584_3_;
        this.field_240849_d_ = p_i232584_4_;
        this.field_218793_d = p_i232584_5_;
        this.field_240850_f_ = p_i232584_6_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_218790_a = buf.readBlockPos();
        this.field_240847_b_ = buf.readResourceLocation();
        this.field_240848_c_ = buf.readResourceLocation();
        this.field_240849_d_ = buf.readResourceLocation();
        this.field_218793_d = buf.readString(32767);
        this.field_240850_f_ = JigsawTileEntity.OrientationType.func_235673_a_(buf.readString(32767)).orElse(JigsawTileEntity.OrientationType.ALIGNED);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.field_218790_a);
        buf.writeResourceLocation(this.field_240847_b_);
        buf.writeResourceLocation(this.field_240848_c_);
        buf.writeResourceLocation(this.field_240849_d_);
        buf.writeString(this.field_218793_d);
        buf.writeString(this.field_240850_f_.getString());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler)
    {
        handler.func_217262_a(this);
    }

    public BlockPos func_218789_b()
    {
        return this.field_218790_a;
    }

    public ResourceLocation func_240851_c_()
    {
        return this.field_240847_b_;
    }

    public ResourceLocation func_240852_d_()
    {
        return this.field_240848_c_;
    }

    public ResourceLocation func_240853_e_()
    {
        return this.field_240849_d_;
    }

    public String func_218788_e()
    {
        return this.field_218793_d;
    }

    public JigsawTileEntity.OrientationType func_240854_g_()
    {
        return this.field_240850_f_;
    }
}
