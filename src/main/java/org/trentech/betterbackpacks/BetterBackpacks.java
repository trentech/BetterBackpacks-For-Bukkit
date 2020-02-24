package org.trentech.betterbackpacks;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterBackpacks extends JavaPlugin {

	public final Logger log = Logger.getLogger("Minecraft");

	private EventListener eventlistener = new EventListener();
	private CommandHandler cmdExecutor = new CommandHandler();
	public static BetterBackpacks plugin;

    @Override
    public void onEnable(){
    	plugin = this;

		getServer().getPluginManager().registerEvents(this.eventlistener, this);
		getCommand("backpack").setExecutor(cmdExecutor);
    	
    	getConfig().options().copyDefaults(true);
    	saveConfig();

        for(int x = 9; x <= 54; x = x+9) {
        	if(BetterBackpacks.getPlugin().getConfig().getString("Backpack" + x) != null) {
            	getLogger().info("Registering Recipe for Backpack" + x);
            	Bukkit.addRecipe(RecipeBuilder.getRecipe("Backpack" + x, "backpack" + x, RecipeBuilder.getItem(x)));
        	}
        }
    }
    
    public static BetterBackpacks getPlugin() {
    	return plugin;
    }
}
