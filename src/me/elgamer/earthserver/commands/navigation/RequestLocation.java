package me.elgamer.earthserver.commands.navigation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.LocationSQL;

public class RequestLocation implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command cannot be sent from the console!");
			return false;
		}
		
		Player p = (Player) sender;
		
		if (!(p.hasPermission("earthserver.location.request"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}
		
		if (args.length != 1) {
			p.sendMessage(ChatColor.RED + "/locationrequest <name>");
			return true;
		}
		
		LocationSQL locationSQL = Main.getInstance().locationData;
		
		if (locationSQL.requestExists(args[0])) {
			p.sendMessage(ChatColor.RED + "This location has already been requested.");
			return true;
		}
		
		if (locationSQL.addRequest(args[0], p.getLocation())) {
			p.sendMessage(ChatColor.GREEN + "You have requested the location " + args[0]);
		} else {
			p.sendMessage(ChatColor.RED + "An error has occured, please try again.");
		}
		return true;
	}

}
