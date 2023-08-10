package net.minecraft.network.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientThread extends RConThread
{
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean loggedIn;
    private final Socket clientSocket;
    private final byte[] buffer = new byte[1460];
    private final String rconPassword;
    private final IServer field_232651_i_;

    ClientThread(IServer p_i50687_1_, String p_i50687_2_, Socket p_i50687_3_)
    {
        super("RCON Client " + p_i50687_3_.getInetAddress());
        this.field_232651_i_ = p_i50687_1_;
        this.clientSocket = p_i50687_3_;

        try
        {
            this.clientSocket.setSoTimeout(0);
        }
        catch (Exception exception)
        {
            this.running = false;
        }

        this.rconPassword = p_i50687_2_;
    }

    public void run()
    {
        try
        {
            while (true)
            {
                if (!this.running)
                {
                    return;
                }

                BufferedInputStream bufferedinputstream = new BufferedInputStream(this.clientSocket.getInputStream());
                int i = bufferedinputstream.read(this.buffer, 0, 1460);

                if (10 <= i)
                {
                    int j = 0;
                    int k = RConUtils.getBytesAsLEInt(this.buffer, 0, i);

                    if (k != i - 4)
                    {
                        return;
                    }

                    j = j + 4;
                    int l = RConUtils.getBytesAsLEInt(this.buffer, j, i);
                    j = j + 4;
                    int i1 = RConUtils.getRemainingBytesAsLEInt(this.buffer, j);
                    j = j + 4;

                    switch (i1)
                    {
                        case 2:
                            if (this.loggedIn)
                            {
                                String s1 = RConUtils.getBytesAsString(this.buffer, j, i);

                                try
                                {
                                    this.sendMultipacketResponse(l, this.field_232651_i_.handleRConCommand(s1));
                                }
                                catch (Exception exception)
                                {
                                    this.sendMultipacketResponse(l, "Error executing: " + s1 + " (" + exception.getMessage() + ")");
                                }

                                continue;
                            }

                            this.sendLoginFailedResponse();
                            continue;

                        case 3:
                            String s = RConUtils.getBytesAsString(this.buffer, j, i);
                            int j1 = j + s.length();

                            if (!s.isEmpty() && s.equals(this.rconPassword))
                            {
                                this.loggedIn = true;
                                this.sendResponse(l, 2, "");
                                continue;
                            }

                            this.loggedIn = false;
                            this.sendLoginFailedResponse();
                            continue;

                        default:
                            this.sendMultipacketResponse(l, String.format("Unknown request %s", Integer.toHexString(i1)));
                            continue;
                    }
                }

                return;
            }
        }
        catch (IOException ioexception)
        {
            return;
        }
        catch (Exception exception1)
        {
            LOGGER.error("Exception whilst parsing RCON input", (Throwable)exception1);
            return;
        }
        finally
        {
            this.closeSocket();
            LOGGER.info("Thread {} shutting down", (Object)this.threadName);
            this.running = false;
        }
    }

    /**
     * Sends the given response message to the client
     */
    private void sendResponse(int p_72654_1_, int p_72654_2_, String message) throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(1248);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        byte[] abyte = message.getBytes(StandardCharsets.UTF_8);
        dataoutputstream.writeInt(Integer.reverseBytes(abyte.length + 10));
        dataoutputstream.writeInt(Integer.reverseBytes(p_72654_1_));
        dataoutputstream.writeInt(Integer.reverseBytes(p_72654_2_));
        dataoutputstream.write(abyte);
        dataoutputstream.write(0);
        dataoutputstream.write(0);
        this.clientSocket.getOutputStream().write(bytearrayoutputstream.toByteArray());
    }

    /**
     * Sends the standard RCon 'authorization failed' response packet
     */
    private void sendLoginFailedResponse() throws IOException
    {
        this.sendResponse(-1, 2, "");
    }

    /**
     * Splits the response message into individual packets and sends each one
     */
    private void sendMultipacketResponse(int p_72655_1_, String p_72655_2_) throws IOException
    {
        int i = p_72655_2_.length();

        do
        {
            int j = 4096 <= i ? 4096 : i;
            this.sendResponse(p_72655_1_, 0, p_72655_2_.substring(0, j));
            p_72655_2_ = p_72655_2_.substring(j);
            i = p_72655_2_.length();
        }
        while (0 != i);
    }

    public void func_219591_b()
    {
        this.running = false;
        this.closeSocket();
        super.func_219591_b();
    }

    /**
     * Closes the client socket
     */
    private void closeSocket()
    {
        try
        {
            this.clientSocket.close();
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Failed to close socket", (Throwable)ioexception);
        }
    }
}
