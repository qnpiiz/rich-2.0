package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public final class Ingredient implements Predicate<ItemStack>
{
    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final Ingredient.IItemList[] acceptedItems;
    private ItemStack[] matchingStacks;
    private IntList matchingStacksPacked;

    private Ingredient(Stream <? extends Ingredient.IItemList > itemLists)
    {
        this.acceptedItems = itemLists.toArray((size) ->
        {
            return new Ingredient.IItemList[size];
        });
    }

    public ItemStack[] getMatchingStacks()
    {
        this.determineMatchingStacks();
        return this.matchingStacks;
    }

    private void determineMatchingStacks()
    {
        if (this.matchingStacks == null)
        {
            this.matchingStacks = Arrays.stream(this.acceptedItems).flatMap((ingredientList) ->
            {
                return ingredientList.getStacks().stream();
            }).distinct().toArray((size) ->
            {
                return new ItemStack[size];
            });
        }
    }

    public boolean test(@Nullable ItemStack p_test_1_)
    {
        if (p_test_1_ == null)
        {
            return false;
        }
        else
        {
            this.determineMatchingStacks();

            if (this.matchingStacks.length == 0)
            {
                return p_test_1_.isEmpty();
            }
            else
            {
                for (ItemStack itemstack : this.matchingStacks)
                {
                    if (itemstack.getItem() == p_test_1_.getItem())
                    {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public IntList getValidItemStacksPacked()
    {
        if (this.matchingStacksPacked == null)
        {
            this.determineMatchingStacks();
            this.matchingStacksPacked = new IntArrayList(this.matchingStacks.length);

            for (ItemStack itemstack : this.matchingStacks)
            {
                this.matchingStacksPacked.add(RecipeItemHelper.pack(itemstack));
            }

            this.matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.matchingStacksPacked;
    }

    public void write(PacketBuffer buffer)
    {
        this.determineMatchingStacks();
        buffer.writeVarInt(this.matchingStacks.length);

        for (int i = 0; i < this.matchingStacks.length; ++i)
        {
            buffer.writeItemStack(this.matchingStacks[i]);
        }
    }

    public JsonElement serialize()
    {
        if (this.acceptedItems.length == 1)
        {
            return this.acceptedItems[0].serialize();
        }
        else
        {
            JsonArray jsonarray = new JsonArray();

            for (Ingredient.IItemList ingredient$iitemlist : this.acceptedItems)
            {
                jsonarray.add(ingredient$iitemlist.serialize());
            }

            return jsonarray;
        }
    }

    public boolean hasNoMatchingItems()
    {
        return this.acceptedItems.length == 0 && (this.matchingStacks == null || this.matchingStacks.length == 0) && (this.matchingStacksPacked == null || this.matchingStacksPacked.isEmpty());
    }

    private static Ingredient fromItemListStream(Stream <? extends Ingredient.IItemList > stream)
    {
        Ingredient ingredient = new Ingredient(stream);
        return ingredient.acceptedItems.length == 0 ? EMPTY : ingredient;
    }

    public static Ingredient fromItems(IItemProvider... itemsIn)
    {
        return fromStacks(Arrays.stream(itemsIn).map(ItemStack::new));
    }

    public static Ingredient fromStacks(ItemStack... stacks)
    {
        return fromStacks(Arrays.stream(stacks));
    }

    public static Ingredient fromStacks(Stream<ItemStack> stacks)
    {
        return fromItemListStream(stacks.filter((stack) ->
        {
            return !stack.isEmpty();
        }).map((stack) ->
        {
            return new Ingredient.SingleItemList(stack);
        }));
    }

    public static Ingredient fromTag(ITag<Item> tagIn)
    {
        return fromItemListStream(Stream.of(new Ingredient.TagList(tagIn)));
    }

    public static Ingredient read(PacketBuffer buffer)
    {
        int i = buffer.readVarInt();
        return fromItemListStream(Stream.generate(() ->
        {
            return new Ingredient.SingleItemList(buffer.readItemStack());
        }).limit((long)i));
    }

    public static Ingredient deserialize(@Nullable JsonElement json)
    {
        if (json != null && !json.isJsonNull())
        {
            if (json.isJsonObject())
            {
                return fromItemListStream(Stream.of(deserializeItemList(json.getAsJsonObject())));
            }
            else if (json.isJsonArray())
            {
                JsonArray jsonarray = json.getAsJsonArray();

                if (jsonarray.size() == 0)
                {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                }
                else
                {
                    return fromItemListStream(StreamSupport.stream(jsonarray.spliterator(), false).map((element) ->
                    {
                        return deserializeItemList(JSONUtils.getJsonObject(element, "item"));
                    }));
                }
            }
            else
            {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        }
        else
        {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    private static Ingredient.IItemList deserializeItemList(JsonObject json)
    {
        if (json.has("item") && json.has("tag"))
        {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        else if (json.has("item"))
        {
            ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(json, "item"));
            Item item = Registry.ITEM.getOptional(resourcelocation1).orElseThrow(() ->
            {
                return new JsonSyntaxException("Unknown item '" + resourcelocation1 + "'");
            });
            return new Ingredient.SingleItemList(new ItemStack(item));
        }
        else if (json.has("tag"))
        {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "tag"));
            ITag<Item> itag = TagCollectionManager.getManager().getItemTags().get(resourcelocation);

            if (itag == null)
            {
                throw new JsonSyntaxException("Unknown item tag '" + resourcelocation + "'");
            }
            else
            {
                return new Ingredient.TagList(itag);
            }
        }
        else
        {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

    interface IItemList
    {
        Collection<ItemStack> getStacks();

        JsonObject serialize();
    }

    static class SingleItemList implements Ingredient.IItemList
    {
        private final ItemStack stack;

        private SingleItemList(ItemStack stackIn)
        {
            this.stack = stackIn;
        }

        public Collection<ItemStack> getStacks()
        {
            return Collections.singleton(this.stack);
        }

        public JsonObject serialize()
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.stack.getItem()).toString());
            return jsonobject;
        }
    }

    static class TagList implements Ingredient.IItemList
    {
        private final ITag<Item> tag;

        private TagList(ITag<Item> tagIn)
        {
            this.tag = tagIn;
        }

        public Collection<ItemStack> getStacks()
        {
            List<ItemStack> list = Lists.newArrayList();

            for (Item item : this.tag.getAllElements())
            {
                list.add(new ItemStack(item));
            }

            return list;
        }

        public JsonObject serialize()
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", TagCollectionManager.getManager().getItemTags().getValidatedIdFromTag(this.tag).toString());
            return jsonobject;
        }
    }
}
