package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootPredicateManager extends JsonReloadListener
{
    private static final Logger field_227510_a_ = LogManager.getLogger();
    private static final Gson field_227511_b_ = LootSerializers.func_237386_a_().create();
    private Map<ResourceLocation, ILootCondition> field_227512_c_ = ImmutableMap.of();

    public LootPredicateManager()
    {
        super(field_227511_b_, "predicates");
    }

    @Nullable
    public ILootCondition func_227517_a_(ResourceLocation p_227517_1_)
    {
        return this.field_227512_c_.get(p_227517_1_);
    }

    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        Builder<ResourceLocation, ILootCondition> builder = ImmutableMap.builder();
        objectIn.forEach((p_237404_1_, p_237404_2_) ->
        {
            try {
                if (p_237404_2_.isJsonArray())
                {
                    ILootCondition[] ailootcondition = field_227511_b_.fromJson(p_237404_2_, ILootCondition[].class);
                    builder.put(p_237404_1_, new LootPredicateManager.AndCombiner(ailootcondition));
                }
                else {
                    ILootCondition ilootcondition = field_227511_b_.fromJson(p_237404_2_, ILootCondition.class);
                    builder.put(p_237404_1_, ilootcondition);
                }
            }
            catch (Exception exception)
            {
                field_227510_a_.error("Couldn't parse loot table {}", p_237404_1_, exception);
            }
        });
        Map<ResourceLocation, ILootCondition> map = builder.build();
        ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.GENERIC, map::get, (p_227518_0_) ->
        {
            return null;
        });
        map.forEach((p_227515_1_, p_227515_2_) ->
        {
            p_227515_2_.func_225580_a_(validationtracker.func_227535_b_("{" + p_227515_1_ + "}", p_227515_1_));
        });
        validationtracker.getProblems().forEach((p_227516_0_, p_227516_1_) ->
        {
            field_227510_a_.warn("Found validation problem in " + p_227516_0_ + ": " + p_227516_1_);
        });
        this.field_227512_c_ = map;
    }

    public Set<ResourceLocation> func_227513_a_()
    {
        return Collections.unmodifiableSet(this.field_227512_c_.keySet());
    }

    static class AndCombiner implements ILootCondition
    {
        private final ILootCondition[] field_237405_a_;
        private final Predicate<LootContext> field_237406_b_;

        private AndCombiner(ILootCondition[] p_i232164_1_)
        {
            this.field_237405_a_ = p_i232164_1_;
            this.field_237406_b_ = LootConditionManager.and(p_i232164_1_);
        }

        public final boolean test(LootContext p_test_1_)
        {
            return this.field_237406_b_.test(p_test_1_);
        }

        public void func_225580_a_(ValidationTracker p_225580_1_)
        {
            ILootCondition.super.func_225580_a_(p_225580_1_);

            for (int i = 0; i < this.field_237405_a_.length; ++i)
            {
                this.field_237405_a_[i].func_225580_a_(p_225580_1_.func_227534_b_(".term[" + i + "]"));
            }
        }

        public LootConditionType func_230419_b_()
        {
            throw new UnsupportedOperationException();
        }
    }
}
