package net.craftersland.itemrestrict.restrictions;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class Crafting implements Listener {
	
	private ItemRestrict ir;
	
	public Crafting(ItemRestrict ir) {
		this.ir = ir;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onItemCrafted(PrepareItemCraftEvent event) {
		if (event.getRecipe() != null) {
			ItemStack item = event.getRecipe().getResult();
			if (event.getViewers().isEmpty() == false) {
				Player p = (Player) event.getViewers().get(0);
				
				MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Crafting, p, item.getTypeId(), item.getData().getData(), p.getLocation());
				
				if (bannedInfo != null) {
					event.getInventory().setResult(null);
					
					if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
						if (ir.is19Server == true) {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
						} else {
							p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
						}
					}
					ir.getConfigHandler().printMessage(p, "chatMessages.craftingRestricted", bannedInfo.reason);
				}
			} else {
	            MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Crafting, null, item.getTypeId(), item.getData().getData(), null);
				
				if (bannedInfo != null) {
					event.getInventory().setResult(null);
				}
			}
		}
	}

}
