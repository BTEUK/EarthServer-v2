package me.elgamer.earthserver.commands.claim;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionLogs;

public class FixRegions implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!(p.hasPermission("earthserver.admin.convertclaims"))) {
				p.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}
		}

		ResultSet logs = RegionLogs.getLogsAfter(1626985001887L);

		try {
			while (logs.next()) {
				if (logs.getString("ROLE").equals("owner")) {
					if (OwnerData.hasOwner(logs.getString("REGION_ID"))) {
						continue;
					}
					OwnerData.addOwner(logs.getString("REGION_ID"), logs.getString("UUID"));
					RegionLogs.newLog(logs.getString("REGION_ID"), logs.getString("UUID"), "owner");
					Bukkit.broadcastMessage(ChatColor.GREEN + "Added Owner");
				}
			}
			logs.first();
			if (logs.getString("ROLE").equals("member")) {
				if (!(OwnerData.isOwner(logs.getString("UUID"), logs.getString("REGION_ID")))){
					MemberData.addMember(logs.getString("REGION_ID"), logs.getString("UUID"));
					RegionLogs.newLog(logs.getString("REGION_ID"), logs.getString("UUID"), "member");
					Bukkit.broadcastMessage(ChatColor.GREEN + "Added Member");
				}
			}
			while (logs.next()) {
				if (logs.getString("ROLE").equals("member")) {
					if (!(OwnerData.isOwner(logs.getString("UUID"), logs.getString("REGION_ID")))){
						MemberData.addMember(logs.getString("REGION_ID"), logs.getString("UUID"));
						RegionLogs.newLog(logs.getString("REGION_ID"), logs.getString("UUID"), "member");
						Bukkit.broadcastMessage(ChatColor.GREEN + "Added Member");
					}
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sender.sendMessage(ChatColor.GREEN + "Fix Complete!");
		return true;
	}

}
