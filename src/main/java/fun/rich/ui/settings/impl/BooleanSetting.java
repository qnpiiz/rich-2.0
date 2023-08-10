package fun.rich.ui.settings.impl;

import fun.rich.ui.settings.Setting;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public class BooleanSetting extends Setting {

    private boolean state;
    private @Getter @Setter String desc;

    public BooleanSetting(String name, String desc, boolean state, Supplier<Boolean> visible) {
        this.name = name;
        this.desc = desc;
        this.state = state;
        setVisible(visible);
    }

    public BooleanSetting(String name, boolean state, Supplier<Boolean> visible) {
        this.name = name;
        this.state = state;
        setVisible(visible);
    }
    public BooleanSetting(String name, boolean state) {
        this.name = name;
        this.state = state;
    }

    public BooleanSetting(String name) {
        this(name, false);
    }

    public boolean getBoolValue() {
        return state;
    }

    public void setBoolValue(boolean state) {
        this.state = state;
    }
}
