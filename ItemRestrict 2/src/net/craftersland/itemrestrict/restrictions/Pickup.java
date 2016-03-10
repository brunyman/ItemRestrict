package net.craftersland.itemrestrict.restrictions;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class Pickup implements Listener {
	
	private ItemRestrict ir;
	
	public Pickup(ItemRestrict ir) {
		this.ir = ir;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onItemPickup(PlayerPickupItemEvent event) {
		if (ir.getConfigHandler().getBoolean("General.Restrictions.PickupBans") == true) {
			Player p = event.getPlayer();
			ItemStack item = event.getItem().getItemStack();
			
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getData().getData(), p.getLocation());
			
			if (bannedInfo == null) {
				MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Pickup, p, item.getTypeId(), item.getData().getData(), p.getLocation());
				
				if (bannedInfo2 != null) {
					event.setCancelled(true);
					
					Location loc = event.getItem().getLocation();
					event.getItem().teleport(new Location(loc.getWorld(), loc.getX() + getRandomInt(), loc.getY() + getRandomInt(), loc.getZ() + getRandomInt()));
					
					if (ir.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
						if (ir.is19Server == true) {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
						} else {
							p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
						}
					}
					ir.getConfigHandler().printMessage(p, "chatMessages.pickupRestricted", bannedInfo2.reason);
				}
			}
		}
	}
	
	private int getRandomInt() {
		Random randomGenerator = new Random();
		int randSlot = randomGenerator.nextInt(5);
		return randSlot;
	}

}
