package net.minecraft.world.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.util.SharedConstants;

public class VersionData
{
    private final int storageVersion;
    private final long lastPlayed;
    private final String name;
    private final int id;
    private final boolean snapshot;

    public VersionData(int storageVersion, long lastPlayed, String name, int id, boolean snapshot)
    {
        this.storageVersion = storageVersion;
        this.lastPlayed = lastPlayed;
        this.name = name;
        this.id = id;
        this.snapshot = snapshot;
    }

    public static VersionData getVersionData(Dynamic<?> nbt)
    {
        int i = nbt.get("version").asInt(0);
        long j = nbt.get("LastPlayed").asLong(0L);
        OptionalDynamic<?> optionaldynamic = nbt.get("Version");
        return optionaldynamic.result().isPresent() ? new VersionData(i, j, optionaldynamic.get("Name").asString(SharedConstants.getVersion().getName()), optionaldynamic.get("Id").asInt(SharedConstants.getVersion().getWorldVersion()), optionaldynamic.get("Snapshot").asBoolean(!SharedConstants.getVersion().isStable())) : new VersionData(i, j, "", 0, false);
    }

    public int getStorageVersionID()
    {
        return this.storageVersion;
    }

    public long getLastPlayed()
    {
        return this.lastPlayed;
    }

    public String getName()
    {
        return this.name;
    }

    public int getID()
    {
        return this.id;
    }

    public boolean isSnapshot()
    {
        return this.snapshot;
    }
}
