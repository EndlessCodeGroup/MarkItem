package ru.endlesscode.markitem;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ru.endlesscode.markitem.misc.Config;

/**
 * Created by OsipXD on 10.09.2015
 * It is part of the MarkItem.
 * All rights reserved 2014 - 2015 © «EndlessCode Group»
 */
public class MarkItem extends JavaPlugin {

    private static MarkItem instance;
    private static ItemMarker itemMarker;

    public static MarkItem getInstance() {
        return instance;
    }

    public static ItemMarker getItemMarker() {
        return itemMarker;
    }

    @Override
    public void onEnable() {
        instance = this;
        Config.loadConfig(this);

        if (!Config.getConfig().getBoolean("enabled")) {
            this.getLogger().warning("Plugin is not enabled!");
            this.setEnabled(false);
            return;
        }

        Glow.register();

        itemMarker = new ItemMarker();
        this.getServer().getPluginManager().registerEvents(itemMarker, this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            CommandExecutor.giveMark(sender);
        } else {
            CommandExecutor.giveMark(sender, args[0]);
        }

        return true;
    }
}
