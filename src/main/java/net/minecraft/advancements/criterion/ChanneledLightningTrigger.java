package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class ChanneledLightningTrigger extends AbstractCriterionTrigger<ChanneledLightningTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

    public ResourceLocation getId()
    {
        return ID;
    }

    public ChanneledLightningTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = EntityPredicate.AndPredicate.deserialize(json, "victims", conditionsParser);
        return new ChanneledLightningTrigger.Instance(entityPredicate, aentitypredicate$andpredicate);
    }

    public void trigger(ServerPlayerEntity player, Collection <? extends Entity > entityTriggered)
    {
        List<LootContext> list = entityTriggered.stream().map((entity) ->
        {
            return EntityPredicate.getLootContext(player, entity);
        }).collect(Collectors.toList());
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(list);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final EntityPredicate.AndPredicate[] victims;

        public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate[] victims)
        {
            super(ChanneledLightningTrigger.ID, player);
            this.victims = victims;
        }

        public static ChanneledLightningTrigger.Instance channeledLightning(EntityPredicate... victims)
        {
            return new ChanneledLightningTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, Stream.of(victims).map(EntityPredicate.AndPredicate::createAndFromEntityCondition).toArray((entityCount) ->
            {
                return new EntityPredicate.AndPredicate[entityCount];
            }));
        }

        public boolean test(Collection <? extends LootContext > victims)
        {
            for (EntityPredicate.AndPredicate entitypredicate$andpredicate : this.victims)
            {
                boolean flag = false;

                for (LootContext lootcontext : victims)
                {
                    if (entitypredicate$andpredicate.testContext(lootcontext))
                    {
                        flag = true;
                        break;
                    }
                }

                if (!flag)
                {
                    return false;
                }
            }

            return true;
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("victims", EntityPredicate.AndPredicate.serializeConditionsIn(this.victims, conditions));
            return jsonobject;
        }
    }
}
