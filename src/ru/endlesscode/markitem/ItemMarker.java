package ru.endlesscode.markitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import ru.endlesscode.markitem.misc.Config;

/**
 * Created by OsipXD on 10.09.2015
 * It is part of the MarkItem.
 * All rights reserved 2014 - 2015 © «EndlessCode Group»
 */
public class ItemMarker implements Listener {
    private final ItemStack mark;
    private int count = 0;

    public ItemMarker() {
        String[] textures = Config.getConfig().getString("mark.texture").split(":");

        if (Material.getMaterial(textures[0]) == null) {
            MarkItem.getInstance().getLogger().log(Level.WARNING, "Material {0} not found", textures[0]);
            this.mark = new ItemStack(Material.AIR);
            return;
        }

        ItemStack item = new ItemStack(Material.getMaterial(textures[0]));

        if (textures.length == 2) {
            item.setDurability(Byte.parseByte(textures[1]));
        }

        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("mark.name")));
        List<String> lore = new ArrayList<>();
        Collections.addAll(lore, ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("mark.lore")).split("\n"));
        im.setLore(lore);
        item.setItemMeta(im);

        this.mark = item;

        this.init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        ItemStack item;
        List<String> allowedList = Config.getConfig().getStringList("allowed");
        for (String allowed : allowedList) {
            if (allowed.contains("-")) {
                String[] splitted = allowed.split("-");
                int start = Integer.parseInt(splitted[0]);
                int end = Integer.parseInt(splitted[1]);
                for (int i = start; i <= end; i++) {
                    item = new ItemStack(i, 1, (short) -1);

                    if (!isDenied(item)) {
                        this.addRecipe(item);
                    }
                }
            } else if (allowed.contains("#")) {
                String[] splitted = allowed.split("#");
                int id = Integer.parseInt(splitted[0]);
                short data = Short.parseShort(splitted[1]);

                item = new ItemStack(id, 1, data);
                if (!isDenied(item)) {
                    this.addRecipe(item);
                }
            } else {
                int id = Integer.parseInt(allowed);

                item = new ItemStack(id, 1, (short) -1);
                if (!isDenied(item)) {
                    this.addRecipe(item);
                }
            }
        }

        MarkItem.getInstance().getLogger().log(Level.INFO, "{0} item(s) have been initialized", this.count);
    }

    @SuppressWarnings("deprecation")
    private boolean isDenied(ItemStack item) {
        List<String> deniedList = Config.getConfig().getStringList("denied");
        for (String denied : deniedList) {
            if (denied.contains("-")) {
                String[] splitted = denied.split("-");
                int start = Integer.parseInt(splitted[0]);
                int end = Integer.parseInt(splitted[1]);

                if (item.getType().getId() >= start && item.getType().getId() <= end) {
                    return true;
                }
            } else if (denied.contains("#")) {
                String[] splitted = denied.split("#");
                int id = Integer.parseInt(splitted[0]);
                short data = Short.parseShort(splitted[1]);

                if (item.getType().getId() == id && item.getDurability() == data) {
                    return true;
                }
            } else {
                int id = Integer.parseInt(denied);

                if (item.getType().getId() == id) {
                    return true;
                }
            }
        }

        return false;
    }

    private void addRecipe(ItemStack item) {
        this.count++;
        ShapelessRecipe recipe = new ShapelessRecipe(item);
        recipe.addIngredient(item.getData());
        recipe.addIngredient(this.mark.getData());
        MarkItem.getInstance().getServer().addRecipe(recipe);
    }

    private ItemStack addMark(ItemStack item) {
        if (!this.hasMark(item)) {
            ItemMeta im = item.getItemMeta();
            List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            lore.add(MarkItem.UNIQUE_MARK_TAG + ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("mark.text")));
            im.setLore(lore);
            item.setItemMeta(im);
        }

        return item;
    }

    public ItemStack removeMark(ItemStack item) {
        if (!this.hasMark(item)) {
            ItemMeta im = item.getItemMeta();
            List<String> lore = im.getLore();
            for (String s : lore) {
                if (s.startsWith(MarkItem.UNIQUE_MARK_TAG)) {
                    lore.remove(s);
                }
            }
            im.setLore(lore);
            item.setItemMeta(im);
        }
        return item;
    }

    public boolean hasMark(ItemStack item) {
        if (item.getItemMeta().hasLore()) {
            for (String s : item.getItemMeta().getLore()) {
                if (s.startsWith(MarkItem.UNIQUE_MARK_TAG)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack getMark() {
        return this.mark;
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

            if (matrix.contains(this.mark)) {
                matrix.remove(this.mark);
                ItemStack result = matrix.get(0).clone();

                if (this.isDenied(result) || this.hasMark(result)) {
                    event.getInventory().setResult(null);
                } else {
                    event.getInventory().setResult(this.addMark(result));
                }
            }
        }
    }
}
