package com.ashkiano.anvilenchantblocker;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnvilEnchantBlocker extends JavaPlugin implements Listener {

    private Set<Enchantment> forbiddenEnchants = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadForbiddenEnchants();
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 22040);
        this.getLogger().info("Thank you for using the AnvilEnchantBlocker plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");
    }

    private void loadForbiddenEnchants() {
        FileConfiguration config = getConfig();
        List<String> enchantNames = config.getStringList("forbidden-enchants");

        for (String enchantName : enchantNames) {
            Enchantment enchantment = Enchantment.getByName(enchantName);
            if (enchantment != null) {
                forbiddenEnchants.add(enchantment);
            } else {
                getLogger().warning("Invalid enchantment name in config: " + enchantName);
            }
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstItem = anvilInventory.getItem(0);
        ItemStack secondItem = anvilInventory.getItem(1);

        if (firstItem != null && secondItem != null) {
            if (hasForbiddenEnchants(firstItem) || hasForbiddenEnchants(secondItem)) {
                event.setResult(null);
            }
        }
    }

    private boolean hasForbiddenEnchants(ItemStack item) {
        for (Enchantment enchantment : forbiddenEnchants) {
            if (item.containsEnchantment(enchantment)) {
                return true;
            }
        }
        return false;
    }
}