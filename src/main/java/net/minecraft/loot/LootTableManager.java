package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableManager extends JsonReloadListener
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON_INSTANCE = LootSerializers.func_237388_c_().create();
    private Map<ResourceLocation, LootTable> registeredLootTables = ImmutableMap.of();
    private final LootPredicateManager lootPredicateManager;

    public LootTableManager(LootPredicateManager lootPredicateManager)
    {
        super(GSON_INSTANCE, "loot_tables");
        this.lootPredicateManager = lootPredicateManager;
    }

    public LootTable getLootTableFromLocation(ResourceLocation ressources)
    {
        return this.registeredLootTables.getOrDefault(ressources, LootTable.EMPTY_LOOT_TABLE);
    }

    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        Builder<ResourceLocation, LootTable> builder = ImmutableMap.builder();
        JsonElement jsonelement = objectIn.remove(LootTables.EMPTY);

        if (jsonelement != null)
        {
            LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
        }

        objectIn.forEach((id, element) ->
        {
            try {
                LootTable loottable = GSON_INSTANCE.fromJson(element, LootTable.class);
                builder.put(id, loottable);
            }
            catch (Exception exception)
            {
                LOGGER.error("Couldn't parse loot table {}", id, exception);
            }
        });
        builder.put(LootTables.EMPTY, LootTable.EMPTY_LOOT_TABLE);
        ImmutableMap<ResourceLocation, LootTable> immutablemap = builder.build();
        ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.GENERIC, this.lootPredicateManager::func_227517_a_, immutablemap::get);
        immutablemap.forEach((id, lootTable) ->
        {
            validateLootTable(validationtracker, id, lootTable);
        });
        validationtracker.getProblems().forEach((errorLocation, p_215303_1_) ->
        {
            LOGGER.warn("Found validation problem in " + errorLocation + ": " + p_215303_1_);
        });
        this.registeredLootTables = immutablemap;
    }

    public static void validateLootTable(ValidationTracker validator, ResourceLocation id, LootTable lootTable)
    {
        lootTable.validate(validator.func_227529_a_(lootTable.getParameterSet()).func_227531_a_("{" + id + "}", id));
    }

    public static JsonElement toJson(LootTable lootTableIn)
    {
        return GSON_INSTANCE.toJsonTree(lootTableIn);
    }

    public Set<ResourceLocation> getLootTableKeys()
    {
        return this.registeredLootTables.keySet();
    }
}
