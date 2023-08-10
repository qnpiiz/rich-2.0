package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.CryptException;
import net.minecraft.util.CryptManager;

public class SEncryptionRequestPacket implements IPacket<IClientLoginNetHandler>
{
    private String hashedServerId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public SEncryptionRequestPacket()
    {
    }

    public SEncryptionRequestPacket(String p_i242142_1_, byte[] p_i242142_2_, byte[] p_i242142_3_)
    {
        this.hashedServerId = p_i242142_1_;
        this.publicKey = p_i242142_2_;
        this.verifyToken = p_i242142_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.hashedServerId = buf.readString(20);
        this.publicKey = buf.readByteArray();
        this.verifyToken = buf.readByteArray();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.hashedServerId);
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.verifyToken);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientLoginNetHandler handler)
    {
        handler.handleEncryptionRequest(this);
    }

    public String getServerId()
    {
        return this.hashedServerId;
    }

    public PublicKey getPublicKey() throws CryptException
    {
        return CryptManager.decodePublicKey(this.publicKey);
    }

    public byte[] getVerifyToken()
    {
        return this.verifyToken;
    }
}
