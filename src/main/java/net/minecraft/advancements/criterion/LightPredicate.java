package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class LightPredicate
{
    public static final LightPredicate ANY = new LightPredicate(MinMaxBounds.IntBound.UNBOUNDED);
    private final MinMaxBounds.IntBound bounds;

    private LightPredicate(MinMaxBounds.IntBound bounds)
    {
        this.bounds = bounds;
    }

    public boolean test(ServerWorld world, BlockPos pos)
    {
        if (this == ANY)
        {
            return true;
        }
        else if (!world.isBlockPresent(pos))
        {
            return false;
        }
        else
        {
            return this.bounds.test(world.getLight(pos));
        }
    }

    public JsonElement serialize()
    {
        if (this == ANY)
        {
            return JsonNull.INSTANCE;
        }
        else
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add("light", this.bounds.serialize());
            return jsonobject;
        }
    }

    public static LightPredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "light");
            MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("light"));
            return new LightPredicate(minmaxbounds$intbound);
        }
        else
        {
            return ANY;
        }
    }
}
