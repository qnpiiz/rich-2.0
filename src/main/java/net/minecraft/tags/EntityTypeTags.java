package net.minecraft.tags;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

public final class EntityTypeTags
{
    protected static final TagRegistry < EntityType<? >> tagCollection = TagRegistryManager.create(new ResourceLocation("entity_type"), ITagCollectionSupplier::getEntityTypeTags);
    public static final ITag.INamedTag < EntityType<? >> SKELETONS = getTagById("skeletons");
    public static final ITag.INamedTag < EntityType<? >> RAIDERS = getTagById("raiders");
    public static final ITag.INamedTag < EntityType<? >> BEEHIVE_INHABITORS = getTagById("beehive_inhabitors");
    public static final ITag.INamedTag < EntityType<? >> ARROWS = getTagById("arrows");
    public static final ITag.INamedTag < EntityType<? >> IMPACT_PROJECTILES = getTagById("impact_projectiles");

    private static ITag.INamedTag < EntityType<? >> getTagById(String id)
    {
        return tagCollection.createTag(id);
    }

    public static ITagCollection < EntityType<? >> getCollection()
    {
        return tagCollection.getCollection();
    }

    public static List <? extends ITag.INamedTag < EntityType<? >>> getAllTags()
    {
        return tagCollection.getTags();
    }
}
