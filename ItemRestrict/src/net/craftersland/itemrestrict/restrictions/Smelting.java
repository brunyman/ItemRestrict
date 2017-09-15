package net.craftersland.itemrestrict.restrictions;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class Smelting implements Listener {
	
	private ItemRestrict ir;
	
	public Smelting(ItemRestrict ir) {
		this.ir = ir;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onItemCrafted(FurnaceSmeltEvent event) {
		ItemStack item = event.getSource();
		Furnace f = (Furnace) event.getBlock().getState();
		if (f.getInventory().getViewers().isEmpty() == false) {
			Player p = (Player) f.getInventory().getViewers().get(0);
			
			MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Smelting, p, item.getTypeId(), item.getDurability(), p.getLocation());
			
			if (bannedInfo != null) {
				event.setCancelled(true);
				
				ir.getSoundHandler().sendPlingSound(p);
				ir.getConfigHandler().printMessage(p, "chatMessages.smeltingRestricted", bannedInfo.reason);
			}
		} else {
            MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Smelting, null, item.getTypeId(), item.getDurability(), null);
			
			if (bannedInfo != null) {
				event.setCancelled(true);
			}
		}
	}

}
