package fun.rich.ui.settings.impl;

import lombok.Getter;
import fun.rich.ui.settings.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class ListSetting extends Setting {

    public final List<String> modes;
    public String currentMode;
    public int index;

    public ListSetting(String name, String currentMode, Supplier<Boolean> visible, String... options) {
        this.name = name;
        this.modes = Arrays.asList(options);
        this.index = modes.indexOf(currentMode);
        this.currentMode = modes.get(index);
        setVisible(visible);
        addSettings(this);
    }

    public void setListMode(String selected) {
        this.currentMode = selected;
        this.index = this.modes.indexOf(selected);
    }

    public String getOptions() {
        return this.modes.get(this.index);
    }
}
