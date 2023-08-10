package net.minecraft.world.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.server.SessionLockManager;
import net.minecraft.util.FileUtil;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormat
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter BACKUP_DATE_FORMAT = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final ImmutableList<String> WORLD_GEN_SETTING_STRINGS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
    private final Path savesDir;
    private final Path backupsDir;
    private final DataFixer dataFixer;

    public SaveFormat(Path savesDir, Path backupsDir, DataFixer dataFixer)
    {
        this.dataFixer = dataFixer;

        try
        {
            Files.createDirectories(Files.exists(savesDir) ? savesDir.toRealPath() : savesDir);
        }
        catch (IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }

        this.savesDir = savesDir;
        this.backupsDir = backupsDir;
    }

    public static SaveFormat create(Path savesDir)
    {
        return new SaveFormat(savesDir, savesDir.resolve("../backups"), DataFixesManager.getDataFixer());
    }

    private static <T> Pair<DimensionGeneratorSettings, Lifecycle> getSettingLifecyclePair(Dynamic<T> nbt, DataFixer fixer, int version)
    {
        Dynamic<T> dynamic = nbt.get("WorldGenSettings").orElseEmptyMap();

        for (String s : WORLD_GEN_SETTING_STRINGS)
        {
            Optional <? extends Dynamic<? >> optional = nbt.get(s).result();

            if (optional.isPresent())
            {
                dynamic = dynamic.set(s, optional.get());
            }
        }

        Dynamic<T> dynamic1 = fixer.update(TypeReferences.WORLD_GEN_SETTINGS, dynamic, version, SharedConstants.getVersion().getWorldVersion());
        DataResult<DimensionGeneratorSettings> dataresult = DimensionGeneratorSettings.field_236201_a_.parse(dynamic1);
        return Pair.of(dataresult.resultOrPartial(Util.func_240982_a_("WorldGenSettings: ", LOGGER::error)).orElseGet(() ->
        {
            Registry<DimensionType> registry = RegistryLookupCodec.getLookUpCodec(Registry.DIMENSION_TYPE_KEY).codec().parse(dynamic1).resultOrPartial(Util.func_240982_a_("Dimension type registry: ", LOGGER::error)).orElseThrow(() -> {
                return new IllegalStateException("Failed to get dimension registry");
            });
            Registry<Biome> registry1 = RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).codec().parse(dynamic1).resultOrPartial(Util.func_240982_a_("Biome registry: ", LOGGER::error)).orElseThrow(() -> {
                return new IllegalStateException("Failed to get biome registry");
            });
            Registry<DimensionSettings> registry2 = RegistryLookupCodec.getLookUpCodec(Registry.NOISE_SETTINGS_KEY).codec().parse(dynamic1).resultOrPartial(Util.func_240982_a_("Noise settings registry: ", LOGGER::error)).orElseThrow(() -> {
                return new IllegalStateException("Failed to get noise settings registry");
            });
            return DimensionGeneratorSettings.func_242751_a(registry, registry1, registry2);
        }), dataresult.lifecycle());
    }

    private static DatapackCodec decodeDatapackCodec(Dynamic<?> nbt)
    {
        return DatapackCodec.CODEC.parse(nbt).resultOrPartial(LOGGER::error).orElse(DatapackCodec.VANILLA_CODEC);
    }

    public List<WorldSummary> getSaveList() throws AnvilConverterException
    {
        if (!Files.isDirectory(this.savesDir))
        {
            throw new AnvilConverterException((new TranslationTextComponent("selectWorld.load_folder_access")).getString());
        }
        else
        {
            List<WorldSummary> list = Lists.newArrayList();
            File[] afile = this.savesDir.toFile().listFiles();

            for (File file1 : afile)
            {
                if (file1.isDirectory())
                {
                    boolean flag;

                    try
                    {
                        flag = SessionLockManager.func_232999_b_(file1.toPath());
                    }
                    catch (Exception exception)
                    {
                        LOGGER.warn("Failed to read {} lock", file1, exception);
                        continue;
                    }

                    WorldSummary worldsummary = this.readFromLevelData(file1, this.readWorldSummary(file1, flag));

                    if (worldsummary != null)
                    {
                        list.add(worldsummary);
                    }
                }
            }

            return list;
        }
    }

    private int getStorageVersionId()
    {
        return 19133;
    }

    @Nullable
    private <T> T readFromLevelData(File saveDir, BiFunction<File, DataFixer, T> levelDatReader)
    {
        if (!saveDir.exists())
        {
            return (T)null;
        }
        else
        {
            File file1 = new File(saveDir, "level.dat");

            if (file1.exists())
            {
                T t = levelDatReader.apply(file1, this.dataFixer);

                if (t != null)
                {
                    return t;
                }
            }

            file1 = new File(saveDir, "level.dat_old");
            return (T)(file1.exists() ? levelDatReader.apply(file1, this.dataFixer) : null);
        }
    }

    @Nullable
    private static DatapackCodec readWorldDatapackCodec(File levelDat, DataFixer fixer)
    {
        try
        {
            CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(levelDat);
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
            compoundnbt1.remove("Player");
            int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
            Dynamic<INBT> dynamic = fixer.update(DefaultTypeReferences.LEVEL.getTypeReference(), new Dynamic<>(NBTDynamicOps.INSTANCE, compoundnbt1), i, SharedConstants.getVersion().getWorldVersion());
            return dynamic.get("DataPacks").result().map(SaveFormat::decodeDatapackCodec).orElse(DatapackCodec.VANILLA_CODEC);
        }
        catch (Exception exception)
        {
            LOGGER.error("Exception reading {}", levelDat, exception);
            return null;
        }
    }

    private static BiFunction<File, DataFixer, ServerWorldInfo> readServerWorldInfo(DynamicOps<INBT> nbt, DatapackCodec datapackCodec)
    {
        return (file, fixer) ->
        {
            try {
                CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(file);
                CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
                CompoundNBT compoundnbt2 = compoundnbt1.contains("Player", 10) ? compoundnbt1.getCompound("Player") : null;
                compoundnbt1.remove("Player");
                int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
                Dynamic<INBT> dynamic = fixer.update(DefaultTypeReferences.LEVEL.getTypeReference(), new Dynamic<>(nbt, compoundnbt1), i, SharedConstants.getVersion().getWorldVersion());
                Pair<DimensionGeneratorSettings, Lifecycle> pair = getSettingLifecyclePair(dynamic, fixer, i);
                VersionData versiondata = VersionData.getVersionData(dynamic);
                WorldSettings worldsettings = WorldSettings.decodeWorldSettings(dynamic, datapackCodec);
                return ServerWorldInfo.decodeWorldInfo(dynamic, fixer, i, compoundnbt2, worldsettings, versiondata, pair.getFirst(), pair.getSecond());
            }
            catch (Exception exception)
            {
                LOGGER.error("Exception reading {}", file, exception);
                return null;
            }
        };
    }

    private BiFunction<File, DataFixer, WorldSummary> readWorldSummary(File saveDir, boolean locked)
    {
        return (file, fixer) ->
        {
            try {
                CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(file);
                CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
                compoundnbt1.remove("Player");
                int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
                Dynamic<INBT> dynamic = fixer.update(DefaultTypeReferences.LEVEL.getTypeReference(), new Dynamic<>(NBTDynamicOps.INSTANCE, compoundnbt1), i, SharedConstants.getVersion().getWorldVersion());
                VersionData versiondata = VersionData.getVersionData(dynamic);
                int j = versiondata.getStorageVersionID();

                if (j != 19132 && j != 19133)
                {
                    return null;
                }
                else {
                    boolean flag = j != this.getStorageVersionId();
                    File file1 = new File(saveDir, "icon.png");
                    DatapackCodec datapackcodec = dynamic.get("DataPacks").result().map(SaveFormat::decodeDatapackCodec).orElse(DatapackCodec.VANILLA_CODEC);
                    WorldSettings worldsettings = WorldSettings.decodeWorldSettings(dynamic, datapackcodec);
                    return new WorldSummary(worldsettings, versiondata, saveDir.getName(), flag, locked, file1);
                }
            }
            catch (Exception exception)
            {
                LOGGER.error("Exception reading {}", file, exception);
                return null;
            }
        };
    }

    public boolean isNewLevelIdAcceptable(String saveName)
    {
        try
        {
            Path path = this.savesDir.resolve(saveName);
            Files.createDirectory(path);
            Files.deleteIfExists(path);
            return true;
        }
        catch (IOException ioexception)
        {
            return false;
        }
    }

    /**
     * Return whether the given world can be loaded.
     */
    public boolean canLoadWorld(String saveName)
    {
        return Files.isDirectory(this.savesDir.resolve(saveName));
    }

    public Path getSavesDir()
    {
        return this.savesDir;
    }

    /**
     * Gets the folder where backups are stored
     */
    public Path getBackupsFolder()
    {
        return this.backupsDir;
    }

    public SaveFormat.LevelSave getLevelSave(String saveName) throws IOException
    {
        return new SaveFormat.LevelSave(saveName);
    }

    public class LevelSave implements AutoCloseable
    {
        private final SessionLockManager saveDirLockManager;
        private final Path saveDir;
        private final String saveName;
        private final Map<FolderName, Path> localPathCache = Maps.newHashMap();

        public LevelSave(String saveName) throws IOException
        {
            this.saveName = saveName;
            this.saveDir = SaveFormat.this.savesDir.resolve(saveName);
            this.saveDirLockManager = SessionLockManager.func_232998_a_(this.saveDir);
        }

        public String getSaveName()
        {
            return this.saveName;
        }

        public Path resolveFilePath(FolderName folderName)
        {
            return this.localPathCache.computeIfAbsent(folderName, (folder) ->
            {
                return this.saveDir.resolve(folder.getFileName());
            });
        }

        public File getDimensionFolder(RegistryKey<World> dimensionKey)
        {
            return DimensionType.getDimensionFolder(dimensionKey, this.saveDir.toFile());
        }

        private void validateSaveDirLock()
        {
            if (!this.saveDirLockManager.func_232997_a_())
            {
                throw new IllegalStateException("Lock is no longer valid");
            }
        }

        public PlayerData getPlayerDataManager()
        {
            this.validateSaveDirLock();
            return new PlayerData(this, SaveFormat.this.dataFixer);
        }

        public boolean isSaveFormatOutdated()
        {
            WorldSummary worldsummary = this.readWorldSummary();
            return worldsummary != null && worldsummary.getVersionData().getStorageVersionID() != SaveFormat.this.getStorageVersionId();
        }

        public boolean convertRegions(IProgressUpdate progress)
        {
            this.validateSaveDirLock();
            return AnvilSaveConverter.convertRegions(this, progress);
        }

        @Nullable
        public WorldSummary readWorldSummary()
        {
            this.validateSaveDirLock();
            return SaveFormat.this.readFromLevelData(this.saveDir.toFile(), SaveFormat.this.readWorldSummary(this.saveDir.toFile(), false));
        }

        @Nullable
        public IServerConfiguration readServerConfiguration(DynamicOps<INBT> nbt, DatapackCodec datapackCodec)
        {
            this.validateSaveDirLock();
            return SaveFormat.this.readFromLevelData(this.saveDir.toFile(), SaveFormat.readServerWorldInfo(nbt, datapackCodec));
        }

        @Nullable
        public DatapackCodec readDatapackCodec()
        {
            this.validateSaveDirLock();
            return SaveFormat.this.readFromLevelData(this.saveDir.toFile(), (levelDatFile, dataFixer) ->
            {
                return SaveFormat.readWorldDatapackCodec(levelDatFile, dataFixer);
            });
        }

        public void saveLevel(DynamicRegistries registries, IServerConfiguration serverConfiguration)
        {
            this.saveLevel(registries, serverConfiguration, (CompoundNBT)null);
        }

        public void saveLevel(DynamicRegistries registries, IServerConfiguration serverConfiguration, @Nullable CompoundNBT hostPlayerNBT)
        {
            File file1 = this.saveDir.toFile();
            CompoundNBT compoundnbt = serverConfiguration.serialize(registries, hostPlayerNBT);
            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt1.put("Data", compoundnbt);

            try
            {
                File file2 = File.createTempFile("level", ".dat", file1);
                CompressedStreamTools.writeCompressed(compoundnbt1, file2);
                File file3 = new File(file1, "level.dat_old");
                File file4 = new File(file1, "level.dat");
                Util.backupThenUpdate(file4, file2, file3);
            }
            catch (Exception exception)
            {
                SaveFormat.LOGGER.error("Failed to save level {}", file1, exception);
            }
        }

        public File getIconFile()
        {
            this.validateSaveDirLock();
            return this.saveDir.resolve("icon.png").toFile();
        }

        public void deleteSave() throws IOException
        {
            this.validateSaveDirLock();
            final Path path = this.saveDir.resolve("session.lock");

            for (int i = 1; i <= 5; ++i)
            {
                SaveFormat.LOGGER.info("Attempt {}...", (int)i);

                try
                {
                    Files.walkFileTree(this.saveDir, new SimpleFileVisitor<Path>()
                    {
                        public FileVisitResult visitFile(Path p_visitFile_1_, BasicFileAttributes p_visitFile_2_) throws IOException
                        {
                            if (!p_visitFile_1_.equals(path))
                            {
                                SaveFormat.LOGGER.debug("Deleting {}", (Object)p_visitFile_1_);
                                Files.delete(p_visitFile_1_);
                            }

                            return FileVisitResult.CONTINUE;
                        }
                        public FileVisitResult postVisitDirectory(Path p_postVisitDirectory_1_, IOException p_postVisitDirectory_2_) throws IOException
                        {
                            if (p_postVisitDirectory_2_ != null)
                            {
                                throw p_postVisitDirectory_2_;
                            }
                            else
                            {
                                if (p_postVisitDirectory_1_.equals(LevelSave.this.saveDir))
                                {
                                    LevelSave.this.saveDirLockManager.close();
                                    Files.deleteIfExists(path);
                                }

                                Files.delete(p_postVisitDirectory_1_);
                                return FileVisitResult.CONTINUE;
                            }
                        }
                    });
                    break;
                }
                catch (IOException ioexception)
                {
                    if (i >= 5)
                    {
                        throw ioexception;
                    }

                    SaveFormat.LOGGER.warn("Failed to delete {}", this.saveDir, ioexception);

                    try
                    {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException interruptedexception)
                    {
                    }
                }
            }
        }

        public void updateSaveName(String saveName) throws IOException
        {
            this.validateSaveDirLock();
            File file1 = new File(SaveFormat.this.savesDir.toFile(), this.saveName);

            if (file1.exists())
            {
                File file2 = new File(file1, "level.dat");

                if (file2.exists())
                {
                    CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(file2);
                    CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
                    compoundnbt1.putString("LevelName", saveName);
                    CompressedStreamTools.writeCompressed(compoundnbt, file2);
                }
            }
        }

        public long createBackup() throws IOException
        {
            this.validateSaveDirLock();
            String s = LocalDateTime.now().format(SaveFormat.BACKUP_DATE_FORMAT) + "_" + this.saveName;
            Path path = SaveFormat.this.getBackupsFolder();

            try
            {
                Files.createDirectories(Files.exists(path) ? path.toRealPath() : path);
            }
            catch (IOException ioexception)
            {
                throw new RuntimeException(ioexception);
            }

            Path path1 = path.resolve(FileUtil.findAvailableName(path, s, ".zip"));

            try (final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path1))))
            {
                final Path path2 = Paths.get(this.saveName);
                Files.walkFileTree(this.saveDir, new SimpleFileVisitor<Path>()
                {
                    public FileVisitResult visitFile(Path p_visitFile_1_, BasicFileAttributes p_visitFile_2_) throws IOException
                    {
                        if (p_visitFile_1_.endsWith("session.lock"))
                        {
                            return FileVisitResult.CONTINUE;
                        }
                        else
                        {
                            String s1 = path2.resolve(LevelSave.this.saveDir.relativize(p_visitFile_1_)).toString().replace('\\', '/');
                            ZipEntry zipentry = new ZipEntry(s1);
                            zipoutputstream.putNextEntry(zipentry);
                            com.google.common.io.Files.asByteSource(p_visitFile_1_.toFile()).copyTo(zipoutputstream);
                            zipoutputstream.closeEntry();
                            return FileVisitResult.CONTINUE;
                        }
                    }
                });
            }

            return Files.size(path1);
        }

        public void close() throws IOException
        {
            this.saveDirLockManager.close();
        }
    }
}
