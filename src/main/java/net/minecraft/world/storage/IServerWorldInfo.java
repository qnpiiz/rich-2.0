package net.minecraft.world.storage;

import java.util.UUID;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameType;
import net.minecraft.world.border.WorldBorder;

public interface IServerWorldInfo extends ISpawnWorldInfo
{
    /**
     * Get current world name
     */
    String getWorldName();

    /**
     * Sets whether it is thundering or not.
     */
    void setThundering(boolean thunderingIn);

    /**
     * Return the number of ticks until rain.
     */
    int getRainTime();

    /**
     * Sets the number of ticks until rain.
     */
    void setRainTime(int time);

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    void setThunderTime(int time);

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    int getThunderTime();

default void addToCrashReport(CrashReportCategory category)
    {
        ISpawnWorldInfo.super.addToCrashReport(category);
        category.addDetail("Level name", this::getWorldName);
        category.addDetail("Level game mode", () ->
        {
            return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameType().getName(), this.getGameType().getID(), this.isHardcore(), this.areCommandsAllowed());
        });
        category.addDetail("Level weather", () ->
        {
            return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering());
        });
    }

    int getClearWeatherTime();

    void setClearWeatherTime(int time);

    int getWanderingTraderSpawnDelay();

    void setWanderingTraderSpawnDelay(int delay);

    int getWanderingTraderSpawnChance();

    void setWanderingTraderSpawnChance(int chance);

    void setWanderingTraderID(UUID id);

    /**
     * Gets the GameType.
     */
    GameType getGameType();

    void setWorldBorderSerializer(WorldBorder.Serializer serializer);

    WorldBorder.Serializer getWorldBorderSerializer();

    /**
     * Returns true if the World is initialized.
     */
    boolean isInitialized();

    /**
     * Sets the initialization status of the World.
     */
    void setInitialized(boolean initializedIn);

    /**
     * Returns true if commands are allowed on this World.
     */
    boolean areCommandsAllowed();

    void setGameType(GameType type);

    TimerCallbackManager<MinecraftServer> getScheduledEvents();

    void setGameTime(long time);

    /**
     * Set current world time
     */
    void setDayTime(long time);
}
