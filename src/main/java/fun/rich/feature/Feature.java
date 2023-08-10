package fun.rich.feature;

import fun.rich.event.EventManager;
import fun.rich.feature.impl.FeatureCategory;
import fun.rich.ui.notification.NotificationMode;
import fun.rich.ui.notification.NotificationRenderer;
import fun.rich.ui.settings.impl.BooleanSetting;
import fun.rich.ui.settings.impl.ColorSetting;
import fun.rich.ui.settings.impl.ListSetting;
import fun.rich.ui.settings.impl.NumberSetting;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import fun.rich.ui.settings.Configurable;
import fun.rich.ui.settings.Setting;
import russia.ui.settings.impl.*;

@Getter
public class Feature extends Configurable {

    protected Minecraft mc = Minecraft.getInstance();

    private final FeatureCategory category;
    private final String label;

    private boolean enabled;
    private @Setter String suffix;
    private @Setter int bind;

    public Feature(String label, FeatureCategory category) {
        this.label = label;
        this.category = category;
        this.bind = GLFW.GLFW_KEY_UNKNOWN;
        this.enabled = false;
    }

    public String getDisplayName() {
        return suffix == null ? label : label.concat(" ").concat(suffix);
    }

    public void onEnable() {
        if (!label.contains("ClickGui") && !label.contains("Notifications"))
            NotificationRenderer.queue("Feature", label+" was "+TextFormatting.GREEN+"Enabled", 1, NotificationMode.INFO);

        EventManager.register(this);
    }

    public void onDisable() {
        if (!label.contains("ClickGui") && !label.contains("Notifications"))
            NotificationRenderer.queue("Feature", label+" was "+TextFormatting.RED+"Disabled", 1, NotificationMode.INFO);

        EventManager.unregister(this);
    }

    public void toggle() {
        this.enabled = !this.enabled;

        if (enabled)
            onEnable();
        else
            onDisable();
    }

    public void setEnabled(boolean enabled) {
        if (enabled)
            EventManager.register(this);
        else
            EventManager.unregister(this);

        this.enabled = enabled;
    }

    public JSONObject save() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", isEnabled());
        jsonObject.put("keyIndex", bind);

        JSONObject propertiesObject = new JSONObject();
        for (Setting set : this.getSettings()) {
            if (getSettings() != null) {
                if (set instanceof BooleanSetting)
                    propertiesObject.put(set.getName(), ((BooleanSetting) set).getBoolValue());
                else if (set instanceof ListSetting)
                    propertiesObject.put(set.getName(), ((ListSetting) set).getCurrentMode());
                else if (set instanceof NumberSetting)
                    propertiesObject.put(set.getName(), ((NumberSetting) set).getNumberValue());
                else if (set instanceof ColorSetting)
                    propertiesObject.put(set.getName(), ((ColorSetting) set).getColorValue());
            }

            jsonObject.put("Settings", propertiesObject);
        }

        return jsonObject;
    }

    public void load(JSONObject jsonObject) {
        if (jsonObject != null) {
            if (jsonObject.has("state"))
                this.setEnabled(jsonObject.getBoolean("state"));
            if (jsonObject.has("keyIndex"))
                this.setBind(jsonObject.getInt("keyIndex"));

            if (jsonObject.has("Settings")) {
                for (Setting set : getSettings()) {
                    JSONObject propertiesObject = jsonObject.getJSONObject("Settings");

                    if (set instanceof BooleanSetting)
                        ((BooleanSetting) set).setBoolValue(propertiesObject.getBoolean(set.getName()));
                    else if (set instanceof ListSetting)
                        ((ListSetting) set).setListMode(propertiesObject.getString(set.getName()));
                    else if (set instanceof NumberSetting)
                        ((NumberSetting) set).setValueNumber(propertiesObject.getFloat(set.getName()));
                    else if (set instanceof ColorSetting)
                        ((ColorSetting) set).setColorValue(propertiesObject.getInt(set.getName()));
                }
            }
        }
    }
}
