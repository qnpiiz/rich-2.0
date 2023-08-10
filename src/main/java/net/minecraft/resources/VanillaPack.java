package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements IResourcePack
{
    public static Path basePath;
    private static final Logger LOGGER = LogManager.getLogger();
    public static Class<?> baseClass;
    private static final Map<ResourcePackType, FileSystem> FILE_SYSTEMS_BY_PACK_TYPE = Util.make(Maps.newHashMap(), (p_lambda$static$0_0_) ->
    {
        synchronized (VanillaPack.class)
        {
            for (ResourcePackType resourcepacktype : ResourcePackType.values())
            {
                URL url = VanillaPack.class.getResource("/" + resourcepacktype.getDirectoryName() + "/.mcassetsroot");

                try
                {
                    URI uri = url.toURI();

                    if ("jar".equals(uri.getScheme()))
                    {
                        FileSystem filesystem;

                        try
                        {
                            filesystem = FileSystems.getFileSystem(uri);
                        }
                        catch (FileSystemNotFoundException filesystemnotfoundexception)
                        {
                            filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                        }

                        p_lambda$static$0_0_.put(resourcepacktype, filesystem);
                    }
                }
                catch (URISyntaxException | IOException ioexception)
                {
                    LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)ioexception);
                }
            }
        }
    });
    public final Set<String> resourceNamespaces;
    private static final boolean ON_WINDOWS = Util.getOSType() == Util.OS.WINDOWS;
    private static final boolean FORGE = Reflector.ForgeHooksClient.exists();

    public VanillaPack(String... resourceNamespacesIn)
    {
        this.resourceNamespaces = ImmutableSet.copyOf(resourceNamespacesIn);
    }

    public InputStream getRootResourceStream(String fileName) throws IOException
    {
        if (!fileName.contains("/") && !fileName.contains("\\"))
        {
            if (basePath != null)
            {
                Path path = basePath.resolve(fileName);

                if (Files.exists(path))
                {
                    return Files.newInputStream(path);
                }
            }

            return this.getInputStreamVanilla(fileName);
        }
        else
        {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
    }

    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException
    {
        InputStream inputstream = this.getInputStreamVanilla(type, location);

        if (inputstream != null)
        {
            return inputstream;
        }
        else
        {
            throw new FileNotFoundException(location.getPath());
        }
    }

    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn)
    {
        Set<ResourceLocation> set = Sets.newHashSet();

        if (basePath != null)
        {
            try
            {
                collectResources(set, maxDepthIn, namespaceIn, basePath.resolve(type.getDirectoryName()), pathIn, filterIn);
            }
            catch (IOException ioexception3)
            {
            }

            if (type == ResourcePackType.CLIENT_RESOURCES)
            {
                Enumeration<URL> enumeration = null;

                try
                {
                    enumeration = baseClass.getClassLoader().getResources(type.getDirectoryName() + "/");
                }
                catch (IOException ioexception2)
                {
                }

                while (enumeration != null && enumeration.hasMoreElements())
                {
                    try
                    {
                        URI uri = enumeration.nextElement().toURI();

                        if ("file".equals(uri.getScheme()))
                        {
                            collectResources(set, maxDepthIn, namespaceIn, Paths.get(uri), pathIn, filterIn);
                        }
                    }
                    catch (URISyntaxException | IOException ioexception1)
                    {
                    }
                }
            }
        }

        try
        {
            URL url1 = VanillaPack.class.getResource("/" + type.getDirectoryName() + "/.mcassetsroot");

            if (url1 == null)
            {
                LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
                return set;
            }

            URI uri1 = url1.toURI();

            if ("file".equals(uri1.getScheme()))
            {
                URL url = new URL(url1.toString().substring(0, url1.toString().length() - ".mcassetsroot".length()));
                Path path = Paths.get(url.toURI());
                collectResources(set, maxDepthIn, namespaceIn, path, pathIn, filterIn);
            }
            else if ("jar".equals(uri1.getScheme()))
            {
                Path path1 = FILE_SYSTEMS_BY_PACK_TYPE.get(type).getPath("/" + type.getDirectoryName());
                collectResources(set, maxDepthIn, "minecraft", path1, pathIn, filterIn);
            }
            else
            {
                LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", (Object)uri1);
            }
        }
        catch (FileNotFoundException | NoSuchFileException nosuchfileexception)
        {
        }
        catch (URISyntaxException | IOException ioexception)
        {
            LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)ioexception);
        }

        return set;
    }

    private static void collectResources(Collection<ResourceLocation> resourceLocationsIn, int maxDepthIn, String namespaceIn, Path pathIn, String pathNameIn, Predicate<String> filterIn) throws IOException
    {
        Path path = pathIn.resolve(namespaceIn);

        try (Stream<Path> stream = Files.walk(path.resolve(pathNameIn), maxDepthIn))
        {
            stream.filter((p_lambda$collectResources$1_1_) ->
            {
                return !p_lambda$collectResources$1_1_.endsWith(".mcmeta") && Files.isRegularFile(p_lambda$collectResources$1_1_) && filterIn.test(p_lambda$collectResources$1_1_.getFileName().toString());
            }).map((p_lambda$collectResources$2_2_) ->
            {
                return new ResourceLocation(namespaceIn, path.relativize(p_lambda$collectResources$2_2_).toString().replaceAll("\\\\", "/"));
            }).forEach(resourceLocationsIn::add);
        }
    }

    @Nullable
    protected InputStream getInputStreamVanilla(ResourcePackType type, ResourceLocation location)
    {
        String s = getPath(type, location);
        InputStream inputstream = ReflectorForge.getOptiFineResourceStream(s);

        if (inputstream != null)
        {
            return inputstream;
        }
        else
        {
            if (basePath != null)
            {
                Path path = basePath.resolve(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath());

                if (Files.exists(path))
                {
                    try
                    {
                        return Files.newInputStream(path);
                    }
                    catch (IOException ioexception1)
                    {
                    }
                }
            }

            try
            {
                URL url = VanillaPack.class.getResource(s);
                return isValid(s, url) ? (FORGE ? this.getExtraInputStream(type, s) : url.openStream()) : null;
            }
            catch (IOException ioexception1)
            {
                return VanillaPack.class.getResourceAsStream(s);
            }
        }
    }

    private static String getPath(ResourcePackType packTypeIn, ResourceLocation locationIn)
    {
        return "/" + packTypeIn.getDirectoryName() + "/" + locationIn.getNamespace() + "/" + locationIn.getPath();
    }

    private static boolean isValid(String pathIn, @Nullable URL urlIn) throws IOException
    {
        return urlIn != null && (urlIn.getProtocol().equals("jar") || validatePath(new File(urlIn.getFile()), pathIn));
    }

    @Nullable
    protected InputStream getInputStreamVanilla(String pathIn)
    {
        return FORGE ? this.getExtraInputStream(ResourcePackType.SERVER_DATA, "/" + pathIn) : VanillaPack.class.getResourceAsStream("/" + pathIn);
    }

    public boolean resourceExists(ResourcePackType type, ResourceLocation location)
    {
        String s = getPath(type, location);
        InputStream inputstream = ReflectorForge.getOptiFineResourceStream(s);

        if (inputstream != null)
        {
            return true;
        }
        else
        {
            if (basePath != null)
            {
                Path path = basePath.resolve(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath());

                if (Files.exists(path))
                {
                    return true;
                }
            }

            try
            {
                URL url = VanillaPack.class.getResource(s);
                return isValid(s, url);
            }
            catch (IOException ioexception1)
            {
                return false;
            }
        }
    }

    public Set<String> getResourceNamespaces(ResourcePackType type)
    {
        return this.resourceNamespaces;
    }

    @Nullable
    public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException
    {
        try (InputStream inputstream = this.getRootResourceStream("pack.mcmeta"))
        {
            return ResourcePack.getResourceMetadata(deserializer, inputstream);
        }
        catch (RuntimeException | FileNotFoundException filenotfoundexception)
        {
            return (T)null;
        }
    }

    public String getName()
    {
        return "Default";
    }

    public void close()
    {
    }

    private static boolean validatePath(File p_validatePath_0_, String p_validatePath_1_) throws IOException
    {
        String s = p_validatePath_0_.getPath();

        if (s.startsWith("file:"))
        {
            if (ON_WINDOWS)
            {
                s = s.replace("\\", "/");
            }

            return s.endsWith(p_validatePath_1_);
        }
        else
        {
            return FolderPack.validatePath(p_validatePath_0_, p_validatePath_1_);
        }
    }

    private InputStream getExtraInputStream(ResourcePackType p_getExtraInputStream_1_, String p_getExtraInputStream_2_)
    {
        try
        {
            FileSystem filesystem = FILE_SYSTEMS_BY_PACK_TYPE.get(p_getExtraInputStream_1_);
            return filesystem != null ? Files.newInputStream(filesystem.getPath(p_getExtraInputStream_2_)) : VanillaPack.class.getResourceAsStream(p_getExtraInputStream_2_);
        }
        catch (IOException ioexception)
        {
            return VanillaPack.class.getResourceAsStream(p_getExtraInputStream_2_);
        }
    }
}
