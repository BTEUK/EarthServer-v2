package me.elgamer.earthserver.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RegionData;

public class User {

	public Player p;
	public String uuid;
	public String name;
	public String builder_role;

	public String current_region;
	public boolean hasWorldEdit;
	
	public World current_world;
	
	public int buildingTime;
	
	public String request_name;
	public String request_region;
	public int request_slot;
	public int request_page;

	public User(Player p) {
		this.p = p;
		uuid = p.getUniqueId().toString();
		name = p.getName();
		
		this.buildingTime = PlayerData.getBuildingTime(uuid);

		current_world = p.getWorld();
		
		if (current_world.getName().equals(Main.getInstance().getConfig().getString("World_Name"))) {
			current_region = getRegion(p);
		} else {
			current_region = "buildhub";
		}

		if (p.hasPermission("group.builder")) {
			builder_role = "builder";
			if (!(current_region.equals("buildhub"))) {
				hasWorldEdit = updatePerms(this, current_region);
			}
		} else if (p.hasPermission("group.jrbuilder")) {
			builder_role = "jrbuilder";
			if (!(current_region.equals("buildhub"))) {
				hasWorldEdit = updatePerms(this, current_region);
			}
		} else if (p.hasPermission("group.apprentice")) {
			builder_role = "apprentice";
		} else {
			builder_role = "guest";
		}

	}

	public static String getRegion(Player p) {
		Location l = p.getLocation();
		double x = l.getX();
		double z = l.getZ();
		int rX = (int) Math.floor((x/512));
		int rZ = (int) Math.floor((z/512));
		return (rX + "," + rZ);
	}

	public static boolean updatePerms(User u, String region) {

		if (RegionData.isOpen(region)) {
			Permissions.giveWorldedit(u.uuid);
			return true;
		} else if (OwnerData.isOwner(u.uuid, region)) {
			Permissions.giveWorldedit(u.uuid);
			OwnerData.updateTime(u.uuid, region);
			return true;
		} else if (MemberData.isMember(u.uuid, region)) {
			Permissions.giveWorldedit(u.uuid);
			MemberData.updateTime(u.uuid, region);
			return true;
		} else {
			Permissions.removeWorldedit(u.uuid);
			return false;
		}

	}

}
