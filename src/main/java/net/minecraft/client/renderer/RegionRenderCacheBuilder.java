package net.minecraft.client.renderer;

import java.util.Map;
import java.util.stream.Collectors;

public class RegionRenderCacheBuilder
{
    private final Map<RenderType, BufferBuilder> builders = RenderType.getBlockRenderTypes().stream().collect(Collectors.toMap((renderType) ->
    {
        return renderType;
    }, (renderType) ->
    {
        return new BufferBuilder(renderType.getBufferSize());
    }));

    public BufferBuilder getBuilder(RenderType renderTypeIn)
    {
        return this.builders.get(renderTypeIn);
    }

    public void resetBuilders()
    {
        this.builders.values().forEach(BufferBuilder::reset);
    }

    public void discardBuilders()
    {
        this.builders.values().forEach(BufferBuilder::discard);
    }
}
