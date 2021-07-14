package me.elgamer.earthserver.commands.claim;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OldClaimSQL;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.utils.OldClaim;

public class ConvertClaimData implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!(p.hasPermission("earthserver.admin.convertclaims"))) {
				p.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}
		}
		
		if (RegionData.hasEntry()) {
			sender.sendMessage(ChatColor.RED + "This command can only be run when there are no claims in the new system!");
		}
	
		ArrayList<OldClaim> claims = OldClaimSQL.getAllClaims();
		
		RegionData.convertRegions(claims);
		OwnerData.convertOwners(claims);
		MemberData.convertMembers(claims);
		RegionLogs.startLogs();
		//Disabled for testing purposes.
		//WorldGuardFunctions.removePublic(claims);
		//Permissions.removePermissions(claims);
		
		return true;
	}

}
