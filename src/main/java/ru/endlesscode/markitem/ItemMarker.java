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
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.endlesscode.markitem.ItemUtils.KEY_MARK;
import static ru.endlesscode.markitem.ItemUtils.KEY_MARKED;

public class ItemMarker implements Listener {
    private final ItemStack mark;
    private final ItemMeta markMeta;
    private final String markText;

    private static final String RECIPE_PREFIX = "marked_";

    public ItemMarker(Config config) {
        markText = config.getMarkText();
        String[] textures = config.getMarkTexture().split(":");
        Material textureType = Material.getMaterial(textures[0]);

        if (textureType == null) {
            MarkItem.getInstance().getLogger().log(Level.WARNING, "Material {0} not found", textures[0]);
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
                MarkItem.getInstance().getLogger().log(Level.WARNING, "Material {0} is not damageable", textures[0]);
            }
        } catch (NumberFormatException e) {
            MarkItem.getInstance().getLogger().log(Level.WARNING, "{0} is not a number", textures[1]);
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
        this.init(config);
    }

    @SuppressWarnings("deprecation")
    private void init(Config config) {
        List<Pattern> denyPatterns = config.getDenied();
        List<Pattern> allowPatterns = config.getAllowed();

        Set<Material> allowed = EnumSet.noneOf(Material.class);
        for(Material type : Material.values()) {
            if(type.isLegacy() || !type.isItem()) continue;
            String typeStr = type.name();

            boolean denied = false;
            for(Pattern pattern : denyPatterns) {
                if(pattern.matcher(typeStr).matches()) {
                    denied = true;
                    break;
                }
            }
            if(denied) continue;

            for(Pattern pattern : allowPatterns) {
                if(pattern.matcher(typeStr).matches()) {
                    allowed.add(type);
                    break;
                }
            }
        }

        for(Material type : allowed) {
            addRecipe(type);
        }

        MarkItem.getInstance().getLogger().log(Level.INFO, "{0} item(s) have been initialized", allowed.size());
    }

    private void addRecipe(Material type) {
        ItemStack item = new ItemStack(type);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(
                MarkItem.getInstance(),
                "marked_" + type.name().toLowerCase(Locale.ENGLISH)
        ), item);
        recipe.addIngredient(item.getData());
        recipe.addIngredient(this.mark.getData());
        Bukkit.addRecipe(recipe);
    }

    private ItemStack addMark(ItemStack item) {
        if (!this.hasMark(item)) {
            ItemMeta im = item.getItemMeta();
            List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            lore.add(this.markText);
            im.setLore(lore);
            im.getPersistentDataContainer().set(KEY_MARKED, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(im);
        }

        return item;
    }

    public boolean hasMark(ItemStack item) {
        return item.hasItemMeta() &&
                item.getItemMeta().getPersistentDataContainer().has(KEY_MARKED, PersistentDataType.BYTE);
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
                .filter(ItemUtils::isNotEmpty)
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

                if (this.hasMark(result)) {
                    event.getInventory().setResult(null);
                } else {
                    event.getInventory().setResult(this.addMark(result));
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
