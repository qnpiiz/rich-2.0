package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;

public class EntityFlagsPredicate
{
    public static final EntityFlagsPredicate ALWAYS_TRUE = (new EntityFlagsPredicate.Builder()).build();
    @Nullable
    private final Boolean onFire;
    @Nullable
    private final Boolean sneaking;
    @Nullable
    private final Boolean sprinting;
    @Nullable
    private final Boolean swimming;
    @Nullable
    private final Boolean baby;

    public EntityFlagsPredicate(@Nullable Boolean onFire, @Nullable Boolean sneaking, @Nullable Boolean sprinting, @Nullable Boolean swimming, @Nullable Boolean baby)
    {
        this.onFire = onFire;
        this.sneaking = sneaking;
        this.sprinting = sprinting;
        this.swimming = swimming;
        this.baby = baby;
    }

    public boolean test(Entity entity)
    {
        if (this.onFire != null && entity.isBurning() != this.onFire)
        {
            return false;
        }
        else if (this.sneaking != null && entity.isCrouching() != this.sneaking)
        {
            return false;
        }
        else if (this.sprinting != null && entity.isSprinting() != this.sprinting)
        {
            return false;
        }
        else if (this.swimming != null && entity.isSwimming() != this.swimming)
        {
            return false;
        }
        else
        {
            return this.baby == null || !(entity instanceof LivingEntity) || ((LivingEntity)entity).isChild() == this.baby;
        }
    }

    @Nullable
    private static Boolean getBoolean(JsonObject jsonObject, String name)
    {
        return jsonObject.has(name) ? JSONUtils.getBoolean(jsonObject, name) : null;
    }

    public static EntityFlagsPredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "entity flags");
            Boolean obool = getBoolean(jsonobject, "is_on_fire");
            Boolean obool1 = getBoolean(jsonobject, "is_sneaking");
            Boolean obool2 = getBoolean(jsonobject, "is_sprinting");
            Boolean obool3 = getBoolean(jsonobject, "is_swimming");
            Boolean obool4 = getBoolean(jsonobject, "is_baby");
            return new EntityFlagsPredicate(obool, obool1, obool2, obool3, obool4);
        }
        else
        {
            return ALWAYS_TRUE;
        }
    }

    private void putBoolean(JsonObject jsonObject, String name, @Nullable Boolean bool)
    {
        if (bool != null)
        {
            jsonObject.addProperty(name, bool);
        }
    }

    public JsonElement serialize()
    {
        if (this == ALWAYS_TRUE)
        {
            return JsonNull.INSTANCE;
        }
        else
        {
            JsonObject jsonobject = new JsonObject();
            this.putBoolean(jsonobject, "is_on_fire", this.onFire);
            this.putBoolean(jsonobject, "is_sneaking", this.sneaking);
            this.putBoolean(jsonobject, "is_sprinting", this.sprinting);
            this.putBoolean(jsonobject, "is_swimming", this.swimming);
            this.putBoolean(jsonobject, "is_baby", this.baby);
            return jsonobject;
        }
    }

    public static class Builder
    {
        @Nullable
        private Boolean onFire;
        @Nullable
        private Boolean sneaking;
        @Nullable
        private Boolean sprinting;
        @Nullable
        private Boolean swimming;
        @Nullable
        private Boolean baby;

        public static EntityFlagsPredicate.Builder create()
        {
            return new EntityFlagsPredicate.Builder();
        }

        public EntityFlagsPredicate.Builder onFire(@Nullable Boolean onFire)
        {
            this.onFire = onFire;
            return this;
        }

        public EntityFlagsPredicate.Builder isBaby(@Nullable Boolean baby)
        {
            this.baby = baby;
            return this;
        }

        public EntityFlagsPredicate build()
        {
            return new EntityFlagsPredicate(this.onFire, this.sneaking, this.sprinting, this.swimming, this.baby);
        }
    }
}
