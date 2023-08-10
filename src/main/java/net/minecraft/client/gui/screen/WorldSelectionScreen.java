package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

public class WorldSelectionScreen extends Screen
{
    protected final Screen prevScreen;
    private List<IReorderingProcessor> worldVersTooltip;
    private Button deleteButton;
    private Button selectButton;
    private Button renameButton;
    private Button copyButton;
    protected TextFieldWidget searchField;
    private WorldSelectionList selectionList;

    public WorldSelectionScreen(Screen screenIn)
    {
        super(new TranslationTextComponent("selectWorld.title"));
        this.prevScreen = screenIn;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public void tick()
    {
        this.searchField.tick();
    }

    protected void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.searchField = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.searchField, new TranslationTextComponent("selectWorld.search"));
        this.searchField.setResponder((p_214329_1_) ->
        {
            this.selectionList.func_212330_a(() -> {
                return p_214329_1_;
            }, false);
        });
        this.selectionList = new WorldSelectionList(this, this.mc, this.width, this.height, 48, this.height - 64, 36, () ->
        {
            return this.searchField.getText();
        }, this.selectionList);
        this.children.add(this.searchField);
        this.children.add(this.selectionList);
        this.selectButton = this.addButton(new Button(this.width / 2 - 154, this.height - 52, 150, 20, new TranslationTextComponent("selectWorld.select"), (p_214325_1_) ->
        {
            this.selectionList.func_214376_a().ifPresent(WorldSelectionList.Entry::func_214438_a);
        }));
        this.addButton(new Button(this.width / 2 + 4, this.height - 52, 150, 20, new TranslationTextComponent("selectWorld.create"), (p_214326_1_) ->
        {
            this.mc.displayGuiScreen(CreateWorldScreen.func_243425_a(this));
        }));
        this.renameButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 72, 20, new TranslationTextComponent("selectWorld.edit"), (p_214323_1_) ->
        {
            this.selectionList.func_214376_a().ifPresent(WorldSelectionList.Entry::func_214444_c);
        }));
        this.deleteButton = this.addButton(new Button(this.width / 2 - 76, this.height - 28, 72, 20, new TranslationTextComponent("selectWorld.delete"), (p_214330_1_) ->
        {
            this.selectionList.func_214376_a().ifPresent(WorldSelectionList.Entry::func_214442_b);
        }));
        this.copyButton = this.addButton(new Button(this.width / 2 + 4, this.height - 28, 72, 20, new TranslationTextComponent("selectWorld.recreate"), (p_214328_1_) ->
        {
            this.selectionList.func_214376_a().ifPresent(WorldSelectionList.Entry::func_214445_d);
        }));
        this.addButton(new Button(this.width / 2 + 82, this.height - 28, 72, 20, DialogTexts.GUI_CANCEL, (p_214327_1_) ->
        {
            this.mc.displayGuiScreen(this.prevScreen);
        }));
        this.func_214324_a(false);
        this.setFocusedDefault(this.searchField);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return super.keyPressed(keyCode, scanCode, modifiers) ? true : this.searchField.keyPressed(keyCode, scanCode, modifiers);
    }

    public void closeScreen()
    {
        this.mc.displayGuiScreen(this.prevScreen);
    }

    public boolean charTyped(char codePoint, int modifiers)
    {
        return this.searchField.charTyped(codePoint, modifiers);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.worldVersTooltip = null;
        this.selectionList.render(matrixStack, mouseX, mouseY, partialTicks);
        this.searchField.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.worldVersTooltip != null)
        {
            this.renderTooltip(matrixStack, this.worldVersTooltip, mouseX, mouseY);
        }
    }

    public void func_239026_b_(List<IReorderingProcessor> p_239026_1_)
    {
        this.worldVersTooltip = p_239026_1_;
    }

    public void func_214324_a(boolean p_214324_1_)
    {
        this.selectButton.active = p_214324_1_;
        this.deleteButton.active = p_214324_1_;
        this.renameButton.active = p_214324_1_;
        this.copyButton.active = p_214324_1_;
    }

    public void onClose()
    {
        if (this.selectionList != null)
        {
            this.selectionList.getEventListeners().forEach(WorldSelectionList.Entry::close);
        }
    }
}
