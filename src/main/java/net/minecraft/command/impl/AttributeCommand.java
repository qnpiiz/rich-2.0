package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class AttributeCommand
{
    private static final SuggestionProvider<CommandSource> field_240996_a_ = (p_241005_0_, p_241005_1_) ->
    {
        return ISuggestionProvider.suggestIterable(Registry.ATTRIBUTE.keySet(), p_241005_1_);
    };
    private static final DynamicCommandExceptionType field_240997_b_ = new DynamicCommandExceptionType((p_241011_0_) ->
    {
        return new TranslationTextComponent("commands.attribute.failed.entity", p_241011_0_);
    });
    private static final Dynamic2CommandExceptionType field_240998_c_ = new Dynamic2CommandExceptionType((p_241012_0_, p_241012_1_) ->
    {
        return new TranslationTextComponent("commands.attribute.failed.no_attribute", p_241012_0_, p_241012_1_);
    });
    private static final Dynamic3CommandExceptionType field_240999_d_ = new Dynamic3CommandExceptionType((p_241017_0_, p_241017_1_, p_241017_2_) ->
    {
        return new TranslationTextComponent("commands.attribute.failed.no_modifier", p_241017_1_, p_241017_0_, p_241017_2_);
    });
    private static final Dynamic3CommandExceptionType field_241000_e_ = new Dynamic3CommandExceptionType((p_241013_0_, p_241013_1_, p_241013_2_) ->
    {
        return new TranslationTextComponent("commands.attribute.failed.modifier_already_present", p_241013_2_, p_241013_1_, p_241013_0_);
    });

    public static void register(CommandDispatcher<CommandSource> p_241003_0_)
    {
        p_241003_0_.register(Commands.literal("attribute").requires((p_241006_0_) ->
        {
            return p_241006_0_.hasPermissionLevel(2);
        }).then(Commands.argument("target", EntityArgument.entity()).then(Commands.argument("attribute", ResourceLocationArgument.resourceLocation()).suggests(field_240996_a_).then(Commands.literal("get").executes((p_241027_0_) ->
        {
            return func_241007_a_(p_241027_0_.getSource(), EntityArgument.getEntity(p_241027_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241027_0_, "attribute"), 1.0D);
        }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_241026_0_) ->
        {
            return func_241007_a_(p_241026_0_.getSource(), EntityArgument.getEntity(p_241026_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241026_0_, "attribute"), DoubleArgumentType.getDouble(p_241026_0_, "scale"));
        }))).then(Commands.literal("base").then(Commands.literal("set").then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes((p_241025_0_) ->
        {
            return func_241019_c_(p_241025_0_.getSource(), EntityArgument.getEntity(p_241025_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241025_0_, "attribute"), DoubleArgumentType.getDouble(p_241025_0_, "value"));
        }))).then(Commands.literal("get").executes((p_241024_0_) ->
        {
            return func_241016_b_(p_241024_0_.getSource(), EntityArgument.getEntity(p_241024_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241024_0_, "attribute"), 1.0D);
        }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_241023_0_) ->
        {
            return func_241016_b_(p_241023_0_.getSource(), EntityArgument.getEntity(p_241023_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241023_0_, "attribute"), DoubleArgumentType.getDouble(p_241023_0_, "scale"));
        })))).then(Commands.literal("modifier").then(Commands.literal("add").then(Commands.argument("uuid", UUIDArgument.func_239194_a_()).then(Commands.argument("name", StringArgumentType.string()).then(Commands.argument("value", DoubleArgumentType.doubleArg()).then(Commands.literal("add").executes((p_241022_0_) ->
        {
            return func_241010_a_(p_241022_0_.getSource(), EntityArgument.getEntity(p_241022_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241022_0_, "attribute"), UUIDArgument.func_239195_a_(p_241022_0_, "uuid"), StringArgumentType.getString(p_241022_0_, "name"), DoubleArgumentType.getDouble(p_241022_0_, "value"), AttributeModifier.Operation.ADDITION);
        })).then(Commands.literal("multiply").executes((p_241021_0_) ->
        {
            return func_241010_a_(p_241021_0_.getSource(), EntityArgument.getEntity(p_241021_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241021_0_, "attribute"), UUIDArgument.func_239195_a_(p_241021_0_, "uuid"), StringArgumentType.getString(p_241021_0_, "name"), DoubleArgumentType.getDouble(p_241021_0_, "value"), AttributeModifier.Operation.MULTIPLY_TOTAL);
        })).then(Commands.literal("multiply_base").executes((p_241020_0_) ->
        {
            return func_241010_a_(p_241020_0_.getSource(), EntityArgument.getEntity(p_241020_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241020_0_, "attribute"), UUIDArgument.func_239195_a_(p_241020_0_, "uuid"), StringArgumentType.getString(p_241020_0_, "name"), DoubleArgumentType.getDouble(p_241020_0_, "value"), AttributeModifier.Operation.MULTIPLY_BASE);
        })))))).then(Commands.literal("remove").then(Commands.argument("uuid", UUIDArgument.func_239194_a_()).executes((p_241018_0_) ->
        {
            return func_241008_a_(p_241018_0_.getSource(), EntityArgument.getEntity(p_241018_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241018_0_, "attribute"), UUIDArgument.func_239195_a_(p_241018_0_, "uuid"));
        }))).then(Commands.literal("value").then(Commands.literal("get").then(Commands.argument("uuid", UUIDArgument.func_239194_a_()).executes((p_241015_0_) ->
        {
            return func_241009_a_(p_241015_0_.getSource(), EntityArgument.getEntity(p_241015_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241015_0_, "attribute"), UUIDArgument.func_239195_a_(p_241015_0_, "uuid"), 1.0D);
        }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_241004_0_) ->
        {
            return func_241009_a_(p_241004_0_.getSource(), EntityArgument.getEntity(p_241004_0_, "target"), ResourceLocationArgument.func_239094_d_(p_241004_0_, "attribute"), UUIDArgument.func_239195_a_(p_241004_0_, "uuid"), DoubleArgumentType.getDouble(p_241004_0_, "scale"));
        })))))))));
    }

    private static ModifiableAttributeInstance func_241002_a_(Entity p_241002_0_, Attribute p_241002_1_) throws CommandSyntaxException
    {
        ModifiableAttributeInstance modifiableattributeinstance = func_241001_a_(p_241002_0_).getAttributeManager().createInstanceIfAbsent(p_241002_1_);

        if (modifiableattributeinstance == null)
        {
            throw field_240998_c_.create(p_241002_0_.getName(), new TranslationTextComponent(p_241002_1_.getAttributeName()));
        }
        else
        {
            return modifiableattributeinstance;
        }
    }

    private static LivingEntity func_241001_a_(Entity p_241001_0_) throws CommandSyntaxException
    {
        if (!(p_241001_0_ instanceof LivingEntity))
        {
            throw field_240997_b_.create(p_241001_0_.getName());
        }
        else
        {
            return (LivingEntity)p_241001_0_;
        }
    }

    private static LivingEntity func_241014_b_(Entity p_241014_0_, Attribute p_241014_1_) throws CommandSyntaxException
    {
        LivingEntity livingentity = func_241001_a_(p_241014_0_);

        if (!livingentity.getAttributeManager().hasAttributeInstance(p_241014_1_))
        {
            throw field_240998_c_.create(p_241014_0_.getName(), new TranslationTextComponent(p_241014_1_.getAttributeName()));
        }
        else
        {
            return livingentity;
        }
    }

    private static int func_241007_a_(CommandSource p_241007_0_, Entity p_241007_1_, Attribute p_241007_2_, double p_241007_3_) throws CommandSyntaxException
    {
        LivingEntity livingentity = func_241014_b_(p_241007_1_, p_241007_2_);
        double d0 = livingentity.getAttributeValue(p_241007_2_);
        p_241007_0_.sendFeedback(new TranslationTextComponent("commands.attribute.value.get.success", new TranslationTextComponent(p_241007_2_.getAttributeName()), p_241007_1_.getName(), d0), false);
        return (int)(d0 * p_241007_3_);
    }

    private static int func_241016_b_(CommandSource p_241016_0_, Entity p_241016_1_, Attribute p_241016_2_, double p_241016_3_) throws CommandSyntaxException
    {
        LivingEntity livingentity = func_241014_b_(p_241016_1_, p_241016_2_);
        double d0 = livingentity.getBaseAttributeValue(p_241016_2_);
        p_241016_0_.sendFeedback(new TranslationTextComponent("commands.attribute.base_value.get.success", new TranslationTextComponent(p_241016_2_.getAttributeName()), p_241016_1_.getName(), d0), false);
        return (int)(d0 * p_241016_3_);
    }

    private static int func_241009_a_(CommandSource p_241009_0_, Entity p_241009_1_, Attribute p_241009_2_, UUID p_241009_3_, double p_241009_4_) throws CommandSyntaxException
    {
        LivingEntity livingentity = func_241014_b_(p_241009_1_, p_241009_2_);
        AttributeModifierManager attributemodifiermanager = livingentity.getAttributeManager();

        if (!attributemodifiermanager.hasModifier(p_241009_2_, p_241009_3_))
        {
            throw field_240999_d_.create(p_241009_1_.getName(), new TranslationTextComponent(p_241009_2_.getAttributeName()), p_241009_3_);
        }
        else
        {
            double d0 = attributemodifiermanager.getModifierValue(p_241009_2_, p_241009_3_);
            p_241009_0_.sendFeedback(new TranslationTextComponent("commands.attribute.modifier.value.get.success", p_241009_3_, new TranslationTextComponent(p_241009_2_.getAttributeName()), p_241009_1_.getName(), d0), false);
            return (int)(d0 * p_241009_4_);
        }
    }

    private static int func_241019_c_(CommandSource p_241019_0_, Entity p_241019_1_, Attribute p_241019_2_, double p_241019_3_) throws CommandSyntaxException
    {
        func_241002_a_(p_241019_1_, p_241019_2_).setBaseValue(p_241019_3_);
        p_241019_0_.sendFeedback(new TranslationTextComponent("commands.attribute.base_value.set.success", new TranslationTextComponent(p_241019_2_.getAttributeName()), p_241019_1_.getName(), p_241019_3_), false);
        return 1;
    }

    private static int func_241010_a_(CommandSource p_241010_0_, Entity p_241010_1_, Attribute p_241010_2_, UUID p_241010_3_, String p_241010_4_, double p_241010_5_, AttributeModifier.Operation p_241010_7_) throws CommandSyntaxException
    {
        ModifiableAttributeInstance modifiableattributeinstance = func_241002_a_(p_241010_1_, p_241010_2_);
        AttributeModifier attributemodifier = new AttributeModifier(p_241010_3_, p_241010_4_, p_241010_5_, p_241010_7_);

        if (modifiableattributeinstance.hasModifier(attributemodifier))
        {
            throw field_241000_e_.create(p_241010_1_.getName(), new TranslationTextComponent(p_241010_2_.getAttributeName()), p_241010_3_);
        }
        else
        {
            modifiableattributeinstance.applyPersistentModifier(attributemodifier);
            p_241010_0_.sendFeedback(new TranslationTextComponent("commands.attribute.modifier.add.success", p_241010_3_, new TranslationTextComponent(p_241010_2_.getAttributeName()), p_241010_1_.getName()), false);
            return 1;
        }
    }

    private static int func_241008_a_(CommandSource p_241008_0_, Entity p_241008_1_, Attribute p_241008_2_, UUID p_241008_3_) throws CommandSyntaxException
    {
        ModifiableAttributeInstance modifiableattributeinstance = func_241002_a_(p_241008_1_, p_241008_2_);

        if (modifiableattributeinstance.removePersistentModifier(p_241008_3_))
        {
            p_241008_0_.sendFeedback(new TranslationTextComponent("commands.attribute.modifier.remove.success", p_241008_3_, new TranslationTextComponent(p_241008_2_.getAttributeName()), p_241008_1_.getName()), false);
            return 1;
        }
        else
        {
            throw field_240999_d_.create(p_241008_1_.getName(), new TranslationTextComponent(p_241008_2_.getAttributeName()), p_241008_3_);
        }
    }
}
