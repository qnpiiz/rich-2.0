package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class SerializableTickList<T> implements ITickList<T>
{
    private final List<SerializableTickList.TickHolder<T>> ticks;
    private final Function<T, ResourceLocation> toId;

    public SerializableTickList(Function<T, ResourceLocation> p_i231603_1_, List<NextTickListEntry<T>> p_i231603_2_, long p_i231603_3_)
    {
        this(p_i231603_1_, p_i231603_2_.stream().map((p_234854_2_) ->
        {
            return new SerializableTickList.TickHolder<>(p_234854_2_.getTarget(), p_234854_2_.position, (int)(p_234854_2_.field_235017_b_ - p_i231603_3_), p_234854_2_.priority);
        }).collect(Collectors.toList()));
    }

    private SerializableTickList(Function<T, ResourceLocation> p_i50010_1_, List<SerializableTickList.TickHolder<T>> p_i50010_2_)
    {
        this.ticks = p_i50010_2_;
        this.toId = p_i50010_1_;
    }

    public boolean isTickScheduled(BlockPos pos, T itemIn)
    {
        return false;
    }

    public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority)
    {
        this.ticks.add(new SerializableTickList.TickHolder<>(itemIn, pos, scheduledTime, priority));
    }

    /**
     * Checks if this position/item is scheduled to be updated this tick
     */
    public boolean isTickPending(BlockPos pos, T obj)
    {
        return false;
    }

    public ListNBT func_234857_b_()
    {
        ListNBT listnbt = new ListNBT();

        for (SerializableTickList.TickHolder<T> tickholder : this.ticks)
        {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("i", this.toId.apply(tickholder.field_234861_d_).toString());
            compoundnbt.putInt("x", tickholder.field_234858_a_.getX());
            compoundnbt.putInt("y", tickholder.field_234858_a_.getY());
            compoundnbt.putInt("z", tickholder.field_234858_a_.getZ());
            compoundnbt.putInt("t", tickholder.field_234859_b_);
            compoundnbt.putInt("p", tickholder.field_234860_c_.getPriority());
            listnbt.add(compoundnbt);
        }

        return listnbt;
    }

    public static <T> SerializableTickList<T> create(ListNBT p_222984_0_, Function<T, ResourceLocation> p_222984_1_, Function<ResourceLocation, T> p_222984_2_)
    {
        List<SerializableTickList.TickHolder<T>> list = Lists.newArrayList();

        for (int i = 0; i < p_222984_0_.size(); ++i)
        {
            CompoundNBT compoundnbt = p_222984_0_.getCompound(i);
            T t = p_222984_2_.apply(new ResourceLocation(compoundnbt.getString("i")));

            if (t != null)
            {
                BlockPos blockpos = new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z"));
                list.add(new SerializableTickList.TickHolder<>(t, blockpos, compoundnbt.getInt("t"), TickPriority.getPriority(compoundnbt.getInt("p"))));
            }
        }

        return new SerializableTickList<>(p_222984_1_, list);
    }

    public void func_234855_a_(ITickList<T> p_234855_1_)
    {
        this.ticks.forEach((p_234856_1_) ->
        {
            p_234855_1_.scheduleTick(p_234856_1_.field_234858_a_, p_234856_1_.field_234861_d_, p_234856_1_.field_234859_b_, p_234856_1_.field_234860_c_);
        });
    }

    static class TickHolder<T>
    {
        private final T field_234861_d_;
        public final BlockPos field_234858_a_;
        public final int field_234859_b_;
        public final TickPriority field_234860_c_;

        private TickHolder(T p_i231604_1_, BlockPos p_i231604_2_, int p_i231604_3_, TickPriority p_i231604_4_)
        {
            this.field_234861_d_ = p_i231604_1_;
            this.field_234858_a_ = p_i231604_2_;
            this.field_234859_b_ = p_i231604_3_;
            this.field_234860_c_ = p_i231604_4_;
        }

        public String toString()
        {
            return this.field_234861_d_ + ": " + this.field_234858_a_ + ", " + this.field_234859_b_ + ", " + this.field_234860_c_;
        }
    }
}
