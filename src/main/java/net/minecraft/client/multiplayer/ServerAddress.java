package net.minecraft.client.multiplayer;

import com.mojang.datafixers.util.Pair;
import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class ServerAddress
{
    private final String ipAddress;
    private final int serverPort;

    private ServerAddress(String address, int port)
    {
        this.ipAddress = address;
        this.serverPort = port;
    }

    public String getIP()
    {
        try
        {
            return IDN.toASCII(this.ipAddress);
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            return "";
        }
    }

    public int getPort()
    {
        return this.serverPort;
    }

    public static ServerAddress fromString(String addrString)
    {
        if (addrString == null)
        {
            return null;
        }
        else
        {
            String[] astring = addrString.split(":");

            if (addrString.startsWith("["))
            {
                int i = addrString.indexOf("]");

                if (i > 0)
                {
                    String s = addrString.substring(1, i);
                    String s1 = addrString.substring(i + 1).trim();

                    if (s1.startsWith(":") && !s1.isEmpty())
                    {
                        s1 = s1.substring(1);
                        astring = new String[] {s, s1};
                    }
                    else
                    {
                        astring = new String[] {s};
                    }
                }
            }

            if (astring.length > 2)
            {
                astring = new String[] {addrString};
            }

            String s2 = astring[0];
            int j = astring.length > 1 ? getInt(astring[1], 25565) : 25565;

            if (j == 25565)
            {
                Pair<String, Integer> pair = func_241677_b_(s2);
                s2 = pair.getFirst();
                j = pair.getSecond();
            }

            return new ServerAddress(s2, j);
        }
    }

    private static Pair<String, Integer> func_241677_b_(String p_241677_0_)
    {
        try
        {
            String s = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            DirContext dircontext = new InitialDirContext(hashtable);
            Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + p_241677_0_, new String[] {"SRV"});
            Attribute attribute = attributes.get("srv");

            if (attribute != null)
            {
                String[] astring = attribute.get().toString().split(" ", 4);
                return Pair.of(astring[3], getInt(astring[2], 25565));
            }
        }
        catch (Throwable throwable)
        {
        }

        return Pair.of(p_241677_0_, 25565);
    }

    private static int getInt(String value, int defaultValue)
    {
        try
        {
            return Integer.parseInt(value.trim());
        }
        catch (Exception exception)
        {
            return defaultValue;
        }
    }
}
