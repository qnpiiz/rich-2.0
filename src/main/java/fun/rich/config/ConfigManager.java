package fun.rich.config;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SuppressWarnings("LombokGetterMayBeUsed")
public final class ConfigManager extends Manager<Config> {

    public static final File configDirectory = new File("C:\\RichClient\\game\\configs", "configs");
    @Getter
    private static final List<Config> loadedConfigs = Lists.newLinkedList();

    public ConfigManager() {
        setContents(loadConfigs());
        configDirectory.mkdirs();
    }

    private static List<Config> loadConfigs() {
        File[] files = configDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (FilenameUtils.getExtension(file.getName()).equals("json"))
                    loadedConfigs.add(new Config(FilenameUtils.removeExtension(file.getName())));
            }
        }

        return loadedConfigs;
    }

    public void load() {
        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }
        if (configDirectory != null) {
            File[] files = configDirectory.listFiles(f -> !f.isDirectory() && FilenameUtils.getExtension(f.getName()).equals("json"));
            for (File f : files) {
                Config config = new Config(FilenameUtils.removeExtension(f.getName()).replace(" ", ""));
                loadedConfigs.add(config);
            }
        }
    }

    public boolean loadConfig(String configName) {
        if (configName == null)
            return false;
        Config config = findConfig(configName);
        if (config == null)
            return false;
        try {
            JSONObject object = new JSONObject(FileUtils.readFileToString(config.getFile(), StandardCharsets.UTF_8));
            config.load(object);
            return true;
        } catch (Throwable exception) {
            return false;
        }
    }

    public boolean saveConfig(String configName) {
        if (configName == null)
            return false;
        Config config;
        if ((config = findConfig(configName)) == null) {
            Config newConfig = (config = new Config(configName));
            getContents().add(newConfig);
        }

        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(config.save());
        try {
            FileWriter writer = new FileWriter(config.getFile());
            writer.write(contentPrettyPrint);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Config findConfig(String configName) {
        if (configName == null) return null;
        for (Config config : getContents()) {
            if (config.getName().equalsIgnoreCase(configName))
                return config;
        }

        if (new File(configDirectory, configName + ".json").exists())
            return new Config(configName);

        return null;
    }

    public boolean deleteConfig(String configName) {
        if (configName == null)
            return false;
        Config config;
        if ((config = findConfig(configName)) != null) {
            final File f = config.getFile();
            getContents().remove(config);
            return f.exists() && f.delete();
        }
        return false;
    }
}
