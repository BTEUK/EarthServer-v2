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
		
		OwnerData ownerData = Main.getInstance().ownerData;
		MemberData memberData = Main.getInstance().memberData;
		RegionLogs regionLogs = Main.getInstance().regionLogs;

		//Find minimum inactive time for demotion
		long currentTime = Time.currentTime();

		long days = config.getLong("owner_inactive");

		//Testing value
		//long timeSpan = days * 2000;

		long timeSpan = days * 24 * 60 * 60 * 1000;
		long inactivity = currentTime - timeSpan;

		HashMap<String, String> inactiveOwners = ownerData.getInactiveOwners(inactivity);

		if (inactiveOwners == null || inactiveOwners.isEmpty()) {
			//Bukkit.broadcastMessage("Return");
			return;
		} else {
			
			//Bukkit.broadcastMessage("Step 1 " + inactiveOwners.size());
			regionLogs.closeLogs(inactiveOwners);
			//Bukkit.broadcastMessage("Step 2");
			regionLogs.newLogs(inactiveOwners, "member");
			//Bukkit.broadcastMessage("Step 3");
			ownerData.removeInactiveOwners(inactivity);
			//Bukkit.broadcastMessage("Step 4");
			ownerData.addNewOwners(inactiveOwners);
			memberData.addMembers(inactiveOwners);
			Bukkit.broadcastMessage("Inactive Owners demoted to Member");

		}

	}

	public static void members() {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		
		MemberData memberData = Main.getInstance().memberData;
		RegionLogs regionLogs = Main.getInstance().regionLogs;

		//Find minimum inactive time for demotion
		long currentTime = Time.currentTime();

		long days = config.getLong("member_inactive");
		
		//Testing value
		//long timeSpan = days * 1000;
		
		long timeSpan = days * 24 * 60 * 60 * 1000;
		long inactivity = currentTime - timeSpan;

		HashMap<String, String> inactiveMembers = memberData.getInactiveMembers(inactivity);

		if (inactiveMembers == null || inactiveMembers.isEmpty()) {
			return;
		} else {
			regionLogs.closeLogs(inactiveMembers);
			WorldGuardFunctions.removeMembers(inactiveMembers);
			memberData.removeInactiveMembers(inactivity);
			Bukkit.broadcastMessage("Inactive Members removed from regions");
		}

	}

}