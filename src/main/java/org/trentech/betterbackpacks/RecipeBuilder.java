package org.trentech.betterbackpacks;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class RecipeBuilder implements Listener {
    public static ConcurrentHashMap<UUID, String> hash = new ConcurrentHashMap<>();

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!hash.containsKey(player.getUniqueId())) {
            return;
        }

        if (!(event.getInventory() instanceof CraftingInventory)) {
            return;
        }
        CraftingInventory inventory = (CraftingInventory) event.getInventory();

        if (event.getSlot() != 0) {
            return;
        }

        if (inventory.getResult() != null) {
            player.sendMessage(ChatColor.RED + "This Recipe Already Exists.");
            event.setCancelled(true);

            return;
        }
        
        for (int x = 0; x <= (inventory.getMatrix()).length - 1; x++) {
            FileConfiguration config = BetterBackpacks.getPlugin().getConfig();

            if (inventory.getMatrix()[x] == null) {
                config.set(hash.get(player.getUniqueId()) + ".slot" + x, (new ItemStack(Material.AIR)).serialize());
            } else {
                config.set(hash.get(player.getUniqueId()) + ".slot" + x, inventory.getMatrix()[x].serialize());
            }
        }

        BetterBackpacks.getPlugin().saveConfig();

        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "Recipe saved in config. Restart Server for changes to take effect.");

        hash.remove(player.getUniqueId());
    }
    
    public static ShapedRecipe getRecipe(String path, ItemStack result) {     
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey((Plugin) BetterBackpacks.getPlugin(), path), result);
        char[] shapes = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};

        int index;
        if (BetterBackpacks.getPlugin().getConfig().getString(path + ".recipe.slot4") == null) {
            recipe.shape(new String[] {"AB", "CD"});
            index = 4;
        } else {
            index = 8;
            recipe.shape(new String[] {"ABC", "DEF", "GHI"});
        }

        for (int x = 0; x <= index; x++) {
            String ingredient = BetterBackpacks.getPlugin().getConfig().getString(path + ".recipe.slot" + x);

            if (ingredient.startsWith("BACKPACK")) {
                recipe.setIngredient(shapes[x], Material.getMaterial(BetterBackpacks.getPlugin().getConfig().getString("backpack" + Integer.parseInt(ingredient.replace("BACKPACK", "")) + ".result")));
                EventListener.recipes.put(path, Integer.valueOf(Integer.parseInt(ingredient.replace("BACKPACK", ""))));
            } else {
                recipe.setIngredient(shapes[x], Material.getMaterial(ingredient));
            }
        }

        return recipe;
    }

    public static ItemStack getItem(int number) {
        ItemStack itemStack = new ItemStack(Material.getMaterial(BetterBackpacks.getPlugin().getConfig().getString("backpack" + number + ".result")));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("Backpack");
        itemStack.setItemMeta(itemMeta);

        NBTItem nbti = new NBTItem(itemStack);

        NBTCompound backpack = nbti.addCompound("Backpack");
        backpack.setInteger("Size", Integer.valueOf(number));
        backpack.setInteger("Id", Integer.valueOf(UUID.randomUUID().hashCode()));

        return nbti.getItem();
    }
}