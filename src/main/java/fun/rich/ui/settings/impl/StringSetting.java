package fun.rich.ui.settings.impl;

import lombok.Getter;
import lombok.Setter;
import fun.rich.ui.settings.Setting;

import java.util.function.Supplier;

@Getter
@Setter
public class StringSetting extends Setting {

    public String defaultText;
    public String currentText;

    public StringSetting(String name, String defaultText, String currentText, Supplier<Boolean> visible) {
        this.name = name;
        this.defaultText = defaultText;
        this.currentText = currentText;
        setVisible(visible);
    }
}
