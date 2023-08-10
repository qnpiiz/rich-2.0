package net.minecraft.client.renderer.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.CustomGuis;
import net.optifine.EmissiveTextures;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements IFutureReloadListener, ITickable, AutoCloseable
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation RESOURCE_LOCATION_EMPTY = new ResourceLocation("");
    private final Map<ResourceLocation, Texture> mapTextureObjects = Maps.newHashMap();
    private final Set<ITickable> listTickables = Sets.newHashSet();
    private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
    private final IResourceManager resourceManager;
    private Texture boundTexture;
    private ResourceLocation boundTextureLocation;

    public TextureManager(IResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    public void bindTexture(ResourceLocation resource)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(() ->
            {
                this.bindTextureRaw(resource);
            });
        }
        else
        {
            this.bindTextureRaw(resource);
        }
    }

    private void bindTextureRaw(ResourceLocation resource)
    {
        if (Config.isCustomGuis())
        {
            resource = CustomGuis.getTextureLocation(resource);
        }

        Texture texture = this.mapTextureObjects.get(resource);

        if (texture == null)
        {
            texture = new SimpleTexture(resource);
            this.loadTexture(resource, texture);
        }

        if (Config.isShaders())
        {
            ShadersTex.bindTexture(texture);
        }
        else
        {
            texture.bindTexture();
        }

        this.boundTexture = texture;
        this.boundTextureLocation = resource;
    }

    public void loadTexture(ResourceLocation textureLocation, Texture textureObj)
    {
        textureObj = this.func_230183_b_(textureLocation, textureObj);
        Texture texture = this.mapTextureObjects.put(textureLocation, textureObj);

        if (texture != textureObj)
        {
            if (texture != null && texture != MissingTextureSprite.getDynamicTexture())
            {
                this.listTickables.remove(texture);
                this.func_243505_b(textureLocation, texture);
            }

            if (textureObj instanceof ITickable)
            {
                this.listTickables.add((ITickable)textureObj);
            }
        }
    }

    private void func_243505_b(ResourceLocation p_243505_1_, Texture p_243505_2_)
    {
        if (p_243505_2_ != MissingTextureSprite.getDynamicTexture())
        {
            try
            {
                p_243505_2_.close();
            }
            catch (Exception exception)
            {
                LOGGER.warn("Failed to close texture {}", p_243505_1_, exception);
            }
        }

        p_243505_2_.deleteGlTexture();
    }

    private Texture func_230183_b_(ResourceLocation p_230183_1_, Texture p_230183_2_)
    {
        try
        {
            p_230183_2_.loadTexture(this.resourceManager);
            return p_230183_2_;
        }
        catch (IOException ioexception)
        {
            if (p_230183_1_ != RESOURCE_LOCATION_EMPTY)
            {
                LOGGER.warn("Failed to load texture: {}", p_230183_1_, ioexception);
            }

            return MissingTextureSprite.getDynamicTexture();
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
            crashreportcategory.addDetail("Resource location", p_230183_1_);
            crashreportcategory.addDetail("Texture object class", () ->
            {
                return p_230183_2_.getClass().getName();
            });
            throw new ReportedException(crashreport);
        }
    }

    @Nullable
    public Texture getTexture(ResourceLocation textureLocation)
    {
        return this.mapTextureObjects.get(textureLocation);
    }

    public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture)
    {
        Integer integer = this.mapTextureCounters.get(name);

        if (integer == null)
        {
            integer = 1;
        }
        else
        {
            integer = integer + 1;
        }

        this.mapTextureCounters.put(name, integer);
        ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
        this.loadTexture(resourcelocation, texture);
        return resourcelocation;
    }

    public CompletableFuture<Void> loadAsync(ResourceLocation textureLocation, Executor executor)
    {
        if (!this.mapTextureObjects.containsKey(textureLocation))
        {
            PreloadedTexture preloadedtexture = new PreloadedTexture(this.resourceManager, textureLocation, executor);
            this.mapTextureObjects.put(textureLocation, preloadedtexture);
            return preloadedtexture.getCompletableFuture().thenRunAsync(() ->
            {
                this.loadTexture(textureLocation, preloadedtexture);
            }, TextureManager::execute);
        }
        else
        {
            return CompletableFuture.completedFuture((Void)null);
        }
    }

    private static void execute(Runnable runnableIn)
    {
        Minecraft.getInstance().execute(() ->
        {
            RenderSystem.recordRenderCall(runnableIn::run);
        });
    }

    public void tick()
    {
        for (ITickable itickable : this.listTickables)
        {
            itickable.tick();
        }
    }

    public void deleteTexture(ResourceLocation textureLocation)
    {
        Texture texture = this.getTexture(textureLocation);

        if (texture != null)
        {
            this.mapTextureObjects.remove(textureLocation);
            TextureUtil.releaseTextureId(texture.getGlTextureId());
        }
    }

    public void close()
    {
        this.mapTextureObjects.forEach(this::func_243505_b);
        this.mapTextureObjects.clear();
        this.listTickables.clear();
        this.mapTextureCounters.clear();
    }

    public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        Config.dbg("*** Reloading textures ***");
        Config.log("Resource packs: " + Config.getResourcePackNames());
        Iterator iterator = this.mapTextureObjects.keySet().iterator();

        while (iterator.hasNext())
        {
            ResourceLocation resourcelocation = (ResourceLocation)iterator.next();
            String s = resourcelocation.getPath();

            if (s.startsWith("optifine/") || EmissiveTextures.isEmissive(resourcelocation))
            {
                Texture texture = this.mapTextureObjects.get(resourcelocation);

                if (texture instanceof Texture)
                {
                    texture.deleteGlTexture();
                }

                iterator.remove();
            }
        }

        EmissiveTextures.update();
        return CompletableFuture.allOf(MainMenuScreen.loadAsync(this, backgroundExecutor), this.loadAsync(Widget.WIDGETS_LOCATION, backgroundExecutor)).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((p_lambda$reload$4_3_) ->
        {
            MissingTextureSprite.getDynamicTexture();
            RealmsMainScreen.func_227932_a_(this.resourceManager);
            Set<Entry<ResourceLocation, Texture>> set = new HashSet<>(this.mapTextureObjects.entrySet());
            Iterator<Entry<ResourceLocation, Texture>> iterator1 = set.iterator();

            while (iterator1.hasNext())
            {
                Entry<ResourceLocation, Texture> entry = iterator1.next();
                ResourceLocation resourcelocation1 = entry.getKey();
                Texture texture1 = entry.getValue();

                if (texture1 == MissingTextureSprite.getDynamicTexture() && !resourcelocation1.equals(MissingTextureSprite.getLocation()))
                {
                    iterator1.remove();
                }
                else
                {
                    texture1.loadTexture(this, resourceManager, resourcelocation1, gameExecutor);
                }
            }
        }, (p_lambda$reload$5_0_) ->
        {
            RenderSystem.recordRenderCall(p_lambda$reload$5_0_::run);
        });
    }

    public Texture getBoundTexture()
    {
        return this.boundTexture;
    }

    public ResourceLocation getBoundTextureLocation()
    {
        return this.boundTextureLocation;
    }
}
