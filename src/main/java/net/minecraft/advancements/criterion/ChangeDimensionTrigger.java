package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ChangeDimensionTrigger extends AbstractCriterionTrigger<ChangeDimensionTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("changed_dimension");

    public ResourceLocation getId()
    {
        return ID;
    }

    public ChangeDimensionTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        RegistryKey<World> registrykey = json.has("from") ? RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(JSONUtils.getString(json, "from"))) : null;
        RegistryKey<World> registrykey1 = json.has("to") ? RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(JSONUtils.getString(json, "to"))) : null;
        return new ChangeDimensionTrigger.Instance(entityPredicate, registrykey, registrykey1);
    }

    public void testForAll(ServerPlayerEntity player, RegistryKey<World> fromWorld, RegistryKey<World> toWorld)
    {
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(fromWorld, toWorld);
        });
    }

    public static class Instance extends CriterionInstance
    {
        @Nullable
        private final RegistryKey<World> from;
        @Nullable
        private final RegistryKey<World> to;

        public Instance(EntityPredicate.AndPredicate entityPredicate, @Nullable RegistryKey<World> fromWorld, @Nullable RegistryKey<World> toWorld)
        {
            super(ChangeDimensionTrigger.ID, entityPredicate);
            this.from = fromWorld;
            this.to = toWorld;
        }

        public static ChangeDimensionTrigger.Instance toWorld(RegistryKey<World> toWorld)
        {
            return new ChangeDimensionTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, (RegistryKey<World>)null, toWorld);
        }

        public boolean test(RegistryKey<World> fromWorld, RegistryKey<World> toWorld)
        {
            if (this.from != null && this.from != fromWorld)
            {
                return false;
            }
            else
            {
                return this.to == null || this.to == toWorld;
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);

            if (this.from != null)
            {
                jsonobject.addProperty("from", this.from.getLocation().toString());
            }

            if (this.to != null)
            {
                jsonobject.addProperty("to", this.to.getLocation().toString());
            }

            return jsonobject;
        }
    }
}
