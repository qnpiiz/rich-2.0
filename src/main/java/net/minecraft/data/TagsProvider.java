package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final Registry<T> registry;
    private final Map<ResourceLocation, ITag.Builder> tagToBuilder = Maps.newLinkedHashMap();

    protected TagsProvider(DataGenerator generatorIn, Registry<T> registryIn)
    {
        this.generator = generatorIn;
        this.registry = registryIn;
    }

    protected abstract void registerTags();

    /**
     * Performs this provider's action.
     */
    public void act(DirectoryCache cache)
    {
        this.tagToBuilder.clear();
        this.registerTags();
        ITag<T> itag = Tag.getEmptyTag();
        Function<ResourceLocation, ITag<T>> function = (key) ->
        {
            return this.tagToBuilder.containsKey(key) ? itag : null;
        };
        Function<ResourceLocation, T> function1 = (key) ->
        {
            return this.registry.getOptional(key).orElse((T)null);
        };
        this.tagToBuilder.forEach((tagName, builder) ->
        {
            List<ITag.Proxy> list = builder.getProxyTags(function, function1).collect(Collectors.toList());

            if (!list.isEmpty())
            {
                throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", tagName, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
            }
            else {
                JsonObject jsonobject = builder.serialize();
                Path path = this.makePath(tagName);

                try {
                    String s = GSON.toJson((JsonElement)jsonobject);
                    String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();

                    if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path))
                    {
                        Files.createDirectories(path.getParent());

                        try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path))
                        {
                            bufferedwriter.write(s);
                        }
                    }

                    cache.recordHash(path, s1);
                }
                catch (IOException ioexception)
                {
                    LOGGER.error("Couldn't save tags to {}", path, ioexception);
                }
            }
        });
    }

    /**
     * Resolves a Path for the location to save the given tag.
     */
    protected abstract Path makePath(ResourceLocation id);

    protected TagsProvider.Builder<T> getOrCreateBuilder(ITag.INamedTag<T> tag)
    {
        ITag.Builder itag$builder = this.createBuilderIfAbsent(tag);
        return new TagsProvider.Builder<>(itag$builder, this.registry, "vanilla");
    }

    protected ITag.Builder createBuilderIfAbsent(ITag.INamedTag<T> tag)
    {
        return this.tagToBuilder.computeIfAbsent(tag.getName(), (key) ->
        {
            return new ITag.Builder();
        });
    }

    public static class Builder<T>
    {
        private final ITag.Builder builder;
        private final Registry<T> registry;
        private final String id;

        private Builder(ITag.Builder builder, Registry<T> registry, String id)
        {
            this.builder = builder;
            this.registry = registry;
            this.id = id;
        }

        public TagsProvider.Builder<T> addItemEntry(T item)
        {
            this.builder.addItemEntry(this.registry.getKey(item), this.id);
            return this;
        }

        public TagsProvider.Builder<T> addTag(ITag.INamedTag<T> tag)
        {
            this.builder.addTagEntry(tag.getName(), this.id);
            return this;
        }

        @SafeVarargs
        public final TagsProvider.Builder<T> add(T... toAdd)
        {
            Stream.<T>of(toAdd).map(this.registry::getKey).forEach((key) ->
            {
                this.builder.addItemEntry(key, this.id);
            });
            return this;
        }
    }
}
