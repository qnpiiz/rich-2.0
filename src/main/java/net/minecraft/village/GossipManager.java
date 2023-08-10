package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.Util;

public class GossipManager
{
    private final Map<UUID, GossipManager.Gossips> uuid_gossips_mapping = Maps.newHashMap();

    public void tick()
    {
        Iterator<GossipManager.Gossips> iterator = this.uuid_gossips_mapping.values().iterator();

        while (iterator.hasNext())
        {
            GossipManager.Gossips gossipmanager$gossips = iterator.next();
            gossipmanager$gossips.decay();

            if (gossipmanager$gossips.isGossipTypeMapEmpty())
            {
                iterator.remove();
            }
        }
    }

    private Stream<GossipManager.GossipEntry> getGossipEntries()
    {
        return this.uuid_gossips_mapping.entrySet().stream().flatMap((uniqueGossipEntry) ->
        {
            return uniqueGossipEntry.getValue().unpack(uniqueGossipEntry.getKey());
        });
    }

    private Collection<GossipManager.GossipEntry> selectGossipsForTransfer(Random rand, int gossipAmount)
    {
        List<GossipManager.GossipEntry> list = this.getGossipEntries().collect(Collectors.toList());

        if (list.isEmpty())
        {
            return Collections.emptyList();
        }
        else
        {
            int[] aint = new int[list.size()];
            int i = 0;

            for (int j = 0; j < list.size(); ++j)
            {
                GossipManager.GossipEntry gossipmanager$gossipentry = list.get(j);
                i += Math.abs(gossipmanager$gossipentry.weightedValue());
                aint[j] = i - 1;
            }

            Set<GossipManager.GossipEntry> set = Sets.newIdentityHashSet();

            for (int i1 = 0; i1 < gossipAmount; ++i1)
            {
                int k = rand.nextInt(i);
                int l = Arrays.binarySearch(aint, k);
                set.add(list.get(l < 0 ? -l - 1 : l));
            }

            return set;
        }
    }

    private GossipManager.Gossips getOrCreate(UUID identifier)
    {
        return this.uuid_gossips_mapping.computeIfAbsent(identifier, (id) ->
        {
            return new GossipManager.Gossips();
        });
    }

    public void transferFrom(GossipManager gossip, Random rand, int gossipAmount)
    {
        Collection<GossipManager.GossipEntry> collection = gossip.selectGossipsForTransfer(rand, gossipAmount);
        collection.forEach((gossipEntry) ->
        {
            int i = gossipEntry.value - gossipEntry.type.decayPerTransfer;

            if (i >= 2)
            {
                this.getOrCreate(gossipEntry.target).gossipTypeMap.mergeInt(gossipEntry.type, i, GossipManager::getMax);
            }
        });
    }

    public int getReputation(UUID identifier, Predicate<GossipType> gossip)
    {
        GossipManager.Gossips gossipmanager$gossips = this.uuid_gossips_mapping.get(identifier);
        return gossipmanager$gossips != null ? gossipmanager$gossips.weightedValue(gossip) : 0;
    }

    public void add(UUID identifier, GossipType gossipType, int gossipValue)
    {
        GossipManager.Gossips gossipmanager$gossips = this.getOrCreate(identifier);
        gossipmanager$gossips.gossipTypeMap.mergeInt(gossipType, gossipValue, (p_220915_2_, p_220915_3_) ->
        {
            return this.mergeValuesForAddition(gossipType, p_220915_2_, p_220915_3_);
        });
        gossipmanager$gossips.putGossipType(gossipType);

        if (gossipmanager$gossips.isGossipTypeMapEmpty())
        {
            this.uuid_gossips_mapping.remove(identifier);
        }
    }

    public <T> Dynamic<T> write(DynamicOps<T> dynamic)
    {
        return new Dynamic<>(dynamic, dynamic.createList(this.getGossipEntries().map((gossipEntry) ->
        {
            return gossipEntry.write(dynamic);
        }).map(Dynamic::getValue)));
    }

    public void read(Dynamic<?> dynamic)
    {
        dynamic.asStream().map(GossipManager.GossipEntry::read).flatMap((p_234056_0_) ->
        {
            return Util.streamOptional(p_234056_0_.result());
        }).forEach((gossipEntry) ->
        {
            this.getOrCreate(gossipEntry.target).gossipTypeMap.put(gossipEntry.type, gossipEntry.value);
        });
    }

    /**
     * Returns the greater of two int values
     */
    private static int getMax(int value1, int value2)
    {
        return Math.max(value1, value2);
    }

    private int mergeValuesForAddition(GossipType gossipTypeIn, int existing, int additive)
    {
        int i = existing + additive;
        return i > gossipTypeIn.max ? Math.max(gossipTypeIn.max, existing) : i;
    }

    static class GossipEntry
    {
        public final UUID target;
        public final GossipType type;
        public final int value;

        public GossipEntry(UUID target, GossipType type, int value)
        {
            this.target = target;
            this.type = type;
            this.value = value;
        }

        public int weightedValue()
        {
            return this.value * this.type.weight;
        }

        public String toString()
        {
            return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + '}';
        }

        public <T> Dynamic<T> write(DynamicOps<T> dynamic)
        {
            return new Dynamic<>(dynamic, dynamic.createMap(ImmutableMap.of(dynamic.createString("Target"), UUIDCodec.CODEC.encodeStart(dynamic, this.target).result().orElseThrow(RuntimeException::new), dynamic.createString("Type"), dynamic.createString(this.type.id), dynamic.createString("Value"), dynamic.createInt(this.value))));
        }

        public static DataResult<GossipManager.GossipEntry> read(Dynamic<?> dynamic)
        {
            return DataResult.unbox(DataResult.instance().group(dynamic.get("Target").read(UUIDCodec.CODEC), dynamic.get("Type").asString().map(GossipType::byId), dynamic.get("Value").asNumber().map(Number::intValue)).apply(DataResult.instance(), GossipManager.GossipEntry::new));
        }
    }

    static class Gossips
    {
        private final Object2IntMap<GossipType> gossipTypeMap = new Object2IntOpenHashMap<>();

        private Gossips()
        {
        }

        public int weightedValue(Predicate<GossipType> gossipType)
        {
            return this.gossipTypeMap.object2IntEntrySet().stream().filter((p_220898_1_) ->
            {
                return gossipType.test(p_220898_1_.getKey());
            }).mapToInt((p_220894_0_) ->
            {
                return p_220894_0_.getIntValue() * (p_220894_0_.getKey()).weight;
            }).sum();
        }

        public Stream<GossipManager.GossipEntry> unpack(UUID identifier)
        {
            return this.gossipTypeMap.object2IntEntrySet().stream().map((p_220897_1_) ->
            {
                return new GossipManager.GossipEntry(identifier, p_220897_1_.getKey(), p_220897_1_.getIntValue());
            });
        }

        public void decay()
        {
            ObjectIterator<Entry<GossipType>> objectiterator = this.gossipTypeMap.object2IntEntrySet().iterator();

            while (objectiterator.hasNext())
            {
                Entry<GossipType> entry = objectiterator.next();
                int i = entry.getIntValue() - (entry.getKey()).decayPerDay;

                if (i < 2)
                {
                    objectiterator.remove();
                }
                else
                {
                    entry.setValue(i);
                }
            }
        }

        public boolean isGossipTypeMapEmpty()
        {
            return this.gossipTypeMap.isEmpty();
        }

        public void putGossipType(GossipType gossipType)
        {
            int i = this.gossipTypeMap.getInt(gossipType);

            if (i > gossipType.max)
            {
                this.gossipTypeMap.put(gossipType, gossipType.max);
            }

            if (i < 2)
            {
                this.removeGossipType(gossipType);
            }
        }

        public void removeGossipType(GossipType gossipType)
        {
            this.gossipTypeMap.removeInt(gossipType);
        }
    }
}
