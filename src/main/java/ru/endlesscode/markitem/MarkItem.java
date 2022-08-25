package ru.endlesscode.markitem;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.markitem.util.Log;

public class MarkItem extends JavaPlugin {

    private static MarkItem instance;
    private static ItemMarker itemMarker;

    public static ItemMarker getItemMarker() {
        return itemMarker;
    }

    @NotNull
    public static NamespacedKey namespacedKey(@NotNull String key) {
        return new NamespacedKey(instance, key);
    }

    @Override
    public void onEnable() {
        instance = this;
        Log.init(getLogger());

        saveDefaultConfig();
        Config config = new Config(this.getConfig());

        if (!config.isEnabled()) {
            this.getLogger().warning("Plugin is not enabled.");
            this.setEnabled(false);
            return;
        }

        Glow.register();

        itemMarker = new ItemMarker(config);
        this.getServer().getPluginManager().registerEvents(itemMarker, this);
        this.getServer().getPluginManager().registerEvents(new PlayerInventoryKeeper(), this);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (args.length == 0) {
            CommandExecutor.giveMark(sender);
        } else {
            CommandExecutor.giveMark(sender, args[0]);
        }

        return true;
    }
}
