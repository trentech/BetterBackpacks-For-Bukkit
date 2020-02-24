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
        	if(BetterBackpacks.getPlugin().getConfig().getString("Backpack" + x) != null) {
        		if(!BetterBackpacks.getPlugin().getConfig().getBoolean("Backpack" + x + ".Enable")) {
        			continue;
        		}
        		
            	getLogger().info("Registering Recipe for Backpack" + x);
            	Bukkit.addRecipe(RecipeBuilder.getRecipe("Backpack" + x, "backpack" + x, RecipeBuilder.getItem(x)));
        	}
        }
    }
    
    public static BetterBackpacks getPlugin() {
    	return plugin;
    }
}