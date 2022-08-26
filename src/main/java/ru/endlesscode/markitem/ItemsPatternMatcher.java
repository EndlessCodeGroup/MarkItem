package ru.endlesscode.markitem;

import org.bukkit.inventory.ItemStack;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static ru.endlesscode.markitem.util.Lists.anyMatch;
import static ru.endlesscode.markitem.util.Lists.noneMatch;

class ItemsPatternMatcher implements Predicate<ItemStack> {

    private final BukkitItemsRegistry itemsRegistry;
    private final List<Pattern> allowedPatterns;
    private final List<Pattern> deniedPatterns;

    ItemsPatternMatcher(BukkitItemsRegistry itemsRegistry,
                        List<Pattern> allowedPatterns,
                        List<Pattern> deniedPatterns) {
        this.itemsRegistry = itemsRegistry;
        this.allowedPatterns = allowedPatterns;
        this.deniedPatterns = deniedPatterns;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        String itemId = itemsRegistry.getItemId(itemStack);
        assert itemId != null;

        return anyMatch(allowedPatterns, itemId) && noneMatch(deniedPatterns, itemId);
    }
}
