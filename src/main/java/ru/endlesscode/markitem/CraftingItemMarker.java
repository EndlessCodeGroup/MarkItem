package ru.endlesscode.markitem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.markitem.util.Items;
import ru.endlesscode.markitem.util.Log;

import java.util.Arrays;

import static ru.endlesscode.markitem.ItemsProvider.isMark;

public class CraftingItemMarker implements Listener {

    private final ItemMarker marker;

    private static final NamespacedKey KEY_RECIPE = MarkItemPlugin.namespacedKey("recipe");

    public CraftingItemMarker(@NotNull ItemMarker marker) {
        this.marker = marker;
    }

    void registerRecipe(@NotNull ItemsProvider itemsProvider) {
        ShapelessRecipe recipe = new ShapelessRecipe(KEY_RECIPE, itemsProvider.getRecipeItem())
                .addIngredient(itemsProvider.getMark().getType())
                .addIngredient(new RecipeChoice.MaterialChoice(Material.values()));

        if (Bukkit.addRecipe(recipe)) {
            Log.i("Marked item recipe added successfully!");
        } else {
            Log.w("Marked item recipe wasn't added for some reason");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(PrepareItemCraftEvent event) {
        if (!isMarkedItemRecipe(event.getRecipe())) return;

        // Filter out unwanted empty items
        ItemStack[] matrix = Arrays.stream(event.getInventory().getMatrix())
                .filter(Items::isNotEmpty)
                .toArray(ItemStack[]::new);

        // Let's see which of two items should be marked
        if (matrix.length != 2) return;
        boolean firstIsMark = isMark(matrix[0]);
        boolean secondIsMark = isMark(matrix[1]);

        ItemStack itemToMark = null;
        if (!secondIsMark && firstIsMark) {
            itemToMark = matrix[1];
        } else if (!firstIsMark && secondIsMark) {
            itemToMark = matrix[0];
        }

        ItemStack markedItem = marker.tryToMarkItem(itemToMark);
        if (markedItem != null) {
            markedItem.setAmount(event.getRecipe().getResult().getAmount());
        }
        event.getInventory().setResult(markedItem);
    }

    private boolean isMarkedItemRecipe(Recipe recipe) {
        if (!(recipe instanceof ShapelessRecipe)) return false;
        return KEY_RECIPE.equals(((ShapelessRecipe) recipe).getKey());
    }
}
