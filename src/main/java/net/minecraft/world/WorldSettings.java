package net.minecraft.world;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.codec.DatapackCodec;

public final class WorldSettings
{
    private final String worldName;
    private final GameType gameType;
    private final boolean hardcoreEnabled;
    private final Difficulty difficulty;
    private final boolean commandsAllowed;
    private final GameRules gameRules;
    private final DatapackCodec datapackCodec;

    public WorldSettings(String worldName, GameType gameType, boolean hardcoreEnabled, Difficulty difficulty, boolean commandsAllowed, GameRules gameRules, DatapackCodec datapackCodec)
    {
        this.worldName = worldName;
        this.gameType = gameType;
        this.hardcoreEnabled = hardcoreEnabled;
        this.difficulty = difficulty;
        this.commandsAllowed = commandsAllowed;
        this.gameRules = gameRules;
        this.datapackCodec = datapackCodec;
    }

    public static WorldSettings decodeWorldSettings(Dynamic<?> dynamic, DatapackCodec codec)
    {
        GameType gametype = GameType.getByID(dynamic.get("GameType").asInt(0));
        return new WorldSettings(dynamic.get("LevelName").asString(""), gametype, dynamic.get("hardcore").asBoolean(false), dynamic.get("Difficulty").asNumber().map((dimensionTypeID) ->
        {
            return Difficulty.byId(dimensionTypeID.byteValue());
        }).result().orElse(Difficulty.NORMAL), dynamic.get("allowCommands").asBoolean(gametype == GameType.CREATIVE), new GameRules(dynamic.get("GameRules")), codec);
    }

    public String getWorldName()
    {
        return this.worldName;
    }

    public GameType getGameType()
    {
        return this.gameType;
    }

    public boolean isHardcoreEnabled()
    {
        return this.hardcoreEnabled;
    }

    public Difficulty getDifficulty()
    {
        return this.difficulty;
    }

    public boolean isCommandsAllowed()
    {
        return this.commandsAllowed;
    }

    public GameRules getGameRules()
    {
        return this.gameRules;
    }

    public DatapackCodec getDatapackCodec()
    {
        return this.datapackCodec;
    }

    public WorldSettings setGameType(GameType gameType)
    {
        return new WorldSettings(this.worldName, gameType, this.hardcoreEnabled, this.difficulty, this.commandsAllowed, this.gameRules, this.datapackCodec);
    }

    public WorldSettings setDifficulty(Difficulty difficulty)
    {
        return new WorldSettings(this.worldName, this.gameType, this.hardcoreEnabled, difficulty, this.commandsAllowed, this.gameRules, this.datapackCodec);
    }

    public WorldSettings setDatapackCodec(DatapackCodec datapackCodec)
    {
        return new WorldSettings(this.worldName, this.gameType, this.hardcoreEnabled, this.difficulty, this.commandsAllowed, this.gameRules, datapackCodec);
    }

    public WorldSettings clone()
    {
        return new WorldSettings(this.worldName, this.gameType, this.hardcoreEnabled, this.difficulty, this.commandsAllowed, this.gameRules.clone(), this.datapackCodec);
    }
}
