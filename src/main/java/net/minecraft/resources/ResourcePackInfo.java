package net.minecraft.resources;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackInfo implements AutoCloseable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PackMetadataSection BROKEN_ASSETS_FALLBACK = new PackMetadataSection((new TranslationTextComponent("resourcePack.broken_assets")).mergeStyle(new TextFormatting[] {TextFormatting.RED, TextFormatting.ITALIC}), SharedConstants.getVersion().getPackVersion());
    private final String name;
    private final Supplier<IResourcePack> resourcePackSupplier;
    private final ITextComponent title;
    private final ITextComponent description;
    private final PackCompatibility compatibility;
    private final ResourcePackInfo.Priority priority;
    private final boolean alwaysEnabled;
    private final boolean orderLocked;
    private final IPackNameDecorator decorator;

    @Nullable
    public static ResourcePackInfo createResourcePack(String nameIn, boolean p_195793_1_, Supplier<IResourcePack> p_195793_2_, ResourcePackInfo.IFactory factory, ResourcePackInfo.Priority p_195793_4_, IPackNameDecorator p_195793_5_)
    {
        try (IResourcePack iresourcepack = p_195793_2_.get())
        {
            PackMetadataSection packmetadatasection = iresourcepack.getMetadata(PackMetadataSection.SERIALIZER);

            if (p_195793_1_ && packmetadatasection == null)
            {
                LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
                packmetadatasection = BROKEN_ASSETS_FALLBACK;
            }

            if (packmetadatasection != null)
            {
                return factory.create(nameIn, p_195793_1_, p_195793_2_, iresourcepack, packmetadatasection, p_195793_4_, p_195793_5_);
            }

            LOGGER.warn("Couldn't find pack meta for pack {}", (Object)nameIn);
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Couldn't get pack info for: {}", (Object)ioexception.toString());
        }

        return null;
    }

    public ResourcePackInfo(String p_i231422_1_, boolean p_i231422_2_, Supplier<IResourcePack> p_i231422_3_, ITextComponent p_i231422_4_, ITextComponent p_i231422_5_, PackCompatibility p_i231422_6_, ResourcePackInfo.Priority p_i231422_7_, boolean p_i231422_8_, IPackNameDecorator p_i231422_9_)
    {
        this.name = p_i231422_1_;
        this.resourcePackSupplier = p_i231422_3_;
        this.title = p_i231422_4_;
        this.description = p_i231422_5_;
        this.compatibility = p_i231422_6_;
        this.alwaysEnabled = p_i231422_2_;
        this.priority = p_i231422_7_;
        this.orderLocked = p_i231422_8_;
        this.decorator = p_i231422_9_;
    }

    public ResourcePackInfo(String p_i231421_1_, boolean p_i231421_2_, Supplier<IResourcePack> p_i231421_3_, IResourcePack p_i231421_4_, PackMetadataSection p_i231421_5_, ResourcePackInfo.Priority p_i231421_6_, IPackNameDecorator p_i231421_7_)
    {
        this(p_i231421_1_, p_i231421_2_, p_i231421_3_, new StringTextComponent(p_i231421_4_.getName()), p_i231421_5_.getDescription(), PackCompatibility.getCompatibility(p_i231421_5_.getPackFormat()), p_i231421_6_, false, p_i231421_7_);
    }

    public ITextComponent getTitle()
    {
        return this.title;
    }

    public ITextComponent getDescription()
    {
        return this.description;
    }

    public ITextComponent getChatLink(boolean p_195794_1_)
    {
        return TextComponentUtils.wrapWithSquareBrackets(this.decorator.decorate(new StringTextComponent(this.name))).modifyStyle((p_211689_2_) ->
        {
            return p_211689_2_.setFormatting(p_195794_1_ ? TextFormatting.GREEN : TextFormatting.RED).setInsertion(StringArgumentType.escapeIfRequired(this.name)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new StringTextComponent("")).append(this.title).appendString("\n").append(this.description)));
        });
    }

    public PackCompatibility getCompatibility()
    {
        return this.compatibility;
    }

    public IResourcePack getResourcePack()
    {
        return this.resourcePackSupplier.get();
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isAlwaysEnabled()
    {
        return this.alwaysEnabled;
    }

    public boolean isOrderLocked()
    {
        return this.orderLocked;
    }

    public ResourcePackInfo.Priority getPriority()
    {
        return this.priority;
    }

    public IPackNameDecorator getDecorator()
    {
        return this.decorator;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ResourcePackInfo))
        {
            return false;
        }
        else
        {
            ResourcePackInfo resourcepackinfo = (ResourcePackInfo)p_equals_1_;
            return this.name.equals(resourcepackinfo.name);
        }
    }

    public int hashCode()
    {
        return this.name.hashCode();
    }

    public void close()
    {
    }

    @FunctionalInterface
    public interface IFactory
    {
        @Nullable
        ResourcePackInfo create(String p_create_1_, boolean p_create_2_, Supplier<IResourcePack> p_create_3_, IResourcePack p_create_4_, PackMetadataSection p_create_5_, ResourcePackInfo.Priority p_create_6_, IPackNameDecorator p_create_7_);
    }

    public static enum Priority
    {
        TOP,
        BOTTOM;

        public <T> int insert(List<T> p_198993_1_, T p_198993_2_, Function<T, ResourcePackInfo> p_198993_3_, boolean p_198993_4_)
        {
            ResourcePackInfo.Priority resourcepackinfo$priority = p_198993_4_ ? this.opposite() : this;

            if (resourcepackinfo$priority == BOTTOM)
            {
                int j;

                for (j = 0; j < p_198993_1_.size(); ++j)
                {
                    ResourcePackInfo resourcepackinfo1 = p_198993_3_.apply(p_198993_1_.get(j));

                    if (!resourcepackinfo1.isOrderLocked() || resourcepackinfo1.getPriority() != this)
                    {
                        break;
                    }
                }

                p_198993_1_.add(j, p_198993_2_);
                return j;
            }
            else
            {
                int i;

                for (i = p_198993_1_.size() - 1; i >= 0; --i)
                {
                    ResourcePackInfo resourcepackinfo = p_198993_3_.apply(p_198993_1_.get(i));

                    if (!resourcepackinfo.isOrderLocked() || resourcepackinfo.getPriority() != this)
                    {
                        break;
                    }
                }

                p_198993_1_.add(i + 1, p_198993_2_);
                return i + 1;
            }
        }

        public ResourcePackInfo.Priority opposite()
        {
            return this == TOP ? BOTTOM : TOP;
        }
    }
}
