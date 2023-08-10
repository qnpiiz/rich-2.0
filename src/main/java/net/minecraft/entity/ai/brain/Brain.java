package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Brain<E extends LivingEntity>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Supplier<Codec<Brain<E>>> brainCodec;
    private final Map < MemoryModuleType<?>, Optional <? extends Memory<? >>> memories = Maps.newHashMap();
    private final Map < SensorType <? extends Sensor <? super E >> , Sensor <? super E >> sensors = Maps.newLinkedHashMap();
    private final Map < Integer, Map < Activity, Set < Task <? super E >>> > taskPriorityMap = Maps.newTreeMap();
    private Schedule schedule = Schedule.EMPTY;
    private final Map < Activity, Set < Pair < MemoryModuleType<?>, MemoryModuleStatus >>> requiredMemoryStates = Maps.newHashMap();
    private final Map < Activity, Set < MemoryModuleType<? >>> memoryMap = Maps.newHashMap();
    private Set<Activity> defaultActivities = Sets.newHashSet();
    private final Set<Activity> activities = Sets.newHashSet();
    private Activity fallbackActivity = Activity.IDLE;
    private long lastGameTime = -9999L;

    public static <E extends LivingEntity> Brain.BrainCodec<E> createCodec(Collection <? extends MemoryModuleType<? >> memoryTypes, Collection <? extends SensorType <? extends Sensor <? super E >>> sensorTypes)
    {
        return new Brain.BrainCodec<>(memoryTypes, sensorTypes);
    }

    public static <E extends LivingEntity> Codec<Brain<E>> getBrainCodec(final Collection <? extends MemoryModuleType<? >> memoryTypes, final Collection <? extends SensorType <? extends Sensor <? super E >>> sensorTypes)
    {
        final MutableObject<Codec<Brain<E>>> mutableobject = new MutableObject<>();
        mutableobject.setValue((new MapCodec<Brain<E>>()
        {
            public <T> Stream<T> keys(DynamicOps<T> p_keys_1_)
            {
                return memoryTypes.stream().flatMap((memoryType) ->
                {
                    return Util.streamOptional(memoryType.getMemoryCodec().map((memoryCodec) -> {
                        return Registry.MEMORY_MODULE_TYPE.getKey(memoryType);
                    }));
                }).map((memoryTypeKey) ->
                {
                    return p_keys_1_.createString(memoryTypeKey.toString());
                });
            }
            public <T> DataResult<Brain<E>> decode(DynamicOps<T> p_decode_1_, MapLike<T> p_decode_2_)
            {
                MutableObject < DataResult < Builder < Brain.MemoryCodec<? >>> > mutableobject1 = new MutableObject<>(DataResult.success(ImmutableList.builder()));
                p_decode_2_.entries().forEach((inputPair) ->
                {
                    DataResult < MemoryModuleType<? >> dataresult = Registry.MEMORY_MODULE_TYPE.parse(p_decode_1_, inputPair.getFirst());
                    DataResult <? extends Brain.MemoryCodec<? >> dataresult1 = dataresult.flatMap((memoryType) -> {
                        return this.decodeMemory(memoryType, p_decode_1_, (T)inputPair.getSecond());
                    });
                    mutableobject1.setValue(mutableobject1.getValue().apply2(Builder::add, dataresult1));
                });
                ImmutableList < Brain.MemoryCodec<? >> immutablelist = mutableobject1.getValue().resultOrPartial(Brain.LOGGER::error).map(Builder::build).orElseGet(ImmutableList::of);
                return DataResult.success(new Brain<>(memoryTypes, sensorTypes, immutablelist, mutableobject::getValue));
            }
            private <T, U> DataResult<Brain.MemoryCodec<U>> decodeMemory(MemoryModuleType<U> memoryType, DynamicOps<T> ops, T input)
            {
                return memoryType.getMemoryCodec().map(DataResult::success).orElseGet(() ->
                {
                    return DataResult.error("No codec for memory: " + memoryType);
                }).flatMap((memoryCodec) ->
                {
                    return memoryCodec.parse(ops, input);
                }).map((memory) ->
                {
                    return new Brain.MemoryCodec<>(memoryType, Optional.of(memory));
                });
            }
            public <T> RecordBuilder<T> encode(Brain<E> p_encode_1_, DynamicOps<T> p_encode_2_, RecordBuilder<T> p_encode_3_)
            {
                p_encode_1_.createMemoryCodecs().forEach((memoryCodec) ->
                {
                    memoryCodec.encode(p_encode_2_, p_encode_3_);
                });
                return p_encode_3_;
            }
        }).fieldOf("memories").codec());
        return mutableobject.getValue();
    }

    public Brain(Collection <? extends MemoryModuleType<? >> memories, Collection <? extends SensorType <? extends Sensor <? super E >>> sensors, ImmutableList < Brain.MemoryCodec<? >> memoryCodecs, Supplier<Codec<Brain<E>>> brainCodec)
    {
        this.brainCodec = brainCodec;

        for (MemoryModuleType<?> memorymoduletype : memories)
        {
            this.memories.put(memorymoduletype, Optional.empty());
        }

        for (SensorType <? extends Sensor <? super E >> sensortype : sensors)
        {
            this.sensors.put(sensortype, sensortype.getSensor());
        }

        for (Sensor <? super E > sensor : this.sensors.values())
        {
            for (MemoryModuleType<?> memorymoduletype1 : sensor.getUsedMemories())
            {
                this.memories.put(memorymoduletype1, Optional.empty());
            }
        }

        for (Brain.MemoryCodec<?> memorycodec : memoryCodecs)
        {
            memorycodec.refreshMemory(this);
        }
    }

    public <T> DataResult<T> encode(DynamicOps<T> ops)
    {
        return this.brainCodec.get().encodeStart(ops, this);
    }

    private Stream < Brain.MemoryCodec<? >> createMemoryCodecs()
    {
        return this.memories.entrySet().stream().map((entry) ->
        {
            return Brain.MemoryCodec.createCodec(entry.getKey(), entry.getValue());
        });
    }

    public boolean hasMemory(MemoryModuleType<?> typeIn)
    {
        return this.hasMemory(typeIn, MemoryModuleStatus.VALUE_PRESENT);
    }

    public <U> void removeMemory(MemoryModuleType<U> type)
    {
        this.setMemory(type, Optional.empty());
    }

    public <U> void setMemory(MemoryModuleType<U> memoryType, @Nullable U memory)
    {
        this.setMemory(memoryType, Optional.ofNullable(memory));
    }

    public <U> void replaceMemory(MemoryModuleType<U> memoryType, U memory, long timesToLive)
    {
        this.replaceMemory(memoryType, Optional.of(Memory.create(memory, timesToLive)));
    }

    public <U> void setMemory(MemoryModuleType<U> memoryType, Optional <? extends U > memory)
    {
        this.replaceMemory(memoryType, memory.map(Memory::create));
    }

    private <U> void replaceMemory(MemoryModuleType<U> memoryType, Optional <? extends Memory<? >> memory)
    {
        if (this.memories.containsKey(memoryType))
        {
            if (memory.isPresent() && this.isEmptyCollection(memory.get().getValue()))
            {
                this.removeMemory(memoryType);
            }
            else
            {
                this.memories.put(memoryType, memory);
            }
        }
    }

    public <U> Optional<U> getMemory(MemoryModuleType<U> type)
    {
        return (Optional<U>) this.memories.get(type).map(Memory::getValue);
    }

    public <U> boolean hasMemory(MemoryModuleType<U> memoryType, U memory)
    {
        return !this.hasMemory(memoryType) ? false : this.getMemory(memoryType).filter((memoryIn) ->
        {
            return memoryIn.equals(memory);
        }).isPresent();
    }

    public boolean hasMemory(MemoryModuleType<?> memoryTypeIn, MemoryModuleStatus memoryStatusIn)
    {
        Optional <? extends Memory<? >> optional = this.memories.get(memoryTypeIn);

        if (optional == null)
        {
            return false;
        }
        else
        {
            return memoryStatusIn == MemoryModuleStatus.REGISTERED || memoryStatusIn == MemoryModuleStatus.VALUE_PRESENT && optional.isPresent() || memoryStatusIn == MemoryModuleStatus.VALUE_ABSENT && !optional.isPresent();
        }
    }

    public Schedule getSchedule()
    {
        return this.schedule;
    }

    public void setSchedule(Schedule newSchedule)
    {
        this.schedule = newSchedule;
    }

    public void setDefaultActivities(Set<Activity> newActivities)
    {
        this.defaultActivities = newActivities;
    }

    @Deprecated
    public List < Task <? super E >> getRunningTasks()
    {
        List < Task <? super E >> list = new ObjectArrayList<>();

        for (Map < Activity, Set < Task <? super E >>> map : this.taskPriorityMap.values())
        {
            for (Set < Task <? super E >> set : map.values())
            {
                for (Task <? super E > task : set)
                {
                    if (task.getStatus() == Task.Status.RUNNING)
                    {
                        list.add(task);
                    }
                }
            }
        }

        return list;
    }

    public void switchToFallbackActivity()
    {
        this.switchActivity(this.fallbackActivity);
    }

    public Optional<Activity> getTemporaryActivity()
    {
        for (Activity activity : this.activities)
        {
            if (!this.defaultActivities.contains(activity))
            {
                return Optional.of(activity);
            }
        }

        return Optional.empty();
    }

    public void switchTo(Activity activityIn)
    {
        if (this.hasRequiredMemories(activityIn))
        {
            this.switchActivity(activityIn);
        }
        else
        {
            this.switchToFallbackActivity();
        }
    }

    private void switchActivity(Activity activity)
    {
        if (!this.hasActivity(activity))
        {
            this.removeUnassociatedMemories(activity);
            this.activities.clear();
            this.activities.addAll(this.defaultActivities);
            this.activities.add(activity);
        }
    }

    private void removeUnassociatedMemories(Activity activityIn)
    {
        for (Activity activity : this.activities)
        {
            if (activity != activityIn)
            {
                Set < MemoryModuleType<? >> set = this.memoryMap.get(activity);

                if (set != null)
                {
                    for (MemoryModuleType<?> memorymoduletype : set)
                    {
                        this.removeMemory(memorymoduletype);
                    }
                }
            }
        }
    }

    public void updateActivity(long dayTime, long gameTime)
    {
        if (gameTime - this.lastGameTime > 20L)
        {
            this.lastGameTime = gameTime;
            Activity activity = this.getSchedule().getScheduledActivity((int)(dayTime % 24000L));

            if (!this.activities.contains(activity))
            {
                this.switchTo(activity);
            }
        }
    }

    public void switchActivities(List<Activity> activities)
    {
        for (Activity activity : activities)
        {
            if (this.hasRequiredMemories(activity))
            {
                this.switchActivity(activity);
                break;
            }
        }
    }

    public void setFallbackActivity(Activity newFallbackActivity)
    {
        this.fallbackActivity = newFallbackActivity;
    }

    public void registerActivity(Activity activity, int priorityStart, ImmutableList <? extends Task <? super E >> tasks)
    {
        this.registerActivity(activity, this.getTaskPriorityList(priorityStart, tasks));
    }

    public void registerActivity(Activity activity, int priorityStart, ImmutableList <? extends Task <? super E >> tasks, MemoryModuleType<?> memoryType)
    {
        Set < Pair < MemoryModuleType<?>, MemoryModuleStatus >> set = ImmutableSet.of(Pair.of(memoryType, MemoryModuleStatus.VALUE_PRESENT));
        Set < MemoryModuleType<? >> set1 = ImmutableSet.of(memoryType);
        this.registerActivity(activity, this.getTaskPriorityList(priorityStart, tasks), set, set1);
    }

    public void registerActivity(Activity activityIn, ImmutableList <? extends Pair < Integer, ? extends Task <? super E >>> tasks)
    {
        this.registerActivity(activityIn, tasks, ImmutableSet.of(), Sets.newHashSet());
    }

    public void registerActivity(Activity activity, ImmutableList <? extends Pair < Integer, ? extends Task <? super E >>> tasks, Set < Pair < MemoryModuleType<?>, MemoryModuleStatus >> memoryStatuses)
    {
        this.registerActivity(activity, tasks, memoryStatuses, Sets.newHashSet());
    }

    private void registerActivity(Activity activity, ImmutableList <? extends Pair < Integer, ? extends Task <? super E >>> tasks, Set < Pair < MemoryModuleType<?>, MemoryModuleStatus >> memorieStatuses, Set < MemoryModuleType<? >> memoryTypes)
    {
        this.requiredMemoryStates.put(activity, memorieStatuses);

        if (!memoryTypes.isEmpty())
        {
            this.memoryMap.put(activity, memoryTypes);
        }

        for (Pair < Integer, ? extends Task <? super E >> pair : tasks)
        {
            this.taskPriorityMap.computeIfAbsent(pair.getFirst(), (activityPriority) ->
            {
                return Maps.newHashMap();
            }).computeIfAbsent(activity, (activityIn) ->
            {
                return Sets.newLinkedHashSet();
            }).add(pair.getSecond());
        }
    }

    public boolean hasActivity(Activity activityIn)
    {
        return this.activities.contains(activityIn);
    }

    public Brain<E> copy()
    {
        Brain<E> brain = new Brain<>(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.brainCodec);

        for (Entry < MemoryModuleType<?>, Optional <? extends Memory<? >>> entry : this.memories.entrySet())
        {
            MemoryModuleType<?> memorymoduletype = entry.getKey();

            if (entry.getValue().isPresent())
            {
                brain.memories.put(memorymoduletype, entry.getValue());
            }
        }

        return brain;
    }

    public void tick(ServerWorld worldIn, E entityIn)
    {
        this.tickMemories();
        this.tickSensors(worldIn, entityIn);
        this.startTasks(worldIn, entityIn);
        this.tickTasks(worldIn, entityIn);
    }

    private void tickSensors(ServerWorld world, E brainHolder)
    {
        for (Sensor <? super E > sensor : this.sensors.values())
        {
            sensor.tick(world, brainHolder);
        }
    }

    private void tickMemories()
    {
        for (Entry < MemoryModuleType<?>, Optional <? extends Memory<? >>> entry : this.memories.entrySet())
        {
            if (entry.getValue().isPresent())
            {
                Memory<?> memory = entry.getValue().get();
                memory.tick();

                if (memory.isForgotten())
                {
                    this.removeMemory(entry.getKey());
                }
            }
        }
    }

    public void stopAllTasks(ServerWorld worldIn, E owner)
    {
        long i = owner.world.getGameTime();

        for (Task <? super E > task : this.getRunningTasks())
        {
            task.stop(worldIn, owner, i);
        }
    }

    private void startTasks(ServerWorld worldIn, E entityIn)
    {
        long i = worldIn.getGameTime();

        for (Map < Activity, Set < Task <? super E >>> map : this.taskPriorityMap.values())
        {
            for (Entry < Activity, Set < Task <? super E >>> entry : map.entrySet())
            {
                Activity activity = entry.getKey();

                if (this.activities.contains(activity))
                {
                    for (Task <? super E > task : entry.getValue())
                    {
                        if (task.getStatus() == Task.Status.STOPPED)
                        {
                            task.start(worldIn, entityIn, i);
                        }
                    }
                }
            }
        }
    }

    private void tickTasks(ServerWorld worldIn, E entityIn)
    {
        long i = worldIn.getGameTime();

        for (Task <? super E > task : this.getRunningTasks())
        {
            task.tick(worldIn, entityIn, i);
        }
    }

    private boolean hasRequiredMemories(Activity activityIn)
    {
        if (!this.requiredMemoryStates.containsKey(activityIn))
        {
            return false;
        }
        else
        {
            for (Pair < MemoryModuleType<?>, MemoryModuleStatus > pair : this.requiredMemoryStates.get(activityIn))
            {
                MemoryModuleType<?> memorymoduletype = pair.getFirst();
                MemoryModuleStatus memorymodulestatus = pair.getSecond();

                if (!this.hasMemory(memorymoduletype, memorymodulestatus))
                {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean isEmptyCollection(Object collection)
    {
        return collection instanceof Collection && ((Collection)collection).isEmpty();
    }

    ImmutableList <? extends Pair < Integer, ? extends Task <? super E >>> getTaskPriorityList(int priorityStart, ImmutableList <? extends Task <? super E >> tasks)
    {
        int i = priorityStart;
        Builder < Pair < Integer, ? extends Task <? super E >>> builder = ImmutableList.builder();

        for (Task <? super E > task : tasks)
        {
            builder.add(Pair.of(i++, task));
        }

        return builder.build();
    }

    public static final class BrainCodec<E extends LivingEntity>
    {
        private final Collection <? extends MemoryModuleType<? >> memoryTypes;
        private final Collection <? extends SensorType <? extends Sensor <? super E >>> sensorTypes;
        private final Codec<Brain<E>> brainCodec;

        private BrainCodec(Collection <? extends MemoryModuleType<? >> memoryTypes, Collection <? extends SensorType <? extends Sensor <? super E >>> sensorTypes)
        {
            this.memoryTypes = memoryTypes;
            this.sensorTypes = sensorTypes;
            this.brainCodec = Brain.getBrainCodec(memoryTypes, sensorTypes);
        }

        public Brain<E> deserialize(Dynamic<?> ops)
        {
            return this.brainCodec.parse(ops).resultOrPartial(Brain.LOGGER::error).orElseGet(() ->
            {
                return new Brain<>(this.memoryTypes, this.sensorTypes, ImmutableList.of(), () -> {
                    return this.brainCodec;
                });
            });
        }
    }

    static final class MemoryCodec<U>
    {
        private final MemoryModuleType<U> memoryType;
        private final Optional <? extends Memory<U >> memory;

        private static <U> Brain.MemoryCodec<U> createCodec(MemoryModuleType<U> memoryType, Optional <? extends Memory<? >> memory)
        {
            return new Brain.MemoryCodec<>(memoryType, (Optional <? extends Memory<U >>) memory);
        }

        private MemoryCodec(MemoryModuleType<U> memoryType, Optional <? extends Memory<U >> memory)
        {
            this.memoryType = memoryType;
            this.memory = memory;
        }

        private void refreshMemory(Brain<?> brain)
        {
            brain.replaceMemory(this.memoryType, this.memory);
        }

        public <T> void encode(DynamicOps<T> ops, RecordBuilder<T> builder)
        {
            this.memoryType.getMemoryCodec().ifPresent((memoryCodec) ->
            {
                this.memory.ifPresent((memory) -> {
                    builder.add(Registry.MEMORY_MODULE_TYPE.encodeStart(ops, this.memoryType), memoryCodec.encodeStart(ops, memory));
                });
            });
        }
    }
}
