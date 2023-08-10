package fun.rich.ui.clickgui.component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ExpandableComponent extends Component {

    private boolean expanded;

    public ExpandableComponent(Component parent, String name, int x, int y, int width, int height) {
        super(parent, name, x, y, width, height);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            onPress(mouseX, mouseY, button);
            if (canExpand() && button == 1)
                expanded = !expanded;
        }

        if (isExpanded())
            super.onMouseClick(mouseX, mouseY, button);
    }

    public abstract boolean canExpand();
    public abstract int getHeightWithExpand();
    public abstract void onPress(int mouseX, int mouseY, int button);
}
