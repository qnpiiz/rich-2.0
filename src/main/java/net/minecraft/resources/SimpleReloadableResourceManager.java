package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, FallbackResourceManager> namespaceResourceManagers = Maps.newHashMap();
    private final List<IFutureReloadListener> reloadListeners = Lists.newArrayList();
    private final List<IFutureReloadListener> initTaskQueue = Lists.newArrayList();
    private final Set<String> resourceNamespaces = Sets.newLinkedHashSet();
    private final List<IResourcePack> resourcePacks = Lists.newArrayList();
    private final ResourcePackType type;

    public SimpleReloadableResourceManager(ResourcePackType type)
    {
        this.type = type;
    }

    public void addResourcePack(IResourcePack resourcePack)
    {
        this.resourcePacks.add(resourcePack);

        for (String s : resourcePack.getResourceNamespaces(this.type))
        {
            this.resourceNamespaces.add(s);
            FallbackResourceManager fallbackresourcemanager = this.namespaceResourceManagers.get(s);

            if (fallbackresourcemanager == null)
            {
                fallbackresourcemanager = new FallbackResourceManager(this.type, s);
                this.namespaceResourceManagers.put(s, fallbackresourcemanager);
            }

            fallbackresourcemanager.addResourcePack(resourcePack);
        }
    }

    public Set<String> getResourceNamespaces()
    {
        return this.resourceNamespaces;
    }

    public IResource getResource(ResourceLocation resourceLocationIn) throws IOException
    {
        IResourceManager iresourcemanager = this.namespaceResourceManagers.get(resourceLocationIn.getNamespace());

        if (iresourcemanager != null)
        {
            return iresourcemanager.getResource(resourceLocationIn);
        }
        else
        {
            throw new FileNotFoundException(resourceLocationIn.toString());
        }
    }

    public boolean hasResource(ResourceLocation path)
    {
        IResourceManager iresourcemanager = this.namespaceResourceManagers.get(path.getNamespace());
        return iresourcemanager != null ? iresourcemanager.hasResource(path) : false;
    }

    public List<IResource> getAllResources(ResourceLocation resourceLocationIn) throws IOException
    {
        IResourceManager iresourcemanager = this.namespaceResourceManagers.get(resourceLocationIn.getNamespace());

        if (iresourcemanager != null)
        {
            return iresourcemanager.getAllResources(resourceLocationIn);
        }
        else
        {
            throw new FileNotFoundException(resourceLocationIn.toString());
        }
    }

    public Collection<ResourceLocation> getAllResourceLocations(String pathIn, Predicate<String> filter)
    {
        Set<ResourceLocation> set = Sets.newHashSet();

        for (FallbackResourceManager fallbackresourcemanager : this.namespaceResourceManagers.values())
        {
            set.addAll(fallbackresourcemanager.getAllResourceLocations(pathIn, filter));
        }

        List<ResourceLocation> list = Lists.newArrayList(set);
        Collections.sort(list);
        return list;
    }

    private void clearResourceNamespaces()
    {
        this.namespaceResourceManagers.clear();
        this.resourceNamespaces.clear();
        this.resourcePacks.forEach(IResourcePack::close);
        this.resourcePacks.clear();
    }

    public void close()
    {
        this.clearResourceNamespaces();
    }

    public void addReloadListener(IFutureReloadListener listener)
    {
        this.reloadListeners.add(listener);
        this.initTaskQueue.add(listener);
    }

    protected IAsyncReloader initializeAsyncReloader(Executor backgroundExecutor, Executor gameExecutor, List<IFutureReloadListener> listeners, CompletableFuture<Unit> waitingFor)
    {
        IAsyncReloader iasyncreloader;

        if (LOGGER.isDebugEnabled())
        {
            iasyncreloader = new DebugAsyncReloader(this, Lists.newArrayList(listeners), backgroundExecutor, gameExecutor, waitingFor);
        }
        else
        {
            iasyncreloader = AsyncReloader.create(this, Lists.newArrayList(listeners), backgroundExecutor, gameExecutor, waitingFor);
        }

        this.initTaskQueue.clear();
        return iasyncreloader;
    }

    public IAsyncReloader reloadResources(Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> waitingFor, List<IResourcePack> resourcePacks)
    {
        this.clearResourceNamespaces();
        LOGGER.info("Reloading ResourceManager: {}", () ->
        {
            return resourcePacks.stream().map(IResourcePack::getName).collect(Collectors.joining(", "));
        });

        for (IResourcePack iresourcepack : resourcePacks)
        {
            try
            {
                this.addResourcePack(iresourcepack);
            }
            catch (Exception exception)
            {
                LOGGER.error("Failed to add resource pack {}", iresourcepack.getName(), exception);
                return new SimpleReloadableResourceManager.FailedPackReloader(new SimpleReloadableResourceManager.FailedPackException(iresourcepack, exception));
            }
        }

        return this.initializeAsyncReloader(backgroundExecutor, gameExecutor, this.reloadListeners, waitingFor);
    }

    public Stream<IResourcePack> getResourcePackStream()
    {
        return this.resourcePacks.stream();
    }

    public static class FailedPackException extends RuntimeException
    {
        private final IResourcePack pack;

        public FailedPackException(IResourcePack pack, Throwable throwable)
        {
            super(pack.getName(), throwable);
            this.pack = pack;
        }

        public IResourcePack getPack()
        {
            return this.pack;
        }
    }

    static class FailedPackReloader implements IAsyncReloader
    {
        private final SimpleReloadableResourceManager.FailedPackException exception;
        private final CompletableFuture<Unit> onceDone;

        public FailedPackReloader(SimpleReloadableResourceManager.FailedPackException exception)
        {
            this.exception = exception;
            this.onceDone = new CompletableFuture<>();
            this.onceDone.completeExceptionally(exception);
        }

        public CompletableFuture<Unit> onceDone()
        {
            return this.onceDone;
        }

        public float estimateExecutionSpeed()
        {
            return 0.0F;
        }

        public boolean asyncPartDone()
        {
            return false;
        }

        public boolean fullyDone()
        {
            return true;
        }

        public void join()
        {
            throw this.exception;
        }
    }
}
