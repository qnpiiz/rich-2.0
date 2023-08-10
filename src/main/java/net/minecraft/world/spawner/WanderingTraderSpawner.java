package net.minecraft.world.spawner;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;

public class WanderingTraderSpawner implements ISpecialSpawner
{
    private final Random random = new Random();
    private final IServerWorldInfo field_234559_b_;
    private int field_221248_c;
    private int field_221249_d;
    private int field_221250_e;

    public WanderingTraderSpawner(IServerWorldInfo p_i231576_1_)
    {
        this.field_234559_b_ = p_i231576_1_;
        this.field_221248_c = 1200;
        this.field_221249_d = p_i231576_1_.getWanderingTraderSpawnDelay();
        this.field_221250_e = p_i231576_1_.getWanderingTraderSpawnChance();

        if (this.field_221249_d == 0 && this.field_221250_e == 0)
        {
            this.field_221249_d = 24000;
            p_i231576_1_.setWanderingTraderSpawnDelay(this.field_221249_d);
            this.field_221250_e = 25;
            p_i231576_1_.setWanderingTraderSpawnChance(this.field_221250_e);
        }
    }

    public int func_230253_a_(ServerWorld p_230253_1_, boolean p_230253_2_, boolean p_230253_3_)
    {
        if (!p_230253_1_.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING))
        {
            return 0;
        }
        else if (--this.field_221248_c > 0)
        {
            return 0;
        }
        else
        {
            this.field_221248_c = 1200;
            this.field_221249_d -= 1200;
            this.field_234559_b_.setWanderingTraderSpawnDelay(this.field_221249_d);

            if (this.field_221249_d > 0)
            {
                return 0;
            }
            else
            {
                this.field_221249_d = 24000;

                if (!p_230253_1_.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING))
                {
                    return 0;
                }
                else
                {
                    int i = this.field_221250_e;
                    this.field_221250_e = MathHelper.clamp(this.field_221250_e + 25, 25, 75);
                    this.field_234559_b_.setWanderingTraderSpawnChance(this.field_221250_e);

                    if (this.random.nextInt(100) > i)
                    {
                        return 0;
                    }
                    else if (this.func_234562_a_(p_230253_1_))
                    {
                        this.field_221250_e = 25;
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        }
    }

    private boolean func_234562_a_(ServerWorld p_234562_1_)
    {
        PlayerEntity playerentity = p_234562_1_.getRandomPlayer();

        if (playerentity == null)
        {
            return true;
        }
        else if (this.random.nextInt(10) != 0)
        {
            return false;
        }
        else
        {
            BlockPos blockpos = playerentity.getPosition();
            int i = 48;
            PointOfInterestManager pointofinterestmanager = p_234562_1_.getPointOfInterestManager();
            Optional<BlockPos> optional = pointofinterestmanager.find(PointOfInterestType.MEETING.getPredicate(), (p_221241_0_) ->
            {
                return true;
            }, blockpos, 48, PointOfInterestManager.Status.ANY);
            BlockPos blockpos1 = optional.orElse(blockpos);
            BlockPos blockpos2 = this.func_234561_a_(p_234562_1_, blockpos1, 48);

            if (blockpos2 != null && this.func_234560_a_(p_234562_1_, blockpos2))
            {
                if (p_234562_1_.func_242406_i(blockpos2).equals(Optional.of(Biomes.THE_VOID)))
                {
                    return false;
                }

                WanderingTraderEntity wanderingtraderentity = EntityType.WANDERING_TRADER.spawn(p_234562_1_, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos2, SpawnReason.EVENT, false, false);

                if (wanderingtraderentity != null)
                {
                    for (int j = 0; j < 2; ++j)
                    {
                        this.func_242373_a(p_234562_1_, wanderingtraderentity, 4);
                    }

                    this.field_234559_b_.setWanderingTraderID(wanderingtraderentity.getUniqueID());
                    wanderingtraderentity.setDespawnDelay(48000);
                    wanderingtraderentity.setWanderTarget(blockpos1);
                    wanderingtraderentity.setHomePosAndDistance(blockpos1, 16);
                    return true;
                }
            }

            return false;
        }
    }

    private void func_242373_a(ServerWorld p_242373_1_, WanderingTraderEntity p_242373_2_, int p_242373_3_)
    {
        BlockPos blockpos = this.func_234561_a_(p_242373_1_, p_242373_2_.getPosition(), p_242373_3_);

        if (blockpos != null)
        {
            TraderLlamaEntity traderllamaentity = EntityType.TRADER_LLAMA.spawn(p_242373_1_, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos, SpawnReason.EVENT, false, false);

            if (traderllamaentity != null)
            {
                traderllamaentity.setLeashHolder(p_242373_2_, true);
            }
        }
    }

    @Nullable
    private BlockPos func_234561_a_(IWorldReader p_234561_1_, BlockPos p_234561_2_, int p_234561_3_)
    {
        BlockPos blockpos = null;

        for (int i = 0; i < 10; ++i)
        {
            int j = p_234561_2_.getX() + this.random.nextInt(p_234561_3_ * 2) - p_234561_3_;
            int k = p_234561_2_.getZ() + this.random.nextInt(p_234561_3_ * 2) - p_234561_3_;
            int l = p_234561_1_.getHeight(Heightmap.Type.WORLD_SURFACE, j, k);
            BlockPos blockpos1 = new BlockPos(j, l, k);

            if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, p_234561_1_, blockpos1, EntityType.WANDERING_TRADER))
            {
                blockpos = blockpos1;
                break;
            }
        }

        return blockpos;
    }

    private boolean func_234560_a_(IBlockReader p_234560_1_, BlockPos p_234560_2_)
    {
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(p_234560_2_, p_234560_2_.add(1, 2, 1)))
        {
            if (!p_234560_1_.getBlockState(blockpos).getCollisionShape(p_234560_1_, blockpos).isEmpty())
            {
                return false;
            }
        }

        return true;
    }
}
