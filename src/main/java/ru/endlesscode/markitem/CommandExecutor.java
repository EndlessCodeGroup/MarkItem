package ru.endlesscode.markitem;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandExecutor {
    public static void giveMark(CommandSender sender) {
        if (sender instanceof Player) {
            giveMark((Player) sender);
        } else {
            sender.sendMessage("It is player side command");
        }
    }


    public static void giveMark(CommandSender sender, String name) {
        Player player = sender.getServer().getPlayer(name);

        if (player == null) {
            sender.sendMessage("Player " + name + " not found.");
        } else {
            giveMark(player);
        }
    }

    private static void giveMark(Player player) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(MarkItem.getItemMarker().getMark());
        }
    }
}
