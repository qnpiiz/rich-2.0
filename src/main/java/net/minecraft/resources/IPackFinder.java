package net.minecraft.resources;

import java.util.function.Consumer;

public interface IPackFinder
{
    void findPacks(Consumer<ResourcePackInfo> infoConsumer, ResourcePackInfo.IFactory infoFactory);
}
