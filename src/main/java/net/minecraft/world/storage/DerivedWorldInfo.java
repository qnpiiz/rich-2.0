package net.minecraft.world.storage;

import java.util.UUID;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.border.WorldBorder;

public class DerivedWorldInfo implements IServerWorldInfo
{
    private final IServerConfiguration configuration;
    private final IServerWorldInfo delegate;

    public DerivedWorldInfo(IServerConfiguration configuration, IServerWorldInfo delegate)
    {
        this.configuration = configuration;
        this.delegate = delegate;
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX()
    {
        return this.delegate.getSpawnX();
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY()
    {
        return this.delegate.getSpawnY();
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ()
    {
        return this.delegate.getSpawnZ();
    }

    public float getSpawnAngle()
    {
        return this.delegate.getSpawnAngle();
    }

    public long getGameTime()
    {
        return this.delegate.getGameTime();
    }

    /**
     * Get current world time
     */
    public long getDayTime()
    {
        return this.delegate.getDayTime();
    }

    /**
     * Get current world name
     */
    public String getWorldName()
    {
        return this.configuration.getWorldName();
    }

    public int getClearWeatherTime()
    {
        return this.delegate.getClearWeatherTime();
    }

    public void setClearWeatherTime(int time)
    {
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering()
    {
        return this.delegate.isThundering();
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime()
    {
        return this.delegate.getThunderTime();
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean isRaining()
    {
        return this.delegate.isRaining();
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime()
    {
        return this.delegate.getRainTime();
    }

    /**
     * Gets the GameType.
     */
    public GameType getGameType()
    {
        return this.configuration.getGameType();
    }

    /**
     * Set the x spawn position to the passed in value
     */
    public void setSpawnX(int x)
    {
    }

    /**
     * Sets the y spawn position
     */
    public void setSpawnY(int y)
    {
    }

    /**
     * Set the z spawn position to the passed in value
     */
    public void setSpawnZ(int z)
    {
    }

    public void setSpawnAngle(float angle)
    {
    }

    public void setGameTime(long time)
    {
    }

    /**
     * Set current world time
     */
    public void setDayTime(long time)
    {
    }

    public void setSpawn(BlockPos spawnPoint, float angle)
    {
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(boolean thunderingIn)
    {
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(int time)
    {
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setRaining(boolean isRaining)
    {
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(int time)
    {
    }

    public void setGameType(GameType type)
    {
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcore()
    {
        return this.configuration.isHardcore();
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean areCommandsAllowed()
    {
        return this.configuration.areCommandsAllowed();
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized()
    {
        return this.delegate.isInitialized();
    }

    /**
     * Sets the initialization status of the World.
     */
    public void setInitialized(boolean initializedIn)
    {
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRulesInstance()
    {
        return this.configuration.getGameRulesInstance();
    }

    public WorldBorder.Serializer getWorldBorderSerializer()
    {
        return this.delegate.getWorldBorderSerializer();
    }

    public void setWorldBorderSerializer(WorldBorder.Serializer serializer)
    {
    }

    public Difficulty getDifficulty()
    {
        return this.configuration.getDifficulty();
    }

    public boolean isDifficultyLocked()
    {
        return this.configuration.isDifficultyLocked();
    }

    public TimerCallbackManager<MinecraftServer> getScheduledEvents()
    {
        return this.delegate.getScheduledEvents();
    }

    public int getWanderingTraderSpawnDelay()
    {
        return 0;
    }

    public void setWanderingTraderSpawnDelay(int delay)
    {
    }

    public int getWanderingTraderSpawnChance()
    {
        return 0;
    }

    public void setWanderingTraderSpawnChance(int chance)
    {
    }

    public void setWanderingTraderID(UUID id)
    {
    }

    /**
     * Adds this WorldInfo instance to the crash report.
     */
    public void addToCrashReport(CrashReportCategory category)
    {
        category.addDetail("Derived", true);
        this.delegate.addToCrashReport(category);
    }
}
