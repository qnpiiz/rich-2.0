package net.minecraft.tags;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;

public interface ITagCollectionSupplier
{
    /** CONFIGURED_FEATURE_KEY */
    ITagCollectionSupplier TAG_COLLECTION_SUPPLIER = getTagCollectionSupplier(ITagCollection.getEmptyTagCollection(), ITagCollection.getEmptyTagCollection(), ITagCollection.getEmptyTagCollection(), ITagCollection.getEmptyTagCollection());

    ITagCollection<Block> getBlockTags();

    ITagCollection<Item> getItemTags();

    ITagCollection<Fluid> getFluidTags();

    ITagCollection < EntityType<? >> getEntityTypeTags();

default void updateTags()
    {
        TagRegistryManager.fetchTags(this);
        Blocks.cacheBlockStates();
    }

default void writeTagCollectionSupplierToBuffer(PacketBuffer buffer)
    {
        this.getBlockTags().writeTagCollectionToBuffer(buffer, Registry.BLOCK);
        this.getItemTags().writeTagCollectionToBuffer(buffer, Registry.ITEM);
        this.getFluidTags().writeTagCollectionToBuffer(buffer, Registry.FLUID);
        this.getEntityTypeTags().writeTagCollectionToBuffer(buffer, Registry.ENTITY_TYPE);
    }

    static ITagCollectionSupplier readTagCollectionSupplierFromBuffer(PacketBuffer buffer)
    {
        ITagCollection<Block> itagcollection = ITagCollection.readTagCollectionFromBuffer(buffer, Registry.BLOCK);
        ITagCollection<Item> itagcollection1 = ITagCollection.readTagCollectionFromBuffer(buffer, Registry.ITEM);
        ITagCollection<Fluid> itagcollection2 = ITagCollection.readTagCollectionFromBuffer(buffer, Registry.FLUID);
        ITagCollection < EntityType<? >> itagcollection3 = ITagCollection.readTagCollectionFromBuffer(buffer, Registry.ENTITY_TYPE);
        return getTagCollectionSupplier(itagcollection, itagcollection1, itagcollection2, itagcollection3);
    }

    static ITagCollectionSupplier getTagCollectionSupplier(final ITagCollection<Block> blockTags, final ITagCollection<Item> itemTags, final ITagCollection<Fluid> fluidTags, final ITagCollection < EntityType<? >> entityTypeTags)
    {
        return new ITagCollectionSupplier()
        {
            public ITagCollection<Block> getBlockTags()
            {
                return blockTags;
            }
            public ITagCollection<Item> getItemTags()
            {
                return itemTags;
            }
            public ITagCollection<Fluid> getFluidTags()
            {
                return fluidTags;
            }
            public ITagCollection < EntityType<? >> getEntityTypeTags()
            {
                return entityTypeTags;
            }
        };
    }
}
