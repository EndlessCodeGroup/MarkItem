package ru.endlesscode.markitem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import ru.endlesscode.markitem.util.Items;
import ru.endlesscode.markitem.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemMarker implements Listener {
    private final ItemStack mark;
    private final ItemMeta markMeta;
    private final String markText;

    private static final NamespacedKey KEY_MARK = MarkItem.namespacedKey("mark");
    private static final NamespacedKey KEY_MARKED = MarkItem.namespacedKey("markitem_marked");
    private static final String RECIPE_PREFIX = "marked_";

    public ItemMarker(Config config) {
        markText = config.getMarkText();
        String[] textures = config.getMarkTexture().split(":");
        Material textureType = Material.getMaterial(textures[0]);

        if (textureType == null) {
            Log.w("Material {0} not found", textures[0]);
            this.mark = new ItemStack(Material.AIR);
            this.markMeta = mark.getItemMeta();
            return;
        }

        ItemStack item = new ItemStack(textureType);

        if (textures.length == 2) try {
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

        ItemMeta im = item.getItemMeta();
        im.setDisplayName(config.getMarkName());
        im.setLore(config.getMarkLore());
        item.setItemMeta(im);

        if (config.isMarkGlow()) {
            Glow.addGlow(item);
        }

        this.mark = item;
        this.markMeta = item.getItemMeta();
        addRecipes(config.getAllowed(), config.getDenied());
    }

    private void addRecipes(List<Pattern> allowPatterns, List<Pattern> denyPatterns) {
        long count = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(material -> anyMatch(allowPatterns, material))
                .filter(material -> noneMatch(denyPatterns, material))
                .map(this::addRecipe)
                // Count only successfully added recipes
                .filter(Boolean::booleanValue)
                .count();

        Log.i("{0} recipe(s) has been added", count);
    }

    private boolean noneMatch(List<Pattern> patterns, Material material) {
        return !anyMatch(patterns, material);
    }

    private boolean anyMatch(List<Pattern> patterns, Material material) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(material.name()).matches());
    }

    private boolean addRecipe(Material type) {
        ShapelessRecipe recipe = new ShapelessRecipe(
                MarkItem.namespacedKey(RECIPE_PREFIX + type.name()),
                new ItemStack(type)
        )
                .addIngredient(type)
                .addIngredient(mark.getData());

        return Bukkit.addRecipe(recipe);
    }

    private ItemStack addMarkToItem(ItemStack item) {
        if (!itemIsMarked(item)) {
            Items.editItemMeta(item, im -> {
                List<String> lore = im.getLore() != null ? im.getLore() : new ArrayList<>();
                lore.add(this.markText);
                im.setLore(lore);
            });
            Items.addFlag(item, KEY_MARKED);
        }

        return item;
    }

    public static boolean itemIsMarked(ItemStack item) {
        return Items.hasFlag(item, KEY_MARKED);
    }

    public ItemStack getMark() {
        return this.mark;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(PrepareItemCraftEvent event) {
        if (!isMarkedItemRecipe(event.getRecipe())) {
            return;
        }

        List<ItemStack> matrix = Arrays.stream(event.getInventory().getMatrix())
                .filter(Items::isNotEmpty)
                .collect(Collectors.toList());

        if (matrix.size() != 2) {
            return;
        }

        for (Iterator<ItemStack> it = matrix.iterator(); it.hasNext(); ) {
            ItemStack is = it.next();
            if (!is.hasItemMeta()) {
                continue;
            }
            ItemMeta meta = is.getItemMeta();

            if (meta.hasLore() && meta.getLore().containsAll(markMeta.getLore())) {
                it.remove();

                ItemStack result = matrix.get(0).clone();

                if (itemIsMarked(result)) {
                    event.getInventory().setResult(null);
                } else {
                    event.getInventory().setResult(addMarkToItem(result));
                }
            }
        }
    }

    private boolean isMarkedItemRecipe(Recipe recipe) {
        if (!(recipe instanceof ShapelessRecipe)) return false;

        NamespacedKey key = ((ShapelessRecipe) recipe).getKey();
        String markItemNamespace = KEY_MARK.getNamespace();
        return key.getNamespace().equals(markItemNamespace) &&
                key.getKey().startsWith(RECIPE_PREFIX);
    }
}
