package net.craftersland.itemrestrict.restrictions;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class Placement implements Listener {
	
	private ItemRestrict ir;
	
	public Placement(ItemRestrict ir) {
		this.ir = ir;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onInteract(BlockPlaceEvent event) {
		if (ir.placementBanned.size() != 0) {	
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Placement, event.getPlayer(), event.getItemInHand().getTypeId(), event.getItemInHand().getData().getData(), event.getPlayer().getLocation());
			
			if (bannedInfo != null) {
				event.setCancelled(true);
				//p.getWorld().dropItem(p.getLocation(), item);
				//p.setItemInHand(null);
				
				if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					if (ir.is19Server == true) {
						event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
					} else {
						event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
					}
				}
				ir.getConfigHandler().printMessage(event.getPlayer(), "chatMessages.placementRestricted", bannedInfo.reason);
			} else if (ir.is19Server == true) {
				MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Placement, event.getPlayer(), event.getPlayer().getInventory().getItemInOffHand().getTypeId(), event.getPlayer().getInventory().getItemInOffHand().getData().getData(), event.getPlayer().getLocation());
				if (bannedInfo2 != null) {
					event.setCancelled(true);
					if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
						event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
					}
					ir.getConfigHandler().printMessage(event.getPlayer(), "chatMessages.placementRestricted", bannedInfo2.reason);
				}
			}
		}
	}

}
