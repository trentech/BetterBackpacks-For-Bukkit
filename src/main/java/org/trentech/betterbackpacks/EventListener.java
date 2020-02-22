package org.trentech.betterbackpacks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventListener implements Listener {

	private BetterBackpacks plugin;
	public EventListener(BetterBackpacks plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerClickEvent(InventoryClickEvent event){		
		if(event.getCurrentItem() != null){
			for(Entry<Integer, ItemStack> entry : getBackpacks().entrySet()){
				ItemStack itemStack = entry.getValue();
				
				if(event.getCurrentItem().getType() == itemStack.getType() && event.getCurrentItem().hasItemMeta()){
					if(event.getCurrentItem().getItemMeta().hasDisplayName()){
						if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Backpack-") && (event.getView().getTitle().contains("Backpack-") || event.getInventory().getType() == InventoryType.ANVIL)){
							event.setCancelled(true);
							break;
						}
					}
					if(event.getCurrentItem().getItemMeta().hasLore()){
						if((event.getCurrentItem().getItemMeta().getLore().get(0).contains("Backpack-") || event.getCurrentItem().getItemMeta().getLore().get(0).equalsIgnoreCase("New Backpack")) && event.getView().getTitle().contains("Backpack-")){
							event.setCancelled(true);
							break;
						}
					}else{
						if(event.getCurrentItem().getItemMeta().hasDisplayName()){
							if((event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("New Backpack") || event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Backpack")) && event.getInventory().getType() == InventoryType.ANVIL){
								event.setCancelled(true);
								break;
							}
						}
					}
				}
			}
		}	
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLoginEvent(PlayerJoinEvent event){
		String uuid = event.getPlayer().getUniqueId().toString();

		if(!DataSource.instance.tableExist(uuid)) {
			plugin.log.info(String.format("[%s] Creating player table", new Object[] { plugin.getDescription().getName() }));
			DataSource.instance.createTable(uuid);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			for(Entry<Integer, ItemStack> entry : getBackpacks().entrySet()){
				ItemStack itemStack = entry.getValue();
				
				if(player.getInventory().getItemInMainHand().getType() == itemStack.getType()){				
					if(player.getInventory().getItemInMainHand().getItemMeta().hasLore()){
						List<String> lore = player.getInventory().getItemInMainHand().getItemMeta().getLore();
						
						if(lore.get(0).equalsIgnoreCase("New Backpack")){
							event.setCancelled(true);
							if(player.getInventory().getItemInMainHand().getAmount() == 1){							
								String uuid = player.getUniqueId().toString();

								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date date = new Date();
								String strDate = dateFormat.format(date).toString();
								DataSource.instance.createBackpack(uuid, "temp", entry.getKey(), null, strDate);
								int backpackId = DataSource.instance.getBackpackId(uuid, "temp");
								String backpack = "Backpack-" + backpackId;
								DataSource.instance.setBackpackName(uuid, backpack, backpackId);
								ItemMeta itemMeta = player.getInventory().getItemInMainHand().getItemMeta();
								lore.clear();
								lore.add(backpack);
								lore.add(ChatColor.GREEN + player.getName());
								itemMeta.setLore(lore);
								player.getInventory().getItemInMainHand().setItemMeta(itemMeta);
								DataSource.instance.openBackpack(player, uuid, backpack);
								break;
							}else{
								player.sendMessage(ChatColor.RED + "Do not stack backpacks!");
							}
						}else if(lore.get(0).contains("Backpack-")){
							event.setCancelled(true);
							if(player.getInventory().getItemInMainHand().getAmount() == 1){	
								String backpack = lore.get(0);
								String backpackOwner = lore.get(1).replace("[", "").replace("]", "");
								DataSource.instance.openBackpack(player, BetterBackpacks.getPlugin().getServer().getPlayerExact(ChatColor.stripColor(backpackOwner)).toString(), ChatColor.stripColor(backpack));
								break;
							}else{
								player.sendMessage(ChatColor.RED + "Do not stack backpacks!");
							}
						}				
					}
				}			
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCloseEvent(InventoryCloseEvent event){
		if(event.getView().getTitle().contains("Backpack")){
			String[] title = event.getView().getTitle().split(":");
			String backpack = title[0];
			String uuidOwner = BetterBackpacks.getPlugin().getServer().getPlayerExact(title[1]).toString();
			Inventory inv = event.getInventory();
			ItemStack[] invArray = inv.getContents();
			List<ItemStack> list = new LinkedList<ItemStack>();
		    int index = 1;
		    int size = DataSource.instance.getBackpackSize(uuidOwner, backpack);
		    for(ItemStack item : invArray){
				if(index < size){
					list.add(item);
				}
		    }
		    ItemStack[] newInv = new ItemStack[size];
		    for (int i = 0; i < size; i++) {
		    	newInv[i] = ((ItemStack) list.get(i));
		    }
			DataSource.instance.saveBackpack(uuidOwner, backpack, size, newInv);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String strDate = dateFormat.format(date).toString();
			DataSource.instance.setBackpackDate(uuidOwner, backpack, strDate);
		}
	}

	private HashMap<Integer, ItemStack> getBackpacks() {
		HashMap<Integer, ItemStack> list = new HashMap<>();
		FileConfiguration config = BetterBackpacks.getPlugin().getConfig();
		
        for(int x = 9; x <= 54; x = x*2) {
        	if(config.getString("Backpack" + x) != null) {
    			ConfigurationSection map = config.getConfigurationSection("Backpack" + x + ".Result");
    			
    			if(map != null) {
    				list.put(x, ItemStack.deserialize(map.getValues(true)));
    			}
        	}
        }
        
        return list;
	}
}
