package ru.endlesscode.markitem;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.markitem.util.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class ItemMarker {

    private final List<String> markText;
    private final Set<Material> allowedMaterials;

    private static final NamespacedKey KEY_MARKED = MarkItem.namespacedKey("markitem_marked");

    ItemMarker(Config config) {
        markText = config.getMarkText();
        allowedMaterials = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(material -> anyMatch(config.getAllowed(), material))
                .filter(material -> noneMatch(config.getDenied(), material))
                .collect(Collectors.toSet());
    }

    private boolean noneMatch(List<Pattern> patterns, Material material) {
        return !anyMatch(patterns, material);
    }

    private boolean anyMatch(List<Pattern> patterns, Material material) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(material.name()).matches());
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
        return allowedMaterials.contains(itemStack.getType());
    }
}
