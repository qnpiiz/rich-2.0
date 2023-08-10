package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map < GameRules.RuleKey<?>, GameRules.RuleType<? >> GAME_RULES = Maps.newTreeMap(Comparator.comparing((key) ->
    {
        return key.gameRuleName;
    }));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_FIRE_TICK = register("doFireTick", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> MOB_GRIEFING = register("mobGriefing", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> KEEP_INVENTORY = register("keepInventory", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_MOB_SPAWNING = register("doMobSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_MOB_LOOT = register("doMobLoot", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_TILE_DROPS = register("doTileDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_ENTITY_DROPS = register("doEntityDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> COMMAND_BLOCK_OUTPUT = register("commandBlockOutput", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> NATURAL_REGENERATION = register("naturalRegeneration", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_DAYLIGHT_CYCLE = register("doDaylightCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> LOG_ADMIN_COMMANDS = register("logAdminCommands", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> SHOW_DEATH_MESSAGES = register("showDeathMessages", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.IntegerValue> RANDOM_TICK_SPEED = register("randomTickSpeed", GameRules.Category.UPDATES, GameRules.IntegerValue.create(3));
    public static final GameRules.RuleKey<GameRules.BooleanValue> SEND_COMMAND_FEEDBACK = register("sendCommandFeedback", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> REDUCED_DEBUG_INFO = register("reducedDebugInfo", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (server, value) ->
    {
        byte b0 = (byte)(value.get() ? 22 : 23);

        for (ServerPlayerEntity serverplayerentity : server.getPlayerList().getPlayers())
        {
            serverplayerentity.connection.sendPacket(new SEntityStatusPacket(serverplayerentity, b0));
        }
    }));
    public static final GameRules.RuleKey<GameRules.BooleanValue> SPECTATORS_GENERATE_CHUNKS = register("spectatorsGenerateChunks", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.IntegerValue> SPAWN_RADIUS = register("spawnRadius", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.RuleKey<GameRules.IntegerValue> MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.Category.MOBS, GameRules.IntegerValue.create(24));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_WEATHER_CYCLE = register("doWeatherCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    public static final GameRules.RuleKey<GameRules.IntegerValue> MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.Category.MISC, GameRules.IntegerValue.create(65536));
    public static final GameRules.RuleKey<GameRules.BooleanValue> ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DISABLE_RAIDS = register("disableRaids", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_INSOMNIA = register("doInsomnia", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_IMMEDIATE_RESPAWN = register("doImmediateRespawn", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false, (server, value) ->
    {
        for (ServerPlayerEntity serverplayerentity : server.getPlayerList().getPlayers())
        {
            serverplayerentity.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241775_l_, value.get() ? 1.0F : 0.0F));
        }
    }));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DROWNING_DAMAGE = register("drowningDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> FALL_DAMAGE = register("fallDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> FIRE_DAMAGE = register("fireDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_PATROL_SPAWNING = register("doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> DO_TRADER_SPAWNING = register("doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> FORGIVE_DEAD_PLAYERS = register("forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> UNIVERSAL_ANGER = register("universalAnger", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
    private final Map < GameRules.RuleKey<?>, GameRules.RuleValue<? >> rules;

    private static <T extends GameRules.RuleValue<T>> GameRules.RuleKey<T> register(String name, GameRules.Category category, GameRules.RuleType<T> type)
    {
        GameRules.RuleKey<T> rulekey = new GameRules.RuleKey<>(name, category);
        GameRules.RuleType<?> ruletype = GAME_RULES.put(rulekey, type);

        if (ruletype != null)
        {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        }
        else
        {
            return rulekey;
        }
    }

    public GameRules(DynamicLike<?> dynamic)
    {
        this();
        this.decode(dynamic);
    }

    public GameRules()
    {
        this.rules = GAME_RULES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) ->
        {
            return entry.getValue().createValue();
        }));
    }

    private GameRules(Map < GameRules.RuleKey<?>, GameRules.RuleValue<? >> keyToValueMap)
    {
        this.rules = keyToValueMap;
    }

    public <T extends GameRules.RuleValue<T>> T get(GameRules.RuleKey<T> key)
    {
        return (T)(this.rules.get(key));
    }

    /**
     * Return the defined game rules as NBT.
     */
    public CompoundNBT write()
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        this.rules.forEach((key, value) ->
        {
            compoundnbt.putString(key.gameRuleName, value.stringValue());
        });
        return compoundnbt;
    }

    private void decode(DynamicLike<?> dynamic)
    {
        this.rules.forEach((key, value) ->
        {
            dynamic.get(key.gameRuleName).asString().result().ifPresent(value::setStringValue);
        });
    }

    public GameRules clone()
    {
        return new GameRules(this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) ->
        {
            return entry.getValue().clone();
        })));
    }

    public static void visitAll(GameRules.IRuleEntryVisitor visitor)
    {
        GAME_RULES.forEach((key, type) ->
        {
            visit(visitor, key, type);
        });
    }

    private static <T extends GameRules.RuleValue<T>> void visit(GameRules.IRuleEntryVisitor visitor, GameRules.RuleKey<?> key, GameRules.RuleType<?> type)
    {
        visitor.visit((RuleKey)key, type);
        type.visitRule(visitor, (RuleKey) key);
    }

    public void func_234899_a_(GameRules rules, @Nullable MinecraftServer server)
    {
        rules.rules.keySet().forEach((key) ->
        {
            this.getValue(key, rules, server);
        });
    }

    private <T extends GameRules.RuleValue<T>> void getValue(GameRules.RuleKey<T> key, GameRules rules, @Nullable MinecraftServer server)
    {
        T t = rules.get(key);
        this.<T>get(key).changeValue(t, server);
    }

    public boolean getBoolean(GameRules.RuleKey<GameRules.BooleanValue> key)
    {
        return this.get(key).get();
    }

    public int getInt(GameRules.RuleKey<GameRules.IntegerValue> key)
    {
        return this.get(key).get();
    }

    public static class BooleanValue extends GameRules.RuleValue<GameRules.BooleanValue>
    {
        private boolean value;

        private static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changeListener)
        {
            return new GameRules.RuleType<>(BoolArgumentType::bool, (type) ->
            {
                return new GameRules.BooleanValue(type, defaultValue);
            }, changeListener, GameRules.IRuleEntryVisitor::changeBoolean);
        }

        private static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue)
        {
            return create(defaultValue, (server, value) ->
            {
            });
        }

        public BooleanValue(GameRules.RuleType<GameRules.BooleanValue> type, boolean defaultValue)
        {
            super(type);
            this.value = defaultValue;
        }

        protected void updateValue0(CommandContext<CommandSource> context, String paramName)
        {
            this.value = BoolArgumentType.getBool(context, paramName);
        }

        public boolean get()
        {
            return this.value;
        }

        public void set(boolean valueIn, @Nullable MinecraftServer server)
        {
            this.value = valueIn;
            this.notifyChange(server);
        }

        public String stringValue()
        {
            return Boolean.toString(this.value);
        }

        protected void setStringValue(String valueIn)
        {
            this.value = Boolean.parseBoolean(valueIn);
        }

        public int intValue()
        {
            return this.value ? 1 : 0;
        }

        protected GameRules.BooleanValue getValue()
        {
            return this;
        }

        protected GameRules.BooleanValue clone()
        {
            return new GameRules.BooleanValue(this.type, this.value);
        }

        public void changeValue(GameRules.BooleanValue value, @Nullable MinecraftServer server)
        {
            this.value = value.value;
            this.notifyChange(server);
        }
    }

    public static enum Category
    {
        PLAYER("gamerule.category.player"),
        MOBS("gamerule.category.mobs"),
        SPAWNING("gamerule.category.spawning"),
        DROPS("gamerule.category.drops"),
        UPDATES("gamerule.category.updates"),
        CHAT("gamerule.category.chat"),
        MISC("gamerule.category.misc");

        private final String localeString;

        private Category(String localeString)
        {
            this.localeString = localeString;
        }

        public String getLocaleString()
        {
            return this.localeString;
        }
    }

    interface IRule<T extends GameRules.RuleValue<T>>
    {
        void call(GameRules.IRuleEntryVisitor p_call_1_, GameRules.RuleKey<T> p_call_2_, GameRules.RuleType<T> p_call_3_);
    }

    public interface IRuleEntryVisitor
    {
    default <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key, GameRules.RuleType<T> type)
        {
        }

    default void changeBoolean(GameRules.RuleKey<GameRules.BooleanValue> value1, GameRules.RuleType<GameRules.BooleanValue> value2)
        {
        }

    default void changeInteger(GameRules.RuleKey<GameRules.IntegerValue> value1, GameRules.RuleType<GameRules.IntegerValue> value2)
        {
        }
    }

    public static class IntegerValue extends GameRules.RuleValue<GameRules.IntegerValue>
    {
        private int value;

        private static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changeListener)
        {
            return new GameRules.RuleType<>(IntegerArgumentType::integer, (type) ->
            {
                return new GameRules.IntegerValue(type, defaultValue);
            }, changeListener, GameRules.IRuleEntryVisitor::changeInteger);
        }

        private static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue)
        {
            return create(defaultValue, (server, value) ->
            {
            });
        }

        public IntegerValue(GameRules.RuleType<GameRules.IntegerValue> type, int defaultValue)
        {
            super(type);
            this.value = defaultValue;
        }

        protected void updateValue0(CommandContext<CommandSource> context, String paramName)
        {
            this.value = IntegerArgumentType.getInteger(context, paramName);
        }

        public int get()
        {
            return this.value;
        }

        public String stringValue()
        {
            return Integer.toString(this.value);
        }

        protected void setStringValue(String valueIn)
        {
            this.value = parseInt(valueIn);
        }

        public boolean parseIntValue(String name)
        {
            try
            {
                this.value = Integer.parseInt(name);
                return true;
            }
            catch (NumberFormatException numberformatexception)
            {
                return false;
            }
        }

        private static int parseInt(String strValue)
        {
            if (!strValue.isEmpty())
            {
                try
                {
                    return Integer.parseInt(strValue);
                }
                catch (NumberFormatException numberformatexception)
                {
                    GameRules.LOGGER.warn("Failed to parse integer {}", (Object)strValue);
                }
            }

            return 0;
        }

        public int intValue()
        {
            return this.value;
        }

        protected GameRules.IntegerValue getValue()
        {
            return this;
        }

        protected GameRules.IntegerValue clone()
        {
            return new GameRules.IntegerValue(this.type, this.value);
        }

        public void changeValue(GameRules.IntegerValue value, @Nullable MinecraftServer server)
        {
            this.value = value.value;
            this.notifyChange(server);
        }
    }

    public static final class RuleKey<T extends GameRules.RuleValue<T>>
    {
        private final String gameRuleName;
        private final GameRules.Category category;

        public RuleKey(String gameRuleName, GameRules.Category category)
        {
            this.gameRuleName = gameRuleName;
            this.category = category;
        }

        public String toString()
        {
            return this.gameRuleName;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else
            {
                return p_equals_1_ instanceof GameRules.RuleKey && ((GameRules.RuleKey)p_equals_1_).gameRuleName.equals(this.gameRuleName);
            }
        }

        public int hashCode()
        {
            return this.gameRuleName.hashCode();
        }

        public String getName()
        {
            return this.gameRuleName;
        }

        public String getLocaleString()
        {
            return "gamerule." + this.gameRuleName;
        }

        public GameRules.Category getCategory()
        {
            return this.category;
        }
    }

    public static class RuleType<T extends GameRules.RuleValue<T>>
    {
        private final Supplier < ArgumentType<? >> argTypeSupplier;
        private final Function<GameRules.RuleType<T>, T> valueCreator;
        private final BiConsumer<MinecraftServer, T> changeListener;
        private final GameRules.IRule<T> rule;

        private RuleType(Supplier < ArgumentType<? >> argTypeSupplier, Function<GameRules.RuleType<T>, T> valueCreator, BiConsumer<MinecraftServer, T> changeListener, GameRules.IRule<T> rule)
        {
            this.argTypeSupplier = argTypeSupplier;
            this.valueCreator = valueCreator;
            this.changeListener = changeListener;
            this.rule = rule;
        }

        public RequiredArgumentBuilder < CommandSource, ? > createArgument(String name)
        {
            return Commands.argument(name, this.argTypeSupplier.get());
        }

        public T createValue()
        {
            return this.valueCreator.apply(this);
        }

        public void visitRule(GameRules.IRuleEntryVisitor visitor, GameRules.RuleKey<T> key)
        {
            this.rule.call(visitor, key, this);
        }
    }

    public abstract static class RuleValue<T extends GameRules.RuleValue<T>>
    {
        protected final GameRules.RuleType<T> type;

        public RuleValue(GameRules.RuleType<T> type)
        {
            this.type = type;
        }

        protected abstract void updateValue0(CommandContext<CommandSource> context, String paramName);

        public void updateValue(CommandContext<CommandSource> context, String paramName)
        {
            this.updateValue0(context, paramName);
            this.notifyChange(context.getSource().getServer());
        }

        protected void notifyChange(@Nullable MinecraftServer server)
        {
            if (server != null)
            {
                this.type.changeListener.accept(server, this.getValue());
            }
        }

        protected abstract void setStringValue(String valueIn);

        public abstract String stringValue();

        public String toString()
        {
            return this.stringValue();
        }

        public abstract int intValue();

        protected abstract T getValue();

        protected abstract T clone();

        public abstract void changeValue(T value, @Nullable MinecraftServer server);
    }
}
