package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockPredicateArgument implements ArgumentType<BlockPredicateArgument.IResult>
{
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
    private static final DynamicCommandExceptionType UNKNOWN_TAG = new DynamicCommandExceptionType((p_208682_0_) ->
    {
        return new TranslationTextComponent("arguments.block.tag.unknown", p_208682_0_);
    });

    public static BlockPredicateArgument blockPredicate()
    {
        return new BlockPredicateArgument();
    }

    public BlockPredicateArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException
    {
        BlockStateParser blockstateparser = (new BlockStateParser(p_parse_1_, true)).parse(true);

        if (blockstateparser.getState() != null)
        {
            BlockPredicateArgument.BlockPredicate blockpredicateargument$blockpredicate = new BlockPredicateArgument.BlockPredicate(blockstateparser.getState(), blockstateparser.getProperties().keySet(), blockstateparser.getNbt());
            return (p_199823_1_) ->
            {
                return blockpredicateargument$blockpredicate;
            };
        }
        else
        {
            ResourceLocation resourcelocation = blockstateparser.getTag();
            return (p_199822_2_) ->
            {
                ITag<Block> itag = p_199822_2_.getBlockTags().get(resourcelocation);

                if (itag == null)
                {
                    throw UNKNOWN_TAG.create(resourcelocation.toString());
                }
                else {
                    return new BlockPredicateArgument.TagPredicate(itag, blockstateparser.getStringProperties(), blockstateparser.getNbt());
                }
            };
        }
    }

    public static Predicate<CachedBlockInfo> getBlockPredicate(CommandContext<CommandSource> context, String name) throws CommandSyntaxException
    {
        return context.getArgument(name, BlockPredicateArgument.IResult.class).create(context.getSource().getServer().func_244266_aF());
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_)
    {
        StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
        stringreader.setCursor(p_listSuggestions_2_.getStart());
        BlockStateParser blockstateparser = new BlockStateParser(stringreader, true);

        try
        {
            blockstateparser.parse(true);
        }
        catch (CommandSyntaxException commandsyntaxexception)
        {
        }

        return blockstateparser.getSuggestions(p_listSuggestions_2_, BlockTags.getCollection());
    }

    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }

    static class BlockPredicate implements Predicate<CachedBlockInfo>
    {
        private final BlockState state;
        private final Set < Property<? >> properties;
        @Nullable
        private final CompoundNBT nbt;

        public BlockPredicate(BlockState stateIn, Set < Property<? >> propertiesIn, @Nullable CompoundNBT nbtIn)
        {
            this.state = stateIn;
            this.properties = propertiesIn;
            this.nbt = nbtIn;
        }

        public boolean test(CachedBlockInfo p_test_1_)
        {
            BlockState blockstate = p_test_1_.getBlockState();

            if (!blockstate.isIn(this.state.getBlock()))
            {
                return false;
            }
            else
            {
                for (Property<?> property : this.properties)
                {
                    if (blockstate.get(property) != this.state.get(property))
                    {
                        return false;
                    }
                }

                if (this.nbt == null)
                {
                    return true;
                }
                else
                {
                    TileEntity tileentity = p_test_1_.getTileEntity();
                    return tileentity != null && NBTUtil.areNBTEquals(this.nbt, tileentity.write(new CompoundNBT()), true);
                }
            }
        }
    }

    public interface IResult
    {
        Predicate<CachedBlockInfo> create(ITagCollectionSupplier p_create_1_) throws CommandSyntaxException;
    }

    static class TagPredicate implements Predicate<CachedBlockInfo>
    {
        private final ITag<Block> tag;
        @Nullable
        private final CompoundNBT nbt;
        private final Map<String, String> properties;

        private TagPredicate(ITag<Block> tagIn, Map<String, String> propertiesIn, @Nullable CompoundNBT nbtIn)
        {
            this.tag = tagIn;
            this.properties = propertiesIn;
            this.nbt = nbtIn;
        }

        public boolean test(CachedBlockInfo p_test_1_)
        {
            BlockState blockstate = p_test_1_.getBlockState();

            if (!blockstate.isIn(this.tag))
            {
                return false;
            }
            else
            {
                for (Entry<String, String> entry : this.properties.entrySet())
                {
                    Property<?> property = blockstate.getBlock().getStateContainer().getProperty(entry.getKey());

                    if (property == null)
                    {
                        return false;
                    }

                    Comparable<?> comparable = (Comparable)property.parseValue(entry.getValue()).orElse(null);

                    if (comparable == null)
                    {
                        return false;
                    }

                    if (blockstate.get(property) != comparable)
                    {
                        return false;
                    }
                }

                if (this.nbt == null)
                {
                    return true;
                }
                else
                {
                    TileEntity tileentity = p_test_1_.getTileEntity();
                    return tileentity != null && NBTUtil.areNBTEquals(this.nbt, tileentity.write(new CompoundNBT()), true);
                }
            }
        }
    }
}
