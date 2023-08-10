package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class BlockPredicate
{
    public static final BlockPredicate ANY = new BlockPredicate((ITag<Block>)null, (Block)null, StatePropertiesPredicate.EMPTY, NBTPredicate.ANY);
    @Nullable
    private final ITag<Block> tag;
    @Nullable
    private final Block block;
    private final StatePropertiesPredicate statePredicate;
    private final NBTPredicate nbtPredicate;

    public BlockPredicate(@Nullable ITag<Block> tag, @Nullable Block block, StatePropertiesPredicate statePredicate, NBTPredicate nbtPredicate)
    {
        this.tag = tag;
        this.block = block;
        this.statePredicate = statePredicate;
        this.nbtPredicate = nbtPredicate;
    }

    public boolean test(ServerWorld world, BlockPos pos)
    {
        if (this == ANY)
        {
            return true;
        }
        else if (!world.isBlockPresent(pos))
        {
            return false;
        }
        else
        {
            BlockState blockstate = world.getBlockState(pos);
            Block block = blockstate.getBlock();

            if (this.tag != null && !this.tag.contains(block))
            {
                return false;
            }
            else if (this.block != null && block != this.block)
            {
                return false;
            }
            else if (!this.statePredicate.matches(blockstate))
            {
                return false;
            }
            else
            {
                if (this.nbtPredicate != NBTPredicate.ANY)
                {
                    TileEntity tileentity = world.getTileEntity(pos);

                    if (tileentity == null || !this.nbtPredicate.test(tileentity.write(new CompoundNBT())))
                    {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static BlockPredicate deserialize(@Nullable JsonElement json)
    {
        if (json != null && !json.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(json, "block");
            NBTPredicate nbtpredicate = NBTPredicate.deserialize(jsonobject.get("nbt"));
            Block block = null;

            if (jsonobject.has("block"))
            {
                ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "block"));
                block = Registry.BLOCK.getOrDefault(resourcelocation);
            }

            ITag<Block> itag = null;

            if (jsonobject.has("tag"))
            {
                ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(jsonobject, "tag"));
                itag = TagCollectionManager.getManager().getBlockTags().get(resourcelocation1);

                if (itag == null)
                {
                    throw new JsonSyntaxException("Unknown block tag '" + resourcelocation1 + "'");
                }
            }

            StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.deserializeProperties(jsonobject.get("state"));
            return new BlockPredicate(itag, block, statepropertiespredicate, nbtpredicate);
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

            if (this.block != null)
            {
                jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            }

            if (this.tag != null)
            {
                jsonobject.addProperty("tag", TagCollectionManager.getManager().getBlockTags().getValidatedIdFromTag(this.tag).toString());
            }

            jsonobject.add("nbt", this.nbtPredicate.serialize());
            jsonobject.add("state", this.statePredicate.toJsonElement());
            return jsonobject;
        }
    }

    public static class Builder
    {
        @Nullable
        private Block block;
        @Nullable
        private ITag<Block> tag;
        private StatePropertiesPredicate statePredicate = StatePropertiesPredicate.EMPTY;
        private NBTPredicate nbtPredicate = NBTPredicate.ANY;

        private Builder()
        {
        }

        public static BlockPredicate.Builder createBuilder()
        {
            return new BlockPredicate.Builder();
        }

        public BlockPredicate.Builder setBlock(Block block)
        {
            this.block = block;
            return this;
        }

        public BlockPredicate.Builder setTag(ITag<Block> tag)
        {
            this.tag = tag;
            return this;
        }

        public BlockPredicate.Builder setStatePredicate(StatePropertiesPredicate statePredicate)
        {
            this.statePredicate = statePredicate;
            return this;
        }

        public BlockPredicate build()
        {
            return new BlockPredicate(this.tag, this.block, this.statePredicate, this.nbtPredicate);
        }
    }
}
