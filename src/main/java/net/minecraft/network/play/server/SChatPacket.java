package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

public class SChatPacket implements IPacket<IClientPlayNetHandler>
{
    private ITextComponent chatComponent;
    private ChatType type;
    private UUID field_240809_c_;

    public SChatPacket()
    {
    }

    public SChatPacket(ITextComponent p_i232578_1_, ChatType p_i232578_2_, UUID p_i232578_3_)
    {
        this.chatComponent = p_i232578_1_;
        this.type = p_i232578_2_;
        this.field_240809_c_ = p_i232578_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.chatComponent = buf.readTextComponent();
        this.type = ChatType.byId(buf.readByte());
        this.field_240809_c_ = buf.readUniqueId();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeTextComponent(this.chatComponent);
        buf.writeByte(this.type.getId());
        buf.writeUniqueId(this.field_240809_c_);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleChat(this);
    }

    public ITextComponent getChatComponent()
    {
        return this.chatComponent;
    }

    /**
     * This method returns true if the type is SYSTEM or ABOVE_HOTBAR, and false if CHAT
     */
    public boolean isSystem()
    {
        return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
    }

    public ChatType getType()
    {
        return this.type;
    }

    public UUID func_240810_e_()
    {
        return this.field_240809_c_;
    }

    public boolean shouldSkipErrors()
    {
        return true;
    }
}
