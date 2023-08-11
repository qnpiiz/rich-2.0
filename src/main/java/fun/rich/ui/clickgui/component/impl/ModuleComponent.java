package fun.rich.ui.clickgui.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.rich.ui.clickgui.component.Component;
import fun.rich.ui.clickgui.component.ExpandableComponent;
import fun.rich.ui.components.SorterHelper;
import fun.rich.ui.settings.impl.*;
import net.minecraft.client.MainWindow;
import org.lwjgl.glfw.GLFW;
import fun.rich.feature.Feature;
import fun.rich.feature.impl.hud.ClickGUI;
import fun.rich.ui.clickgui.ClickGuiScreen;
import fun.rich.ui.clickgui.Panel;
import fun.rich.ui.settings.Setting;
import fun.rich.utils.math.TimerHelper;

import java.awt.*;

public class ModuleComponent extends ExpandableComponent {

    private final Feature module;
    public static TimerHelper timerHelper = new TimerHelper();
    private boolean binding;
    private final int alpha = 0;

    public ModuleComponent(fun.rich.ui.clickgui.component.Component parent, Feature module, int x, int y, int width, int height) {
        super(parent, module.getLabel(), x, y, width, height);
        this.module = module;

        int propertyX = Panel.X_ITEM_OFFSET;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting)
                components.add(new BooleanSettingComponent(this, (BooleanSetting) setting, propertyX, height, width - (Panel.X_ITEM_OFFSET * 2), Panel.ITEM_HEIGHT + 6));
            else if (setting instanceof ColorSetting)
                components.add(new ColorPickerComponent(this, (ColorSetting) setting, propertyX, height, width - (Panel.X_ITEM_OFFSET * 2), Panel.ITEM_HEIGHT));
            else if (setting instanceof NumberSetting)
                components.add(new NumberSettingComponent(this, (NumberSetting) setting, propertyX, height, width - (Panel.X_ITEM_OFFSET * 2), Panel.ITEM_HEIGHT + 5));
            else if (setting instanceof ListSetting)
                components.add(new ListSettingComponent(this, (ListSetting) setting, propertyX, height, width - (Panel.X_ITEM_OFFSET * 2), Panel.ITEM_HEIGHT + 2));
            else if (setting instanceof MultipleBoolSetting)
                components.add(new MultipleBoolSettingComponent(this, (MultipleBoolSetting) setting, propertyX, height, width - (Panel.X_ITEM_OFFSET * 2), Panel.ITEM_HEIGHT + 1));
        }
    }

    public boolean ready = false;
    private static String i = " ";

    private String getI(String s) {
        if (!timerHelper.hasReached(5))
            return i;
        else
            timerHelper.reset();

        if (i.length() < s.length()) {
            ready = false;
            return i += s.charAt(i.length());
        }

        ready = true;
        return i;
    }

    @Override
    public void drawComponent(MainWindow scaledResolution, int mouseX, int mouseY, MatrixStack matrixStack) {
        components.sort(new SorterHelper());
        float x = getX();
        float y = getY() - 2;
        int width = getWidth();
        int height = getHeight();
        if (isExpanded()) {
            int childY = Panel.ITEM_HEIGHT;
            for (fun.rich.ui.clickgui.component.Component child : components) {
                int cHeight = child.getHeight();
                if (child instanceof BooleanSettingComponent) {
                    BooleanSettingComponent booleanSettingComponent = (BooleanSettingComponent) child;
                    if (!booleanSettingComponent.booleanSetting.isVisible())
                        continue;
                }
                if (child instanceof NumberSettingComponent) {
                    NumberSettingComponent numberSettingComponent = (NumberSettingComponent) child;
                    if (!numberSettingComponent.numberSetting.isVisible())
                        continue;
                }

                if (child instanceof ColorPickerComponent) {
                    ColorPickerComponent colorPickerComponent = (ColorPickerComponent) child;
                    if (!colorPickerComponent.getSetting().isVisible())
                        continue;
                }
                if (child instanceof ListSettingComponent) {
                    ListSettingComponent listSettingComponent = (ListSettingComponent) child;
                    if (!listSettingComponent.getSetting().isVisible())
                        continue;
                }
                if (child instanceof ExpandableComponent) {
                    ExpandableComponent expandableComponent = (ExpandableComponent) child;
                    if (expandableComponent.isExpanded())
                        cHeight = expandableComponent.getHeightWithExpand();
                }

                child.setY(childY);
                child.drawComponent(scaledResolution, mouseX, mouseY, matrixStack);
                childY += cHeight;
            }
        }

        Color color = new Color(ClickGUI.color.getColorValue());
        Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 140);
        boolean hovered = isHovered(mouseX, mouseY);

        if (components.size() > 0.5)
            mc.rubik_16.drawStringWithShadow(isExpanded() ? "" : "...", x + width - 8.5f, y + height / 2F - 3.5, -1, matrixStack);

        components.sort(new SorterHelper());
        ready = false;

        if (module.isEnabled())
            mc.rubik_17.drawCenteredStringWithShadow(binding ? "Press a key.. " + GLFW.glfwGetKeyName(module.getBind(), 0) : getName(), x + 53.5f, y + height / 2F - 3, module.isEnabled() ? color.getRGB() : Color.GRAY.getRGB(), matrixStack);
        else
            mc.rubik_17.drawCenteredStringWithShadow(binding ? "Press a key.. " + GLFW.glfwGetKeyName(module.getBind(), 0) : getName(), x + 53.5f, y + height / 2F - 3, module.isEnabled() ? new Color(color.getRGB()).getRGB() : Color.GRAY.getRGB(), matrixStack);
    }

    @Override
    public boolean canExpand() {
        return !components.isEmpty();
    }

    @Override
    public int getHeightWithExpand() {
        int height = getHeight();
        if (isExpanded()) {
            for (Component child : components) {
                int cHeight = child.getHeight();
                if (child instanceof BooleanSettingComponent) {
                    BooleanSettingComponent booleanSettingComponent = (BooleanSettingComponent) child;
                    if (!booleanSettingComponent.booleanSetting.isVisible())
                        continue;
                }
                if (child instanceof NumberSettingComponent) {
                    NumberSettingComponent numberSettingComponent = (NumberSettingComponent) child;
                    if (!numberSettingComponent.numberSetting.isVisible())
                        continue;
                }
                if (child instanceof ColorPickerComponent) {
                    ColorPickerComponent colorPickerComponent = (ColorPickerComponent) child;
                    if (!colorPickerComponent.getSetting().isVisible())
                        continue;
                }
                if (child instanceof ListSettingComponent) {
                    ListSettingComponent listSettingComponent = (ListSettingComponent) child;
                    if (!listSettingComponent.getSetting().isVisible())
                        continue;
                }
                if (child instanceof ExpandableComponent) {
                    ExpandableComponent expandableComponent = (ExpandableComponent) child;
                    if (expandableComponent.isExpanded())
                        cHeight = expandableComponent.getHeightWithExpand();
                }

                height += cHeight;
            }
        }

        return height;
    }

    @Override
    public void onPress(int mouseX, int mouseY, int button) {
        switch (button) {
            case 0:
                module.toggle();
                break;
            case 2:
                binding = !binding;
                break;
        }
    }

    @Override
    public void onKeyPress(int keyCode) {
        if (binding) {
            ClickGuiScreen.escapeKeyInUse = true;
            module.setBind(keyCode == GLFW.GLFW_KEY_DELETE ? GLFW.GLFW_KEY_UNKNOWN : keyCode);
            binding = false;
        }
    }
}
