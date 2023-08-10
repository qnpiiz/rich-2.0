package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMap extends LootFunction
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Structure<?> field_237425_a_ = Structure.field_236380_p_;
    public static final MapDecoration.Type field_215910_a = MapDecoration.Type.MANSION;
    private final Structure<?> destination;
    private final MapDecoration.Type decoration;
    private final byte zoom;
    private final int searchRadius;
    private final boolean skipExistingChunks;

    private ExplorationMap(ILootCondition[] p_i232169_1_, Structure<?> p_i232169_2_, MapDecoration.Type p_i232169_3_, byte p_i232169_4_, int p_i232169_5_, boolean p_i232169_6_)
    {
        super(p_i232169_1_);
        this.destination = p_i232169_2_;
        this.decoration = p_i232169_3_;
        this.zoom = p_i232169_4_;
        this.searchRadius = p_i232169_5_;
        this.skipExistingChunks = p_i232169_6_;
    }

    public LootFunctionType getFunctionType()
    {
        return LootFunctionManager.EXPLORATION_MAP;
    }

    public Set < LootParameter<? >> getRequiredParameters()
    {
        return ImmutableSet.of(LootParameters.field_237457_g_);
    }

    public ItemStack doApply(ItemStack stack, LootContext context)
    {
        if (stack.getItem() != Items.MAP)
        {
            return stack;
        }
        else
        {
            Vector3d vector3d = context.get(LootParameters.field_237457_g_);

            if (vector3d != null)
            {
                ServerWorld serverworld = context.getWorld();
                BlockPos blockpos = serverworld.func_241117_a_(this.destination, new BlockPos(vector3d), this.searchRadius, this.skipExistingChunks);

                if (blockpos != null)
                {
                    ItemStack itemstack = FilledMapItem.setupNewMap(serverworld, blockpos.getX(), blockpos.getZ(), this.zoom, true, true);
                    FilledMapItem.func_226642_a_(serverworld, itemstack);
                    MapData.addTargetDecoration(itemstack, blockpos, "+", this.decoration);
                    itemstack.setDisplayName(new TranslationTextComponent("filled_map." + this.destination.getStructureName().toLowerCase(Locale.ROOT)));
                    return itemstack;
                }
            }

            return stack;
        }
    }

    public static ExplorationMap.Builder func_215903_b()
    {
        return new ExplorationMap.Builder();
    }

    public static class Builder extends LootFunction.Builder<ExplorationMap.Builder>
    {
        private Structure<?> field_216066_a = ExplorationMap.field_237425_a_;
        private MapDecoration.Type field_216067_b = ExplorationMap.field_215910_a;
        private byte field_216068_c = 2;
        private int field_216069_d = 50;
        private boolean field_216070_e = true;

        protected ExplorationMap.Builder doCast()
        {
            return this;
        }

        public ExplorationMap.Builder func_237427_a_(Structure<?> p_237427_1_)
        {
            this.field_216066_a = p_237427_1_;
            return this;
        }

        public ExplorationMap.Builder func_216064_a(MapDecoration.Type p_216064_1_)
        {
            this.field_216067_b = p_216064_1_;
            return this;
        }

        public ExplorationMap.Builder func_216062_a(byte p_216062_1_)
        {
            this.field_216068_c = p_216062_1_;
            return this;
        }

        public ExplorationMap.Builder func_216063_a(boolean p_216063_1_)
        {
            this.field_216070_e = p_216063_1_;
            return this;
        }

        public ILootFunction build()
        {
            return new ExplorationMap(this.getConditions(), this.field_216066_a, this.field_216067_b, this.field_216068_c, this.field_216069_d, this.field_216070_e);
        }
    }

    public static class Serializer extends LootFunction.Serializer<ExplorationMap>
    {
        public void serialize(JsonObject p_230424_1_, ExplorationMap p_230424_2_, JsonSerializationContext p_230424_3_)
        {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);

            if (!p_230424_2_.destination.equals(ExplorationMap.field_237425_a_))
            {
                p_230424_1_.add("destination", p_230424_3_.serialize(p_230424_2_.destination.getStructureName()));
            }

            if (p_230424_2_.decoration != ExplorationMap.field_215910_a)
            {
                p_230424_1_.add("decoration", p_230424_3_.serialize(p_230424_2_.decoration.toString().toLowerCase(Locale.ROOT)));
            }

            if (p_230424_2_.zoom != 2)
            {
                p_230424_1_.addProperty("zoom", p_230424_2_.zoom);
            }

            if (p_230424_2_.searchRadius != 50)
            {
                p_230424_1_.addProperty("search_radius", p_230424_2_.searchRadius);
            }

            if (!p_230424_2_.skipExistingChunks)
            {
                p_230424_1_.addProperty("skip_existing_chunks", p_230424_2_.skipExistingChunks);
            }
        }

        public ExplorationMap deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn)
        {
            Structure<?> structure = func_237428_a_(object);
            String s = object.has("decoration") ? JSONUtils.getString(object, "decoration") : "mansion";
            MapDecoration.Type mapdecoration$type = ExplorationMap.field_215910_a;

            try
            {
                mapdecoration$type = MapDecoration.Type.valueOf(s.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException illegalargumentexception)
            {
                ExplorationMap.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMap.field_215910_a, (Object)s);
            }

            byte b0 = JSONUtils.getByte(object, "zoom", (byte)2);
            int i = JSONUtils.getInt(object, "search_radius", 50);
            boolean flag = JSONUtils.getBoolean(object, "skip_existing_chunks", true);
            return new ExplorationMap(conditionsIn, structure, mapdecoration$type, b0, i, flag);
        }

        private static Structure<?> func_237428_a_(JsonObject p_237428_0_)
        {
            if (p_237428_0_.has("destination"))
            {
                String s = JSONUtils.getString(p_237428_0_, "destination");
                Structure<?> structure = Structure.field_236365_a_.get(s.toLowerCase(Locale.ROOT));

                if (structure != null)
                {
                    return structure;
                }
            }

            return ExplorationMap.field_237425_a_;
        }
    }
}
