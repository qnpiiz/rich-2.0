package net.minecraft.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResourcePack extends AutoCloseable
{
    InputStream getRootResourceStream(String fileName) throws IOException;

    InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException;

    Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn);

    boolean resourceExists(ResourcePackType type, ResourceLocation location);

    Set<String> getResourceNamespaces(ResourcePackType type);

    @Nullable
    <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException;

    String getName();

    void close();
}
