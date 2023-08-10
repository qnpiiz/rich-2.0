package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class StateContainer<O, S extends StateHolder<O, S>>
{
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap < String, Property<? >> properties;
    private final ImmutableList<S> validStates;

    protected StateContainer(Function<O, S> p_i231877_1_, O p_i231877_2_, StateContainer.IFactory<O, S> p_i231877_3_, Map < String, Property<? >> p_i231877_4_)
    {
        this.owner = p_i231877_2_;
        this.properties = ImmutableSortedMap.copyOf(p_i231877_4_);
        Supplier<S> supplier = () ->
        {
            return p_i231877_1_.apply(p_i231877_2_);
        };
        MapCodec<S> mapcodec = MapCodec.of(Encoder.empty(), Decoder.unit(supplier));

        for (Entry < String, Property<? >> entry : this.properties.entrySet())
        {
            mapcodec = func_241487_a_(mapcodec, supplier, entry.getKey(), entry.getValue());
        }

        MapCodec<S> mapcodec1 = mapcodec;
        Map < Map < Property<?>, Comparable<? >> , S > map = Maps.newLinkedHashMap();
        List<S> list = Lists.newArrayList();
        Stream < List < Pair < Property<?>, Comparable<? >>> > stream = Stream.of(Collections.emptyList());

        for (Property<?> property : this.properties.values())
        {
            stream = stream.flatMap((p_200999_1_) ->
            {
                return property.getAllowedValues().stream().map((p_200998_2_) -> {
                    List < Pair < Property<?>, Comparable<? >>> list1 = Lists.newArrayList(p_200999_1_);
                    list1.add(Pair.of(property, p_200998_2_));
                    return list1;
                });
            });
        }

        stream.forEach((p_201000_5_) ->
        {
            ImmutableMap < Property<?>, Comparable<? >> immutablemap = p_201000_5_.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
            S s1 = p_i231877_3_.create(p_i231877_2_, immutablemap, mapcodec1);
            map.put(immutablemap, s1);
            list.add(s1);
        });

        for (S s : list)
        {
            s.func_235899_a_(map);
        }

        this.validStates = ImmutableList.copyOf(list);
    }

    private static < S extends StateHolder <? , S > , T extends Comparable<T >> MapCodec<S> func_241487_a_(MapCodec<S> p_241487_0_, Supplier<S> p_241487_1_, String p_241487_2_, Property<T> p_241487_3_)
    {
        return Codec.mapPair(p_241487_0_, p_241487_3_.func_241492_e_().fieldOf(p_241487_2_).setPartial(() ->
        {
            return p_241487_3_.func_241489_a_(p_241487_1_.get());
        })).xmap((p_241485_1_) ->
        {
            return p_241485_1_.getFirst().with(p_241487_3_, p_241485_1_.getSecond().func_241493_b_());
        }, (p_241484_1_) ->
        {
            return Pair.of(p_241484_1_, p_241487_3_.func_241489_a_(p_241484_1_));
        });
    }

    public ImmutableList<S> getValidStates()
    {
        return this.validStates;
    }

    public S getBaseState()
    {
        return this.validStates.get(0);
    }

    public O getOwner()
    {
        return this.owner;
    }

    public Collection < Property<? >> getProperties()
    {
        return this.properties.values();
    }

    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.properties.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public Property<?> getProperty(String propertyName)
    {
        return this.properties.get(propertyName);
    }

    public static class Builder<O, S extends StateHolder<O, S>>
    {
        private final O owner;
        private final Map < String, Property<? >> properties = Maps.newHashMap();

        public Builder(O object)
        {
            this.owner = object;
        }

        public StateContainer.Builder<O, S> add(Property<?>... propertiesIn)
        {
            for (Property<?> property : propertiesIn)
            {
                this.validateProperty(property);
                this.properties.put(property.getName(), property);
            }

            return this;
        }

        private <T extends Comparable<T>> void validateProperty(Property<T> property)
        {
            String s = property.getName();

            if (!StateContainer.NAME_PATTERN.matcher(s).matches())
            {
                throw new IllegalArgumentException(this.owner + " has invalidly named property: " + s);
            }
            else
            {
                Collection<T> collection = property.getAllowedValues();

                if (collection.size() <= 1)
                {
                    throw new IllegalArgumentException(this.owner + " attempted use property " + s + " with <= 1 possible values");
                }
                else
                {
                    for (T t : collection)
                    {
                        String s1 = property.getName(t);

                        if (!StateContainer.NAME_PATTERN.matcher(s1).matches())
                        {
                            throw new IllegalArgumentException(this.owner + " has property: " + s + " with invalidly named value: " + s1);
                        }
                    }

                    if (this.properties.containsKey(s))
                    {
                        throw new IllegalArgumentException(this.owner + " has duplicate property: " + s);
                    }
                }
            }
        }

        public StateContainer<O, S> func_235882_a_(Function<O, S> p_235882_1_, StateContainer.IFactory<O, S> p_235882_2_)
        {
            return new StateContainer<>(p_235882_1_, this.owner, p_235882_2_, this.properties);
        }
    }

    public interface IFactory<O, S>
    {
        S create(O p_create_1_, ImmutableMap < Property<?>, Comparable<? >> p_create_2_, MapCodec<S> p_create_3_);
    }
}
