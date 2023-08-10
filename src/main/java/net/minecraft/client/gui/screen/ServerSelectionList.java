package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerSelectionList extends ExtendedList<ServerSelectionList.Entry>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadPoolExecutor field_214358_b = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
    private static final ResourceLocation field_214359_c = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation field_214360_d = new ResourceLocation("textures/gui/server_selection.png");
    private static final ITextComponent field_243365_r = new TranslationTextComponent("lanServer.scanning");
    private static final ITextComponent field_243366_s = (new TranslationTextComponent("multiplayer.status.cannot_resolve")).mergeStyle(TextFormatting.DARK_RED);
    private static final ITextComponent field_243367_t = (new TranslationTextComponent("multiplayer.status.cannot_connect")).mergeStyle(TextFormatting.DARK_RED);
    private static final ITextComponent field_244607_u = new TranslationTextComponent("multiplayer.status.incompatible");
    private static final ITextComponent field_243370_w = new TranslationTextComponent("multiplayer.status.no_connection");
    private static final ITextComponent field_243371_x = new TranslationTextComponent("multiplayer.status.pinging");
    private final MultiplayerScreen owner;
    private final List<ServerSelectionList.NormalEntry> serverListInternet = Lists.newArrayList();
    private final ServerSelectionList.Entry lanScanEntry = new ServerSelectionList.LanScanEntry();
    private final List<ServerSelectionList.LanDetectedEntry> serverListLan = Lists.newArrayList();

    public ServerSelectionList(MultiplayerScreen ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    private void setList()
    {
        this.clearEntries();
        this.serverListInternet.forEach(this::addEntry);
        this.addEntry(this.lanScanEntry);
        this.serverListLan.forEach(this::addEntry);
    }

    public void setSelected(@Nullable ServerSelectionList.Entry entry)
    {
        super.setSelected(entry);

        if (this.getSelected() instanceof ServerSelectionList.NormalEntry)
        {
            NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", ((ServerSelectionList.NormalEntry)this.getSelected()).server.serverName)).getString());
        }

        this.owner.func_214295_b();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        ServerSelectionList.Entry serverselectionlist$entry = this.getSelected();
        return serverselectionlist$entry != null && serverselectionlist$entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void moveSelection(AbstractList.Ordering p_241219_1_)
    {
        this.func_241572_a_(p_241219_1_, (p_241612_0_) ->
        {
            return !(p_241612_0_ instanceof ServerSelectionList.LanScanEntry);
        });
    }

    public void updateOnlineServers(ServerList p_148195_1_)
    {
        this.serverListInternet.clear();

        for (int i = 0; i < p_148195_1_.countServers(); ++i)
        {
            this.serverListInternet.add(new ServerSelectionList.NormalEntry(this.owner, p_148195_1_.getServerData(i)));
        }

        this.setList();
    }

    public void updateNetworkServers(List<LanServerInfo> p_148194_1_)
    {
        this.serverListLan.clear();

        for (LanServerInfo lanserverinfo : p_148194_1_)
        {
            this.serverListLan.add(new ServerSelectionList.LanDetectedEntry(this.owner, lanserverinfo));
        }

        this.setList();
    }

    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 30;
    }

    public int getRowWidth()
    {
        return super.getRowWidth() + 85;
    }

    protected boolean isFocused()
    {
        return this.owner.getListener() == this;
    }

    public abstract static class Entry extends ExtendedList.AbstractListEntry<ServerSelectionList.Entry>
    {
    }

    public static class LanDetectedEntry extends ServerSelectionList.Entry
    {
        private static final ITextComponent field_243386_c = new TranslationTextComponent("lanServer.title");
        private static final ITextComponent field_243387_d = new TranslationTextComponent("selectServer.hiddenAddress");
        private final MultiplayerScreen screen;
        protected final Minecraft mc;
        protected final LanServerInfo serverData;
        private long lastClickTime;

        protected LanDetectedEntry(MultiplayerScreen p_i47141_1_, LanServerInfo p_i47141_2_)
        {
            this.screen = p_i47141_1_;
            this.serverData = p_i47141_2_;
            this.mc = Minecraft.getInstance();
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            this.mc.fontRenderer.func_243248_b(p_230432_1_, field_243386_c, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 1), 16777215);
            this.mc.fontRenderer.drawString(p_230432_1_, this.serverData.getServerMotd(), (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12), 8421504);

            if (this.mc.gameSettings.hideServerAddress)
            {
                this.mc.fontRenderer.func_243248_b(p_230432_1_, field_243387_d, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12 + 11), 3158064);
            }
            else
            {
                this.mc.fontRenderer.drawString(p_230432_1_, this.serverData.getServerIpPort(), (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12 + 11), 3158064);
            }
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            this.screen.func_214287_a(this);

            if (Util.milliTime() - this.lastClickTime < 250L)
            {
                this.screen.connectToSelected();
            }

            this.lastClickTime = Util.milliTime();
            return false;
        }

        public LanServerInfo getServerData()
        {
            return this.serverData;
        }
    }

    public static class LanScanEntry extends ServerSelectionList.Entry
    {
        private final Minecraft mc = Minecraft.getInstance();

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            int i = p_230432_3_ + p_230432_6_ / 2 - 9 / 2;
            this.mc.fontRenderer.func_243248_b(p_230432_1_, ServerSelectionList.field_243365_r, (float)(this.mc.currentScreen.width / 2 - this.mc.fontRenderer.getStringPropertyWidth(ServerSelectionList.field_243365_r) / 2), (float)i, 16777215);
            String s;

            switch ((int)(Util.milliTime() / 300L % 4L))
            {
                case 0:
                default:
                    s = "O o o";
                    break;

                case 1:
                case 3:
                    s = "o O o";
                    break;

                case 2:
                    s = "o o O";
            }

            this.mc.fontRenderer.drawString(p_230432_1_, s, (float)(this.mc.currentScreen.width / 2 - this.mc.fontRenderer.getStringWidth(s) / 2), (float)(i + 9), 8421504);
        }
    }

    public class NormalEntry extends ServerSelectionList.Entry
    {
        private final MultiplayerScreen owner;
        private final Minecraft mc;
        private final ServerData server;
        private final ResourceLocation serverIcon;
        private String lastIconB64;
        private DynamicTexture icon;
        private long lastClickTime;

        protected NormalEntry(MultiplayerScreen p_i50669_2_, ServerData p_i50669_3_)
        {
            this.owner = p_i50669_2_;
            this.server = p_i50669_3_;
            this.mc = Minecraft.getInstance();
            this.serverIcon = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(p_i50669_3_.serverIP) + "/icon");
            this.icon = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
        }

        public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_)
        {
            if (!this.server.pinged)
            {
                this.server.pinged = true;
                this.server.pingToServer = -2L;
                this.server.serverMOTD = StringTextComponent.EMPTY;
                this.server.populationInfo = StringTextComponent.EMPTY;
                ServerSelectionList.field_214358_b.submit(() ->
                {
                    try {
                        this.owner.getOldServerPinger().ping(this.server, () -> {
                            this.mc.execute(this::func_241613_a_);
                        });
                    }
                    catch (UnknownHostException unknownhostexception)
                    {
                        this.server.pingToServer = -1L;
                        this.server.serverMOTD = ServerSelectionList.field_243366_s;
                    }
                    catch (Exception exception)
                    {
                        this.server.pingToServer = -1L;
                        this.server.serverMOTD = ServerSelectionList.field_243367_t;
                    }
                });
            }

            boolean flag = this.server.version != SharedConstants.getVersion().getProtocolVersion();
            this.mc.fontRenderer.drawString(p_230432_1_, this.server.serverName, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 1), 16777215);
            List<IReorderingProcessor> list = this.mc.fontRenderer.trimStringToWidth(this.server.serverMOTD, p_230432_5_ - 32 - 2);

            for (int i = 0; i < Math.min(list.size(), 2); ++i)
            {
                this.mc.fontRenderer.func_238422_b_(p_230432_1_, list.get(i), (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12 + 9 * i), 8421504);
            }

            ITextComponent itextcomponent1 = (ITextComponent)(flag ? this.server.gameVersion.deepCopy().mergeStyle(TextFormatting.RED) : this.server.populationInfo);
            int j = this.mc.fontRenderer.getStringPropertyWidth(itextcomponent1);
            this.mc.fontRenderer.func_243248_b(p_230432_1_, itextcomponent1, (float)(p_230432_4_ + p_230432_5_ - j - 15 - 2), (float)(p_230432_3_ + 1), 8421504);
            int k = 0;
            int l;
            List<ITextComponent> list1;
            ITextComponent itextcomponent;

            if (flag)
            {
                l = 5;
                itextcomponent = ServerSelectionList.field_244607_u;
                list1 = this.server.playerList;
            }
            else if (this.server.pinged && this.server.pingToServer != -2L)
            {
                if (this.server.pingToServer < 0L)
                {
                    l = 5;
                }
                else if (this.server.pingToServer < 150L)
                {
                    l = 0;
                }
                else if (this.server.pingToServer < 300L)
                {
                    l = 1;
                }
                else if (this.server.pingToServer < 600L)
                {
                    l = 2;
                }
                else if (this.server.pingToServer < 1000L)
                {
                    l = 3;
                }
                else
                {
                    l = 4;
                }

                if (this.server.pingToServer < 0L)
                {
                    itextcomponent = ServerSelectionList.field_243370_w;
                    list1 = Collections.emptyList();
                }
                else
                {
                    itextcomponent = new TranslationTextComponent("multiplayer.status.ping", this.server.pingToServer);
                    list1 = this.server.playerList;
                }
            }
            else
            {
                k = 1;
                l = (int)(Util.milliTime() / 100L + (long)(p_230432_2_ * 2) & 7L);

                if (l > 4)
                {
                    l = 8 - l;
                }

                itextcomponent = ServerSelectionList.field_243371_x;
                list1 = Collections.emptyList();
            }

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            AbstractGui.blit(p_230432_1_, p_230432_4_ + p_230432_5_ - 15, p_230432_3_, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256, 256);
            String s = this.server.getBase64EncodedIconData();

            if (!Objects.equals(s, this.lastIconB64))
            {
                if (this.func_241614_a_(s))
                {
                    this.lastIconB64 = s;
                }
                else
                {
                    this.server.setBase64EncodedIconData((String)null);
                    this.func_241613_a_();
                }
            }

            if (this.icon != null)
            {
                this.func_238859_a_(p_230432_1_, p_230432_4_, p_230432_3_, this.serverIcon);
            }
            else
            {
                this.func_238859_a_(p_230432_1_, p_230432_4_, p_230432_3_, ServerSelectionList.field_214359_c);
            }

            int i1 = p_230432_7_ - p_230432_4_;
            int j1 = p_230432_8_ - p_230432_3_;

            if (i1 >= p_230432_5_ - 15 && i1 <= p_230432_5_ - 5 && j1 >= 0 && j1 <= 8)
            {
                this.owner.func_238854_b_(Collections.singletonList(itextcomponent));
            }
            else if (i1 >= p_230432_5_ - j - 15 - 2 && i1 <= p_230432_5_ - 15 - 2 && j1 >= 0 && j1 <= 8)
            {
                this.owner.func_238854_b_(list1);
            }

            if (this.mc.gameSettings.touchscreen || p_230432_9_)
            {
                this.mc.getTextureManager().bindTexture(ServerSelectionList.field_214360_d);
                AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + 32, p_230432_3_ + 32, -1601138544);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                int k1 = p_230432_7_ - p_230432_4_;
                int l1 = p_230432_8_ - p_230432_3_;

                if (this.canJoin())
                {
                    if (k1 < 32 && k1 > 16)
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 32.0F, 32, 32, 256, 256);
                    }
                    else
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 0.0F, 32, 32, 256, 256);
                    }
                }

                if (p_230432_2_ > 0)
                {
                    if (k1 < 16 && l1 < 16)
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 32.0F, 32, 32, 256, 256);
                    }
                    else
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 0.0F, 32, 32, 256, 256);
                    }
                }

                if (p_230432_2_ < this.owner.getServerList().countServers() - 1)
                {
                    if (k1 < 16 && l1 > 16)
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 32.0F, 32, 32, 256, 256);
                    }
                    else
                    {
                        AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 0.0F, 32, 32, 256, 256);
                    }
                }
            }
        }

        public void func_241613_a_()
        {
            this.owner.getServerList().saveServerList();
        }

        protected void func_238859_a_(MatrixStack p_238859_1_, int p_238859_2_, int p_238859_3_, ResourceLocation p_238859_4_)
        {
            this.mc.getTextureManager().bindTexture(p_238859_4_);
            RenderSystem.enableBlend();
            AbstractGui.blit(p_238859_1_, p_238859_2_, p_238859_3_, 0.0F, 0.0F, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }

        private boolean canJoin()
        {
            return true;
        }

        private boolean func_241614_a_(@Nullable String p_241614_1_)
        {
            if (p_241614_1_ == null)
            {
                this.mc.getTextureManager().deleteTexture(this.serverIcon);

                if (this.icon != null && this.icon.getTextureData() != null)
                {
                    this.icon.getTextureData().close();
                }

                this.icon = null;
            }
            else
            {
                try
                {
                    NativeImage nativeimage = NativeImage.readBase64(p_241614_1_);
                    Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
                    Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");

                    if (this.icon == null)
                    {
                        this.icon = new DynamicTexture(nativeimage);
                    }
                    else
                    {
                        this.icon.setTextureData(nativeimage);
                        this.icon.updateDynamicTexture();
                    }

                    this.mc.getTextureManager().loadTexture(this.serverIcon, this.icon);
                }
                catch (Throwable throwable)
                {
                    ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.server.serverName, this.server.serverIP, throwable);
                    return false;
                }
            }

            return true;
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            if (Screen.hasShiftDown())
            {
                ServerSelectionList serverselectionlist = this.owner.serverListSelector;
                int i = serverselectionlist.getEventListeners().indexOf(this);

                if (keyCode == 264 && i < this.owner.getServerList().countServers() - 1 || keyCode == 265 && i > 0)
                {
                    this.func_228196_a_(i, keyCode == 264 ? i + 1 : i - 1);
                    return true;
                }
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private void func_228196_a_(int p_228196_1_, int p_228196_2_)
        {
            this.owner.getServerList().swapServers(p_228196_1_, p_228196_2_);
            this.owner.serverListSelector.updateOnlineServers(this.owner.getServerList());
            ServerSelectionList.Entry serverselectionlist$entry = this.owner.serverListSelector.getEventListeners().get(p_228196_2_);
            this.owner.serverListSelector.setSelected(serverselectionlist$entry);
            ServerSelectionList.this.ensureVisible(serverselectionlist$entry);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            double d0 = mouseX - (double)ServerSelectionList.this.getRowLeft();
            double d1 = mouseY - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.getEventListeners().indexOf(this));

            if (d0 <= 32.0D)
            {
                if (d0 < 32.0D && d0 > 16.0D && this.canJoin())
                {
                    this.owner.func_214287_a(this);
                    this.owner.connectToSelected();
                    return true;
                }

                int i = this.owner.serverListSelector.getEventListeners().indexOf(this);

                if (d0 < 16.0D && d1 < 16.0D && i > 0)
                {
                    this.func_228196_a_(i, i - 1);
                    return true;
                }

                if (d0 < 16.0D && d1 > 16.0D && i < this.owner.getServerList().countServers() - 1)
                {
                    this.func_228196_a_(i, i + 1);
                    return true;
                }
            }

            this.owner.func_214287_a(this);

            if (Util.milliTime() - this.lastClickTime < 250L)
            {
                this.owner.connectToSelected();
            }

            this.lastClickTime = Util.milliTime();
            return false;
        }

        public ServerData getServerData()
        {
            return this.server;
        }
    }
}
