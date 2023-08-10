package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public interface ITag<T>
{
    static <T> Codec<ITag<T>> getTagCodec(Supplier<ITagCollection<T>> collectionSupplier)
    {
        return ResourceLocation.CODEC.flatXmap((tagId) ->
        {
            return Optional.ofNullable(collectionSupplier.get().get(tagId)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown tag: " + tagId);
            });
        }, (tag) ->
        {
            return Optional.ofNullable(collectionSupplier.get().getDirectIdFromTag(tag)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown tag: " + tag);
            });
        });
    }

    boolean contains(T element);

    List<T> getAllElements();

default T getRandomElement(Random random)
    {
        List<T> list = this.getAllElements();
        return list.get(random.nextInt(list.size()));
    }

    static <T> ITag<T> getTagOf(Set<T> elements)
    {
        return Tag.getTagFromContents(elements);
    }

    public static class Builder
    {
        private final List<ITag.Proxy> proxyTags = Lists.newArrayList();

        public static ITag.Builder create()
        {
            return new ITag.Builder();
        }

        public ITag.Builder addProxyTag(ITag.Proxy proxyTag)
        {
            this.proxyTags.add(proxyTag);
            return this;
        }

        public ITag.Builder addTag(ITag.ITagEntry tagEntry, String identifier)
        {
            return this.addProxyTag(new ITag.Proxy(tagEntry, identifier));
        }

        public ITag.Builder addItemEntry(ResourceLocation registryName, String identifier)
        {
            return this.addTag(new ITag.ItemEntry(registryName), identifier);
        }

        public ITag.Builder addTagEntry(ResourceLocation tag, String identifier)
        {
            return this.addTag(new ITag.TagEntry(tag), identifier);
        }

        public <T> Optional<ITag<T>> build(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction)
        {
            ImmutableSet.Builder<T> builder = ImmutableSet.builder();

            for (ITag.Proxy itag$proxy : this.proxyTags)
            {
                if (!itag$proxy.getEntry().matches(resourceTagFunction, resourceElementFunction, builder::add))
                {
                    return Optional.empty();
                }
            }

            return Optional.of(ITag.getTagOf(builder.build()));
        }

        public Stream<ITag.Proxy> getProxyStream()
        {
            return this.proxyTags.stream();
        }

        public <T> Stream<ITag.Proxy> getProxyTags(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction)
        {
            return this.getProxyStream().filter((tagProxy) ->
            {
                return !tagProxy.getEntry().matches(resourceTagFunction, resourceElementFunction, (tagType) -> {
                });
            });
        }

        public ITag.Builder deserialize(JsonObject json, String identifier)
        {
            JsonArray jsonarray = JSONUtils.getJsonArray(json, "values");
            List<ITag.ITagEntry> list = Lists.newArrayList();

            for (JsonElement jsonelement : jsonarray)
            {
                list.add(deserializeTagEntry(jsonelement));
            }

            if (JSONUtils.getBoolean(json, "replace", false))
            {
                this.proxyTags.clear();
            }

            list.forEach((tagEntry) ->
            {
                this.proxyTags.add(new ITag.Proxy(tagEntry, identifier));
            });
            return this;
        }

        private static ITag.ITagEntry deserializeTagEntry(JsonElement json)
        {
            String s;
            boolean flag;

            if (json.isJsonObject())
            {
                JsonObject jsonobject = json.getAsJsonObject();
                s = JSONUtils.getString(jsonobject, "id");
                flag = JSONUtils.getBoolean(jsonobject, "required", true);
            }
            else
            {
                s = JSONUtils.getString(json, "id");
                flag = true;
            }

            if (s.startsWith("#"))
            {
                ResourceLocation resourcelocation1 = new ResourceLocation(s.substring(1));
                return (ITag.ITagEntry)(flag ? new ITag.TagEntry(resourcelocation1) : new ITag.OptionalTagEntry(resourcelocation1));
            }
            else
            {
                ResourceLocation resourcelocation = new ResourceLocation(s);
                return (ITag.ITagEntry)(flag ? new ITag.ItemEntry(resourcelocation) : new ITag.OptionalItemEntry(resourcelocation));
            }
        }

        public JsonObject serialize()
        {
            JsonObject jsonobject = new JsonObject();
            JsonArray jsonarray = new JsonArray();

            for (ITag.Proxy itag$proxy : this.proxyTags)
            {
                itag$proxy.getEntry().addAdditionalData(jsonarray);
            }

            jsonobject.addProperty("replace", false);
            jsonobject.add("values", jsonarray);
            return jsonobject;
        }
    }

    public interface INamedTag<T> extends ITag<T>
    {
        ResourceLocation getName();
    }

    public interface ITagEntry
    {
        <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer);

        void addAdditionalData(JsonArray jsonArray);
    }

    public static class ItemEntry implements ITag.ITagEntry
    {
        private final ResourceLocation identifier;

        public ItemEntry(ResourceLocation identifier)
        {
            this.identifier = identifier;
        }

        public <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer)
        {
            T t = resourceElementFunction.apply(this.identifier);

            if (t == null)
            {
                return false;
            }
            else
            {
                elementConsumer.accept(t);
                return true;
            }
        }

        public void addAdditionalData(JsonArray jsonArray)
        {
            jsonArray.add(this.identifier.toString());
        }

        public String toString()
        {
            return this.identifier.toString();
        }
    }

    public static class OptionalItemEntry implements ITag.ITagEntry
    {
        private final ResourceLocation id;

        public OptionalItemEntry(ResourceLocation id)
        {
            this.id = id;
        }

        public <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer)
        {
            T t = resourceElementFunction.apply(this.id);

            if (t != null)
            {
                elementConsumer.accept(t);
            }

            return true;
        }

        public void addAdditionalData(JsonArray jsonArray)
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("id", this.id.toString());
            jsonobject.addProperty("required", false);
            jsonArray.add(jsonobject);
        }

        public String toString()
        {
            return this.id.toString() + "?";
        }
    }

    public static class OptionalTagEntry implements ITag.ITagEntry
    {
        private final ResourceLocation id;

        public OptionalTagEntry(ResourceLocation id)
        {
            this.id = id;
        }

        public <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer)
        {
            ITag<T> itag = resourceTagFunction.apply(this.id);

            if (itag != null)
            {
                itag.getAllElements().forEach(elementConsumer);
            }

            return true;
        }

        public void addAdditionalData(JsonArray jsonArray)
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("id", "#" + this.id);
            jsonobject.addProperty("required", false);
            jsonArray.add(jsonobject);
        }

        public String toString()
        {
            return "#" + this.id + "?";
        }
    }

    public static class Proxy
    {
        private final ITag.ITagEntry entry;
        private final String identifier;

        private Proxy(ITag.ITagEntry entry, String identifier)
        {
            this.entry = entry;
            this.identifier = identifier;
        }

        public ITag.ITagEntry getEntry()
        {
            return this.entry;
        }

        public String toString()
        {
            return this.entry.toString() + " (from " + this.identifier + ")";
        }
    }

    public static class TagEntry implements ITag.ITagEntry
    {
        private final ResourceLocation id;

        public TagEntry(ResourceLocation resourceLocationIn)
        {
            this.id = resourceLocationIn;
        }

        public <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer)
        {
            ITag<T> itag = resourceTagFunction.apply(this.id);

            if (itag == null)
            {
                return false;
            }
            else
            {
                itag.getAllElements().forEach(elementConsumer);
                return true;
            }
        }

        public void addAdditionalData(JsonArray jsonArray)
        {
            jsonArray.add("#" + this.id);
        }

        public String toString()
        {
            return "#" + this.id;
        }
    }
}
