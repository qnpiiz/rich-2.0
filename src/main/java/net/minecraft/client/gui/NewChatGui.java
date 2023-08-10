package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewChatGui extends AbstractGui
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine<ITextComponent>> chatLines = Lists.newArrayList();
    private final List<ChatLine<IReorderingProcessor>> drawnChatLines = Lists.newArrayList();
    private final Deque<ITextComponent> field_238489_i_ = Queues.newArrayDeque();
    private int scrollPos;
    private boolean isScrolled;
    private long field_238490_l_ = 0L;
    private int lastChatWidth = 0;

    public NewChatGui(Minecraft mcIn)
    {
        this.mc = mcIn;
    }

    public void func_238492_a_(MatrixStack p_238492_1_, int p_238492_2_)
    {
        int i = this.getChatWidth();

        if (this.lastChatWidth != i)
        {
            this.lastChatWidth = i;
            this.refreshChat();
        }

        if (!this.func_238496_i_())
        {
            this.func_238498_k_();
            int j = this.getLineCount();
            int k = this.drawnChatLines.size();

            if (k > 0)
            {
                boolean flag = false;

                if (this.getChatOpen())
                {
                    flag = true;
                }

                double d0 = this.getScale();
                int l = MathHelper.ceil((double)this.getChatWidth() / d0);
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, 8.0F, 0.0F);
                RenderSystem.scaled(d0, d0, 1.0D);
                double d1 = this.mc.gameSettings.chatOpacity * (double)0.9F + (double)0.1F;
                double d2 = this.mc.gameSettings.accessibilityTextBackgroundOpacity;
                double d3 = 9.0D * (this.mc.gameSettings.chatLineSpacing + 1.0D);
                double d4 = -8.0D * (this.mc.gameSettings.chatLineSpacing + 1.0D) + 4.0D * this.mc.gameSettings.chatLineSpacing;
                int i1 = 0;

                for (int j1 = 0; j1 + this.scrollPos < this.drawnChatLines.size() && j1 < j; ++j1)
                {
                    ChatLine<IReorderingProcessor> chatline = this.drawnChatLines.get(j1 + this.scrollPos);

                    if (chatline != null)
                    {
                        int k1 = p_238492_2_ - chatline.getUpdatedCounter();

                        if (k1 < 200 || flag)
                        {
                            double d5 = flag ? 1.0D : getLineBrightness(k1);
                            int i2 = (int)(255.0D * d5 * d1);
                            int j2 = (int)(255.0D * d5 * d2);
                            ++i1;

                            if (i2 > 3)
                            {
                                int k2 = 0;
                                double d6 = (double)(-j1) * d3;
                                p_238492_1_.push();
                                p_238492_1_.translate(0.0D, 0.0D, 50.0D);

                                if (this.mc.gameSettings.ofChatBackground == 5)
                                {
                                    l = this.mc.fontRenderer.func_243245_a(chatline.getLineString()) - 2;
                                }

                                if (this.mc.gameSettings.ofChatBackground != 3)
                                {
                                    fill(p_238492_1_, -2, (int)(d6 - d3), 0 + l + 4, (int)d6, j2 << 24);
                                }

                                RenderSystem.enableBlend();
                                p_238492_1_.translate(0.0D, 0.0D, 50.0D);

                                if (!this.mc.gameSettings.ofChatShadow)
                                {
                                    this.mc.fontRenderer.func_238422_b_(p_238492_1_, chatline.getLineString(), 0.0F, (float)((int)(d6 + d4)), 16777215 + (i2 << 24));
                                }
                                else
                                {
                                    this.mc.fontRenderer.func_238407_a_(p_238492_1_, chatline.getLineString(), 0.0F, (float)((int)(d6 + d4)), 16777215 + (i2 << 24));
                                }

                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                                p_238492_1_.pop();
                            }
                        }
                    }
                }

                if (!this.field_238489_i_.isEmpty())
                {
                    int l2 = (int)(128.0D * d1);
                    int j3 = (int)(255.0D * d2);
                    p_238492_1_.push();
                    p_238492_1_.translate(0.0D, 0.0D, 50.0D);
                    fill(p_238492_1_, -2, 0, l + 4, 9, j3 << 24);
                    RenderSystem.enableBlend();
                    p_238492_1_.translate(0.0D, 0.0D, 50.0D);
                    this.mc.fontRenderer.func_243246_a(p_238492_1_, new TranslationTextComponent("chat.queue", this.field_238489_i_.size()), 0.0F, 1.0F, 16777215 + (l2 << 24));
                    p_238492_1_.pop();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableBlend();
                }

                if (flag)
                {
                    int i3 = 9;
                    RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
                    int k3 = k * i3 + k;
                    int l3 = i1 * i3 + i1;
                    int i4 = this.scrollPos * l3 / k;
                    int l1 = l3 * l3 / k3;

                    if (k3 != l3)
                    {
                        int j4 = i4 > 0 ? 170 : 96;
                        int k4 = this.isScrolled ? 13382451 : 3355562;
                        fill(p_238492_1_, 0, -i4, 2, -i4 - l1, k4 + (j4 << 24));
                        fill(p_238492_1_, 2, -i4, 1, -i4 - l1, 13421772 + (j4 << 24));
                    }
                }

                RenderSystem.popMatrix();
            }
        }
    }

    private boolean func_238496_i_()
    {
        return this.mc.gameSettings.chatVisibility == ChatVisibility.HIDDEN;
    }

    private static double getLineBrightness(int counterIn)
    {
        double d0 = (double)counterIn / 200.0D;
        d0 = 1.0D - d0;
        d0 = d0 * 10.0D;
        d0 = MathHelper.clamp(d0, 0.0D, 1.0D);
        return d0 * d0;
    }

    /**
     * Clears the chat.
     *  
     * @param clearSentMsgHistory Whether or not to clear the user's sent message history
     */
    public void clearChatMessages(boolean clearSentMsgHistory)
    {
        this.field_238489_i_.clear();
        this.drawnChatLines.clear();
        this.chatLines.clear();

        if (clearSentMsgHistory)
        {
            this.sentMessages.clear();
        }
    }

    public void printChatMessage(ITextComponent chatComponent)
    {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId)
    {
        this.func_238493_a_(chatComponent, chatLineId, this.mc.ingameGUI.getTicks(), false);
        LOGGER.info("[CHAT] {}", (Object)chatComponent.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void func_238493_a_(ITextComponent p_238493_1_, int p_238493_2_, int p_238493_3_, boolean p_238493_4_)
    {
        if (p_238493_2_ != 0)
        {
            this.deleteChatLine(p_238493_2_);
        }

        int i = MathHelper.floor((double)this.getChatWidth() / this.getScale());
        List<IReorderingProcessor> list = RenderComponentsUtil.func_238505_a_(p_238493_1_, i, this.mc.fontRenderer);
        boolean flag = this.getChatOpen();

        for (IReorderingProcessor ireorderingprocessor : list)
        {
            if (flag && this.scrollPos > 0)
            {
                this.isScrolled = true;
                this.addScrollPos(1.0D);
            }

            this.drawnChatLines.add(0, new ChatLine<>(p_238493_3_, ireorderingprocessor, p_238493_2_));
        }

        while (this.drawnChatLines.size() > 100)
        {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!p_238493_4_)
        {
            this.chatLines.add(0, new ChatLine<>(p_238493_3_, p_238493_1_, p_238493_2_));

            while (this.chatLines.size() > 100)
            {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat()
    {
        this.drawnChatLines.clear();
        this.resetScroll();

        for (int i = this.chatLines.size() - 1; i >= 0; --i)
        {
            ChatLine<ITextComponent> chatline = this.chatLines.get(i);
            this.func_238493_a_(chatline.getLineString(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages()
    {
        return this.sentMessages;
    }

    /**
     * Adds this string to the list of sent messages, for recall using the up/down arrow keys
     */
    public void addToSentMessages(String message)
    {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message))
        {
            this.sentMessages.add(message);
        }
    }

    /**
     * Resets the chat scroll (executed when the GUI is closed, among others)
     */
    public void resetScroll()
    {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void addScrollPos(double posInc)
    {
        this.scrollPos = (int)((double)this.scrollPos + posInc);
        int i = this.drawnChatLines.size();

        if (this.scrollPos > i - this.getLineCount())
        {
            this.scrollPos = i - this.getLineCount();
        }

        if (this.scrollPos <= 0)
        {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    public boolean func_238491_a_(double p_238491_1_, double p_238491_3_)
    {
        if (this.getChatOpen() && !this.mc.gameSettings.hideGUI && !this.func_238496_i_() && !this.field_238489_i_.isEmpty())
        {
            double d0 = p_238491_1_ - 2.0D;
            double d1 = (double)this.mc.getMainWindow().getScaledHeight() - p_238491_3_ - 40.0D;

            if (d0 <= (double)MathHelper.floor((double)this.getChatWidth() / this.getScale()) && d1 < 0.0D && d1 > (double)MathHelper.floor(-9.0D * this.getScale()))
            {
                this.printChatMessage(this.field_238489_i_.remove());
                this.field_238490_l_ = System.currentTimeMillis();
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Nullable
    public Style func_238494_b_(double p_238494_1_, double p_238494_3_)
    {
        if (this.getChatOpen() && !this.mc.gameSettings.hideGUI && !this.func_238496_i_())
        {
            double d0 = p_238494_1_ - 2.0D;
            double d1 = (double)this.mc.getMainWindow().getScaledHeight() - p_238494_3_ - 40.0D;
            d0 = (double)MathHelper.floor(d0 / this.getScale());
            d1 = (double)MathHelper.floor(d1 / (this.getScale() * (this.mc.gameSettings.chatLineSpacing + 1.0D)));

            if (!(d0 < 0.0D) && !(d1 < 0.0D))
            {
                int i = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (d0 <= (double)MathHelper.floor((double)this.getChatWidth() / this.getScale()) && d1 < (double)(9 * i + i))
                {
                    int j = (int)(d1 / 9.0D + (double)this.scrollPos);

                    if (j >= 0 && j < this.drawnChatLines.size())
                    {
                        ChatLine<IReorderingProcessor> chatline = this.drawnChatLines.get(j);
                        return this.mc.fontRenderer.getCharacterManager().func_243239_a(chatline.getLineString(), (int)d0);
                    }
                }

                return null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns true if the chat GUI is open
     */
    private boolean getChatOpen()
    {
        return this.mc.currentScreen instanceof ChatScreen;
    }

    /**
     * finds and deletes a Chat line by ID
     */
    public void deleteChatLine(int id)
    {
        this.drawnChatLines.removeIf((p_lambda$deleteChatLine$0_1_) ->
        {
            return p_lambda$deleteChatLine$0_1_.getChatLineID() == id;
        });
        this.chatLines.removeIf((p_lambda$deleteChatLine$1_1_) ->
        {
            return p_lambda$deleteChatLine$1_1_.getChatLineID() == id;
        });
    }

    public int getChatWidth()
    {
        int i = calculateChatboxWidth(this.mc.gameSettings.chatWidth);
        MainWindow mainwindow = Minecraft.getInstance().getMainWindow();
        int j = (int)((double)(mainwindow.getFramebufferWidth() - 3) / mainwindow.getGuiScaleFactor());
        return MathHelper.clamp(i, 0, j);
    }

    public int getChatHeight()
    {
        return calculateChatboxHeight((this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused) / (this.mc.gameSettings.chatLineSpacing + 1.0D));
    }

    public double getScale()
    {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(double p_194814_0_)
    {
        int i = 320;
        int j = 40;
        return MathHelper.floor(p_194814_0_ * 280.0D + 40.0D);
    }

    public static int calculateChatboxHeight(double p_194816_0_)
    {
        int i = 180;
        int j = 20;
        return MathHelper.floor(p_194816_0_ * 160.0D + 20.0D);
    }

    public int getLineCount()
    {
        return this.getChatHeight() / 9;
    }

    private long func_238497_j_()
    {
        return (long)(this.mc.gameSettings.chatDelay * 1000.0D);
    }

    private void func_238498_k_()
    {
        if (!this.field_238489_i_.isEmpty())
        {
            long i = System.currentTimeMillis();

            if (i - this.field_238490_l_ >= this.func_238497_j_())
            {
                this.printChatMessage(this.field_238489_i_.remove());
                this.field_238490_l_ = i;
            }
        }
    }

    public void func_238495_b_(ITextComponent p_238495_1_)
    {
        if (this.mc.gameSettings.chatDelay <= 0.0D)
        {
            this.printChatMessage(p_238495_1_);
        }
        else
        {
            long i = System.currentTimeMillis();

            if (i - this.field_238490_l_ >= this.func_238497_j_())
            {
                this.printChatMessage(p_238495_1_);
                this.field_238490_l_ = i;
            }
            else
            {
                this.field_238489_i_.add(p_238495_1_);
            }
        }
    }
}
