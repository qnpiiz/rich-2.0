package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public abstract class CriterionInstance implements ICriterionInstance
{
    private final ResourceLocation criterion;
    private final EntityPredicate.AndPredicate playerCondition;

    public CriterionInstance(ResourceLocation criterion, EntityPredicate.AndPredicate playerCondition)
    {
        this.criterion = criterion;
        this.playerCondition = playerCondition;
    }

    public ResourceLocation getId()
    {
        return this.criterion;
    }

    protected EntityPredicate.AndPredicate getPlayerCondition()
    {
        return this.playerCondition;
    }

    public JsonObject serialize(ConditionArraySerializer conditions)
    {
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("player", this.playerCondition.serializeConditions(conditions));
        return jsonobject;
    }

    public String toString()
    {
        return "AbstractCriterionInstance{criterion=" + this.criterion + '}';
    }
}
