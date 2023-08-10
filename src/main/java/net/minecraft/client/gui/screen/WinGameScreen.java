package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResource;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WinGameScreen extends Screen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
    private static final String field_238663_q_ = "" + TextFormatting.WHITE + TextFormatting.OBFUSCATED + TextFormatting.GREEN + TextFormatting.AQUA;
    private final boolean poem;
    private final Runnable onFinished;
    private float time;
    private List<IReorderingProcessor> lines;
    private IntSet field_238664_v_;
    private int totalScrollLength;
    private float scrollSpeed = 0.5F;

    public WinGameScreen(boolean poemIn, Runnable onFinishedIn)
    {
        super(NarratorChatListener.EMPTY);
        this.poem = poemIn;
        this.onFinished = onFinishedIn;

        if (!poemIn)
        {
            this.scrollSpeed = 0.75F;
        }
    }

    public void tick()
    {
        this.mc.getMusicTicker().tick();
        this.mc.getSoundHandler().tick(false);
        float f = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;

        if (this.time > f)
        {
            this.sendRespawnPacket();
        }
    }

    public void closeScreen()
    {
        this.sendRespawnPacket();
    }

    private void sendRespawnPacket()
    {
        this.onFinished.run();
        this.mc.displayGuiScreen((Screen)null);
    }

    protected void init()
    {
        if (this.lines == null)
        {
            this.lines = Lists.newArrayList();
            this.field_238664_v_ = new IntOpenHashSet();
            IResource iresource = null;

            try
            {
                int i = 274;

                if (this.poem)
                {
                    iresource = this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
                    InputStream inputstream = iresource.getInputStream();
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                    Random random = new Random(8124371L);
                    String s;

                    while ((s = bufferedreader.readLine()) != null)
                    {
                        int j;
                        String s1;
                        String s2;

                        for (s = s.replaceAll("PLAYERNAME", this.mc.getSession().getUsername()); (j = s.indexOf(field_238663_q_)) != -1; s = s1 + TextFormatting.WHITE + TextFormatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + s2)
                        {
                            s1 = s.substring(0, j);
                            s2 = s.substring(j + field_238663_q_.length());
                        }

                        this.lines.addAll(this.mc.fontRenderer.trimStringToWidth(new StringTextComponent(s), 274));
                        this.lines.add(IReorderingProcessor.field_242232_a);
                    }

                    inputstream.close();

                    for (int k = 0; k < 8; ++k)
                    {
                        this.lines.add(IReorderingProcessor.field_242232_a);
                    }
                }

                InputStream inputstream1 = this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
                BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(inputstream1, StandardCharsets.UTF_8));
                String s3;

                while ((s3 = bufferedreader1.readLine()) != null)
                {
                    s3 = s3.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                    s3 = s3.replaceAll("\t", "    ");
                    boolean flag;

                    if (s3.startsWith("[C]"))
                    {
                        s3 = s3.substring(3);
                        flag = true;
                    }
                    else
                    {
                        flag = false;
                    }

                    for (IReorderingProcessor ireorderingprocessor : this.mc.fontRenderer.trimStringToWidth(new StringTextComponent(s3), 274))
                    {
                        if (flag)
                        {
                            this.field_238664_v_.add(this.lines.size());
                        }

                        this.lines.add(ireorderingprocessor);
                    }

                    this.lines.add(IReorderingProcessor.field_242232_a);
                }

                inputstream1.close();
                this.totalScrollLength = this.lines.size() * 12;
            }
            catch (Exception exception)
            {
                LOGGER.error("Couldn't load credits", (Throwable)exception);
            }
            finally
            {
                IOUtils.closeQuietly((Closeable)iresource);
            }
        }
    }

    private void drawWinGameScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.mc.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
        int i = this.width;
        float f = -this.time * 0.5F * this.scrollSpeed;
        float f1 = (float)this.height - this.time * 0.5F * this.scrollSpeed;
        float f2 = 0.015625F;
        float f3 = this.time * 0.02F;
        float f4 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
        float f5 = (f4 - 20.0F - this.time) * 0.005F;

        if (f5 < f3)
        {
            f3 = f5;
        }

        if (f3 > 1.0F)
        {
            f3 = 1.0F;
        }

        f3 = f3 * f3;
        f3 = f3 * 96.0F / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)this.height, (double)this.getBlitOffset()).tex(0.0F, f * 0.015625F).color(f3, f3, f3, 1.0F).endVertex();
        bufferbuilder.pos((double)i, (double)this.height, (double)this.getBlitOffset()).tex((float)i * 0.015625F, f * 0.015625F).color(f3, f3, f3, 1.0F).endVertex();
        bufferbuilder.pos((double)i, 0.0D, (double)this.getBlitOffset()).tex((float)i * 0.015625F, f1 * 0.015625F).color(f3, f3, f3, 1.0F).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, (double)this.getBlitOffset()).tex(0.0F, f1 * 0.015625F).color(f3, f3, f3, 1.0F).endVertex();
        tessellator.draw();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.drawWinGameScreen(mouseX, mouseY, partialTicks);
        int i = 274;
        int j = this.width / 2 - 137;
        int k = this.height + 50;
        this.time += partialTicks;
        float f = -this.time * this.scrollSpeed;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0F, f, 0.0F);
        this.mc.getTextureManager().bindTexture(MINECRAFT_LOGO);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        this.blitBlackOutline(j, k, (p_238665_2_, p_238665_3_) ->
        {
            this.blit(matrixStack, p_238665_2_ + 0, p_238665_3_, 0, 0, 155, 44);
            this.blit(matrixStack, p_238665_2_ + 155, p_238665_3_, 0, 45, 155, 44);
        });
        RenderSystem.disableBlend();
        this.mc.getTextureManager().bindTexture(MINECRAFT_EDITION);
        blit(matrixStack, j + 88, k + 37, 0.0F, 0.0F, 98, 14, 128, 16);
        RenderSystem.disableAlphaTest();
        int l = k + 100;

        for (int i1 = 0; i1 < this.lines.size(); ++i1)
        {
            if (i1 == this.lines.size() - 1)
            {
                float f1 = (float)l + f - (float)(this.height / 2 - 6);

                if (f1 < 0.0F)
                {
                    RenderSystem.translatef(0.0F, -f1, 0.0F);
                }
            }

            if ((float)l + f + 12.0F + 8.0F > 0.0F && (float)l + f < (float)this.height)
            {
                IReorderingProcessor ireorderingprocessor = this.lines.get(i1);

                if (this.field_238664_v_.contains(i1))
                {
                    this.font.func_238407_a_(matrixStack, ireorderingprocessor, (float)(j + (274 - this.font.func_243245_a(ireorderingprocessor)) / 2), (float)l, 16777215);
                }
                else
                {
                    this.font.random.setSeed((long)((float)((long)i1 * 4238972211L) + this.time / 4.0F));
                    this.font.func_238407_a_(matrixStack, ireorderingprocessor, (float)j, (float)l, 16777215);
                }
            }

            l += 12;
        }

        RenderSystem.popMatrix();
        this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        int j1 = this.width;
        int k1 = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)k1, (double)this.getBlitOffset()).tex(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos((double)j1, (double)k1, (double)this.getBlitOffset()).tex(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos((double)j1, 0.0D, (double)this.getBlitOffset()).tex(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, (double)this.getBlitOffset()).tex(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
        RenderSystem.disableBlend();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
