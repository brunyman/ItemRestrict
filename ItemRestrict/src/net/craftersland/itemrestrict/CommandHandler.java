package net.craftersland.itemrestrict;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	
    private ItemRestrict itemrestrict;
	
	public CommandHandler(ItemRestrict itemrestrict) {
		this.itemrestrict = itemrestrict;
	}
	
	//Commands
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String cmdlabel, final String[] args) {
		final Player p;
		//Main command: /itemrestrict
		if (cmdlabel.equalsIgnoreCase("itemrestrict")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					p = (Player) sender;
					sendHelp(p);
					return true;
				} else {
					sendConsoleHelp(sender);
					return false;
				}
			}
			if (args.length == 1) {
				//command: /itemrestrict reload
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender instanceof Player) {
						p = (Player) sender;
						if (p.hasPermission("ItemRestrict.admin")) {
							
							itemrestrict.onReload();
							
							itemrestrict.getConfigHandler().printMessage(p, "chatMessages.reload", "");
							if (itemrestrict.getConfigHandler().getString("General.Sounds.onCommands").matches("true")) {
								p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
							}
							
							return true;
						} else {
							itemrestrict.getConfigHandler().printMessage(p, "chatMessages.noPermission", "");
							
							if (itemrestrict.getConfigHandler().getString("General.Sounds.onCommands").matches("true")) {
								p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
							}
							return false;
						}
					} else {
						itemrestrict.onReload();
						return true;
					}
				}
				//command: /itemrestrict help
				if (args[0].equalsIgnoreCase("help")) {
					if (sender instanceof Player) {
						p = (Player) sender;
						sendHelp(p);
						return true;
					} else {
						sendConsoleHelp(sender);
						return false;
					}
				}
			}
			
			if (args.length > 1 || !args[0].equalsIgnoreCase("help") && !args[0].equalsIgnoreCase("reload")) {
				if (sender instanceof Player) {
					p = (Player) sender;
					itemrestrict.getConfigHandler().printMessage(p, "chatMessages.unknownCommand", "");
					if (itemrestrict.getConfigHandler().getString("General.Sounds.onCommands").matches("true")) {
						p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Unknown command for help do /itemrestrict");
				}
			}
		}
		return false;
	}
	
	//Player help page
	public void sendHelp(Player p) {
		if (itemrestrict.getConfigHandler().getString("General.Sounds.onCommands").matches("true")) {
			p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 1);
		}
		
		p.sendMessage(" ");
		p.sendMessage(ChatColor.DARK_RED + "-=-=-=-=-=-=-=-=-=-< " + ChatColor.RED + "" + ChatColor.BOLD + "ItemRestrict" + ChatColor.DARK_RED + " >-=-=-=-=-=-=-=-=-=-");
		if (p.hasPermission("ItemRestrict.admin")) {
			p.sendMessage(" ");
			p.sendMessage(ChatColor.RED + "        Reload plugin config and RestrictedItems list:");
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">> " + ChatColor.WHITE + "/itemrestrict reload");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.DARK_RED+ "-=-=-=-=-=-=-=-=-< " + ChatColor.RED + "" + ChatColor.BOLD + "Admin Help Page" + ChatColor.DARK_RED + " >-=-=-=-=-=-=-=-=-");
			p.sendMessage(" ");
		} else {
			p.sendMessage(" ");
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Make a restricted items list!");
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Plugin Download: " + ChatColor.WHITE + "http://goo.gl/1p1GeZ");
			p.sendMessage(" ");
			p.sendMessage(ChatColor.DARK_RED + "-=-=-=-=-=-=-=-=-=-=-< " + ChatColor.RED + "" + ChatColor.BOLD + "Help Page" + ChatColor.DARK_RED + " >-=-=-=-=-=-=-=-=-=-");
			p.sendMessage(" ");
		}
	}
	
	//Console help page
	public void sendConsoleHelp(CommandSender sender) {
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.DARK_RED + "-=-=-=-=-=-=-=-=-=-< " + ChatColor.RED + "" + ChatColor.BOLD + "ItemRestrict" + ChatColor.DARK_RED + " >-=-=-=-=-=-=-=-=-=-");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.RED + "        Reload plugin config and RestrictedItems list:");
			sender.sendMessage(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "/itemrestrict reload");
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.DARK_RED + "-=-=-=-=-=-=-=-=-< " + ChatColor.RED + "" + ChatColor.BOLD + "Console Help Page" + ChatColor.DARK_RED + " >-=-=-=-=-=-=-=-=-");
			sender.sendMessage(" ");
	}

}
