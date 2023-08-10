package fun.rich.ui.clickgui.component;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;

import java.util.List;

@Getter
public class Component {

    private final Component parent;
    protected final List<Component> components = Lists.newLinkedList();
    private final String name;
    private @Setter int x, y;
    private @Setter int width, height;

    protected final Minecraft mc = Minecraft.getInstance();

    public Component(Component parent, String name, int x, int y, int width, int height) {
        this.parent = parent;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawComponent(MainWindow scaledResolution, int mouseX, int mouseY, MatrixStack matrixStack) {
        for (Component child : components)
            child.drawComponent(scaledResolution, mouseX, mouseY, matrixStack);
    }

    public void onMouseClick(int mouseX, int mouseY, int button) {
        for (Component child : components)
            child.onMouseClick(mouseX, mouseY, button);
    }

    public void onMouseRelease(int button) {
        for (Component child : components)
            child.onMouseRelease(button);
    }

    public void onKeyPress(int keyCode) {
        for (Component child : components)
            child.onKeyPress(keyCode);
    }

    public int getX() {
        Component familyMember = parent;
        int familyTreeX = x;

        while (familyMember != null) {
            familyTreeX += familyMember.x;
            familyMember = familyMember.parent;
        }

        return familyTreeX;
    }

    public int getY() {
        Component familyMember = parent;
        int familyTreeY = y;

        while (familyMember != null) {
            familyTreeY += familyMember.y;
            familyMember = familyMember.parent;
        }

        return familyTreeY;
    }

    protected boolean isHovered(double mouseX, double mouseY) {
        double x;
        double y;
        return (mouseX >= (x = getX()) && mouseY >= (y = getY()) && mouseX < x + getWidth() && mouseY < y + getHeight());
    }
}
