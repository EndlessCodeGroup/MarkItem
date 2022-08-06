package ru.endlesscode.markitem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

class PlayerListener implements Listener {
    private static final Map<UUID, ItemStack[]> INVENTORIES = new HashMap<>();
    private static final Map<UUID, ItemStack[]> ARMORS = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        List<ItemStack> armorList = new ArrayList<>();
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null || armor.getType() == Material.AIR || !ItemMarker.itemIsMarked(armor)) {
                armorList.add(new ItemStack(Material.AIR, 0));
            } else {
                armorList.add(armor);
                event.getDrops().remove(armor);
            }
        }

        ARMORS.put(player.getUniqueId(), armorList.toArray(new ItemStack[armorList.size()]));

        List<ItemStack> contents = new ArrayList<>();
        for (ItemStack drop : new ArrayList<>(event.getDrops())) {
            if (drop.getType() != Material.AIR && ItemMarker.itemIsMarked(drop)) {
                contents.add(drop);
                event.getDrops().remove(drop);
            }
        }

        INVENTORIES.put(player.getUniqueId(), contents.toArray(new ItemStack[contents.size()]));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.getInventory().setArmorContents(ARMORS.get(player.getUniqueId()));
        player.getInventory().addItem(INVENTORIES.get(player.getUniqueId()));

        ARMORS.remove(player.getUniqueId());
        INVENTORIES.remove(player.getUniqueId());
    }
}
