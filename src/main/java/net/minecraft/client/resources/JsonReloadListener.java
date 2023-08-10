package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonReloadListener extends ReloadListener<Map<ResourceLocation, JsonElement>>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final Gson gson;
    private final String folder;

    public JsonReloadListener(Gson p_i51536_1_, String p_i51536_2_)
    {
        this.gson = p_i51536_1_;
        this.folder = p_i51536_2_;
    }

    protected Map<ResourceLocation, JsonElement> prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
        int i = this.folder.length() + 1;

        for (ResourceLocation resourcelocation : resourceManagerIn.getAllResourceLocations(this.folder, (p_223379_0_) ->
    {
        return p_223379_0_.endsWith(".json");
        }))
        {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(i, s.length() - JSON_EXTENSION_LENGTH));

            try (
                    IResource iresource = resourceManagerIn.getResource(resourcelocation);
                    InputStream inputstream = iresource.getInputStream();
                    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                )
            {
                JsonElement jsonelement = JSONUtils.fromJson(this.gson, reader, JsonElement.class);

                if (jsonelement != null)
                {
                    JsonElement jsonelement1 = map.put(resourcelocation1, jsonelement);

                    if (jsonelement1 != null)
                    {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                    }
                }
                else
                {
                    LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourcelocation1, resourcelocation);
                }
            }
            catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception)
            {
                LOGGER.error("Couldn't parse data file {} from {}", resourcelocation1, resourcelocation, jsonparseexception);
            }
        }
        return map;
    }
}
