package fun.rich.command.impl;

import fun.rich.Rich;
import fun.rich.command.CommandAbstract;
import fun.rich.utils.other.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class FriendCommand extends CommandAbstract {

    public FriendCommand() {
        super("friend", "friend list", "§6.friend" + TextFormatting.LIGHT_PURPLE + " add " + "§3<nickname> | §6.friend" + TextFormatting.LIGHT_PURPLE + " del " + "§3<nickname> | §6.friend" + TextFormatting.LIGHT_PURPLE + " list " + "| §6.friend" + TextFormatting.LIGHT_PURPLE + " clear", "friend");
    }

    @Override
    public void execute(String... arguments) {
        try {
            if (arguments.length > 1) {
                if (arguments[0].equalsIgnoreCase("friend")) {
                    if (arguments[1].equalsIgnoreCase("add")) {
                        String name = arguments[2];
                        if (name.equals(Minecraft.getInstance().player.getName().getString())) {
                            ChatUtils.addChatMessage(TextFormatting.RED + "You can't add yourself!");
                            return;
                        }

                        if (!Rich.instance.friendManager.isFriend(name)) {
                            Rich.instance.friendManager.addFriend(name);
                            ChatUtils.addChatMessage("Friend " + TextFormatting.GREEN + name + TextFormatting.WHITE + " successfully added to your friend list!");
                        }
                    }
                    if (arguments[1].equalsIgnoreCase("del")) {
                        String name = arguments[2];
                        if (Rich.instance.friendManager.isFriend(name)) {
                            Rich.instance.friendManager.removeFriend(name);
                            ChatUtils.addChatMessage("Friend " + TextFormatting.RED + name + TextFormatting.WHITE + " deleted from your friend list!");
                        }
                    }
                    if (arguments[1].equalsIgnoreCase("clear")) {
                        if (Rich.instance.friendManager.getFriends().isEmpty()) {
                            ChatUtils.addChatMessage(TextFormatting.RED + "Your friend list is empty!");
                            return;
                        }

                        Rich.instance.friendManager.getFriends().clear();
                        ChatUtils.addChatMessage("Your " + TextFormatting.GREEN + "friend list " + TextFormatting.WHITE + "was cleared!");
                    }
                    if (arguments[1].equalsIgnoreCase("list")) {
                        if (Rich.instance.friendManager.getFriends().isEmpty()) {
                            ChatUtils.addChatMessage(TextFormatting.RED + "Your friend list is empty!");
                            return;
                        }

                        Rich.instance.friendManager.getFriends().forEach(friend -> ChatUtils.addChatMessage(TextFormatting.GREEN + "Friend list: " + TextFormatting.RED + friend.getName()));
                    }
                }
            } else
                ChatUtils.addChatMessage(getUsage());
        } catch (Exception exception) {
            ChatUtils.addChatMessage("§cNo, no, no. Usage: " + getUsage());
        }
    }
}
