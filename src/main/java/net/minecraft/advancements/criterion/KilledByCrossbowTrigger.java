package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class KilledByCrossbowTrigger extends AbstractCriterionTrigger<KilledByCrossbowTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

    public ResourceLocation getId()
    {
        return ID;
    }

    public KilledByCrossbowTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = EntityPredicate.AndPredicate.deserialize(json, "victims", conditionsParser);
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("unique_entity_types"));
        return new KilledByCrossbowTrigger.Instance(entityPredicate, aentitypredicate$andpredicate, minmaxbounds$intbound);
    }

    public void test(ServerPlayerEntity player, Collection<Entity> entities)
    {
        List<LootContext> list = Lists.newArrayList();
        Set < EntityType<? >> set = Sets.newHashSet();

        for (Entity entity : entities)
        {
            set.add(entity.getType());
            list.add(EntityPredicate.getLootContext(player, entity));
        }

        this.triggerListeners(player, (instance) ->
        {
            return instance.test(list, set.size());
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final EntityPredicate.AndPredicate[] entities;
        private final MinMaxBounds.IntBound bounds;

        public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate[] entities, MinMaxBounds.IntBound bounds)
        {
            super(KilledByCrossbowTrigger.ID, player);
            this.entities = entities;
            this.bounds = bounds;
        }

        public static KilledByCrossbowTrigger.Instance fromBuilders(EntityPredicate.Builder... builders)
        {
            EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = new EntityPredicate.AndPredicate[builders.length];

            for (int i = 0; i < builders.length; ++i)
            {
                EntityPredicate.Builder entitypredicate$builder = builders[i];
                aentitypredicate$andpredicate[i] = EntityPredicate.AndPredicate.createAndFromEntityCondition(entitypredicate$builder.build());
            }

            return new KilledByCrossbowTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, aentitypredicate$andpredicate, MinMaxBounds.IntBound.UNBOUNDED);
        }

        public static KilledByCrossbowTrigger.Instance fromBounds(MinMaxBounds.IntBound bounds)
        {
            EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = new EntityPredicate.AndPredicate[0];
            return new KilledByCrossbowTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, aentitypredicate$andpredicate, bounds);
        }

        public boolean test(Collection<LootContext> contexts, int bounds)
        {
            if (this.entities.length > 0)
            {
                List<LootContext> list = Lists.newArrayList(contexts);

                for (EntityPredicate.AndPredicate entitypredicate$andpredicate : this.entities)
                {
                    boolean flag = false;
                    Iterator<LootContext> iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        LootContext lootcontext = iterator.next();

                        if (entitypredicate$andpredicate.testContext(lootcontext))
                        {
                            iterator.remove();
                            flag = true;
                            break;
                        }
                    }

                    if (!flag)
                    {
                        return false;
                    }
                }
            }

            return this.bounds.test(bounds);
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("victims", EntityPredicate.AndPredicate.serializeConditionsIn(this.entities, conditions));
            jsonobject.add("unique_entity_types", this.bounds.serialize());
            return jsonobject;
        }
    }
}
