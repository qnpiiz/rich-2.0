package net.minecraft.realms;

import net.minecraft.client.multiplayer.ServerAddress;

public class RealmsServerAddress
{
    private final String field_230727_a_;
    private final int field_230728_b_;

    protected RealmsServerAddress(String hostIn, int portIn)
    {
        this.field_230727_a_ = hostIn;
        this.field_230728_b_ = portIn;
    }

    public String func_231412_a_()
    {
        return this.field_230727_a_;
    }

    public int func_231414_b_()
    {
        return this.field_230728_b_;
    }

    public static RealmsServerAddress func_231413_a_(String p_231413_0_)
    {
        ServerAddress serveraddress = ServerAddress.fromString(p_231413_0_);
        return new RealmsServerAddress(serveraddress.getIP(), serveraddress.getPort());
    }
}
