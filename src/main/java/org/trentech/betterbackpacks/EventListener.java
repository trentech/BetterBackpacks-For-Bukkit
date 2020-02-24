package org.trentech.betterbackpacks;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;

public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerClickEvent(InventoryClickEvent event){		
		if(event.getCurrentItem() != null){
			if(event.getCurrentItem().hasItemMeta()){
				if(event.getCurrentItem().getItemMeta().hasDisplayName() && event.getInventory().getType() == InventoryType.ANVIL){
					if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Backpack")){
						event.setCancelled(true);
						return;
					}
				}
			}
		}	
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && player.getInventory().getItemInMainHand().hasItemMeta()) {
			ItemStack itemStack = player.getInventory().getItemInMainHand();
			ItemMeta itemMeta = itemStack.getItemMeta();
			
			if(itemMeta.hasDisplayName()){
				if(itemMeta.getDisplayName().equalsIgnoreCase("Backpack")) {
					event.setCancelled(true);
					
					NBTItem nbti = new NBTItem(itemStack);
					NBTCompound backpack = nbti.getCompound("Backpack");
					int size = backpack.getInteger("Size");
					ItemStack[] items;
					
					if(!backpack.hasKey("Inventory")) {
						items = BetterBackpacks.getPlugin().getServer().createInventory(player, size).getContents();
						
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
					
					Inventory inventory = BetterBackpacks.getPlugin().getServer().createInventory(player, size, "Backpack");
					inventory.setContents(items);

					player.openInventory(inventory);
					
//					List<String> lore = player.getInventory().getItemInMainHand().getItemMeta().getLore();
//					
//					int size = Integer.parseInt(lore.get(0).replace("Size: ", ""));
//					
//					Backpack backpack = new Backpack(BetterBackpacks.getPlugin().getServer().createInventory(player, size).getContents(), new Date(), size);
//
//					if(lore.size() == 1){
//						lore.add(1, ChatColor.GREEN + player.getName());
//						lore.add(2, backpack.getId().toString());
//						itemMeta.setLore(lore);
//						player.getInventory().getItemInMainHand().setItemMeta(itemMeta);
//						
//						DataSource.instance.openBackpack(player, player.getName(), backpack);
//					} else {
//						String owner = lore.get(1);
//						String id = lore.get(2);
//						
//						Optional<Backpack> optional = DataSource.instance.getBackpack(id);
//						
//						if(optional.isPresent()) {						
//							if(player.getInventory().getItemInMainHand().getAmount() == 1){	
//								DataSource.instance.openBackpack(player, ChatColor.stripColor(owner), optional.get());
//							}else{
//								player.sendMessage(ChatColor.RED + "Do not stack backpacks!");
//							}
//						}
//					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCloseEvent(InventoryCloseEvent event){
		if(event.getView().getTitle().contains("Backpack")){
			ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
			
			NBTItem nbti = new NBTItem(itemStack);
			NBTCompound backpack = nbti.getCompound("Backpack");
			int size = backpack.getInteger("Size");		
			Inventory inv = event.getInventory();
			ItemStack[] invArray = inv.getContents();
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
}
