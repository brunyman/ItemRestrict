package net.craftersland.itemrestrict;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.itemsprocessor.MaterialCollection;
import net.craftersland.itemrestrict.itemsprocessor.MaterialData;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemRestrict extends JavaPlugin {
	//for logging to the console and log file
	public static Logger log;
	public static ItemRestrict itemrestrict;
	
	public ArrayList<World> enforcementWorlds = new ArrayList<World>();
	public MaterialCollection ownershipBanned = new MaterialCollection();
	public MaterialCollection craftingBanned = new MaterialCollection();
	public MaterialCollection brewingBanned = new MaterialCollection();
	public MaterialCollection wearingBanned = new MaterialCollection();
	public MaterialCollection creativeBanned = new MaterialCollection();
	public MaterialCollection usageBanned = new MaterialCollection();
	public MaterialCollection placementBanned = new MaterialCollection();
	public MaterialCollection worldBanned = new MaterialCollection();
	
	private ConfigHandler configHandler;
	private RestrictedItemsHandler restrictedHandler;
	private MaterialData materialData;
	private MaterialCollection materialCollection;
	private int nextChunkPercentile = 0;
	
	
	public void onEnable() {
		log = getLogger();
		log.info("Loading ItemRestrict v" + getDescription().getVersion() + "...");
		
		//Create ItemRestrict plugin folder
    	(new File("plugins"+System.getProperty("file.separator")+"ItemRestrict")).mkdir();
    	
    	//Load Configuration
        configHandler = new ConfigHandler(this);
        //Load Restricted Items
        restrictedHandler = new RestrictedItemsHandler(this);
        
        //Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new PlayerHandler(this), this);
    	pm.registerEvents(new EventsHandler(this), this);
    	CommandHandler cH = new CommandHandler(this);
    	getCommand("itemrestrict").setExecutor(cH);
    	
    	if (configHandler.getString("General.Restrictions.EnableBrewingBans").matches("true")) {
    		log.info("Brewing restrictions enabled!");
    	} else {
    		log.info("Brewing restrictions disabled!");
    	}
    	if (configHandler.getString("General.Restrictions.ArmorWearingBans").matches("true")) {
    		log.info("Wearing restrictions enabled!");
    		wearingRestrictions();
    	} else {
    		log.info("Wearing restrictions disabled!");
    	}
    	
    	if (configHandler.getString("General.WorldScannerON") == "true") {
    		log.info("WorldScanner is enabled.");
    		//start the repeating scan for banned items in loaded chunks
    		//runs every minute and scans 5% of loaded chunks.
    		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
    			@SuppressWarnings("deprecation")
				public void run() {
    				log.info("WorldScanner Task Started...");
    				ArrayList<World> worlds;
    				if(worldBanned.size() > 0) {
    					if (enforcementWorlds.size() == 0) {
    						worlds = (ArrayList<World>) getServer().getWorlds();
    					} else {
    						worlds = enforcementWorlds;
    					}
    					
    					for(int i = 0; i < worlds.size(); i++) {
    						World world = worlds.get(i);
    						try {
    							Chunk [] chunks = world.getLoadedChunks();
    							
    							//scan 5% of chunks each pass
        						int firstChunk = (int)(chunks.length * (nextChunkPercentile / 100f));
        						int lastChunk = (int)(chunks.length * ((nextChunkPercentile + 5) / 100f));
        						
        						//for each chunk to be scanned
        						for(int j = firstChunk; j < lastChunk; j++) {
        							Chunk chunk = chunks[j];
        							
        							//scan all its blocks for removable blocks
        							for(int x = 0; x < 16; x++) {
        								for(int y = 0; y < chunk.getWorld().getMaxHeight(); y++) {
        									for(int z = 0; z < 16; z++) {
        										final Block block = chunk.getBlock(x, y, z);
        										MaterialData materialInfo = new MaterialData(block.getTypeId(), block.getData(), null, null);
        										MaterialData bannedInfo = worldBanned.Contains(materialInfo);
        										if(bannedInfo != null) {
        											Bukkit.getScheduler().runTask(ItemRestrict.this, new Runnable() {
    													@Override
    													public void run() {
    														block.setType(Material.AIR);		
    													}
        												
        											});
        											
        											ItemRestrict.log.info("Removed " + bannedInfo.toString() + " @ " + getFriendlyLocationString(block.getLocation()));
        										}
        									}
        								}
        							}
        						}
    						} catch(Exception e) {
    							
    						}
    					}
    					
    					nextChunkPercentile += 5;
    					if(nextChunkPercentile >= 100) nextChunkPercentile = 0;
    					log.info("WorldScanner Task Ended.");
    				}
    			}
    		}, 20L * 60, 20L * 60);
		} else {
			log.info("WorldScanner is disabled.");
		}
    	
    	log.info("ItemRestrict has been successfully loaded!");
	}
	
	public void onReload() {
		log.info("Reloading config and RestrictedItems...");
		enforcementWorlds.clear();
		ownershipBanned.clear();
		craftingBanned.clear();
		brewingBanned.clear();
		creativeBanned.clear();
		usageBanned.clear();
		placementBanned.clear();
		worldBanned.clear();
		
		//Load Configuration
        configHandler = new ConfigHandler(this);
        //Load Restricted Items
        restrictedHandler = new RestrictedItemsHandler(this);
        
        log.info("Reload complete!");
	}
	
	public void onDisable() {
		log.info("ItemRestrict has been disabled.");
	}
	
	public static String getFriendlyLocationString(Location location) 
	{
		return location.getWorld().getName() + "(" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")";
	}
	
	//Getting other classes public
	public ConfigHandler getConfigHandler() {
		return configHandler;
	}
	public RestrictedItemsHandler getRestrictedItemsHandler() {
		return restrictedHandler;
	}
	public MaterialData getMaterialData() {
		return materialData;
	}
	public MaterialCollection getMaterialCollection() {
		return materialCollection;
	}
	
	//WEARING RESTRICTIONS TASK
	private void wearingRestrictions() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (final Player p : Bukkit.getOnlinePlayers()) {
					final ItemStack boots = p.getInventory().getBoots();
					final ItemStack leggings = p.getInventory().getLeggings();
					final ItemStack chestplate = p.getInventory().getChestplate();
					final ItemStack helmet = p.getInventory().getHelmet();
					
					if (boots != null) {
						MaterialData bannedInfo = getRestrictedItemsHandler().isBanned(ActionType.Wearing, p, boots.getTypeId(), boots.getData().getData(), p.getLocation());
						if (bannedInfo != null) {
							getConfigHandler().printMessage(p, "chatMessages.wearingRestricted", bannedInfo.reason);
							Bukkit.getScheduler().runTask(ItemRestrict.this, new Runnable() {
								@Override
								public void run() {
									
									p.getWorld().dropItem(p.getLocation(), boots);
									p.getInventory().setBoots(new ItemStack(Material.AIR));
									p.updateInventory();
									p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
								}
							});
						}
					}
					if (leggings != null) {
						MaterialData bannedInfo = getRestrictedItemsHandler().isBanned(ActionType.Wearing, p, leggings.getTypeId(), leggings.getData().getData(), p.getLocation());
						if (bannedInfo != null) {
							getConfigHandler().printMessage(p, "chatMessages.wearingRestricted", bannedInfo.reason);
							Bukkit.getScheduler().runTask(ItemRestrict.this, new Runnable() {
								@Override
								public void run() {
									
									p.getWorld().dropItem(p.getLocation(), leggings);
									p.getInventory().setLeggings(new ItemStack(Material.AIR));
									p.updateInventory();
									p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
								}
							});
						}
					}
					if (chestplate != null) {
						MaterialData bannedInfo = getRestrictedItemsHandler().isBanned(ActionType.Wearing, p, chestplate.getTypeId(), chestplate.getData().getData(), p.getLocation());
						if (bannedInfo != null) {
							getConfigHandler().printMessage(p, "chatMessages.wearingRestricted", bannedInfo.reason);
							Bukkit.getScheduler().runTask(ItemRestrict.this, new Runnable() {
								@Override
								public void run() {
									
									p.getWorld().dropItem(p.getLocation(), chestplate);
									p.getInventory().setChestplate(new ItemStack(Material.AIR));
									p.updateInventory();
									p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
								}
							});
						}
					}
					if (helmet != null) {
						MaterialData bannedInfo = getRestrictedItemsHandler().isBanned(ActionType.Wearing, p, helmet.getTypeId(), helmet.getData().getData(), p.getLocation());
						if (bannedInfo != null) {
							getConfigHandler().printMessage(p, "chatMessages.wearingRestricted", bannedInfo.reason);
							Bukkit.getScheduler().runTask(ItemRestrict.this, new Runnable() {
								@Override
								public void run() {
									
									p.getWorld().dropItem(p.getLocation(), helmet);
									p.getInventory().setHelmet(new ItemStack(Material.AIR));
									p.updateInventory();
									p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
								}
							});
						}
					}
				}
			}
		}, 20L, 20L);
	}

}
