package net.craftersland.itemrestrict.restrictions;

import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

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
			Player p = event.getPlayer();
			ItemStack item = event.getItemInHand();
			
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Placement, p, item.getTypeId(), item.getData().getData(), p.getLocation());
			
			if (bannedInfo != null) {
				event.setCancelled(true);
				
				int randSlot = getRandomSlot();
				ItemStack randItem = p.getInventory().getItem(randSlot);
				
				p.setItemInHand(randItem);
				p.getInventory().setItem(randSlot, item);
				
				if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					if (ir.is19Server == true) {
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
					} else {
						p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
					}
				}
				ir.getConfigHandler().printMessage(p, "chatMessages.placementRestricted", bannedInfo.reason);
			}
		}
	}
	
	private int getRandomSlot() {
		Random randomGenerator = new Random();
		int randSlot = randomGenerator.nextInt(36);
		return randSlot;
	}

}
