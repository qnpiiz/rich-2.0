package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.Property;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemParser
{
    public static final SimpleCommandExceptionType ITEM_TAGS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.item.tag.disallowed"));
    public static final DynamicCommandExceptionType ITEM_BAD_ID = new DynamicCommandExceptionType((p_208696_0_) ->
    {
        return new TranslationTextComponent("argument.item.id.invalid", p_208696_0_);
    });
    private static final BiFunction<SuggestionsBuilder, ITagCollection<Item>, CompletableFuture<Suggestions>> DEFAULT_SUGGESTIONS_BUILDER = (p_239571_0_, p_239571_1_) ->
    {
        return p_239571_0_.buildFuture();
    };
    private final StringReader reader;
    private final boolean allowTags;
    private final Map < Property<?>, Comparable<? >> field_197336_d = Maps.newHashMap();
    private Item item;
    @Nullable
    private CompoundNBT nbt;
    private ResourceLocation tag = new ResourceLocation("");
    private int readerCursor;
    private BiFunction<SuggestionsBuilder, ITagCollection<Item>, CompletableFuture<Suggestions>> suggestionsBuilder = DEFAULT_SUGGESTIONS_BUILDER;

    public ItemParser(StringReader readerIn, boolean allowTags)
    {
        this.reader = readerIn;
        this.allowTags = allowTags;
    }

    public Item getItem()
    {
        return this.item;
    }

    @Nullable
    public CompoundNBT getNbt()
    {
        return this.nbt;
    }

    public ResourceLocation getTag()
    {
        return this.tag;
    }

    public void readItem() throws CommandSyntaxException
    {
        int i = this.reader.getCursor();
        ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
        this.item = Registry.ITEM.getOptional(resourcelocation).orElseThrow(() ->
        {
            this.reader.setCursor(i);
            return ITEM_BAD_ID.createWithContext(this.reader, resourcelocation.toString());
        });
    }

    public void readTag() throws CommandSyntaxException
    {
        if (!this.allowTags)
        {
            throw ITEM_TAGS_NOT_ALLOWED.create();
        }
        else
        {
            this.suggestionsBuilder = this::suggestTag;
            this.reader.expect('#');
            this.readerCursor = this.reader.getCursor();
            this.tag = ResourceLocation.read(this.reader);
        }
    }

    public void readNBT() throws CommandSyntaxException
    {
        this.nbt = (new JsonToNBT(this.reader)).readStruct();
    }

    public ItemParser parse() throws CommandSyntaxException
    {
        this.suggestionsBuilder = this::suggestTagOrItem;

        if (this.reader.canRead() && this.reader.peek() == '#')
        {
            this.readTag();
        }
        else
        {
            this.readItem();
            this.suggestionsBuilder = this::suggestItem;
        }

        if (this.reader.canRead() && this.reader.peek() == '{')
        {
            this.suggestionsBuilder = DEFAULT_SUGGESTIONS_BUILDER;
            this.readNBT();
        }

        return this;
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder builder, ITagCollection<Item> p_197328_2_)
    {
        if (builder.getRemaining().isEmpty())
        {
            builder.suggest(String.valueOf('{'));
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder, ITagCollection<Item> p_201955_2_)
    {
        return ISuggestionProvider.suggestIterable(p_201955_2_.getRegisteredTags(), builder.createOffset(this.readerCursor));
    }

    private CompletableFuture<Suggestions> suggestTagOrItem(SuggestionsBuilder builder, ITagCollection<Item> p_197331_2_)
    {
        if (this.allowTags)
        {
            ISuggestionProvider.suggestIterable(p_197331_2_.getRegisteredTags(), builder, String.valueOf('#'));
        }

        return ISuggestionProvider.suggestIterable(Registry.ITEM.keySet(), builder);
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder, ITagCollection<Item> p_197329_2_)
    {
        return this.suggestionsBuilder.apply(builder.createOffset(this.reader.getCursor()), p_197329_2_);
    }
}
