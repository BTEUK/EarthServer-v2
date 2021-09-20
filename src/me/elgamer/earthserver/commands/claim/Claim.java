package me.elgamer.earthserver.commands.claim;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.gui.claim.ClaimGui;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
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

			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("info")) {
					
					OwnerData ownerData = Main.getInstance().ownerData;
					PlayerData playerData = Main.getInstance().playerData;

					if (ownerData.getOwner(u.current_region).equals("false")) {
						p.sendMessage(ChatColor.GREEN + "Region " + u.current_region);
					} else {
						p.sendMessage(ChatColor.GREEN + "Region " + u.current_region + " owned by " + playerData.getName(ownerData.getOwner(u.current_region)));
					}

					MemberData memberData = Main.getInstance().memberData;

					if (memberData.hasMember(u.current_region)) {
						ArrayList<String> members = memberData.getMembers(u.current_region);

						String memberString = null;

						for (int i = 0; i < members.size(); i++) {

							if (i == 0) {
								memberString = playerData.getName(members.get(i));
							} else {
								memberString = memberString + ", " + playerData.getName(members.get(i));
							}

						}

						if (memberString.split(" ").length > 1) {
							memberString = memberString + " are members of this region.";
						} else {
							memberString = memberString + " is a member of this region.";
						}

						if (memberString != null) {
							p.sendMessage(ChatColor.GREEN + memberString);
						}
					}

					return true;
				}
			}

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
