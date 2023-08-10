package net.minecraft.village;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PointOfInterestData
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Short2ObjectMap<PointOfInterest> records = new Short2ObjectOpenHashMap<>();
    private final Map<PointOfInterestType, Set<PointOfInterest>> byType = Maps.newHashMap();
    private final Runnable onChange;
    private boolean valid;

    public static Codec<PointOfInterestData> func_234158_a_(Runnable p_234158_0_)
    {
        return RecordCodecBuilder.create((RecordCodecBuilder.Instance<PointOfInterestData> p_234159_1_) ->
        {
            return p_234159_1_.group(RecordCodecBuilder.point(p_234158_0_), Codec.BOOL.optionalFieldOf("Valid", Boolean.valueOf(false)).forGetter((data) -> {
                return data.valid;
            }), PointOfInterest.func_234150_a_(p_234158_0_).listOf().fieldOf("Records").forGetter((data) -> {
                return ImmutableList.copyOf(data.records.values());
            })).apply(p_234159_1_, PointOfInterestData::new);
        }).orElseGet(Util.func_240982_a_("Failed to read POI section: ", LOGGER::error), () ->
        {
            return new PointOfInterestData(p_234158_0_, false, ImmutableList.of());
        });
    }

    public PointOfInterestData(Runnable onChangeIn)
    {
        this(onChangeIn, true, ImmutableList.of());
    }

    private PointOfInterestData(Runnable onChange, boolean valid, List<PointOfInterest> interestPoints)
    {
        this.onChange = onChange;
        this.valid = valid;
        interestPoints.forEach(this::add);
    }

    public Stream<PointOfInterest> getRecords(Predicate<PointOfInterestType> typePredicate, PointOfInterestManager.Status status)
    {
        return this.byType.entrySet().stream().filter((typeToPointEntry) ->
        {
            return typePredicate.test(typeToPointEntry.getKey());
        }).flatMap((p_234160_0_) ->
        {
            return p_234160_0_.getValue().stream();
        }).filter(status.getTest());
    }

    public void add(BlockPos pos, PointOfInterestType type)
    {
        if (this.add(new PointOfInterest(pos, type, this.onChange)))
        {
            LOGGER.debug("Added POI of type {} @ {}", () ->
            {
                return type;
            }, () ->
            {
                return pos;
            });
            this.onChange.run();
        }
    }

    private boolean add(PointOfInterest p_218254_1_)
    {
        BlockPos blockpos = p_218254_1_.getPos();
        PointOfInterestType pointofinteresttype = p_218254_1_.getType();
        short short1 = SectionPos.toRelativeOffset(blockpos);
        PointOfInterest pointofinterest = this.records.get(short1);

        if (pointofinterest != null)
        {
            if (pointofinteresttype.equals(pointofinterest.getType()))
            {
                return false;
            }

            String s = "POI data mismatch: already registered at " + blockpos;

            if (SharedConstants.developmentMode)
            {
                throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException(s));
            }

            LOGGER.error(s);
        }

        this.records.put(short1, p_218254_1_);
        this.byType.computeIfAbsent(pointofinteresttype, (type) ->
        {
            return Sets.newHashSet();
        }).add(p_218254_1_);
        return true;
    }

    public void remove(BlockPos pos)
    {
        PointOfInterest pointofinterest = this.records.remove(SectionPos.toRelativeOffset(pos));

        if (pointofinterest == null)
        {
            LOGGER.error("POI data mismatch: never registered at " + pos);
        }
        else
        {
            this.byType.get(pointofinterest.getType()).remove(pointofinterest);
            LOGGER.debug("Removed POI of type {} @ {}", pointofinterest::getType, pointofinterest::getPos);
            this.onChange.run();
        }
    }

    public boolean release(BlockPos pos)
    {
        PointOfInterest pointofinterest = this.records.get(SectionPos.toRelativeOffset(pos));

        if (pointofinterest == null)
        {
            throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException("POI never registered at " + pos));
        }
        else
        {
            boolean flag = pointofinterest.release();
            this.onChange.run();
            return flag;
        }
    }

    public boolean exists(BlockPos pos, Predicate<PointOfInterestType> typePredicate)
    {
        short short1 = SectionPos.toRelativeOffset(pos);
        PointOfInterest pointofinterest = this.records.get(short1);
        return pointofinterest != null && typePredicate.test(pointofinterest.getType());
    }

    public Optional<PointOfInterestType> getType(BlockPos pos)
    {
        short short1 = SectionPos.toRelativeOffset(pos);
        PointOfInterest pointofinterest = this.records.get(short1);
        return pointofinterest != null ? Optional.of(pointofinterest.getType()) : Optional.empty();
    }

    public void refresh(Consumer<BiConsumer<BlockPos, PointOfInterestType>> posToTypeConsumer)
    {
        if (!this.valid)
        {
            Short2ObjectMap<PointOfInterest> short2objectmap = new Short2ObjectOpenHashMap<>(this.records);
            this.clear();
            posToTypeConsumer.accept((pos, type) ->
            {
                short short1 = SectionPos.toRelativeOffset(pos);
                PointOfInterest pointofinterest = short2objectmap.computeIfAbsent(short1, (p_234156_3_) -> {
                    return new PointOfInterest(pos, type, this.onChange);
                });
                this.add(pointofinterest);
            });
            this.valid = true;
            this.onChange.run();
        }
    }

    private void clear()
    {
        this.records.clear();
        this.byType.clear();
    }

    boolean isValid()
    {
        return this.valid;
    }
}
