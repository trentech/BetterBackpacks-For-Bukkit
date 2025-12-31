package org.trentech.betterbackpacks;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    public static ConcurrentHashMap<String, Integer> recipes = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
		for (int x = 9; x <= 54; x += 9) {
			String backpack = "backpack" + x;
			
			if (BetterBackpacks.getPlugin().getConfig().getString(backpack) != null && BetterBackpacks.getPlugin().getConfig().getBoolean(backpack + ".enable")) {
				event.getPlayer().discoverRecipe(new NamespacedKey(BetterBackpacks.getPlugin(), backpack));
			} 
		}   	   	
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase("Backpack")) {
            return;
        }
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) {
            return;
        }

        Optional<BackpackData> backpackData = read(itemStack);
        
        if(backpackData.isEmpty()) {
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

        Optional<BackpackData> backpackData = read(itemStack);
        
        if(backpackData.isEmpty()) {
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

        Optional<BackpackData> backpackData = read(result);
        
        if(backpackData.isEmpty()) {
        	return;
        }

        BackpackData resultData = backpackData.get();

        int size = resultData.size;
        
        if (!recipes.containsKey("backpack" + size)) {
            return;
        }

        boolean correct = false;
        for(ItemStack itemStack : event.getInventory().getMatrix()) {
    		if(itemStack == null) {
    			continue;
    		} 
    		
            Optional<BackpackData> backpackIngredient = read(itemStack);
            
            if(backpackIngredient.isEmpty()) {
            	continue;
            }

            BackpackData backpack = backpackIngredient.get();
            
    		int oldSize = backpack.size;
    		
    		if(oldSize != recipes.get("backpack" + size)) {
    			event.getInventory().setResult(result);
    			return;
    		}
    		
    		if(!backpack.hasInventory()) {
    			correct = true;
    			break;
    		}
    		
            NBT.modify(result, nbt -> {
            	nbt.getCompound("Backpack").setByteArray("Inventory", backpack.inventory);
            });

    		event.getInventory().setResult(result);
    		
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

        
        Optional<BackpackData> backpackData = read(itemStack);
        
        if(backpackData.isEmpty()) {
        	return;
        }

        
        if (event.getClickedBlock() != null && (event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE) || event.getClickedBlock().getType().equals(Material.ENCHANTING_TABLE) || event.getClickedBlock().getType().equals(Material.STONECUTTER))) {
            return;
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof org.bukkit.block.Container) {
            return;
        }

        event.setCancelled(true);

        BackpackData backpack = backpackData.get();
        
        int size = backpack.size;
        ItemStack[] items;
        
        if(!backpack.hasInventory()) {
            items = BetterBackpacks.getPlugin().getServer().createInventory((InventoryHolder) player, size).getContents();

            NBT.modify(itemStack, nbt -> {
            	ReadWriteNBT backpck = nbt.getOrCreateCompound("Backpack");
            	
            	try {
					backpck.setByteArray("Inventory", Utils.serialize(items));
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
            });

            player.getInventory().setItemInMainHand(itemStack);
        } else {
            try {
                items = Utils.deserialize(backpackData.get().inventory);
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

        Optional<BackpackData> backpackData = read(itemStack);
        
        if(backpackData.isEmpty()) {
        	return;
        }
        BackpackData backpack = backpackData.get();

        int size = backpack.size;

        ItemStack[] invArray = inventory.getContents();
        ItemStack[] newInv = new ItemStack[size];

        for (int i = 0; i < size; i++) {
            newInv[i] = invArray[i];
        }

        NBT.modify(itemStack, nbt -> {
        	try {
				nbt.getCompound("Backpack").setByteArray("Inventory", Utils.serialize(newInv));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
        });
        
        event.getPlayer().getInventory().setItemInMainHand(itemStack);
    }
    
    public record BackpackData(int size, int id, byte[] inventory) {
        public boolean hasInventory() {
            return inventory != null && inventory.length > 0;
        }
    }
    
    public static Optional<BackpackData> read(ItemStack itemStack) {
        return Optional.ofNullable(NBT.get(itemStack, nbt -> {
        	if(!nbt.hasTag("Backpack")) return null;
        	
            ReadableNBT backpack = nbt.getCompound("Backpack");

            int size = backpack.getInteger("Size");
            int id = backpack.getInteger("Id");
            byte[] inventory = backpack.hasTag("Inventory") ? backpack.getByteArray("Inventory") : null;

            return new BackpackData(size, id, inventory);
        }));
    }
}