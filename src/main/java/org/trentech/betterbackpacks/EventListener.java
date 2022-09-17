package org.trentech.betterbackpacks;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    public static ConcurrentHashMap<String, Integer> recipes = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase("Backpack")) {
            return;
        }
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) {
            return;
        }

        NBTItem nbtResult = new NBTItem(itemStack);

        if (!nbtResult.hasKey("Backpack").booleanValue()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        ItemStack itemStack = event.getCursor();

        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
            return;
        }

        NBTItem nbtResult = new NBTItem(itemStack);

        if (!nbtResult.hasKey("Backpack").booleanValue()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();

        if (result == null) {
            return;
        }

        if (!(event.getRecipe() instanceof org.bukkit.inventory.ShapedRecipe)) {
            return;
        }

        NBTItem nbtResult = new NBTItem(result);

        if (!nbtResult.hasKey("Backpack").booleanValue()) {
            return;
        }

        NBTCompound resultTag = nbtResult.getCompound("Backpack");

        int size = resultTag.getInteger("Size").intValue();

        if (!recipes.containsKey("backpack" + size)) {
            return;
        }

        boolean correct = false;
        for(ItemStack itemStack : event.getInventory().getMatrix()) {
    		if(itemStack == null) {
    			continue;
    		} 		
    		NBTItem nbtIngredient = new NBTItem(itemStack);
    		
    		if(!nbtIngredient.hasKey("Backpack")) {
    			continue;
    		}
    		NBTCompound backpack = nbtIngredient.getCompound("Backpack");
    		
    		int oldSize = backpack.getInteger("Size");
    		
    		if(oldSize != recipes.get("backpack" + size)) {
    			event.getInventory().setResult(nbtResult.getItem());
    			return;
    		}
    		
    		if(!backpack.hasKey("Inventory")) {
    			correct = true;
    			break;
    		}
    		
    		resultTag.setByteArray("Inventory", backpack.getByteArray("Inventory"));
    		
    		event.getInventory().setResult(nbtResult.getItem());
    		
    		correct = true;
    	}
    	
    	if(!correct) {
    		event.getInventory().setResult(new ItemStack(Material.AIR));
    	}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().equals(Material.AIR)) {
            return;
        }

        NBTItem nbti = new NBTItem(itemStack);

        if (!nbti.hasKey("Backpack").booleanValue()) {
            return;
        }

        if (event.getClickedBlock() != null && (event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE) || event.getClickedBlock().getType().equals(Material.ENCHANTING_TABLE) || event.getClickedBlock().getType().equals(Material.STONECUTTER))) {
            return;
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof org.bukkit.block.Container) {
            return;
        }

        event.setCancelled(true);

        NBTCompound backpack = nbti.getCompound("Backpack");
        int size = backpack.getInteger("Size").intValue();
        ItemStack[] items;
        
        if (!backpack.hasKey("Inventory").booleanValue()) {
            items = BetterBackpacks.getPlugin().getServer().createInventory((InventoryHolder) player, size).getContents();

            try {
                backpack.setByteArray("Inventory", Utils.serialize(items));
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
                return;
            }
            
            itemStack = nbti.getItem();
            player.getInventory().setItemInMainHand(itemStack);
        } else {
            try {
                items = Utils.deserialize(backpack.getByteArray("Inventory"));
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                return;
            }
        }
        
        Inventory inventory = BetterBackpacks.getPlugin().getServer().createInventory((InventoryHolder) player, size, "Backpack");
        inventory.setContents(items);

        player.openInventory(inventory);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCloseEvent(InventoryCloseEvent event) {
        if (!event.getView().getTitle().contains("Backpack")) {
            return;
        }

        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        Inventory inventory = event.getInventory();

        NBTItem nbti = new NBTItem(itemStack);
        NBTCompound backpack = nbti.getCompound("Backpack");
        int size = backpack.getInteger("Size").intValue();

        ItemStack[] invArray = inventory.getContents();
        ItemStack[] newInv = new ItemStack[size];

        for (int i = 0; i < size; i++) {
            newInv[i] = invArray[i];
        }

        try {
            backpack.setByteArray("Inventory", Utils.serialize(newInv));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        itemStack = nbti.getItem();

        event.getPlayer().getInventory().setItemInMainHand(itemStack);
    }
}