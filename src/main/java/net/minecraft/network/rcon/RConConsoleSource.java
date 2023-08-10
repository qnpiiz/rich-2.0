package net.minecraft.network.rcon;

import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class RConConsoleSource implements ICommandSource
{
    private static final StringTextComponent field_232647_b_ = new StringTextComponent("Rcon");
    private final StringBuffer buffer = new StringBuffer();
    private final MinecraftServer server;

    public RConConsoleSource(MinecraftServer serverIn)
    {
        this.server = serverIn;
    }

    /**
     * Clears the RCon log
     */
    public void resetLog()
    {
        this.buffer.setLength(0);
    }

    /**
     * Gets the contents of the RCon log
     */
    public String getLogContents()
    {
        return this.buffer.toString();
    }

    public CommandSource getCommandSource()
    {
        ServerWorld serverworld = this.server.func_241755_D_();
        return new CommandSource(this, Vector3d.copy(serverworld.getSpawnPoint()), Vector2f.ZERO, serverworld, 4, "Rcon", field_232647_b_, this.server, (Entity)null);
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void sendMessage(ITextComponent component, UUID senderUUID)
    {
        this.buffer.append(component.getString());
    }

    public boolean shouldReceiveFeedback()
    {
        return true;
    }

    public boolean shouldReceiveErrors()
    {
        return true;
    }

    public boolean allowLogging()
    {
        return this.server.allowLoggingRcon();
    }
}
