package net.minecraft.tags;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public interface ITagCollection<T>
{
    Map<ResourceLocation, ITag<T>> getIDTagMap();

    @Nullable

default ITag<T> get(ResourceLocation resourceLocationIn)
    {
        return this.getIDTagMap().get(resourceLocationIn);
    }

    ITag<T> getTagByID(ResourceLocation id);

    @Nullable
    ResourceLocation getDirectIdFromTag(ITag<T> tag);

default ResourceLocation getValidatedIdFromTag(ITag<T> tag)
    {
        ResourceLocation resourcelocation = this.getDirectIdFromTag(tag);

        if (resourcelocation == null)
        {
            throw new IllegalStateException("Unrecognized tag");
        }
        else
        {
            return resourcelocation;
        }
    }

default Collection<ResourceLocation> getRegisteredTags()
    {
        return this.getIDTagMap().keySet();
    }

default Collection<ResourceLocation> getOwningTags(T itemIn)
    {
        List<ResourceLocation> list = Lists.newArrayList();

        for (Entry<ResourceLocation, ITag<T>> entry : this.getIDTagMap().entrySet())
        {
            if (entry.getValue().contains(itemIn))
            {
                list.add(entry.getKey());
            }
        }

        return list;
    }

default void writeTagCollectionToBuffer(PacketBuffer buffer, DefaultedRegistry<T> defaulted)
    {
        Map<ResourceLocation, ITag<T>> map = this.getIDTagMap();
        buffer.writeVarInt(map.size());

        for (Entry<ResourceLocation, ITag<T>> entry : map.entrySet())
        {
            buffer.writeResourceLocation(entry.getKey());
            buffer.writeVarInt(entry.getValue().getAllElements().size());

            for (T t : entry.getValue().getAllElements())
            {
                buffer.writeVarInt(defaulted.getId(t));
            }
        }
    }

    static <T> ITagCollection<T> readTagCollectionFromBuffer(PacketBuffer buffer, Registry<T> registry)
    {
        Map<ResourceLocation, ITag<T>> map = Maps.newHashMap();
        int i = buffer.readVarInt();

        for (int j = 0; j < i; ++j)
        {
            ResourceLocation resourcelocation = buffer.readResourceLocation();
            int k = buffer.readVarInt();
            Builder<T> builder = ImmutableSet.builder();

            for (int l = 0; l < k; ++l)
            {
                builder.add(registry.getByValue(buffer.readVarInt()));
            }

            map.put(resourcelocation, ITag.getTagOf(builder.build()));
        }

        return getTagCollectionFromMap(map);
    }

    static <T> ITagCollection<T> getEmptyTagCollection()
    {
        return getTagCollectionFromMap(ImmutableBiMap.of());
    }

    static <T> ITagCollection<T> getTagCollectionFromMap(Map<ResourceLocation, ITag<T>> idTagMap)
    {
        final BiMap<ResourceLocation, ITag<T>> bimap = ImmutableBiMap.copyOf(idTagMap);
        return new ITagCollection<T>()
        {
            private final ITag<T> emptyTag = Tag.getEmptyTag();
            public ITag<T> getTagByID(ResourceLocation id)
            {
                return bimap.getOrDefault(id, this.emptyTag);
            }
            @Nullable
            public ResourceLocation getDirectIdFromTag(ITag<T> tag)
            {
                return tag instanceof ITag.INamedTag ? ((ITag.INamedTag)tag).getName() : bimap.inverse().get(tag);
            }
            public Map<ResourceLocation, ITag<T>> getIDTagMap()
            {
                return bimap;
            }
        };
    }
}
