package ru.endlesscode.markitem;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.markitem.util.Items;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class ItemMarker {

    private final BukkitItemsRegistry itemsRegistry;
    private final List<String> markText;
    private final List<Pattern> allowedPatterns;
    private final List<Pattern> deniedPatterns;

    private static final NamespacedKey KEY_MARKED = MarkItemPlugin.namespacedKey("markitem_marked");

    ItemMarker(Config config, BukkitItemsRegistry itemsRegistry) {
        this.itemsRegistry = itemsRegistry;
        this.markText = config.getMarkText();
        this.allowedPatterns = config.getAllowed();
        this.deniedPatterns = config.getDenied();
    }

    /**
     * Tries to mark the given item.
     * @param item The item to mark
     * @return Marked item or null it this item is already marked or can not be marked
     */
    @Nullable ItemStack tryToMarkItem(@Nullable ItemStack item) {
        if (item == null || itemIsMarked(item) || !canBeMarked(item)) return null;

        ItemStack markedItem = item.clone();
        Items.editItemMeta(markedItem, im -> {
            List<String> lore = im.getLore() != null ? im.getLore() : new ArrayList<>();
            lore.addAll(this.markText);
            im.setLore(lore);
        });
        Items.addFlag(markedItem, KEY_MARKED);

        return markedItem;
    }

    static boolean itemIsMarked(@NotNull ItemStack item) {
        return Items.hasFlag(item, KEY_MARKED);
    }

    private boolean canBeMarked(@NotNull ItemStack itemStack) {
        String itemId = itemsRegistry.getItemId(itemStack);
        assert itemId != null;

        return anyMatch(allowedPatterns, itemId) && noneMatch(deniedPatterns, itemId);
    }

    private boolean noneMatch(List<Pattern> patterns, @NotNull String value) {
        return !anyMatch(patterns, value);
    }

    private boolean anyMatch(List<Pattern> patterns, @NotNull String value) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(value).matches());
    }
}
