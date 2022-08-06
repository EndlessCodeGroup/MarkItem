package ru.endlesscode.markitem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.markitem.util.Items;

import java.util.*;

class PlayerListener implements Listener {
    private static final Map<UUID, ItemStack[]> INVENTORIES = new HashMap<>();
    private static final Map<UUID, ItemStack[]> ARMORS = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        if (drops.isEmpty()) return;

        Player player = event.getEntity();

        // We should keep armor first to remove it from drop list
        // BEFORE we walk over all drops
        keepDroppedArmor(player, drops);
        keepDrop(player.getUniqueId(), drops);
    }

    private void keepDroppedArmor(@NotNull Player player, @NotNull List<ItemStack> drops) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            boolean itemKept = shouldKeepItem(armor[i]) && drops.remove(armor[i]);
            if (!itemKept) armor[i] = null;
        }

        ARMORS.put(player.getUniqueId(), armor);
    }

    private void keepDrop(@NotNull UUID playerId, @NotNull List<ItemStack> drops) {
        List<ItemStack> contents = new ArrayList<>();
        for (ItemStack drop : new ArrayList<>(drops)) {
            if (shouldKeepItem(drop)) {
                contents.add(drop);
                drops.remove(drop);
            }
        }

        INVENTORIES.put(playerId, contents.toArray(new ItemStack[0]));
    }

    private boolean shouldKeepItem(@Nullable ItemStack itemStack) {
        return Items.isNotEmpty(itemStack) &&
                ItemMarker.itemIsMarked(itemStack);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        ItemStack[] armor = ARMORS.remove(uuid);
        if (armor != null) restorePlayerArmor(player, armor);

        ItemStack[] inventory = INVENTORIES.remove(uuid);
        if (inventory != null) player.getInventory().addItem(inventory);
    }

    private void restorePlayerArmor(@NotNull Player player, ItemStack[] savedArmor) {
        ItemStack[] mergedArmor = player.getInventory().getArmorContents();
        for (int i = 0; i < savedArmor.length; i++) {
            if (savedArmor[i] != null) mergedArmor[i] = savedArmor[i];
        }
        player.getInventory().setArmorContents(mergedArmor);
    }
}
