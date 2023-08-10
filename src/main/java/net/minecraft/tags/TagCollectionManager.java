package net.minecraft.tags;

import java.util.stream.Collectors;

public class TagCollectionManager
{
    private static volatile ITagCollectionSupplier manager = ITagCollectionSupplier.getTagCollectionSupplier(ITagCollection.getTagCollectionFromMap(BlockTags.getAllTags().stream().collect(Collectors.toMap(ITag.INamedTag::getName, (blockTag) ->
    {
        return blockTag;
    }))), ITagCollection.getTagCollectionFromMap(ItemTags.getAllTags().stream().collect(Collectors.toMap(ITag.INamedTag::getName, (itemTag) ->
    {
        return itemTag;
    }))), ITagCollection.getTagCollectionFromMap(FluidTags.getAllTags().stream().collect(Collectors.toMap(ITag.INamedTag::getName, (fluidTag) ->
    {
        return fluidTag;
    }))), ITagCollection.getTagCollectionFromMap(EntityTypeTags.getAllTags().stream().collect(Collectors.toMap(ITag.INamedTag::getName, (entityTypeTag) ->
    {
        return entityTypeTag;
    }))));

    public static ITagCollectionSupplier getManager()
    {
        return manager;
    }

    public static void setManager(ITagCollectionSupplier managerIn)
    {
        manager = managerIn;
    }
}
