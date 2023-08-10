package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class MessageCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((p_198539_0_) ->
        {
            return sendPrivateMessage(p_198539_0_.getSource(), EntityArgument.getPlayers(p_198539_0_, "targets"), MessageArgument.getMessage(p_198539_0_, "message"));
        }))));
        dispatcher.register(Commands.literal("tell").redirect(literalcommandnode));
        dispatcher.register(Commands.literal("w").redirect(literalcommandnode));
    }

    private static int sendPrivateMessage(CommandSource source, Collection<ServerPlayerEntity> recipients, ITextComponent message)
    {
        UUID uuid = source.getEntity() == null ? Util.DUMMY_UUID : source.getEntity().getUniqueID();
        Entity entity = source.getEntity();
        Consumer<ITextComponent> consumer;

        if (entity instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
            consumer = (p_244374_2_) ->
            {
                serverplayerentity.sendMessage((new TranslationTextComponent("commands.message.display.outgoing", p_244374_2_, message)).mergeStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), serverplayerentity.getUniqueID());
            };
        }
        else
        {
            consumer = (p_244375_2_) ->
            {
                source.sendFeedback((new TranslationTextComponent("commands.message.display.outgoing", p_244375_2_, message)).mergeStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), false);
            };
        }

        for (ServerPlayerEntity serverplayerentity1 : recipients)
        {
            consumer.accept(serverplayerentity1.getDisplayName());
            serverplayerentity1.sendMessage((new TranslationTextComponent("commands.message.display.incoming", source.getDisplayName(), message)).mergeStyle(new TextFormatting[] {TextFormatting.GRAY, TextFormatting.ITALIC}), uuid);
        }

        return recipients.size();
    }
}
