package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockStateParser
{
    public static final SimpleCommandExceptionType STATE_TAGS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.block.tag.disallowed"));
    public static final DynamicCommandExceptionType STATE_BAD_ID = new DynamicCommandExceptionType((p_208687_0_) ->
    {
        return new TranslationTextComponent("argument.block.id.invalid", p_208687_0_);
    });
    public static final Dynamic2CommandExceptionType STATE_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((p_208685_0_, p_208685_1_) ->
    {
        return new TranslationTextComponent("argument.block.property.unknown", p_208685_0_, p_208685_1_);
    });
    public static final Dynamic2CommandExceptionType STATE_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((p_208690_0_, p_208690_1_) ->
    {
        return new TranslationTextComponent("argument.block.property.duplicate", p_208690_1_, p_208690_0_);
    });
    public static final Dynamic3CommandExceptionType STATE_INVALID_PROPERTY_VALUE = new Dynamic3CommandExceptionType((p_208684_0_, p_208684_1_, p_208684_2_) ->
    {
        return new TranslationTextComponent("argument.block.property.invalid", p_208684_0_, p_208684_2_, p_208684_1_);
    });
    public static final Dynamic2CommandExceptionType STATE_NO_VALUE = new Dynamic2CommandExceptionType((p_208689_0_, p_208689_1_) ->
    {
        return new TranslationTextComponent("argument.block.property.novalue", p_208689_0_, p_208689_1_);
    });
    public static final SimpleCommandExceptionType STATE_UNCLOSED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.block.property.unclosed"));
    private static final BiFunction<SuggestionsBuilder, ITagCollection<Block>, CompletableFuture<Suggestions>> SUGGEST_NONE = (p_239308_0_, p_239308_1_) ->
    {
        return p_239308_0_.buildFuture();
    };
    private final StringReader reader;
    private final boolean tagsAllowed;
    private final Map < Property<?>, Comparable<? >> properties = Maps.newHashMap();
    private final Map<String, String> stringProperties = Maps.newHashMap();
    private ResourceLocation blockID = new ResourceLocation("");
    private StateContainer<Block, BlockState> blockStateContainer;
    private BlockState state;
    @Nullable
    private CompoundNBT nbt;
    private ResourceLocation tag = new ResourceLocation("");
    private int cursorPos;
    private BiFunction<SuggestionsBuilder, ITagCollection<Block>, CompletableFuture<Suggestions>> suggestor = SUGGEST_NONE;

    public BlockStateParser(StringReader readerIn, boolean allowTags)
    {
        this.reader = readerIn;
        this.tagsAllowed = allowTags;
    }

    public Map < Property<?>, Comparable<? >> getProperties()
    {
        return this.properties;
    }

    @Nullable
    public BlockState getState()
    {
        return this.state;
    }

    @Nullable
    public CompoundNBT getNbt()
    {
        return this.nbt;
    }

    @Nullable
    public ResourceLocation getTag()
    {
        return this.tag;
    }

    public BlockStateParser parse(boolean parseTileEntity) throws CommandSyntaxException
    {
        this.suggestor = this::suggestTagOrBlock;

        if (this.reader.canRead() && this.reader.peek() == '#')
        {
            this.readTag();
            this.suggestor = this::func_212599_i;

            if (this.reader.canRead() && this.reader.peek() == '[')
            {
                this.readStringProperties();
                this.suggestor = this::suggestNbt;
            }
        }
        else
        {
            this.readBlock();
            this.suggestor = this::suggestPropertyOrNbt;

            if (this.reader.canRead() && this.reader.peek() == '[')
            {
                this.readProperties();
                this.suggestor = this::suggestNbt;
            }
        }

        if (parseTileEntity && this.reader.canRead() && this.reader.peek() == '{')
        {
            this.suggestor = SUGGEST_NONE;
            this.readNBT();
        }

        return this;
    }

    private CompletableFuture<Suggestions> suggestPropertyOrEnd(SuggestionsBuilder builder, ITagCollection<Block> p_197252_2_)
    {
        if (builder.getRemaining().isEmpty())
        {
            builder.suggest(String.valueOf(']'));
        }

        return this.suggestProperty(builder, p_197252_2_);
    }

    private CompletableFuture<Suggestions> suggestStringPropertyOrEnd(SuggestionsBuilder builder, ITagCollection<Block> p_200136_2_)
    {
        if (builder.getRemaining().isEmpty())
        {
            builder.suggest(String.valueOf(']'));
        }

        return this.suggestStringProperty(builder, p_200136_2_);
    }

    private CompletableFuture<Suggestions> suggestProperty(SuggestionsBuilder builder, ITagCollection<Block> p_197256_2_)
    {
        String s = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (Property<?> property : this.state.getProperties())
        {
            if (!this.properties.containsKey(property) && property.getName().startsWith(s))
            {
                builder.suggest(property.getName() + '=');
            }
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestStringProperty(SuggestionsBuilder builder, ITagCollection<Block> p_200134_2_)
    {
        String s = builder.getRemaining().toLowerCase(Locale.ROOT);

        if (this.tag != null && !this.tag.getPath().isEmpty())
        {
            ITag<Block> itag = p_200134_2_.get(this.tag);

            if (itag != null)
            {
                for (Block block : itag.getAllElements())
                {
                    for (Property<?> property : block.getStateContainer().getProperties())
                    {
                        if (!this.stringProperties.containsKey(property.getName()) && property.getName().startsWith(s))
                        {
                            builder.suggest(property.getName() + '=');
                        }
                    }
                }
            }
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestNbt(SuggestionsBuilder builder, ITagCollection<Block> p_197244_2_)
    {
        if (builder.getRemaining().isEmpty() && this.func_212598_k(p_197244_2_))
        {
            builder.suggest(String.valueOf('{'));
        }

        return builder.buildFuture();
    }

    private boolean func_212598_k(ITagCollection<Block> p_212598_1_)
    {
        if (this.state != null)
        {
            return this.state.getBlock().isTileEntityProvider();
        }
        else
        {
            if (this.tag != null)
            {
                ITag<Block> itag = p_212598_1_.get(this.tag);

                if (itag != null)
                {
                    for (Block block : itag.getAllElements())
                    {
                        if (block.isTileEntityProvider())
                        {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder builder, ITagCollection<Block> p_197246_2_)
    {
        if (builder.getRemaining().isEmpty())
        {
            builder.suggest(String.valueOf('='));
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestPropertyEndOrContinue(SuggestionsBuilder builder, ITagCollection<Block> p_197248_2_)
    {
        if (builder.getRemaining().isEmpty())
        {
            builder.suggest(String.valueOf(']'));
        }

        if (builder.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size())
        {
            builder.suggest(String.valueOf(','));
        }

        return builder.buildFuture();
    }

    private static <T extends Comparable<T>> SuggestionsBuilder suggestValue(SuggestionsBuilder builder, Property<T> property)
    {
        for (T t : property.getAllowedValues())
        {
            if (t instanceof Integer)
            {
                builder.suggest((Integer) t);
            }
            else
            {
                builder.suggest(property.getName(t));
            }
        }

        return builder;
    }

    private CompletableFuture<Suggestions> func_239295_a_(SuggestionsBuilder p_239295_1_, ITagCollection<Block> p_239295_2_, String p_239295_3_)
    {
        boolean flag = false;

        if (this.tag != null && !this.tag.getPath().isEmpty())
        {
            ITag<Block> itag = p_239295_2_.get(this.tag);

            if (itag != null)
            {
                for (Block block : itag.getAllElements())
                {
                    Property<?> property = block.getStateContainer().getProperty(p_239295_3_);

                    if (property != null)
                    {
                        suggestValue(p_239295_1_, property);
                    }

                    if (!flag)
                    {
                        for (Property<?> property1 : block.getStateContainer().getProperties())
                        {
                            if (!this.stringProperties.containsKey(property1.getName()))
                            {
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (flag)
        {
            p_239295_1_.suggest(String.valueOf(','));
        }

        p_239295_1_.suggest(String.valueOf(']'));
        return p_239295_1_.buildFuture();
    }

    private CompletableFuture<Suggestions> func_212599_i(SuggestionsBuilder p_212599_1_, ITagCollection<Block> p_212599_2_)
    {
        if (p_212599_1_.getRemaining().isEmpty())
        {
            ITag<Block> itag = p_212599_2_.get(this.tag);

            if (itag != null)
            {
                boolean flag = false;
                boolean flag1 = false;

                for (Block block : itag.getAllElements())
                {
                    flag |= !block.getStateContainer().getProperties().isEmpty();
                    flag1 |= block.isTileEntityProvider();

                    if (flag && flag1)
                    {
                        break;
                    }
                }

                if (flag)
                {
                    p_212599_1_.suggest(String.valueOf('['));
                }

                if (flag1)
                {
                    p_212599_1_.suggest(String.valueOf('{'));
                }
            }
        }

        return this.suggestTag(p_212599_1_, p_212599_2_);
    }

    private CompletableFuture<Suggestions> suggestPropertyOrNbt(SuggestionsBuilder builder, ITagCollection<Block> p_197255_2_)
    {
        if (builder.getRemaining().isEmpty())
        {
            if (!this.state.getBlock().getStateContainer().getProperties().isEmpty())
            {
                builder.suggest(String.valueOf('['));
            }

            if (this.state.getBlock().isTileEntityProvider())
            {
                builder.suggest(String.valueOf('{'));
            }
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder, ITagCollection<Block> p_201953_2_)
    {
        return ISuggestionProvider.suggestIterable(p_201953_2_.getRegisteredTags(), builder.createOffset(this.cursorPos).add(builder));
    }

    private CompletableFuture<Suggestions> suggestTagOrBlock(SuggestionsBuilder builder, ITagCollection<Block> p_197250_2_)
    {
        if (this.tagsAllowed)
        {
            ISuggestionProvider.suggestIterable(p_197250_2_.getRegisteredTags(), builder, String.valueOf('#'));
        }

        ISuggestionProvider.suggestIterable(Registry.BLOCK.keySet(), builder);
        return builder.buildFuture();
    }

    public void readBlock() throws CommandSyntaxException
    {
        int i = this.reader.getCursor();
        this.blockID = ResourceLocation.read(this.reader);
        Block block = Registry.BLOCK.getOptional(this.blockID).orElseThrow(() ->
        {
            this.reader.setCursor(i);
            return STATE_BAD_ID.createWithContext(this.reader, this.blockID.toString());
        });
        this.blockStateContainer = block.getStateContainer();
        this.state = block.getDefaultState();
    }

    public void readTag() throws CommandSyntaxException
    {
        if (!this.tagsAllowed)
        {
            throw STATE_TAGS_NOT_ALLOWED.create();
        }
        else
        {
            this.suggestor = this::suggestTag;
            this.reader.expect('#');
            this.cursorPos = this.reader.getCursor();
            this.tag = ResourceLocation.read(this.reader);
        }
    }

    public void readProperties() throws CommandSyntaxException
    {
        this.reader.skip();
        this.suggestor = this::suggestPropertyOrEnd;
        this.reader.skipWhitespace();

        while (true)
        {
            if (this.reader.canRead() && this.reader.peek() != ']')
            {
                this.reader.skipWhitespace();
                int i = this.reader.getCursor();
                String s = this.reader.readString();
                Property<?> property = this.blockStateContainer.getProperty(s);

                if (property == null)
                {
                    this.reader.setCursor(i);
                    throw STATE_UNKNOWN_PROPERTY.createWithContext(this.reader, this.blockID.toString(), s);
                }

                if (this.properties.containsKey(property))
                {
                    this.reader.setCursor(i);
                    throw STATE_DUPLICATE_PROPERTY.createWithContext(this.reader, this.blockID.toString(), s);
                }

                this.reader.skipWhitespace();
                this.suggestor = this::suggestEquals;

                if (!this.reader.canRead() || this.reader.peek() != '=')
                {
                    throw STATE_NO_VALUE.createWithContext(this.reader, this.blockID.toString(), s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestor = (p_197251_1_, p_197251_2_) ->
                {
                    return suggestValue(p_197251_1_, property).buildFuture();
                };
                int j = this.reader.getCursor();
                this.parseValue(property, this.reader.readString(), j);
                this.suggestor = this::suggestPropertyEndOrContinue;
                this.reader.skipWhitespace();

                if (!this.reader.canRead())
                {
                    continue;
                }

                if (this.reader.peek() == ',')
                {
                    this.reader.skip();
                    this.suggestor = this::suggestProperty;
                    continue;
                }

                if (this.reader.peek() != ']')
                {
                    throw STATE_UNCLOSED.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead())
            {
                this.reader.skip();
                return;
            }

            throw STATE_UNCLOSED.createWithContext(this.reader);
        }
    }

    public void readStringProperties() throws CommandSyntaxException
    {
        this.reader.skip();
        this.suggestor = this::suggestStringPropertyOrEnd;
        int i = -1;
        this.reader.skipWhitespace();

        while (true)
        {
            if (this.reader.canRead() && this.reader.peek() != ']')
            {
                this.reader.skipWhitespace();
                int j = this.reader.getCursor();
                String s = this.reader.readString();

                if (this.stringProperties.containsKey(s))
                {
                    this.reader.setCursor(j);
                    throw STATE_DUPLICATE_PROPERTY.createWithContext(this.reader, this.blockID.toString(), s);
                }

                this.reader.skipWhitespace();

                if (!this.reader.canRead() || this.reader.peek() != '=')
                {
                    this.reader.setCursor(j);
                    throw STATE_NO_VALUE.createWithContext(this.reader, this.blockID.toString(), s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestor = (p_200138_2_, p_200138_3_) ->
                {
                    return this.func_239295_a_(p_200138_2_, p_200138_3_, s);
                };
                i = this.reader.getCursor();
                String s1 = this.reader.readString();
                this.stringProperties.put(s, s1);
                this.reader.skipWhitespace();

                if (!this.reader.canRead())
                {
                    continue;
                }

                i = -1;

                if (this.reader.peek() == ',')
                {
                    this.reader.skip();
                    this.suggestor = this::suggestStringProperty;
                    continue;
                }

                if (this.reader.peek() != ']')
                {
                    throw STATE_UNCLOSED.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead())
            {
                this.reader.skip();
                return;
            }

            if (i >= 0)
            {
                this.reader.setCursor(i);
            }

            throw STATE_UNCLOSED.createWithContext(this.reader);
        }
    }

    public void readNBT() throws CommandSyntaxException
    {
        this.nbt = (new JsonToNBT(this.reader)).readStruct();
    }

    private <T extends Comparable<T>> void parseValue(Property<T> property, String value, int valuePosition) throws CommandSyntaxException
    {
        Optional<T> optional = property.parseValue(value);

        if (optional.isPresent())
        {
            this.state = this.state.with(property, optional.get());
            this.properties.put(property, optional.get());
        }
        else
        {
            this.reader.setCursor(valuePosition);
            throw STATE_INVALID_PROPERTY_VALUE.createWithContext(this.reader, this.blockID.toString(), property.getName(), value);
        }
    }

    public static String toString(BlockState state)
    {
        StringBuilder stringbuilder = new StringBuilder(Registry.BLOCK.getKey(state.getBlock()).toString());

        if (!state.getProperties().isEmpty())
        {
            stringbuilder.append('[');
            boolean flag = false;

            for (Entry < Property<?>, Comparable<? >> entry : state.getValues().entrySet())
            {
                if (flag)
                {
                    stringbuilder.append(',');
                }

                propValToString(stringbuilder, entry.getKey(), entry.getValue());
                flag = true;
            }

            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }

    private static <T extends Comparable<T>> void propValToString(StringBuilder builder, Property<T> property, Comparable<?> value)
    {
        builder.append(property.getName());
        builder.append('=');
        builder.append(property.getName((T)value));
    }

    public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, ITagCollection<Block> p_197245_2_)
    {
        return this.suggestor.apply(builder.createOffset(this.reader.getCursor()), p_197245_2_);
    }

    public Map<String, String> getStringProperties()
    {
        return this.stringProperties;
    }
}
