package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Map.Entry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.gen.feature.structure.Structure;

public class LocateCommand
{
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.locate.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("locate").requires((p_198533_0_) ->
        {
            return p_198533_0_.hasPermissionLevel(2);
        });

        for (Entry < String, Structure<? >> entry : Structure.field_236365_a_.entrySet())
        {
            literalargumentbuilder = literalargumentbuilder.then(Commands.literal(entry.getKey()).executes((p_241056_1_) ->
            {
                return func_241053_a_(p_241056_1_.getSource(), entry.getValue());
            }));
        }

        dispatcher.register(literalargumentbuilder);
    }

    private static int func_241053_a_(CommandSource p_241053_0_, Structure<?> p_241053_1_) throws CommandSyntaxException
    {
        BlockPos blockpos = new BlockPos(p_241053_0_.getPos());
        BlockPos blockpos1 = p_241053_0_.getWorld().func_241117_a_(p_241053_1_, blockpos, 100, false);

        if (blockpos1 == null)
        {
            throw FAILED_EXCEPTION.create();
        }
        else
        {
            return func_241054_a_(p_241053_0_, p_241053_1_.getStructureName(), blockpos, blockpos1, "commands.locate.success");
        }
    }

    public static int func_241054_a_(CommandSource p_241054_0_, String p_241054_1_, BlockPos p_241054_2_, BlockPos p_241054_3_, String p_241054_4_)
    {
        int i = MathHelper.floor(getDistance(p_241054_2_.getX(), p_241054_2_.getZ(), p_241054_3_.getX(), p_241054_3_.getZ()));
        ITextComponent itextcomponent = TextComponentUtils.wrapWithSquareBrackets(new TranslationTextComponent("chat.coordinates", p_241054_3_.getX(), "~", p_241054_3_.getZ())).modifyStyle((p_241055_1_) ->
        {
            return p_241055_1_.setFormatting(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + p_241054_3_.getX() + " ~ " + p_241054_3_.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip")));
        });
        p_241054_0_.sendFeedback(new TranslationTextComponent(p_241054_4_, p_241054_1_, itextcomponent, i), false);
        return i;
    }

    private static float getDistance(int x1, int z1, int x2, int z2)
    {
        int i = x2 - x1;
        int j = z2 - z1;
        return MathHelper.sqrt((float)(i * i + j * j));
    }
}
