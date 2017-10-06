package net.craftersland.itemrestrict;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.craftersland.itemrestrict.restrictions.BlockBreak;
import net.craftersland.itemrestrict.restrictions.Brewing;
import net.craftersland.itemrestrict.restrictions.Crafting;
import net.craftersland.itemrestrict.restrictions.Creative;
import net.craftersland.itemrestrict.restrictions.Drop;
import net.craftersland.itemrestrict.restrictions.OffHandSwap;
import net.craftersland.itemrestrict.restrictions.Ownership;
import net.craftersland.itemrestrict.restrictions.Pickup;
import net.craftersland.itemrestrict.restrictions.Placement;
import net.craftersland.itemrestrict.restrictions.Smelting;
import net.craftersland.itemrestrict.restrictions.Usage;
import net.craftersland.itemrestrict.utils.DisableRecipe;
import net.craftersland.itemrestrict.utils.MaterialCollection;
import net.craftersland.itemrestrict.utils.WearingScanner;
import net.craftersland.itemrestrict.utils.WorldScanner;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemRestrict extends JavaPlugin {
	
	public static Logger log;
	public static String pluginName = "ItemRestrict";
	
	public static ArrayList<World> enforcementWorlds = new ArrayList<World>();
	public static MaterialCollection ownershipBanned = new MaterialCollection();
	public MaterialCollection craftingBanned = new MaterialCollection();
	public MaterialCollection smeltingBanned = new MaterialCollection();
	public List<String> craftingDisabled = new ArrayList<String>();
	public List<String> smeltingDisabled = new ArrayList<String>();
	public List<Recipe> disabledRecipes = new ArrayList<Recipe>();
	public MaterialCollection brewingBanned = new MaterialCollection();
	public MaterialCollection wearingBanned = new MaterialCollection();
	public MaterialCollection creativeBanned = new MaterialCollection();
	public MaterialCollection usageBanned = new MaterialCollection();
	public MaterialCollection placementBanned = new MaterialCollection();
	public MaterialCollection blockBreakBanned = new MaterialCollection();
	public MaterialCollection pickupBanned = new MaterialCollection();
	public MaterialCollection dropBanned = new MaterialCollection();
	public MaterialCollection worldBanned = new MaterialCollection();
	public Map<Boolean, Integer> worldScanner = new HashMap<Boolean, Integer>();
	public Map<Boolean, Integer> wearingScanner = new HashMap<Boolean, Integer>();
	
	public boolean mcpcServer = false;
	public boolean is19Server = true;
	public boolean is112Server = false;
	
	private static ConfigHandler configHandler;
	private static RestrictedItemsHandler restrictedHandler;
	private static WorldScanner ws;
	private static WearingScanner es;
	private static DisableRecipe ds;
	private static SoundHandler sH;
	
	public void onEnable() {
		log = getLogger();
		checkServerVersion();
		worldScanner.put(false, 0);
		wearingScanner.put(false, 0);
		
		//Create ItemRestrict plugin folder
    	(new File("plugins"+System.getProperty("file.separator")+"ItemRestrict")).mkdir();
    	
    	//Load Configuration
        configHandler = new ConfigHandler(this);
        //Load Restricted Items
        restrictedHandler = new RestrictedItemsHandler(this);
        //Load Classes
        ws = new WorldScanner(this);
        es = new WearingScanner(this);
        if (is112Server == false) {
        	ds = new DisableRecipe(this);
        } else {
        	log.warning("Removing recipes from the game is not possible in 1.12 due to a spigot bug: https://goo.gl/4v71Zv .Use CraftingBanned feature until this is fixed!");
        }
        sH = new SoundHandler(this);
        
        //Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new Ownership(this), this);
    	pm.registerEvents(new Crafting(this), this);
    	pm.registerEvents(new Smelting(this), this);
    	pm.registerEvents(new Brewing(this), this);
    	pm.registerEvents(new Creative(this), this);
    	pm.registerEvents(new Usage(this), this);
    	pm.registerEvents(new Placement(this), this);
    	pm.registerEvents(new BlockBreak(this), this);
    	pm.registerEvents(new Pickup(this), this);
    	pm.registerEvents(new Drop(this), this);
    	if (is19Server == true) {
    		pm.registerEvents(new OffHandSwap(this), this);
    	}
    	CommandHandler cH = new CommandHandler(this);
    	getCommand("itemrestrict").setExecutor(cH);
    	
    	printConsoleStatus();
    	
    	if (configHandler.getBoolean("General.Restrictions.ArmorWearingBans") == true) {
    		//Start the wearing scanner task
    		es.wearingScanTask();
    	}
    	if (configHandler.getBoolean("General.WorldScannerON") == true) {
    		//Start the world scanner task
    		ws.worldScanTask();
		}
    	
    	log.info(pluginName + " loaded successfully!");
	}
	
	public void onReload() {
		log.info("Reloading config and RestrictedItems...");
		enforcementWorlds.clear();
		ownershipBanned.clear();
		craftingBanned.clear();
		smeltingBanned.clear();
		craftingDisabled.clear();
		brewingBanned.clear();
		wearingBanned.clear();
		creativeBanned.clear();
		usageBanned.clear();
		placementBanned.clear();
		blockBreakBanned.clear();
		pickupBanned.clear();
		dropBanned.clear();
		worldBanned.clear();
		
		//Load Configuration
        configHandler = new ConfigHandler(this);
        //Load Restricted Items
        restrictedHandler = new RestrictedItemsHandler(this);
        
        //Restore recipes
        if (is112Server == false) {
        	ds.restoreRecipes();
        }
        if (configHandler.getBoolean("General.WorldScannerON") == true && worldScanner.containsKey(false)) {
        	ws.worldScanTask();
        } else if (configHandler.getBoolean("General.WorldScannerON") == false && worldScanner.containsKey(true)) {
        	Bukkit.getScheduler().cancelTask(worldScanner.get(true));
        	worldScanner.clear();
        	worldScanner.put(false, 0);
        }
        if (configHandler.getBoolean("General.Restrictions.ArmorWearingBans") == true && wearingScanner.containsKey(false)) {
        	es.wearingScanTask();
        } else if (configHandler.getBoolean("General.Restrictions.ArmorWearingBans") == false && wearingScanner.containsKey(true)) {
        	Bukkit.getScheduler().cancelTask(wearingScanner.get(true));
        	wearingScanner.clear();
        	wearingScanner.put(false, 0);
        }
        
        //Disable Recipes Task
        if (is112Server == false) {
        	ds.disableRecipesTask(1);
        } else {
        	log.warning("Removing recipes from the game is not possible in 1.12 due to a spigot bug: https://goo.gl/4v71Zv .Use CraftingBanned feature until this is fixed!");
        }
        
        printConsoleStatus();
        
        log.info("Reload complete!");
	}
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		log.info(pluginName + " is disabled!");
	}
	
	private void printConsoleStatus() {
		if (configHandler.getBoolean("General.Restrictions.EnableBrewingBans") == true) {
    		log.info("Brewing restrictions enabled!");
    	} else {
    		log.info("Brewing restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.ArmorWearingBans") == true) {
    		log.info("Wearing restrictions enabled!");
    	} else {
    		log.info("Wearing restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.CreativeBans") == true) {
    		log.info("Creative restrictions enabled!");
    	} else {
    		log.info("Creative restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.PickupBans") == true) {
    		log.info("Pickup restrictions enabled!");
    	} else {
    		log.info("Pickup restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.DropBans") == true) {
    		log.info("Drop restrictions enabled!");
    	} else {
    		log.info("Drop restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.BreakBans") == true) {
    		log.info("Block break restrictions enabled!");
    	} else {
    		log.info("Block break restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.WorldScannerON") == true) {
    		log.info("WorldScanner is enabled!");
		} else {
			log.info("WorldScanner is disabled!");
		}
	}
	
	private void checkServerVersion() {
		String[] serverVersion = Bukkit.getBukkitVersion().split("-");
	    String version = serverVersion[0];
	    log.info("Server version detected: " + version);
	    if (version.matches("1.6.4")) {
	    	mcpcServer = true;
	    	is19Server = false;
	    } else if (version.matches("1.7.10") || version.matches("1.8") || version.matches("1.8.3") || version.matches("1.8.8") || version.matches("1.8.7") || version.matches("1.8.6") || version.matches("1.8.5") || version.matches("1.8.4")) {
	    	is19Server = false;
	    } else if (version.matches("1.12") || version.matches("1.12.1") || version.matches("1.12.2")) {
	    	is112Server = true;
	    }
	}
	
	//Getting other classes public
	public ConfigHandler getConfigHandler() {
		return configHandler;
	}
	public RestrictedItemsHandler getRestrictedItemsHandler() {
		return restrictedHandler;
	}
	public DisableRecipe getDisableRecipe() {
		return ds;
	}
	public SoundHandler getSoundHandler() {
		return sH;
	}

}
