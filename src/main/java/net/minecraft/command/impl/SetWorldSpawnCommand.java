package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.AngleArgument;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class SetWorldSpawnCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("setworldspawn").requires((p_198704_0_) ->
        {
            return p_198704_0_.hasPermissionLevel(2);
        }).executes((p_198700_0_) ->
        {
            return setSpawn(p_198700_0_.getSource(), new BlockPos(p_198700_0_.getSource().getPos()), 0.0F);
        }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_198703_0_) ->
        {
            return setSpawn(p_198703_0_.getSource(), BlockPosArgument.getBlockPos(p_198703_0_, "pos"), 0.0F);
        }).then(Commands.argument("angle", AngleArgument.func_242991_a()).executes((p_244377_0_) ->
        {
            return setSpawn(p_244377_0_.getSource(), BlockPosArgument.getBlockPos(p_244377_0_, "pos"), AngleArgument.func_242992_a(p_244377_0_, "angle"));
        }))));
    }

    private static int setSpawn(CommandSource source, BlockPos pos, float p_198701_2_)
    {
        source.getWorld().func_241124_a__(pos, p_198701_2_);
        source.sendFeedback(new TranslationTextComponent("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), p_198701_2_), true);
        return 1;
    }
}
