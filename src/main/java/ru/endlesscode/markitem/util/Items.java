package ru.endlesscode.markitem.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static org.bukkit.persistence.PersistentDataType.BYTE;

public class Items {

    private Items() {
        // Should not be instantiated
    }

    @Contract("null -> false")
    public static boolean isNotEmpty(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    public static void addFlag(@NotNull ItemStack itemStack, @NotNull NamespacedKey flag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(flag, BYTE, (byte) 1);
            itemStack.setItemMeta(itemMeta);
        }
    }

    public static boolean hasFlag(@NotNull ItemStack itemStack, @NotNull NamespacedKey flag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            return itemMeta.getPersistentDataContainer().has(flag, BYTE);
        }
        return false;
    }

    public static void editItemMeta(@NotNull ItemStack itemStack, Consumer<ItemMeta> edit) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            edit.accept(itemMeta);
            itemStack.setItemMeta(itemMeta);
        }
    }
}
