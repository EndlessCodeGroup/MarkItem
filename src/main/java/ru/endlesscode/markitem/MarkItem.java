package ru.endlesscode.markitem;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.markitem.util.Log;
import ru.endlesscode.mimic.Mimic;
import ru.endlesscode.mimic.MimicApiLevel;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

public class MarkItem extends JavaPlugin {

    private static MarkItem instance;

    private CommandExecutor commandExecutor;

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
            getLogger().warning("Plugin is not enabled.");
            setEnabled(false);
            return;
        }

        if (!checkMimicEnabled()) {
            getLogger().severe("Download latest version here: https://www.spigotmc.org/resources/82515/");
            setEnabled(false);
            return;
        }

        BukkitItemsRegistry itemsRegistry = Mimic.getInstance().getItemsRegistry();

        ItemMarker marker = new ItemMarker(config, itemsRegistry);
        final CraftingItemMarker craftingMarker = new CraftingItemMarker(marker);
        getServer().getPluginManager().registerEvents(craftingMarker, this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryKeeper(), this);

        final ItemsProvider itemsProvider = new ItemsProvider(config, itemsRegistry);
        hookMimic(itemsProvider);
        commandExecutor = new CommandExecutor(itemsProvider);

        // Register recipe when all Mimic registries will be available
        getServer().getScheduler().runTask(this, () -> craftingMarker.registerRecipe(itemsProvider));
    }

    private boolean checkMimicEnabled() {
        if (!getServer().getPluginManager().isPluginEnabled("Mimic")) {
            getLogger().severe("Mimic is required for the plugin!");
            return false;
        }

        if (!MimicApiLevel.checkApiLevel(MimicApiLevel.VERSION_0_7)) {
            getLogger().severe("Required at least Mimic 0.7!");
            return false;
        }

        return true;
    }

    private void hookMimic(ItemsProvider itemsProvider) {
        MarkItemRegistry registry = new MarkItemRegistry(itemsProvider);
        Mimic.getInstance().registerItemsRegistry(registry, MimicApiLevel.CURRENT, this);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (args.length == 0) {
            commandExecutor.giveMark(sender);
        } else {
            commandExecutor.giveMark(sender, args[0]);
        }

        return true;
    }
}
