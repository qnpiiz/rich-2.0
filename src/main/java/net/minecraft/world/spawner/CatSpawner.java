package net.minecraft.world.spawner;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public class CatSpawner implements ISpecialSpawner
{
    private int field_221125_a;

    public int func_230253_a_(ServerWorld p_230253_1_, boolean p_230253_2_, boolean p_230253_3_)
    {
        if (p_230253_3_ && p_230253_1_.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING))
        {
            --this.field_221125_a;

            if (this.field_221125_a > 0)
            {
                return 0;
            }
            else
            {
                this.field_221125_a = 1200;
                PlayerEntity playerentity = p_230253_1_.getRandomPlayer();

                if (playerentity == null)
                {
                    return 0;
                }
                else
                {
                    Random random = p_230253_1_.rand;
                    int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                    int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                    BlockPos blockpos = playerentity.getPosition().add(i, 0, j);

                    if (!p_230253_1_.isAreaLoaded(blockpos.getX() - 10, blockpos.getY() - 10, blockpos.getZ() - 10, blockpos.getX() + 10, blockpos.getY() + 10, blockpos.getZ() + 10))
                    {
                        return 0;
                    }
                    else
                    {
                        if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, p_230253_1_, blockpos, EntityType.CAT))
                        {
                            if (p_230253_1_.func_241119_a_(blockpos, 2))
                            {
                                return this.func_221121_a(p_230253_1_, blockpos);
                            }

                            if (p_230253_1_.func_241112_a_().func_235010_a_(blockpos, true, Structure.field_236374_j_).isValid())
                            {
                                return this.func_221123_a(p_230253_1_, blockpos);
                            }
                        }

                        return 0;
                    }
                }
            }
        }
        else
        {
            return 0;
        }
    }

    private int func_221121_a(ServerWorld worldIn, BlockPos p_221121_2_)
    {
        int i = 48;

        if (worldIn.getPointOfInterestManager().getCountInRange(PointOfInterestType.HOME.getPredicate(), p_221121_2_, 48, PointOfInterestManager.Status.IS_OCCUPIED) > 4L)
        {
            List<CatEntity> list = worldIn.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(p_221121_2_)).grow(48.0D, 8.0D, 48.0D));

            if (list.size() < 5)
            {
                return this.spawnCat(p_221121_2_, worldIn);
            }
        }

        return 0;
    }

    private int func_221123_a(ServerWorld worldIn, BlockPos pos)
    {
        int i = 16;
        List<CatEntity> list = worldIn.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(pos)).grow(16.0D, 8.0D, 16.0D));
        return list.size() < 1 ? this.spawnCat(pos, worldIn) : 0;
    }

    private int spawnCat(BlockPos pos, ServerWorld worldIn)
    {
        CatEntity catentity = EntityType.CAT.create(worldIn);

        if (catentity == null)
        {
            return 0;
        }
        else
        {
            catentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(pos), SpawnReason.NATURAL, (ILivingEntityData)null, (CompoundNBT)null);
            catentity.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
            worldIn.func_242417_l(catentity);
            return 1;
        }
    }
}
