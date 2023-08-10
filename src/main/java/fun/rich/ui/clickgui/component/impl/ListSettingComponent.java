package fun.rich.ui.clickgui.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import fun.rich.feature.impl.hud.ClickGUI;
import fun.rich.ui.clickgui.Panel;
import fun.rich.ui.clickgui.component.Component;
import fun.rich.ui.clickgui.component.ExpandableComponent;
import fun.rich.ui.clickgui.component.PropertyComponent;
import fun.rich.ui.settings.Setting;
import fun.rich.ui.settings.impl.ListSetting;
import fun.rich.utils.render.RenderUtils;

import java.awt.Color;

public class ListSettingComponent extends ExpandableComponent implements PropertyComponent {

    private final ListSetting listSetting;

    public ListSettingComponent(Component parent, ListSetting listSetting, int x, int y, int width, int height) {
        super(parent, listSetting.getName(), x, y, width, height);
        this.listSetting = listSetting;
    }

    @Override
    public void drawComponent(MainWindow scaledResolution, int mouseX, int mouseY, MatrixStack matrixStack) {
        super.drawComponent(scaledResolution, mouseX, mouseY, matrixStack);
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        String selectedText = listSetting.currentMode;
        int dropDownBoxY = y + 4;
        int textColor = 0xFFFFFF;
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(20, 20, 20, 111).getRGB(), matrixStack);
        RenderUtils.drawRect(x + 0.5F, dropDownBoxY, x + getWidth() - 0.5F, (int) (dropDownBoxY + 11), new Color(30, 30, 30).getRGB(), matrixStack);
        mc.rubik_14.drawStringWithOutline(getName(), x + 2 + Panel.X_ITEM_OFFSET, dropDownBoxY + 3, new Color(222, 222, 222).getRGB(), matrixStack);
        mc.rubik_14.drawCenteredBlurredString(selectedText, x + width - 16, dropDownBoxY + 3, 8, RenderUtils.injectAlpha(new Color(ClickGUI.color.getColorValue()), 100), ClickGUI.color.getColorValue(), matrixStack);

        if (isExpanded()) {
            RenderUtils.drawRect(x + Panel.X_ITEM_OFFSET, y + height, x + width - Panel.X_ITEM_OFFSET, y + getHeightWithExpand(), new Color(25, 25, 25, 160).getRGB(), matrixStack);
            handleRender(x, y + getHeight() + 2, width, textColor, matrixStack);
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        super.onMouseClick(mouseX, mouseY, button);
        if (isExpanded()) {
            handleClick(mouseX, mouseY, getX(), getY() + getHeight() + 2, getWidth());
        }
    }

    private void handleRender(int x, int y, int width, int textColor, MatrixStack matrixStack) {
        int color = 0;
        Color onecolor = new Color(ClickGUI.color.getColorValue());

        for (String e : listSetting.getModes()) {
            if (listSetting.currentMode.equals(e))
                mc.rubik_15.drawCenteredBlurredString(e, x + Panel.X_ITEM_OFFSET + width / 2 + 0.5f, y + 2.5F, 8, RenderUtils.injectAlpha(new Color(ClickGUI.color.getColorValue()), 60), ClickGUI.color.getColorValue(), matrixStack);
            else
                mc.rubik_15.drawCenteredString(e, x + Panel.X_ITEM_OFFSET + width / 2 + 0.5f, y + 2.5F, Color.GRAY.getRGB(), matrixStack);

            y += (Panel.ITEM_HEIGHT - 3);
        }
    }

    private void handleClick(int mouseX, int mouseY, int x, int y, int width) {
        for (String e : this.listSetting.getModes()) {
            if (mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + Panel.ITEM_HEIGHT - 3)
                listSetting.setListMode(e);

            y += Panel.ITEM_HEIGHT - 3;
        }
    }


    @Override
    public int getHeightWithExpand() {
        return getHeight() + listSetting.getModes().toArray().length * (Panel.ITEM_HEIGHT - 3);
    }

    @Override
    public void onPress(int mouseX, int mouseY, int button) {
    }

    @Override
    public boolean canExpand() {
        return listSetting.modes.toArray().length > 0;
    }

    @Override
    public Setting getSetting() {
        return listSetting;
    }
}
