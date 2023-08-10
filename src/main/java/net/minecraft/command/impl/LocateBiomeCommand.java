package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;

public class LocateBiomeCommand
{
    public static final DynamicCommandExceptionType field_241044_a_ = new DynamicCommandExceptionType((p_241052_0_) ->
    {
        return new TranslationTextComponent("commands.locatebiome.invalid", p_241052_0_);
    });
    private static final DynamicCommandExceptionType field_241045_b_ = new DynamicCommandExceptionType((p_241050_0_) ->
    {
        return new TranslationTextComponent("commands.locatebiome.notFound", p_241050_0_);
    });

    public static void register(CommandDispatcher<CommandSource> p_241046_0_)
    {
        p_241046_0_.register(Commands.literal("locatebiome").requires((p_241048_0_) ->
        {
            return p_241048_0_.hasPermissionLevel(2);
        }).then(Commands.argument("biome", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.field_239574_d_).executes((p_241047_0_) ->
        {
            return func_241049_a_(p_241047_0_.getSource(), p_241047_0_.getArgument("biome", ResourceLocation.class));
        })));
    }

    private static int func_241049_a_(CommandSource p_241049_0_, ResourceLocation p_241049_1_) throws CommandSyntaxException
    {
        Biome biome = p_241049_0_.getServer().func_244267_aX().getRegistry(Registry.BIOME_KEY).getOptional(p_241049_1_).orElseThrow(() ->
        {
            return field_241044_a_.create(p_241049_1_);
        });
        BlockPos blockpos = new BlockPos(p_241049_0_.getPos());
        BlockPos blockpos1 = p_241049_0_.getWorld().func_241116_a_(biome, blockpos, 6400, 8);
        String s = p_241049_1_.toString();

        if (blockpos1 == null)
        {
            throw field_241045_b_.create(s);
        }
        else
        {
            return LocateCommand.func_241054_a_(p_241049_0_, s, blockpos, blockpos1, "commands.locatebiome.success");
        }
    }
}
