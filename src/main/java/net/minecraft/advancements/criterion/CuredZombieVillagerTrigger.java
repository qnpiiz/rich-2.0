package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class CuredZombieVillagerTrigger extends AbstractCriterionTrigger<CuredZombieVillagerTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");

    public ResourceLocation getId()
    {
        return ID;
    }

    public CuredZombieVillagerTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "zombie", conditionsParser);
        EntityPredicate.AndPredicate entitypredicate$andpredicate1 = EntityPredicate.AndPredicate.deserializeJSONObject(json, "villager", conditionsParser);
        return new CuredZombieVillagerTrigger.Instance(entityPredicate, entitypredicate$andpredicate, entitypredicate$andpredicate1);
    }

    public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager)
    {
        LootContext lootcontext = EntityPredicate.getLootContext(player, zombie);
        LootContext lootcontext1 = EntityPredicate.getLootContext(player, villager);
        this.triggerListeners(player, (instance) ->
        {
            return instance.test(lootcontext, lootcontext1);
        });
    }

    public static class Instance extends CriterionInstance
    {
        private final EntityPredicate.AndPredicate zombie;
        private final EntityPredicate.AndPredicate villager;

        public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate zombie, EntityPredicate.AndPredicate villager)
        {
            super(CuredZombieVillagerTrigger.ID, player);
            this.zombie = zombie;
            this.villager = villager;
        }

        public static CuredZombieVillagerTrigger.Instance any()
        {
            return new CuredZombieVillagerTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.ANY_AND, EntityPredicate.AndPredicate.ANY_AND);
        }

        public boolean test(LootContext zombie, LootContext villager)
        {
            if (!this.zombie.testContext(zombie))
            {
                return false;
            }
            else
            {
                return this.villager.testContext(villager);
            }
        }

        public JsonObject serialize(ConditionArraySerializer conditions)
        {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("zombie", this.zombie.serializeConditions(conditions));
            jsonobject.add("villager", this.villager.serializeConditions(conditions));
            return jsonobject;
        }
    }
}
