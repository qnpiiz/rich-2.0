package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.AngleArgument;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SpawnPointCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("spawnpoint").requires((p_198699_0_) ->
        {
            return p_198699_0_.hasPermissionLevel(2);
        }).executes((p_198697_0_) ->
        {
            return setSpawnPoint(p_198697_0_.getSource(), Collections.singleton(p_198697_0_.getSource().asPlayer()), new BlockPos(p_198697_0_.getSource().getPos()), 0.0F);
        }).then(Commands.argument("targets", EntityArgument.players()).executes((p_198694_0_) ->
        {
            return setSpawnPoint(p_198694_0_.getSource(), EntityArgument.getPlayers(p_198694_0_, "targets"), new BlockPos(p_198694_0_.getSource().getPos()), 0.0F);
        }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_198698_0_) ->
        {
            return setSpawnPoint(p_198698_0_.getSource(), EntityArgument.getPlayers(p_198698_0_, "targets"), BlockPosArgument.getBlockPos(p_198698_0_, "pos"), 0.0F);
        }).then(Commands.argument("angle", AngleArgument.func_242991_a()).executes((p_244376_0_) ->
        {
            return setSpawnPoint(p_244376_0_.getSource(), EntityArgument.getPlayers(p_244376_0_, "targets"), BlockPosArgument.getBlockPos(p_244376_0_, "pos"), AngleArgument.func_242992_a(p_244376_0_, "angle"));
        })))));
    }

    private static int setSpawnPoint(CommandSource source, Collection<ServerPlayerEntity> targets, BlockPos pos, float p_198696_3_)
    {
        RegistryKey<World> registrykey = source.getWorld().getDimensionKey();

        for (ServerPlayerEntity serverplayerentity : targets)
        {
            serverplayerentity.func_242111_a(registrykey, pos, p_198696_3_, true, false);
        }

        String s = registrykey.getLocation().toString();

        if (targets.size() == 1)
        {
            source.sendFeedback(new TranslationTextComponent("commands.spawnpoint.success.single", pos.getX(), pos.getY(), pos.getZ(), p_198696_3_, s, targets.iterator().next().getDisplayName()), true);
        }
        else
        {
            source.sendFeedback(new TranslationTextComponent("commands.spawnpoint.success.multiple", pos.getX(), pos.getY(), pos.getZ(), p_198696_3_, s, targets.size()), true);
        }

        return targets.size();
    }
}
