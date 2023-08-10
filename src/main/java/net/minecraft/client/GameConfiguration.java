package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.FolderResourceIndex;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.util.Session;

public class GameConfiguration
{
    public final GameConfiguration.UserInformation userInfo;
    public final ScreenSize displayInfo;
    public final GameConfiguration.FolderInformation folderInfo;
    public final GameConfiguration.GameInformation gameInfo;
    public final GameConfiguration.ServerInformation serverInfo;

    public GameConfiguration(GameConfiguration.UserInformation userInfo, ScreenSize screenSize, GameConfiguration.FolderInformation folderInfo, GameConfiguration.GameInformation gameInfo, GameConfiguration.ServerInformation serverInfo)
    {
        this.userInfo = userInfo;
        this.displayInfo = screenSize;
        this.folderInfo = folderInfo;
        this.gameInfo = gameInfo;
        this.serverInfo = serverInfo;
    }

    public static class FolderInformation
    {
        public final File gameDir;
        public final File resourcePacksDir;
        public final File assetsDir;
        @Nullable
        public final String assetIndex;

        public FolderInformation(File mcDataDirIn, File resourcePacksDirIn, File assetsDirIn, @Nullable String assetIndexIn)
        {
            this.gameDir = mcDataDirIn;
            this.resourcePacksDir = resourcePacksDirIn;
            this.assetsDir = assetsDirIn;
            this.assetIndex = assetIndexIn;
        }

        public ResourceIndex getAssetsIndex()
        {
            return (ResourceIndex)(this.assetIndex == null ? new FolderResourceIndex(this.assetsDir) : new ResourceIndex(this.assetsDir, this.assetIndex));
        }
    }

    public static class GameInformation
    {
        public final boolean isDemo;
        public final String version;
        public final String versionType;
        public final boolean disableMultiplayer;
        public final boolean disableChat;

        public GameInformation(boolean isDemo, String version, String versionType, boolean disableMultiplayer, boolean disableChat)
        {
            this.isDemo = isDemo;
            this.version = version;
            this.versionType = versionType;
            this.disableMultiplayer = disableMultiplayer;
            this.disableChat = disableChat;
        }
    }

    public static class ServerInformation
    {
        @Nullable
        public final String serverName;
        public final int serverPort;

        public ServerInformation(@Nullable String serverNameIn, int serverPortIn)
        {
            this.serverName = serverNameIn;
            this.serverPort = serverPortIn;
        }
    }

    public static class UserInformation
    {
        public final Session session;
        public final PropertyMap userProperties;
        public final PropertyMap profileProperties;
        public final Proxy proxy;

        public UserInformation(Session sessionIn, PropertyMap userPropertiesIn, PropertyMap profilePropertiesIn, Proxy proxyIn)
        {
            this.session = sessionIn;
            this.userProperties = userPropertiesIn;
            this.profileProperties = profilePropertiesIn;
            this.proxy = proxyIn;
        }
    }
}
