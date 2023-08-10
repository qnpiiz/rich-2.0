package net.minecraft.util.text.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoverEvent
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final HoverEvent.Action<?> action;
    private final Object value;

    public <T> HoverEvent(HoverEvent.Action<T> action, T value)
    {
        this.action = action;
        this.value = value;
    }

    public HoverEvent.Action<?> getAction()
    {
        return this.action;
    }

    @Nullable
    public <T> T getParameter(HoverEvent.Action<T> actionType)
    {
        return (T)(this.action == actionType ? actionType.castParameter(this.value) : null);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            HoverEvent hoverevent = (HoverEvent)p_equals_1_;
            return this.action == hoverevent.action && Objects.equals(this.value, hoverevent.value);
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode()
    {
        int i = this.action.hashCode();
        return 31 * i + (this.value != null ? this.value.hashCode() : 0);
    }

    @Nullable
    public static HoverEvent deserialize(JsonObject json)
    {
        String s = JSONUtils.getString(json, "action", (String)null);

        if (s == null)
        {
            return null;
        }
        else
        {
            HoverEvent.Action<?> action = HoverEvent.Action.getValueByCanonicalName(s);

            if (action == null)
            {
                return null;
            }
            else
            {
                JsonElement jsonelement = json.get("contents");

                if (jsonelement != null)
                {
                    return action.deserialize(jsonelement);
                }
                else
                {
                    ITextComponent itextcomponent = ITextComponent.Serializer.getComponentFromJson(json.get("value"));
                    return itextcomponent != null ? action.deserialize(itextcomponent) : null;
                }
            }
        }
    }

    public JsonObject serialize()
    {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("action", this.action.getCanonicalName());
        jsonobject.add("contents", this.action.serialize(this.value));
        return jsonobject;
    }

    public static class Action<T>
    {
        public static final HoverEvent.Action<ITextComponent> SHOW_TEXT = new HoverEvent.Action<>("show_text", true, ITextComponent.Serializer::getComponentFromJson, ITextComponent.Serializer::toJsonTree, Function.identity());
        public static final HoverEvent.Action<HoverEvent.ItemHover> SHOW_ITEM = new HoverEvent.Action<>("show_item", true, (element) ->
        {
            return HoverEvent.ItemHover.deserialize(element);
        }, (hover) ->
        {
            return hover.serialize();
        }, (component) ->
        {
            return HoverEvent.ItemHover.deserialize(component);
        });
        public static final HoverEvent.Action<HoverEvent.EntityHover> SHOW_ENTITY = new HoverEvent.Action<>("show_entity", true, HoverEvent.EntityHover::deserialize, HoverEvent.EntityHover::serialize, HoverEvent.EntityHover::deserialize);
        private static final Map<String, HoverEvent.Action> NAME_MAPPING = Stream.of(SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY).collect(ImmutableMap.toImmutableMap(HoverEvent.Action::getCanonicalName, (action) ->
        {
            return action;
        }));
        private final String canonicalName;
        private final boolean allowedInChat;
        private final Function<JsonElement, T> deserializeFromJSON;
        private final Function<T, JsonElement> serializeToJSON;
        private final Function<ITextComponent, T> deserializeFromTextComponent;

        public Action(String canonicalName, boolean allowedInChat, Function<JsonElement, T> deserializeFromJSON, Function<T, JsonElement> serializeToJSON, Function<ITextComponent, T> deserializeFromTextComponent)
        {
            this.canonicalName = canonicalName;
            this.allowedInChat = allowedInChat;
            this.deserializeFromJSON = deserializeFromJSON;
            this.serializeToJSON = serializeToJSON;
            this.deserializeFromTextComponent = deserializeFromTextComponent;
        }

        public boolean shouldAllowInChat()
        {
            return this.allowedInChat;
        }

        public String getCanonicalName()
        {
            return this.canonicalName;
        }

        @Nullable
        public static HoverEvent.Action getValueByCanonicalName(String canonicalNameIn)
        {
            return NAME_MAPPING.get(canonicalNameIn);
        }

        private T castParameter(Object parameter)
        {
            return (T)parameter;
        }

        @Nullable
        public HoverEvent deserialize(JsonElement element)
        {
            T t = this.deserializeFromJSON.apply(element);
            return t == null ? null : new HoverEvent(this, t);
        }

        @Nullable
        public HoverEvent deserialize(ITextComponent component)
        {
            T t = this.deserializeFromTextComponent.apply(component);
            return t == null ? null : new HoverEvent(this, t);
        }

        public JsonElement serialize(Object parameter)
        {
            return this.serializeToJSON.apply(this.castParameter(parameter));
        }

        public String toString()
        {
            return "<action " + this.canonicalName + ">";
        }
    }

    public static class EntityHover
    {
        public final EntityType<?> type;
        public final UUID id;
        @Nullable
        public final ITextComponent name;
        @Nullable
        private List<ITextComponent> tooltip;

        public EntityHover(EntityType<?> type, UUID id, @Nullable ITextComponent name)
        {
            this.type = type;
            this.id = id;
            this.name = name;
        }

        @Nullable
        public static HoverEvent.EntityHover deserialize(JsonElement element)
        {
            if (!element.isJsonObject())
            {
                return null;
            }
            else
            {
                JsonObject jsonobject = element.getAsJsonObject();
                EntityType<?> entitytype = Registry.ENTITY_TYPE.getOrDefault(new ResourceLocation(JSONUtils.getString(jsonobject, "type")));
                UUID uuid = UUID.fromString(JSONUtils.getString(jsonobject, "id"));
                ITextComponent itextcomponent = ITextComponent.Serializer.getComponentFromJson(jsonobject.get("name"));
                return new HoverEvent.EntityHover(entitytype, uuid, itextcomponent);
            }
        }

        @Nullable
        public static HoverEvent.EntityHover deserialize(ITextComponent component)
        {
            try
            {
                CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(component.getString());
                ITextComponent itextcomponent = ITextComponent.Serializer.getComponentFromJson(compoundnbt.getString("name"));
                EntityType<?> entitytype = Registry.ENTITY_TYPE.getOrDefault(new ResourceLocation(compoundnbt.getString("type")));
                UUID uuid = UUID.fromString(compoundnbt.getString("id"));
                return new HoverEvent.EntityHover(entitytype, uuid, itextcomponent);
            }
            catch (CommandSyntaxException | JsonSyntaxException jsonsyntaxexception)
            {
                return null;
            }
        }

        public JsonElement serialize()
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("type", Registry.ENTITY_TYPE.getKey(this.type).toString());
            jsonobject.addProperty("id", this.id.toString());

            if (this.name != null)
            {
                jsonobject.add("name", ITextComponent.Serializer.toJsonTree(this.name));
            }

            return jsonobject;
        }

        public List<ITextComponent> getTooltip()
        {
            if (this.tooltip == null)
            {
                this.tooltip = Lists.newArrayList();

                if (this.name != null)
                {
                    this.tooltip.add(this.name);
                }

                this.tooltip.add(new TranslationTextComponent("gui.entity_tooltip.type", this.type.getName()));
                this.tooltip.add(new StringTextComponent(this.id.toString()));
            }

            return this.tooltip;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
            {
                HoverEvent.EntityHover hoverevent$entityhover = (HoverEvent.EntityHover)p_equals_1_;
                return this.type.equals(hoverevent$entityhover.type) && this.id.equals(hoverevent$entityhover.id) && Objects.equals(this.name, hoverevent$entityhover.name);
            }
            else
            {
                return false;
            }
        }

        public int hashCode()
        {
            int i = this.type.hashCode();
            i = 31 * i + this.id.hashCode();
            return 31 * i + (this.name != null ? this.name.hashCode() : 0);
        }
    }

    public static class ItemHover
    {
        private final Item item;
        private final int count;
        @Nullable
        private final CompoundNBT tag;
        @Nullable
        private ItemStack stack;

        ItemHover(Item item, int count, @Nullable CompoundNBT tag)
        {
            this.item = item;
            this.count = count;
            this.tag = tag;
        }

        public ItemHover(ItemStack stack)
        {
            this(stack.getItem(), stack.getCount(), stack.getTag() != null ? stack.getTag().copy() : null);
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
            {
                HoverEvent.ItemHover hoverevent$itemhover = (HoverEvent.ItemHover)p_equals_1_;
                return this.count == hoverevent$itemhover.count && this.item.equals(hoverevent$itemhover.item) && Objects.equals(this.tag, hoverevent$itemhover.tag);
            }
            else
            {
                return false;
            }
        }

        public int hashCode()
        {
            int i = this.item.hashCode();
            i = 31 * i + this.count;
            return 31 * i + (this.tag != null ? this.tag.hashCode() : 0);
        }

        public ItemStack createStack()
        {
            if (this.stack == null)
            {
                this.stack = new ItemStack(this.item, this.count);

                if (this.tag != null)
                {
                    this.stack.setTag(this.tag);
                }
            }

            return this.stack;
        }

        private static HoverEvent.ItemHover deserialize(JsonElement element)
        {
            if (element.isJsonPrimitive())
            {
                return new HoverEvent.ItemHover(Registry.ITEM.getOrDefault(new ResourceLocation(element.getAsString())), 1, (CompoundNBT)null);
            }
            else
            {
                JsonObject jsonobject = JSONUtils.getJsonObject(element, "item");
                Item item = Registry.ITEM.getOrDefault(new ResourceLocation(JSONUtils.getString(jsonobject, "id")));
                int i = JSONUtils.getInt(jsonobject, "count", 1);

                if (jsonobject.has("tag"))
                {
                    String s = JSONUtils.getString(jsonobject, "tag");

                    try
                    {
                        CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(s);
                        return new HoverEvent.ItemHover(item, i, compoundnbt);
                    }
                    catch (CommandSyntaxException commandsyntaxexception)
                    {
                        HoverEvent.LOGGER.warn("Failed to parse tag: {}", s, commandsyntaxexception);
                    }
                }

                return new HoverEvent.ItemHover(item, i, (CompoundNBT)null);
            }
        }

        @Nullable
        private static HoverEvent.ItemHover deserialize(ITextComponent component)
        {
            try
            {
                CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(component.getString());
                return new HoverEvent.ItemHover(ItemStack.read(compoundnbt));
            }
            catch (CommandSyntaxException commandsyntaxexception)
            {
                HoverEvent.LOGGER.warn("Failed to parse item tag: {}", component, commandsyntaxexception);
                return null;
            }
        }

        private JsonElement serialize()
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("id", Registry.ITEM.getKey(this.item).toString());

            if (this.count != 1)
            {
                jsonobject.addProperty("count", this.count);
            }

            if (this.tag != null)
            {
                jsonobject.addProperty("tag", this.tag.toString());
            }

            return jsonobject;
        }
    }
}
