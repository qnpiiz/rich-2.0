package fun.rich.ui.settings.impl;

import lombok.Getter;
import fun.rich.ui.settings.Setting;

import java.util.Arrays;
import java.util.List;

@Getter
public class MultipleBoolSetting extends Setting {

    private final List<BooleanSetting> boolSettings;

    public MultipleBoolSetting(String name, BooleanSetting... booleanSettings) {
        this.name = name;
        boolSettings = Arrays.asList(booleanSettings);
    }

    public BooleanSetting getSetting(String settingName) {
        return boolSettings.stream().filter(booleanSetting -> booleanSetting.getName().equals(settingName)).findFirst().orElse(null);
    }
}
