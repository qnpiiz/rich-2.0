package net.minecraft.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U>
{
    protected final List<WeightedList.Entry<U>> field_220658_a;
    private final Random random = new Random();

    public WeightedList()
    {
        this(Lists.newArrayList());
    }

    private WeightedList(List<WeightedList.Entry<U>> p_i231541_1_)
    {
        this.field_220658_a = Lists.newArrayList(p_i231541_1_);
    }

    public static <U> Codec<WeightedList<U>> func_234002_a_(Codec<U> p_234002_0_)
    {
        return WeightedList.Entry.<U>func_234008_a_(p_234002_0_).listOf().xmap(WeightedList::new, (p_234001_0_) ->
        {
            return p_234001_0_.field_220658_a;
        });
    }

    public WeightedList<U> func_226313_a_(U p_226313_1_, int p_226313_2_)
    {
        this.field_220658_a.add(new WeightedList.Entry(p_226313_1_, p_226313_2_));
        return this;
    }

    public WeightedList<U> func_226309_a_()
    {
        return this.func_226314_a_(this.random);
    }

    public WeightedList<U> func_226314_a_(Random p_226314_1_)
    {
        this.field_220658_a.forEach((p_234004_1_) ->
        {
            p_234004_1_.func_220648_a(p_226314_1_.nextFloat());
        });
        this.field_220658_a.sort(Comparator.comparingDouble((p_234003_0_) ->
        {
            return p_234003_0_.func_220649_a();
        }));
        return this;
    }

    public boolean func_234005_b_()
    {
        return this.field_220658_a.isEmpty();
    }

    public Stream<U> func_220655_b()
    {
        return this.field_220658_a.stream().map(WeightedList.Entry::func_220647_b);
    }

    public U func_226318_b_(Random p_226318_1_)
    {
        return this.func_226314_a_(p_226318_1_).func_220655_b().findFirst().orElseThrow(RuntimeException::new);
    }

    public String toString()
    {
        return "WeightedList[" + this.field_220658_a + "]";
    }

    public static class Entry<T>
    {
        private final T field_220651_b;
        private final int field_220652_c;
        private double field_220653_d;

        private Entry(T p_i231542_1_, int p_i231542_2_)
        {
            this.field_220652_c = p_i231542_2_;
            this.field_220651_b = p_i231542_1_;
        }

        private double func_220649_a()
        {
            return this.field_220653_d;
        }

        private void func_220648_a(float p_220648_1_)
        {
            this.field_220653_d = -Math.pow((double)p_220648_1_, (double)(1.0F / (float)this.field_220652_c));
        }

        public T func_220647_b()
        {
            return this.field_220651_b;
        }

        public String toString()
        {
            return "" + this.field_220652_c + ":" + this.field_220651_b;
        }

        public static <E> Codec<WeightedList.Entry<E>> func_234008_a_(final Codec<E> p_234008_0_)
        {
            return new Codec<WeightedList.Entry<E>>()
            {
                public <T> DataResult<Pair<WeightedList.Entry<E>, T>> decode(DynamicOps<T> p_decode_1_, T p_decode_2_)
                {
                    Dynamic<T> dynamic = new Dynamic<>(p_decode_1_, p_decode_2_);
                    return dynamic.get("data").flatMap(p_234008_0_::parse).map((p_234012_1_) ->
                    {
                        return new WeightedList.Entry(p_234012_1_, dynamic.get("weight").asInt(1));
                    }).map((p_234013_1_) ->
                    {
                        return Pair.of(p_234013_1_, p_decode_1_.empty());
                    });
                }
                public <T> DataResult<T> encode(WeightedList.Entry<E> p_encode_1_, DynamicOps<T> p_encode_2_, T p_encode_3_)
                {
                    return p_encode_2_.mapBuilder().add("weight", p_encode_2_.createInt(p_encode_1_.field_220652_c)).add("data", p_234008_0_.encodeStart(p_encode_2_, p_encode_1_.field_220651_b)).build(p_encode_3_);
                }
            };
        }
    }
}
