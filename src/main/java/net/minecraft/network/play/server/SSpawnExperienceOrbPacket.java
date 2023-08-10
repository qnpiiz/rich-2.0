package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SSpawnExperienceOrbPacket implements IPacket<IClientPlayNetHandler>
{
    private int entityID;
    private double posX;
    private double posY;
    private double posZ;
    private int xpValue;

    public SSpawnExperienceOrbPacket()
    {
    }

    public SSpawnExperienceOrbPacket(ExperienceOrbEntity orb)
    {
        this.entityID = orb.getEntityId();
        this.posX = orb.getPosX();
        this.posY = orb.getPosY();
        this.posZ = orb.getPosZ();
        this.xpValue = orb.getXpValue();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityID = buf.readVarInt();
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
        this.xpValue = buf.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarInt(this.entityID);
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
        buf.writeShort(this.xpValue);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleSpawnExperienceOrb(this);
    }

    public int getEntityID()
    {
        return this.entityID;
    }

    public double getX()
    {
        return this.posX;
    }

    public double getY()
    {
        return this.posY;
    }

    public double getZ()
    {
        return this.posZ;
    }

    public int getXPValue()
    {
        return this.xpValue;
    }
}
