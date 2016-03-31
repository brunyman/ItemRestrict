package net.craftersland.itemrestrict;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.craftersland.itemrestrict.restrictions.Brewing;
import net.craftersland.itemrestrict.restrictions.Crafting;
import net.craftersland.itemrestrict.restrictions.Creative;
import net.craftersland.itemrestrict.restrictions.Drop;
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
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemRestrict extends JavaPlugin {
	
	public static Logger log;
	public static ItemRestrict itemRestrict;
	
	public ArrayList<World> enforcementWorlds = new ArrayList<World>();
	public MaterialCollection ownershipBanned = new MaterialCollection();
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
	public MaterialCollection pickupBanned = new MaterialCollection();
	public MaterialCollection dropBanned = new MaterialCollection();
	public MaterialCollection worldBanned = new MaterialCollection();
	public Map<Boolean, Integer> worldScanner = new HashMap<Boolean, Integer>();
	public Map<Boolean, Integer> wearingScanner = new HashMap<Boolean, Integer>();
	
	public boolean mcpcServer = false;
	public boolean is19Server = false;
	
	private ConfigHandler configHandler;
	private RestrictedItemsHandler restrictedHandler;
	private WorldScanner ws;
	private WearingScanner es;
	private DisableRecipe ds;
	
	public void onEnable() {
		log = getLogger();
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
        ds = new DisableRecipe(this);
        
        //Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new Ownership(this), this);
    	pm.registerEvents(new Crafting(this), this);
    	pm.registerEvents(new Smelting(this), this);
    	pm.registerEvents(new Brewing(this), this);
    	pm.registerEvents(new Creative(this), this);
    	pm.registerEvents(new Usage(this), this);
    	pm.registerEvents(new Placement(this), this);
    	pm.registerEvents(new Pickup(this), this);
    	pm.registerEvents(new Drop(this), this);
    	CommandHandler cH = new CommandHandler(this);
    	getCommand("itemrestrict").setExecutor(cH);
    	
    	printConsoleStatus();
    	checkServerVersion();
    	
    	if (configHandler.getBoolean("General.Restrictions.ArmorWearingBans") == true) {
    		//Start the wearing scanner task
    		es.wearingScanTask();
    	}
    	if (configHandler.getBoolean("General.WorldScannerON") == true) {
    		//Start the world scanner task
    		ws.worldScanTask();
		}
    	
    	log.info("ItemRestrict has been successfully loaded!");
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
		pickupBanned.clear();
		dropBanned.clear();
		worldBanned.clear();
		
		//Load Configuration
        configHandler = new ConfigHandler(this);
        //Load Restricted Items
        restrictedHandler = new RestrictedItemsHandler(this);
        
        //Restore recipes
        ds.restoreRecipes();
        
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
        ds.disableRecipesTask(1);
        
        printConsoleStatus();
        
        log.info("Reload complete!");
	}
	
	public void onDisable() {
		log.info("ItemRestrict has been disabled.");
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
        if (configHandler.getBoolean("General.WorldScannerON") == true) {
    		log.info("WorldScanner is enabled!");
		} else {
			log.info("WorldScanner is disabled!");
		}
	}
	
	private void checkServerVersion() {
		String[] serverVersion = Bukkit.getBukkitVersion().split("-");
	    String version = serverVersion[0];
	    if (version.matches("1.6.4")) {
	    	log.info("1.6.4 Server version detected!");
	    	mcpcServer = true;
	    } else if (version.matches("1.9") || version.matches("1.9.1") || version.matches("1.9.2")) {
	    	is19Server = true;
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

}
