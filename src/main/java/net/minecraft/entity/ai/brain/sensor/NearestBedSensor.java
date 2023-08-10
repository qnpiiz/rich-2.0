package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class NearestBedSensor extends Sensor<MobEntity>
{
    private final Long2LongMap bedPositionToTimeMap = new Long2LongOpenHashMap();
    private int bedsFound;
    private long persistTime;

    public NearestBedSensor()
    {
        super(20);
    }

    public Set < MemoryModuleType<? >> getUsedMemories()
    {
        return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
    }

    protected void update(ServerWorld worldIn, MobEntity entityIn)
    {
        if (entityIn.isChild())
        {
            this.bedsFound = 0;
            this.persistTime = worldIn.getGameTime() + (long)worldIn.getRandom().nextInt(20);
            PointOfInterestManager pointofinterestmanager = worldIn.getPointOfInterestManager();
            Predicate<BlockPos> predicate = (pos) ->
            {
                long i = pos.toLong();

                if (this.bedPositionToTimeMap.containsKey(i))
                {
                    return false;
                }
                else if (++this.bedsFound >= 5)
                {
                    return false;
                }
                else {
                    this.bedPositionToTimeMap.put(i, this.persistTime + 40L);
                    return true;
                }
            };
            Stream<BlockPos> stream = pointofinterestmanager.findAll(PointOfInterestType.HOME.getPredicate(), predicate, entityIn.getPosition(), 48, PointOfInterestManager.Status.ANY);
            Path path = entityIn.getNavigator().pathfind(stream, PointOfInterestType.HOME.getValidRange());

            if (path != null && path.reachesTarget())
            {
                BlockPos blockpos = path.getTarget();
                Optional<PointOfInterestType> optional = pointofinterestmanager.getType(blockpos);

                if (optional.isPresent())
                {
                    entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_BED, blockpos);
                }
            }
            else if (this.bedsFound < 5)
            {
                this.bedPositionToTimeMap.long2LongEntrySet().removeIf((bedLocatedTime) ->
                {
                    return bedLocatedTime.getLongValue() < this.persistTime;
                });
            }
        }
    }
}
