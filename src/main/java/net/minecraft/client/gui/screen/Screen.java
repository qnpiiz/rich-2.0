package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Screen extends FocusableGui implements IScreen, IRenderable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
    protected final ITextComponent title;
    protected final List<IGuiEventListener> children = Lists.newArrayList();
    @Nullable
    protected Minecraft mc;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<Widget> buttons = Lists.newArrayList();
    public boolean passEvents;
    protected FontRenderer font;
    private URI clickedLink;

    protected Screen(ITextComponent titleIn)
    {
        this.title = titleIn;
    }

    public ITextComponent getTitle()
    {
        return this.title;
    }

    public String getNarrationMessage()
    {
        return this.getTitle().getString();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        for (int i = 0; i < this.buttons.size(); ++i)
        {
            this.buttons.get(i).render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256 && this.shouldCloseOnEsc())
        {
            this.closeScreen();
            return true;
        }
        else if (keyCode == 258)
        {
            boolean flag = !hasShiftDown();

            if (!this.changeFocus(flag))
            {
                this.changeFocus(flag);
            }

            return false;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public boolean shouldCloseOnEsc()
    {
        return true;
    }

    public void closeScreen()
    {
        this.mc.displayGuiScreen((Screen)null);
    }

    protected <T extends Widget> T addButton(T button)
    {
        this.buttons.add(button);
        return this.addListener(button);
    }

    protected <T extends IGuiEventListener> T addListener(T listener)
    {
        this.children.add(listener);
        return listener;
    }

    protected void renderTooltip(MatrixStack matrixStack, ItemStack itemStack, int mouseX, int mouseY)
    {
        this.func_243308_b(matrixStack, this.getTooltipFromItem(itemStack), mouseX, mouseY);
    }

    public List<ITextComponent> getTooltipFromItem(ItemStack itemStack)
    {
        return itemStack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    public void renderTooltip(MatrixStack matrixStack, ITextComponent text, int mouseX, int mouseY)
    {
        this.renderTooltip(matrixStack, Arrays.asList(text.func_241878_f()), mouseX, mouseY);
    }

    public void func_243308_b(MatrixStack p_243308_1_, List<ITextComponent> p_243308_2_, int p_243308_3_, int p_243308_4_)
    {
        this.renderTooltip(p_243308_1_, Lists.transform(p_243308_2_, ITextComponent::func_241878_f), p_243308_3_, p_243308_4_);
    }

    public void renderTooltip(MatrixStack matrixStack, List <? extends IReorderingProcessor > tooltips, int mouseX, int mouseY)
    {
        if (!tooltips.isEmpty())
        {
            int i = 0;

            for (IReorderingProcessor ireorderingprocessor : tooltips)
            {
                int j = this.font.func_243245_a(ireorderingprocessor);

                if (j > i)
                {
                    i = j;
                }
            }

            int i2 = mouseX + 12;
            int j2 = mouseY - 12;
            int k = 8;

            if (tooltips.size() > 1)
            {
                k += 2 + (tooltips.size() - 1) * 10;
            }

            if (i2 + i > this.width)
            {
                i2 -= 28 + i;
            }

            if (j2 + k + 6 > this.height)
            {
                j2 = this.height - k - 6;
            }

            matrixStack.push();
            int l = -267386864;
            int i1 = 1347420415;
            int j1 = 1344798847;
            int k1 = 400;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            Matrix4f matrix4f = matrixStack.getLast().getMatrix();
            fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 - 4, i2 + i + 3, j2 - 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 + k + 3, i2 + i + 3, j2 + k + 4, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 - 3, i2 + i + 3, j2 + k + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferbuilder, i2 - 4, j2 - 3, i2 - 3, j2 + k + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferbuilder, i2 + i + 3, j2 - 3, i2 + i + 4, j2 + k + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 - 3 + 1, i2 - 3 + 1, j2 + k + 3 - 1, 400, 1347420415, 1344798847);
            fillGradient(matrix4f, bufferbuilder, i2 + i + 2, j2 - 3 + 1, i2 + i + 3, j2 + k + 3 - 1, 400, 1347420415, 1344798847);
            fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 - 3, i2 + i + 3, j2 - 3 + 1, 400, 1347420415, 1347420415);
            fillGradient(matrix4f, bufferbuilder, i2 - 3, j2 + k + 2, i2 + i + 3, j2 + k + 3, 400, 1344798847, 1344798847);
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.shadeModel(7425);
            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            RenderSystem.shadeModel(7424);
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            matrixStack.translate(0.0D, 0.0D, 400.0D);

            for (int l1 = 0; l1 < tooltips.size(); ++l1)
            {
                IReorderingProcessor ireorderingprocessor1 = tooltips.get(l1);

                if (ireorderingprocessor1 != null)
                {
                    this.font.func_238416_a_(ireorderingprocessor1, (float)i2, (float)j2, -1, true, matrix4f, irendertypebuffer$impl, false, 0, 15728880);
                }

                if (l1 == 0)
                {
                    j2 += 2;
                }

                j2 += 10;
            }

            irendertypebuffer$impl.finish();
            matrixStack.pop();
        }
    }

    protected void renderComponentHoverEffect(MatrixStack matrixStack, @Nullable Style style, int mouseX, int mouseY)
    {
        if (style != null && style.getHoverEvent() != null)
        {
            HoverEvent hoverevent = style.getHoverEvent();
            HoverEvent.ItemHover hoverevent$itemhover = hoverevent.getParameter(HoverEvent.Action.SHOW_ITEM);

            if (hoverevent$itemhover != null)
            {
                this.renderTooltip(matrixStack, hoverevent$itemhover.createStack(), mouseX, mouseY);
            }
            else
            {
                HoverEvent.EntityHover hoverevent$entityhover = hoverevent.getParameter(HoverEvent.Action.SHOW_ENTITY);

                if (hoverevent$entityhover != null)
                {
                    if (this.mc.gameSettings.advancedItemTooltips)
                    {
                        this.func_243308_b(matrixStack, hoverevent$entityhover.getTooltip(), mouseX, mouseY);
                    }
                }
                else
                {
                    ITextComponent itextcomponent = hoverevent.getParameter(HoverEvent.Action.SHOW_TEXT);

                    if (itextcomponent != null)
                    {
                        this.renderTooltip(matrixStack, this.mc.fontRenderer.trimStringToWidth(itextcomponent, Math.max(this.width / 2, 200)), mouseX, mouseY);
                    }
                }
            }
        }
    }

    protected void insertText(String text, boolean overwrite)
    {
    }

    public boolean handleComponentClicked(@Nullable Style style)
    {
        if (style == null)
        {
            return false;
        }
        else
        {
            ClickEvent clickevent = style.getClickEvent();

            if (hasShiftDown())
            {
                if (style.getInsertion() != null)
                {
                    this.insertText(style.getInsertion(), false);
                }
            }
            else if (clickevent != null)
            {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL)
                {
                    if (!this.mc.gameSettings.chatLinks)
                    {
                        return false;
                    }

                    try
                    {
                        URI uri = new URI(clickevent.getValue());
                        String s = uri.getScheme();

                        if (s == null)
                        {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }

                        if (!ALLOWED_PROTOCOLS.contains(s.toLowerCase(Locale.ROOT)))
                        {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                        }

                        if (this.mc.gameSettings.chatLinksPrompt)
                        {
                            this.clickedLink = uri;
                            this.mc.displayGuiScreen(new ConfirmOpenLinkScreen(this::confirmLink, clickevent.getValue(), false));
                        }
                        else
                        {
                            this.openLink(uri);
                        }
                    }
                    catch (URISyntaxException urisyntaxexception)
                    {
                        LOGGER.error("Can't open url for {}", clickevent, urisyntaxexception);
                    }
                }
                else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE)
                {
                    URI uri1 = (new File(clickevent.getValue())).toURI();
                    this.openLink(uri1);
                }
                else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                {
                    this.insertText(clickevent.getValue(), true);
                }
                else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
                {
                    this.sendMessage(clickevent.getValue(), false);
                }
                else if (clickevent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD)
                {
                    this.mc.keyboardListener.setClipboardString(clickevent.getValue());
                }
                else
                {
                    LOGGER.error("Don't know how to handle {}", (Object)clickevent);
                }

                return true;
            }

            return false;
        }
    }

    public void sendMessage(String text)
    {
        this.sendMessage(text, true);
    }

    public void sendMessage(String text, boolean addToChat)
    {
        if (addToChat)
        {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(text);
        }

        this.mc.player.sendChatMessage(text);
    }

    public void init(Minecraft minecraft, int width, int height)
    {
        this.mc = minecraft;
        this.itemRenderer = minecraft.getItemRenderer();
        this.font = minecraft.fontRenderer;
        this.width = width;
        this.height = height;
        this.buttons.clear();
        this.children.clear();
        this.setListener((IGuiEventListener)null);
        this.init();
    }

    public List <? extends IGuiEventListener > getEventListeners()
    {
        return this.children;
    }

    protected void init()
    {
    }

    public void tick()
    {
    }

    public void onClose()
    {
    }

    public void renderBackground(MatrixStack matrixStack)
    {
        this.renderBackground(matrixStack, 0);
    }

    public void renderBackground(MatrixStack matrixStack, int vOffset)
    {
        if (this.mc.world != null)
        {
            this.fillGradient(matrixStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else
        {
            this.renderDirtBackground(vOffset);
        }
    }

    public void renderDirtBackground(int vOffset)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public boolean isPauseScreen()
    {
        return true;
    }

    private void confirmLink(boolean doOpen)
    {
        if (doOpen)
        {
            this.openLink(this.clickedLink);
        }

        this.clickedLink = null;
        this.mc.displayGuiScreen(this);
    }

    private void openLink(URI uri)
    {
        Util.getOSType().openURI(uri);
    }

    public static boolean hasControlDown()
    {
        if (Minecraft.IS_RUNNING_ON_MAC)
        {
            return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 343) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 347);
        }
        else
        {
            return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 341) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 345);
        }
    }

    public static boolean hasShiftDown()
    {
        return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);
    }

    public static boolean hasAltDown()
    {
        return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 342) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 346);
    }

    public static boolean isCut(int keyCode)
    {
        return keyCode == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isPaste(int keyCode)
    {
        return keyCode == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isCopy(int keyCode)
    {
        return keyCode == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isSelectAll(int keyCode)
    {
        return keyCode == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public void resize(Minecraft minecraft, int width, int height)
    {
        this.init(minecraft, width, height);
    }

    public static void wrapScreenError(Runnable action, String errorDesc, String screenName)
    {
        try
        {
            action.run();
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, errorDesc);
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
            crashreportcategory.addDetail("Screen name", () ->
            {
                return screenName;
            });
            throw new ReportedException(crashreport);
        }
    }

    protected boolean isValidCharacterForName(String text, char charTyped, int cursorPos)
    {
        int i = text.indexOf(58);
        int j = text.indexOf(47);

        if (charTyped == ':')
        {
            return (j == -1 || cursorPos <= j) && i == -1;
        }
        else if (charTyped == '/')
        {
            return cursorPos > i;
        }
        else
        {
            return charTyped == '_' || charTyped == '-' || charTyped >= 'a' && charTyped <= 'z' || charTyped >= '0' && charTyped <= '9' || charTyped == '.';
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return true;
    }

    public void addPacks(List<Path> packs)
    {
    }
}
