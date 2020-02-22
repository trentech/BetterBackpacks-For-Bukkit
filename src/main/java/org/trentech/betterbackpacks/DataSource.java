package org.trentech.betterbackpacks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class DataSource extends SQLMethods{

	public static DataSource instance = new DataSource();
	
	public void saveBackpack(String uuid, String backpack, int size, ItemStack[] invArray){
		ByteArrayOutputStream inv = new ByteArrayOutputStream();
	    try {
	        BukkitObjectOutputStream invObjOS = new BukkitObjectOutputStream(inv);
	        invObjOS.writeObject(invArray);
	        invObjOS.close();
	    } catch (IOException ioexception) {
	        ioexception.printStackTrace();
	    }
	    setBackpackInv(uuid, backpack, inv.toByteArray());
	}
	
	public ItemStack[] getBackpackInventory(String uuid, String backpack){
        byte[] byteInv =  getBackpackInv(uuid, backpack);
        Object inv = null;
        if(byteInv != null){
    		ByteArrayInputStream ByteArIS = new ByteArrayInputStream(byteInv);           
            try {
                BukkitObjectInputStream invObjIS = new BukkitObjectInputStream(ByteArIS);
                inv = invObjIS.readObject();
                invObjIS.close();
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        }else{
        	int size = DataSource.instance.getBackpackSize(uuid, backpack);
        	inv = new ItemStack[size];
        }
		return (ItemStack[]) inv;
	}

	public void openBackpack(Player player, String backpackOwner, String backpack){
		if(DataSource.instance.backpackExist(backpackOwner, backpack)){
			if(backpackOwner.equalsIgnoreCase(player.getUniqueId().toString()) || player.hasPermission("BetterBackpacks.openothers")){
				ItemStack[] inv = DataSource.instance.getBackpackInventory(backpackOwner, backpack);
				int size = getBackpackSize(backpackOwner, backpack);
				Inventory openBackpack = BetterBackpacks.getPlugin().getServer().createInventory(player, size, backpack + ":" + BetterBackpacks.getPlugin().getServer().getPlayer(UUID.fromString(backpackOwner)).getName());
				openBackpack.setContents(inv);
				player.openInventory(openBackpack);
			}else{
				player.sendMessage(ChatColor.DARK_RED + "You do not have permission to open this backpack!");
			}
		}else{
			player.sendMessage(ChatColor.DARK_RED + "Backpack does not Exist!");
		}
	}
	
	public void backpackDateCheck(){	
		long days = BetterBackpacks.getPlugin().getConfig().getInt("Days-Before-Removal") * 86400;
		if(days != 0){
			Date date = new Date();
			HashMap<String, String> backpacks = getBackpackList();
			for(Map.Entry<String, String> entry : backpacks.entrySet()){
				String playerName = entry.getKey();
				String backpack = entry.getValue();
				String strBackPackDate = DataSource.instance.getBackpackDate(playerName, backpack);
				Date backpackDate = null;
				try {
					backpackDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strBackPackDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long compare = TimeUnit.MILLISECONDS.toSeconds(date.getTime() - backpackDate.getTime());
				if(days - compare <= 0){
					BetterBackpacks.getPlugin().getLogger().info(String.format("[%s] Deleting old backpack - " + backpack + ":" + playerName, new Object[] {BetterBackpacks.getPlugin().getDescription().getName()}));
					DataSource.instance.deleteBackpack(playerName, backpack);
				}
			}
		}
	}
}
