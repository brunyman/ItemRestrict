package net.craftersland.itemrestrict.utils;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class WearingScanner {
	
	private ItemRestrict ir;
	
	public WearingScanner(ItemRestrict ir) {
		this.ir = ir;
	}
	
	    //WEARING RESTRICTIONS TASK
		public void wearingScanTask() {
			
			BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(ir, new Runnable() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					for (final Player p : Bukkit.getOnlinePlayers()) {
						final ItemStack boots = p.getInventory().getBoots();
						final ItemStack leggings = p.getInventory().getLeggings();
						final ItemStack chestplate = p.getInventory().getChestplate();
						final ItemStack helmet = p.getInventory().getHelmet();
						
						if (boots != null) {
							MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Wearing, p, boots.getTypeId(), boots.getData().getData(), p.getLocation());
							if (bannedInfo != null) {
								ir.getConfigHandler().printMessage(p, "chatMessages.wearingRestricted", bannedInfo.reason);
								Bukkit.getScheduler().runTask(ir, new Runnable() {
									@Override
									public void run() {
										
										p.getInventory().addItem(boots);
										p.getInventory().setBoots(null);
										ir.getSoundHandler().sendPlingSound(p);
									}
								});
							}
						}
						if (leggings != null) {
							MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Wearing, p, leggings.getTypeId(), leggings.getData().getData(), p.getLocation());
							if (bannedInfo != null) {
								ir.getConfigHandler().printMessage(p, "chatMessages.wearingRestricted", bannedInfo.reason);
								Bukkit.getScheduler().runTask(ir, new Runnable() {
									@Override
									public void run() {
										
										p.getInventory().addItem(leggings);
										p.getInventory().setLeggings(null);
										ir.getSoundHandler().sendPlingSound(p);
									}
								});
							}
						}
						if (chestplate != null) {
							MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Wearing, p, chestplate.getTypeId(), chestplate.getData().getData(), p.getLocation());
							if (bannedInfo != null) {
								ir.getConfigHandler().printMessage(p, "chatMessages.wearingRestricted", bannedInfo.reason);
								Bukkit.getScheduler().runTask(ir, new Runnable() {
									@Override
									public void run() {
										
										p.getInventory().addItem(chestplate);
										p.getInventory().setChestplate(null);
										ir.getSoundHandler().sendPlingSound(p);
									}
								});
							}
						}
						if (helmet != null) {
							MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Wearing, p, helmet.getTypeId(), helmet.getData().getData(), p.getLocation());
							if (bannedInfo != null) {
								ir.getConfigHandler().printMessage(p, "chatMessages.wearingRestricted", bannedInfo.reason);
								Bukkit.getScheduler().runTask(ir, new Runnable() {
									@Override
									public void run() {
										
										p.getInventory().addItem(helmet);
										p.getInventory().setHelmet(null);
										ir.getSoundHandler().sendPlingSound(p);
									}
								});
							}
						}
					}
				}
			}, 20L, 20L);
			ir.wearingScanner.clear();
			ir.wearingScanner.put(true, task.getTaskId());
		}

}
