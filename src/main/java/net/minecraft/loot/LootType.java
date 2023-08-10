package net.minecraft.loot;

public class LootType<T>
{
    private final ILootSerializer <? extends T > serializer;

    public LootType(ILootSerializer <? extends T > serializer)
    {
        this.serializer = serializer;
    }

    public ILootSerializer <? extends T > getSerializer()
    {
        return this.serializer;
    }
}
