package net.craftersland.itemrestrict;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHandler {
	
	private ItemRestrict pd;
	
	public SoundHandler(ItemRestrict pd) {
		this.pd = pd;
	}
	
	public void sendAnvilLandSound(Player p) {
		if (pd.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
			if (pd.is19Server == true) {
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
			} else {
				p.playSound(p.getLocation(), Sound.valueOf("ANVIL_LAND"), 1, 1);
			}
		}
	}
	
	public void sendEndermanTeleportSound(Player p) {
		if (pd.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
			if (pd.is19Server == true) {
				p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
			} else {
				p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
			}
		}
	}
	
	public void sendItemBreakSound(Player p) {
		if (pd.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
			if (pd.is19Server == true) {
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
			} else {
				p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
			}
		}
	}
	
	public void sendPlingSound(Player p) {
		if (pd.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
			if (pd.is19Server == true) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 3F, 3F);
			} else {
				p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 3F, 3F);
			}
		}
	}
	
	public void sendLevelUpSound(Player p) {
		if (pd.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
			if (pd.is19Server == true) {
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
			} else {
				p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1F, 1F);
			}
		}
	}
	
	public void sendArrowHit(Player p) {
		if (pd.getConfigHandler().getBoolean("General.Sounds.onRestrictions") == true) {
			if (pd.is19Server == true) {
				p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1F, 1F);
			} else {
				p.playSound(p.getLocation(), Sound.valueOf("SUCCESSFUL_HIT"), 1F, 1F);
			}
		}
	}

}
