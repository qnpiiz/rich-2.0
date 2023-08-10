package net.minecraft.world.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.command.TimerCallbackSerializers;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorldInfo implements IServerWorldInfo, IServerConfiguration
{
    private static final Logger LOGGER = LogManager.getLogger();
    private WorldSettings worldSettings;
    private final DimensionGeneratorSettings generatorSettings;
    private final Lifecycle lifecycle;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private float spawnAngle;
    private long gameTime;
    private long dayTime;
    @Nullable
    private final DataFixer dataFixer;
    private final int version;
    private boolean dataFixed;
    @Nullable
    private CompoundNBT loadedPlayerNBT;
    private final int levelStorageVersion;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean initialized;
    private boolean difficultyLocked;
    private WorldBorder.Serializer borderSerializer;
    private CompoundNBT dragonFightNBT;
    @Nullable
    private CompoundNBT customBossEventNBT;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    @Nullable
    private UUID wanderingTraderID;
    private final Set<String> serverBrands;
    private boolean wasModded;
    private final TimerCallbackManager<MinecraftServer> schedueledEvents;

    private ServerWorldInfo(@Nullable DataFixer dataFixer, int version, @Nullable CompoundNBT loadedPlayerNBT, boolean wasModded, int spawnX, int spawnY, int spawnZ, float spawnAngle, long gameTime, long dayTime, int levelStorageVersion, int clearWeatherTime, int rainTime, boolean raining, int thunderTime, boolean thundering, boolean initialized, boolean difficultyLocked, WorldBorder.Serializer borderSerializer, int wanderingTraderSpawnDelay, int wanderingTraderSpawnChance, @Nullable UUID wanderingTraderID, LinkedHashSet<String> serverBrands, TimerCallbackManager<MinecraftServer> schedueledEvents, @Nullable CompoundNBT customBossEventNBT, CompoundNBT dragonFightNBT, WorldSettings worldSettings, DimensionGeneratorSettings generatorSettings, Lifecycle lifecycle)
    {
        this.dataFixer = dataFixer;
        this.wasModded = wasModded;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.spawnAngle = spawnAngle;
        this.gameTime = gameTime;
        this.dayTime = dayTime;
        this.levelStorageVersion = levelStorageVersion;
        this.clearWeatherTime = clearWeatherTime;
        this.rainTime = rainTime;
        this.raining = raining;
        this.thunderTime = thunderTime;
        this.thundering = thundering;
        this.initialized = initialized;
        this.difficultyLocked = difficultyLocked;
        this.borderSerializer = borderSerializer;
        this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
        this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
        this.wanderingTraderID = wanderingTraderID;
        this.serverBrands = serverBrands;
        this.loadedPlayerNBT = loadedPlayerNBT;
        this.version = version;
        this.schedueledEvents = schedueledEvents;
        this.customBossEventNBT = customBossEventNBT;
        this.dragonFightNBT = dragonFightNBT;
        this.worldSettings = worldSettings;
        this.generatorSettings = generatorSettings;
        this.lifecycle = lifecycle;
    }

    public ServerWorldInfo(WorldSettings worldSettings, DimensionGeneratorSettings generatorSettings, Lifecycle lifecycle)
    {
        this((DataFixer)null, SharedConstants.getVersion().getWorldVersion(), (CompoundNBT)null, false, 0, 0, 0, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SERIALIZER, 0, 0, (UUID)null, Sets.newLinkedHashSet(), new TimerCallbackManager<>(TimerCallbackSerializers.field_216342_a), (CompoundNBT)null, new CompoundNBT(), worldSettings.clone(), generatorSettings, lifecycle);
    }

    public static ServerWorldInfo decodeWorldInfo(Dynamic<INBT> dynamic, DataFixer dataFixer, int version, @Nullable CompoundNBT playerNBT, WorldSettings worldSettings, VersionData versionData, DimensionGeneratorSettings generatorSettings, Lifecycle lifecycle)
    {
        long i = dynamic.get("Time").asLong(0L);
        CompoundNBT compoundnbt = (CompoundNBT)dynamic.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() ->
        {
            return dynamic.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue();
        });
        return new ServerWorldInfo(dataFixer, version, playerNBT, dynamic.get("WasModded").asBoolean(false), dynamic.get("SpawnX").asInt(0), dynamic.get("SpawnY").asInt(0), dynamic.get("SpawnZ").asInt(0), dynamic.get("SpawnAngle").asFloat(0.0F), i, dynamic.get("DayTime").asLong(i), versionData.getStorageVersionID(), dynamic.get("clearWeatherTime").asInt(0), dynamic.get("rainTime").asInt(0), dynamic.get("raining").asBoolean(false), dynamic.get("thunderTime").asInt(0), dynamic.get("thundering").asBoolean(false), dynamic.get("initialized").asBoolean(true), dynamic.get("DifficultyLocked").asBoolean(false), WorldBorder.Serializer.deserialize(dynamic, WorldBorder.DEFAULT_SERIALIZER), dynamic.get("WanderingTraderSpawnDelay").asInt(0), dynamic.get("WanderingTraderSpawnChance").asInt(0), dynamic.get("WanderingTraderId").read(UUIDCodec.CODEC).result().orElse((UUID)null), dynamic.get("ServerBrands").asStream().flatMap((nbt) ->
        {
            return Util.streamOptional(nbt.asString().result());
        }).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new TimerCallbackManager<>(TimerCallbackSerializers.field_216342_a, dynamic.get("ScheduledEvents").asStream()), (CompoundNBT)dynamic.get("CustomBossEvents").orElseEmptyMap().getValue(), compoundnbt, worldSettings, generatorSettings, lifecycle);
    }

    public CompoundNBT serialize(DynamicRegistries registries, @Nullable CompoundNBT hostPlayerNBT)
    {
        this.updatePlayerData();

        if (hostPlayerNBT == null)
        {
            hostPlayerNBT = this.loadedPlayerNBT;
        }

        CompoundNBT compoundnbt = new CompoundNBT();
        this.serialize(registries, compoundnbt, hostPlayerNBT);
        return compoundnbt;
    }

    private void serialize(DynamicRegistries registry, CompoundNBT nbt, @Nullable CompoundNBT playerNBT)
    {
        ListNBT listnbt = new ListNBT();
        this.serverBrands.stream().map(StringNBT::valueOf).forEach(listnbt::add);
        nbt.put("ServerBrands", listnbt);
        nbt.putBoolean("WasModded", this.wasModded);
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("Name", SharedConstants.getVersion().getName());
        compoundnbt.putInt("Id", SharedConstants.getVersion().getWorldVersion());
        compoundnbt.putBoolean("Snapshot", !SharedConstants.getVersion().isStable());
        nbt.put("Version", compoundnbt);
        nbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
        WorldGenSettingsExport<INBT> worldgensettingsexport = WorldGenSettingsExport.create(NBTDynamicOps.INSTANCE, registry);
        DimensionGeneratorSettings.field_236201_a_.encodeStart(worldgensettingsexport, this.generatorSettings).resultOrPartial(Util.func_240982_a_("WorldGenSettings: ", LOGGER::error)).ifPresent((worldSettingsNBT) ->
        {
            nbt.put("WorldGenSettings", worldSettingsNBT);
        });
        nbt.putInt("GameType", this.worldSettings.getGameType().getID());
        nbt.putInt("SpawnX", this.spawnX);
        nbt.putInt("SpawnY", this.spawnY);
        nbt.putInt("SpawnZ", this.spawnZ);
        nbt.putFloat("SpawnAngle", this.spawnAngle);
        nbt.putLong("Time", this.gameTime);
        nbt.putLong("DayTime", this.dayTime);
        nbt.putLong("LastPlayed", Util.millisecondsSinceEpoch());
        nbt.putString("LevelName", this.worldSettings.getWorldName());
        nbt.putInt("version", 19133);
        nbt.putInt("clearWeatherTime", this.clearWeatherTime);
        nbt.putInt("rainTime", this.rainTime);
        nbt.putBoolean("raining", this.raining);
        nbt.putInt("thunderTime", this.thunderTime);
        nbt.putBoolean("thundering", this.thundering);
        nbt.putBoolean("hardcore", this.worldSettings.isHardcoreEnabled());
        nbt.putBoolean("allowCommands", this.worldSettings.isCommandsAllowed());
        nbt.putBoolean("initialized", this.initialized);
        this.borderSerializer.serialize(nbt);
        nbt.putByte("Difficulty", (byte)this.worldSettings.getDifficulty().getId());
        nbt.putBoolean("DifficultyLocked", this.difficultyLocked);
        nbt.put("GameRules", this.worldSettings.getGameRules().write());
        nbt.put("DragonFight", this.dragonFightNBT);

        if (playerNBT != null)
        {
            nbt.put("Player", playerNBT);
        }

        DatapackCodec.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.worldSettings.getDatapackCodec()).result().ifPresent((dataPacksNBT) ->
        {
            nbt.put("DataPacks", dataPacksNBT);
        });

        if (this.customBossEventNBT != null)
        {
            nbt.put("CustomBossEvents", this.customBossEventNBT);
        }

        nbt.put("ScheduledEvents", this.schedueledEvents.write());
        nbt.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        nbt.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);

        if (this.wanderingTraderID != null)
        {
            nbt.putUniqueId("WanderingTraderId", this.wanderingTraderID);
        }
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX()
    {
        return this.spawnX;
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY()
    {
        return this.spawnY;
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ()
    {
        return this.spawnZ;
    }

    public float getSpawnAngle()
    {
        return this.spawnAngle;
    }

    public long getGameTime()
    {
        return this.gameTime;
    }

    /**
     * Get current world time
     */
    public long getDayTime()
    {
        return this.dayTime;
    }

    private void updatePlayerData()
    {
        if (!this.dataFixed && this.loadedPlayerNBT != null)
        {
            if (this.version < SharedConstants.getVersion().getWorldVersion())
            {
                if (this.dataFixer == null)
                {
                    throw(NullPointerException)Util.pauseDevMode(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
                }

                this.loadedPlayerNBT = NBTUtil.update(this.dataFixer, DefaultTypeReferences.PLAYER, this.loadedPlayerNBT, this.version);
            }

            this.dataFixed = true;
        }
    }

    public CompoundNBT getHostPlayerNBT()
    {
        this.updatePlayerData();
        return this.loadedPlayerNBT;
    }

    /**
     * Set the x spawn position to the passed in value
     */
    public void setSpawnX(int x)
    {
        this.spawnX = x;
    }

    /**
     * Sets the y spawn position
     */
    public void setSpawnY(int y)
    {
        this.spawnY = y;
    }

    /**
     * Set the z spawn position to the passed in value
     */
    public void setSpawnZ(int z)
    {
        this.spawnZ = z;
    }

    public void setSpawnAngle(float angle)
    {
        this.spawnAngle = angle;
    }

    public void setGameTime(long time)
    {
        this.gameTime = time;
    }

    /**
     * Set current world time
     */
    public void setDayTime(long time)
    {
        this.dayTime = time;
    }

    public void setSpawn(BlockPos spawnPoint, float angle)
    {
        this.spawnX = spawnPoint.getX();
        this.spawnY = spawnPoint.getY();
        this.spawnZ = spawnPoint.getZ();
        this.spawnAngle = angle;
    }

    /**
     * Get current world name
     */
    public String getWorldName()
    {
        return this.worldSettings.getWorldName();
    }

    public int getStorageVersionId()
    {
        return this.levelStorageVersion;
    }

    public int getClearWeatherTime()
    {
        return this.clearWeatherTime;
    }

    public void setClearWeatherTime(int time)
    {
        this.clearWeatherTime = time;
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering()
    {
        return this.thundering;
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(boolean thunderingIn)
    {
        this.thundering = thunderingIn;
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime()
    {
        return this.thunderTime;
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(int time)
    {
        this.thunderTime = time;
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean isRaining()
    {
        return this.raining;
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setRaining(boolean isRaining)
    {
        this.raining = isRaining;
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime()
    {
        return this.rainTime;
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(int time)
    {
        this.rainTime = time;
    }

    /**
     * Gets the GameType.
     */
    public GameType getGameType()
    {
        return this.worldSettings.getGameType();
    }

    public void setGameType(GameType type)
    {
        this.worldSettings = this.worldSettings.setGameType(type);
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcore()
    {
        return this.worldSettings.isHardcoreEnabled();
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean areCommandsAllowed()
    {
        return this.worldSettings.isCommandsAllowed();
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized()
    {
        return this.initialized;
    }

    /**
     * Sets the initialization status of the World.
     */
    public void setInitialized(boolean initializedIn)
    {
        this.initialized = initializedIn;
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRulesInstance()
    {
        return this.worldSettings.getGameRules();
    }

    public WorldBorder.Serializer getWorldBorderSerializer()
    {
        return this.borderSerializer;
    }

    public void setWorldBorderSerializer(WorldBorder.Serializer serializer)
    {
        this.borderSerializer = serializer;
    }

    public Difficulty getDifficulty()
    {
        return this.worldSettings.getDifficulty();
    }

    public void setDifficulty(Difficulty difficulty)
    {
        this.worldSettings = this.worldSettings.setDifficulty(difficulty);
    }

    public boolean isDifficultyLocked()
    {
        return this.difficultyLocked;
    }

    public void setDifficultyLocked(boolean locked)
    {
        this.difficultyLocked = locked;
    }

    public TimerCallbackManager<MinecraftServer> getScheduledEvents()
    {
        return this.schedueledEvents;
    }

    /**
     * Adds this WorldInfo instance to the crash report.
     */
    public void addToCrashReport(CrashReportCategory category)
    {
        IServerWorldInfo.super.addToCrashReport(category);
        IServerConfiguration.super.addToCrashReport(category);
    }

    public DimensionGeneratorSettings getDimensionGeneratorSettings()
    {
        return this.generatorSettings;
    }

    public Lifecycle getLifecycle()
    {
        return this.lifecycle;
    }

    public CompoundNBT getDragonFightData()
    {
        return this.dragonFightNBT;
    }

    public void setDragonFightData(CompoundNBT nbt)
    {
        this.dragonFightNBT = nbt;
    }

    public DatapackCodec getDatapackCodec()
    {
        return this.worldSettings.getDatapackCodec();
    }

    public void setDatapackCodec(DatapackCodec codec)
    {
        this.worldSettings = this.worldSettings.setDatapackCodec(codec);
    }

    @Nullable
    public CompoundNBT getCustomBossEventData()
    {
        return this.customBossEventNBT;
    }

    public void setCustomBossEventData(@Nullable CompoundNBT nbt)
    {
        this.customBossEventNBT = nbt;
    }

    public int getWanderingTraderSpawnDelay()
    {
        return this.wanderingTraderSpawnDelay;
    }

    public void setWanderingTraderSpawnDelay(int delay)
    {
        this.wanderingTraderSpawnDelay = delay;
    }

    public int getWanderingTraderSpawnChance()
    {
        return this.wanderingTraderSpawnChance;
    }

    public void setWanderingTraderSpawnChance(int chance)
    {
        this.wanderingTraderSpawnChance = chance;
    }

    public void setWanderingTraderID(UUID id)
    {
        this.wanderingTraderID = id;
    }

    public void addServerBranding(String name, boolean isModded)
    {
        this.serverBrands.add(name);
        this.wasModded |= isModded;
    }

    public boolean isModded()
    {
        return this.wasModded;
    }

    public Set<String> getServerBranding()
    {
        return ImmutableSet.copyOf(this.serverBrands);
    }

    public IServerWorldInfo getServerWorldInfo()
    {
        return this;
    }

    public WorldSettings getWorldSettings()
    {
        return this.worldSettings.clone();
    }
}
