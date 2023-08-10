package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class DemoScreen extends Screen
{
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");
    private IBidiRenderer field_243286_b = IBidiRenderer.field_243257_a;
    private IBidiRenderer field_243287_c = IBidiRenderer.field_243257_a;

    public DemoScreen()
    {
        super(new TranslationTextComponent("demo.help.title"));
    }

    protected void init()
    {
        int i = -16;
        this.addButton(new Button(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, new TranslationTextComponent("demo.help.buy"), (p_213019_0_) ->
        {
            p_213019_0_.active = false;
            Util.getOSType().openURI("http://www.minecraft.net/store?source=demo");
        }));
        this.addButton(new Button(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, new TranslationTextComponent("demo.help.later"), (p_213018_1_) ->
        {
            this.mc.displayGuiScreen((Screen)null);
            this.mc.mouseHelper.grabMouse();
        }));
        GameSettings gamesettings = this.mc.gameSettings;
        this.field_243286_b = IBidiRenderer.func_243260_a(this.font, new TranslationTextComponent("demo.help.movementShort", gamesettings.keyBindForward.func_238171_j_(), gamesettings.keyBindLeft.func_238171_j_(), gamesettings.keyBindBack.func_238171_j_(), gamesettings.keyBindRight.func_238171_j_()), new TranslationTextComponent("demo.help.movementMouse"), new TranslationTextComponent("demo.help.jump", gamesettings.keyBindJump.func_238171_j_()), new TranslationTextComponent("demo.help.inventory", gamesettings.keyBindInventory.func_238171_j_()));
        this.field_243287_c = IBidiRenderer.func_243258_a(this.font, new TranslationTextComponent("demo.help.fullWrapped"), 218);
    }

    public void renderBackground(MatrixStack matrixStack)
    {
        super.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(DEMO_BACKGROUND_LOCATION);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.blit(matrixStack, i, j, 0, 0, 248, 166);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        this.font.func_243248_b(matrixStack, this.title, (float)i, (float)j, 2039583);
        j = this.field_243286_b.func_241866_c(matrixStack, i, j + 12, 12, 5197647);
        this.field_243287_c.func_241866_c(matrixStack, i, j + 20, 9, 2039583);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
