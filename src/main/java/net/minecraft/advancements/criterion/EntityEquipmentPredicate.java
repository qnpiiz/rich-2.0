package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.raid.Raid;

public class EntityEquipmentPredicate
{
    public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    public static final EntityEquipmentPredicate WEARING_ILLAGER_BANNER = new EntityEquipmentPredicate(ItemPredicate.Builder.create().item(Items.WHITE_BANNER).nbt(Raid.createIllagerBanner().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    private final ItemPredicate head;
    private final ItemPredicate chest;
    private final ItemPredicate legs;
    private final ItemPredicate feet;
    private final ItemPredicate mainHand;
    private final ItemPredicate offHand;

    public EntityEquipmentPredicate(ItemPredicate head, ItemPredicate chest, ItemPredicate legs, ItemPredicate feet, ItemPredicate mainHand, ItemPredicate offHand)
    {
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.mainHand = mainHand;
        this.offHand = offHand;
    }

    public boolean test(@Nullable Entity entity)
    {
        if (this == ANY)
        {
            return true;
        }
        else if (!(entity instanceof LivingEntity))
        {
            return false;
        }
        else
        {
            LivingEntity livingentity = (LivingEntity)entity;

            if (!this.head.test(livingentity.getItemStackFromSlot(EquipmentSlotType.HEAD)))
            {
                return false;
            }
            else if (!this.chest.test(livingentity.getItemStackFromSlot(EquipmentSlotType.CHEST)))
            {
                return false;
            }
            else if (!this.legs.test(livingentity.getItemStackFromSlot(EquipmentSlotType.LEGS)))
            {
                return false;
            }
            else if (!this.feet.test(livingentity.getItemStackFromSlot(EquipmentSlotType.FEET)))
            {
                return false;
            }
            else if (!this.mainHand.test(livingentity.getItemStackFromSlot(EquipmentSlotType.MAINHAND)))
            {
                return false;
            }
            else
            {
                return this.offHand.test(livingentity.getItemStackFromSlot(EquipmentSlotType.OFFHAND));
            }
        }
    }

    public static EntityEquipmentPredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "equipment");
            ItemPredicate itempredicate = ItemPredicate.deserialize(jsonobject.get("head"));
            ItemPredicate itempredicate1 = ItemPredicate.deserialize(jsonobject.get("chest"));
            ItemPredicate itempredicate2 = ItemPredicate.deserialize(jsonobject.get("legs"));
            ItemPredicate itempredicate3 = ItemPredicate.deserialize(jsonobject.get("feet"));
            ItemPredicate itempredicate4 = ItemPredicate.deserialize(jsonobject.get("mainhand"));
            ItemPredicate itempredicate5 = ItemPredicate.deserialize(jsonobject.get("offhand"));
            return new EntityEquipmentPredicate(itempredicate, itempredicate1, itempredicate2, itempredicate3, itempredicate4, itempredicate5);
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
            jsonobject.add("head", this.head.serialize());
            jsonobject.add("chest", this.chest.serialize());
            jsonobject.add("legs", this.legs.serialize());
            jsonobject.add("feet", this.feet.serialize());
            jsonobject.add("mainhand", this.mainHand.serialize());
            jsonobject.add("offhand", this.offHand.serialize());
            return jsonobject;
        }
    }

    public static class Builder
    {
        private ItemPredicate head = ItemPredicate.ANY;
        private ItemPredicate chest = ItemPredicate.ANY;
        private ItemPredicate legs = ItemPredicate.ANY;
        private ItemPredicate feet = ItemPredicate.ANY;
        private ItemPredicate mainHand = ItemPredicate.ANY;
        private ItemPredicate offHand = ItemPredicate.ANY;

        public static EntityEquipmentPredicate.Builder createBuilder()
        {
            return new EntityEquipmentPredicate.Builder();
        }

        public EntityEquipmentPredicate.Builder setHeadCondition(ItemPredicate condition)
        {
            this.head = condition;
            return this;
        }

        public EntityEquipmentPredicate.Builder setChestCondition(ItemPredicate condition)
        {
            this.chest = condition;
            return this;
        }

        public EntityEquipmentPredicate.Builder setLegsCondition(ItemPredicate condition)
        {
            this.legs = condition;
            return this;
        }

        public EntityEquipmentPredicate.Builder setFeetCondition(ItemPredicate condition)
        {
            this.feet = condition;
            return this;
        }

        public EntityEquipmentPredicate build()
        {
            return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainHand, this.offHand);
        }
    }
}
