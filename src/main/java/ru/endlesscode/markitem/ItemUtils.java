package ru.endlesscode.markitem;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

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
}
