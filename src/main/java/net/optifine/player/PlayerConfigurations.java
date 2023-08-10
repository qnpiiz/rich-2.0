package net.optifine.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.optifine.http.FileDownloadThread;
import net.optifine.http.HttpUtils;

public class PlayerConfigurations
{
    private static Map mapConfigurations = null;
    private static boolean reloadPlayerItems = Boolean.getBoolean("player.models.reload");
    private static long timeReloadPlayerItemsMs = System.currentTimeMillis();

    public static void renderPlayerItems(BipedModel modelBiped, AbstractClientPlayerEntity player, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn)
    {
        PlayerConfiguration playerconfiguration = getPlayerConfiguration(player);

        if (playerconfiguration != null)
        {
            playerconfiguration.renderPlayerItems(modelBiped, player, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        }
    }

    public static synchronized PlayerConfiguration getPlayerConfiguration(AbstractClientPlayerEntity player)
    {
        if (reloadPlayerItems && System.currentTimeMillis() > timeReloadPlayerItemsMs + 5000L)
        {
            AbstractClientPlayerEntity abstractclientplayerentity = Minecraft.getInstance().player;

            if (abstractclientplayerentity != null)
            {
                setPlayerConfiguration(abstractclientplayerentity.getNameClear(), (PlayerConfiguration)null);
                timeReloadPlayerItemsMs = System.currentTimeMillis();
            }
        }

        String s1 = player.getNameClear();

        if (s1 == null)
        {
            return null;
        }
        else
        {
            PlayerConfiguration playerconfiguration = (PlayerConfiguration)getMapConfigurations().get(s1);

            if (playerconfiguration == null)
            {
                playerconfiguration = new PlayerConfiguration();
                getMapConfigurations().put(s1, playerconfiguration);
                PlayerConfigurationReceiver playerconfigurationreceiver = new PlayerConfigurationReceiver(s1);
                String s = HttpUtils.getPlayerItemsUrl() + "/users/" + s1 + ".cfg";
                FileDownloadThread filedownloadthread = new FileDownloadThread(s, playerconfigurationreceiver);
                filedownloadthread.start();
            }

            return playerconfiguration;
        }
    }

    public static synchronized void setPlayerConfiguration(String player, PlayerConfiguration pc)
    {
        getMapConfigurations().put(player, pc);
    }

    private static Map getMapConfigurations()
    {
        if (mapConfigurations == null)
        {
            mapConfigurations = new HashMap();
        }

        return mapConfigurations;
    }
}
