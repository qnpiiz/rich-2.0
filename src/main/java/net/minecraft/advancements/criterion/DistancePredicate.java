package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

public class DistancePredicate
{
    public static final DistancePredicate ANY = new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED);
    private final MinMaxBounds.FloatBound x;
    private final MinMaxBounds.FloatBound y;
    private final MinMaxBounds.FloatBound z;
    private final MinMaxBounds.FloatBound horizontal;
    private final MinMaxBounds.FloatBound absolute;

    public DistancePredicate(MinMaxBounds.FloatBound x, MinMaxBounds.FloatBound y, MinMaxBounds.FloatBound z, MinMaxBounds.FloatBound horizontal, MinMaxBounds.FloatBound absolute)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontal = horizontal;
        this.absolute = absolute;
    }

    public static DistancePredicate forHorizontal(MinMaxBounds.FloatBound distance)
    {
        return new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, distance, MinMaxBounds.FloatBound.UNBOUNDED);
    }

    public static DistancePredicate forVertical(MinMaxBounds.FloatBound distance)
    {
        return new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, distance, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED);
    }

    public boolean test(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        float f = (float)(x1 - x2);
        float f1 = (float)(y1 - y2);
        float f2 = (float)(z1 - z2);

        if (this.x.test(MathHelper.abs(f)) && this.y.test(MathHelper.abs(f1)) && this.z.test(MathHelper.abs(f2)))
        {
            if (!this.horizontal.testSquared((double)(f * f + f2 * f2)))
            {
                return false;
            }
            else
            {
                return this.absolute.testSquared((double)(f * f + f1 * f1 + f2 * f2));
            }
        }
        else
        {
            return false;
        }
    }

    public static DistancePredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "distance");
            MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(jsonobject.get("x"));
            MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("y"));
            MinMaxBounds.FloatBound minmaxbounds$floatbound2 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("z"));
            MinMaxBounds.FloatBound minmaxbounds$floatbound3 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("horizontal"));
            MinMaxBounds.FloatBound minmaxbounds$floatbound4 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("absolute"));
            return new DistancePredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, minmaxbounds$floatbound2, minmaxbounds$floatbound3, minmaxbounds$floatbound4);
        }
        else
        {
            return ANY;
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
            jsonobject.add("x", this.x.serialize());
            jsonobject.add("y", this.y.serialize());
            jsonobject.add("z", this.z.serialize());
            jsonobject.add("horizontal", this.horizontal.serialize());
            jsonobject.add("absolute", this.absolute.serialize());
            return jsonobject;
        }
    }
}
