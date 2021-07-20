package me.elgamer.earthserver.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionLogs;

public class Inactive {

	public static void owners() {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Find minimum inactive time for demotion
		long currentTime = Time.currentTime();

		long days = config.getLong("owner_inactive");

		//Testing value
		//long timeSpan = days * 2000;

		long timeSpan = days * 24 * 60 * 60 * 1000;
		long inactivity = currentTime - timeSpan;

		HashMap<String, String> inactiveOwners = OwnerData.getInactiveOwners(inactivity);

		if (inactiveOwners == null || inactiveOwners.isEmpty()) {
			Bukkit.broadcastMessage("Return");
			return;
		} else {
			
			//Bukkit.broadcastMessage("Step 1 " + inactiveOwners.size());
			RegionLogs.closeLogs(inactiveOwners);
			//Bukkit.broadcastMessage("Step 2");
			RegionLogs.newLogs(inactiveOwners, "member");
			//Bukkit.broadcastMessage("Step 3");
			OwnerData.removeInactiveOwners(inactivity);
			//Bukkit.broadcastMessage("Step 4");
			OwnerData.addNewOwners(inactiveOwners);
			MemberData.addMembers(inactiveOwners);
			Bukkit.broadcastMessage("Inactive Owners demoted to Member");

		}

	}

	public static void members() {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Find minimum inactive time for demotion
		long currentTime = Time.currentTime();

		long days = config.getLong("member_inactive");
		
		//Testing value
		//long timeSpan = days * 1000;
		
		long timeSpan = days * 24 * 60 * 60 * 1000;
		long inactivity = currentTime - timeSpan;

		HashMap<String, String> inactiveMembers = MemberData.getInactiveMembers(inactivity);

		if (inactiveMembers == null || inactiveMembers.isEmpty()) {
			return;
		} else {
			RegionLogs.closeLogs(inactiveMembers);
			//WorldGuardFunctions.removeMembers(inactiveMembers);
			MemberData.removeInactiveMembers(inactivity);
			Bukkit.broadcastMessage("Inactive Members removed from regions");
		}

	}

}