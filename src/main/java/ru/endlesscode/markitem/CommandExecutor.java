package ru.endlesscode.markitem;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandExecutor {

    private final ItemsProvider itemsProvider;

    CommandExecutor(ItemsProvider itemsProvider) {
        this.itemsProvider = itemsProvider;
    }

    void giveMark(CommandSender sender) {
        if (sender instanceof Player) {
            giveMark((Player) sender);
        } else {
            sender.sendMessage("This command should be executed by player");
        }
    }


    void giveMark(CommandSender sender, String name) {
        Player player = sender.getServer().getPlayer(name);

        if (player == null) {
            sender.sendMessage("Player " + name + " not found.");
        } else {
            giveMark(player);
            sender.sendMessage("Gave mark to " + name);
        }
    }

    private void giveMark(Player player) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(itemsProvider.getMark());
        }
    }
}
