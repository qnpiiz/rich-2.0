package net.minecraft.client.gui.fonts.providers;

import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;

public interface IGlyphProviderFactory
{
    @Nullable
    IGlyphProvider create(IResourceManager resourceManagerIn);
}
