package net.craftersland.itemrestrict.restrictions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class Usage implements Listener {
	
	private ItemRestrict ir;
	private Set<String> safety = new HashSet<String>();
	
	public Usage(ItemRestrict ir) {
		this.ir = ir;
	}
	
	private boolean isEventSafe(final String pN) {
		if (safety.contains(pN) == true) {
			return false;
		}
		safety.add(pN);
		Bukkit.getScheduler().runTaskLaterAsynchronously(ir, new Runnable() {

			@Override
			public void run() {
				safety.remove(pN);
			}
			
		}, 1L);
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onInteract(PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		ItemStack item = null;
		ItemStack item2 = null;
		if (ir.is19Server == false) {
			item = p.getItemInHand();
		} else {
			item = p.getInventory().getItemInMainHand();
			item2 = p.getInventory().getItemInOffHand();
		}
		Block interactigBlock = event.getClickedBlock();

		if (item.getType().isBlock() == false) {
			if (ir.is19Server == false) {
				MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
				
				if (bannedInfo == null) {
					MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getTypeId(), item.getData().getData(), p.getLocation());
					
					if (bannedInfo2 != null) {
						if (isEventSafe(event.getPlayer().getName()) == false) return;
						event.setCancelled(true);
						Bukkit.getScheduler().runTask(ir, new Runnable() {

							@Override
							public void run() {
								ItemStack handItem = p.getItemInHand();
								p.getWorld().dropItem(p.getLocation(), handItem);
								p.setItemInHand(null);
								p.closeInventory();
								p.updateInventory();
							}
							
						});
						
						if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
							if (ir.is19Server == true) {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
							} else {
								p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
							}
						}
						ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo2.reason);
					} else if (interactigBlock != null) {
						if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							MaterialData bannedInfo3 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, interactigBlock.getTypeId(), interactigBlock.getData(), p.getLocation());
							
							if (bannedInfo3 != null) {
								if (isEventSafe(event.getPlayer().getName()) == false) return;
								event.setCancelled(true);
								
								if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
									if (ir.is19Server == true) {
										p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
									} else {
										p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
									}
								}
								ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo3.reason);
							}
						}
					}
				}
			} else {
				if (item.getType() != Material.AIR) {
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
					
					if (bannedInfo == null) {
						MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getTypeId(), item.getData().getData(), p.getLocation());
						
						if (bannedInfo2 != null) {
							if (isEventSafe(event.getPlayer().getName()) == false) return;
							event.setCancelled(true);
							
							Bukkit.getScheduler().runTask(ir, new Runnable() {

								@Override
								public void run() {
									ItemStack handItem = p.getInventory().getItemInMainHand();
									p.getWorld().dropItem(p.getLocation(), handItem);
									p.getInventory().setItemInMainHand(null);
									p.closeInventory();
									p.updateInventory();
								}
								
							});
							
							if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
								if (ir.is19Server == true) {
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
								} else {
									p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
								}
							}
							ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo2.reason);
						} else if (interactigBlock != null) {
							if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
								MaterialData bannedInfo3 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, interactigBlock.getTypeId(), interactigBlock.getData(), p.getLocation());
								
								if (bannedInfo3 != null) {
									if (isEventSafe(event.getPlayer().getName()) == false) return;
									event.setCancelled(true);
									
									if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
										if (ir.is19Server == true) {
											p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
										} else {
											p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
										}
									}
									ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo3.reason);
								}
							}
						}
					}
				} else {
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item2.getTypeId(), item2.getData().getData(), p.getLocation());
					
					if (bannedInfo == null) {
						MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item2.getTypeId(), item2.getData().getData(), p.getLocation());
						
						if (bannedInfo2 != null) {
							if (isEventSafe(event.getPlayer().getName()) == false) return;
							event.setCancelled(true);
							
							Bukkit.getScheduler().runTask(ir, new Runnable() {

								@Override
								public void run() {
									ItemStack handItem = p.getInventory().getItemInOffHand();
									p.getWorld().dropItem(p.getLocation(), handItem);
									p.getInventory().setItemInOffHand(null);
									p.closeInventory();
									p.updateInventory();
								}
								
							});
							
							if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
								if (ir.is19Server == true) {
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
								} else {
									p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
								}
							}
							ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo2.reason);
						} else if (interactigBlock != null) {
							if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
								MaterialData bannedInfo3 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, interactigBlock.getTypeId(), interactigBlock.getData(), p.getLocation());
								
								if (bannedInfo3 != null) {
									event.setCancelled(true);
									
									if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
										if (ir.is19Server == true) {
											p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
										} else {
											p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
										}
									}
									ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo3.reason);
								}
							}
						}
					}
				}
			}
		} else if (item.getType().isBlock() == true) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (interactigBlock != null) {
					MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, interactigBlock.getTypeId(), interactigBlock.getData(), p.getLocation());
					
					if (bannedInfo2 != null) {
						event.setCancelled(true);
						
						if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
							if (ir.is19Server == true) {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
							} else {
								p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
							}
						}
						ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo2.reason);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemHeldSwitch(PlayerItemHeldEvent event) {
		int newSlot = event.getNewSlot();
		Player p = event.getPlayer();
		ItemStack item = p.getInventory().getItem(newSlot);
		
		if (item != null) {
			if (item.getType().isBlock() == false) {
				MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
				
				if (bannedInfo == null) {
					MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getTypeId(), item.getData().getData(), p.getLocation());
					
					if (bannedInfo2 != null) {
						p.getWorld().dropItem(p.getLocation(), item);
						p.getInventory().setItem(newSlot, null);
						p.updateInventory();
						
						if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
							if (ir.is19Server == true) {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
							} else {
								p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
							}
						}
						ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo2.reason);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			ItemStack item = p.getItemInHand();
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getTypeId(), item.getData().getData(), p.getLocation());
			
			if (bannedInfo != null) {
				event.setCancelled(true);
			}
		} else if (ir.mcpcServer == false) {
			if (event.getDamager() instanceof Projectile) {
				Projectile pr = (Projectile) event.getDamager();
				if (pr.getShooter() instanceof Player) {
					Player p = (Player) pr.getShooter();
					ItemStack item = p.getItemInHand();
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getTypeId(), item.getData().getData(), p.getLocation());
					
					if (bannedInfo != null) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
