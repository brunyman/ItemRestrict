package net.craftersland.itemrestrict.restrictions;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class Ownership implements Listener {
	
	private ItemRestrict ir;
	
	public Ownership(ItemRestrict ir) {
		this.ir = ir;
	}
	
	//Inventory Click
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemClicked(InventoryClickEvent event) {
		if (event.getSlotType() != null) {
			if (event.getCurrentItem() != null) {
				Player p = (Player) event.getWhoClicked();
				ItemStack currentItem = event.getCurrentItem();
				ItemStack cursorItem = event.getCursor();
				
				if (ir.getConfigHandler().getBoolean("General.Settings.OwnershipPlayerInvOnly") == true) {
					if (event.getInventory().getType() == InventoryType.PLAYER) {
						inventoryClickRestriction(event, currentItem, p, false);
					} else if (event.getRawSlot() >= event.getInventory().getSize()) {
						inventoryClickRestriction(event, cursorItem, p, true);
					}
				} else {
					inventoryClickRestriction(event, currentItem, p, false);
				}
				
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemDrag(final InventoryDragEvent event) {
		if (ir.getConfigHandler().getBoolean("General.Settings.OwnershipPlayerInvOnly") == true) {
			Player p = (Player) event.getWhoClicked();
			ItemStack cursorItem = event.getOldCursor();
			
			if (cursorItem != null) {
				MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, cursorItem.getTypeId(), cursorItem.getData().getData(), p.getLocation());
				
				if (bannedInfo != null) {
					event.setCancelled(true);
					
					if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
						if (ir.is19Server == true) {
							p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
						} else {
							p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
						}
					}
					ir.getConfigHandler().printMessage(p, "chatMessages.restricted", bannedInfo.reason);
				}
			}
		}
	}
	
	private void inventoryClickRestriction(InventoryClickEvent event, ItemStack currentItem, Player p, Boolean removeCursorItem) {
		@SuppressWarnings("deprecation")
		MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, currentItem.getTypeId(), currentItem.getData().getData(), p.getLocation());
		
		if (bannedInfo != null) {
			event.setCancelled(true);
			
			if (event.getSlotType() == SlotType.ARMOR) {
				if (p.getInventory().getHelmet() != null) {
					if (p.getInventory().getHelmet().isSimilar(currentItem)) {
						p.getInventory().setHelmet(null);
					}
				}
				if (p.getInventory().getChestplate() != null) {
					if (p.getInventory().getChestplate().isSimilar(currentItem)) {
						p.getInventory().setChestplate(null);
					}
				}
				if (p.getInventory().getLeggings() != null) {
					if (p.getInventory().getLeggings().isSimilar(currentItem)) {
						p.getInventory().setLeggings(null);
					}
				}
				if (p.getInventory().getBoots() != null) {
					if (p.getInventory().getBoots().isSimilar(currentItem)) {
						p.getInventory().setBoots(null);
					}
				}
			} else if (removeCursorItem == true) {
				p.setItemOnCursor(null);
			} else {
				p.getInventory().remove(currentItem);
				event.getInventory().remove(currentItem);
			}
			
			if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
				if (ir.is19Server == true) {
					p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
				} else {
					p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
				}
			}
			ir.getConfigHandler().printMessage(p, "chatMessages.restrictedConfiscated", bannedInfo.reason);
		}
	}
	
	//Item Pickup
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemPickup(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		ItemStack item = event.getItem().getItemStack();
		
		MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
		
		if (bannedInfo != null) {
			event.setCancelled(true);
			
			event.getItem().remove();
			
			if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
				if (ir.is19Server == true) {
					p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
				} else {
					p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
				}
			}
			p.playEffect(event.getItem().getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
			ir.getConfigHandler().printMessage(p, "chatMessages.pickupRestricted", bannedInfo.reason);
		}
	}
	
	//Switch hotbar slot
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemHeldSwitch(PlayerItemHeldEvent event) {
		int newSlot = event.getNewSlot();
		Player p = event.getPlayer();
		ItemStack item = p.getInventory().getItem(newSlot);
		
		if (item != null) {
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
			
			if (bannedInfo != null) {
				p.getInventory().setItem(newSlot, null);
				
				if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
					if (ir.is19Server == true) {
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
					} else {
						p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
					}
				}
				ir.getConfigHandler().printMessage(p, "chatMessages.restrictedConfiscated", bannedInfo.reason);
			}
		}
	}
	
	//Creative event
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onCreativeEvents(InventoryCreativeEvent event) {
		ItemStack cursorItem = event.getCursor();
		
		if (cursorItem != null) {
			Player p = (Player) event.getWhoClicked();
			
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, cursorItem.getTypeId(), cursorItem.getData().getData(), p.getLocation());
			
			if (bannedInfo != null) {
				event.setCancelled(true);
				event.setCursor(null);
				
				if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
					if (ir.is19Server == true) {
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
					} else {
						p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
					}
				}
				ir.getConfigHandler().printMessage(p, "chatMessages.restrictedConfiscated", bannedInfo.reason);
			}
		}
	}
	
	//Interact event
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack item = p.getItemInHand();
		
		if (item != null) {
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
			
			if (bannedInfo != null) {
				event.setCancelled(true);
				p.setItemInHand(null);
				
				if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
					if (ir.is19Server == true) {
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
					} else {
						p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
					}
				}
				ir.getConfigHandler().printMessage(p, "chatMessages.restrictedConfiscated", bannedInfo.reason);
			}
		}
	}
	
	//Item drop
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		ItemStack item = event.getItemDrop().getItemStack();
		
		MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
		
		if (bannedInfo != null) {
			event.getItemDrop().remove();
			p.setItemInHand(null);
			
			if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
				if (ir.is19Server == true) {
					p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
				} else {
					p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
				}
			}
			ir.getConfigHandler().printMessage(p, "chatMessages.restrictedConfiscated", bannedInfo.reason);
		}
	}

}
