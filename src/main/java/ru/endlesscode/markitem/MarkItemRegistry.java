package ru.endlesscode.markitem;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

import java.util.Collection;
import java.util.Collections;

/**
 * Registry with only one item to make it possible to get mark via Mimic.
 */
class MarkItemRegistry implements BukkitItemsRegistry {

    static final String MARK_ID = "mark";

    private final ItemsProvider itemsProvider;

    MarkItemRegistry(ItemsProvider itemsProvider) {
        this.itemsProvider = itemsProvider;
    }

    @NotNull
    @Override
    public Collection<String> getKnownIds() {
        return Collections.singletonList(MARK_ID);
    }

    @Nullable
    @Override
    public ItemStack getItem(@NotNull String itemId, @Nullable Object payload, int amount) {
        if (!itemId.equals(MARK_ID)) return null;

        ItemStack mark = itemsProvider.getMark();
        mark.setAmount(amount);
        return mark;
    }

    @Nullable
    @Override
    public String getItemId(@NotNull ItemStack item) {
        return ItemsProvider.isMark(item) ? MARK_ID : null;
    }

    @Override
    public boolean isItemExists(@NotNull String itemId) {
        return itemId.equals(MARK_ID);
    }
}
