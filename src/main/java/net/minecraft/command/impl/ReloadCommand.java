package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.IServerConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadCommand
{
    private static final Logger field_241057_a_ = LogManager.getLogger();

    public static void func_241062_a_(Collection<String> p_241062_0_, CommandSource p_241062_1_)
    {
        p_241062_1_.getServer().func_240780_a_(p_241062_0_).exceptionally((p_241061_1_) ->
        {
            field_241057_a_.warn("Failed to execute reload", p_241061_1_);
            p_241062_1_.sendErrorMessage(new TranslationTextComponent("commands.reload.failure"));
            return null;
        });
    }

    private static Collection<String> func_241058_a_(ResourcePackList p_241058_0_, IServerConfiguration p_241058_1_, Collection<String> p_241058_2_)
    {
        p_241058_0_.reloadPacksFromFinders();
        Collection<String> collection = Lists.newArrayList(p_241058_2_);
        Collection<String> collection1 = p_241058_1_.getDatapackCodec().getDisabled();

        for (String s : p_241058_0_.func_232616_b_())
        {
            if (!collection1.contains(s) && !collection.contains(s))
            {
                collection.add(s);
            }
        }

        return collection;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("reload").requires((p_198599_0_) ->
        {
            return p_198599_0_.hasPermissionLevel(2);
        }).executes((p_198598_0_) ->
        {
            CommandSource commandsource = p_198598_0_.getSource();
            MinecraftServer minecraftserver = commandsource.getServer();
            ResourcePackList resourcepacklist = minecraftserver.getResourcePacks();
            IServerConfiguration iserverconfiguration = minecraftserver.func_240793_aU_();
            Collection<String> collection = resourcepacklist.func_232621_d_();
            Collection<String> collection1 = func_241058_a_(resourcepacklist, iserverconfiguration, collection);
            commandsource.sendFeedback(new TranslationTextComponent("commands.reload.success"), true);
            func_241062_a_(collection1, commandsource);
            return 0;
        }));
    }
}
