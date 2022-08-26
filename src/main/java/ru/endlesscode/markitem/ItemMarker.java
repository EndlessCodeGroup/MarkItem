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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.markitem.util.Items;
import ru.endlesscode.markitem.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ItemMarker implements Listener {
    private final ItemStack mark;
    private final List<String> markText;

    private static final NamespacedKey KEY_MARK = MarkItem.namespacedKey("mark");
    private static final NamespacedKey KEY_MARKED = MarkItem.namespacedKey("markitem_marked");
    private static final NamespacedKey KEY_RECIPE = MarkItem.namespacedKey("recipe");

    public ItemMarker(@NotNull Config config) {
        markText = config.getMarkText();
        String[] textures = config.getMarkTexture().split(":");
        Material textureType = Material.getMaterial(textures[0]);

        if (textureType == null) {
            Log.w("Material {0} not found", textures[0]);
            this.mark = new ItemStack(Material.AIR);
            return;
        }

        ItemStack item = new ItemStack(textureType);

        if (textures.length == 2) {
            try {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof Damageable) {
                    ((Damageable) meta).setDamage(Integer.parseInt(textures[1], 0));
                    item.setItemMeta(meta);
                } else {
                    Log.w("Material {0} is not damageable", textures[0]);
                }
            } catch (NumberFormatException e) {
                Log.w("{0} is not a number", textures[1]);
            }
        }

        Items.editItemMeta(item, im -> {
            im.setDisplayName(config.getMarkName());
            im.setLore(config.getMarkLore());
        });

        Items.addFlag(item, KEY_MARK);
        this.mark = item;

        ItemStack recipeItem = new ItemStack(mark.getType());
        Items.editItemMeta(recipeItem, im -> {
            im.setDisplayName(config.getRecipeTitle());
            im.setLore(config.getRecipeDescription());
        });
        addRecipe(recipeItem, config.getAllowed(), config.getDenied());
    }

    private void addRecipe(ItemStack result, List<Pattern> allowPatterns, List<Pattern> denyPatterns) {
        Material[] materials = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(material -> anyMatch(allowPatterns, material))
                .filter(material -> noneMatch(denyPatterns, material))
                .toArray(Material[]::new);

        ShapelessRecipe recipe = new ShapelessRecipe(KEY_RECIPE, result)
                .addIngredient(mark.getType())
                .addIngredient(new RecipeChoice.MaterialChoice(materials));

        if (Bukkit.addRecipe(recipe)) {
            Log.i("Added marked item recipe for {0} material(s)", materials.length);
        } else {
            Log.w("Marked item recipe wasn't added for some reason");
        }
    }

    private boolean noneMatch(List<Pattern> patterns, Material material) {
        return !anyMatch(patterns, material);
    }

    private boolean anyMatch(List<Pattern> patterns, Material material) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(material.name()).matches());
    }

    private ItemStack addMarkToItem(ItemStack item) {
        if (!itemIsMarked(item)) {
            Items.editItemMeta(item, im -> {
                List<String> lore = im.getLore() != null ? im.getLore() : new ArrayList<>();
                lore.addAll(this.markText);
                im.setLore(lore);
            });
            Items.addFlag(item, KEY_MARKED);
        }

        return item;
    }

    public static boolean itemIsMarked(@NotNull ItemStack item) {
        return Items.hasFlag(item, KEY_MARKED);
    }

    public ItemStack getMark() {
        return this.mark;
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

        // We don't want to mark the same item twice
        if (itemToMark == null || itemIsMarked(itemToMark)) {
            event.getInventory().setResult(null);
        } else {
            ItemStack result = itemToMark.clone();
            event.getInventory().setResult(addMarkToItem(result));
        }
    }

    private boolean isMarkedItemRecipe(Recipe recipe) {
        if (!(recipe instanceof ShapelessRecipe)) return false;
        return KEY_RECIPE.equals(((ShapelessRecipe) recipe).getKey());
    }

    static boolean isMark(@NotNull ItemStack itemStack) {
        return Items.hasFlag(itemStack, KEY_MARK);
    }
}
