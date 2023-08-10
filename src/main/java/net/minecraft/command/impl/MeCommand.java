package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.filter.IChatFilter;

public class MeCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes((p_198365_0_) ->
        {
            String s = StringArgumentType.getString(p_198365_0_, "action");
            Entity entity = p_198365_0_.getSource().getEntity();
            MinecraftServer minecraftserver = p_198365_0_.getSource().getServer();

            if (entity != null)
            {
                if (entity instanceof ServerPlayerEntity)
                {
                    IChatFilter ichatfilter = ((ServerPlayerEntity)entity).func_244529_Q();

                    if (ichatfilter != null)
                    {
                        ichatfilter.func_244432_a(s).thenAcceptAsync((p_244713_3_) ->
                        {
                            p_244713_3_.ifPresent((p_244712_3_) -> {
                                minecraftserver.getPlayerList().func_232641_a_(func_244711_a(p_198365_0_, p_244712_3_), ChatType.CHAT, entity.getUniqueID());
                            });
                        }, minecraftserver);
                        return 1;
                    }
                }

                minecraftserver.getPlayerList().func_232641_a_(func_244711_a(p_198365_0_, s), ChatType.CHAT, entity.getUniqueID());
            }
            else {
                minecraftserver.getPlayerList().func_232641_a_(func_244711_a(p_198365_0_, s), ChatType.SYSTEM, Util.DUMMY_UUID);
            }

            return 1;
        })));
    }

    private static ITextComponent func_244711_a(CommandContext<CommandSource> p_244711_0_, String p_244711_1_)
    {
        return new TranslationTextComponent("chat.type.emote", p_244711_0_.getSource().getDisplayName(), p_244711_1_);
    }
}
