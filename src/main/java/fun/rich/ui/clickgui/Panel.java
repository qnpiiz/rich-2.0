package fun.rich.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.rich.Rich;
import fun.rich.feature.impl.hud.ClickGUI;
import fun.rich.ui.clickgui.component.AnimationState;
import fun.rich.ui.clickgui.component.Component;
import fun.rich.ui.clickgui.component.DraggablePanel;
import fun.rich.ui.clickgui.component.ExpandableComponent;
import fun.rich.ui.clickgui.component.impl.ModuleComponent;
import fun.rich.utils.render.RenderUtils;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import fun.rich.feature.Feature;
import fun.rich.feature.impl.FeatureCategory;

import java.awt.*;
import java.util.List;

public class Panel extends DraggablePanel {

    protected final Minecraft mc = Minecraft.getInstance();

    public static final int HEADER_WIDTH = 107;
    public static final int X_ITEM_OFFSET = 1;
    public static final int ITEM_HEIGHT = 15;
    public static final int HEADER_HEIGHT = 17;
    public List<Feature> features;
    public FeatureCategory type;
    public AnimationState state;
    private int prevX;
    private int prevY;
    private boolean dragging;

    public Panel(FeatureCategory category, int x, int y) {
        super(null, category.name(), x, y, HEADER_WIDTH, HEADER_HEIGHT);
        int moduleY = HEADER_HEIGHT;
        this.state = AnimationState.STATIC;
        this.features = Rich.instance.featureManager.getFeaturesCategory(category);
        for (Feature feature : features) {
            this.components.add(new ModuleComponent(this, feature, X_ITEM_OFFSET, moduleY, HEADER_WIDTH - (X_ITEM_OFFSET * 2), ITEM_HEIGHT));
            moduleY += ITEM_HEIGHT;
        }

        this.type = category;
    }

    @Override
    public void drawComponent(MainWindow scaledResolution, int mouseX, int mouseY, MatrixStack matrixStack) {
        if (dragging) {
            setX(mouseX - prevX);
            setY(mouseY - prevY);
        }

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        int headerHeight;
        int heightWithExpand = getHeightWithExpand();
        headerHeight = (isExpanded() ? heightWithExpand : height);

        float startAlpha1 = 0.14f;
        int size1 = 25;
        float left1 = x + 1.0f;
        float right1 = x + width;
        float bottom1 = y + headerHeight - 6.0f;
        float top1 = y + headerHeight - 2.0f;
        float top2 = y + 13.0f;
        Color color = new Color(ClickGUI.color.getColorValue());
        float extendedHeight = 2;

        RenderUtils.drawSmoothRect(x, y + 14.5f, x + width, y + headerHeight - extendedHeight, new Color(20, 20, 20, 150).getRGB(), matrixStack);
        RenderUtils.drawGradientSideways(x - 1, y + headerHeight - extendedHeight, x + width + 1, y + headerHeight - extendedHeight + 2 , color.getRGB(), color.darker().getRGB(), matrixStack);
        RenderUtils.drawBlurredShadow(x - 1, y + headerHeight - extendedHeight - 1.5f, width + 1, extendedHeight + 2, 5, RenderUtils.injectAlpha(color.darker(), 115));

        RenderUtils.drawColorRect(x - 4, y,  x + width + 3,  y + this.getHeight() - 2.5f, new Color(15, 15, 15, 230), new Color(15, 15, 15, 230),new Color(25, 25, 25, 230),new Color(35, 35, 35, 230), matrixStack);
        mc.rubik_16.drawCenteredString(getName().toUpperCase(), x + 53.5f, y + HEADER_HEIGHT / 2F - 4, Color.LIGHT_GRAY.getRGB(), matrixStack);

        super.drawComponent(scaledResolution, mouseX, mouseY, matrixStack);

        if (isExpanded()) {
            for (fun.rich.ui.clickgui.component.Component component : components) {
                component.setY(height);
                component.drawComponent(scaledResolution, mouseX, mouseY, matrixStack);
                int cHeight = component.getHeight();
                if (component instanceof ExpandableComponent) {
                    ExpandableComponent expandableComponent = (ExpandableComponent) component;
                    if (expandableComponent.isExpanded())
                        cHeight = expandableComponent.getHeightWithExpand() + 5;
                }

                height += cHeight;
            }
        }
    }

    @Override
    public void onPress(int mouseX, int mouseY, int button) {
        if (button == 0 && !this.dragging) {
            dragging = true;
            prevX = mouseX - getX();
            prevY = mouseY - getY();
        }
    }

    @Override
    public void onMouseRelease(int button) {
        super.onMouseRelease(button);
        dragging = false;
    }

    @Override
    public boolean canExpand() {
        return !features.isEmpty();
    }

    @Override
    public int getHeightWithExpand() {
        int height = getHeight();
        if (isExpanded()) {
            for (Component component : components) {
                int cHeight = component.getHeight();
                if (component instanceof ExpandableComponent) {
                    ExpandableComponent expandableComponent = (ExpandableComponent) component;
                    if (expandableComponent.isExpanded())
                        cHeight = expandableComponent.getHeightWithExpand() + 5;
                }
                height += cHeight;
            }
        }

        return height;
    }
}
