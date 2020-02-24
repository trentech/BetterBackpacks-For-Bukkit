package org.trentech.betterbackpacks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterBackpacks extends JavaPlugin {

	public static BetterBackpacks plugin;

    @Override
    public void onEnable(){
    	plugin = this;
    	
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    	
		getServer().getPluginManager().registerEvents(new EventListener(), this);

        for(int x = 9; x <= 54; x = x+9) {
        	String backpack = "backpack" + x;
        	if(BetterBackpacks.getPlugin().getConfig().getString(backpack) != null) {
        		if(!BetterBackpacks.getPlugin().getConfig().getBoolean(backpack + ".enable")) {
        			continue;
        		}
        		
            	getLogger().info("Registering Recipe for Backpack" + x);
            	Bukkit.addRecipe(RecipeBuilder.getRecipe(backpack, RecipeBuilder.getItem(x)));
        	}
        }
    }
    
    public static BetterBackpacks getPlugin() {
    	return plugin;
    }
}