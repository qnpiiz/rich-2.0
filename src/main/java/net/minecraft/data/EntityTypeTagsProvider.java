package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagsProvider extends TagsProvider < EntityType<? >>
{
    public EntityTypeTagsProvider(DataGenerator p_i50784_1_)
    {
        super(p_i50784_1_, Registry.ENTITY_TYPE);
    }

    protected void registerTags()
    {
        this.getOrCreateBuilder(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
        this.getOrCreateBuilder(EntityTypeTags.RAIDERS).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
        this.getOrCreateBuilder(EntityTypeTags.BEEHIVE_INHABITORS).addItemEntry(EntityType.BEE);
        this.getOrCreateBuilder(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
        this.getOrCreateBuilder(EntityTypeTags.IMPACT_PROJECTILES).addTag(EntityTypeTags.ARROWS).add(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL);
    }

    /**
     * Resolves a Path for the location to save the given tag.
     */
    protected Path makePath(ResourceLocation id)
    {
        return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/entity_types/" + id.getPath() + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    public String getName()
    {
        return "Entity Type Tags";
    }
}
