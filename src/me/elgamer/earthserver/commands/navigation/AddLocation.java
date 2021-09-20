package me.elgamer.earthserver.commands.navigation;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.LocationSQL;

public class AddLocation implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command cannot be sent from the console!");
			return true;
		}

		Player p = (Player) sender;

		if (!(p.hasPermission("earthserver.location.add"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}

		if (args.length < 3 || args.length > 4) {
			p.sendMessage(ChatColor.RED + "/addlocation <name> <category> <subcategory> [requestName]");
			return true;
		}

		if (!(args[1].equalsIgnoreCase("london") || 
				args[1].equalsIgnoreCase("england") || 
				args[1].equalsIgnoreCase("wales") ||
				args[1].equalsIgnoreCase("scotland") ||
				args[1].equalsIgnoreCase("northern-ireland") ||
				args[1].equalsIgnoreCase("other"))) {

			p.sendMessage(ChatColor.RED + "Category must be one of the following:");
			p.sendMessage(ChatColor.RED + "England, Scotland, Wales, Northern-Ireland, London or Other");
			return true;

		}	

		LocationSQL locationSQL = Main.getInstance().locationData;
		
		if (args.length == 4) {
			if (!(locationSQL.requestExists(args[3]))) {
				p.sendMessage("This location has not been requested");
				return true;
			} else {
				if (locationSQL.locationExists(args[0])) {
					p.sendMessage("This location has already been added");
					return true;
				}

				Location l = locationSQL.getRequestLocation(args[3]);

				if (locationSQL.addLocation(args[0], args[1], args[2], l)) {
					p.sendMessage(ChatColor.GREEN + "The location " + args[0] + " has been added to the navigation menu in category " + args[1] + " and subcategory " + args[2]);
				}
				return true;
			}	
		}

		if (args.length != 3) {
			p.sendMessage(ChatColor.RED + "/addlocation <name> <category> <subcategory> [requestName]");
			return true;
		}

		if (locationSQL.locationExists(args[0])) {
			p.sendMessage("This location has already been added");
			return true;
		}

		if (locationSQL.addLocation(args[0], args[1], args[2], p.getLocation())) {
			p.sendMessage(ChatColor.GREEN + "The location " + args[0] + " has been added to the navigation menu in category " + args[1] + " and subcategory " + args[2]);
		} else {
			p.sendMessage(ChatColor.RED + "An error has occured, please try again.");
		}
		return true;
	}

}
