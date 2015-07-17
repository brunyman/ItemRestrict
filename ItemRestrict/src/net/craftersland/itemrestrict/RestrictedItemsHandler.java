package net.craftersland.itemrestrict;

import java.io.File;
import java.util.List;

import net.craftersland.itemrestrict.itemsprocessor.MaterialCollection;
import net.craftersland.itemrestrict.itemsprocessor.MaterialData;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class RestrictedItemsHandler {
	
	private ItemRestrict itemrestrict;
	private File restrictedItemsFile;
	
	public RestrictedItemsHandler(ItemRestrict itemrestrict) {
		this.itemrestrict = itemrestrict;
		
		try {
			restrictedItemsFile = new File("plugins"+System.getProperty("file.separator")+"ItemRestrict"+System.getProperty("file.separator")+"RestrictedItems.yml");
			
			//Create RestrictedItems.yml and/or load it
			if (!restrictedItemsFile.exists()) {
				ItemRestrict.log.info("No RestrictedItems.yml file found! Creating new one...");
				
				itemrestrict.saveResource("RestrictedItems.yml", false);
			}
			
			FileConfiguration ymlFormat = YamlConfiguration.loadConfiguration(restrictedItemsFile);
			
			//
			//OWNERSHIP BANS - players can't have these at all (crafting is also blocked in this case)
			//
			List<String> OwnershipBanned = ymlFormat.getStringList("OwnershipBanned");
			//parse the strings from the config file
			parseMaterialListFromConfig(OwnershipBanned, itemrestrict.ownershipBanned, "OwnershipBanned");
			
			//
			//CRAFTING BANS 
			//
			List<String> CraftingBanned = ymlFormat.getStringList("CraftingBanned");
			//parse the strings from the config file
			parseMaterialListFromConfig(CraftingBanned, itemrestrict.craftingBanned, "CraftingBanned");
			
			//
			//BREWING BANS 
			//
			List<String> BrewingBanned = ymlFormat.getStringList("BrewingBanned");
			//parse the strings from the config file
			parseMaterialListFromConfig(BrewingBanned, itemrestrict.brewingBanned, "BrewingBanned");
			
			//
			//WEARING BANS 
			//
			List<String> WearingBanned = ymlFormat.getStringList("WearingBanned");
			//parse the strings from the config file
			parseMaterialListFromConfig(WearingBanned, itemrestrict.wearingBanned, "WearingBanned");
			
			//
			//USAGE BANS 
			//
			List<String> UsageBanned = ymlFormat.getStringList("UsageBanned");
			//parse the strings from the config file
			parseMaterialListFromConfig(UsageBanned, itemrestrict.usageBanned, "UsageBanned");
			
			//
			//PLACEMENT BANS 
			//
			List<String> PlacementBanned = ymlFormat.getStringList("PlacementBanned");
			//parse the strings from the config file
			parseMaterialListFromConfig(PlacementBanned, itemrestrict.placementBanned, "PlacementBanned");
			
			//
			//CREATIVE MENU BANS 
			//
			List<String> CreativeBanned = ymlFormat.getStringList("CreativeBanned");
			//parse the strings from the config file
			parseMaterialListFromConfig(CreativeBanned, itemrestrict.creativeBanned, "CreativeBanned");
			
			//
			//WORLD BANS 
			//
			List<String> WorldBanned = ymlFormat.getStringList("WorldBanned");
			//parse the strings from the config file
			parseMaterialListFromConfig(WorldBanned, itemrestrict.worldBanned, "WorldBanned");
			
		} catch (Exception e) {
			ItemRestrict.log.severe("Could not create RestrictedItems.yml file!");
		    }
		
		}
	
	private void parseMaterialListFromConfig(List<String> stringsToParse, MaterialCollection materialCollection, String configString)
	{
		materialCollection.clear();
		
		//for each string in the list
		for(int i = 0; i < stringsToParse.size(); i++)
		{
			//try to parse the string value into a material info
			MaterialData materialData = MaterialData.fromString(stringsToParse.get(i));
			
			//null value returned indicates an error parsing the string from the config file
			if(materialData == null)
			{
				//show error in log
				ItemRestrict.log.warning("ERROR: Unable to read material entry: " + stringsToParse.get(i) + " ,from RestrictedItems.yml file.");
			}
			
			//otherwise store the valid entry in config data
			else
			{
				materialCollection.Add(materialData);
			}
		}		
	}
	
	public MaterialData isBanned(ActionType actionType, Player player, int typeId, byte data, Location location) 
	{
		if (itemrestrict.getConfigHandler().getString("General.EnableOnAllWorlds") != "true") {
			if(!itemrestrict.enforcementWorlds.contains(location.getWorld())) return null;
		}
		if(player != null && player.hasPermission("ItemRestrict.admin") || player != null && player.hasPermission("ItemRestrict.bypass")) return null;
		MaterialCollection collectionToSearch;
		String permissionNode;
		if(actionType == ActionType.Usage)
		{
			collectionToSearch = itemrestrict.usageBanned;
			permissionNode = "use";
		}
		else if(actionType == ActionType.Placement)
		{
			collectionToSearch = itemrestrict.placementBanned;
			permissionNode = "place";
		}
		else if(actionType == ActionType.Crafting)
		{
			collectionToSearch = itemrestrict.craftingBanned;
			permissionNode = "craft";
		}
		else if(actionType == ActionType.Brewing)
		{
			collectionToSearch = itemrestrict.brewingBanned;
			permissionNode = "brew";
		}
		else if(actionType == ActionType.Wearing)
		{
			collectionToSearch = itemrestrict.wearingBanned;
			permissionNode = "wear";
		}
		else if(actionType == ActionType.Creative)
		{
			collectionToSearch = itemrestrict.creativeBanned;
			permissionNode = "creative";
		}
		else
		{
			collectionToSearch = itemrestrict.ownershipBanned;
			permissionNode = "own";
		}
		
		MaterialData bannedInfo = collectionToSearch.Contains(new MaterialData(typeId, data, null, null));
		if(bannedInfo != null)
		{
			if (player == null) return bannedInfo;
			if(player.hasPermission("ItemRestrict.bypass." + typeId + ".*.*")) return null;
			if(player.hasPermission("ItemRestrict.bypass." + typeId + ".*." + permissionNode)) return null;
			if(player.hasPermission("ItemRestrict.bypass." + typeId + "." + data + "." + permissionNode)) return null;			
			if(player.hasPermission("ItemRestrict.bypass." + typeId + "." + data + ".*")) return null;
			
			return bannedInfo;
		}
				
		return null;
	}
	
	public enum ActionType
	{
		Usage, Ownership, Placement, Crafting, Creative, Brewing, Wearing
	}

}
