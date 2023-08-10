package net.minecraft.util.registry;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.server.DebugLoggingPrintStream;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap
{
    public static final PrintStream SYSOUT = System.out;

    /** Whether the blocks, items, etc have already been registered */
    private static boolean alreadyRegistered;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Registers blocks, items, stats, etc.
     */
    public static void register()
    {
        if (!alreadyRegistered)
        {
            alreadyRegistered = true;

            if (Registry.REGISTRY.keySet().isEmpty())
            {
                throw new IllegalStateException("Unable to load registries");
            }
            else
            {
                FireBlock.init();
                ComposterBlock.init();

                if (EntityType.getKey(EntityType.PLAYER) == null)
                {
                    throw new IllegalStateException("Failed loading EntityTypes");
                }
                else
                {
                    PotionBrewing.init();
                    EntityOptions.registerOptions();
                    IDispenseItemBehavior.init();
                    ArgumentTypes.registerArgumentTypes();
                    TagRegistryManager.checkHelperRegistrations();
                    redirectOutputToLog();
                }
            }
        }
    }

    private static <T> void addTranslationStrings(Iterable<T> objects, Function<T, String> objectToKeyFunction, Set<String> translationSet)
    {
        LanguageMap languagemap = LanguageMap.getInstance();
        objects.forEach((registryElement) ->
        {
            String s = objectToKeyFunction.apply(registryElement);

            if (!languagemap.func_230506_b_(s))
            {
                translationSet.add(s);
            }
        });
    }

    private static void addGameRuleTranslationStrings(final Set<String> translations)
    {
        final LanguageMap languagemap = LanguageMap.getInstance();
        GameRules.visitAll(new GameRules.IRuleEntryVisitor()
        {
            public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key, GameRules.RuleType<T> type)
            {
                if (!languagemap.func_230506_b_(key.getLocaleString()))
                {
                    translations.add(key.getName());
                }
            }
        });
    }

    public static Set<String> getTranslationStrings()
    {
        Set<String> set = new TreeSet<>();
        addTranslationStrings(Registry.ATTRIBUTE, Attribute::getAttributeName, set);
        addTranslationStrings(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set);
        addTranslationStrings(Registry.EFFECTS, Effect::getName, set);
        addTranslationStrings(Registry.ITEM, Item::getTranslationKey, set);
        addTranslationStrings(Registry.ENCHANTMENT, Enchantment::getName, set);
        addTranslationStrings(Registry.BLOCK, Block::getTranslationKey, set);
        addTranslationStrings(Registry.CUSTOM_STAT, (translationFunction) ->
        {
            return "stat." + translationFunction.toString().replace(':', '.');
        }, set);
        addGameRuleTranslationStrings(set);
        return set;
    }

    public static void checkTranslations()
    {
        if (!alreadyRegistered)
        {
            throw new IllegalArgumentException("Not bootstrapped");
        }
        else
        {
            if (SharedConstants.developmentMode)
            {
                getTranslationStrings().forEach((raw) ->
                {
                    LOGGER.error("Missing translations: " + raw);
                });
                Commands.func_242986_b();
            }

            GlobalEntityTypeAttributes.validateEntityAttributes();
        }
    }

    /**
     * redirect standard streams to logger
     */
    private static void redirectOutputToLog()
    {
        if (LOGGER.isDebugEnabled())
        {
            System.setErr(new DebugLoggingPrintStream("STDERR", System.err));
            System.setOut(new DebugLoggingPrintStream("STDOUT", SYSOUT));
        }
        else
        {
            System.setErr(new LoggingPrintStream("STDERR", System.err));
            System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
        }
    }

    public static void printToSYSOUT(String message)
    {
        SYSOUT.println(message);
    }
}
