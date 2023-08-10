package net.minecraft.tags;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollectionReader<T>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int FILE_TYPE_LENGHT_VALUE = ".json".length();
    private final Function<ResourceLocation, Optional<T>> idToTagFunction;
    private final String path;
    private final String tagType;

    public TagCollectionReader(Function<ResourceLocation, Optional<T>> idToTagFunction, String path, String tagType)
    {
        this.idToTagFunction = idToTagFunction;
        this.path = path;
        this.tagType = tagType;
    }

    public CompletableFuture<Map<ResourceLocation, ITag.Builder>> readTagsFromManager(IResourceManager manager, Executor executor)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            Map<ResourceLocation, ITag.Builder> map = Maps.newHashMap();

            for (ResourceLocation resourcelocation : manager.getAllResourceLocations(this.path, (fileName) -> {
            return fileName.endsWith(".json");
            }))
            {
                String s = resourcelocation.getPath();
                ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.path.length() + 1, s.length() - FILE_TYPE_LENGHT_VALUE));

                try
                {
                    for (IResource iresource : manager.getAllResources(resourcelocation))
                    {
                        try (
                                InputStream inputstream = iresource.getInputStream();
                                Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                            )
                        {
                            JsonObject jsonobject = JSONUtils.fromJson(GSON, reader, JsonObject.class);

                            if (jsonobject == null)
                            {
                                LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it is empty or null", this.tagType, resourcelocation1, resourcelocation, iresource.getPackName());
                            }
                            else
                            {
                                map.computeIfAbsent(resourcelocation1, (id) ->
                                {
                                    return ITag.Builder.create();
                                }).deserialize(jsonobject, iresource.getPackName());
                            }
                        }
                        catch (RuntimeException | IOException ioexception)
                        {
                            LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.tagType, resourcelocation1, resourcelocation, iresource.getPackName(), ioexception);
                        }
                        finally
                        {
                            IOUtils.closeQuietly((Closeable)iresource);
                        }
                    }
                }
                catch (IOException ioexception1)
                {
                    LOGGER.error("Couldn't read {} tag list {} from {}", this.tagType, resourcelocation1, resourcelocation, ioexception1);
                }
            }

            return map;
        }, executor);
    }

    public ITagCollection<T> buildTagCollectionFromMap(Map<ResourceLocation, ITag.Builder> idToBuilderMap)
    {
        Map<ResourceLocation, ITag<T>> map = Maps.newHashMap();
        Function<ResourceLocation, ITag<T>> function = map::get;
        Function<ResourceLocation, T> function1 = (id) ->
        {
            return this.idToTagFunction.apply(id).orElse((T)null);
        };

        while (!idToBuilderMap.isEmpty())
        {
            boolean flag = false;
            Iterator<Entry<ResourceLocation, ITag.Builder>> iterator = idToBuilderMap.entrySet().iterator();

            while (iterator.hasNext())
            {
                Entry<ResourceLocation, ITag.Builder> entry = iterator.next();
                Optional<ITag<T>> optional = entry.getValue().build(function, function1);

                if (optional.isPresent())
                {
                    map.put(entry.getKey(), optional.get());
                    iterator.remove();
                    flag = true;
                }
            }

            if (!flag)
            {
                break;
            }
        }

        idToBuilderMap.forEach((tagID, builder) ->
        {
            LOGGER.error("Couldn't load {} tag {} as it is missing following references: {}", this.tagType, tagID, builder.getProxyTags(function, function1).map(Objects::toString).collect(Collectors.joining(",")));
        });
        return ITagCollection.getTagCollectionFromMap(map);
    }
}
