package org.trentech.betterbackpacks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandHandler implements CommandExecutor {
	
	private BetterBackpacks plugin;
	public CommandHandler(BetterBackpacks plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("backpack") || label.equalsIgnoreCase("bp")) {
			if(args.length >= 1){
				if(args[0].equalsIgnoreCase("reload")){
					if(sender.hasPermission("BetterBackpacks.reload")){
						this.plugin.reloadConfig();
						this.plugin.saveConfig();
						DataSource.instance.dispose();
						try {
							DataSource.instance.connect();
						} catch (Exception e) {
							this.plugin.log.severe(String.format("[%s] - Unable to connect to database!", new Object[] { this.plugin.getDescription().getName() }));
						}
						sender.sendMessage(ChatColor.DARK_GREEN + "BetterBackpacks Reloaded!");
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}							
				}else if(args[0].equalsIgnoreCase("remove")){
					if(sender.hasPermission("BetterBackpacks.remove")){
						if(args.length == 3){
							String uuid = BetterBackpacks.getPlugin().getServer().getPlayerExact(args[1]).getUniqueId().toString();
							if(DataSource.instance.backpackExist(uuid, args[2])){
								DataSource.instance.deleteBackpack(uuid, args[2]);
								sender.sendMessage(ChatColor.DARK_GREEN + "Backpack removed!");
							}else{
								sender.sendMessage(ChatColor.DARK_RED + "Backpack does not exist!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/backpack remove [player] [backpack]");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}					
				}else if(args[0].equalsIgnoreCase("give")){
					if(sender.hasPermission("BetterBackpacks.give")){
						if(args.length == 3){
							if(BetterBackpacks.getPlugin().getServer().getPlayer(BetterBackpacks.getPlugin().getServer().getPlayerExact(args[1]).getUniqueId().toString()).isOnline()){
								if(args[2].equalsIgnoreCase("9") || args[2].equalsIgnoreCase("18") || args[2].equalsIgnoreCase("27") || args[2].equalsIgnoreCase("36") || args[2].equalsIgnoreCase("45") || args[2].equalsIgnoreCase("54")){
									Player player = BetterBackpacks.getPlugin().getServer().getPlayerExact(args[1]);
									if(player.getInventory().firstEmpty() > -1){
										String playerName = player.getName();
										String uuid = player.getUniqueId().toString();
										DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										Date date = new Date();
										String strDate = dateFormat.format(date).toString();
										DataSource.instance.createBackpack(uuid, "temp", Integer.parseInt(args[2]), null, strDate);
										int backpackId = DataSource.instance.getBackpackId(uuid, "temp");
										String backpack = "Backpack-" + backpackId;
										DataSource.instance.setBackpackName(uuid, backpack, backpackId);
										ItemStack item = RecipeBuilder.getItem(Integer.parseInt(args[2]));							
										ItemMeta itemMeta = item.getItemMeta();
										itemMeta.setDisplayName("Backpack");
										ArrayList<String> lore = new ArrayList<String>();
										lore.clear();
										lore.add(backpack);
										lore.add(ChatColor.GREEN + playerName);
										itemMeta.setLore(lore);
										item.setItemMeta(itemMeta);
										player.getInventory().addItem(item);
										player.sendMessage(ChatColor.DARK_GREEN + "You have been given a backpack!");
										sender.sendMessage(ChatColor.DARK_GREEN + playerName + " has recieved a backpack!");
									}else{
										sender.sendMessage(ChatColor.DARK_RED + "Player does not have enough room in inventory!");
									}
								}else{
									sender.sendMessage(ChatColor.DARK_RED + "Invalid Size. Must be intervals of 9, Max 54!");
								}
							}else{
								sender.sendMessage(ChatColor.DARK_RED + "Player does not exist! Make sure they are online!");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + "/bp give [player] [size]");
						}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
					}					
				}				
			}else{
				sender.sendMessage(ChatColor.DARK_GREEN + "Command List:");
				sender.sendMessage(ChatColor.YELLOW + "/bp reload");
				sender.sendMessage(ChatColor.YELLOW + "/bp remove [player] [backpack]");
				sender.sendMessage(ChatColor.YELLOW + "/bp give [player] [size]");
			}
		}
		return true;
	}

}
