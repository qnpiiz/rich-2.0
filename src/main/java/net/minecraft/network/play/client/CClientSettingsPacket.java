package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.HandSide;

public class CClientSettingsPacket implements IPacket<IServerPlayNetHandler>
{
    private String lang;
    private int view;
    private ChatVisibility chatVisibility;
    private boolean enableColors;
    private int modelPartFlags;
    private HandSide mainHand;

    public CClientSettingsPacket()
    {
    }

    public CClientSettingsPacket(String lang, int view, ChatVisibility chatVisibility, boolean enableColors, int modelPartFlags, HandSide mainHand)
    {
        this.lang = lang;
        this.view = view;
        this.chatVisibility = chatVisibility;
        this.enableColors = enableColors;
        this.modelPartFlags = modelPartFlags;
        this.mainHand = mainHand;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.lang = buf.readString(16);
        this.view = buf.readByte();
        this.chatVisibility = buf.readEnumValue(ChatVisibility.class);
        this.enableColors = buf.readBoolean();
        this.modelPartFlags = buf.readUnsignedByte();
        this.mainHand = buf.readEnumValue(HandSide.class);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.lang);
        buf.writeByte(this.view);
        buf.writeEnumValue(this.chatVisibility);
        buf.writeBoolean(this.enableColors);
        buf.writeByte(this.modelPartFlags);
        buf.writeEnumValue(this.mainHand);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler)
    {
        handler.processClientSettings(this);
    }

    public ChatVisibility getChatVisibility()
    {
        return this.chatVisibility;
    }

    public boolean isColorsEnabled()
    {
        return this.enableColors;
    }

    public int getModelPartFlags()
    {
        return this.modelPartFlags;
    }

    public HandSide getMainHand()
    {
        return this.mainHand;
    }
}
