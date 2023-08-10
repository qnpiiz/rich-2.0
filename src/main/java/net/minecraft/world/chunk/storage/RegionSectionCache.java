package net.minecraft.world.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionSectionCache<R> implements AutoCloseable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final IOWorker field_227173_b_;
    private final Long2ObjectMap<Optional<R>> data = new Long2ObjectOpenHashMap<>();
    private final LongLinkedOpenHashSet dirtySections = new LongLinkedOpenHashSet();
    private final Function<Runnable, Codec<R>> field_235988_e_;
    private final Function<Runnable, R> field_219124_f;
    private final DataFixer field_219125_g;
    private final DefaultTypeReferences field_219126_h;

    public RegionSectionCache(File p_i231897_1_, Function<Runnable, Codec<R>> p_i231897_2_, Function<Runnable, R> p_i231897_3_, DataFixer p_i231897_4_, DefaultTypeReferences p_i231897_5_, boolean p_i231897_6_)
    {
        this.field_235988_e_ = p_i231897_2_;
        this.field_219124_f = p_i231897_3_;
        this.field_219125_g = p_i231897_4_;
        this.field_219126_h = p_i231897_5_;
        this.field_227173_b_ = new IOWorker(p_i231897_1_, p_i231897_6_, p_i231897_1_.getName());
    }

    protected void tick(BooleanSupplier p_219115_1_)
    {
        while (!this.dirtySections.isEmpty() && p_219115_1_.getAsBoolean())
        {
            ChunkPos chunkpos = SectionPos.from(this.dirtySections.firstLong()).asChunkPos();
            this.save(chunkpos);
        }
    }

    @Nullable
    protected Optional<R> func_219106_c(long p_219106_1_)
    {
        return this.data.get(p_219106_1_);
    }

    protected Optional<R> func_219113_d(long p_219113_1_)
    {
        SectionPos sectionpos = SectionPos.from(p_219113_1_);

        if (this.func_219114_b(sectionpos))
        {
            return Optional.empty();
        }
        else
        {
            Optional<R> optional = this.func_219106_c(p_219113_1_);

            if (optional != null)
            {
                return optional;
            }
            else
            {
                this.func_219107_b(sectionpos.asChunkPos());
                optional = this.func_219106_c(p_219113_1_);

                if (optional == null)
                {
                    throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException());
                }
                else
                {
                    return optional;
                }
            }
        }
    }

    protected boolean func_219114_b(SectionPos p_219114_1_)
    {
        return World.isYOutOfBounds(SectionPos.toWorld(p_219114_1_.getSectionY()));
    }

    protected R func_235995_e_(long p_235995_1_)
    {
        Optional<R> optional = this.func_219113_d(p_235995_1_);

        if (optional.isPresent())
        {
            return optional.get();
        }
        else
        {
            R r = this.field_219124_f.apply(() ->
            {
                this.markDirty(p_235995_1_);
            });
            this.data.put(p_235995_1_, Optional.of(r));
            return r;
        }
    }

    private void func_219107_b(ChunkPos p_219107_1_)
    {
        this.func_235992_a_(p_219107_1_, NBTDynamicOps.INSTANCE, this.func_223138_c(p_219107_1_));
    }

    @Nullable
    private CompoundNBT func_223138_c(ChunkPos p_223138_1_)
    {
        try
        {
            return this.field_227173_b_.func_227090_a_(p_223138_1_);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Error reading chunk {} data from disk", p_223138_1_, ioexception);
            return null;
        }
    }

    private <T> void func_235992_a_(ChunkPos p_235992_1_, DynamicOps<T> p_235992_2_, @Nullable T p_235992_3_)
    {
        if (p_235992_3_ == null)
        {
            for (int i = 0; i < 16; ++i)
            {
                this.data.put(SectionPos.from(p_235992_1_, i).asLong(), Optional.empty());
            }
        }
        else
        {
            Dynamic<T> dynamic1 = new Dynamic<>(p_235992_2_, p_235992_3_);
            int j = func_235993_a_(dynamic1);
            int k = SharedConstants.getVersion().getWorldVersion();
            boolean flag = j != k;
            Dynamic<T> dynamic = this.field_219125_g.update(this.field_219126_h.getTypeReference(), dynamic1, j, k);
            OptionalDynamic<T> optionaldynamic = dynamic.get("Sections");

            for (int l = 0; l < 16; ++l)
            {
                long i1 = SectionPos.from(p_235992_1_, l).asLong();
                Optional<R> optional = optionaldynamic.get(Integer.toString(l)).result().flatMap((p_235989_3_) ->
                {
                    return this.field_235988_e_.apply(() -> {
                        this.markDirty(i1);
                    }).parse(p_235989_3_).resultOrPartial(LOGGER::error);
                });
                this.data.put(i1, optional);
                optional.ifPresent((p_235990_4_) ->
                {
                    this.onSectionLoad(i1);

                    if (flag)
                    {
                        this.markDirty(i1);
                    }
                });
            }
        }
    }

    private void save(ChunkPos p_219117_1_)
    {
        Dynamic<INBT> dynamic = this.func_235991_a_(p_219117_1_, NBTDynamicOps.INSTANCE);
        INBT inbt = dynamic.getValue();

        if (inbt instanceof CompoundNBT)
        {
            this.field_227173_b_.func_227093_a_(p_219117_1_, (CompoundNBT)inbt);
        }
        else
        {
            LOGGER.error("Expected compound tag, got {}", (Object)inbt);
        }
    }

    private <T> Dynamic<T> func_235991_a_(ChunkPos p_235991_1_, DynamicOps<T> p_235991_2_)
    {
        Map<T, T> map = Maps.newHashMap();

        for (int i = 0; i < 16; ++i)
        {
            long j = SectionPos.from(p_235991_1_, i).asLong();
            this.dirtySections.remove(j);
            Optional<R> optional = this.data.get(j);

            if (optional != null && optional.isPresent())
            {
                DataResult<T> dataresult = this.field_235988_e_.apply(() ->
                {
                    this.markDirty(j);
                }).encodeStart(p_235991_2_, optional.get());
                String s = Integer.toString(i);
                dataresult.resultOrPartial(LOGGER::error).ifPresent((p_235994_3_) ->
                {
                    map.put(p_235991_2_.createString(s), p_235994_3_);
                });
            }
        }

        return new Dynamic<>(p_235991_2_, p_235991_2_.createMap(ImmutableMap.of(p_235991_2_.createString("Sections"), p_235991_2_.createMap(map), p_235991_2_.createString("DataVersion"), p_235991_2_.createInt(SharedConstants.getVersion().getWorldVersion()))));
    }

    protected void onSectionLoad(long p_219111_1_)
    {
    }

    protected void markDirty(long sectionPosIn)
    {
        Optional<R> optional = this.data.get(sectionPosIn);

        if (optional != null && optional.isPresent())
        {
            this.dirtySections.add(sectionPosIn);
        }
        else
        {
            LOGGER.warn("No data for position: {}", (Object)SectionPos.from(sectionPosIn));
        }
    }

    private static int func_235993_a_(Dynamic<?> p_235993_0_)
    {
        return p_235993_0_.get("DataVersion").asInt(1945);
    }

    public void saveIfDirty(ChunkPos p_219112_1_)
    {
        if (!this.dirtySections.isEmpty())
        {
            for (int i = 0; i < 16; ++i)
            {
                long j = SectionPos.from(p_219112_1_, i).asLong();

                if (this.dirtySections.contains(j))
                {
                    this.save(p_219112_1_);
                    return;
                }
            }
        }
    }

    public void close() throws IOException
    {
        this.field_227173_b_.close();
    }
}
