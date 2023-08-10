package fun.rich.ui.settings.impl;

import lombok.Getter;
import lombok.Setter;
import fun.rich.ui.settings.Setting;

import java.util.function.Supplier;

@Getter
@Setter
public class ColorSetting extends Setting {

    private int colorValue;

    public ColorSetting(String name, int color, Supplier<Boolean> visible) {
        this.name = name;
        this.colorValue = color;
        setVisible(visible);
    }
}
