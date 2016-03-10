package net.craftersland.itemrestrict.restrictions;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class Drop implements Listener {
	
	private ItemRestrict ir;
	
	public Drop(ItemRestrict ir) {
		this.ir = ir;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemDrop(PlayerDropItemEvent event) {
		if (ir.getConfigHandler().getBoolean("General.Restrictions.DropBans") == true) {
			Player p = event.getPlayer();
			ItemStack item = event.getItemDrop().getItemStack();
			
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
			
			if (bannedInfo == null) {
				MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Drop, p, item.getTypeId(), item.getData().getData(), p.getLocation());
				
				if (bannedInfo2 != null) {
					event.setCancelled(true);
					
					if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
						if (ir.is19Server == true) {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
						} else {
							p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
						}
					}
					ir.getConfigHandler().printMessage(p, "chatMessages.dropingRestricted", bannedInfo2.reason);
				}
			}
		}
	}

}
