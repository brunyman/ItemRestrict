package net.craftersland.itemrestrict;

import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.itemsprocessor.MaterialData;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerHandler implements Listener {
	
	private ItemRestrict itemrestrict;
	
	public PlayerHandler(ItemRestrict itemrestrict) {
		this.itemrestrict = itemrestrict;
	}

		//when something is clicked in an inventory
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.LOWEST)
		void onItemClicked(InventoryClickEvent event) {
			Player player = (Player)event.getWhoClicked();
			
			ItemStack item = event.getCursor();
			ItemStack item2 = event.getCurrentItem();
			Material material = event.getCursor().getType();
			if(item2 == null) return;
			
			//For creative menu only
			MaterialData bannedInfo3 = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Creative, player, item.getTypeId(), item.getData().getData(), player.getLocation());
			if(bannedInfo3 != null && (player != null) && (item != null) && (material != null) && (player.getGameMode().equals(GameMode.CREATIVE)) && material.getId() == bannedInfo3.typeID) {
				event.setCancelled(true);
				
				player.getInventory().remove(item);
				
				if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
				}
				
				itemrestrict.getConfigHandler().printMessage(player, "chatMessages.creativeRestricted", bannedInfo3.reason);
				return;
			}
			
			//For creative menu ownership
			MaterialData bannedInfo2 = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Ownership, player, item.getTypeId(), item.getData().getData(), player.getLocation());
			if(bannedInfo2 != null && (player != null) && (item != null) && (material != null) && (player.getGameMode().equals(GameMode.CREATIVE)) && material.getId() == bannedInfo2.typeID) {
				event.setCancelled(true);
				
				player.getInventory().remove(item);
				if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
				}
				player.updateInventory();
				itemrestrict.getConfigHandler().printMessage(player, "chatMessages.restrictedConfiscated", bannedInfo2.reason);
				return;
			}
			//For all other menu's
			MaterialData bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Ownership, player, item2.getTypeId(), item2.getData().getData(), player.getLocation());
			if(bannedInfo != null) {
				event.setCancelled(true);
				
				//CHANGED SO ALL RESTRICTED ITEMS WILL GET CONFISCATED
				if (event.getInventory().getType() != InventoryType.CRAFTING) {
					event.getInventory().remove(item2);
					if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
						player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
					}
					itemrestrict.getConfigHandler().printMessage(player, "chatMessages.restrictedConfiscated", bannedInfo.reason);
				} else {
					if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
						player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
					}
					player.getInventory().remove(item2);
					player.updateInventory();
					itemrestrict.getConfigHandler().printMessage(player, "chatMessages.restrictedConfiscated", bannedInfo.reason);
				}
				
			}
		}
		
		//when a player picks up an item
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		void onPlayerPickupItem(PlayerPickupItemEvent event) {
			Player player = event.getPlayer();
			ItemStack item = event.getItem().getItemStack();

			MaterialData bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Ownership, player, item.getTypeId(), item.getData().getData(), player.getLocation());
			if(bannedInfo != null) {
				itemrestrict.getConfigHandler().printMessage(player, "chatMessages.pickupRestricted", bannedInfo.reason);
				event.getItem().remove();
				if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
				}
				player.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
				player.updateInventory();
				event.setCancelled(true);						
			} 
		}
		
		//when something is crafted (may not be a player crafting)
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		void onItemCrafted(CraftItemEvent event)
		{
			Player player = (Player)event.getWhoClicked();
			ItemStack item = event.getRecipe().getResult();
			
			MaterialData bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Crafting, player, item.getTypeId(), item.getData().getData(), player.getLocation());
			if(bannedInfo != null)
			{
				event.setCancelled(true);
				if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
				}
				
				itemrestrict.getConfigHandler().printMessage(player, "chatMessages.craftingRestricted", bannedInfo.reason);
			}
		}
		
		//when a player interacts with the world
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.LOWEST)
		void onPlayerInteract(PlayerInteractEvent event) {
			Player player = event.getPlayer();
			
			//ignore pressure plates for this
			if(event.getAction() == Action.PHYSICAL){
				return;
			}

			MaterialData bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Ownership, player, player.getItemInHand().getTypeId(), player.getItemInHand().getData().getData(), player.getLocation());
			if(bannedInfo != null) {
				event.setCancelled(true);
				player.getInventory().setItemInHand(new ItemStack(Material.AIR));
				if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
				}
				
				player.updateInventory();
				ItemRestrict.log.info("Confiscated " + bannedInfo.toString() + " from " + player.getName() + ".");
				itemrestrict.getConfigHandler().printMessage(player, "chatMessages.restrictedConfiscated", bannedInfo.reason);
				return;
			}
			
			bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Usage, player, player.getItemInHand().getTypeId(), player.getItemInHand().getData().getData(), player.getLocation());
			if(bannedInfo != null) {
				event.setCancelled(true);
				if (player.getOpenInventory() != null) {
					player.getOpenInventory().close();
				}
				if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
				}
				player.updateInventory();
				itemrestrict.getConfigHandler().printMessage(player, "chatMessages.ussageRestricted", bannedInfo.reason);
			} else {
				//Placement Banned
				bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Placement, player, player.getItemInHand().getTypeId(), player.getItemInHand().getData().getData(), player.getLocation());
				if(bannedInfo != null) {
					event.setCancelled(true);
					//Drop the item in hand
					player.getWorld().dropItem(player.getLocation(), player.getItemInHand());
					player.setItemInHand(new ItemStack(Material.AIR));
					if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
						player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
					}
					
					player.updateInventory();
					itemrestrict.getConfigHandler().printMessage(player, "chatMessages.placementRestricted", bannedInfo.reason);
				}
			}
			
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block block = event.getClickedBlock();
				
				bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Usage, player, block.getTypeId(), block.getData(), block.getLocation());
				if(bannedInfo != null) {
					event.setCancelled(true);
					if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
					}
					
					player.updateInventory();
					itemrestrict.getConfigHandler().printMessage(player, "chatMessages.ussageRestricted", bannedInfo.reason);
				} else {
					bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Ownership, player, block.getTypeId(), block.getData(), block.getLocation());
					if(bannedInfo != null)
					{
						event.setCancelled(true);
						if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
							player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
						}
						itemrestrict.getConfigHandler().printMessage(player, "chatMessages.placementRestricted", bannedInfo.reason);
					}
				}
			}
		}
			
		//Disable damage event for usage banned items
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.LOWEST)
		void onDamage(EntityDamageByEntityEvent event) {
			//Check if damager is a player else stop
			if (event.getDamager().getType() != EntityType.PLAYER) {
				return;
			};
			//If damager is a player check the item used to damage
			Player player = (Player)event.getDamager();
			if (player == null) return;
			
			MaterialData bannedInfo = itemrestrict.getRestrictedItemsHandler().isBanned(ActionType.Usage, player, player.getItemInHand().getTypeId(), player.getItemInHand().getData().getData(), player.getLocation());
			if(bannedInfo != null) {
				//Cancel damage event
				event.setCancelled(true);
				//Drop the item in hand
				player.getWorld().dropItem(player.getLocation(), player.getItemInHand());
				player.setItemInHand(new ItemStack(Material.AIR));
				if (itemrestrict.getConfigHandler().getString("General.Sounds.onRestrictions").matches("true")) {
					player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
				}
				
				player.updateInventory();
				//Send message
				itemrestrict.getConfigHandler().printMessage(player, "chatMessages.ussageRestricted", bannedInfo.reason);
			}
		}

}
