package net.minecraft.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.JSONUtils;

public class FishingPredicate
{
    public static final FishingPredicate field_234635_a_ = new FishingPredicate(false);
    private boolean field_234636_b_;

    private FishingPredicate(boolean p_i231586_1_)
    {
        this.field_234636_b_ = p_i231586_1_;
    }

    public static FishingPredicate func_234640_a_(boolean p_234640_0_)
    {
        return new FishingPredicate(p_234640_0_);
    }

    public static FishingPredicate func_234639_a_(@Nullable JsonElement p_234639_0_)
    {
        if (p_234639_0_ != null && !p_234639_0_.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(p_234639_0_, "fishing_hook");
            JsonElement jsonelement = jsonobject.get("in_open_water");
            return jsonelement != null ? new FishingPredicate(JSONUtils.getBoolean(jsonelement, "in_open_water")) : field_234635_a_;
        }
        else
        {
            return field_234635_a_;
        }
    }

    public JsonElement func_234637_a_()
    {
        if (this == field_234635_a_)
        {
            return JsonNull.INSTANCE;
        }
        else
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add("in_open_water", new JsonPrimitive(this.field_234636_b_));
            return jsonobject;
        }
    }

    public boolean func_234638_a_(Entity p_234638_1_)
    {
        if (this == field_234635_a_)
        {
            return true;
        }
        else if (!(p_234638_1_ instanceof FishingBobberEntity))
        {
            return false;
        }
        else
        {
            FishingBobberEntity fishingbobberentity = (FishingBobberEntity)p_234638_1_;
            return this.field_234636_b_ == fishingbobberentity.func_234605_g_();
        }
    }
}
