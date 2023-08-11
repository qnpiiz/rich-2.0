package fun.rich.config;

import org.json.JSONObject;

public interface ConfigUpdater {

    JSONObject save();

    void load(JSONObject object);
}
