package net.craftersland.itemrestrict;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ConfigHandler {
	
	private ItemRestrict ir;
	
	public ConfigHandler(final ItemRestrict ir) {
		this.ir = ir;
		
		        //Create the config file
				if (!(new File("plugins"+System.getProperty("file.separator")+"ItemRestrict"+System.getProperty("file.separator")+"config.yml").exists())) {
					ItemRestrict.log.info("No config file found! Creating new one...");
					ir.saveResource("config.yml", false);
				}
				//Load the config file
				try {
					ir.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"ItemRestrict"+System.getProperty("file.separator")+"config.yml"));
				} catch (Exception e) {
					ItemRestrict.log.severe("Could not load config file! Error: " + e.getMessage());
					e.printStackTrace();
				}
				
				//Check the worlds the restrictions will take place
				if (getString("General.EnableOnAllWorlds") == "true") {
					//Restrictions will take place on all worlds
					ItemRestrict.log.info("Restrictions enabled on all worlds.");
				} else {
					getWorldsTask();				
				}
				
	}
	
	//Read config data
	public String getString(String key) {
		if (!ir.getConfig().contains(key)) {
			ir.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the ItemRestrict folder! (Try generating a new one by deleting the current)");
			return "Error could not locate in config:"+key;
		}
			return ir.getConfig().getString(key);
	}
	
	public Integer getInteger(String key) {
		if (!ir.getConfig().contains(key)) {
			ir.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the ItemRestrict folder! (Try generating a new one by deleting the current)");
			return null;
		}
			return ir.getConfig().getInt(key);
	}
	
	public Boolean getBoolean(String key) {
		if (!ir.getConfig().contains(key)) {
			ir.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the ItemRestrict folder! (Try generating a new one by deleting the current)");
			return null;
		}
			return ir.getConfig().getBoolean(key);
	}
	
	//Send chat messages from config
	public void printMessage(Player p, String messageKey, String reason) {
		if (ir.getConfig().contains(messageKey)){
			List<String> message = new ArrayList<String>();
			message.add(ir.getConfig().getString(messageKey));
			
			if (reason != null) {
				message.set(0, message.get(0).replaceAll("%reason", "" + reason));
			}
			
			if (p != null) {				
				//Message format
				p.sendMessage(getString("chatMessages.prefix").replaceAll("&", "§") + message.get(0).replaceAll("&", "§"));
			}		
		} else {
			ir.getLogger().severe("Could not locate '"+messageKey+"' in the config.yml inside of the ItemRestrict folder!");
			p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Could not locate '"+messageKey+"' in the config.yml inside of the ItemRestrict folder!");
		}
			
	}
	
	//Get worlds
	private void getWorldsTask() {
		//Get worlds to enable restrictions from config
		final List<String> enabledWorlds = ir.getConfig().getStringList("General.Worlds");
		ItemRestrict.log.info("Scanning for loaded worlds in 10 seconds...");
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(ir, new Runnable() {

			@Override
			public void run() {
				//validate that list
				ir.enforcementWorlds = new ArrayList<World>();
				ItemRestrict.log.info("Scanning for loaded worlds...");
				for(int i = 0; i < enabledWorlds.size(); i++)
				{
					String worldName = enabledWorlds.get(i);
					World world = ir.getServer().getWorld(worldName);
					if(world == null)
					{
						ItemRestrict.log.warning("Error: There's no world named " + worldName + ".  Please update your config.yml.");
					}
					else
					{
						ir.enforcementWorlds.add(world);
					}
				}
				if(enabledWorlds.size() == 0)
				{			
					ItemRestrict.log.warning("No worlds found listed in config! Restrictions will not take place!");
				}
				//List the world names found.
				ArrayList<String> worldNames = new ArrayList<String>();
				for (World x : ir.enforcementWorlds) {
					worldNames.add(x.getName());
				}
				ItemRestrict.log.info("Plugin enabled on worlds: " + worldNames.toString());
			}
			
		}, 200L);
	}

}
