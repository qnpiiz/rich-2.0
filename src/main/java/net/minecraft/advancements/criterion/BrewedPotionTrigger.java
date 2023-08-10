package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BrewedPotionTrigger extends AbstractCriterionTrigger<BrewedPotionTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("brewed_potion");

    public ResourceLocation getId()
    {
        return ID;
    }

    public BrewedPotionTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        Potion potion = null;

        if (json.has("potion"))
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "potion"));
            potion = Registry.POTION.getOptional(resourcelocation).orElseThrow(() ->
            {
                return new JsonSyntaxException("Unknown potion '" + resourcelocation + "'");
            });
        }

        return new BrewedPotionTrigger.Instance(entityPredicate, potion);
    }

    public void trigger(ServerPlayerEntity player, Potion potionIn)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(potionIn);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final Potion potion;

        public Instance(EntityPredicate.AndPredicate player, @Nullable Potion potion)
        {
            super(BrewedPotionTrigger.ID, player);
            this.potion = potion;
        }

        public static BrewedPotionTrigger.Instance brewedPotion()
        {
            return new BrewedPotionTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, (Potion)null);
        }

        public boolean test(Potion potion)
        {
            return this.potion == null || this.potion == potion;
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);

            if (this.potion != null)
            {
                jsonobject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
            }

            return jsonobject;
        }
    }
}
