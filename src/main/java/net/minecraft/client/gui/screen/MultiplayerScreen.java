package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiplayerScreen extends Screen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerPinger oldServerPinger = new ServerPinger();
    private final Screen parentScreen;
    protected ServerSelectionList serverListSelector;
    private ServerList savedServerList;
    private Button btnEditServer;
    private Button btnSelectServer;
    private Button btnDeleteServer;
    private List<ITextComponent> hoveringText;
    private ServerData selectedServer;
    private LanServerDetector.LanServerList lanServerList;
    private LanServerDetector.LanServerFindThread lanServerDetector;
    private boolean initialized;

    public MultiplayerScreen(Screen parentScreen)
    {
        super(new TranslationTextComponent("multiplayer.title"));
        this.parentScreen = parentScreen;
    }

    protected void init()
    {
        super.init();
        this.mc.keyboardListener.enableRepeatEvents(true);

        if (this.initialized)
        {
            this.serverListSelector.updateSize(this.width, this.height, 32, this.height - 64);
        }
        else
        {
            this.initialized = true;
            this.savedServerList = new ServerList(this.mc);
            this.savedServerList.loadServerList();
            this.lanServerList = new LanServerDetector.LanServerList();

            try
            {
                this.lanServerDetector = new LanServerDetector.LanServerFindThread(this.lanServerList);
                this.lanServerDetector.start();
            }
            catch (Exception exception)
            {
                LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
            }

            this.serverListSelector = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
            this.serverListSelector.updateOnlineServers(this.savedServerList);
        }

        this.children.add(this.serverListSelector);
        this.btnSelectServer = this.addButton(new Button(this.width / 2 - 154, this.height - 52, 100, 20, new TranslationTextComponent("selectServer.select"), (p_214293_1_) ->
        {
            this.connectToSelected();
        }));
        this.addButton(new Button(this.width / 2 - 50, this.height - 52, 100, 20, new TranslationTextComponent("selectServer.direct"), (p_214286_1_) ->
        {
            this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
            this.mc.displayGuiScreen(new ServerListScreen(this, this::func_214290_d, this.selectedServer));
        }));
        this.addButton(new Button(this.width / 2 + 4 + 50, this.height - 52, 100, 20, new TranslationTextComponent("selectServer.add"), (p_214288_1_) ->
        {
            this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
            this.mc.displayGuiScreen(new AddServerScreen(this, this::func_214284_c, this.selectedServer));
        }));
        this.btnEditServer = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 70, 20, new TranslationTextComponent("selectServer.edit"), (p_214283_1_) ->
        {
            ServerSelectionList.Entry serverselectionlist$entry = this.serverListSelector.getSelected();

            if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry)
            {
                ServerData serverdata = ((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData();
                this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
                this.selectedServer.copyFrom(serverdata);
                this.mc.displayGuiScreen(new AddServerScreen(this, this::func_214292_b, this.selectedServer));
            }
        }));
        this.btnDeleteServer = this.addButton(new Button(this.width / 2 - 74, this.height - 28, 70, 20, new TranslationTextComponent("selectServer.delete"), (p_214294_1_) ->
        {
            ServerSelectionList.Entry serverselectionlist$entry = this.serverListSelector.getSelected();

            if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry)
            {
                String s = ((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData().serverName;

                if (s != null)
                {
                    ITextComponent itextcomponent = new TranslationTextComponent("selectServer.deleteQuestion");
                    ITextComponent itextcomponent1 = new TranslationTextComponent("selectServer.deleteWarning", s);
                    ITextComponent itextcomponent2 = new TranslationTextComponent("selectServer.deleteButton");
                    ITextComponent itextcomponent3 = DialogTexts.GUI_CANCEL;
                    this.mc.displayGuiScreen(new ConfirmScreen(this::func_214285_a, itextcomponent, itextcomponent1, itextcomponent2, itextcomponent3));
                }
            }
        }));
        this.addButton(new Button(this.width / 2 + 4, this.height - 28, 70, 20, new TranslationTextComponent("selectServer.refresh"), (p_214291_1_) ->
        {
            this.refreshServerList();
        }));
        this.addButton(new Button(this.width / 2 + 4 + 76, this.height - 28, 75, 20, DialogTexts.GUI_CANCEL, (p_214289_1_) ->
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }));
        this.func_214295_b();
    }

    public void tick()
    {
        super.tick();

        if (this.lanServerList.getWasUpdated())
        {
            List<LanServerInfo> list = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.serverListSelector.updateNetworkServers(list);
        }

        this.oldServerPinger.pingPendingNetworks();
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);

        if (this.lanServerDetector != null)
        {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }

        this.oldServerPinger.clearPendingNetworks();
    }

    private void refreshServerList()
    {
        this.mc.displayGuiScreen(new MultiplayerScreen(this.parentScreen));
    }

    private void func_214285_a(boolean p_214285_1_)
    {
        ServerSelectionList.Entry serverselectionlist$entry = this.serverListSelector.getSelected();

        if (p_214285_1_ && serverselectionlist$entry instanceof ServerSelectionList.NormalEntry)
        {
            this.savedServerList.func_217506_a(((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData());
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelected((ServerSelectionList.Entry)null);
            this.serverListSelector.updateOnlineServers(this.savedServerList);
        }

        this.mc.displayGuiScreen(this);
    }

    private void func_214292_b(boolean p_214292_1_)
    {
        ServerSelectionList.Entry serverselectionlist$entry = this.serverListSelector.getSelected();

        if (p_214292_1_ && serverselectionlist$entry instanceof ServerSelectionList.NormalEntry)
        {
            ServerData serverdata = ((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData();
            serverdata.serverName = this.selectedServer.serverName;
            serverdata.serverIP = this.selectedServer.serverIP;
            serverdata.copyFrom(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.updateOnlineServers(this.savedServerList);
        }

        this.mc.displayGuiScreen(this);
    }

    private void func_214284_c(boolean p_214284_1_)
    {
        if (p_214284_1_)
        {
            this.savedServerList.addServerData(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelected((ServerSelectionList.Entry)null);
            this.serverListSelector.updateOnlineServers(this.savedServerList);
        }

        this.mc.displayGuiScreen(this);
    }

    private void func_214290_d(boolean p_214290_1_)
    {
        if (p_214290_1_)
        {
            this.connectToServer(this.selectedServer);
        }
        else
        {
            this.mc.displayGuiScreen(this);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else if (keyCode == 294)
        {
            this.refreshServerList();
            return true;
        }
        else if (this.serverListSelector.getSelected() != null)
        {
            if (keyCode != 257 && keyCode != 335)
            {
                return this.serverListSelector.keyPressed(keyCode, scanCode, modifiers);
            }
            else
            {
                this.connectToSelected();
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.hoveringText = null;
        this.renderBackground(matrixStack);
        this.serverListSelector.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.hoveringText != null)
        {
            this.func_243308_b(matrixStack, this.hoveringText, mouseX, mouseY);
        }
    }

    public void connectToSelected()
    {
        ServerSelectionList.Entry serverselectionlist$entry = this.serverListSelector.getSelected();

        if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry)
        {
            this.connectToServer(((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData());
        }
        else if (serverselectionlist$entry instanceof ServerSelectionList.LanDetectedEntry)
        {
            LanServerInfo lanserverinfo = ((ServerSelectionList.LanDetectedEntry)serverselectionlist$entry).getServerData();
            this.connectToServer(new ServerData(lanserverinfo.getServerMotd(), lanserverinfo.getServerIpPort(), true));
        }
    }

    private void connectToServer(ServerData server)
    {
        this.mc.displayGuiScreen(new ConnectingScreen(this, this.mc, server));
    }

    public void func_214287_a(ServerSelectionList.Entry p_214287_1_)
    {
        this.serverListSelector.setSelected(p_214287_1_);
        this.func_214295_b();
    }

    protected void func_214295_b()
    {
        this.btnSelectServer.active = false;
        this.btnEditServer.active = false;
        this.btnDeleteServer.active = false;
        ServerSelectionList.Entry serverselectionlist$entry = this.serverListSelector.getSelected();

        if (serverselectionlist$entry != null && !(serverselectionlist$entry instanceof ServerSelectionList.LanScanEntry))
        {
            this.btnSelectServer.active = true;

            if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry)
            {
                this.btnEditServer.active = true;
                this.btnDeleteServer.active = true;
            }
        }
    }

    public ServerPinger getOldServerPinger()
    {
        return this.oldServerPinger;
    }

    public void func_238854_b_(List<ITextComponent> p_238854_1_)
    {
        this.hoveringText = p_238854_1_;
    }

    public ServerList getServerList()
    {
        return this.savedServerList;
    }
}
