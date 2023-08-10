package net.minecraft.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTToSNBTConverter implements IDataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;

    public NBTToSNBTConverter(DataGenerator generatorIn)
    {
        this.generator = generatorIn;
    }

    /**
     * Performs this provider's action.
     */
    public void act(DirectoryCache cache) throws IOException
    {
        Path path = this.generator.getOutputFolder();

        for (Path path1 : this.generator.getInputFolders())
        {
            Files.walk(path1).filter((path2) ->
            {
                return path2.toString().endsWith(".nbt");
            }).forEach((nbtPath) ->
            {
                convertNBTToSNBT(nbtPath, this.getFileName(path1, nbtPath), path);
            });
        }
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    public String getName()
    {
        return "NBT to SNBT";
    }

    /**
     * Gets the name of the given NBT file, based on its path and the input directory. The result does not have the
     * ".nbt" extension.
     */
    private String getFileName(Path inputFolder, Path fileIn)
    {
        String s = inputFolder.relativize(fileIn).toString().replaceAll("\\\\", "/");
        return s.substring(0, s.length() - ".nbt".length());
    }

    @Nullable
    public static Path convertNBTToSNBT(Path snbtPath, String name, Path nbtPath)
    {
        try
        {
            CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(Files.newInputStream(snbtPath));
            ITextComponent itextcomponent = compoundnbt.toFormattedComponent("    ", 0);
            String s = itextcomponent.getString() + "\n";
            Path path = nbtPath.resolve(name + ".snbt");
            Files.createDirectories(path.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path))
            {
                bufferedwriter.write(s);
            }

            LOGGER.info("Converted {} from NBT to SNBT", (Object)name);
            return path;
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", name, snbtPath, ioexception);
            return null;
        }
    }
}
