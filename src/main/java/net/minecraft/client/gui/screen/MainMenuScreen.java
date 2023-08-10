package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import fun.rich.utils.render.RenderUtils;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MainMenuScreen extends Screen {

    public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/accessibility.png");
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");

    private final float initTime = System.currentTimeMillis();

    public MainMenuScreen() {
        this(false);
    }

    public MainMenuScreen(boolean fadeIn) {
        super(new TranslationTextComponent("narrator.screen.title"));
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 90, this.height / 2, 180, 26, new StringTextComponent("Singleplayer"), (p_lambda$init$2_1_) -> mc.displayGuiScreen(new WorldSelectionScreen(this))));
        this.addButton(new Button(this.width / 2 - 90, this.height / 2 + 28, 180, 26, new StringTextComponent("Multiplayer"), (p_lambda$init$2_1_) -> mc.displayGuiScreen(new MultiplayerScreen(this))));
        this.addButton(new Button(this.width / 2 - 90, this.height / 2 + 56, 180, 26, new StringTextComponent("Alt Manager"), (p_lambda$init$2_1_) -> mc.displayGuiScreen(new MultiplayerScreen(this))));
        this.addButton(new Button(this.width / 2 - 90, this.height / 2 + 84, 180, 26, new StringTextComponent("Options"), (p_lambda$init$2_1_) -> mc.displayGuiScreen(new OptionsScreen(this, mc.gameSettings))));
        this.addButton(new Button(this.width / 2 - 90, this.height / 2 + 112, 180, 26, new StringTextComponent("Quit"), (p_lambda$init$2_1_) -> mc.shutdown()));
        // TODO: Add alt manager

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        MainWindow sr = mc.getMainWindow();

        RenderUtils.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(17, 17, 17).getRGB(), matrixStack);
        mc.neverlose900_30.drawCenteredStringWithOutline("RICH 2.0", (float) (sr.getScaledWidth() / 2 + 1), (float) (sr.getScaledHeight() / 2.5f), -1, matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public static CompletableFuture<Void> loadAsync(TextureManager texMngr, Executor backgroundExecutor) {
        return CompletableFuture.allOf(texMngr.loadAsync(MINECRAFT_TITLE_TEXTURES, backgroundExecutor), texMngr.loadAsync(MINECRAFT_TITLE_EDITION, backgroundExecutor), texMngr.loadAsync(PANORAMA_OVERLAY_TEXTURES, backgroundExecutor), PANORAMA_RESOURCES.loadAsync(texMngr, backgroundExecutor));
    }

    public boolean isPauseScreen() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}
