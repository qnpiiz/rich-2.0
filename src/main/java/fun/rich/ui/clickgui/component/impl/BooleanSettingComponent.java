package fun.rich.ui.clickgui.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.rich.utils.math.AnimationHelper;
import net.minecraft.client.MainWindow;
import fun.rich.feature.impl.hud.ClickGUI;
import fun.rich.ui.clickgui.component.Component;
import fun.rich.ui.clickgui.component.PropertyComponent;
import fun.rich.ui.settings.Setting;
import fun.rich.ui.settings.impl.BooleanSetting;
import fun.rich.utils.render.RenderUtils;

import java.awt.*;

public class BooleanSettingComponent extends Component implements PropertyComponent {

    public float textHoverAnimate = 0f;
    public float leftRectAnimation = 0;
    public float rightRectAnimation = 0;
    public BooleanSetting booleanSetting;

    public BooleanSettingComponent(Component parent, BooleanSetting booleanSetting, int x, int y, int width, int height) {
        super(parent, booleanSetting.getName(), x, y, width, height);
        this.booleanSetting = booleanSetting;
    }

    @Override
    public void drawComponent(MainWindow scaledResolution, int mouseX, int mouseY, MatrixStack matrixStack) {
        if (booleanSetting.isVisible()) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();
            int middleHeight = getHeight() / 2;
            boolean hovered = isHovered(mouseX, mouseY);
            RenderUtils.drawRect(x, y, x + width, y + height, new Color(20, 20, 20, 111).getRGB(), matrixStack);
            mc.neverlose500_13.drawStringWithShadow(getName(), x + 3, y + middleHeight - 2, Color.GRAY.getRGB(), matrixStack);
            textHoverAnimate = AnimationHelper.animation(textHoverAnimate, hovered ? 2.3f : 2, 0);
            leftRectAnimation = AnimationHelper.animation(leftRectAnimation, booleanSetting.getBoolValue() ? 10 : 17, 0);
            rightRectAnimation = AnimationHelper.animation(rightRectAnimation, (booleanSetting.getBoolValue() ? 3 : 10), 0);
            RenderUtils.drawSmoothRect(x + width - 18, y + 7, x + width - 2, y + height - 5, new Color(21, 21, 21).getRGB(), matrixStack);
            RenderUtils.drawRect(x + width - leftRectAnimation, y + 7.5f, x + width - rightRectAnimation, y + height - 6, booleanSetting.getBoolValue() ? ClickGUI.color.getColorValue() : new Color(50, 50, 50).getRGB(), matrixStack);
            RenderUtils.drawBlurredShadow(x + width - leftRectAnimation + 1, y + 7.5f, rightRectAnimation + 3, 6, 8, booleanSetting.getBoolValue() ? new Color(ClickGUI.color.getColorValue()) : new Color(50, 50, 50,0));

            if (hovered) {
                if (booleanSetting.getDesc() != null) {
                    RenderUtils.drawSmoothRect(x + width + 20, y + height / 1.5F + 4.5F, x + width + 25 + mc.rubik_18.getStringWidth(booleanSetting.getDesc()), y + 6.5F, new Color(0, 0, 0, 80).getRGB(), matrixStack);
                    mc.rubik_18.drawStringWithShadow(booleanSetting.getDesc(), x + width + 22, y + height / 1.35F - 5F, -1, matrixStack);
                }
            }
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (button == 0 && isHovered(mouseX, mouseY) && booleanSetting.isVisible()) {
            booleanSetting.setBoolValue(!booleanSetting.getBoolValue());
        }
    }

    @Override
    public Setting getSetting() {
        return booleanSetting;
    }
}
