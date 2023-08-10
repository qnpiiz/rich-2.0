package net.minecraft.world.spawner;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeMagnifier;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldEntitySpawner
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_234960_b_ = (int)Math.pow(17.0D, 2.0D);
    private static final EntityClassification[] field_234961_c_ = Stream.of(EntityClassification.values()).filter((p_234965_0_) ->
    {
        return p_234965_0_ != EntityClassification.MISC;
    }).toArray((p_234963_0_) ->
    {
        return new EntityClassification[p_234963_0_];
    });

    public static WorldEntitySpawner.EntityDensityManager func_234964_a_(int p_234964_0_, Iterable<Entity> p_234964_1_, WorldEntitySpawner.IInitialDensityAdder p_234964_2_)
    {
        MobDensityTracker mobdensitytracker = new MobDensityTracker();
        Object2IntOpenHashMap<EntityClassification> object2intopenhashmap = new Object2IntOpenHashMap<>();
        Iterator iterator = p_234964_1_.iterator();

        while (true)
        {
            Entity entity;
            MobEntity mobentity;

            do
            {
                if (!iterator.hasNext())
                {
                    return new WorldEntitySpawner.EntityDensityManager(p_234964_0_, object2intopenhashmap, mobdensitytracker);
                }

                entity = (Entity)iterator.next();

                if (!(entity instanceof MobEntity))
                {
                    break;
                }

                mobentity = (MobEntity)entity;
            }
            while (mobentity.isNoDespawnRequired() || mobentity.preventDespawn());

            final Entity entity_f = entity;
            EntityClassification entityclassification = entity.getType().getClassification();

            if (entityclassification != EntityClassification.MISC)
            {
                BlockPos blockpos = entity.getPosition();
                long i = ChunkPos.asLong(blockpos.getX() >> 4, blockpos.getZ() >> 4);
                p_234964_2_.query(i, (p_234971_5_) ->
                {
                    MobSpawnInfo.SpawnCosts mobspawninfo$spawncosts = func_234980_b_(blockpos, p_234971_5_).getMobSpawnInfo().getSpawnCost(entity_f.getType());

                    if (mobspawninfo$spawncosts != null)
                    {
                        mobdensitytracker.func_234998_a_(entity_f.getPosition(), mobspawninfo$spawncosts.getEntitySpawnCost());
                    }

                    object2intopenhashmap.addTo(entityclassification, 1);
                });
            }
        }
    }

    private static Biome func_234980_b_(BlockPos p_234980_0_, IChunk p_234980_1_)
    {
        return DefaultBiomeMagnifier.INSTANCE.getBiome(0L, p_234980_0_.getX(), p_234980_0_.getY(), p_234980_0_.getZ(), p_234980_1_.getBiomes());
    }

    public static void func_234979_a_(ServerWorld p_234979_0_, Chunk p_234979_1_, WorldEntitySpawner.EntityDensityManager p_234979_2_, boolean p_234979_3_, boolean p_234979_4_, boolean p_234979_5_)
    {
        p_234979_0_.getProfiler().startSection("spawner");

        for (EntityClassification entityclassification : field_234961_c_)
        {
            if ((p_234979_3_ || !entityclassification.getPeacefulCreature()) && (p_234979_4_ || entityclassification.getPeacefulCreature()) && (p_234979_5_ || !entityclassification.getAnimal()) && p_234979_2_.func_234991_a_(entityclassification))
            {
                func_234967_a_(entityclassification, p_234979_0_, p_234979_1_, (p_234969_1_, p_234969_2_, p_234969_3_) ->
                {
                    return p_234979_2_.func_234989_a_(p_234969_1_, p_234969_2_, p_234969_3_);
                }, (p_234970_1_, p_234970_2_) ->
                {
                    p_234979_2_.func_234990_a_(p_234970_1_, p_234970_2_);
                });
            }
        }

        p_234979_0_.getProfiler().endSection();
    }

    public static void func_234967_a_(EntityClassification p_234967_0_, ServerWorld p_234967_1_, Chunk p_234967_2_, WorldEntitySpawner.IDensityCheck p_234967_3_, WorldEntitySpawner.IOnSpawnDensityAdder p_234967_4_)
    {
        BlockPos blockpos = getRandomHeight(p_234967_1_, p_234967_2_);

        if (blockpos.getY() >= 1)
        {
            func_234966_a_(p_234967_0_, p_234967_1_, p_234967_2_, blockpos, p_234967_3_, p_234967_4_);
        }
    }

    public static void func_234966_a_(EntityClassification p_234966_0_, ServerWorld p_234966_1_, IChunk p_234966_2_, BlockPos p_234966_3_, WorldEntitySpawner.IDensityCheck p_234966_4_, WorldEntitySpawner.IOnSpawnDensityAdder p_234966_5_)
    {
        StructureManager structuremanager = p_234966_1_.func_241112_a_();
        ChunkGenerator chunkgenerator = p_234966_1_.getChunkProvider().getChunkGenerator();
        int i = p_234966_3_.getY();
        BlockState blockstate = p_234966_2_.getBlockState(p_234966_3_);

        if (!blockstate.isNormalCube(p_234966_2_, p_234966_3_))
        {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            int j = 0;

            for (int k = 0; k < 3; ++k)
            {
                int l = p_234966_3_.getX();
                int i1 = p_234966_3_.getZ();
                int j1 = 6;
                MobSpawnInfo.Spawners mobspawninfo$spawners = null;
                ILivingEntityData ilivingentitydata = null;
                int k1 = MathHelper.ceil(p_234966_1_.rand.nextFloat() * 4.0F);
                int l1 = 0;

                for (int i2 = 0; i2 < k1; ++i2)
                {
                    l += p_234966_1_.rand.nextInt(6) - p_234966_1_.rand.nextInt(6);
                    i1 += p_234966_1_.rand.nextInt(6) - p_234966_1_.rand.nextInt(6);
                    blockpos$mutable.setPos(l, i, i1);
                    double d0 = (double)l + 0.5D;
                    double d1 = (double)i1 + 0.5D;
                    PlayerEntity playerentity = p_234966_1_.getClosestPlayer(d0, (double)i, d1, -1.0D, false);

                    if (playerentity != null)
                    {
                        double d2 = playerentity.getDistanceSq(d0, (double)i, d1);

                        if (func_234978_a_(p_234966_1_, p_234966_2_, blockpos$mutable, d2))
                        {
                            if (mobspawninfo$spawners == null)
                            {
                                mobspawninfo$spawners = func_234977_a_(p_234966_1_, structuremanager, chunkgenerator, p_234966_0_, p_234966_1_.rand, blockpos$mutable);

                                if (mobspawninfo$spawners == null)
                                {
                                    break;
                                }

                                k1 = mobspawninfo$spawners.minCount + p_234966_1_.rand.nextInt(1 + mobspawninfo$spawners.maxCount - mobspawninfo$spawners.minCount);
                            }

                            if (func_234975_a_(p_234966_1_, p_234966_0_, structuremanager, chunkgenerator, mobspawninfo$spawners, blockpos$mutable, d2) && p_234966_4_.test(mobspawninfo$spawners.type, blockpos$mutable, p_234966_2_))
                            {
                                MobEntity mobentity = func_234973_a_(p_234966_1_, mobspawninfo$spawners.type);

                                if (mobentity == null)
                                {
                                    return;
                                }

                                mobentity.setLocationAndAngles(d0, (double)i, d1, p_234966_1_.rand.nextFloat() * 360.0F, 0.0F);

                                if (func_234974_a_(p_234966_1_, mobentity, d2))
                                {
                                    ilivingentitydata = mobentity.onInitialSpawn(p_234966_1_, p_234966_1_.getDifficultyForLocation(mobentity.getPosition()), SpawnReason.NATURAL, ilivingentitydata, (CompoundNBT)null);
                                    ++j;
                                    ++l1;
                                    p_234966_1_.func_242417_l(mobentity);
                                    p_234966_5_.run(mobentity, p_234966_2_);

                                    if (j >= mobentity.getMaxSpawnedInChunk())
                                    {
                                        return;
                                    }

                                    if (mobentity.isMaxGroupSize(l1))
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean func_234978_a_(ServerWorld p_234978_0_, IChunk p_234978_1_, BlockPos.Mutable p_234978_2_, double p_234978_3_)
    {
        if (p_234978_3_ <= 576.0D)
        {
            return false;
        }
        else if (p_234978_0_.getSpawnPoint().withinDistance(new Vector3d((double)p_234978_2_.getX() + 0.5D, (double)p_234978_2_.getY(), (double)p_234978_2_.getZ() + 0.5D), 24.0D))
        {
            return false;
        }
        else
        {
            ChunkPos chunkpos = new ChunkPos(p_234978_2_);
            return Objects.equals(chunkpos, p_234978_1_.getPos()) || p_234978_0_.getChunkProvider().isChunkLoaded(chunkpos);
        }
    }

    private static boolean func_234975_a_(ServerWorld p_234975_0_, EntityClassification p_234975_1_, StructureManager p_234975_2_, ChunkGenerator p_234975_3_, MobSpawnInfo.Spawners p_234975_4_, BlockPos.Mutable p_234975_5_, double p_234975_6_)
    {
        EntityType<?> entitytype = p_234975_4_.type;

        if (entitytype.getClassification() == EntityClassification.MISC)
        {
            return false;
        }
        else if (!entitytype.func_225437_d() && p_234975_6_ > (double)(entitytype.getClassification().getInstantDespawnDistance() * entitytype.getClassification().getInstantDespawnDistance()))
        {
            return false;
        }
        else if (entitytype.isSummonable() && func_234976_a_(p_234975_0_, p_234975_2_, p_234975_3_, p_234975_1_, p_234975_4_, p_234975_5_))
        {
            EntitySpawnPlacementRegistry.PlacementType entityspawnplacementregistry$placementtype = EntitySpawnPlacementRegistry.getPlacementType(entitytype);

            if (!canCreatureTypeSpawnAtLocation(entityspawnplacementregistry$placementtype, p_234975_0_, p_234975_5_, entitytype))
            {
                return false;
            }
            else if (!EntitySpawnPlacementRegistry.canSpawnEntity(entitytype, p_234975_0_, SpawnReason.NATURAL, p_234975_5_, p_234975_0_.rand))
            {
                return false;
            }
            else
            {
                return p_234975_0_.hasNoCollisions(entitytype.getBoundingBoxWithSizeApplied((double)p_234975_5_.getX() + 0.5D, (double)p_234975_5_.getY(), (double)p_234975_5_.getZ() + 0.5D));
            }
        }
        else
        {
            return false;
        }
    }

    @Nullable
    private static MobEntity func_234973_a_(ServerWorld p_234973_0_, EntityType<?> p_234973_1_)
    {
        try
        {
            Entity entity = p_234973_1_.create(p_234973_0_);

            if (!(entity instanceof MobEntity))
            {
                throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(p_234973_1_));
            }
            else
            {
                return (MobEntity)entity;
            }
        }
        catch (Exception exception)
        {
            LOGGER.warn("Failed to create mob", (Throwable)exception);
            return null;
        }
    }

    private static boolean func_234974_a_(ServerWorld p_234974_0_, MobEntity p_234974_1_, double p_234974_2_)
    {
        if (p_234974_2_ > (double)(p_234974_1_.getType().getClassification().getInstantDespawnDistance() * p_234974_1_.getType().getClassification().getInstantDespawnDistance()) && p_234974_1_.canDespawn(p_234974_2_))
        {
            return false;
        }
        else
        {
            return p_234974_1_.canSpawn(p_234974_0_, SpawnReason.NATURAL) && p_234974_1_.isNotColliding(p_234974_0_);
        }
    }

    @Nullable
    private static MobSpawnInfo.Spawners func_234977_a_(ServerWorld p_234977_0_, StructureManager p_234977_1_, ChunkGenerator p_234977_2_, EntityClassification p_234977_3_, Random p_234977_4_, BlockPos p_234977_5_)
    {
        Biome biome = p_234977_0_.getBiome(p_234977_5_);

        if (p_234977_3_ == EntityClassification.WATER_AMBIENT && biome.getCategory() == Biome.Category.RIVER && p_234977_4_.nextFloat() < 0.98F)
        {
            return null;
        }
        else
        {
            List<MobSpawnInfo.Spawners> list = func_241463_a_(p_234977_0_, p_234977_1_, p_234977_2_, p_234977_3_, p_234977_5_, biome);
            return list.isEmpty() ? null : WeightedRandom.getRandomItem(p_234977_4_, list);
        }
    }

    private static boolean func_234976_a_(ServerWorld p_234976_0_, StructureManager p_234976_1_, ChunkGenerator p_234976_2_, EntityClassification p_234976_3_, MobSpawnInfo.Spawners p_234976_4_, BlockPos p_234976_5_)
    {
        return func_241463_a_(p_234976_0_, p_234976_1_, p_234976_2_, p_234976_3_, p_234976_5_, (Biome)null).contains(p_234976_4_);
    }

    private static List<MobSpawnInfo.Spawners> func_241463_a_(ServerWorld p_241463_0_, StructureManager p_241463_1_, ChunkGenerator p_241463_2_, EntityClassification p_241463_3_, BlockPos p_241463_4_, @Nullable Biome p_241463_5_)
    {
        return p_241463_3_ == EntityClassification.MONSTER && p_241463_0_.getBlockState(p_241463_4_.down()).getBlock() == Blocks.NETHER_BRICKS && p_241463_1_.func_235010_a_(p_241463_4_, false, Structure.field_236378_n_).isValid() ? Structure.field_236378_n_.getSpawnList() : p_241463_2_.func_230353_a_(p_241463_5_ != null ? p_241463_5_ : p_241463_0_.getBiome(p_241463_4_), p_241463_1_, p_241463_3_, p_241463_4_);
    }

    private static BlockPos getRandomHeight(World worldIn, Chunk p_222262_1_)
    {
        ChunkPos chunkpos = p_222262_1_.getPos();
        int i = chunkpos.getXStart() + worldIn.rand.nextInt(16);
        int j = chunkpos.getZStart() + worldIn.rand.nextInt(16);
        int k = p_222262_1_.getTopBlockY(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = worldIn.rand.nextInt(k + 1);
        return new BlockPos(i, l, j);
    }

    public static boolean func_234968_a_(IBlockReader p_234968_0_, BlockPos p_234968_1_, BlockState p_234968_2_, FluidState p_234968_3_, EntityType<?> p_234968_4_)
    {
        if (p_234968_2_.hasOpaqueCollisionShape(p_234968_0_, p_234968_1_))
        {
            return false;
        }
        else if (p_234968_2_.canProvidePower())
        {
            return false;
        }
        else if (!p_234968_3_.isEmpty())
        {
            return false;
        }
        else if (p_234968_2_.isIn(BlockTags.PREVENT_MOB_SPAWNING_INSIDE))
        {
            return false;
        }
        else
        {
            return !p_234968_4_.func_233597_a_(p_234968_2_);
        }
    }

    public static boolean canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType placeType, IWorldReader worldIn, BlockPos pos, @Nullable EntityType<?> entityTypeIn)
    {
        if (placeType == EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS)
        {
            return true;
        }
        else if (entityTypeIn != null && worldIn.getWorldBorder().contains(pos))
        {
            BlockState blockstate = worldIn.getBlockState(pos);
            FluidState fluidstate = worldIn.getFluidState(pos);
            BlockPos blockpos = pos.up();
            BlockPos blockpos1 = pos.down();

            switch (placeType)
            {
                case IN_WATER:
                    return fluidstate.isTagged(FluidTags.WATER) && worldIn.getFluidState(blockpos1).isTagged(FluidTags.WATER) && !worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos);

                case IN_LAVA:
                    return fluidstate.isTagged(FluidTags.LAVA);

                case ON_GROUND:
                default:
                    BlockState blockstate1 = worldIn.getBlockState(blockpos1);

                    if (!blockstate1.canEntitySpawn(worldIn, blockpos1, entityTypeIn))
                    {
                        return false;
                    }
                    else
                    {
                        return func_234968_a_(worldIn, pos, blockstate, fluidstate, entityTypeIn) && func_234968_a_(worldIn, blockpos, worldIn.getBlockState(blockpos), worldIn.getFluidState(blockpos), entityTypeIn);
                    }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Called during chunk generation to spawn initial creatures.
     */
    public static void performWorldGenSpawning(IServerWorld worldIn, Biome biomeIn, int centerX, int centerZ, Random diameterX)
    {
        MobSpawnInfo mobspawninfo = biomeIn.getMobSpawnInfo();
        List<MobSpawnInfo.Spawners> list = mobspawninfo.getSpawners(EntityClassification.CREATURE);

        if (!list.isEmpty())
        {
            int i = centerX << 4;
            int j = centerZ << 4;

            while (diameterX.nextFloat() < mobspawninfo.getCreatureSpawnProbability())
            {
                MobSpawnInfo.Spawners mobspawninfo$spawners = WeightedRandom.getRandomItem(diameterX, list);
                int k = mobspawninfo$spawners.minCount + diameterX.nextInt(1 + mobspawninfo$spawners.maxCount - mobspawninfo$spawners.minCount);
                ILivingEntityData ilivingentitydata = null;
                int l = i + diameterX.nextInt(16);
                int i1 = j + diameterX.nextInt(16);
                int j1 = l;
                int k1 = i1;

                for (int l1 = 0; l1 < k; ++l1)
                {
                    boolean flag = false;

                    for (int i2 = 0; !flag && i2 < 4; ++i2)
                    {
                        BlockPos blockpos = getTopSolidOrLiquidBlock(worldIn, mobspawninfo$spawners.type, l, i1);

                        if (mobspawninfo$spawners.type.isSummonable() && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementType(mobspawninfo$spawners.type), worldIn, blockpos, mobspawninfo$spawners.type))
                        {
                            float f = mobspawninfo$spawners.type.getWidth();
                            double d0 = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0D - (double)f);
                            double d1 = MathHelper.clamp((double)i1, (double)j + (double)f, (double)j + 16.0D - (double)f);

                            if (!worldIn.hasNoCollisions(mobspawninfo$spawners.type.getBoundingBoxWithSizeApplied(d0, (double)blockpos.getY(), d1)) || !EntitySpawnPlacementRegistry.canSpawnEntity(mobspawninfo$spawners.type, worldIn, SpawnReason.CHUNK_GENERATION, new BlockPos(d0, (double)blockpos.getY(), d1), worldIn.getRandom()))
                            {
                                continue;
                            }

                            Entity entity;

                            try
                            {
                                entity = mobspawninfo$spawners.type.create(worldIn.getWorld());
                            }
                            catch (Exception exception)
                            {
                                LOGGER.warn("Failed to create mob", (Throwable)exception);
                                continue;
                            }

                            entity.setLocationAndAngles(d0, (double)blockpos.getY(), d1, diameterX.nextFloat() * 360.0F, 0.0F);

                            if (entity instanceof MobEntity)
                            {
                                MobEntity mobentity = (MobEntity)entity;

                                if (mobentity.canSpawn(worldIn, SpawnReason.CHUNK_GENERATION) && mobentity.isNotColliding(worldIn))
                                {
                                    ilivingentitydata = mobentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(mobentity.getPosition()), SpawnReason.CHUNK_GENERATION, ilivingentitydata, (CompoundNBT)null);
                                    worldIn.func_242417_l(mobentity);
                                    flag = true;
                                }
                            }
                        }

                        l += diameterX.nextInt(5) - diameterX.nextInt(5);

                        for (i1 += diameterX.nextInt(5) - diameterX.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + diameterX.nextInt(5) - diameterX.nextInt(5))
                        {
                            l = j1 + diameterX.nextInt(5) - diameterX.nextInt(5);
                        }
                    }
                }
            }
        }
    }

    private static BlockPos getTopSolidOrLiquidBlock(IWorldReader worldIn, EntityType<?> p_208498_1_, int x, int z)
    {
        int i = worldIn.getHeight(EntitySpawnPlacementRegistry.func_209342_b(p_208498_1_), x, z);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, i, z);

        if (worldIn.getDimensionType().getHasCeiling())
        {
            do
            {
                blockpos$mutable.move(Direction.DOWN);
            }
            while (!worldIn.getBlockState(blockpos$mutable).isAir());

            do
            {
                blockpos$mutable.move(Direction.DOWN);
            }
            while (worldIn.getBlockState(blockpos$mutable).isAir() && blockpos$mutable.getY() > 0);
        }

        if (EntitySpawnPlacementRegistry.getPlacementType(p_208498_1_) == EntitySpawnPlacementRegistry.PlacementType.ON_GROUND)
        {
            BlockPos blockpos = blockpos$mutable.down();

            if (worldIn.getBlockState(blockpos).allowsMovement(worldIn, blockpos, PathType.LAND))
            {
                return blockpos;
            }
        }

        return blockpos$mutable.toImmutable();
    }

    public static class EntityDensityManager
    {
        private final int field_234981_a_;
        private final Object2IntOpenHashMap<EntityClassification> field_234982_b_;
        private final MobDensityTracker field_234983_c_;
        private final Object2IntMap<EntityClassification> field_234984_d_;
        @Nullable
        private BlockPos field_234985_e_;
        @Nullable
        private EntityType<?> field_234986_f_;
        private double field_234987_g_;

        private EntityDensityManager(int p_i231621_1_, Object2IntOpenHashMap<EntityClassification> p_i231621_2_, MobDensityTracker p_i231621_3_)
        {
            this.field_234981_a_ = p_i231621_1_;
            this.field_234982_b_ = p_i231621_2_;
            this.field_234983_c_ = p_i231621_3_;
            this.field_234984_d_ = Object2IntMaps.unmodifiable(p_i231621_2_);
        }

        private boolean func_234989_a_(EntityType<?> p_234989_1_, BlockPos p_234989_2_, IChunk p_234989_3_)
        {
            this.field_234985_e_ = p_234989_2_;
            this.field_234986_f_ = p_234989_1_;
            MobSpawnInfo.SpawnCosts mobspawninfo$spawncosts = WorldEntitySpawner.func_234980_b_(p_234989_2_, p_234989_3_).getMobSpawnInfo().getSpawnCost(p_234989_1_);

            if (mobspawninfo$spawncosts == null)
            {
                this.field_234987_g_ = 0.0D;
                return true;
            }
            else
            {
                double d0 = mobspawninfo$spawncosts.getEntitySpawnCost();
                this.field_234987_g_ = d0;
                double d1 = this.field_234983_c_.func_234999_b_(p_234989_2_, d0);
                return d1 <= mobspawninfo$spawncosts.getMaxSpawnCost();
            }
        }

        private void func_234990_a_(MobEntity p_234990_1_, IChunk p_234990_2_)
        {
            EntityType<?> entitytype = p_234990_1_.getType();
            BlockPos blockpos = p_234990_1_.getPosition();
            double d0;

            if (blockpos.equals(this.field_234985_e_) && entitytype == this.field_234986_f_)
            {
                d0 = this.field_234987_g_;
            }
            else
            {
                MobSpawnInfo.SpawnCosts mobspawninfo$spawncosts = WorldEntitySpawner.func_234980_b_(blockpos, p_234990_2_).getMobSpawnInfo().getSpawnCost(entitytype);

                if (mobspawninfo$spawncosts != null)
                {
                    d0 = mobspawninfo$spawncosts.getEntitySpawnCost();
                }
                else
                {
                    d0 = 0.0D;
                }
            }

            this.field_234983_c_.func_234998_a_(blockpos, d0);
            this.field_234982_b_.addTo(entitytype.getClassification(), 1);
        }

        public int func_234988_a_()
        {
            return this.field_234981_a_;
        }

        public Object2IntMap<EntityClassification> func_234995_b_()
        {
            return this.field_234984_d_;
        }

        private boolean func_234991_a_(EntityClassification p_234991_1_)
        {
            int i = p_234991_1_.getMaxNumberOfCreature() * this.field_234981_a_ / WorldEntitySpawner.field_234960_b_;
            return this.field_234982_b_.getInt(p_234991_1_) < i;
        }
    }

    @FunctionalInterface
    public interface IDensityCheck
    {
        boolean test(EntityType<?> p_test_1_, BlockPos p_test_2_, IChunk p_test_3_);
    }

    @FunctionalInterface
    public interface IInitialDensityAdder
    {
        void query(long p_query_1_, Consumer<Chunk> p_query_3_);
    }

    @FunctionalInterface
    public interface IOnSpawnDensityAdder
    {
        void run(MobEntity p_run_1_, IChunk p_run_2_);
    }
}
