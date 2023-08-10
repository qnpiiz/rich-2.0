package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.UUIDCodec;

public class SLoginSuccessPacket implements IPacket<IClientLoginNetHandler>
{
    private GameProfile profile;

    public SLoginSuccessPacket()
    {
    }

    public SLoginSuccessPacket(GameProfile profileIn)
    {
        this.profile = profileIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        int[] aint = new int[4];

        for (int i = 0; i < aint.length; ++i)
        {
            aint[i] = buf.readInt();
        }

        UUID uuid = UUIDCodec.decodeUUID(aint);
        String s = buf.readString(16);
        this.profile = new GameProfile(uuid, s);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        for (int i : UUIDCodec.encodeUUID(this.profile.getId()))
        {
            buf.writeInt(i);
        }

        buf.writeString(this.profile.getName());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientLoginNetHandler handler)
    {
        handler.handleLoginSuccess(this);
    }

    public GameProfile getProfile()
    {
        return this.profile;
    }
}
