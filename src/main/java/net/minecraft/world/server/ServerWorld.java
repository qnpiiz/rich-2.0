package net.minecraft.world.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.merchant.IReputationTracking;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapIdTracker;
import net.minecraft.world.storage.SaveFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World implements ISeedReader
{
    public static final BlockPos field_241108_a_ = new BlockPos(100, 50, 0);
    private static final Logger LOGGER = LogManager.getLogger();
    private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap<>();
    private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
    private final Queue<Entity> entitiesToAdd = Queues.newArrayDeque();
    private final List<ServerPlayerEntity> players = Lists.newArrayList();
    private final ServerChunkProvider field_241102_C_;
    boolean tickingEntities;
    private final MinecraftServer server;
    private final IServerWorldInfo field_241103_E_;
    public boolean disableLevelSaving;
    private boolean allPlayersSleeping;
    private int updateEntityTick;
    private final Teleporter worldTeleporter;
    private final ServerTickList<Block> pendingBlockTicks = new ServerTickList<>(this, (p_205341_0_) ->
    {
        return p_205341_0_ == null || p_205341_0_.getDefaultState().isAir();
    }, Registry.BLOCK::getKey, this::tickBlock);
    private final ServerTickList<Fluid> pendingFluidTicks = new ServerTickList<>(this, (p_205774_0_) ->
    {
        return p_205774_0_ == null || p_205774_0_ == Fluids.EMPTY;
    }, Registry.FLUID::getKey, this::tickFluid);
    private final Set<PathNavigator> navigations = Sets.newHashSet();
    protected final RaidManager raids;
    private final ObjectLinkedOpenHashSet<BlockEventData> blockEventQueue = new ObjectLinkedOpenHashSet<>();
    private boolean insideTick;
    private final List<ISpecialSpawner> field_241104_N_;
    @Nullable
    private final DragonFightManager field_241105_O_;
    private final StructureManager field_241106_P_;
    private final boolean field_241107_Q_;

    public ServerWorld(MinecraftServer p_i241885_1_, Executor p_i241885_2_, SaveFormat.LevelSave p_i241885_3_, IServerWorldInfo p_i241885_4_, RegistryKey<World> p_i241885_5_, DimensionType p_i241885_6_, IChunkStatusListener p_i241885_7_, ChunkGenerator p_i241885_8_, boolean p_i241885_9_, long p_i241885_10_, List<ISpecialSpawner> p_i241885_12_, boolean p_i241885_13_)
    {
        super(p_i241885_4_, p_i241885_5_, p_i241885_6_, p_i241885_1_::getProfiler, false, p_i241885_9_, p_i241885_10_);
        this.field_241107_Q_ = p_i241885_13_;
        this.server = p_i241885_1_;
        this.field_241104_N_ = p_i241885_12_;
        this.field_241103_E_ = p_i241885_4_;
        this.field_241102_C_ = new ServerChunkProvider(this, p_i241885_3_, p_i241885_1_.getDataFixer(), p_i241885_1_.func_240792_aT_(), p_i241885_2_, p_i241885_8_, p_i241885_1_.getPlayerList().getViewDistance(), p_i241885_1_.func_230540_aS_(), p_i241885_7_, () ->
        {
            return p_i241885_1_.func_241755_D_().getSavedData();
        });
        this.worldTeleporter = new Teleporter(this);
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
        this.getWorldBorder().setSize(p_i241885_1_.getMaxWorldSize());
        this.raids = this.getSavedData().getOrCreate(() ->
        {
            return new RaidManager(this);
        }, RaidManager.func_234620_a_(this.getDimensionType()));

        if (!p_i241885_1_.isSinglePlayer())
        {
            p_i241885_4_.setGameType(p_i241885_1_.getGameType());
        }

        this.field_241106_P_ = new StructureManager(this, p_i241885_1_.func_240793_aU_().getDimensionGeneratorSettings());

        if (this.getDimensionType().doesHasDragonFight())
        {
            this.field_241105_O_ = new DragonFightManager(this, p_i241885_1_.func_240793_aU_().getDimensionGeneratorSettings().getSeed(), p_i241885_1_.func_240793_aU_().getDragonFightData());
        }
        else
        {
            this.field_241105_O_ = null;
        }
    }

    public void func_241113_a_(int p_241113_1_, int p_241113_2_, boolean p_241113_3_, boolean p_241113_4_)
    {
        this.field_241103_E_.setClearWeatherTime(p_241113_1_);
        this.field_241103_E_.setRainTime(p_241113_2_);
        this.field_241103_E_.setThunderTime(p_241113_2_);
        this.field_241103_E_.setRaining(p_241113_3_);
        this.field_241103_E_.setThundering(p_241113_4_);
    }

    public Biome getNoiseBiomeRaw(int x, int y, int z)
    {
        return this.getChunkProvider().getChunkGenerator().getBiomeProvider().getNoiseBiome(x, y, z);
    }

    public StructureManager func_241112_a_()
    {
        return this.field_241106_P_;
    }

    /**
     * Runs a single tick for the world
     */
    public void tick(BooleanSupplier hasTimeLeft)
    {
        IProfiler iprofiler = this.getProfiler();
        this.insideTick = true;
        iprofiler.startSection("world border");
        this.getWorldBorder().tick();
        iprofiler.endStartSection("weather");
        boolean flag = this.isRaining();

        if (this.getDimensionType().hasSkyLight())
        {
            if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE))
            {
                int i = this.field_241103_E_.getClearWeatherTime();
                int j = this.field_241103_E_.getThunderTime();
                int k = this.field_241103_E_.getRainTime();
                boolean flag1 = this.worldInfo.isThundering();
                boolean flag2 = this.worldInfo.isRaining();

                if (i > 0)
                {
                    --i;
                    j = flag1 ? 0 : 1;
                    k = flag2 ? 0 : 1;
                    flag1 = false;
                    flag2 = false;
                }
                else
                {
                    if (j > 0)
                    {
                        --j;

                        if (j == 0)
                        {
                            flag1 = !flag1;
                        }
                    }
                    else if (flag1)
                    {
                        j = this.rand.nextInt(12000) + 3600;
                    }
                    else
                    {
                        j = this.rand.nextInt(168000) + 12000;
                    }

                    if (k > 0)
                    {
                        --k;

                        if (k == 0)
                        {
                            flag2 = !flag2;
                        }
                    }
                    else if (flag2)
                    {
                        k = this.rand.nextInt(12000) + 12000;
                    }
                    else
                    {
                        k = this.rand.nextInt(168000) + 12000;
                    }
                }

                this.field_241103_E_.setThunderTime(j);
                this.field_241103_E_.setRainTime(k);
                this.field_241103_E_.setClearWeatherTime(i);
                this.field_241103_E_.setThundering(flag1);
                this.field_241103_E_.setRaining(flag2);
            }

            this.prevThunderingStrength = this.thunderingStrength;

            if (this.worldInfo.isThundering())
            {
                this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
            }
            else
            {
                this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
            }

            this.thunderingStrength = MathHelper.clamp(this.thunderingStrength, 0.0F, 1.0F);
            this.prevRainingStrength = this.rainingStrength;

            if (this.worldInfo.isRaining())
            {
                this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
            }
            else
            {
                this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
            }

            this.rainingStrength = MathHelper.clamp(this.rainingStrength, 0.0F, 1.0F);
        }

        if (this.prevRainingStrength != this.rainingStrength)
        {
            this.server.getPlayerList().func_232642_a_(new SChangeGameStatePacket(SChangeGameStatePacket.field_241771_h_, this.rainingStrength), this.getDimensionKey());
        }

        if (this.prevThunderingStrength != this.thunderingStrength)
        {
            this.server.getPlayerList().func_232642_a_(new SChangeGameStatePacket(SChangeGameStatePacket.field_241772_i_, this.thunderingStrength), this.getDimensionKey());
        }

        if (flag != this.isRaining())
        {
            if (flag)
            {
                this.server.getPlayerList().sendPacketToAllPlayers(new SChangeGameStatePacket(SChangeGameStatePacket.field_241766_c_, 0.0F));
            }
            else
            {
                this.server.getPlayerList().sendPacketToAllPlayers(new SChangeGameStatePacket(SChangeGameStatePacket.field_241765_b_, 0.0F));
            }

            this.server.getPlayerList().sendPacketToAllPlayers(new SChangeGameStatePacket(SChangeGameStatePacket.field_241771_h_, this.rainingStrength));
            this.server.getPlayerList().sendPacketToAllPlayers(new SChangeGameStatePacket(SChangeGameStatePacket.field_241772_i_, this.thunderingStrength));
        }

        if (this.allPlayersSleeping && this.players.stream().noneMatch((p_241132_0_) ->
    {
        return !p_241132_0_.isSpectator() && !p_241132_0_.isPlayerFullyAsleep();
        }))
        {
            this.allPlayersSleeping = false;

            if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE))
            {
                long l = this.worldInfo.getDayTime() + 24000L;
                this.func_241114_a_(l - l % 24000L);
            }

            this.wakeUpAllPlayers();

            if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE))
            {
                this.resetRainAndThunder();
            }
        }
        this.calculateInitialSkylight();
        this.func_241126_b_();
        iprofiler.endStartSection("chunkSource");
        this.getChunkProvider().tick(hasTimeLeft);
        iprofiler.endStartSection("tickPending");

        if (!this.isDebug())
        {
            this.pendingBlockTicks.tick();
            this.pendingFluidTicks.tick();
        }

        iprofiler.endStartSection("raid");
        this.raids.tick();
        iprofiler.endStartSection("blockEvents");
        this.sendQueuedBlockEvents();
        this.insideTick = false;
        iprofiler.endStartSection("entities");
        boolean flag3 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();

        if (flag3)
        {
            this.resetUpdateEntityTick();
        }

        if (flag3 || this.updateEntityTick++ < 300)
        {
            if (this.field_241105_O_ != null)
            {
                this.field_241105_O_.tick();
            }

            this.tickingEntities = true;
            ObjectIterator<Entry<Entity>> objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();
            label164:

            while (true)
            {
                Entity entity1;

                while (true)
                {
                    if (!objectiterator.hasNext())
                    {
                        this.tickingEntities = false;
                        Entity entity;

                        while ((entity = this.entitiesToAdd.poll()) != null)
                        {
                            this.onEntityAdded(entity);
                        }

                        this.tickBlockEntities();
                        break label164;
                    }

                    Entry<Entity> entry = objectiterator.next();
                    entity1 = entry.getValue();
                    Entity entity2 = entity1.getRidingEntity();

                    if (!this.server.func_230537_U_() && (entity1 instanceof AnimalEntity || entity1 instanceof WaterMobEntity))
                    {
                        entity1.remove();
                    }

                    if (!this.server.func_230538_V_() && entity1 instanceof INPC)
                    {
                        entity1.remove();
                    }

                    iprofiler.startSection("checkDespawn");

                    if (!entity1.removed)
                    {
                        entity1.checkDespawn();
                    }

                    iprofiler.endSection();

                    if (entity2 == null)
                    {
                        break;
                    }

                    if (entity2.removed || !entity2.isPassenger(entity1))
                    {
                        entity1.stopRiding();
                        break;
                    }
                }

                iprofiler.startSection("tick");

                if (!entity1.removed && !(entity1 instanceof EnderDragonPartEntity))
                {
                    this.guardEntityTick(this::updateEntity, entity1);
                }

                iprofiler.endSection();
                iprofiler.startSection("remove");

                if (entity1.removed)
                {
                    this.removeFromChunk(entity1);
                    objectiterator.remove();
                    this.onEntityRemoved(entity1);
                }

                iprofiler.endSection();
            }
        }

        iprofiler.endSection();
    }

    protected void func_241126_b_()
    {
        if (this.field_241107_Q_)
        {
            long i = this.worldInfo.getGameTime() + 1L;
            this.field_241103_E_.setGameTime(i);
            this.field_241103_E_.getScheduledEvents().run(this.server, i);

            if (this.worldInfo.getGameRulesInstance().getBoolean(GameRules.DO_DAYLIGHT_CYCLE))
            {
                this.func_241114_a_(this.worldInfo.getDayTime() + 1L);
            }
        }
    }

    public void func_241114_a_(long p_241114_1_)
    {
        this.field_241103_E_.setDayTime(p_241114_1_);
    }

    public void func_241123_a_(boolean p_241123_1_, boolean p_241123_2_)
    {
        for (ISpecialSpawner ispecialspawner : this.field_241104_N_)
        {
            ispecialspawner.func_230253_a_(this, p_241123_1_, p_241123_2_);
        }
    }

    private void wakeUpAllPlayers()
    {
        this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach((p_241131_0_) ->
        {
            p_241131_0_.stopSleepInBed(false, false);
        });
    }

    public void tickEnvironment(Chunk chunkIn, int randomTickSpeed)
    {
        ChunkPos chunkpos = chunkIn.getPos();
        boolean flag = this.isRaining();
        int i = chunkpos.getXStart();
        int j = chunkpos.getZStart();
        IProfiler iprofiler = this.getProfiler();
        iprofiler.startSection("thunder");

        if (flag && this.isThundering() && this.rand.nextInt(100000) == 0)
        {
            BlockPos blockpos = this.adjustPosToNearbyEntity(this.getBlockRandomPos(i, 0, j, 15));

            if (this.isRainingAt(blockpos))
            {
                DifficultyInstance difficultyinstance = this.getDifficultyForLocation(blockpos);
                boolean flag1 = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && this.rand.nextDouble() < (double)difficultyinstance.getAdditionalDifficulty() * 0.01D;

                if (flag1)
                {
                    SkeletonHorseEntity skeletonhorseentity = EntityType.SKELETON_HORSE.create(this);
                    skeletonhorseentity.setTrap(true);
                    skeletonhorseentity.setGrowingAge(0);
                    skeletonhorseentity.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                    this.addEntity(skeletonhorseentity);
                }

                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this);
                lightningboltentity.moveForced(Vector3d.copyCenteredHorizontally(blockpos));
                lightningboltentity.setEffectOnly(flag1);
                this.addEntity(lightningboltentity);
            }
        }

        iprofiler.endStartSection("iceandsnow");

        if (this.rand.nextInt(16) == 0)
        {
            BlockPos blockpos2 = this.getHeight(Heightmap.Type.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
            BlockPos blockpos3 = blockpos2.down();
            Biome biome = this.getBiome(blockpos2);

            if (biome.doesWaterFreeze(this, blockpos3))
            {
                this.setBlockState(blockpos3, Blocks.ICE.getDefaultState());
            }

            if (flag && biome.doesSnowGenerate(this, blockpos2))
            {
                this.setBlockState(blockpos2, Blocks.SNOW.getDefaultState());
            }

            if (flag && this.getBiome(blockpos3).getPrecipitation() == Biome.RainType.RAIN)
            {
                this.getBlockState(blockpos3).getBlock().fillWithRain(this, blockpos3);
            }
        }

        iprofiler.endStartSection("tickBlocks");

        if (randomTickSpeed > 0)
        {
            for (ChunkSection chunksection : chunkIn.getSections())
            {
                if (chunksection != Chunk.EMPTY_SECTION && chunksection.needsRandomTickAny())
                {
                    int k = chunksection.getYLocation();

                    for (int l = 0; l < randomTickSpeed; ++l)
                    {
                        BlockPos blockpos1 = this.getBlockRandomPos(i, k, j, 15);
                        iprofiler.startSection("randomTick");
                        BlockState blockstate = chunksection.getBlockState(blockpos1.getX() - i, blockpos1.getY() - k, blockpos1.getZ() - j);

                        if (blockstate.ticksRandomly())
                        {
                            blockstate.randomTick(this, blockpos1, this.rand);
                        }

                        FluidState fluidstate = blockstate.getFluidState();

                        if (fluidstate.ticksRandomly())
                        {
                            fluidstate.randomTick(this, blockpos1, this.rand);
                        }

                        iprofiler.endSection();
                    }
                }
            }
        }

        iprofiler.endSection();
    }

    protected BlockPos adjustPosToNearbyEntity(BlockPos pos)
    {
        BlockPos blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, pos);
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), this.getHeight(), blockpos.getZ()))).grow(3.0D);
        List<LivingEntity> list = this.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb, (p_241115_1_) ->
        {
            return p_241115_1_ != null && p_241115_1_.isAlive() && this.canSeeSky(p_241115_1_.getPosition());
        });

        if (!list.isEmpty())
        {
            return list.get(this.rand.nextInt(list.size())).getPosition();
        }
        else
        {
            if (blockpos.getY() == -1)
            {
                blockpos = blockpos.up(2);
            }

            return blockpos;
        }
    }

    public boolean isInsideTick()
    {
        return this.insideTick;
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag()
    {
        this.allPlayersSleeping = false;

        if (!this.players.isEmpty())
        {
            int i = 0;
            int j = 0;

            for (ServerPlayerEntity serverplayerentity : this.players)
            {
                if (serverplayerentity.isSpectator())
                {
                    ++i;
                }
                else if (serverplayerentity.isSleeping())
                {
                    ++j;
                }
            }

            this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
        }
    }

    public ServerScoreboard getScoreboard()
    {
        return this.server.getScoreboard();
    }

    /**
     * Clears the current rain and thunder weather states.
     */
    private void resetRainAndThunder()
    {
        this.field_241103_E_.setRainTime(0);
        this.field_241103_E_.setRaining(false);
        this.field_241103_E_.setThunderTime(0);
        this.field_241103_E_.setThundering(false);
    }

    /**
     * Resets the updateEntityTick field to 0
     */
    public void resetUpdateEntityTick()
    {
        this.updateEntityTick = 0;
    }

    private void tickFluid(NextTickListEntry<Fluid> fluidTickEntry)
    {
        FluidState fluidstate = this.getFluidState(fluidTickEntry.position);

        if (fluidstate.getFluid() == fluidTickEntry.getTarget())
        {
            fluidstate.tick(this, fluidTickEntry.position);
        }
    }

    private void tickBlock(NextTickListEntry<Block> blockTickEntry)
    {
        BlockState blockstate = this.getBlockState(blockTickEntry.position);

        if (blockstate.isIn(blockTickEntry.getTarget()))
        {
            blockstate.tick(this, blockTickEntry.position, this.rand);
        }
    }

    public void updateEntity(Entity entityIn)
    {
        if (!(entityIn instanceof PlayerEntity) && !this.getChunkProvider().isChunkLoaded(entityIn))
        {
            this.chunkCheck(entityIn);
        }
        else
        {
            entityIn.forceSetPosition(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
            entityIn.prevRotationYaw = entityIn.rotationYaw;
            entityIn.prevRotationPitch = entityIn.rotationPitch;

            if (entityIn.addedToChunk)
            {
                ++entityIn.ticksExisted;
                IProfiler iprofiler = this.getProfiler();
                iprofiler.startSection(() ->
                {
                    return Registry.ENTITY_TYPE.getKey(entityIn.getType()).toString();
                });
                iprofiler.func_230035_c_("tickNonPassenger");
                entityIn.tick();
                iprofiler.endSection();
            }

            this.chunkCheck(entityIn);

            if (entityIn.addedToChunk)
            {
                for (Entity entity : entityIn.getPassengers())
                {
                    this.tickPassenger(entityIn, entity);
                }
            }
        }
    }

    public void tickPassenger(Entity ridingEntity, Entity passengerEntity)
    {
        if (!passengerEntity.removed && passengerEntity.getRidingEntity() == ridingEntity)
        {
            if (passengerEntity instanceof PlayerEntity || this.getChunkProvider().isChunkLoaded(passengerEntity))
            {
                passengerEntity.forceSetPosition(passengerEntity.getPosX(), passengerEntity.getPosY(), passengerEntity.getPosZ());
                passengerEntity.prevRotationYaw = passengerEntity.rotationYaw;
                passengerEntity.prevRotationPitch = passengerEntity.rotationPitch;

                if (passengerEntity.addedToChunk)
                {
                    ++passengerEntity.ticksExisted;
                    IProfiler iprofiler = this.getProfiler();
                    iprofiler.startSection(() ->
                    {
                        return Registry.ENTITY_TYPE.getKey(passengerEntity.getType()).toString();
                    });
                    iprofiler.func_230035_c_("tickPassenger");
                    passengerEntity.updateRidden();
                    iprofiler.endSection();
                }

                this.chunkCheck(passengerEntity);

                if (passengerEntity.addedToChunk)
                {
                    for (Entity entity : passengerEntity.getPassengers())
                    {
                        this.tickPassenger(passengerEntity, entity);
                    }
                }
            }
        }
        else
        {
            passengerEntity.stopRiding();
        }
    }

    public void chunkCheck(Entity entityIn)
    {
        if (entityIn.func_233578_ci_())
        {
            this.getProfiler().startSection("chunkCheck");
            int i = MathHelper.floor(entityIn.getPosX() / 16.0D);
            int j = MathHelper.floor(entityIn.getPosY() / 16.0D);
            int k = MathHelper.floor(entityIn.getPosZ() / 16.0D);

            if (!entityIn.addedToChunk || entityIn.chunkCoordX != i || entityIn.chunkCoordY != j || entityIn.chunkCoordZ != k)
            {
                if (entityIn.addedToChunk && this.chunkExists(entityIn.chunkCoordX, entityIn.chunkCoordZ))
                {
                    this.getChunk(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
                }

                if (!entityIn.func_233577_ch_() && !this.chunkExists(i, k))
                {
                    if (entityIn.addedToChunk)
                    {
                        LOGGER.warn("Entity {} left loaded chunk area", (Object)entityIn);
                    }

                    entityIn.addedToChunk = false;
                }
                else
                {
                    this.getChunk(i, k).addEntity(entityIn);
                }
            }

            this.getProfiler().endSection();
        }
    }

    public boolean isBlockModifiable(PlayerEntity player, BlockPos pos)
    {
        return !this.server.isBlockProtected(this, pos, player) && this.getWorldBorder().contains(pos);
    }

    public void save(@Nullable IProgressUpdate progress, boolean flush, boolean skipSave)
    {
        ServerChunkProvider serverchunkprovider = this.getChunkProvider();

        if (!skipSave)
        {
            if (progress != null)
            {
                progress.displaySavingString(new TranslationTextComponent("menu.savingLevel"));
            }

            this.saveLevel();

            if (progress != null)
            {
                progress.displayLoadingString(new TranslationTextComponent("menu.savingChunks"));
            }

            serverchunkprovider.save(flush);
        }
    }

    /**
     * Saves the chunks to disk.
     */
    private void saveLevel()
    {
        if (this.field_241105_O_ != null)
        {
            this.server.func_240793_aU_().setDragonFightData(this.field_241105_O_.write());
        }

        this.getChunkProvider().getSavedData().save();
    }

    public List<Entity> getEntities(@Nullable EntityType<?> entityTypeIn, Predicate <? super Entity > predicateIn)
    {
        List<Entity> list = Lists.newArrayList();
        ServerChunkProvider serverchunkprovider = this.getChunkProvider();

        for (Entity entity : this.entitiesById.values())
        {
            if ((entityTypeIn == null || entity.getType() == entityTypeIn) && serverchunkprovider.chunkExists(MathHelper.floor(entity.getPosX()) >> 4, MathHelper.floor(entity.getPosZ()) >> 4) && predicateIn.test(entity))
            {
                list.add(entity);
            }
        }

        return list;
    }

    public List<EnderDragonEntity> getDragons()
    {
        List<EnderDragonEntity> list = Lists.newArrayList();

        for (Entity entity : this.entitiesById.values())
        {
            if (entity instanceof EnderDragonEntity && entity.isAlive())
            {
                list.add((EnderDragonEntity)entity);
            }
        }

        return list;
    }

    public List<ServerPlayerEntity> getPlayers(Predicate <? super ServerPlayerEntity > predicateIn)
    {
        List<ServerPlayerEntity> list = Lists.newArrayList();

        for (ServerPlayerEntity serverplayerentity : this.players)
        {
            if (predicateIn.test(serverplayerentity))
            {
                list.add(serverplayerentity);
            }
        }

        return list;
    }

    @Nullable
    public ServerPlayerEntity getRandomPlayer()
    {
        List<ServerPlayerEntity> list = this.getPlayers(LivingEntity::isAlive);
        return list.isEmpty() ? null : list.get(this.rand.nextInt(list.size()));
    }

    public boolean addEntity(Entity entityIn)
    {
        return this.addEntity0(entityIn);
    }

    /**
     * Used for "unnatural" ways of entities appearing in the world, e.g. summon command, interdimensional teleports
     */
    public boolean summonEntity(Entity entityIn)
    {
        return this.addEntity0(entityIn);
    }

    public void addFromAnotherDimension(Entity entityIn)
    {
        boolean flag = entityIn.forceSpawn;
        entityIn.forceSpawn = true;
        this.summonEntity(entityIn);
        entityIn.forceSpawn = flag;
        this.chunkCheck(entityIn);
    }

    public void addDuringCommandTeleport(ServerPlayerEntity playerIn)
    {
        this.addPlayer(playerIn);
        this.chunkCheck(playerIn);
    }

    public void addDuringPortalTeleport(ServerPlayerEntity playerIn)
    {
        this.addPlayer(playerIn);
        this.chunkCheck(playerIn);
    }

    public void addNewPlayer(ServerPlayerEntity player)
    {
        this.addPlayer(player);
    }

    public void addRespawnedPlayer(ServerPlayerEntity player)
    {
        this.addPlayer(player);
    }

    private void addPlayer(ServerPlayerEntity player)
    {
        Entity entity = this.entitiesByUuid.get(player.getUniqueID());

        if (entity != null)
        {
            LOGGER.warn("Force-added player with duplicate UUID {}", (Object)player.getUniqueID().toString());
            entity.detach();
            this.removePlayer((ServerPlayerEntity)entity);
        }

        this.players.add(player);
        this.updateAllPlayersSleepingFlag();
        IChunk ichunk = this.getChunk(MathHelper.floor(player.getPosX() / 16.0D), MathHelper.floor(player.getPosZ() / 16.0D), ChunkStatus.FULL, true);

        if (ichunk instanceof Chunk)
        {
            ichunk.addEntity(player);
        }

        this.onEntityAdded(player);
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    private boolean addEntity0(Entity entityIn)
    {
        if (entityIn.removed)
        {
            LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getKey(entityIn.getType()));
            return false;
        }
        else if (this.hasDuplicateEntity(entityIn))
        {
            return false;
        }
        else
        {
            IChunk ichunk = this.getChunk(MathHelper.floor(entityIn.getPosX() / 16.0D), MathHelper.floor(entityIn.getPosZ() / 16.0D), ChunkStatus.FULL, entityIn.forceSpawn);

            if (!(ichunk instanceof Chunk))
            {
                return false;
            }
            else
            {
                ichunk.addEntity(entityIn);
                this.onEntityAdded(entityIn);
                return true;
            }
        }
    }

    public boolean addEntityIfNotDuplicate(Entity entityIn)
    {
        if (this.hasDuplicateEntity(entityIn))
        {
            return false;
        }
        else
        {
            this.onEntityAdded(entityIn);
            return true;
        }
    }

    private boolean hasDuplicateEntity(Entity entityIn)
    {
        UUID uuid = entityIn.getUniqueID();
        Entity entity = this.func_242105_c(uuid);

        if (entity == null)
        {
            return false;
        }
        else
        {
            LOGGER.warn("Trying to add entity with duplicated UUID {}. Existing {}#{}, new: {}#{}", uuid, EntityType.getKey(entity.getType()), entity.getEntityId(), EntityType.getKey(entityIn.getType()), entityIn.getEntityId());
            return true;
        }
    }

    @Nullable
    private Entity func_242105_c(UUID p_242105_1_)
    {
        Entity entity = this.entitiesByUuid.get(p_242105_1_);

        if (entity != null)
        {
            return entity;
        }
        else
        {
            if (this.tickingEntities)
            {
                for (Entity entity1 : this.entitiesToAdd)
                {
                    if (entity1.getUniqueID().equals(p_242105_1_))
                    {
                        return entity1;
                    }
                }
            }

            return null;
        }
    }

    public boolean func_242106_g(Entity p_242106_1_)
    {
        if (p_242106_1_.getSelfAndPassengers().anyMatch(this::hasDuplicateEntity))
        {
            return false;
        }
        else
        {
            this.func_242417_l(p_242106_1_);
            return true;
        }
    }

    public void onChunkUnloading(Chunk chunkIn)
    {
        this.tileEntitiesToBeRemoved.addAll(chunkIn.getTileEntityMap().values());
        ClassInheritanceMultiMap<Entity>[] aclassinheritancemultimap = chunkIn.getEntityLists();
        int i = aclassinheritancemultimap.length;

        for (int j = 0; j < i; ++j)
        {
            for (Entity entity : aclassinheritancemultimap[j])
            {
                if (!(entity instanceof ServerPlayerEntity))
                {
                    if (this.tickingEntities)
                    {
                        throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException("Removing entity while ticking!"));
                    }

                    this.entitiesById.remove(entity.getEntityId());
                    this.onEntityRemoved(entity);
                }
            }
        }
    }

    public void onEntityRemoved(Entity entityIn)
    {
        if (entityIn instanceof EnderDragonEntity)
        {
            for (EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entityIn).getDragonParts())
            {
                enderdragonpartentity.remove();
            }
        }

        this.entitiesByUuid.remove(entityIn.getUniqueID());
        this.getChunkProvider().untrack(entityIn);

        if (entityIn instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityIn;
            this.players.remove(serverplayerentity);
        }

        this.getScoreboard().removeEntity(entityIn);

        if (entityIn instanceof MobEntity)
        {
            this.navigations.remove(((MobEntity)entityIn).getNavigator());
        }
    }

    private void onEntityAdded(Entity entityIn)
    {
        if (this.tickingEntities)
        {
            this.entitiesToAdd.add(entityIn);
        }
        else
        {
            this.entitiesById.put(entityIn.getEntityId(), entityIn);

            if (entityIn instanceof EnderDragonEntity)
            {
                for (EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entityIn).getDragonParts())
                {
                    this.entitiesById.put(enderdragonpartentity.getEntityId(), enderdragonpartentity);
                }
            }

            this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
            this.getChunkProvider().track(entityIn);

            if (entityIn instanceof MobEntity)
            {
                this.navigations.add(((MobEntity)entityIn).getNavigator());
            }
        }
    }

    public void removeEntity(Entity entityIn)
    {
        if (this.tickingEntities)
        {
            throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException("Removing entity while ticking!"));
        }
        else
        {
            this.removeFromChunk(entityIn);
            this.entitiesById.remove(entityIn.getEntityId());
            this.onEntityRemoved(entityIn);
        }
    }

    private void removeFromChunk(Entity entityIn)
    {
        IChunk ichunk = this.getChunk(entityIn.chunkCoordX, entityIn.chunkCoordZ, ChunkStatus.FULL, false);

        if (ichunk instanceof Chunk)
        {
            ((Chunk)ichunk).removeEntity(entityIn);
        }
    }

    public void removePlayer(ServerPlayerEntity player)
    {
        player.remove();
        this.removeEntity(player);
        this.updateAllPlayersSleepingFlag();
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        for (ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers())
        {
            if (serverplayerentity != null && serverplayerentity.world == this && serverplayerentity.getEntityId() != breakerId)
            {
                double d0 = (double)pos.getX() - serverplayerentity.getPosX();
                double d1 = (double)pos.getY() - serverplayerentity.getPosY();
                double d2 = (double)pos.getZ() - serverplayerentity.getPosZ();

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D)
                {
                    serverplayerentity.connection.sendPacket(new SAnimateBlockBreakPacket(breakerId, pos, progress));
                }
            }
        }
    }

    public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {
        this.server.getPlayerList().sendToAllNearExcept(player, x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0D, this.getDimensionKey(), new SPlaySoundEffectPacket(soundIn, category, x, y, z, volume, pitch));
    }

    public void playMovingSound(@Nullable PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch)
    {
        this.server.getPlayerList().sendToAllNearExcept(playerIn, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), volume > 1.0F ? (double)(16.0F * volume) : 16.0D, this.getDimensionKey(), new SSpawnMovingSoundEffectPacket(eventIn, categoryIn, entityIn, volume, pitch));
    }

    public void playBroadcastSound(int id, BlockPos pos, int data)
    {
        this.server.getPlayerList().sendPacketToAllPlayers(new SPlaySoundEventPacket(id, pos, data, true));
    }

    public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data)
    {
        this.server.getPlayerList().sendToAllNearExcept(player, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 64.0D, this.getDimensionKey(), new SPlaySoundEventPacket(type, pos, data, false));
    }

    /**
     * Flags are as in setBlockState
     */
    public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags)
    {
        this.getChunkProvider().markBlockChanged(pos);
        VoxelShape voxelshape = oldState.getCollisionShape(this, pos);
        VoxelShape voxelshape1 = newState.getCollisionShape(this, pos);

        if (VoxelShapes.compare(voxelshape, voxelshape1, IBooleanFunction.NOT_SAME))
        {
            for (PathNavigator pathnavigator : this.navigations)
            {
                if (!pathnavigator.canUpdatePathOnTimeout())
                {
                    pathnavigator.onUpdateNavigation(pos);
                }
            }
        }
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity entityIn, byte state)
    {
        this.getChunkProvider().sendToTrackingAndSelf(entityIn, new SEntityStatusPacket(entityIn, state));
    }

    /**
     * Gets the world's chunk provider
     */
    public ServerChunkProvider getChunkProvider()
    {
        return this.field_241102_C_;
    }

    public Explosion createExplosion(@Nullable Entity exploder, @Nullable DamageSource damageSource, @Nullable ExplosionContext context, double x, double y, double z, float size, boolean causesFire, Explosion.Mode mode)
    {
        Explosion explosion = new Explosion(this, exploder, damageSource, context, x, y, z, size, causesFire, mode);
        explosion.doExplosionA();
        explosion.doExplosionB(false);

        if (mode == Explosion.Mode.NONE)
        {
            explosion.clearAffectedBlockPositions();
        }

        for (ServerPlayerEntity serverplayerentity : this.players)
        {
            if (serverplayerentity.getDistanceSq(x, y, z) < 4096.0D)
            {
                serverplayerentity.connection.sendPacket(new SExplosionPacket(x, y, z, size, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(serverplayerentity)));
            }
        }

        return explosion;
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam)
    {
        this.blockEventQueue.add(new BlockEventData(pos, blockIn, eventID, eventParam));
    }

    private void sendQueuedBlockEvents()
    {
        while (!this.blockEventQueue.isEmpty())
        {
            BlockEventData blockeventdata = this.blockEventQueue.removeFirst();

            if (this.fireBlockEvent(blockeventdata))
            {
                this.server.getPlayerList().sendToAllNearExcept((PlayerEntity)null, (double)blockeventdata.getPosition().getX(), (double)blockeventdata.getPosition().getY(), (double)blockeventdata.getPosition().getZ(), 64.0D, this.getDimensionKey(), new SBlockActionPacket(blockeventdata.getPosition(), blockeventdata.getBlock(), blockeventdata.getEventID(), blockeventdata.getEventParameter()));
            }
        }
    }

    private boolean fireBlockEvent(BlockEventData event)
    {
        BlockState blockstate = this.getBlockState(event.getPosition());
        return blockstate.isIn(event.getBlock()) ? blockstate.receiveBlockEvent(this, event.getPosition(), event.getEventID(), event.getEventParameter()) : false;
    }

    public ServerTickList<Block> getPendingBlockTicks()
    {
        return this.pendingBlockTicks;
    }

    public ServerTickList<Fluid> getPendingFluidTicks()
    {
        return this.pendingFluidTicks;
    }

    @Nonnull
    public MinecraftServer getServer()
    {
        return this.server;
    }

    public Teleporter getDefaultTeleporter()
    {
        return this.worldTeleporter;
    }

    public TemplateManager getStructureTemplateManager()
    {
        return this.server.func_240792_aT_();
    }

    public <T extends IParticleData> int spawnParticle(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed)
    {
        SSpawnParticlePacket sspawnparticlepacket = new SSpawnParticlePacket(type, false, posX, posY, posZ, (float)xOffset, (float)yOffset, (float)zOffset, (float)speed, particleCount);
        int i = 0;

        for (int j = 0; j < this.players.size(); ++j)
        {
            ServerPlayerEntity serverplayerentity = this.players.get(j);

            if (this.sendPacketWithinDistance(serverplayerentity, false, posX, posY, posZ, sspawnparticlepacket))
            {
                ++i;
            }
        }

        return i;
    }

    public <T extends IParticleData> boolean spawnParticle(ServerPlayerEntity player, T type, boolean longDistance, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed)
    {
        IPacket<?> ipacket = new SSpawnParticlePacket(type, longDistance, posX, posY, posZ, (float)xOffset, (float)yOffset, (float)zOffset, (float)speed, particleCount);
        return this.sendPacketWithinDistance(player, longDistance, posX, posY, posZ, ipacket);
    }

    private boolean sendPacketWithinDistance(ServerPlayerEntity player, boolean longDistance, double posX, double posY, double posZ, IPacket<?> packet)
    {
        if (player.getServerWorld() != this)
        {
            return false;
        }
        else
        {
            BlockPos blockpos = player.getPosition();

            if (blockpos.withinDistance(new Vector3d(posX, posY, posZ), longDistance ? 512.0D : 32.0D))
            {
                player.connection.sendPacket(packet);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    @Nullable

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(int id)
    {
        return this.entitiesById.get(id);
    }

    @Nullable
    public Entity getEntityByUuid(UUID uniqueId)
    {
        return this.entitiesByUuid.get(uniqueId);
    }

    @Nullable
    public BlockPos func_241117_a_(Structure<?> p_241117_1_, BlockPos p_241117_2_, int p_241117_3_, boolean p_241117_4_)
    {
        return !this.server.func_240793_aU_().getDimensionGeneratorSettings().doesGenerateFeatures() ? null : this.getChunkProvider().getChunkGenerator().func_235956_a_(this, p_241117_1_, p_241117_2_, p_241117_3_, p_241117_4_);
    }

    @Nullable
    public BlockPos func_241116_a_(Biome p_241116_1_, BlockPos p_241116_2_, int p_241116_3_, int p_241116_4_)
    {
        return this.getChunkProvider().getChunkGenerator().getBiomeProvider().findBiomePosition(p_241116_2_.getX(), p_241116_2_.getY(), p_241116_2_.getZ(), p_241116_3_, p_241116_4_, (p_242102_1_) ->
        {
            return p_242102_1_ == p_241116_1_;
        }, this.rand, true);
    }

    public RecipeManager getRecipeManager()
    {
        return this.server.getRecipeManager();
    }

    public ITagCollectionSupplier getTags()
    {
        return this.server.func_244266_aF();
    }

    public boolean isSaveDisabled()
    {
        return this.disableLevelSaving;
    }

    public DynamicRegistries func_241828_r()
    {
        return this.server.func_244267_aX();
    }

    public DimensionSavedDataManager getSavedData()
    {
        return this.getChunkProvider().getSavedData();
    }

    @Nullable
    public MapData getMapData(String mapName)
    {
        return this.getServer().func_241755_D_().getSavedData().get(() ->
        {
            return new MapData(mapName);
        }, mapName);
    }

    public void registerMapData(MapData mapDataIn)
    {
        this.getServer().func_241755_D_().getSavedData().set(mapDataIn);
    }

    public int getNextMapId()
    {
        return this.getServer().func_241755_D_().getSavedData().getOrCreate(MapIdTracker::new, "idcounts").getNextId();
    }

    public void func_241124_a__(BlockPos p_241124_1_, float p_241124_2_)
    {
        ChunkPos chunkpos = new ChunkPos(new BlockPos(this.worldInfo.getSpawnX(), 0, this.worldInfo.getSpawnZ()));
        this.worldInfo.setSpawn(p_241124_1_, p_241124_2_);
        this.getChunkProvider().releaseTicket(TicketType.START, chunkpos, 11, Unit.INSTANCE);
        this.getChunkProvider().registerTicket(TicketType.START, new ChunkPos(p_241124_1_), 11, Unit.INSTANCE);
        this.getServer().getPlayerList().sendPacketToAllPlayers(new SWorldSpawnChangedPacket(p_241124_1_, p_241124_2_));
    }

    public BlockPos getSpawnPoint()
    {
        BlockPos blockpos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());

        if (!this.getWorldBorder().contains(blockpos))
        {
            blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
        }

        return blockpos;
    }

    public float func_242107_v()
    {
        return this.worldInfo.getSpawnAngle();
    }

    public LongSet getForcedChunks()
    {
        ForcedChunksSaveData forcedchunkssavedata = this.getSavedData().get(ForcedChunksSaveData::new, "chunks");
        return (LongSet)(forcedchunkssavedata != null ? LongSets.unmodifiable(forcedchunkssavedata.getChunks()) : LongSets.EMPTY_SET);
    }

    public boolean forceChunk(int chunkX, int chunkZ, boolean add)
    {
        ForcedChunksSaveData forcedchunkssavedata = this.getSavedData().getOrCreate(ForcedChunksSaveData::new, "chunks");
        ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
        long i = chunkpos.asLong();
        boolean flag;

        if (add)
        {
            flag = forcedchunkssavedata.getChunks().add(i);

            if (flag)
            {
                this.getChunk(chunkX, chunkZ);
            }
        }
        else
        {
            flag = forcedchunkssavedata.getChunks().remove(i);
        }

        forcedchunkssavedata.setDirty(flag);

        if (flag)
        {
            this.getChunkProvider().forceChunk(chunkpos, add);
        }

        return flag;
    }

    public List<ServerPlayerEntity> getPlayers()
    {
        return this.players;
    }

    public void onBlockStateChange(BlockPos pos, BlockState blockStateIn, BlockState newState)
    {
        Optional<PointOfInterestType> optional = PointOfInterestType.forState(blockStateIn);
        Optional<PointOfInterestType> optional1 = PointOfInterestType.forState(newState);

        if (!Objects.equals(optional, optional1))
        {
            BlockPos blockpos = pos.toImmutable();
            optional.ifPresent((p_241130_2_) ->
            {
                this.getServer().execute(() -> {
                    this.getPointOfInterestManager().remove(blockpos);
                    DebugPacketSender.func_218805_b(this, blockpos);
                });
            });
            optional1.ifPresent((p_217476_2_) ->
            {
                this.getServer().execute(() -> {
                    this.getPointOfInterestManager().add(blockpos, p_217476_2_);
                    DebugPacketSender.func_218799_a(this, blockpos);
                });
            });
        }
    }

    public PointOfInterestManager getPointOfInterestManager()
    {
        return this.getChunkProvider().getPointOfInterestManager();
    }

    public boolean isVillage(BlockPos pos)
    {
        return this.func_241119_a_(pos, 1);
    }

    public boolean isVillage(SectionPos pos)
    {
        return this.isVillage(pos.getCenter());
    }

    public boolean func_241119_a_(BlockPos p_241119_1_, int p_241119_2_)
    {
        if (p_241119_2_ > 6)
        {
            return false;
        }
        else
        {
            return this.sectionsToVillage(SectionPos.from(p_241119_1_)) <= p_241119_2_;
        }
    }

    public int sectionsToVillage(SectionPos pos)
    {
        return this.getPointOfInterestManager().sectionsToVillage(pos);
    }

    public RaidManager getRaids()
    {
        return this.raids;
    }

    @Nullable
    public Raid findRaid(BlockPos pos)
    {
        return this.raids.findRaid(pos, 9216);
    }

    public boolean hasRaid(BlockPos pos)
    {
        return this.findRaid(pos) != null;
    }

    public void updateReputation(IReputationType type, Entity target, IReputationTracking host)
    {
        host.updateReputation(type, target);
    }

    public void writeDebugInfo(Path pathIn) throws IOException
    {
        ChunkManager chunkmanager = this.getChunkProvider().chunkManager;

        try (Writer writer = Files.newBufferedWriter(pathIn.resolve("stats.txt")))
        {
            writer.write(String.format("spawning_chunks: %d\n", chunkmanager.getTicketManager().getSpawningChunksCount()));
            WorldEntitySpawner.EntityDensityManager worldentityspawner$entitydensitymanager = this.getChunkProvider().func_241101_k_();

            if (worldentityspawner$entitydensitymanager != null)
            {
                for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<EntityClassification> entry : worldentityspawner$entitydensitymanager.func_234995_b_().object2IntEntrySet())
                {
                    writer.write(String.format("spawn_count.%s: %d\n", entry.getKey().getName(), entry.getIntValue()));
                }
            }

            writer.write(String.format("entities: %d\n", this.entitiesById.size()));
            writer.write(String.format("block_entities: %d\n", this.loadedTileEntityList.size()));
            writer.write(String.format("block_ticks: %d\n", this.getPendingBlockTicks().func_225420_a()));
            writer.write(String.format("fluid_ticks: %d\n", this.getPendingFluidTicks().func_225420_a()));
            writer.write("distance_manager: " + chunkmanager.getTicketManager().func_225412_c() + "\n");
            writer.write(String.format("pending_tasks: %d\n", this.getChunkProvider().func_225314_f()));
        }

        CrashReport crashreport = new CrashReport("Level dump", new Exception("dummy"));
        this.fillCrashReport(crashreport);

        try (Writer writer1 = Files.newBufferedWriter(pathIn.resolve("example_crash.txt")))
        {
            writer1.write(crashreport.getCompleteReport());
        }

        Path path = pathIn.resolve("chunks.csv");

        try (Writer writer2 = Files.newBufferedWriter(path))
        {
            chunkmanager.func_225406_a(writer2);
        }

        Path path1 = pathIn.resolve("entities.csv");

        try (Writer writer3 = Files.newBufferedWriter(path1))
        {
            dumpEntities(writer3, this.entitiesById.values());
        }

        Path path2 = pathIn.resolve("block_entities.csv");

        try (Writer writer4 = Files.newBufferedWriter(path2))
        {
            this.dumpBlockEntities(writer4);
        }
    }

    private static void dumpEntities(Writer writerIn, Iterable<Entity> entities) throws IOException
    {
        CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("y").func_225423_a("z").func_225423_a("uuid").func_225423_a("type").func_225423_a("alive").func_225423_a("display_name").func_225423_a("custom_name").func_225422_a(writerIn);

        for (Entity entity : entities)
        {
            ITextComponent itextcomponent = entity.getCustomName();
            ITextComponent itextcomponent1 = entity.getDisplayName();
            csvwriter.func_225426_a(entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.getUniqueID(), Registry.ENTITY_TYPE.getKey(entity.getType()), entity.isAlive(), itextcomponent1.getString(), itextcomponent != null ? itextcomponent.getString() : null);
        }
    }

    private void dumpBlockEntities(Writer writerIn) throws IOException
    {
        CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("y").func_225423_a("z").func_225423_a("type").func_225422_a(writerIn);

        for (TileEntity tileentity : this.loadedTileEntityList)
        {
            BlockPos blockpos = tileentity.getPos();
            csvwriter.func_225426_a(blockpos.getX(), blockpos.getY(), blockpos.getZ(), Registry.BLOCK_ENTITY_TYPE.getKey(tileentity.getType()));
        }
    }

    @VisibleForTesting
    public void clearBlockEvents(MutableBoundingBox boundingBox)
    {
        this.blockEventQueue.removeIf((p_241118_1_) ->
        {
            return boundingBox.isVecInside(p_241118_1_.getPosition());
        });
    }

    public void func_230547_a_(BlockPos p_230547_1_, Block p_230547_2_)
    {
        if (!this.isDebug())
        {
            this.notifyNeighborsOfStateChange(p_230547_1_, p_230547_2_);
        }
    }

    public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_)
    {
        return 1.0F;
    }

    public Iterable<Entity> func_241136_z_()
    {
        return Iterables.unmodifiableIterable(this.entitiesById.values());
    }

    public String toString()
    {
        return "ServerLevel[" + this.field_241103_E_.getWorldName() + "]";
    }

    public boolean func_241109_A_()
    {
        return this.server.func_240793_aU_().getDimensionGeneratorSettings().func_236228_i_();
    }

    /**
     * gets the random world seed
     */
    public long getSeed()
    {
        return this.server.func_240793_aU_().getDimensionGeneratorSettings().getSeed();
    }

    @Nullable
    public DragonFightManager func_241110_C_()
    {
        return this.field_241105_O_;
    }

    public Stream <? extends StructureStart<? >> func_241827_a(SectionPos p_241827_1_, Structure<?> p_241827_2_)
    {
        return this.func_241112_a_().func_235011_a_(p_241827_1_, p_241827_2_);
    }

    public ServerWorld getWorld()
    {
        return this;
    }

    @VisibleForTesting
    public String func_244521_F()
    {
        return String.format("players: %s, entities: %d [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entitiesById.size(), func_244524_a(this.entitiesById.values(), (p_244527_0_) ->
        {
            return Registry.ENTITY_TYPE.getKey(p_244527_0_.getType());
        }), this.tickableTileEntities.size(), func_244524_a(this.tickableTileEntities, (p_244526_0_) ->
        {
            return Registry.BLOCK_ENTITY_TYPE.getKey(p_244526_0_.getType());
        }), this.getPendingBlockTicks().func_225420_a(), this.getPendingFluidTicks().func_225420_a(), this.getProviderName());
    }

    private static <T> String func_244524_a(Collection<T> p_244524_0_, Function<T, ResourceLocation> p_244524_1_)
    {
        try
        {
            Object2IntOpenHashMap<ResourceLocation> object2intopenhashmap = new Object2IntOpenHashMap<>();

            for (T t : p_244524_0_)
            {
                ResourceLocation resourcelocation = p_244524_1_.apply(t);
                object2intopenhashmap.addTo(resourcelocation, 1);
            }

            return object2intopenhashmap.object2IntEntrySet().stream().sorted(Comparator.comparing(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation>::getIntValue).reversed()).limit(5L).map((p_244523_0_) ->
            {
                return p_244523_0_.getKey() + ":" + p_244523_0_.getIntValue();
            }).collect(Collectors.joining(","));
        }
        catch (Exception exception)
        {
            return "";
        }
    }

    public static void func_241121_a_(ServerWorld p_241121_0_)
    {
        BlockPos blockpos = field_241108_a_;
        int i = blockpos.getX();
        int j = blockpos.getY() - 2;
        int k = blockpos.getZ();
        BlockPos.getAllInBoxMutable(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach((p_244430_1_) ->
        {
            p_241121_0_.setBlockState(p_244430_1_, Blocks.AIR.getDefaultState());
        });
        BlockPos.getAllInBoxMutable(i - 2, j, k - 2, i + 2, j, k + 2).forEach((p_241122_1_) ->
        {
            p_241121_0_.setBlockState(p_241122_1_, Blocks.OBSIDIAN.getDefaultState());
        });
    }
}
