package me.elgamer.earthserver.utils;

import java.util.HashMap;

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
		long timeSpan = days * 24 * 60 * 60 * 1000;
		long inactivity = currentTime - timeSpan;

		HashMap<String, String> inactiveOwners = OwnerData.getInactiveOwners(inactivity);

		if (inactiveOwners == null) {
			return;
		} else {
			if (MemberData.addMembers(inactiveOwners)) {
				RegionLogs.closeLogs(inactiveOwners);
				RegionLogs.newLogs(inactiveOwners, "member");
				OwnerData.removeInactiveOwners(inactivity);
				OwnerData.addNewOwners(inactiveOwners);
			}
		}

	}

	public static void members() {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Find minimum inactive time for demotion
		long currentTime = Time.currentTime();

		long days = config.getLong("member_inactive");
		long timeSpan = days * 24 * 60 * 60 * 1000;
		long inactivity = currentTime - timeSpan;
		
		HashMap<String, String> inactiveMembers = MemberData.getInactiveMembers(inactivity);
		
		if (inactiveMembers == null) {
			return;
		} else {
			RegionLogs.closeLogs(inactiveMembers);
			WorldGuardFunctions.removeMembers(inactiveMembers);
			MemberData.removeInactiveMembers(inactivity);
		}

	}

}