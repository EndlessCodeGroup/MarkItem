package ru.endlesscode.markitem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.endlesscode.markitem.misc.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Created by OsipXD on 10.09.2015
 * It is part of the MarkItem.
 * All rights reserved 2014 - 2015 © «EndlessCode Group»
 */
public class ItemMarker implements Listener {
    private final ItemStack mark;
    private static final NamespacedKey UNIQUE_MARK_TAG = new NamespacedKey(MarkItem.getInstance(), "markitem_marked");

    public ItemMarker() {
        String[] textures = Config.getConfig().getString("mark.texture").split(":");
        Material textureType = Material.getMaterial(textures[0]);

        if (textureType == null) {
            MarkItem.getInstance().getLogger().log(Level.WARNING, "Material {0} not found", textures[0]);
            this.mark = new ItemStack(Material.AIR);
            return;
        }

        ItemStack item = new ItemStack(textureType);

        if (textures.length == 2) try {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable) {
                ((Damageable)meta).setDamage(Integer.parseInt(textures[1], 0));
                item.setItemMeta(meta);
            } else {
                MarkItem.getInstance().getLogger().log(Level.WARNING, "Material {0} is not damageable", textures[0]);
            }
        } catch (NumberFormatException e) {
            MarkItem.getInstance().getLogger().log(Level.WARNING, "{0} is not a number", textures[1]);
        }

        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("mark.name")));
        List<String> lore = new ArrayList<>();
        Collections.addAll(lore, ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("mark.lore")).split("\n"));
        im.setLore(lore);
        item.setItemMeta(im);

        if (Config.getConfig().getBoolean("mark.glow", false)) {
            Glow.addGlow(item);
        }

        this.mark = item;
        this.init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        List<Pattern> denyPatterns = new ArrayList<>();
        Config.getConfig().getStringList("denied").forEach(s -> denyPatterns.add(Pattern.compile(s, Pattern.CASE_INSENSITIVE)));
        List<Pattern> allowPatterns = new ArrayList<>();
        Config.getConfig().getStringList("allowed").forEach(s -> allowPatterns.add(Pattern.compile(s, Pattern.CASE_INSENSITIVE)));

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

        int count = 0;
        for(Material type : allowed) {
            count++;
            addRecipe(type);
        }

        MarkItem.getInstance().getLogger().log(Level.INFO, "{0} item(s) have been initialized", count);
    }

    private void addRecipe(Material type) {
        ItemStack item = new ItemStack(type);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(
                MarkItem.getInstance(),
                "marked_" + type.name().toLowerCase(Locale.ENGLISH)
        ), item);
        recipe.addIngredient(item.getData());
        recipe.addIngredient(this.mark.getData());
        MarkItem.getInstance().getServer().addRecipe(recipe);
    }

    private ItemStack addMark(ItemStack item) {
        if (!this.hasMark(item)) {
            ItemMeta im = item.getItemMeta();
            List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            lore.add(this.getMarkText());
            im.setLore(lore);
            im.getPersistentDataContainer().set(UNIQUE_MARK_TAG, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(im);
        }

        return item;
    }

// --Commented out by Inspection START (06.10.2015 15:05):
//    public ItemStack removeMark(ItemStack item) {
//        if (!this.hasMark(item)) {
//            ItemMeta im = item.getItemMeta();
//            List<String> lore = im.getLore();
//
//            for (String s : lore) {
//                if (s.startsWith(MarkItem.UNIQUE_MARK_TAG)) {
//                    lore.remove(s);
//                }
//            }
//
//            im.setLore(lore);
//            item.setItemMeta(im);
//        }
//        return item;
//    }
// --Commented out by Inspection STOP (06.10.2015 15:05)

    public boolean hasMark(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(UNIQUE_MARK_TAG, PersistentDataType.BYTE);
    }

    public ItemStack getMark() {
        return this.mark;
    }

    private String getMarkText() {
        return ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("mark.text"));
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        List<ItemStack> matrix = new ArrayList<>(Arrays.asList(event.getInventory().getMatrix()));
        if (event.getRecipe() instanceof ShapelessRecipe) {
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item == null || item.getType() == Material.AIR) {
                    matrix.remove(item);
                }
            }

            if (matrix.size() != 2) {
                return;
            }

            for (Iterator<ItemStack> it = matrix.iterator(); it.hasNext();) {
                ItemStack is = it.next();
                if (!is.getItemMeta().hasLore()) {
                    continue;
                }

                if (is.getItemMeta().getLore().containsAll(this.getMark().getItemMeta().getLore())) {
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
    }
}
