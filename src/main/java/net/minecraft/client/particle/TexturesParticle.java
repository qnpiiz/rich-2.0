package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class TexturesParticle
{
    @Nullable
    private final List<ResourceLocation> textures;

    private TexturesParticle(@Nullable List<ResourceLocation> textures)
    {
        this.textures = textures;
    }

    @Nullable
    public List<ResourceLocation> getTextures()
    {
        return this.textures;
    }

    public static TexturesParticle deserialize(JsonObject json)
    {
        JsonArray jsonarray = JSONUtils.getJsonArray(json, "textures", (JsonArray)null);
        List<ResourceLocation> list;

        if (jsonarray != null)
        {
            list = Streams.stream(jsonarray).map((element) ->
            {
                return JSONUtils.getString(element, "texture");
            }).map(ResourceLocation::new).collect(ImmutableList.toImmutableList());
        }
        else
        {
            list = null;
        }

        return new TexturesParticle(list);
    }
}
