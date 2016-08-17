package net.craftersland.itemrestrict.restrictions;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;

public class Brewing implements Listener {
	
	private ItemRestrict ir;
	
	public Brewing(ItemRestrict ir) {
		this.ir = ir;
	}
	
	//-------------------------------------------------------------------
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBrewingPotions(BrewEvent event) {
		//Check if brewing bans are enabled or not
		if (ir.getConfigHandler().getBoolean("General.Restrictions.EnableBrewingBans") == true) {
			if (ir.is19Server == false) {
				oldFunction(event);
			} else {
				//TODO 1.9 potions
			}
		}
	}
	
	/*private void newFunction(final BrewEvent event) {
		//Get potions before brewing
		PotionMeta potionSlot0 = null;
		PotionMeta potionSlot1 = null;
		PotionMeta potionSlot2 = null;
		
		ItemStack ing = event.getContents().getIngredient();
		
		if (event.getContents().getItem(0) != null) {
			if (event.getContents().getItem(0).getType() == Material.POTION || event.getContents().getItem(0).getType() == Material.SPLASH_POTION || event.getContents().getItem(0).getType() == Material.LINGERING_POTION) {
				potionSlot0 = (PotionMeta) event.getContents().getItem(0);
			}
		}
		if (event.getContents().getItem(1) != null) {
			if (event.getContents().getItem(1).getType() == Material.POTION || event.getContents().getItem(1).getType() == Material.SPLASH_POTION || event.getContents().getItem(1).getType() == Material.LINGERING_POTION) {
				potionSlot1 = (PotionMeta) event.getContents().getItem(1);
			}
		}
		if (event.getContents().getItem(2) != null) {
			if (event.getContents().getItem(2).getType() == Material.POTION || event.getContents().getItem(2).getType() == Material.SPLASH_POTION || event.getContents().getItem(2).getType() == Material.LINGERING_POTION) {
				potionSlot2 = (PotionMeta) event.getContents().getItem(2);
			}
		}
		
		final PotionMeta Slot0 = potionSlot0;
		final PotionMeta Slot1 = potionSlot1;
		final PotionMeta Slot2 = potionSlot2;
		final ItemStack ingredient = new ItemStack(ing.getType(), 1, ing.getDurability());
		
		//Check the brewed potions for banned potions. Delayed task to check the potions after brewing.
		Bukkit.getScheduler().scheduleSyncDelayedTask(ir, new Runnable() {
			
			@SuppressWarnings("deprecation")
			public void run() {
				Player player = null;
				boolean restricted = false;
				boolean ingConsumed = false;
				String reason = "";
				//Check if there is a viewer on brewing stand
				if (event.getContents().getViewers().isEmpty() == false) {
					player = (Player)event.getContents().getViewers().get(0);
				}
				//Get all 3 brewing stand potion slots content
				ItemStack item0 = event.getContents().getItem(0);
				ItemStack item1 = event.getContents().getItem(1);
				ItemStack item2 = event.getContents().getItem(2);
				//Check slot 0 for banned items
				if (item0 != null) {
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item0.getTypeId(), item0.getData().getData(), event.getBlock().getLocation());
					
					if (bannedInfo != null) {
						
						event.getContents().setItem(0, new ItemStack(Slot0));
						
						restricted = true;
						reason = bannedInfo.reason;
					} else {
						ingConsumed = true;
					}
				}
				//Check slot 1 for banned items
				if (item1 != null) {
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item1.getTypeId(), item1.getData().getData(), event.getBlock().getLocation());
					
					if(bannedInfo != null) {
						
						event.getContents().setItem(1, new ItemStack(Slot1));
						
						restricted = true;
						reason = bannedInfo.reason;
					} else {
						ingConsumed = true;
					}
				}
				//Check slot 2 for banned items
				if (item2 != null) {
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item2.getTypeId(), item2.getData().getData(), event.getBlock().getLocation());
					
					if (bannedInfo != null) {
						
						event.getContents().setItem(2, new ItemStack(Slot2));
						
						restricted = true;
						reason = bannedInfo.reason;
					} else {
						ingConsumed = true;
					}
				}
				//If there is a viewer on the brewing stand send the restricted message
				if (player != null && restricted == true) {
					if (ingConsumed == false) {
						player.getInventory().addItem(new ItemStack(ingredient.getType(), 1));
					}
					ir.getConfigHandler().printMessage(player, "chatMessages.brewingRestricted", reason);
					if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
						if (ir.is19Server == true) {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
						} else {
							player.playSound(player.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
						}
					}
				}
				
			}
			
		}, 1L);
	}*/
	
	private void oldFunction(final BrewEvent event) {
		//Get potions before brewing
		ItemStack potionSlot0 = null;
		ItemStack potionSlot1 = null;
		ItemStack potionSlot2 = null;
		ItemStack ing = event.getContents().getIngredient();
		
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
		final ItemStack ingredient = new ItemStack(ing.getType(), 1, ing.getDurability());
		
		//Check the brewed potions for banned potions. Delayed task to check the potions after brewing.
		Bukkit.getScheduler().scheduleSyncDelayedTask(ir, new Runnable() {
			
			@SuppressWarnings("deprecation")
			public void run() {
				Player player = null;
				boolean restricted = false;
				boolean ingConsumed = false;
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
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item0.getTypeId(), item0.getData().getData(), event.getBlock().getLocation());
					
					if (bannedInfo != null) {
						
						event.getContents().setItem(0, new ItemStack(Slot0));
						
						restricted = true;
						reason = bannedInfo.reason;
					} else {
						ingConsumed = true;
					}
				}
				//Check slot 1 for banned items
				if (item1 != null) {
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item1.getTypeId(), item1.getData().getData(), event.getBlock().getLocation());
					
					if(bannedInfo != null) {
						
						event.getContents().setItem(1, new ItemStack(Slot1));
						
						restricted = true;
						reason = bannedInfo.reason;
					} else {
						ingConsumed = true;
					}
				}
				//Check slot 2 for banned items
				if (item2 != null) {
					MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Brewing, player, item2.getTypeId(), item2.getData().getData(), event.getBlock().getLocation());
					
					if (bannedInfo != null) {
						
						event.getContents().setItem(2, new ItemStack(Slot2));
						
						restricted = true;
						reason = bannedInfo.reason;
					} else {
						ingConsumed = true;
					}
				}
				//If there is a viewer on the brewing stand send the restricted message
				if (player != null && restricted == true) {
					if (ingConsumed == false) {
						player.getInventory().addItem(new ItemStack(ingredient.getType(), 1));
					}
					ir.getConfigHandler().printMessage(player, "chatMessages.brewingRestricted", reason);
					if (ir.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
						if (ir.is19Server == true) {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
						} else {
							player.playSound(player.getLocation(), Sound.valueOf("NOTE_PLING"), 1, 1);
						}
					}
				}
				
			}
			
		}, 1L);
	}
}
