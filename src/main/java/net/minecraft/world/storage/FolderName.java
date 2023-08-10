package net.minecraft.world.storage;

public class FolderName
{
    public static final FolderName ADVANCEMENTS = new FolderName("advancements");
    public static final FolderName STATS = new FolderName("stats");
    public static final FolderName PLAYERDATA = new FolderName("playerdata");
    public static final FolderName PLAYERS = new FolderName("players");
    public static final FolderName LEVEL_DAT = new FolderName("level.dat");
    public static final FolderName GENERATED = new FolderName("generated");
    public static final FolderName DATAPACKS = new FolderName("datapacks");
    public static final FolderName RESOURCES_ZIP = new FolderName("resources.zip");
    public static final FolderName DOT = new FolderName(".");
    private final String fileName;

    private FolderName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public String toString()
    {
        return "/" + this.fileName;
    }
}
