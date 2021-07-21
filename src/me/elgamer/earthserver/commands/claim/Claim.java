package me.elgamer.earthserver.commands.claim;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.gui.claim.ClaimGui;
import me.elgamer.earthserver.utils.User;

public class Claim implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (!(p.hasPermission("earthserver.claim"))) {
				p.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}
			
			User u = Main.getUser(p);	
			
			if (u.builder_role.equals("guest") || u.builder_role.equals("apprentice")) {
				User.updateRole(u);
			}
			
			p.openInventory(ClaimGui.GUI(u));
			
			return true;
		} else {
			sender.sendMessage("This command can only be run as a player!");
			return true;
		}

	}

}
