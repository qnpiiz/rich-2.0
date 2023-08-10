package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.FishingPredicate;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class EntityPredicate
{
    public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NBTPredicate.ANY, EntityFlagsPredicate.ALWAYS_TRUE, EntityEquipmentPredicate.ANY, PlayerPredicate.ANY, FishingPredicate.field_234635_a_, (String)null, (ResourceLocation)null);
    private final EntityTypePredicate type;
    private final DistancePredicate distance;
    private final LocationPredicate location;
    private final MobEffectsPredicate effects;
    private final NBTPredicate nbt;
    private final EntityFlagsPredicate flags;
    private final EntityEquipmentPredicate equipment;
    private final PlayerPredicate player;
    private final FishingPredicate fishingCondition;
    private final EntityPredicate mountCondition;
    private final EntityPredicate targetCondition;
    @Nullable
    private final String team;
    @Nullable
    private final ResourceLocation catType;

    private EntityPredicate(EntityTypePredicate type, DistancePredicate distance, LocationPredicate location, MobEffectsPredicate effects, NBTPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment, PlayerPredicate player, FishingPredicate fishingCondition, @Nullable String team, @Nullable ResourceLocation catType)
    {
        this.type = type;
        this.distance = distance;
        this.location = location;
        this.effects = effects;
        this.nbt = nbt;
        this.flags = flags;
        this.equipment = equipment;
        this.player = player;
        this.fishingCondition = fishingCondition;
        this.mountCondition = this;
        this.targetCondition = this;
        this.team = team;
        this.catType = catType;
    }

    private EntityPredicate(EntityTypePredicate type, DistancePredicate distance, LocationPredicate location, MobEffectsPredicate effects, NBTPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment, PlayerPredicate player, FishingPredicate fishingCondition, EntityPredicate mountCondition, EntityPredicate targetCondition, @Nullable String team, @Nullable ResourceLocation catType)
    {
        this.type = type;
        this.distance = distance;
        this.location = location;
        this.effects = effects;
        this.nbt = nbt;
        this.flags = flags;
        this.equipment = equipment;
        this.player = player;
        this.fishingCondition = fishingCondition;
        this.mountCondition = mountCondition;
        this.targetCondition = targetCondition;
        this.team = team;
        this.catType = catType;
    }

    public boolean test(ServerPlayerEntity player, @Nullable Entity entity)
    {
        return this.test(player.getServerWorld(), player.getPositionVec(), entity);
    }

    public boolean test(ServerWorld world, @Nullable Vector3d vector, @Nullable Entity entity)
    {
        if (this == ANY)
        {
            return true;
        }
        else if (entity == null)
        {
            return false;
        }
        else if (!this.type.test(entity.getType()))
        {
            return false;
        }
        else
        {
            if (vector == null)
            {
                if (this.distance != DistancePredicate.ANY)
                {
                    return false;
                }
            }
            else if (!this.distance.test(vector.x, vector.y, vector.z, entity.getPosX(), entity.getPosY(), entity.getPosZ()))
            {
                return false;
            }

            if (!this.location.test(world, entity.getPosX(), entity.getPosY(), entity.getPosZ()))
            {
                return false;
            }
            else if (!this.effects.test(entity))
            {
                return false;
            }
            else if (!this.nbt.test(entity))
            {
                return false;
            }
            else if (!this.flags.test(entity))
            {
                return false;
            }
            else if (!this.equipment.test(entity))
            {
                return false;
            }
            else if (!this.player.test(entity))
            {
                return false;
            }
            else if (!this.fishingCondition.func_234638_a_(entity))
            {
                return false;
            }
            else if (!this.mountCondition.test(world, vector, entity.getRidingEntity()))
            {
                return false;
            }
            else if (!this.targetCondition.test(world, vector, entity instanceof MobEntity ? ((MobEntity)entity).getAttackTarget() : null))
            {
                return false;
            }
            else
            {
                if (this.team != null)
                {
                    Team team = entity.getTeam();

                    if (team == null || !this.team.equals(team.getName()))
                    {
                        return false;
                    }
                }

                return this.catType == null || entity instanceof CatEntity && ((CatEntity)entity).getCatTypeName().equals(this.catType);
            }
        }
    }

    public static EntityPredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "entity");
            EntityTypePredicate entitytypepredicate = EntityTypePredicate.deserialize(jsonobject.get("type"));
            DistancePredicate distancepredicate = DistancePredicate.deserialize(jsonobject.get("distance"));
            LocationPredicate locationpredicate = LocationPredicate.deserialize(jsonobject.get("location"));
            MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.deserialize(jsonobject.get("effects"));
            NBTPredicate nbtpredicate = NBTPredicate.deserialize(jsonobject.get("nbt"));
            EntityFlagsPredicate entityflagspredicate = EntityFlagsPredicate.deserialize(jsonobject.get("flags"));
            EntityEquipmentPredicate entityequipmentpredicate = EntityEquipmentPredicate.deserialize(jsonobject.get("equipment"));
            PlayerPredicate playerpredicate = PlayerPredicate.deserialize(jsonobject.get("player"));
            FishingPredicate fishingpredicate = FishingPredicate.func_234639_a_(jsonobject.get("fishing_hook"));
            EntityPredicate entitypredicate = deserialize(jsonobject.get("vehicle"));
            EntityPredicate entitypredicate1 = deserialize(jsonobject.get("targeted_entity"));
            String s = JSONUtils.getString(jsonobject, "team", (String)null);
            ResourceLocation resourcelocation = jsonobject.has("catType") ? new ResourceLocation(JSONUtils.getString(jsonobject, "catType")) : null;
            return (new EntityPredicate.Builder()).type(entitytypepredicate).distance(distancepredicate).location(locationpredicate).effects(mobeffectspredicate).nbt(nbtpredicate).flags(entityflagspredicate).equipment(entityequipmentpredicate).player(playerpredicate).fishing(fishingpredicate).team(s).mount(entitypredicate).target(entitypredicate1).catTypeOrNull(resourcelocation).build();
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
            jsonobject.add("type", this.type.serialize());
            jsonobject.add("distance", this.distance.serialize());
            jsonobject.add("location", this.location.serialize());
            jsonobject.add("effects", this.effects.serialize());
            jsonobject.add("nbt", this.nbt.serialize());
            jsonobject.add("flags", this.flags.serialize());
            jsonobject.add("equipment", this.equipment.serialize());
            jsonobject.add("player", this.player.serialize());
            jsonobject.add("fishing_hook", this.fishingCondition.func_234637_a_());
            jsonobject.add("vehicle", this.mountCondition.serialize());
            jsonobject.add("targeted_entity", this.targetCondition.serialize());
            jsonobject.addProperty("team", this.team);

            if (this.catType != null)
            {
                jsonobject.addProperty("catType", this.catType.toString());
            }

            return jsonobject;
        }
    }

    public static LootContext getLootContext(ServerPlayerEntity player, Entity entity)
    {
        return (new LootContext.Builder(player.getServerWorld())).withParameter(LootParameters.THIS_ENTITY, entity).withParameter(LootParameters.field_237457_g_, player.getPositionVec()).withRandom(player.getRNG()).build(LootParameterSets.field_237454_j_);
    }

    public static class AndPredicate
    {
        public static final EntityPredicate.AndPredicate ANY_AND = new EntityPredicate.AndPredicate(new ILootCondition[0]);
        private final ILootCondition[] lootConditions;
        private final Predicate<LootContext> lootContext;

        private AndPredicate(ILootCondition[] lootConditions)
        {
            this.lootConditions = lootConditions;
            this.lootContext = LootConditionManager.and(lootConditions);
        }

        public static EntityPredicate.AndPredicate serializePredicate(ILootCondition... conditions)
        {
            return new EntityPredicate.AndPredicate(conditions);
        }

        public static EntityPredicate.AndPredicate deserializeJSONObject(JsonObject jsonObject, String name, ConditionArrayParser conditions)
        {
            JsonElement jsonelement = jsonObject.get(name);
            return fromJSONElement(name, conditions, jsonelement);
        }

        public static EntityPredicate.AndPredicate[] deserialize(JsonObject jsonObject, String name, ConditionArrayParser conditions)
        {
            JsonElement jsonelement = jsonObject.get(name);

            if (jsonelement != null && !jsonelement.isJsonNull())
            {
                JsonArray jsonarray = JSONUtils.getJsonArray(jsonelement, name);
                EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = new EntityPredicate.AndPredicate[jsonarray.size()];

                for (int i = 0; i < jsonarray.size(); ++i)
                {
                    aentitypredicate$andpredicate[i] = fromJSONElement(name + "[" + i + "]", conditions, jsonarray.get(i));
                }

                return aentitypredicate$andpredicate;
            }
            else
            {
                return new EntityPredicate.AndPredicate[0];
            }
        }

        private static EntityPredicate.AndPredicate fromJSONElement(String name, ConditionArrayParser conditions, @Nullable JsonElement element)
        {
            if (element != null && element.isJsonArray())
            {
                ILootCondition[] ailootcondition = conditions.func_234050_a_(element.getAsJsonArray(), conditions.func_234049_a_().toString() + "/" + name, LootParameterSets.field_237454_j_);
                return new EntityPredicate.AndPredicate(ailootcondition);
            }
            else
            {
                EntityPredicate entitypredicate = EntityPredicate.deserialize(element);
                return createAndFromEntityCondition(entitypredicate);
            }
        }

        public static EntityPredicate.AndPredicate createAndFromEntityCondition(EntityPredicate entityCondition)
        {
            if (entityCondition == EntityPredicate.ANY)
            {
                return ANY_AND;
            }
            else
            {
                ILootCondition ilootcondition = EntityHasProperty.func_237477_a_(LootContext.EntityTarget.THIS, entityCondition).build();
                return new EntityPredicate.AndPredicate(new ILootCondition[] {ilootcondition});
            }
        }

        public boolean testContext(LootContext context)
        {
            return this.lootContext.test(context);
        }

        public JsonElement serializeConditions(ConditionArraySerializer serializer)
        {
            return (JsonElement)(this.lootConditions.length == 0 ? JsonNull.INSTANCE : serializer.func_235681_a_(this.lootConditions));
        }

        public static JsonElement serializeConditionsIn(EntityPredicate.AndPredicate[] predicates, ConditionArraySerializer serializer)
        {
            if (predicates.length == 0)
            {
                return JsonNull.INSTANCE;
            }
            else
            {
                JsonArray jsonarray = new JsonArray();

                for (EntityPredicate.AndPredicate entitypredicate$andpredicate : predicates)
                {
                    jsonarray.add(entitypredicate$andpredicate.serializeConditions(serializer));
                }

                return jsonarray;
            }
        }
    }

    public static class Builder
    {
        private EntityTypePredicate type = EntityTypePredicate.ANY;
        private DistancePredicate distance = DistancePredicate.ANY;
        private LocationPredicate location = LocationPredicate.ANY;
        private MobEffectsPredicate effects = MobEffectsPredicate.ANY;
        private NBTPredicate nbt = NBTPredicate.ANY;
        private EntityFlagsPredicate flags = EntityFlagsPredicate.ALWAYS_TRUE;
        private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
        private PlayerPredicate player = PlayerPredicate.ANY;
        private FishingPredicate fishing = FishingPredicate.field_234635_a_;
        private EntityPredicate mount = EntityPredicate.ANY;
        private EntityPredicate target = EntityPredicate.ANY;
        private String team;
        private ResourceLocation catType;

        public static EntityPredicate.Builder create()
        {
            return new EntityPredicate.Builder();
        }

        public EntityPredicate.Builder type(EntityType<?> typeIn)
        {
            this.type = EntityTypePredicate.fromType(typeIn);
            return this;
        }

        public EntityPredicate.Builder type(ITag < EntityType<? >> typeIn)
        {
            this.type = EntityTypePredicate.fromTag(typeIn);
            return this;
        }

        public EntityPredicate.Builder catType(ResourceLocation catTypeIn)
        {
            this.catType = catTypeIn;
            return this;
        }

        public EntityPredicate.Builder type(EntityTypePredicate typeIn)
        {
            this.type = typeIn;
            return this;
        }

        public EntityPredicate.Builder distance(DistancePredicate distanceIn)
        {
            this.distance = distanceIn;
            return this;
        }

        public EntityPredicate.Builder location(LocationPredicate locationIn)
        {
            this.location = locationIn;
            return this;
        }

        public EntityPredicate.Builder effects(MobEffectsPredicate effectsIn)
        {
            this.effects = effectsIn;
            return this;
        }

        public EntityPredicate.Builder nbt(NBTPredicate nbtIn)
        {
            this.nbt = nbtIn;
            return this;
        }

        public EntityPredicate.Builder flags(EntityFlagsPredicate flagsIn)
        {
            this.flags = flagsIn;
            return this;
        }

        public EntityPredicate.Builder equipment(EntityEquipmentPredicate equipmentIn)
        {
            this.equipment = equipmentIn;
            return this;
        }

        public EntityPredicate.Builder player(PlayerPredicate player)
        {
            this.player = player;
            return this;
        }

        public EntityPredicate.Builder fishing(FishingPredicate fishing)
        {
            this.fishing = fishing;
            return this;
        }

        public EntityPredicate.Builder mount(EntityPredicate mount)
        {
            this.mount = mount;
            return this;
        }

        public EntityPredicate.Builder target(EntityPredicate target)
        {
            this.target = target;
            return this;
        }

        public EntityPredicate.Builder team(@Nullable String team)
        {
            this.team = team;
            return this;
        }

        public EntityPredicate.Builder catTypeOrNull(@Nullable ResourceLocation catTypeIn)
        {
            this.catType = catTypeIn;
            return this;
        }

        public EntityPredicate build()
        {
            return new EntityPredicate(this.type, this.distance, this.location, this.effects, this.nbt, this.flags, this.equipment, this.player, this.fishing, this.mount, this.target, this.team, this.catType);
        }
    }
}
