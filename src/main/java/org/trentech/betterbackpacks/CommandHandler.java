package org.trentech.betterbackpacks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("backpack") || label.equalsIgnoreCase("bp")) {
			if(args.length >= 1){
				if(args[0].equalsIgnoreCase("reload")){
					if(sender.hasPermission("BetterBackpacks.reload")){
						BetterBackpacks.getPlugin().reloadConfig();
						BetterBackpacks.getPlugin().saveConfig();

						sender.sendMessage(ChatColor.DARK_GREEN + "BetterBackpacks Reloaded!");
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
										int size = Integer.parseInt(args[2]);

										ItemStack item = RecipeBuilder.getItem(size);

										player.getInventory().addItem(item);
										player.sendMessage(ChatColor.DARK_GREEN + "You have been given a backpack!");
										sender.sendMessage(ChatColor.DARK_GREEN + player.getName() + " has recieved a backpack!");
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
