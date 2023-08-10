package net.minecraft.world.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

public interface IServerConfiguration
{
    DatapackCodec getDatapackCodec();

    void setDatapackCodec(DatapackCodec codec);

    boolean isModded();

    Set<String> getServerBranding();

    void addServerBranding(String name, boolean isModded);

default void addToCrashReport(CrashReportCategory category)
    {
        category.addDetail("Known server brands", () ->
        {
            return String.join(", ", this.getServerBranding());
        });
        category.addDetail("Level was modded", () ->
        {
            return Boolean.toString(this.isModded());
        });
        category.addDetail("Level storage version", () ->
        {
            int i = this.getStorageVersionId();
            return String.format("0x%05X - %s", i, this.getStorageVersionName(i));
        });
    }

default String getStorageVersionName(int storageVersionId)
    {
        switch (storageVersionId)
        {
            case 19132:
                return "McRegion";

            case 19133:
                return "Anvil";

            default:
                return "Unknown?";
        }
    }

    @Nullable
    CompoundNBT getCustomBossEventData();

    void setCustomBossEventData(@Nullable CompoundNBT nbt);

    IServerWorldInfo getServerWorldInfo();

    WorldSettings getWorldSettings();

    CompoundNBT serialize(DynamicRegistries registries, @Nullable CompoundNBT hostPlayerNBT);

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    boolean isHardcore();

    int getStorageVersionId();

    /**
     * Get current world name
     */
    String getWorldName();

    /**
     * Gets the GameType.
     */
    GameType getGameType();

    void setGameType(GameType type);

    /**
     * Returns true if commands are allowed on this World.
     */
    boolean areCommandsAllowed();

    Difficulty getDifficulty();

    void setDifficulty(Difficulty difficulty);

    boolean isDifficultyLocked();

    void setDifficultyLocked(boolean locked);

    /**
     * Gets the GameRules class Instance.
     */
    GameRules getGameRulesInstance();

    CompoundNBT getHostPlayerNBT();

    CompoundNBT getDragonFightData();

    void setDragonFightData(CompoundNBT nbt);

    DimensionGeneratorSettings getDimensionGeneratorSettings();

    Lifecycle getLifecycle();
}
