package ru.endlesscode.markitem;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static org.bukkit.persistence.PersistentDataType.BYTE;

class ItemUtils {

    static final NamespacedKey KEY_MARK = new NamespacedKey(MarkItem.getInstance(), "mark");
    static final NamespacedKey KEY_MARKED = new NamespacedKey(MarkItem.getInstance(), "markitem_marked");

    private ItemUtils() {
        // Should not be instantiated
    }

    @Contract("null -> false")
    static boolean isNotEmpty(@Nullable ItemStack itemStack) {
        return itemStack != null && !itemStack.getType().isAir();
    }

    static void addFlag(@NotNull ItemStack itemStack, @NotNull NamespacedKey flag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(flag, BYTE, (byte) 1);
            itemStack.setItemMeta(itemMeta);
        }
    }

    static boolean hasFlag(@NotNull ItemStack itemStack, @NotNull NamespacedKey flag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            return itemMeta.getPersistentDataContainer().has(flag, BYTE);
        }
        return false;
    }

    @NotNull
    static ItemStack editItemMeta(@NotNull ItemStack itemStack, Consumer<ItemMeta> edit) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            edit.accept(itemMeta);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
