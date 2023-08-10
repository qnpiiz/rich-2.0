package fun.rich.ui.settings.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import fun.rich.ui.settings.Setting;

import java.util.function.Supplier;

@Getter
@Setter
public class NumberSetting extends Setting {

    private final NumberType type;
    private float current, minimum, maximum, increment;
    private String desc;

    public NumberSetting(String name, float current, float minimum, float maximum, float increment, Supplier<Boolean> visible) {
        this.name = name;
        this.minimum = minimum;
        this.current = current;
        this.maximum = maximum;
        this.increment = increment;
        this.type = NumberType.DEFAULT;
        setVisible(visible);
    }

    public NumberSetting(String name, float current, float minimum, float maximum, float increment, Supplier<Boolean> visible, NumberType type) {
        this.name = name;
        this.minimum = minimum;
        this.current = current;
        this.maximum = maximum;
        this.increment = increment;
        this.type = type;
        setVisible(visible);
    }

    public NumberSetting(String name, String desc, float current, float minimum, float maximum, float increment, Supplier<Boolean> visible) {
        this.name = name;
        this.desc = desc;
        this.minimum = minimum;
        this.current = current;
        this.maximum = maximum;
        this.increment = increment;
        this.type = NumberType.DEFAULT;
        setVisible(visible);
    }

    public NumberSetting(String name, String desc, float current, float minimum, float maximum, float increment, Supplier<Boolean> visible, NumberType type) {
        this.name = name;
        this.desc = desc;
        this.minimum = minimum;
        this.current = current;
        this.maximum = maximum;
        this.increment = increment;
        this.type = type;
        setVisible(visible);
    }

    public float getMinValue() {
        return minimum;
    }

    public void setMinValue(float minimum) {
        this.minimum = minimum;
    }

    public float getMaxValue() {
        return maximum;
    }

    public void setMaxValue(float maximum) {
        this.maximum = maximum;
    }

    public float getNumberValue() {
        return current;
    }

    public void setValueNumber(float current) {
        this.current = current;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Getter
    @AllArgsConstructor
    public enum NumberType {

        MS("Ms"),
        APS("Aps"),
        SIZE("Size"),
        PERCENTAGE("Percentage"),
        DISTANCE("Distance"),
        DEFAULT("");

        String name;
    }
}
