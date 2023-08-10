package net.minecraft.network.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.dedicated.ServerProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainThread extends RConThread
{
    private static final Logger field_232652_d_ = LogManager.getLogger();
    private final ServerSocket serverSocket;
    private final String rconPassword;
    private final List<ClientThread> clientThreads = Lists.newArrayList();
    private final IServer field_232653_j_;

    private MainThread(IServer p_i241891_1_, ServerSocket p_i241891_2_, String p_i241891_3_)
    {
        super("RCON Listener");
        this.field_232653_j_ = p_i241891_1_;
        this.serverSocket = p_i241891_2_;
        this.rconPassword = p_i241891_3_;
    }

    /**
     * Cleans up the clientThreads map by removing client Threads that are not running
     */
    private void cleanClientThreadsMap()
    {
        this.clientThreads.removeIf((p_232654_0_) ->
        {
            return !p_232654_0_.isRunning();
        });
    }

    public void run()
    {
        try
        {
            while (this.running)
            {
                try
                {
                    Socket socket = this.serverSocket.accept();
                    ClientThread clientthread = new ClientThread(this.field_232653_j_, this.rconPassword, socket);
                    clientthread.func_241832_a();
                    this.clientThreads.add(clientthread);
                    this.cleanClientThreadsMap();
                }
                catch (SocketTimeoutException sockettimeoutexception)
                {
                    this.cleanClientThreadsMap();
                }
                catch (IOException ioexception)
                {
                    if (this.running)
                    {
                        field_232652_d_.info("IO exception: ", (Throwable)ioexception);
                    }
                }
            }
        }
        finally
        {
            this.func_232655_a_(this.serverSocket);
        }
    }

    @Nullable
    public static MainThread func_242130_a(IServer p_242130_0_)
    {
        ServerProperties serverproperties = p_242130_0_.getServerProperties();
        String s = p_242130_0_.getHostname();

        if (s.isEmpty())
        {
            s = "0.0.0.0";
        }

        int i = serverproperties.rconPort;

        if (0 < i && 65535 >= i)
        {
            String s1 = serverproperties.rconPassword;

            if (s1.isEmpty())
            {
                field_232652_d_.warn("No rcon password set in server.properties, rcon disabled!");
                return null;
            }
            else
            {
                try
                {
                    ServerSocket serversocket = new ServerSocket(i, 0, InetAddress.getByName(s));
                    serversocket.setSoTimeout(500);
                    MainThread mainthread = new MainThread(p_242130_0_, serversocket, s1);

                    if (!mainthread.func_241832_a())
                    {
                        return null;
                    }
                    else
                    {
                        field_232652_d_.info("RCON running on {}:{}", s, i);
                        return mainthread;
                    }
                }
                catch (IOException ioexception)
                {
                    field_232652_d_.warn("Unable to initialise RCON on {}:{}", s, i, ioexception);
                    return null;
                }
            }
        }
        else
        {
            field_232652_d_.warn("Invalid rcon port {} found in server.properties, rcon disabled!", (int)i);
            return null;
        }
    }

    public void func_219591_b()
    {
        this.running = false;
        this.func_232655_a_(this.serverSocket);
        super.func_219591_b();

        for (ClientThread clientthread : this.clientThreads)
        {
            if (clientthread.isRunning())
            {
                clientthread.func_219591_b();
            }
        }

        this.clientThreads.clear();
    }

    private void func_232655_a_(ServerSocket p_232655_1_)
    {
        field_232652_d_.debug("closeSocket: {}", (Object)p_232655_1_);

        try
        {
            p_232655_1_.close();
        }
        catch (IOException ioexception)
        {
            field_232652_d_.warn("Failed to close socket", (Throwable)ioexception);
        }
    }
}
