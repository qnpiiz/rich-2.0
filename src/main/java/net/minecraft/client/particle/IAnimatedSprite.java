package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface IAnimatedSprite
{
    TextureAtlasSprite get(int particleAge, int particleMaxAge);

    TextureAtlasSprite get(Random rand);
}
