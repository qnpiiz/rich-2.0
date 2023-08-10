package net.minecraft.resources;

import java.util.function.Consumer;

public class ServerPackFinder implements IPackFinder
{
    private final VanillaPack field_195738_a = new VanillaPack("minecraft");

    public void findPacks(Consumer<ResourcePackInfo> infoConsumer, ResourcePackInfo.IFactory infoFactory)
    {
        ResourcePackInfo resourcepackinfo = ResourcePackInfo.createResourcePack("vanilla", false, () ->
        {
            return this.field_195738_a;
        }, infoFactory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.BUILTIN);

        if (resourcepackinfo != null)
        {
            infoConsumer.accept(resourcepackinfo);
        }
    }
}
