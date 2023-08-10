package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.VanillaPack;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadingPackFinder implements IPackFinder
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern PATTERN_SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final VanillaPack vanillaPack;
    private final File serverPackDir;
    private final ReentrantLock lockDownload = new ReentrantLock();
    private final ResourceIndex resourceIndex;
    @Nullable
    private CompletableFuture<?> currentDownload;
    @Nullable
    private ResourcePackInfo serverPack;

    public DownloadingPackFinder(File serverPackDirIn, ResourceIndex resourceIndexIn)
    {
        this.serverPackDir = serverPackDirIn;
        this.resourceIndex = resourceIndexIn;
        this.vanillaPack = new VirtualAssetsPack(resourceIndexIn);
    }

    public void findPacks(Consumer<ResourcePackInfo> infoConsumer, ResourcePackInfo.IFactory infoFactory)
    {
        ResourcePackInfo resourcepackinfo = ResourcePackInfo.createResourcePack("vanilla", true, () ->
        {
            return this.vanillaPack;
        }, infoFactory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.BUILTIN);

        if (resourcepackinfo != null)
        {
            infoConsumer.accept(resourcepackinfo);
        }

        if (this.serverPack != null)
        {
            infoConsumer.accept(this.serverPack);
        }

        ResourcePackInfo resourcepackinfo1 = this.func_239453_a_(infoFactory);

        if (resourcepackinfo1 != null)
        {
            infoConsumer.accept(resourcepackinfo1);
        }
    }

    public VanillaPack getVanillaPack()
    {
        return this.vanillaPack;
    }

    private static Map<String, String> getPackDownloadRequestProperties()
    {
        Map<String, String> map = Maps.newHashMap();
        map.put("X-Minecraft-Username", Minecraft.getInstance().getSession().getUsername());
        map.put("X-Minecraft-UUID", Minecraft.getInstance().getSession().getPlayerID());
        map.put("X-Minecraft-Version", SharedConstants.getVersion().getName());
        map.put("X-Minecraft-Version-ID", SharedConstants.getVersion().getId());
        map.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getVersion().getPackVersion()));
        map.put("User-Agent", "Minecraft Java/" + SharedConstants.getVersion().getName());
        return map;
    }

    public CompletableFuture<?> downloadResourcePack(String url, String hash)
    {
        String s = DigestUtils.sha1Hex(url);
        String s1 = PATTERN_SHA1.matcher(hash).matches() ? hash : "";
        this.lockDownload.lock();
        CompletableFuture completablefuture1;

        try
        {
            this.clearResourcePack();
            this.clearDownloads();
            File file1 = new File(this.serverPackDir, s);
            CompletableFuture<?> completablefuture;

            if (file1.exists())
            {
                completablefuture = CompletableFuture.completedFuture("");
            }
            else
            {
                WorkingScreen workingscreen = new WorkingScreen();
                Map<String, String> map = getPackDownloadRequestProperties();
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.runImmediately(() ->
                {
                    minecraft.displayGuiScreen(workingscreen);
                });
                completablefuture = HTTPUtil.downloadResourcePack(file1, url, map, 104857600, workingscreen, minecraft.getProxy());
            }

            this.currentDownload = completablefuture.thenCompose((p_217812_3_) ->
            {
                return !this.checkHash(s1, file1) ? Util.completedExceptionallyFuture(new RuntimeException("Hash check failure for file " + file1 + ", see log")) : this.setServerPack(file1, IPackNameDecorator.SERVER);
            }).whenComplete((p_217815_1_, p_217815_2_) ->
            {
                if (p_217815_2_ != null)
                {
                    LOGGER.warn("Pack application failed: {}, deleting file {}", p_217815_2_.getMessage(), file1);
                    deleteQuiet(file1);
                }
            });
            completablefuture1 = this.currentDownload;
        }
        finally
        {
            this.lockDownload.unlock();
        }

        return completablefuture1;
    }

    private static void deleteQuiet(File fileIn)
    {
        try
        {
            Files.delete(fileIn.toPath());
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Failed to delete file {}: {}", fileIn, ioexception.getMessage());
        }
    }

    public void clearResourcePack()
    {
        this.lockDownload.lock();

        try
        {
            if (this.currentDownload != null)
            {
                this.currentDownload.cancel(true);
            }

            this.currentDownload = null;

            if (this.serverPack != null)
            {
                this.serverPack = null;
                Minecraft.getInstance().scheduleResourcesRefresh();
            }
        }
        finally
        {
            this.lockDownload.unlock();
        }
    }

    private boolean checkHash(String expectedHash, File fileIn)
    {
        try (FileInputStream fileinputstream = new FileInputStream(fileIn))
        {
            String s = DigestUtils.sha1Hex((InputStream)fileinputstream);

            if (expectedHash.isEmpty())
            {
                LOGGER.info("Found file {} without verification hash", (Object)fileIn);
                return true;
            }

            if (s.toLowerCase(Locale.ROOT).equals(expectedHash.toLowerCase(Locale.ROOT)))
            {
                LOGGER.info("Found file {} matching requested hash {}", fileIn, expectedHash);
                return true;
            }

            LOGGER.warn("File {} had wrong hash (expected {}, found {}).", fileIn, expectedHash, s);
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("File {} couldn't be hashed.", fileIn, ioexception);
        }

        return false;
    }

    private void clearDownloads()
    {
        try
        {
            List<File> list = Lists.newArrayList(FileUtils.listFiles(this.serverPackDir, TrueFileFilter.TRUE, (IOFileFilter)null));
            list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            int i = 0;

            for (File file1 : list)
            {
                if (i++ >= 10)
                {
                    LOGGER.info("Deleting old server resource pack {}", (Object)file1.getName());
                    FileUtils.deleteQuietly(file1);
                }
            }
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            LOGGER.error("Error while deleting old server resource pack : {}", (Object)illegalargumentexception.getMessage());
        }
    }

    public CompletableFuture<Void> setServerPack(File fileIn, IPackNameDecorator p_217816_2_)
    {
        PackMetadataSection packmetadatasection;

        try (FilePack filepack = new FilePack(fileIn))
        {
            packmetadatasection = filepack.getMetadata(PackMetadataSection.SERIALIZER);
        }
        catch (IOException ioexception)
        {
            return Util.completedExceptionallyFuture(new IOException(String.format("Invalid resourcepack at %s", fileIn), ioexception));
        }

        LOGGER.info("Applying server pack {}", (Object)fileIn);
        this.serverPack = new ResourcePackInfo("server", true, () ->
        {
            return new FilePack(fileIn);
        }, new TranslationTextComponent("resourcePack.server.name"), packmetadatasection.getDescription(), PackCompatibility.getCompatibility(packmetadatasection.getPackFormat()), ResourcePackInfo.Priority.TOP, true, p_217816_2_);
        return Minecraft.getInstance().scheduleResourcesRefresh();
    }

    @Nullable
    private ResourcePackInfo func_239453_a_(ResourcePackInfo.IFactory p_239453_1_)
    {
        ResourcePackInfo resourcepackinfo = null;
        File file1 = this.resourceIndex.getFile(new ResourceLocation("resourcepacks/programmer_art.zip"));

        if (file1 != null && file1.isFile())
        {
            resourcepackinfo = func_239454_a_(p_239453_1_, () ->
            {
                return func_239460_c_(file1);
            });
        }

        if (resourcepackinfo == null && SharedConstants.developmentMode)
        {
            File file2 = this.resourceIndex.getFile("../resourcepacks/programmer_art");

            if (file2 != null && file2.isDirectory())
            {
                resourcepackinfo = func_239454_a_(p_239453_1_, () ->
                {
                    return func_239459_b_(file2);
                });
            }
        }

        return resourcepackinfo;
    }

    @Nullable
    private static ResourcePackInfo func_239454_a_(ResourcePackInfo.IFactory p_239454_0_, Supplier<IResourcePack> p_239454_1_)
    {
        return ResourcePackInfo.createResourcePack("programer_art", false, p_239454_1_, p_239454_0_, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILTIN);
    }

    private static FolderPack func_239459_b_(File p_239459_0_)
    {
        return new FolderPack(p_239459_0_)
        {
            public String getName()
            {
                return "Programmer Art";
            }
        };
    }

    private static IResourcePack func_239460_c_(File p_239460_0_)
    {
        return new FilePack(p_239460_0_)
        {
            public String getName()
            {
                return "Programmer Art";
            }
        };
    }
}
