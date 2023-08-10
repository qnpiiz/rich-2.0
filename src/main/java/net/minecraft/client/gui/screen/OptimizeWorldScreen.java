package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OptimizeWorldScreen extends Screen
{
    private static final Logger field_239024_a_ = LogManager.getLogger();
    private static final Object2IntMap<RegistryKey<World>> PROGRESS_BAR_COLORS = Util.make(new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy()), (p_212346_0_) ->
    {
        p_212346_0_.put(World.OVERWORLD, -13408734);
        p_212346_0_.put(World.THE_NETHER, -10075085);
        p_212346_0_.put(World.THE_END, -8943531);
        p_212346_0_.defaultReturnValue(-2236963);
    });
    private final BooleanConsumer field_214332_b;
    private final WorldOptimizer optimizer;

    @Nullable
    public static OptimizeWorldScreen func_239025_a_(Minecraft p_239025_0_, BooleanConsumer p_239025_1_, DataFixer p_239025_2_, SaveFormat.LevelSave p_239025_3_, boolean p_239025_4_)
    {
        DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.func_239770_b_();

        try (Minecraft.PackManager minecraft$packmanager = p_239025_0_.reloadDatapacks(dynamicregistries$impl, Minecraft::loadDataPackCodec, Minecraft::loadWorld, false, p_239025_3_))
        {
            IServerConfiguration iserverconfiguration = minecraft$packmanager.getServerConfiguration();
            p_239025_3_.saveLevel(dynamicregistries$impl, iserverconfiguration);
            ImmutableSet<RegistryKey<World>> immutableset = iserverconfiguration.getDimensionGeneratorSettings().func_236226_g_();
            return new OptimizeWorldScreen(p_239025_1_, p_239025_2_, p_239025_3_, iserverconfiguration.getWorldSettings(), p_239025_4_, immutableset);
        }
        catch (Exception exception)
        {
            field_239024_a_.warn("Failed to load datapacks, can't optimize world", (Throwable)exception);
            return null;
        }
    }

    private OptimizeWorldScreen(BooleanConsumer p_i232319_1_, DataFixer p_i232319_2_, SaveFormat.LevelSave p_i232319_3_, WorldSettings p_i232319_4_, boolean p_i232319_5_, ImmutableSet<RegistryKey<World>> p_i232319_6_)
    {
        super(new TranslationTextComponent("optimizeWorld.title", p_i232319_4_.getWorldName()));
        this.field_214332_b = p_i232319_1_;
        this.optimizer = new WorldOptimizer(p_i232319_3_, p_i232319_2_, p_i232319_6_, p_i232319_5_);
    }

    protected void init()
    {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 200, 20, DialogTexts.GUI_CANCEL, (p_214331_1_) ->
        {
            this.optimizer.cancel();
            this.field_214332_b.accept(false);
        }));
    }

    public void tick()
    {
        if (this.optimizer.isFinished())
        {
            this.field_214332_b.accept(true);
        }
    }

    public void closeScreen()
    {
        this.field_214332_b.accept(false);
    }

    public void onClose()
    {
        this.optimizer.cancel();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
        int i = this.width / 2 - 150;
        int j = this.width / 2 + 150;
        int k = this.height / 4 + 100;
        int l = k + 10;
        drawCenteredString(matrixStack, this.font, this.optimizer.getStatusText(), this.width / 2, k - 9 - 2, 10526880);

        if (this.optimizer.getTotalChunks() > 0)
        {
            fill(matrixStack, i - 1, k - 1, j + 1, l + 1, -16777216);
            drawString(matrixStack, this.font, new TranslationTextComponent("optimizeWorld.info.converted", this.optimizer.getConverted()), i, 40, 10526880);
            drawString(matrixStack, this.font, new TranslationTextComponent("optimizeWorld.info.skipped", this.optimizer.getSkipped()), i, 40 + 9 + 3, 10526880);
            drawString(matrixStack, this.font, new TranslationTextComponent("optimizeWorld.info.total", this.optimizer.getTotalChunks()), i, 40 + (9 + 3) * 2, 10526880);
            int i1 = 0;

            for (RegistryKey<World> registrykey : this.optimizer.func_233533_c_())
            {
                int j1 = MathHelper.floor(this.optimizer.func_233531_a_(registrykey) * (float)(j - i));
                fill(matrixStack, i + i1, k, i + i1 + j1, l, PROGRESS_BAR_COLORS.getInt(registrykey));
                i1 += j1;
            }

            int k1 = this.optimizer.getConverted() + this.optimizer.getSkipped();
            drawCenteredString(matrixStack, this.font, k1 + " / " + this.optimizer.getTotalChunks(), this.width / 2, k + 2 * 9 + 2, 10526880);
            drawCenteredString(matrixStack, this.font, MathHelper.floor(this.optimizer.getTotalProgress() * 100.0F) + "%", this.width / 2, k + (l - k) / 2 - 9 / 2, 10526880);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
