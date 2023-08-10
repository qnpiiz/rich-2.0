package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class TagLootEntry extends StandaloneLootEntry
{
    private final ITag<Item> tag;
    private final boolean expand;

    private TagLootEntry(ITag<Item> tag, boolean expand, int weight, int quality, ILootCondition[] conditions, ILootFunction[] functions)
    {
        super(weight, quality, conditions, functions);
        this.tag = tag;
        this.expand = expand;
    }

    public LootPoolEntryType func_230420_a_()
    {
        return LootEntryManager.TAG;
    }

    public void func_216154_a(Consumer<ItemStack> stackConsumer, LootContext context)
    {
        this.tag.getAllElements().forEach((item) ->
        {
            stackConsumer.accept(new ItemStack(item));
        });
    }

    private boolean generateLoot(LootContext context, Consumer<ILootGenerator> generatorConsumer)
    {
        if (!this.test(context))
        {
            return false;
        }
        else
        {
            for (final Item item : this.tag.getAllElements())
            {
                generatorConsumer.accept(new StandaloneLootEntry.Generator()
                {
                    public void func_216188_a(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_)
                    {
                        p_216188_1_.accept(new ItemStack(item));
                    }
                });
            }

            return true;
        }
    }

    public boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_)
    {
        return this.expand ? this.generateLoot(p_expand_1_, p_expand_2_) : super.expand(p_expand_1_, p_expand_2_);
    }

    public static StandaloneLootEntry.Builder<?> getBuilder(ITag<Item> tag)
    {
        return builder((weight, quality, conditions, functions) ->
        {
            return new TagLootEntry(tag, true, weight, quality, conditions, functions);
        });
    }

    public static class Serializer extends StandaloneLootEntry.Serializer<TagLootEntry>
    {
        public void doSerialize(JsonObject object, TagLootEntry context, JsonSerializationContext conditions)
        {
            super.doSerialize(object, context, conditions);
            object.addProperty("name", TagCollectionManager.getManager().getItemTags().getValidatedIdFromTag(context.tag).toString());
            object.addProperty("expand", context.expand);
        }

        protected TagLootEntry deserialize(JsonObject object, JsonDeserializationContext context, int weight, int quality, ILootCondition[] conditions, ILootFunction[] functions)
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(object, "name"));
            ITag<Item> itag = TagCollectionManager.getManager().getItemTags().get(resourcelocation);

            if (itag == null)
            {
                throw new JsonParseException("Can't find tag: " + resourcelocation);
            }
            else
            {
                boolean flag = JSONUtils.getBoolean(object, "expand");
                return new TagLootEntry(itag, flag, weight, quality, conditions, functions);
            }
        }
    }
}
