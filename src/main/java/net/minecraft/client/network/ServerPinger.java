package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPinger
{
    private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(final ServerData server, final Runnable p_147224_2_) throws UnknownHostException
    {
        ServerAddress serveraddress = ServerAddress.fromString(server.serverIP);
        final NetworkManager networkmanager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
        this.pingDestinations.add(networkmanager);
        server.serverMOTD = new TranslationTextComponent("multiplayer.status.pinging");
        server.pingToServer = -1L;
        server.playerList = null;
        networkmanager.setNetHandler(new IClientStatusNetHandler()
        {
            private boolean successful;
            private boolean receivedStatus;
            private long pingSentAt;
            public void handleServerInfo(SServerInfoPacket packetIn)
            {
                if (this.receivedStatus)
                {
                    networkmanager.closeChannel(new TranslationTextComponent("multiplayer.status.unrequested"));
                }
                else
                {
                    this.receivedStatus = true;
                    ServerStatusResponse serverstatusresponse = packetIn.getResponse();

                    if (serverstatusresponse.getServerDescription() != null)
                    {
                        server.serverMOTD = serverstatusresponse.getServerDescription();
                    }
                    else
                    {
                        server.serverMOTD = StringTextComponent.EMPTY;
                    }

                    if (serverstatusresponse.getVersion() != null)
                    {
                        server.gameVersion = new StringTextComponent(serverstatusresponse.getVersion().getName());
                        server.version = serverstatusresponse.getVersion().getProtocol();
                    }
                    else
                    {
                        server.gameVersion = new TranslationTextComponent("multiplayer.status.old");
                        server.version = 0;
                    }

                    if (serverstatusresponse.getPlayers() != null)
                    {
                        server.populationInfo = ServerPinger.func_239171_b_(serverstatusresponse.getPlayers().getOnlinePlayerCount(), serverstatusresponse.getPlayers().getMaxPlayers());
                        List<ITextComponent> list = Lists.newArrayList();

                        if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayers().getPlayers()))
                        {
                            for (GameProfile gameprofile : serverstatusresponse.getPlayers().getPlayers())
                            {
                                list.add(new StringTextComponent(gameprofile.getName()));
                            }

                            if (serverstatusresponse.getPlayers().getPlayers().length < serverstatusresponse.getPlayers().getOnlinePlayerCount())
                            {
                                list.add(new TranslationTextComponent("multiplayer.status.and_more", serverstatusresponse.getPlayers().getOnlinePlayerCount() - serverstatusresponse.getPlayers().getPlayers().length));
                            }

                            server.playerList = list;
                        }
                    }
                    else
                    {
                        server.populationInfo = (new TranslationTextComponent("multiplayer.status.unknown")).mergeStyle(TextFormatting.DARK_GRAY);
                    }

                    String s = null;

                    if (serverstatusresponse.getFavicon() != null)
                    {
                        String s1 = serverstatusresponse.getFavicon();

                        if (s1.startsWith("data:image/png;base64,"))
                        {
                            s = s1.substring("data:image/png;base64,".length());
                        }
                        else
                        {
                            ServerPinger.LOGGER.error("Invalid server icon (unknown format)");
                        }
                    }

                    if (!Objects.equals(s, server.getBase64EncodedIconData()))
                    {
                        server.setBase64EncodedIconData(s);
                        p_147224_2_.run();
                    }

                    this.pingSentAt = Util.milliTime();
                    networkmanager.sendPacket(new CPingPacket(this.pingSentAt));
                    this.successful = true;
                }
            }
            public void handlePong(SPongPacket packetIn)
            {
                long i = this.pingSentAt;
                long j = Util.milliTime();
                server.pingToServer = j - i;
                networkmanager.closeChannel(new TranslationTextComponent("multiplayer.status.finished"));
            }
            public void onDisconnect(ITextComponent reason)
            {
                if (!this.successful)
                {
                    ServerPinger.LOGGER.error("Can't ping {}: {}", server.serverIP, reason.getString());
                    server.serverMOTD = (new TranslationTextComponent("multiplayer.status.cannot_connect")).mergeStyle(TextFormatting.DARK_RED);
                    server.populationInfo = StringTextComponent.EMPTY;
                    ServerPinger.this.tryCompatibilityPing(server);
                }
            }
            public NetworkManager getNetworkManager()
            {
                return networkmanager;
            }
        });

        try
        {
            networkmanager.sendPacket(new CHandshakePacket(serveraddress.getIP(), serveraddress.getPort(), ProtocolType.STATUS));
            networkmanager.sendPacket(new CServerQueryPacket());
        }
        catch (Throwable throwable)
        {
            LOGGER.error(throwable);
        }
    }

    private void tryCompatibilityPing(final ServerData server)
    {
        final ServerAddress serveraddress = ServerAddress.fromString(server.serverIP);
        (new Bootstrap()).group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>()
        {
            protected void initChannel(Channel p_initChannel_1_) throws Exception
            {
                try
                {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException channelexception)
                {
                }

                p_initChannel_1_.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>()
                {
                    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception
                    {
                        super.channelActive(p_channelActive_1_);
                        ByteBuf bytebuf = Unpooled.buffer();

                        try
                        {
                            bytebuf.writeByte(254);
                            bytebuf.writeByte(1);
                            bytebuf.writeByte(250);
                            char[] achar = "MC|PingHost".toCharArray();
                            bytebuf.writeShort(achar.length);

                            for (char c0 : achar)
                            {
                                bytebuf.writeChar(c0);
                            }

                            bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
                            bytebuf.writeByte(127);
                            achar = serveraddress.getIP().toCharArray();
                            bytebuf.writeShort(achar.length);

                            for (char c1 : achar)
                            {
                                bytebuf.writeChar(c1);
                            }

                            bytebuf.writeInt(serveraddress.getPort());
                            p_channelActive_1_.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        }
                        finally
                        {
                            bytebuf.release();
                        }
                    }
                    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, ByteBuf p_channelRead0_2_) throws Exception
                    {
                        short short1 = p_channelRead0_2_.readUnsignedByte();

                        if (short1 == 255)
                        {
                            String s = new String(p_channelRead0_2_.readBytes(p_channelRead0_2_.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                            String[] astring = Iterables.toArray(ServerPinger.PING_RESPONSE_SPLITTER.split(s), String.class);

                            if ("\u00a71".equals(astring[0]))
                            {
                                int i = MathHelper.getInt(astring[1], 0);
                                String s1 = astring[2];
                                String s2 = astring[3];
                                int j = MathHelper.getInt(astring[4], -1);
                                int k = MathHelper.getInt(astring[5], -1);
                                server.version = -1;
                                server.gameVersion = new StringTextComponent(s1);
                                server.serverMOTD = new StringTextComponent(s2);
                                server.populationInfo = ServerPinger.func_239171_b_(j, k);
                            }
                        }

                        p_channelRead0_1_.close();
                    }
                    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception
                    {
                        p_exceptionCaught_1_.close();
                    }
                });
            }
        }).channel(NioSocketChannel.class).connect(serveraddress.getIP(), serveraddress.getPort());
    }

    private static ITextComponent func_239171_b_(int p_239171_0_, int p_239171_1_)
    {
        return (new StringTextComponent(Integer.toString(p_239171_0_))).append((new StringTextComponent("/")).mergeStyle(TextFormatting.DARK_GRAY)).appendString(Integer.toString(p_239171_1_)).mergeStyle(TextFormatting.GRAY);
    }

    public void pingPendingNetworks()
    {
        synchronized (this.pingDestinations)
        {
            Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext())
            {
                NetworkManager networkmanager = iterator.next();

                if (networkmanager.isChannelOpen())
                {
                    networkmanager.tick();
                }
                else
                {
                    iterator.remove();
                    networkmanager.handleDisconnection();
                }
            }
        }
    }

    public void clearPendingNetworks()
    {
        synchronized (this.pingDestinations)
        {
            Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext())
            {
                NetworkManager networkmanager = iterator.next();

                if (networkmanager.isChannelOpen())
                {
                    iterator.remove();
                    networkmanager.closeChannel(new TranslationTextComponent("multiplayer.status.cancelled"));
                }
            }
        }
    }
}
