package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryDumpReport implements IDataProvider
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public RegistryDumpReport(DataGenerator generator)
    {
        this.generator = generator;
    }

    /**
     * Performs this provider's action.
     */
    public void act(DirectoryCache cache) throws IOException
    {
        JsonObject jsonobject = new JsonObject();
        Registry.REGISTRY.keySet().forEach((registryId) ->
        {
            jsonobject.add(registryId.toString(), serialize(Registry.REGISTRY.getOrDefault(registryId)));
        });
        Path path = this.generator.getOutputFolder().resolve("reports/registries.json");
        IDataProvider.save(GSON, cache, jsonobject, path);
    }

    private static <T> JsonElement serialize(Registry<T> registry)
    {
        JsonObject jsonobject = new JsonObject();

        if (registry instanceof DefaultedRegistry)
        {
            ResourceLocation resourcelocation = ((DefaultedRegistry)registry).getDefaultKey();
            jsonobject.addProperty("default", resourcelocation.toString());
        }

        int j = ((Registry)Registry.REGISTRY).getId(registry);
        jsonobject.addProperty("protocol_id", j);
        JsonObject jsonobject1 = new JsonObject();

        for (ResourceLocation resourcelocation1 : registry.keySet())
        {
            T t = registry.getOrDefault(resourcelocation1);
            int i = registry.getId(t);
            JsonObject jsonobject2 = new JsonObject();
            jsonobject2.addProperty("protocol_id", i);
            jsonobject1.add(resourcelocation1.toString(), jsonobject2);
        }

        jsonobject.add("entries", jsonobject1);
        return jsonobject;
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    public String getName()
    {
        return "Registry Dump";
    }
}
