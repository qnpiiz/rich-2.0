package fun.rich.config;

import fun.rich.Rich;
import fun.rich.feature.Feature;
import lombok.Getter;
import org.json.JSONObject;

import java.io.File;

@Getter
public final class Config implements ConfigUpdater {

    private final String name;
    private final File file;

    public Config(String name) {
        this.name = name;
        this.file = new File(ConfigManager.configDirectory, name + ".json");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Throwable ignored) {}
        }
    }

    @Override
    public JSONObject save() {
        JSONObject jsonObject = new JSONObject();
        JSONObject modulesObject = new JSONObject();
        JSONObject panelObject = new JSONObject();

        for (Feature module : Rich.instance.featureManager.getAllFeatures())
            modulesObject.put(module.getLabel(), module.save());

        jsonObject.put("Features", modulesObject);
        return jsonObject;
    }

    @Override
    public void load(JSONObject object) {
        if (object.has("Features")) {
            JSONObject modulesObject = object.getJSONObject("Features");
            for (Feature module : Rich.instance.featureManager.getAllFeatures()) {
                module.setEnabled(false);
                module.load(modulesObject.getJSONObject(module.getLabel()));
            }
        }
    }
}
