package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SPlayerPositionLookPacket implements IPacket<IClientPlayNetHandler>
{
    private double x;
    private double y;
    private double z;
    public float yaw;
    public float pitch;
    private Set<SPlayerPositionLookPacket.Flags> flags;
    private int teleportId;

    public SPlayerPositionLookPacket()
    {
    }

    public SPlayerPositionLookPacket(double xIn, double yIn, double zIn, float yawIn, float pitchIn, Set<SPlayerPositionLookPacket.Flags> flagsIn, int teleportIdIn)
    {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
        this.yaw = yawIn;
        this.pitch = pitchIn;
        this.flags = flagsIn;
        this.teleportId = teleportIdIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.flags = SPlayerPositionLookPacket.Flags.unpack(buf.readUnsignedByte());
        this.teleportId = buf.readVarInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(SPlayerPositionLookPacket.Flags.pack(this.flags));
        buf.writeVarInt(this.teleportId);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handlePlayerPosLook(this);
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public int getTeleportId()
    {
        return this.teleportId;
    }

    public Set<SPlayerPositionLookPacket.Flags> getFlags()
    {
        return this.flags;
    }

    public static enum Flags
    {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int bit;

        private Flags(int bitIn)
        {
            this.bit = bitIn;
        }

        private int getMask()
        {
            return 1 << this.bit;
        }

        private boolean isSet(int flags)
        {
            return (flags & this.getMask()) == this.getMask();
        }

        public static Set<SPlayerPositionLookPacket.Flags> unpack(int flags)
        {
            Set<SPlayerPositionLookPacket.Flags> set = EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class);

            for (SPlayerPositionLookPacket.Flags splayerpositionlookpacket$flags : values())
            {
                if (splayerpositionlookpacket$flags.isSet(flags))
                {
                    set.add(splayerpositionlookpacket$flags);
                }
            }

            return set;
        }

        public static int pack(Set<SPlayerPositionLookPacket.Flags> flags)
        {
            int i = 0;

            for (SPlayerPositionLookPacket.Flags splayerpositionlookpacket$flags : flags)
            {
                i |= splayerpositionlookpacket$flags.getMask();
            }

            return i;
        }
    }
}
