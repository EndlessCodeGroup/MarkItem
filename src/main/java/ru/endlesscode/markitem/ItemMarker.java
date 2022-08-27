package ru.endlesscode.markitem;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.markitem.util.Items;

import java.util.ArrayList;
import java.util.List;

import static ru.endlesscode.markitem.util.Items.*;

class ItemMarker {

    private final ItemsPatternMatcher matcher;
    private final List<String> markText;

    private static final NamespacedKey KEY_MARKED = MarkItemPlugin.namespacedKey("marked");
    private static final NamespacedKey KEY_MARKED_LEGACY = MarkItemPlugin.namespacedKey("markitem_marked");

    ItemMarker(ItemsPatternMatcher matcher, List<String> markText) {
        this.matcher = matcher;
        this.markText = markText;
    }

    /**
     * Tries to mark the given item.
     *
     * @param item The item to mark
     * @return Marked item or null it this item is already marked or can not be marked
     */
    @Nullable ItemStack tryToMarkItem(@Nullable ItemStack item) {
        if (item == null || itemIsMarked(item) || !matcher.test(item)) return null;

        ItemStack markedItem = item.clone();
        Items.editItemMeta(markedItem, im -> {
            List<String> lore = im.getLore() != null ? im.getLore() : new ArrayList<>();
            lore.addAll(this.markText);
            im.setLore(lore);
        });
        addFlag(markedItem, KEY_MARKED);

        return markedItem;
    }

    static boolean itemIsMarked(@NotNull ItemStack item) {
        return hasFlag(item, KEY_MARKED) || migrateLegacyFlag(item);
    }

    // TODO: Remove after v1.1
    private static boolean migrateLegacyFlag(ItemStack item) {
        if (hasFlag(item, KEY_MARKED_LEGACY)) {
            addFlag(item, KEY_MARKED);
            removeFlag(item, KEY_MARKED_LEGACY);
            return true;
        }
        return false;
    }
}
