package net.craftersland.itemrestrict.restrictions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class BlockBreak implements Listener {
	
	private ItemRestrict ir;
	
	public BlockBreak(ItemRestrict ir) {
		this.ir = ir;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	private void onBlockBreak(BlockBreakEvent event) {
		if (ir.blockBreakBanned.size() != 0) {
			boolean compareDrops = false;
			ItemStack itemToDrop = null;
			if (event.getBlock().getDrops().iterator().hasNext() == true) {
				itemToDrop = event.getBlock().getDrops().iterator().next();
				if (event.getBlock().getTypeId() == itemToDrop.getTypeId()) {
					compareDrops = true;
				}
			}
			MaterialData bannedInfo = null;
			if (compareDrops == false) {
				bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.BlockBreak, event.getPlayer(), event.getBlock().getTypeId(), event.getBlock().getState().getData().getData(), event.getPlayer().getLocation());
			} else if (itemToDrop != null) {
				bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.BlockBreak, event.getPlayer(), itemToDrop.getTypeId(), itemToDrop.getDurability(), event.getPlayer().getLocation());
			}
			
			if (bannedInfo != null) {
				event.setCancelled(true);
				//p.getWorld().dropItem(p.getLocation(), item);
				//p.setItemInHand(null);
				
				ir.getSoundHandler().sendPlingSound(event.getPlayer());
				ir.getConfigHandler().printMessage(event.getPlayer(), "chatMessages.blockBreakRestricted", bannedInfo.reason);
			}
		}
	}

}
