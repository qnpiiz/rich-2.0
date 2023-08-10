package net.minecraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.optifine.util.PacketRunnable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketThreadUtil
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static RegistryKey<World> lastDimensionType = null;

    public static <T extends INetHandler> void checkThreadAndEnqueue(IPacket<T> packetIn, T processor, ServerWorld worldIn) throws ThreadQuickExitException
    {
        checkThreadAndEnqueue(packetIn, processor, worldIn.getServer());
    }

    public static <T extends INetHandler> void checkThreadAndEnqueue(IPacket<T> packetIn, T processor, ThreadTaskExecutor<?> executor) throws ThreadQuickExitException
    {
        if (!executor.isOnExecutionThread())
        {
            executor.execute(new PacketRunnable(packetIn, () ->
            {
                clientPreProcessPacket(packetIn);

                if (processor.getNetworkManager().isChannelOpen())
                {
                    packetIn.processPacket(processor);
                }
                else {
                    LOGGER.debug("Ignoring packet due to disconnection: " + packetIn);
                }
            }));
            throw ThreadQuickExitException.INSTANCE;
        }
        else
        {
            clientPreProcessPacket(packetIn);
        }
    }

    protected static void clientPreProcessPacket(IPacket p_clientPreProcessPacket_0_)
    {
        if (p_clientPreProcessPacket_0_ instanceof SPlayerPositionLookPacket)
        {
            Minecraft.getInstance().worldRenderer.onPlayerPositionSet();
        }

        if (p_clientPreProcessPacket_0_ instanceof SRespawnPacket)
        {
            SRespawnPacket srespawnpacket = (SRespawnPacket)p_clientPreProcessPacket_0_;
            lastDimensionType = srespawnpacket.func_240827_c_();
        }
        else if (p_clientPreProcessPacket_0_ instanceof SJoinGamePacket)
        {
            SJoinGamePacket sjoingamepacket = (SJoinGamePacket)p_clientPreProcessPacket_0_;
            lastDimensionType = sjoingamepacket.func_240819_i_();
        }
        else
        {
            lastDimensionType = null;
        }
    }
}
