package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryThread extends RConThread
{
    private static final Logger field_232648_d_ = LogManager.getLogger();
    private long lastAuthCheckTime;
    private final int queryPort;
    private final int serverPort;
    private final int maxPlayers;
    private final String serverMotd;
    private final String worldName;
    private DatagramSocket querySocket;
    private final byte[] buffer = new byte[1460];
    private String queryHostname;
    private String serverHostname;
    private final Map<SocketAddress, QueryThread.Auth> queryClients;
    private final RConOutputStream output;
    private long lastQueryResponseTime;
    private final IServer field_232649_r_;

    private QueryThread(IServer p_i241890_1_, int p_i241890_2_)
    {
        super("Query Listener");
        this.field_232649_r_ = p_i241890_1_;
        this.queryPort = p_i241890_2_;
        this.serverHostname = p_i241890_1_.getHostname();
        this.serverPort = p_i241890_1_.getPort();
        this.serverMotd = p_i241890_1_.getMotd();
        this.maxPlayers = p_i241890_1_.getMaxPlayers();
        this.worldName = p_i241890_1_.func_230542_k__();
        this.lastQueryResponseTime = 0L;
        this.queryHostname = "0.0.0.0";

        if (!this.serverHostname.isEmpty() && !this.queryHostname.equals(this.serverHostname))
        {
            this.queryHostname = this.serverHostname;
        }
        else
        {
            this.serverHostname = "0.0.0.0";

            try
            {
                InetAddress inetaddress = InetAddress.getLocalHost();
                this.queryHostname = inetaddress.getHostAddress();
            }
            catch (UnknownHostException unknownhostexception)
            {
                field_232648_d_.warn("Unable to determine local host IP, please set server-ip in server.properties", (Throwable)unknownhostexception);
            }
        }

        this.output = new RConOutputStream(1460);
        this.queryClients = Maps.newHashMap();
    }

    @Nullable
    public static QueryThread func_242129_a(IServer p_242129_0_)
    {
        int i = p_242129_0_.getServerProperties().queryPort;

        if (0 < i && 65535 >= i)
        {
            QueryThread querythread = new QueryThread(p_242129_0_, i);
            return !querythread.func_241832_a() ? null : querythread;
        }
        else
        {
            field_232648_d_.warn("Invalid query port {} found in server.properties (queries disabled)", (int)i);
            return null;
        }
    }

    /**
     * Sends a byte array as a DatagramPacket response to the client who sent the given DatagramPacket
     */
    private void sendResponsePacket(byte[] data, DatagramPacket requestPacket) throws IOException
    {
        this.querySocket.send(new DatagramPacket(data, data.length, requestPacket.getSocketAddress()));
    }

    /**
     * Parses an incoming DatagramPacket, returning true if the packet was valid
     */
    private boolean parseIncomingPacket(DatagramPacket requestPacket) throws IOException
    {
        byte[] abyte = requestPacket.getData();
        int i = requestPacket.getLength();
        SocketAddress socketaddress = requestPacket.getSocketAddress();
        field_232648_d_.debug("Packet len {} [{}]", i, socketaddress);

        if (3 <= i && -2 == abyte[0] && -3 == abyte[1])
        {
            field_232648_d_.debug("Packet '{}' [{}]", RConUtils.getByteAsHexString(abyte[2]), socketaddress);

            switch (abyte[2])
            {
                case 0:
                    if (!this.verifyClientAuth(requestPacket))
                    {
                        field_232648_d_.debug("Invalid challenge [{}]", (Object)socketaddress);
                        return false;
                    }
                    else if (15 == i)
                    {
                        this.sendResponsePacket(this.createQueryResponse(requestPacket), requestPacket);
                        field_232648_d_.debug("Rules [{}]", (Object)socketaddress);
                    }
                    else
                    {
                        RConOutputStream rconoutputstream = new RConOutputStream(1460);
                        rconoutputstream.writeInt(0);
                        rconoutputstream.writeByteArray(this.getRequestID(requestPacket.getSocketAddress()));
                        rconoutputstream.writeString(this.serverMotd);
                        rconoutputstream.writeString("SMP");
                        rconoutputstream.writeString(this.worldName);
                        rconoutputstream.writeString(Integer.toString(this.field_232649_r_.getCurrentPlayerCount()));
                        rconoutputstream.writeString(Integer.toString(this.maxPlayers));
                        rconoutputstream.writeShort((short)this.serverPort);
                        rconoutputstream.writeString(this.queryHostname);
                        this.sendResponsePacket(rconoutputstream.toByteArray(), requestPacket);
                        field_232648_d_.debug("Status [{}]", (Object)socketaddress);
                    }

                default:
                    return true;

                case 9:
                    this.sendAuthChallenge(requestPacket);
                    field_232648_d_.debug("Challenge [{}]", (Object)socketaddress);
                    return true;
            }
        }
        else
        {
            field_232648_d_.debug("Invalid packet [{}]", (Object)socketaddress);
            return false;
        }
    }

    /**
     * Creates a query response as a byte array for the specified query DatagramPacket
     */
    private byte[] createQueryResponse(DatagramPacket requestPacket) throws IOException
    {
        long i = Util.milliTime();

        if (i < this.lastQueryResponseTime + 5000L)
        {
            byte[] abyte = this.output.toByteArray();
            byte[] abyte1 = this.getRequestID(requestPacket.getSocketAddress());
            abyte[1] = abyte1[0];
            abyte[2] = abyte1[1];
            abyte[3] = abyte1[2];
            abyte[4] = abyte1[3];
            return abyte;
        }
        else
        {
            this.lastQueryResponseTime = i;
            this.output.reset();
            this.output.writeInt(0);
            this.output.writeByteArray(this.getRequestID(requestPacket.getSocketAddress()));
            this.output.writeString("splitnum");
            this.output.writeInt(128);
            this.output.writeInt(0);
            this.output.writeString("hostname");
            this.output.writeString(this.serverMotd);
            this.output.writeString("gametype");
            this.output.writeString("SMP");
            this.output.writeString("game_id");
            this.output.writeString("MINECRAFT");
            this.output.writeString("version");
            this.output.writeString(this.field_232649_r_.getMinecraftVersion());
            this.output.writeString("plugins");
            this.output.writeString(this.field_232649_r_.getPlugins());
            this.output.writeString("map");
            this.output.writeString(this.worldName);
            this.output.writeString("numplayers");
            this.output.writeString("" + this.field_232649_r_.getCurrentPlayerCount());
            this.output.writeString("maxplayers");
            this.output.writeString("" + this.maxPlayers);
            this.output.writeString("hostport");
            this.output.writeString("" + this.serverPort);
            this.output.writeString("hostip");
            this.output.writeString(this.queryHostname);
            this.output.writeInt(0);
            this.output.writeInt(1);
            this.output.writeString("player_");
            this.output.writeInt(0);
            String[] astring = this.field_232649_r_.getOnlinePlayerNames();

            for (String s : astring)
            {
                this.output.writeString(s);
            }

            this.output.writeInt(0);
            return this.output.toByteArray();
        }
    }

    /**
     * Returns the request ID provided by the authorized client
     */
    private byte[] getRequestID(SocketAddress address)
    {
        return this.queryClients.get(address).getRequestId();
    }

    /**
     * Returns true if the client has a valid auth, otherwise false
     */
    private Boolean verifyClientAuth(DatagramPacket requestPacket)
    {
        SocketAddress socketaddress = requestPacket.getSocketAddress();

        if (!this.queryClients.containsKey(socketaddress))
        {
            return false;
        }
        else
        {
            byte[] abyte = requestPacket.getData();
            return this.queryClients.get(socketaddress).getRandomChallenge() == RConUtils.getBytesAsBEint(abyte, 7, requestPacket.getLength());
        }
    }

    /**
     * Sends an auth challenge DatagramPacket to the client and adds the client to the queryClients map
     */
    private void sendAuthChallenge(DatagramPacket requestPacket) throws IOException
    {
        QueryThread.Auth querythread$auth = new QueryThread.Auth(requestPacket);
        this.queryClients.put(requestPacket.getSocketAddress(), querythread$auth);
        this.sendResponsePacket(querythread$auth.getChallengeValue(), requestPacket);
    }

    /**
     * Removes all clients whose auth is no longer valid
     */
    private void cleanQueryClientsMap()
    {
        if (this.running)
        {
            long i = Util.milliTime();

            if (i >= this.lastAuthCheckTime + 30000L)
            {
                this.lastAuthCheckTime = i;
                this.queryClients.values().removeIf((p_232650_2_) ->
                {
                    return p_232650_2_.hasExpired(i);
                });
            }
        }
    }

    public void run()
    {
        field_232648_d_.info("Query running on {}:{}", this.serverHostname, this.queryPort);
        this.lastAuthCheckTime = Util.milliTime();
        DatagramPacket datagrampacket = new DatagramPacket(this.buffer, this.buffer.length);

        try
        {
            while (this.running)
            {
                try
                {
                    this.querySocket.receive(datagrampacket);
                    this.cleanQueryClientsMap();
                    this.parseIncomingPacket(datagrampacket);
                }
                catch (SocketTimeoutException sockettimeoutexception)
                {
                    this.cleanQueryClientsMap();
                }
                catch (PortUnreachableException portunreachableexception)
                {
                }
                catch (IOException ioexception)
                {
                    this.stopWithException(ioexception);
                }
            }
        }
        finally
        {
            field_232648_d_.debug("closeSocket: {}:{}", this.serverHostname, this.queryPort);
            this.querySocket.close();
        }
    }

    public boolean func_241832_a()
    {
        if (this.running)
        {
            return true;
        }
        else
        {
            return !this.initQuerySystem() ? false : super.func_241832_a();
        }
    }

    /**
     * Stops the query server and reports the given Exception
     */
    private void stopWithException(Exception exception)
    {
        if (this.running)
        {
            field_232648_d_.warn("Unexpected exception", (Throwable)exception);

            if (!this.initQuerySystem())
            {
                field_232648_d_.error("Failed to recover from exception, shutting down!");
                this.running = false;
            }
        }
    }

    /**
     * Initializes the query system by binding it to a port
     */
    private boolean initQuerySystem()
    {
        try
        {
            this.querySocket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.serverHostname));
            this.querySocket.setSoTimeout(500);
            return true;
        }
        catch (Exception exception)
        {
            field_232648_d_.warn("Unable to initialise query system on {}:{}", this.serverHostname, this.queryPort, exception);
            return false;
        }
    }

    static class Auth
    {
        private final long timestamp = (new Date()).getTime();
        private final int randomChallenge;
        private final byte[] requestId;
        private final byte[] challengeValue;
        private final String requestIdAsString;

        public Auth(DatagramPacket p_i231427_1_)
        {
            byte[] abyte = p_i231427_1_.getData();
            this.requestId = new byte[4];
            this.requestId[0] = abyte[3];
            this.requestId[1] = abyte[4];
            this.requestId[2] = abyte[5];
            this.requestId[3] = abyte[6];
            this.requestIdAsString = new String(this.requestId, StandardCharsets.UTF_8);
            this.randomChallenge = (new Random()).nextInt(16777216);
            this.challengeValue = String.format("\t%s%d\u0000", this.requestIdAsString, this.randomChallenge).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean hasExpired(long currentTime)
        {
            return this.timestamp < currentTime;
        }

        public int getRandomChallenge()
        {
            return this.randomChallenge;
        }

        public byte[] getChallengeValue()
        {
            return this.challengeValue;
        }

        public byte[] getRequestId()
        {
            return this.requestId;
        }
    }
}
