package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillageSiege implements ISpecialSpawner
{
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean hasSetupSiege;
    private VillageSiege.State siegeState = VillageSiege.State.SIEGE_DONE;
    private int siegeCount;
    private int nextSpawnTime;
    private int spawnX;
    private int spawnY;
    private int spawnZ;

    public int func_230253_a_(ServerWorld p_230253_1_, boolean p_230253_2_, boolean p_230253_3_)
    {
        if (!p_230253_1_.isDaytime() && p_230253_2_)
        {
            float f = p_230253_1_.func_242415_f(0.0F);

            if ((double)f == 0.5D)
            {
                this.siegeState = p_230253_1_.rand.nextInt(10) == 0 ? VillageSiege.State.SIEGE_TONIGHT : VillageSiege.State.SIEGE_DONE;
            }

            if (this.siegeState == VillageSiege.State.SIEGE_DONE)
            {
                return 0;
            }
            else
            {
                if (!this.hasSetupSiege)
                {
                    if (!this.trySetupSiege(p_230253_1_))
                    {
                        return 0;
                    }

                    this.hasSetupSiege = true;
                }

                if (this.nextSpawnTime > 0)
                {
                    --this.nextSpawnTime;
                    return 0;
                }
                else
                {
                    this.nextSpawnTime = 2;

                    if (this.siegeCount > 0)
                    {
                        this.spawnZombie(p_230253_1_);
                        --this.siegeCount;
                    }
                    else
                    {
                        this.siegeState = VillageSiege.State.SIEGE_DONE;
                    }

                    return 1;
                }
            }
        }
        else
        {
            this.siegeState = VillageSiege.State.SIEGE_DONE;
            this.hasSetupSiege = false;
            return 0;
        }
    }

    private boolean trySetupSiege(ServerWorld world)
    {
        for (PlayerEntity playerentity : world.getPlayers())
        {
            if (!playerentity.isSpectator())
            {
                BlockPos blockpos = playerentity.getPosition();

                if (world.isVillage(blockpos) && world.getBiome(blockpos).getCategory() != Biome.Category.MUSHROOM)
                {
                    for (int i = 0; i < 10; ++i)
                    {
                        float f = world.rand.nextFloat() * ((float)Math.PI * 2F);
                        this.spawnX = blockpos.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F);
                        this.spawnY = blockpos.getY();
                        this.spawnZ = blockpos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F);

                        if (this.findRandomSpawnPos(world, new BlockPos(this.spawnX, this.spawnY, this.spawnZ)) != null)
                        {
                            this.nextSpawnTime = 0;
                            this.siegeCount = 20;
                            break;
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private void spawnZombie(ServerWorld world)
    {
        Vector3d vector3d = this.findRandomSpawnPos(world, new BlockPos(this.spawnX, this.spawnY, this.spawnZ));

        if (vector3d != null)
        {
            ZombieEntity zombieentity;

            try
            {
                zombieentity = new ZombieEntity(world);
                zombieentity.onInitialSpawn(world, world.getDifficultyForLocation(zombieentity.getPosition()), SpawnReason.EVENT, (ILivingEntityData)null, (CompoundNBT)null);
            }
            catch (Exception exception)
            {
                LOGGER.warn("Failed to create zombie for village siege at {}", vector3d, exception);
                return;
            }

            zombieentity.setLocationAndAngles(vector3d.x, vector3d.y, vector3d.z, world.rand.nextFloat() * 360.0F, 0.0F);
            world.func_242417_l(zombieentity);
        }
    }

    @Nullable
    private Vector3d findRandomSpawnPos(ServerWorld world, BlockPos pos)
    {
        for (int i = 0; i < 10; ++i)
        {
            int j = pos.getX() + world.rand.nextInt(16) - 8;
            int k = pos.getZ() + world.rand.nextInt(16) - 8;
            int l = world.getHeight(Heightmap.Type.WORLD_SURFACE, j, k);
            BlockPos blockpos = new BlockPos(j, l, k);

            if (world.isVillage(blockpos) && MonsterEntity.canMonsterSpawnInLight(EntityType.ZOMBIE, world, SpawnReason.EVENT, blockpos, world.rand))
            {
                return Vector3d.copyCenteredHorizontally(blockpos);
            }
        }

        return null;
    }

    static enum State
    {
        SIEGE_CAN_ACTIVATE,
        SIEGE_TONIGHT,
        SIEGE_DONE;
    }
}
