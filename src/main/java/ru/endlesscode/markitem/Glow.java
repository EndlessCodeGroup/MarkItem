package ru.endlesscode.markitem;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

class Glow extends EnchantmentWrapper {
    private static final Enchantment GLOW = new Glow();

    private Glow() {
        super("glow_effect");
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return true;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public @NotNull String getName() {
        return "Glow";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    public static void addGlow(ItemStack item) {
        item.addEnchantment(GLOW, 1);
    }

    public static void register() {
        try {
            Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNew.setAccessible(true);
            acceptingNew.set(null, true);
            Enchantment.registerEnchantment(GLOW);
        } catch (Exception ignored) {
        }
    }
}