package fun.rich.ui.clickgui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import fun.rich.feature.impl.FeatureCategory;
import fun.rich.feature.impl.hud.ClickGUI;
import fun.rich.ui.clickgui.component.Component;
import fun.rich.ui.clickgui.component.ExpandableComponent;
import fun.rich.utils.render.ColorUtils;
import fun.rich.utils.render.RenderUtils;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.List;

public class ClickGuiScreen extends Screen {

    public static boolean escapeKeyInUse;
    public float scale = 2;
    public boolean exit = false;

    public List<Panel> components = Lists.newLinkedList();
    public ScreenHelper screenHelper;
    public FeatureCategory type;
    private fun.rich.ui.clickgui.component.Component selectedPanel;
    private static ResourceLocation ANIME_GIRL;

    public ClickGuiScreen() {
        super(new StringTextComponent("ClickGui"));
        int x = 20;
        int y = 80;
        for (FeatureCategory type : FeatureCategory.values()) {
            this.type = type;
            this.components.add(new Panel(type, x, y));
            selectedPanel = new Panel(type, x, y);
            x += width + 125;
        }
        this.screenHelper = new ScreenHelper(0, 0);
    }

    @Override
    protected void init() {
        MainWindow sr = mc.getMainWindow();
        this.screenHelper = new ScreenHelper(0, 0);

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        MainWindow sr = mc.getMainWindow();

        Color color = Color.WHITE;
        Color onecolor = new Color(ClickGUI.bgcolor.getColorValue());
        switch (ClickGUI.backGroundColor.currentMode) {
            case "Astolfo":
                color = ColorUtils.astolfo(true, (int) width);
                break;
            case "Rainbow":
                color = ColorUtils.rainbow(300, 1, 1);
                break;
            case "Static":
                color = onecolor;
                break;
        }

        Color color1 = new Color(color.getRed(), color.getBlue(), color.getGreen(), 90);
        Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 180);
        RenderUtils.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), color1.getRGB(), color2.getRGB(), matrixStack);
        renderBackground(matrixStack);

        for (Panel panel : components)
            panel.drawComponent(sr, mouseX, (int) (mouseY), matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (fun.rich.ui.clickgui.component.Component panel : components) {
            if (delta > 0)
                panel.setY(panel.getY() + 15);
            if (delta < 0)
                panel.setY(panel.getY() - 15);
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.selectedPanel.onKeyPress(keyCode);
        if (!escapeKeyInUse)
            super.keyPressed(keyCode, scanCode, modifiers);
        escapeKeyInUse = false;

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Component component : components) {
            int x = component.getX();
            int y = component.getY();
            int cHeight = component.getHeight();
            if (component instanceof ExpandableComponent) {
                ExpandableComponent expandableComponent = (ExpandableComponent) component;
                if (expandableComponent.isExpanded())
                    cHeight = expandableComponent.getHeightWithExpand();
            }

            if (mouseX > x && mouseY > y && mouseX < x + component.getWidth() && mouseY < y + cHeight) {
                selectedPanel = component;
                component.onMouseClick((int) mouseX, (int) mouseY, button);
                break;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        selectedPanel.onMouseRelease(button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        this.screenHelper = new ScreenHelper(0, 0);
        super.onClose();
    }
}
