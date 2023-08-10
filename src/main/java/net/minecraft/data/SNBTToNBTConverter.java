package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SNBTToNBTConverter implements IDataProvider
{
    @Nullable
    private static final Path EMPTY = null;
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;
    private final List<SNBTToNBTConverter.ITransformer> transformers = Lists.newArrayList();

    public SNBTToNBTConverter(DataGenerator generatorIn)
    {
        this.generator = generatorIn;
    }

    public SNBTToNBTConverter addTransformer(SNBTToNBTConverter.ITransformer transformer)
    {
        this.transformers.add(transformer);
        return this;
    }

    private CompoundNBT snbtToNBT(String fileName, CompoundNBT nbt)
    {
        CompoundNBT compoundnbt = nbt;

        for (SNBTToNBTConverter.ITransformer snbttonbtconverter$itransformer : this.transformers)
        {
            compoundnbt = snbttonbtconverter$itransformer.func_225371_a(fileName, compoundnbt);
        }

        return compoundnbt;
    }

    /**
     * Performs this provider's action.
     */
    public void act(DirectoryCache cache) throws IOException
    {
        Path path = this.generator.getOutputFolder();
        List<CompletableFuture<SNBTToNBTConverter.TaskResult>> list = Lists.newArrayList();

        for (Path path1 : this.generator.getInputFolders())
        {
            Files.walk(path1).filter((snbtPath) ->
            {
                return snbtPath.toString().endsWith(".snbt");
            }).forEach((filePath) ->
            {
                list.add(CompletableFuture.supplyAsync(() -> {
                    return this.convertSNBTToNBT(filePath, this.getFileName(path1, filePath));
                }, Util.getServerExecutor()));
            });
        }

        Util.gather(list).join().stream().filter(Objects::nonNull).forEach((result) ->
        {
            this.writeStructureSNBT(cache, result, path);
        });
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    public String getName()
    {
        return "SNBT -> NBT";
    }

    /**
     * Gets the name of the given SNBT file, based on its path and the input directory. The result does not have the
     * ".snbt" extension.
     */
    private String getFileName(Path inputFolder, Path fileIn)
    {
        String s = inputFolder.relativize(fileIn).toString().replaceAll("\\\\", "/");
        return s.substring(0, s.length() - ".snbt".length());
    }

    @Nullable
    private SNBTToNBTConverter.TaskResult convertSNBTToNBT(Path filePath, String fileName)
    {
        try (BufferedReader bufferedreader = Files.newBufferedReader(filePath))
        {
            String s = IOUtils.toString((Reader)bufferedreader);
            CompoundNBT compoundnbt = this.snbtToNBT(fileName, JsonToNBT.getTagFromJson(s));
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(compoundnbt, bytearrayoutputstream);
            byte[] abyte = bytearrayoutputstream.toByteArray();
            String s1 = HASH_FUNCTION.hashBytes(abyte).toString();
            String s2;

            if (EMPTY != null)
            {
                s2 = compoundnbt.toFormattedComponent("    ", 0).getString() + "\n";
            }
            else
            {
                s2 = null;
            }

            return new SNBTToNBTConverter.TaskResult(fileName, abyte, s2, s1);
        }
        catch (CommandSyntaxException commandsyntaxexception)
        {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", fileName, filePath, commandsyntaxexception);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", fileName, filePath, ioexception);
        }

        return null;
    }

    private void writeStructureSNBT(DirectoryCache directory, SNBTToNBTConverter.TaskResult taskResult, Path pathIn)
    {
        if (taskResult.field_240515_c_ != null)
        {
            Path path = EMPTY.resolve(taskResult.fileName + ".snbt");

            try
            {
                FileUtils.write(path.toFile(), taskResult.field_240515_c_, StandardCharsets.UTF_8);
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Couldn't write structure SNBT {} at {}", taskResult.fileName, path, ioexception);
            }
        }

        Path path1 = pathIn.resolve(taskResult.fileName + ".nbt");

        try
        {
            if (!Objects.equals(directory.getPreviousHash(path1), taskResult.bytesHash) || !Files.exists(path1))
            {
                Files.createDirectories(path1.getParent());

                try (OutputStream outputstream = Files.newOutputStream(path1))
                {
                    outputstream.write(taskResult.nbtBytes);
                }
            }

            directory.recordHash(path1, taskResult.bytesHash);
        }
        catch (IOException ioexception1)
        {
            LOGGER.error("Couldn't write structure {} at {}", taskResult.fileName, path1, ioexception1);
        }
    }

    @FunctionalInterface
    public interface ITransformer
    {
        CompoundNBT func_225371_a(String p_225371_1_, CompoundNBT p_225371_2_);
    }

    static class TaskResult
    {
        private final String fileName;
        private final byte[] nbtBytes;
        @Nullable
        private final String field_240515_c_;
        private final String bytesHash;

        public TaskResult(String fileName, byte[] p_i232551_2_, @Nullable String p_i232551_3_, String bytesHash)
        {
            this.fileName = fileName;
            this.nbtBytes = p_i232551_2_;
            this.field_240515_c_ = p_i232551_3_;
            this.bytesHash = bytesHash;
        }
    }
}
