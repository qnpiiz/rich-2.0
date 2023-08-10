package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class TeamMsgCommand
{
    private static final Style field_241076_a_ = Style.EMPTY.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.type.team.hover"))).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
    private static final SimpleCommandExceptionType field_218919_a = new SimpleCommandExceptionType(new TranslationTextComponent("commands.teammsg.failed.noteam"));

    public static void register(CommandDispatcher<CommandSource> p_218915_0_)
    {
        LiteralCommandNode<CommandSource> literalcommandnode = p_218915_0_.register(Commands.literal("teammsg").then(Commands.argument("message", MessageArgument.message()).executes((p_218916_0_) ->
        {
            return func_218917_a(p_218916_0_.getSource(), MessageArgument.getMessage(p_218916_0_, "message"));
        })));
        p_218915_0_.register(Commands.literal("tm").redirect(literalcommandnode));
    }

    private static int func_218917_a(CommandSource p_218917_0_, ITextComponent p_218917_1_) throws CommandSyntaxException
    {
        Entity entity = p_218917_0_.assertIsEntity();
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)entity.getTeam();

        if (scoreplayerteam == null)
        {
            throw field_218919_a.create();
        }
        else
        {
            ITextComponent itextcomponent = scoreplayerteam.func_237501_d_().mergeStyle(field_241076_a_);
            List<ServerPlayerEntity> list = p_218917_0_.getServer().getPlayerList().getPlayers();

            for (ServerPlayerEntity serverplayerentity : list)
            {
                if (serverplayerentity == entity)
                {
                    serverplayerentity.sendMessage(new TranslationTextComponent("chat.type.team.sent", itextcomponent, p_218917_0_.getDisplayName(), p_218917_1_), entity.getUniqueID());
                }
                else if (serverplayerentity.getTeam() == scoreplayerteam)
                {
                    serverplayerentity.sendMessage(new TranslationTextComponent("chat.type.team.text", itextcomponent, p_218917_0_.getDisplayName(), p_218917_1_), entity.getUniqueID());
                }
            }

            return list.size();
        }
    }
}
