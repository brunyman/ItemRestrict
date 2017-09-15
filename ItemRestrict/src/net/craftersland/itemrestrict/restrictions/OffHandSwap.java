package net.craftersland.itemrestrict.restrictions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class OffHandSwap implements Listener {
	
	private ItemRestrict ir;
	
	public OffHandSwap(ItemRestrict ir) {
		this.ir = ir;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onOffHandSwap(PlayerSwapHandItemsEvent event) {
		MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, event.getPlayer(), event.getOffHandItem().getTypeId(), event.getOffHandItem().getDurability(), event.getPlayer().getLocation());
		if (bannedInfo == null) {
			MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, event.getPlayer(), event.getOffHandItem().getTypeId(), event.getOffHandItem().getDurability(), event.getPlayer().getLocation());
			MaterialData bannedInfo3 = ir.getRestrictedItemsHandler().isBanned(ActionType.Placement, event.getPlayer(), event.getOffHandItem().getTypeId(), event.getOffHandItem().getDurability(), event.getPlayer().getLocation());
			if (bannedInfo2 != null) {
				event.setCancelled(true);
				//event.setOffHandItem(null);
				ir.getSoundHandler().sendPlingSound(event.getPlayer());
				ir.getConfigHandler().printMessage(event.getPlayer(), "chatMessages.ussageRestricted", bannedInfo2.reason);
			} else if (bannedInfo3 != null) {
				event.setCancelled(true);
				//event.setOffHandItem(null);
			}
		} else {
			event.setCancelled(true);
			event.setOffHandItem(null);
		}
	}

}
