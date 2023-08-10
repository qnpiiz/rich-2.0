package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SEntityPacket implements IPacket<IClientPlayNetHandler>
{
    protected int entityId;
    protected short posX;
    protected short posY;
    protected short posZ;
    protected byte yaw;
    protected byte pitch;
    protected boolean onGround;
    protected boolean rotating;
    protected boolean isMovePacket;

    public static long func_218743_a(double p_218743_0_)
    {
        return MathHelper.lfloor(p_218743_0_ * 4096.0D);
    }

    public static double func_244299_a(long p_244299_0_)
    {
        return (double)p_244299_0_ / 4096.0D;
    }

    public Vector3d func_244300_a(Vector3d p_244300_1_)
    {
        double d0 = this.posX == 0 ? p_244300_1_.x : func_244299_a(func_218743_a(p_244300_1_.x) + (long)this.posX);
        double d1 = this.posY == 0 ? p_244300_1_.y : func_244299_a(func_218743_a(p_244300_1_.y) + (long)this.posY);
        double d2 = this.posZ == 0 ? p_244300_1_.z : func_244299_a(func_218743_a(p_244300_1_.z) + (long)this.posZ);
        return new Vector3d(d0, d1, d2);
    }

    public static Vector3d func_218744_a(long x, long y, long z)
    {
        return (new Vector3d((double)x, (double)y, (double)z)).scale((double)2.4414062E-4F);
    }

    public SEntityPacket()
    {
    }

    public SEntityPacket(int entityIdIn)
    {
        this.entityId = entityIdIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarInt(this.entityId);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleEntityMovement(this);
    }

    public String toString()
    {
        return "Entity_" + super.toString();
    }

    @Nullable
    public Entity getEntity(World worldIn)
    {
        return worldIn.getEntityByID(this.entityId);
    }

    public byte getYaw()
    {
        return this.yaw;
    }

    public byte getPitch()
    {
        return this.pitch;
    }

    public boolean isRotating()
    {
        return this.rotating;
    }

    public boolean func_229745_h_()
    {
        return this.isMovePacket;
    }

    public boolean getOnGround()
    {
        return this.onGround;
    }

    public static class LookPacket extends SEntityPacket
    {
        public LookPacket()
        {
            this.rotating = true;
        }

        public LookPacket(int entityIdIn, byte yawIn, byte pitchIn, boolean onGroundIn)
        {
            super(entityIdIn);
            this.yaw = yawIn;
            this.pitch = pitchIn;
            this.rotating = true;
            this.onGround = onGroundIn;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            super.readPacketData(buf);
            this.yaw = buf.readByte();
            this.pitch = buf.readByte();
            this.onGround = buf.readBoolean();
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            super.writePacketData(buf);
            buf.writeByte(this.yaw);
            buf.writeByte(this.pitch);
            buf.writeBoolean(this.onGround);
        }
    }

    public static class MovePacket extends SEntityPacket
    {
        public MovePacket()
        {
            this.rotating = true;
            this.isMovePacket = true;
        }

        public MovePacket(int entityId, short posX, short posY, short posZ, byte yaw, byte pitch, boolean onGroundIn)
        {
            super(entityId);
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.yaw = yaw;
            this.pitch = pitch;
            this.onGround = onGroundIn;
            this.rotating = true;
            this.isMovePacket = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            super.readPacketData(buf);
            this.posX = buf.readShort();
            this.posY = buf.readShort();
            this.posZ = buf.readShort();
            this.yaw = buf.readByte();
            this.pitch = buf.readByte();
            this.onGround = buf.readBoolean();
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            super.writePacketData(buf);
            buf.writeShort(this.posX);
            buf.writeShort(this.posY);
            buf.writeShort(this.posZ);
            buf.writeByte(this.yaw);
            buf.writeByte(this.pitch);
            buf.writeBoolean(this.onGround);
        }
    }

    public static class RelativeMovePacket extends SEntityPacket
    {
        public RelativeMovePacket()
        {
            this.isMovePacket = true;
        }

        public RelativeMovePacket(int entityId, short posX, short posY, short posZ, boolean onGround)
        {
            super(entityId);
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.onGround = onGround;
            this.isMovePacket = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            super.readPacketData(buf);
            this.posX = buf.readShort();
            this.posY = buf.readShort();
            this.posZ = buf.readShort();
            this.onGround = buf.readBoolean();
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            super.writePacketData(buf);
            buf.writeShort(this.posX);
            buf.writeShort(this.posY);
            buf.writeShort(this.posZ);
            buf.writeBoolean(this.onGround);
        }
    }
}
