package me.elgamer.earthserver.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.elgamer.earthserver.Main;

public class WorldGuardFunctions {

	public static void removeMembers(HashMap<String, String> members) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		World world = Bukkit.getWorld(config.getString("World_Name"));

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion region;
		DefaultDomain regionMembers;

		for (Entry<String, String> e : members.entrySet()) {

			region = regions.getRegion(e.getKey());
			regionMembers = region.getMembers();

			regionMembers.removePlayer(UUID.fromString(e.getValue()));
			region.setMembers(regionMembers);

			try {
				regions.save();
			} catch (StorageException e1) {
				e1.printStackTrace();
			}

		}
	}

	private static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}

	public static void removePublic(ArrayList<OldClaim> claims) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		World world = Bukkit.getWorld(config.getString("World_Name"));

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion region;
		DefaultDomain regionMembers;

		for (OldClaim claim : claims) {

			if (claim.public_private) {

				region = regions.getRegion(claim.region);
				regionMembers = region.getMembers();

				regionMembers.removeAll();
				region.setMembers(regionMembers);

				try {
					regions.save();
				} catch (StorageException e1) {
					e1.printStackTrace();
				}
			}

		}


	}

	public static void addMember(String region, String uuid) {

	}

	public static void addOwner(String region, String uuid) {

	}


}
