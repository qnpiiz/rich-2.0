package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerData
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final File playerDataFolder;
    protected final DataFixer fixer;

    public PlayerData(SaveFormat.LevelSave levelSave, DataFixer fixer)
    {
        this.fixer = fixer;
        this.playerDataFolder = levelSave.resolveFilePath(FolderName.PLAYERDATA).toFile();
        this.playerDataFolder.mkdirs();
    }

    public void savePlayerData(PlayerEntity player)
    {
        try
        {
            CompoundNBT compoundnbt = player.writeWithoutTypeId(new CompoundNBT());
            File file1 = File.createTempFile(player.getCachedUniqueIdString() + "-", ".dat", this.playerDataFolder);
            CompressedStreamTools.writeCompressed(compoundnbt, file1);
            File file2 = new File(this.playerDataFolder, player.getCachedUniqueIdString() + ".dat");
            File file3 = new File(this.playerDataFolder, player.getCachedUniqueIdString() + ".dat_old");
            Util.backupThenUpdate(file2, file1, file3);
        }
        catch (Exception exception)
        {
            LOGGER.warn("Failed to save player data for {}", (Object)player.getName().getString());
        }
    }

    @Nullable
    public CompoundNBT loadPlayerData(PlayerEntity player)
    {
        CompoundNBT compoundnbt = null;

        try
        {
            File file1 = new File(this.playerDataFolder, player.getCachedUniqueIdString() + ".dat");

            if (file1.exists() && file1.isFile())
            {
                compoundnbt = CompressedStreamTools.readCompressed(file1);
            }
        }
        catch (Exception exception)
        {
            LOGGER.warn("Failed to load player data for {}", (Object)player.getName().getString());
        }

        if (compoundnbt != null)
        {
            int i = compoundnbt.contains("DataVersion", 3) ? compoundnbt.getInt("DataVersion") : -1;
            player.read(NBTUtil.update(this.fixer, DefaultTypeReferences.PLAYER, compoundnbt, i));
        }

        return compoundnbt;
    }

    public String[] getSeenPlayerUUIDs()
    {
        String[] astring = this.playerDataFolder.list();

        if (astring == null)
        {
            astring = new String[0];
        }

        for (int i = 0; i < astring.length; ++i)
        {
            if (astring[i].endsWith(".dat"))
            {
                astring[i] = astring[i].substring(0, astring[i].length() - 4);
            }
        }

        return astring;
    }
}
