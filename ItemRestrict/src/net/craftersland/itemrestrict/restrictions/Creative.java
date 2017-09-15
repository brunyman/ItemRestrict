package net.craftersland.itemrestrict.restrictions;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class Creative implements Listener {
	
	private ItemRestrict ir;
	
	public Creative(ItemRestrict ir) {
		this.ir = ir;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onCreativeEvents(InventoryCreativeEvent event) {
		if (ir.getConfigHandler().getBoolean("General.Restrictions.CreativeBans") == true) {
			ItemStack cursorItem = event.getCursor();
			
			if (cursorItem != null) {
				Player p = (Player) event.getWhoClicked();
				MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, cursorItem.getTypeId(), cursorItem.getDurability(), p.getLocation());
				
				if (bannedInfo == null) {
					MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Creative, p, cursorItem.getTypeId(), cursorItem.getDurability(), p.getLocation());
					
					if (bannedInfo2 != null) {
						event.setCancelled(true);
						event.setCursor(null);
						
						ir.getSoundHandler().sendItemBreakSound(p);
						ir.getConfigHandler().printMessage(p, "chatMessages.creativeRestricted", bannedInfo2.reason);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemClicked(InventoryClickEvent event) {
		if (ir.getConfigHandler().getBoolean("General.Restrictions.CreativeBans") == true) {
			if (event.getSlotType() != null) {
				if (event.getCurrentItem() != null) {
					Player p = (Player) event.getWhoClicked();
					if (p.getGameMode() == GameMode.CREATIVE) {
						ItemStack currentItem = event.getCurrentItem();
						
						MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, currentItem.getTypeId(), currentItem.getDurability(), p.getLocation());
						
						if (bannedInfo == null) {
							MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Creative, p, currentItem.getTypeId(), currentItem.getDurability(), p.getLocation());
							
							if (bannedInfo2 != null) {
								event.setCancelled(true);
								
								ir.getSoundHandler().sendItemBreakSound(p);
								ir.getConfigHandler().printMessage(p, "chatMessages.creativeRestricted", bannedInfo2.reason);
							}
						}
					}
				}				
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if (p.getGameMode() == GameMode.CREATIVE) {
			ItemStack item = p.getItemInHand();
			
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getDurability(), p.getLocation());
			
			if (bannedInfo == null) {
				MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Creative, p, item.getTypeId(), item.getDurability(), p.getLocation());
				
				if (bannedInfo2 != null) {
					event.setCancelled(true);
					
					ir.getSoundHandler().sendItemBreakSound(p);
					ir.getConfigHandler().printMessage(p, "chatMessages.creativeRestricted", bannedInfo2.reason);
				}
			}
		}
	}

}
