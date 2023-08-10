package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SPlayerAbilitiesPacket implements IPacket<IClientPlayNetHandler>
{
    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;

    public SPlayerAbilitiesPacket()
    {
    }

    public SPlayerAbilitiesPacket(PlayerAbilities capabilities)
    {
        this.invulnerable = capabilities.disableDamage;
        this.flying = capabilities.isFlying;
        this.allowFlying = capabilities.allowFlying;
        this.creativeMode = capabilities.isCreativeMode;
        this.flySpeed = capabilities.getFlySpeed();
        this.walkSpeed = capabilities.getWalkSpeed();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        byte b0 = buf.readByte();
        this.invulnerable = (b0 & 1) != 0;
        this.flying = (b0 & 2) != 0;
        this.allowFlying = (b0 & 4) != 0;
        this.creativeMode = (b0 & 8) != 0;
        this.flySpeed = buf.readFloat();
        this.walkSpeed = buf.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        byte b0 = 0;

        if (this.invulnerable)
        {
            b0 = (byte)(b0 | 1);
        }

        if (this.flying)
        {
            b0 = (byte)(b0 | 2);
        }

        if (this.allowFlying)
        {
            b0 = (byte)(b0 | 4);
        }

        if (this.creativeMode)
        {
            b0 = (byte)(b0 | 8);
        }

        buf.writeByte(b0);
        buf.writeFloat(this.flySpeed);
        buf.writeFloat(this.walkSpeed);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handlePlayerAbilities(this);
    }

    public boolean isInvulnerable()
    {
        return this.invulnerable;
    }

    public boolean isFlying()
    {
        return this.flying;
    }

    public boolean isAllowFlying()
    {
        return this.allowFlying;
    }

    public boolean isCreativeMode()
    {
        return this.creativeMode;
    }

    public float getFlySpeed()
    {
        return this.flySpeed;
    }

    public float getWalkSpeed()
    {
        return this.walkSpeed;
    }
}
