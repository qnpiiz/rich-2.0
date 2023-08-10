package net.minecraft.network;

import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RateLimitedNetworkManager extends NetworkManager
{
    private static final Logger field_244274_g = LogManager.getLogger();
    private static final ITextComponent field_244275_h = new TranslationTextComponent("disconnect.exceeded_packet_rate");
    private final int field_244276_i;

    public RateLimitedNetworkManager(int p_i242078_1_)
    {
        super(PacketDirection.SERVERBOUND);
        this.field_244276_i = p_i242078_1_;
    }

    protected void func_241877_b()
    {
        super.func_241877_b();
        float f = this.getPacketsReceived();

        if (f > (float)this.field_244276_i)
        {
            field_244274_g.warn("Player exceeded rate-limit (sent {} packets per second)", (float)f);
            this.sendPacket(new SDisconnectPacket(field_244275_h), (p_244277_1_) ->
            {
                this.closeChannel(field_244275_h);
            });
            this.disableAutoRead();
        }
    }
}
