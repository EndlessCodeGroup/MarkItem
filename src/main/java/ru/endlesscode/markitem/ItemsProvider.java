package ru.endlesscode.markitem;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.markitem.util.Items;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

import java.util.Objects;

/**
 * Creates items required for plugin: mark item and recipe item.
 */
class ItemsProvider {

    private final Config config;
    private final BukkitItemsRegistry itemsRegistry;

    @Nullable
    private ItemStack mark = null;
    @Nullable
    private ItemStack recipeItem = null;

    ItemsProvider(Config config, BukkitItemsRegistry itemsRegistry) {
        this.config = config;
        this.itemsRegistry = itemsRegistry;
    }

    synchronized @NotNull ItemStack getMark() {
        if (mark == null) mark = createMark();
        return mark;
    }

    private @NotNull ItemStack createMark() {
        ItemStack result = requireItem(config.getMarkTexture(), "mark.texture");

        Items.editItemMeta(result, im -> {
            im.setDisplayName(config.getMarkName());
            im.setLore(config.getMarkLore());
        });

        return result;
    }

    synchronized @NotNull ItemStack getRecipeItem() {
        if (recipeItem == null) recipeItem = createRecipeItem();
        return recipeItem;
    }

    private @NotNull ItemStack createRecipeItem() {
        ItemStack result = requireItem(config.getRecipeTexture(), "recipe.texture");

        Items.editItemMeta(result, im -> {
            im.setDisplayName(config.getRecipeTitle());
            im.setLore(config.getRecipeDescription());
        });

        return result;
    }

    private @NotNull ItemStack requireItem(@NotNull String id, @NotNull String option) {
        if (id.startsWith("markitem") || id.equals(MarkItemRegistry.MARK_ID)) {
            throw new IllegalArgumentException("You can not use item '" + id + "' in option '" + option + "'");
        }

        return Objects.requireNonNull(
                itemsRegistry.getItem(id),
                () -> "Item '" + id + "' not found in Mimic. Please, specify a valid ID in option '" + option + "'"
        );
    }
}
