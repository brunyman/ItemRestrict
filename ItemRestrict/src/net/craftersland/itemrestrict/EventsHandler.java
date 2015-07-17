package net.craftersland.itemrestrict;

import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.itemsprocessor.MaterialData;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;

public class EventsHandler implements Listener {
	
private ItemRestrict itemrestrict;
	
	public EventsHandler(ItemRestrict itemrestrict) {
		this.itemrestrict = itemrestrict;
	}
	
	//-------------------------------------------------------------------
	//Optional Brewing Bans
	//-------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	void onBrewingPotions(final BrewEvent event) {
		//Check if brewing bans are enabled or not
		if (itemrestrict.getConfigHandler().getString("General.Restrictions.EnableBrewingBans") == "false") return;
		
		//Get potions before brewing
		ItemStack potionSlot0 = null;
		ItemStack potionSlot1 = null;
		ItemStack potionSlot2 = null;
		
		if (event.getContents().getItem(0) != null) {
			potionSlot0 = new ItemStack(event.getContents().getItem(0).getType(), 1, (short) event.getContents().getItem(0).getDurability());
		}
		if (event.getContents().getItem(1) != null) {
			potionSlot1 = new ItemStack(event.getContents().getItem(1).getType(), 1, (short) event.getContents().getItem(1).getDurability());
		}
		if (event.getContents().getItem(2) != null) {
			potionSlot2 = new ItemStack(event.getContents().getItem(2).getType(), 1, (short) event.getContents().getItem(2).getDurability());
		}
		
		final ItemStack Slot0 = potionSlot0;
		final ItemStack Slot1 = potionSlot1;
		final ItemStack Slot2 = potionSlot2;
		final ItemStack ingredient = new ItemStack(event.getContents().getIngredient());
		
		//Check the brewed potions for banned potions. Delayed task to check the potions after brewing.
		Bukkit.getScheduler().scheduleSyncDelayedTask(itemrestrict, new Runnable() {
			
			public void run() {
				Player player = null;
				boolean restricted = false;
				String reason = "";
				//Check if there is a viewer on brewing stand
				if (event.getContents().getViewers().isEmpty() == false) {
					player = (Player)event.getContents().getViewers().get(0);
				}
				//Get all 3 brewing stand potion lots content
				ItemStack item0 = event.getContents().getItem(0);
				ItemStack item1 = event.getContents().getItem(1);
				ItemStack item2 = event.getContents().getItem(2);
				//Check slot 0 for banned items
				if (item0 != null) {
					MaterialData bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item0.getTypeId(), item0.getData().getData(), event.getBlock().getLocation());
					
					if(bannedInfo != null) {
						
						event.getContents().setItem(0, new ItemStack(Slot0));
						
						restricted = true;
						reason = bannedInfo.reason;
					}
				}
				//Check slot 1 for banned items
				if (item1 != null) {
					MaterialData bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item1.getTypeId(), item1.getData().getData(), event.getBlock().getLocation());
					
					if(bannedInfo != null) {
						
						event.getContents().setItem(1, new ItemStack(Slot1));
						
						restricted = true;
						reason = bannedInfo.reason;
					}
				}
				//Check slot 2 for banned items
				if (item2 != null) {
					MaterialData bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item2.getTypeId(), item2.getData().getData(), event.getBlock().getLocation());
					
					if(bannedInfo != null) {
						
						event.getContents().setItem(2, new ItemStack(Slot2));
						
						restricted = true;
						reason = bannedInfo.reason;
					}
				}
				//If there is a viewer on the brewing stand send the restricted message
				if (player != null && restricted == true) {
					player.getInventory().addItem(new ItemStack(ingredient.getType(), 1));
					itemrestrict.getConfigHandler().printMessage(player, "chatMessages.brewingRestricted", reason);
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
				}
				
			}
			
		}, 1L);
		
	}

}
