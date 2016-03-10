package net.craftersland.itemrestrict.utils;

import java.util.ArrayList;

import net.craftersland.itemrestrict.ItemRestrict;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

public class WorldScanner {
	
	private int nextChunkPercentile = 0;
	private ItemRestrict ir;
	
	public WorldScanner(ItemRestrict ir) {
		this.ir = ir;
	}
	
	public void worldScanTask() {
		//start the repeating scan for banned items in loaded chunks
		//runs every minute and scans 5% of loaded chunks.
		int delay = ir.getConfigHandler().getInteger("General.WorldScannerDelay") * 60;
		
		BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(ir, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				ItemRestrict.log.info("WorldScanner Task Started...");
				ArrayList<World> worlds;
				if(ir.worldBanned.size() > 0) {
					if (ir.enforcementWorlds.size() == 0) {
						worlds = (ArrayList<World>) Bukkit.getServer().getWorlds();
					} else {
						worlds = ir.enforcementWorlds;
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
    										MaterialData bannedInfo = ir.worldBanned.Contains(materialInfo);
    										if(bannedInfo != null) {
    											Bukkit.getScheduler().runTask(ir, new Runnable() {
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
							ItemRestrict.log.warning("World Scanner Error: " + e.getMessage());
						}
					}
					
					nextChunkPercentile += 5;
					if(nextChunkPercentile >= 100) nextChunkPercentile = 0;
					ItemRestrict.log.info("WorldScanner Task Ended.");
				}
			}
		}, 20L * delay, 20L * delay);
		ir.worldScanner.clear();
		ir.worldScanner.put(true, task.getTaskId());
	}
	
	private static String getFriendlyLocationString(Location location) 
	{
		return location.getWorld().getName() + "(" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")";
	}

}
