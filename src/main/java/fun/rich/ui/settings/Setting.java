package fun.rich.ui.settings;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class Setting extends Configurable {

    protected String name;
    protected Supplier<Boolean> visible;

    public boolean isVisible() {
        return visible.get();
    }

    public void setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
    }
}
