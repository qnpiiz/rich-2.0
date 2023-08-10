package net.minecraft.client.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.particle.FireworkParticle;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.ColorCache;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.CubeCoordinateIterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.MapData;
import net.optifine.Config;
import net.optifine.CustomGuis;
import net.optifine.DynamicLights;
import net.optifine.RandomEntities;
import net.optifine.override.PlayerControllerOF;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;

public class ClientWorld extends World
{
    private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectOpenHashMap<>();
    private final ClientPlayNetHandler connection;
    private final WorldRenderer worldRenderer;
    private final ClientWorld.ClientWorldInfo field_239130_d_;
    private final DimensionRenderInfo field_239131_x_;
    private final Minecraft mc = Minecraft.getInstance();
    private final List<AbstractClientPlayerEntity> players = Lists.newArrayList();
    private Scoreboard scoreboard = new Scoreboard();
    private final Map<String, MapData> maps = Maps.newHashMap();
    private int timeLightningFlash;
    private final Object2ObjectArrayMap<ColorResolver, ColorCache> colorCaches = Util.make(new Object2ObjectArrayMap<>(3), (p_lambda$new$0_0_) ->
    {
        p_lambda$new$0_0_.put(BiomeColors.GRASS_COLOR, new ColorCache());
        p_lambda$new$0_0_.put(BiomeColors.FOLIAGE_COLOR, new ColorCache());
        p_lambda$new$0_0_.put(BiomeColors.WATER_COLOR, new ColorCache());
    });
    private final ClientChunkProvider field_239129_E_;
    private boolean playerUpdate = false;

    public ClientWorld(ClientPlayNetHandler p_i242067_1_, ClientWorld.ClientWorldInfo p_i242067_2_, RegistryKey<World> p_i242067_3_, DimensionType p_i242067_4_, int p_i242067_5_, Supplier<IProfiler> p_i242067_6_, WorldRenderer p_i242067_7_, boolean p_i242067_8_, long p_i242067_9_)
    {
        super(p_i242067_2_, p_i242067_3_, p_i242067_4_, p_i242067_6_, true, p_i242067_8_, p_i242067_9_);
        this.connection = p_i242067_1_;
        this.field_239129_E_ = new ClientChunkProvider(this, p_i242067_5_);
        this.field_239130_d_ = p_i242067_2_;
        this.worldRenderer = p_i242067_7_;
        this.field_239131_x_ = DimensionRenderInfo.func_243495_a(p_i242067_4_);
        this.func_239136_a_(new BlockPos(8, 64, 8), 0.0F);
        this.calculateInitialSkylight();
        this.calculateInitialWeather();

        if (Reflector.CapabilityProvider_gatherCapabilities.exists())
        {
            Reflector.call(this, Reflector.CapabilityProvider_gatherCapabilities);
        }

        Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, this);

        if (this.mc.playerController != null && this.mc.playerController.getClass() == PlayerController.class)
        {
            this.mc.playerController = new PlayerControllerOF(this.mc, this.connection);
            CustomGuis.setPlayerControllerOF((PlayerControllerOF)this.mc.playerController);
        }
    }

    public DimensionRenderInfo func_239132_a_()
    {
        return this.field_239131_x_;
    }

    /**
     * Runs a single tick for the world
     */
    public void tick(BooleanSupplier hasTimeLeft)
    {
        this.getWorldBorder().tick();
        this.func_239141_x_();
        this.getProfiler().startSection("blocks");
        this.field_239129_E_.tick(hasTimeLeft);
        this.getProfiler().endSection();
    }

    private void func_239141_x_()
    {
        this.func_239134_a_(this.worldInfo.getGameTime() + 1L);

        if (this.worldInfo.getGameRulesInstance().getBoolean(GameRules.DO_DAYLIGHT_CYCLE))
        {
            this.setDayTime(this.worldInfo.getDayTime() + 1L);
        }
    }

    public void func_239134_a_(long p_239134_1_)
    {
        this.field_239130_d_.setGameTime(p_239134_1_);
    }

    /**
     * Sets the world time.
     */
    public void setDayTime(long time)
    {
        if (time < 0L)
        {
            time = -time;
            this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, (MinecraftServer)null);
        }
        else
        {
            this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, (MinecraftServer)null);
        }

        this.field_239130_d_.setDayTime(time);
    }

    public Iterable<Entity> getAllEntities()
    {
        return this.entitiesById.values();
    }

    public void tickEntities()
    {
        IProfiler iprofiler = this.getProfiler();
        iprofiler.startSection("entities");
        ObjectIterator<Entry<Entity>> objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

        while (objectiterator.hasNext())
        {
            Entry<Entity> entry = objectiterator.next();
            Entity entity = entry.getValue();

            if (!entity.isPassenger())
            {
                iprofiler.startSection("tick");

                if (!entity.removed)
                {
                    this.guardEntityTick(this::updateEntity, entity);
                }

                iprofiler.endSection();
                iprofiler.startSection("remove");

                if (entity.removed)
                {
                    objectiterator.remove();
                    this.removeEntity(entity);
                }

                iprofiler.endSection();
            }
        }

        this.tickBlockEntities();
        iprofiler.endSection();
    }

    public void updateEntity(Entity entityIn)
    {
        if (!(entityIn instanceof PlayerEntity) && !this.getChunkProvider().isChunkLoaded(entityIn))
        {
            this.checkChunk(entityIn);
        }
        else
        {
            entityIn.forceSetPosition(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
            entityIn.prevRotationYaw = entityIn.rotationYaw;
            entityIn.prevRotationPitch = entityIn.rotationPitch;

            if (entityIn.addedToChunk || entityIn.isSpectator())
            {
                ++entityIn.ticksExisted;
                this.getProfiler().startSection(() ->
                {
                    return Registry.ENTITY_TYPE.getKey(entityIn.getType()).toString();
                });

                if (ReflectorForge.canUpdate(entityIn))
                {
                    entityIn.tick();
                }

                this.getProfiler().endSection();
            }

            this.checkChunk(entityIn);

            if (entityIn.addedToChunk)
            {
                for (Entity entity : entityIn.getPassengers())
                {
                    this.updateEntityRidden(entityIn, entity);
                }
            }
        }
    }

    public void updateEntityRidden(Entity p_217420_1_, Entity p_217420_2_)
    {
        if (!p_217420_2_.removed && p_217420_2_.getRidingEntity() == p_217420_1_)
        {
            if (p_217420_2_ instanceof PlayerEntity || this.getChunkProvider().isChunkLoaded(p_217420_2_))
            {
                p_217420_2_.forceSetPosition(p_217420_2_.getPosX(), p_217420_2_.getPosY(), p_217420_2_.getPosZ());
                p_217420_2_.prevRotationYaw = p_217420_2_.rotationYaw;
                p_217420_2_.prevRotationPitch = p_217420_2_.rotationPitch;

                if (p_217420_2_.addedToChunk)
                {
                    ++p_217420_2_.ticksExisted;
                    p_217420_2_.updateRidden();
                }

                this.checkChunk(p_217420_2_);

                if (p_217420_2_.addedToChunk)
                {
                    for (Entity entity : p_217420_2_.getPassengers())
                    {
                        this.updateEntityRidden(p_217420_2_, entity);
                    }
                }
            }
        }
        else
        {
            p_217420_2_.stopRiding();
        }
    }

    private void checkChunk(Entity entityIn)
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

    public void onChunkUnloaded(Chunk chunkIn)
    {
        Collection collection;

        if (Reflector.ForgeWorld_tileEntitiesToBeRemoved.exists())
        {
            collection = (Collection)Reflector.getFieldValue(this, Reflector.ForgeWorld_tileEntitiesToBeRemoved);
        }
        else
        {
            collection = this.tileEntitiesToBeRemoved;
        }

        collection.addAll(chunkIn.getTileEntityMap().values());
        this.field_239129_E_.getLightManager().enableLightSources(chunkIn.getPos(), false);
    }

    public void onChunkLoaded(int chunkX, int chunkZ)
    {
        this.colorCaches.forEach((p_lambda$onChunkLoaded$2_2_, p_lambda$onChunkLoaded$2_3_) ->
        {
            p_lambda$onChunkLoaded$2_3_.invalidateChunk(chunkX, chunkZ);
        });
    }

    public void clearColorCaches()
    {
        this.colorCaches.forEach((p_lambda$clearColorCaches$3_0_, p_lambda$clearColorCaches$3_1_) ->
        {
            p_lambda$clearColorCaches$3_1_.invalidateAll();
        });
    }

    public boolean chunkExists(int chunkX, int chunkZ)
    {
        return true;
    }

    public int getCountLoadedEntities()
    {
        return this.entitiesById.size();
    }

    public void addPlayer(int playerId, AbstractClientPlayerEntity playerEntityIn)
    {
        this.addEntityImpl(playerId, playerEntityIn);
        this.players.add(playerEntityIn);
    }

    public void addEntity(int entityIdIn, Entity entityToSpawn)
    {
        this.addEntityImpl(entityIdIn, entityToSpawn);
    }

    private void addEntityImpl(int entityIdIn, Entity entityToSpawn)
    {
        if (!Reflector.EntityJoinWorldEvent_Constructor.exists() || !Reflector.postForgeBusEvent(Reflector.EntityJoinWorldEvent_Constructor, entityToSpawn, this))
        {
            this.removeEntityFromWorld(entityIdIn);
            this.entitiesById.put(entityIdIn, entityToSpawn);
            this.getChunkProvider().getChunk(MathHelper.floor(entityToSpawn.getPosX() / 16.0D), MathHelper.floor(entityToSpawn.getPosZ() / 16.0D), ChunkStatus.FULL, true).addEntity(entityToSpawn);

            if (Reflector.IForgeEntity_onAddedToWorld.exists())
            {
                Reflector.call(entityToSpawn, Reflector.IForgeEntity_onAddedToWorld);
            }

            this.onEntityAdded(entityToSpawn);
        }
    }

    public void removeEntityFromWorld(int eid)
    {
        Entity entity = this.entitiesById.remove(eid);

        if (entity != null)
        {
            entity.remove();
            this.removeEntity(entity);
        }
    }

    private void removeEntity(Entity entityIn)
    {
        entityIn.detach();

        if (entityIn.addedToChunk)
        {
            this.getChunk(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntity(entityIn);
        }

        this.players.remove(entityIn);

        if (Reflector.IForgeEntity_onRemovedFromWorld.exists())
        {
            Reflector.call(entityIn, Reflector.IForgeEntity_onRemovedFromWorld);
        }

        if (Reflector.EntityLeaveWorldEvent_Constructor.exists())
        {
            Reflector.postForgeBusEvent(Reflector.EntityLeaveWorldEvent_Constructor, entityIn, this);
        }

        this.onEntityRemoved(entityIn);
    }

    public void addEntitiesToChunk(Chunk chunkIn)
    {
        for (Entry<Entity> entry : this.entitiesById.int2ObjectEntrySet())
        {
            Entity entity = entry.getValue();
            int i = MathHelper.floor(entity.getPosX() / 16.0D);
            int j = MathHelper.floor(entity.getPosZ() / 16.0D);

            if (i == chunkIn.getPos().x && j == chunkIn.getPos().z)
            {
                chunkIn.addEntity(entity);
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

    public void invalidateRegionAndSetBlock(BlockPos pos, BlockState state)
    {
        this.setBlockState(pos, state, 19);
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket()
    {
        this.connection.getNetworkManager().closeChannel(new TranslationTextComponent("multiplayer.status.quitting"));
    }

    public void animateTick(int posX, int posY, int posZ)
    {
        int i = 32;
        Random random = new Random();
        boolean flag = false;

        if (this.mc.playerController.getCurrentGameType() == GameType.CREATIVE)
        {
            for (ItemStack itemstack : this.mc.player.getHeldEquipment())
            {
                if (itemstack.getItem() == Blocks.BARRIER.asItem())
                {
                    flag = true;
                    break;
                }
            }
        }

        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int j = 0; j < 667; ++j)
        {
            this.animateTick(posX, posY, posZ, 16, random, flag, blockpos$mutable);
            this.animateTick(posX, posY, posZ, 32, random, flag, blockpos$mutable);
        }
    }

    public void animateTick(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.Mutable pos)
    {
        int i = x + this.rand.nextInt(offset) - this.rand.nextInt(offset);
        int j = y + this.rand.nextInt(offset) - this.rand.nextInt(offset);
        int k = z + this.rand.nextInt(offset) - this.rand.nextInt(offset);
        pos.setPos(i, j, k);
        BlockState blockstate = this.getBlockState(pos);
        blockstate.getBlock().animateTick(blockstate, this, pos, random);
        FluidState fluidstate = this.getFluidState(pos);

        if (!fluidstate.isEmpty())
        {
            fluidstate.animateTick(this, pos, random);
            IParticleData iparticledata = fluidstate.getDripParticleData();

            if (iparticledata != null && this.rand.nextInt(10) == 0)
            {
                boolean flag = blockstate.isSolidSide(this, pos, Direction.DOWN);
                BlockPos blockpos = pos.down();
                this.spawnFluidParticle(blockpos, this.getBlockState(blockpos), iparticledata, flag);
            }
        }

        if (holdingBarrier && blockstate.isIn(Blocks.BARRIER))
        {
            this.addParticle(ParticleTypes.BARRIER, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 0.0D, 0.0D, 0.0D);
        }

        if (!blockstate.hasOpaqueCollisionShape(this, pos))
        {
            this.getBiome(pos).getAmbientParticle().ifPresent((p_lambda$animateTick$4_2_) ->
            {
                if (p_lambda$animateTick$4_2_.shouldParticleSpawn(this.rand))
                {
                    this.addParticle(p_lambda$animateTick$4_2_.getParticleOptions(), (double)pos.getX() + this.rand.nextDouble(), (double)pos.getY() + this.rand.nextDouble(), (double)pos.getZ() + this.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                }
            });
        }
    }

    private void spawnFluidParticle(BlockPos blockPosIn, BlockState blockStateIn, IParticleData particleDataIn, boolean shapeDownSolid)
    {
        if (blockStateIn.getFluidState().isEmpty())
        {
            VoxelShape voxelshape = blockStateIn.getCollisionShape(this, blockPosIn);
            double d0 = voxelshape.getEnd(Direction.Axis.Y);

            if (d0 < 1.0D)
            {
                if (shapeDownSolid)
                {
                    this.spawnParticle((double)blockPosIn.getX(), (double)(blockPosIn.getX() + 1), (double)blockPosIn.getZ(), (double)(blockPosIn.getZ() + 1), (double)(blockPosIn.getY() + 1) - 0.05D, particleDataIn);
                }
            }
            else if (!blockStateIn.isIn(BlockTags.IMPERMEABLE))
            {
                double d1 = voxelshape.getStart(Direction.Axis.Y);

                if (d1 > 0.0D)
                {
                    this.spawnParticle(blockPosIn, particleDataIn, voxelshape, (double)blockPosIn.getY() + d1 - 0.05D);
                }
                else
                {
                    BlockPos blockpos = blockPosIn.down();
                    BlockState blockstate = this.getBlockState(blockpos);
                    VoxelShape voxelshape1 = blockstate.getCollisionShape(this, blockpos);
                    double d2 = voxelshape1.getEnd(Direction.Axis.Y);

                    if (d2 < 1.0D && blockstate.getFluidState().isEmpty())
                    {
                        this.spawnParticle(blockPosIn, particleDataIn, voxelshape, (double)blockPosIn.getY() - 0.05D);
                    }
                }
            }
        }
    }

    private void spawnParticle(BlockPos posIn, IParticleData particleDataIn, VoxelShape voxelShapeIn, double y)
    {
        this.spawnParticle((double)posIn.getX() + voxelShapeIn.getStart(Direction.Axis.X), (double)posIn.getX() + voxelShapeIn.getEnd(Direction.Axis.X), (double)posIn.getZ() + voxelShapeIn.getStart(Direction.Axis.Z), (double)posIn.getZ() + voxelShapeIn.getEnd(Direction.Axis.Z), y, particleDataIn);
    }

    private void spawnParticle(double xStart, double xEnd, double zStart, double zEnd, double y, IParticleData particleDataIn)
    {
        this.addParticle(particleDataIn, MathHelper.lerp(this.rand.nextDouble(), xStart, xEnd), y, MathHelper.lerp(this.rand.nextDouble(), zStart, zEnd), 0.0D, 0.0D, 0.0D);
    }

    /**
     * also releases skins.
     */
    public void removeAllEntities()
    {
        ObjectIterator<Entry<Entity>> objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

        while (objectiterator.hasNext())
        {
            Entry<Entity> entry = objectiterator.next();
            Entity entity = entry.getValue();

            if (entity.removed)
            {
                objectiterator.remove();
                this.removeEntity(entity);
            }
        }
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory fillCrashReport(CrashReport report)
    {
        CrashReportCategory crashreportcategory = super.fillCrashReport(report);
        crashreportcategory.addDetail("Server brand", () ->
        {
            return this.mc.player.getServerBrand();
        });
        crashreportcategory.addDetail("Server type", () ->
        {
            return this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
        });
        return crashreportcategory;
    }

    public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {
        if (Reflector.ForgeEventFactory_onPlaySoundAtEntity.exists())
        {
            Object object = Reflector.ForgeEventFactory_onPlaySoundAtEntity.call(player, soundIn, category, volume, pitch);

            if (Reflector.callBoolean(object, Reflector.Event_isCanceled) || Reflector.call(object, Reflector.PlaySoundAtEntityEvent_getSound) == null)
            {
                return;
            }

            soundIn = (SoundEvent)Reflector.call(object, Reflector.PlaySoundAtEntityEvent_getSound);
            category = (SoundCategory)Reflector.call(object, Reflector.PlaySoundAtEntityEvent_getCategory);
            volume = Reflector.callFloat(object, Reflector.PlaySoundAtEntityEvent_getVolume);
        }

        if (player == this.mc.player)
        {
            this.playSound(x, y, z, soundIn, category, volume, pitch, false);
        }
    }

    public void playMovingSound(@Nullable PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch)
    {
        if (Reflector.ForgeEventFactory_onPlaySoundAtEntity.exists())
        {
            Object object = Reflector.ForgeEventFactory_onPlaySoundAtEntity.call(playerIn, eventIn, categoryIn, volume, pitch);

            if (Reflector.callBoolean(object, Reflector.Event_isCanceled) || Reflector.call(object, Reflector.PlaySoundAtEntityEvent_getSound) == null)
            {
                return;
            }

            eventIn = (SoundEvent)Reflector.call(object, Reflector.PlaySoundAtEntityEvent_getSound);
            categoryIn = (SoundCategory)Reflector.call(object, Reflector.PlaySoundAtEntityEvent_getCategory);
            volume = Reflector.callFloat(object, Reflector.PlaySoundAtEntityEvent_getVolume);
        }

        if (playerIn == this.mc.player)
        {
            this.mc.getSoundHandler().play(new EntityTickableSound(eventIn, categoryIn, entityIn));
        }
    }

    public void playSound(BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay)
    {
        this.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, soundIn, category, volume, pitch, distanceDelay);
    }

    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay)
    {
        double d0 = this.mc.gameRenderer.getActiveRenderInfo().getProjectedView().squareDistanceTo(x, y, z);
        SimpleSound simplesound = new SimpleSound(soundIn, category, volume, pitch, x, y, z);

        if (distanceDelay && d0 > 100.0D)
        {
            double d1 = Math.sqrt(d0) / 40.0D;
            this.mc.getSoundHandler().playDelayed(simplesound, (int)(d1 * 20.0D));
        }
        else
        {
            this.mc.getSoundHandler().play(simplesound);
        }
    }

    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable CompoundNBT compound)
    {
        this.mc.particles.addEffect(new FireworkParticle.Starter(this, x, y, z, motionX, motionY, motionZ, this.mc.particles, compound));
    }

    public void sendPacketToServer(IPacket<?> packetIn)
    {
        this.connection.sendPacket(packetIn);
    }

    public RecipeManager getRecipeManager()
    {
        return this.connection.getRecipeManager();
    }

    public void setScoreboard(Scoreboard scoreboardIn)
    {
        this.scoreboard = scoreboardIn;
    }

    public ITickList<Block> getPendingBlockTicks()
    {
        return EmptyTickList.get();
    }

    public ITickList<Fluid> getPendingFluidTicks()
    {
        return EmptyTickList.get();
    }

    /**
     * Gets the world's chunk provider
     */
    public ClientChunkProvider getChunkProvider()
    {
        return this.field_239129_E_;
    }

    /**
     * Sets a block state into this world.Flags are as follows:
     * 1 will cause a block update.
     * 2 will send the change to clients.
     * 4 will prevent the block from being re-rendered.
     * 8 will force any re-renders to run on the main thread instead
     * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
     * 32 will prevent neighbor reactions from spawning drops.
     * 64 will signify the block is being moved.
     * Flags can be OR-ed
     */
    public boolean setBlockState(BlockPos pos, BlockState newState, int flags)
    {
        this.playerUpdate = this.isPlayerActing();
        boolean flag = super.setBlockState(pos, newState, flags);
        this.playerUpdate = false;
        return flag;
    }

    private boolean isPlayerActing()
    {
        if (this.mc.playerController instanceof PlayerControllerOF)
        {
            PlayerControllerOF playercontrollerof = (PlayerControllerOF)this.mc.playerController;
            return playercontrollerof.isActing();
        }
        else
        {
            return false;
        }
    }

    public boolean isPlayerUpdate()
    {
        return this.playerUpdate;
    }

    public void onEntityAdded(Entity p_onEntityAdded_1_)
    {
        RandomEntities.entityLoaded(p_onEntityAdded_1_, this);

        if (Config.isDynamicLights())
        {
            DynamicLights.entityAdded(p_onEntityAdded_1_, Config.getRenderGlobal());
        }
    }

    public void onEntityRemoved(Entity p_onEntityRemoved_1_)
    {
        RandomEntities.entityUnloaded(p_onEntityRemoved_1_, this);

        if (Config.isDynamicLights())
        {
            DynamicLights.entityRemoved(p_onEntityRemoved_1_, Config.getRenderGlobal());
        }
    }

    @Nullable
    public MapData getMapData(String mapName)
    {
        return this.maps.get(mapName);
    }

    public void registerMapData(MapData mapDataIn)
    {
        this.maps.put(mapDataIn.getName(), mapDataIn);
    }

    public int getNextMapId()
    {
        return 0;
    }

    public Scoreboard getScoreboard()
    {
        return this.scoreboard;
    }

    public ITagCollectionSupplier getTags()
    {
        return this.connection.getTags();
    }

    public DynamicRegistries func_241828_r()
    {
        return this.connection.func_239165_n_();
    }

    /**
     * Flags are as in setBlockState
     */
    public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags)
    {
        this.worldRenderer.notifyBlockUpdate(this, pos, oldState, newState, flags);
    }

    public void markBlockRangeForRenderUpdate(BlockPos blockPosIn, BlockState oldState, BlockState newState)
    {
        this.worldRenderer.markBlockRangeForRenderUpdate(blockPosIn, oldState, newState);
    }

    public void markSurroundingsForRerender(int sectionX, int sectionY, int sectionZ)
    {
        this.worldRenderer.markSurroundingsForRerender(sectionX, sectionY, sectionZ);
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        this.worldRenderer.sendBlockBreakProgress(breakerId, pos, progress);
    }

    public void playBroadcastSound(int id, BlockPos pos, int data)
    {
        this.worldRenderer.broadcastSound(id, pos, data);
    }

    public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data)
    {
        try
        {
            this.worldRenderer.playEvent(player, type, pos, data);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Playing level event");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
            crashreportcategory.addDetail("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
            crashreportcategory.addDetail("Event source", player);
            crashreportcategory.addDetail("Event type", type);
            crashreportcategory.addDetail("Event data", data);
            throw new ReportedException(crashreport);
        }
    }

    public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        this.worldRenderer.addParticle(particleData, particleData.getType().getAlwaysShow(), x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void addParticle(IParticleData particleData, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        this.worldRenderer.addParticle(particleData, particleData.getType().getAlwaysShow() || forceAlwaysRender, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void addOptionalParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        this.worldRenderer.addParticle(particleData, false, true, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void addOptionalParticle(IParticleData particleData, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        this.worldRenderer.addParticle(particleData, particleData.getType().getAlwaysShow() || ignoreRange, true, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public List<AbstractClientPlayerEntity> getPlayers()
    {
        return this.players;
    }

    public Biome getNoiseBiomeRaw(int x, int y, int z)
    {
        return this.func_241828_r().getRegistry(Registry.BIOME_KEY).getOrThrow(Biomes.PLAINS);
    }

    public float getSunBrightness(float partialTicks)
    {
        float f = this.func_242415_f(partialTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(partialTicks) * 5.0F) / 16.0D));
        f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(partialTicks) * 5.0F) / 16.0D));
        return f1 * 0.8F + 0.2F;
    }

    public Vector3d getSkyColor(BlockPos blockPosIn, float partialTicks)
    {
        float f = this.func_242415_f(partialTicks);
        float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        Biome biome = this.getBiome(blockPosIn);
        int i = biome.getSkyColor();
        float f2 = (float)(i >> 16 & 255) / 255.0F;
        float f3 = (float)(i >> 8 & 255) / 255.0F;
        float f4 = (float)(i & 255) / 255.0F;
        f2 = f2 * f1;
        f3 = f3 * f1;
        f4 = f4 * f1;
        float f5 = this.getRainStrength(partialTicks);

        if (f5 > 0.0F)
        {
            float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
            float f7 = 1.0F - f5 * 0.75F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }

        float f9 = this.getThunderStrength(partialTicks);

        if (f9 > 0.0F)
        {
            float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f8 = 1.0F - f9 * 0.75F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }

        if (this.timeLightningFlash > 0)
        {
            float f11 = (float)this.timeLightningFlash - partialTicks;

            if (f11 > 1.0F)
            {
                f11 = 1.0F;
            }

            f11 = f11 * 0.45F;
            f2 = f2 * (1.0F - f11) + 0.8F * f11;
            f3 = f3 * (1.0F - f11) + 0.8F * f11;
            f4 = f4 * (1.0F - f11) + 1.0F * f11;
        }

        return new Vector3d((double)f2, (double)f3, (double)f4);
    }

    public Vector3d getCloudColor(float partialTicks)
    {
        float f = this.func_242415_f(partialTicks);
        float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 1.0F;
        float f5 = this.getRainStrength(partialTicks);

        if (f5 > 0.0F)
        {
            float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
            float f7 = 1.0F - f5 * 0.95F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }

        f2 = f2 * (f1 * 0.9F + 0.1F);
        f3 = f3 * (f1 * 0.9F + 0.1F);
        f4 = f4 * (f1 * 0.85F + 0.15F);
        float f9 = this.getThunderStrength(partialTicks);

        if (f9 > 0.0F)
        {
            float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f8 = 1.0F - f9 * 0.95F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }

        return new Vector3d((double)f2, (double)f3, (double)f4);
    }

    public float getStarBrightness(float partialTicks)
    {
        float f = this.func_242415_f(partialTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        return f1 * f1 * 0.5F;
    }

    public int getTimeLightningFlash()
    {
        return this.timeLightningFlash;
    }

    public void setTimeLightningFlash(int timeFlashIn)
    {
        this.timeLightningFlash = timeFlashIn;
    }

    public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_)
    {
        boolean flag = this.func_239132_a_().func_239217_c_();
        boolean flag1 = Config.isShaders();

        if (!p_230487_2_)
        {
            return flag ? 0.9F : 1.0F;
        }
        else
        {
            switch (p_230487_1_)
            {
                case DOWN:
                    return flag ? 0.9F : (flag1 ? Shaders.blockLightLevel05 : 0.5F);

                case UP:
                    return flag ? 0.9F : 1.0F;

                case NORTH:
                case SOUTH:
                    if (Config.isShaders())
                    {
                        return Shaders.blockLightLevel08;
                    }

                    return 0.8F;

                case WEST:
                case EAST:
                    if (Config.isShaders())
                    {
                        return Shaders.blockLightLevel06;
                    }

                    return 0.6F;

                default:
                    return 1.0F;
            }
        }
    }

    public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn)
    {
        ColorCache colorcache = this.colorCaches.get(colorResolverIn);
        return colorcache.getColor(blockPosIn, () ->
        {
            return this.getBlockColorRaw(blockPosIn, colorResolverIn);
        });
    }

    public int getBlockColorRaw(BlockPos blockPosIn, ColorResolver colorResolverIn)
    {
        int i = Minecraft.getInstance().gameSettings.biomeBlendRadius;

        if (i == 0)
        {
            return colorResolverIn.getColor(this.getBiome(blockPosIn), (double)blockPosIn.getX(), (double)blockPosIn.getZ());
        }
        else
        {
            int j = (i * 2 + 1) * (i * 2 + 1);
            int k = 0;
            int l = 0;
            int i1 = 0;
            CubeCoordinateIterator cubecoordinateiterator = new CubeCoordinateIterator(blockPosIn.getX() - i, blockPosIn.getY(), blockPosIn.getZ() - i, blockPosIn.getX() + i, blockPosIn.getY(), blockPosIn.getZ() + i);
            int j1;

            for (BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(); cubecoordinateiterator.hasNext(); i1 += j1 & 255)
            {
                blockpos$mutable.setPos(cubecoordinateiterator.getX(), cubecoordinateiterator.getY(), cubecoordinateiterator.getZ());
                j1 = colorResolverIn.getColor(this.getBiome(blockpos$mutable), (double)blockpos$mutable.getX(), (double)blockpos$mutable.getZ());
                k += (j1 & 16711680) >> 16;
                l += (j1 & 65280) >> 8;
            }

            return (k / j & 255) << 16 | (l / j & 255) << 8 | i1 / j & 255;
        }
    }

    public BlockPos func_239140_u_()
    {
        BlockPos blockpos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());

        if (!this.getWorldBorder().contains(blockpos))
        {
            blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
        }

        return blockpos;
    }

    public float func_243489_v()
    {
        return this.worldInfo.getSpawnAngle();
    }

    public void func_239136_a_(BlockPos p_239136_1_, float p_239136_2_)
    {
        this.worldInfo.setSpawn(p_239136_1_, p_239136_2_);
    }

    public String toString()
    {
        return "ClientLevel";
    }

    /**
     * Returns the world's WorldInfo object
     */
    public ClientWorld.ClientWorldInfo getWorldInfo()
    {
        return this.field_239130_d_;
    }

    public static class ClientWorldInfo implements ISpawnWorldInfo
    {
        private final boolean hardcore;
        private final GameRules gameRules;
        private final boolean flatWorld;
        private int spawnX;
        private int spawnY;
        private int spawnZ;
        private float field_243490_g;
        private long gameTime;
        private long dayTime;
        private boolean raining;
        private Difficulty difficulty;
        private boolean field_239154_k_;

        public ClientWorldInfo(Difficulty p_i232338_1_, boolean p_i232338_2_, boolean flatWorld)
        {
            this.difficulty = p_i232338_1_;
            this.hardcore = p_i232338_2_;
            this.flatWorld = flatWorld;
            this.gameRules = new GameRules();
        }

        public int getSpawnX()
        {
            return this.spawnX;
        }

        public int getSpawnY()
        {
            return this.spawnY;
        }

        public int getSpawnZ()
        {
            return this.spawnZ;
        }

        public float getSpawnAngle()
        {
            return this.field_243490_g;
        }

        public long getGameTime()
        {
            return this.gameTime;
        }

        public long getDayTime()
        {
            return this.dayTime;
        }

        public void setSpawnX(int x)
        {
            this.spawnX = x;
        }

        public void setSpawnY(int y)
        {
            this.spawnY = y;
        }

        public void setSpawnZ(int z)
        {
            this.spawnZ = z;
        }

        public void setSpawnAngle(float angle)
        {
            this.field_243490_g = angle;
        }

        public void setGameTime(long time)
        {
            this.gameTime = time;
        }

        public void setDayTime(long time)
        {
            this.dayTime = time;
        }

        public void setSpawn(BlockPos spawnPoint, float angle)
        {
            this.spawnX = spawnPoint.getX();
            this.spawnY = spawnPoint.getY();
            this.spawnZ = spawnPoint.getZ();
            this.field_243490_g = angle;
        }

        public boolean isThundering()
        {
            return false;
        }

        public boolean isRaining()
        {
            return this.raining;
        }

        public void setRaining(boolean isRaining)
        {
            this.raining = isRaining;
        }

        public boolean isHardcore()
        {
            return this.hardcore;
        }

        public GameRules getGameRulesInstance()
        {
            return this.gameRules;
        }

        public Difficulty getDifficulty()
        {
            return this.difficulty;
        }

        public boolean isDifficultyLocked()
        {
            return this.field_239154_k_;
        }

        public void addToCrashReport(CrashReportCategory category)
        {
            ISpawnWorldInfo.super.addToCrashReport(category);
        }

        public void setDifficulty(Difficulty difficulty)
        {
            Reflector.ForgeHooks_onDifficultyChange.callVoid(difficulty, this.difficulty);
            this.difficulty = difficulty;
        }

        public void setDifficultyLocked(boolean difficultyLocked)
        {
            this.field_239154_k_ = difficultyLocked;
        }

        public double getVoidFogHeight()
        {
            return this.flatWorld ? 0.0D : 63.0D;
        }

        public double getFogDistance()
        {
            return this.flatWorld ? 1.0D : 0.03125D;
        }
    }
}
