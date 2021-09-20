package me.elgamer.earthserver.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionData;

public class WorldGuardFunctions {

	public static void removeMembers(HashMap<String, String> members) {

		World world = Main.buildWorld;

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

		World world = Main.buildWorld;

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

		World world = Main.buildWorld;

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion WGregion = regions.getRegion(region);
		DefaultDomain regionMembers = WGregion.getMembers();

		regionMembers.addPlayer(UUID.fromString(uuid));
		WGregion.setMembers(regionMembers);

		try {
			regions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}

	}

	public static void setOpen(String region) {

		World world = Main.buildWorld;

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion WGregion = regions.getRegion(region);
		DefaultDomain regionMembers = WGregion.getMembers();

		regionMembers.addGroup("jrbuilder");
		WGregion.setMembers(regionMembers);

		try {
			regions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}
		
	}

	public static void setClosed(String region) {

		World world = Main.buildWorld;

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion WGregion = regions.getRegion(region);
		DefaultDomain regionMembers = WGregion.getMembers();

		regionMembers.removeGroup("jrbuilder");
		WGregion.setMembers(regionMembers);

		try {
			regions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void setLocked(String region) {
		
		World world = Main.buildWorld;

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion WGregion = regions.getRegion(region);
		DefaultDomain regionMembers = WGregion.getMembers();
		
		regionMembers.clear();
		WGregion.setMembers(regionMembers);

		try {
			regions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void setUnlocked(String region) {
		
		World world = Main.buildWorld;
		
		RegionData regionData = Main.getInstance().regionData;
		OwnerData ownerData = Main.getInstance().ownerData;
		MemberData memberData = Main.getInstance().memberData;

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion WGregion = regions.getRegion(region);
		DefaultDomain regionMembers = WGregion.getMembers();
		
		if (ownerData.hasOwner(region)) {
			regionMembers.addPlayer(UUID.fromString(ownerData.getOwner(region)));
		}
		
		if (memberData.hasMember(region)) {
			ArrayList<String> members = memberData.getMembers(region);
			
			for (String member : members) {
				regionMembers.addPlayer(UUID.fromString(member));
			}
		}
		
		if (regionData.isOpen(region)) {
			regionMembers.addGroup("jrbuilder");
		}
		
		WGregion.setMembers(regionMembers);

		try {
			regions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void removeMember(String region, String uuid) {

		World world = Main.buildWorld;

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion WGregion = regions.getRegion(region);
		DefaultDomain regionMembers = WGregion.getMembers();

		regionMembers.removePlayer(UUID.fromString(uuid));
		WGregion.setMembers(regionMembers);

		try {
			regions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}

	}
	
	public static void createRegion(String region, int x, int z) {
		
		World world = Main.buildWorld;

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);
		
		BlockVector min = new BlockVector(x*512, -512, z*512);
		BlockVector max = new BlockVector(x*512 +511, 1536, z*512 +511);
		ProtectedRegion WGregion = new ProtectedCuboidRegion(region, min, max);
		
		regions.addRegion(WGregion);

		try {
			regions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void convertOwners(ArrayList<OldClaim> claims) {

		World world = Main.buildWorld;

		WorldGuardPlugin wg = getWorldGuard();

		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion region;
		DefaultDomain regionMembers;
		DefaultDomain regionOwners;
		UUID ownerID = null;
		Set<UUID> owners;
		
		for (OldClaim e : claims) {

			region = regions.getRegion(e.region);
			regionMembers = region.getMembers();
			regionOwners = region.getOwners();

			owners = regionOwners.getUniqueIds();
			for (UUID uuid : owners) {
				ownerID = uuid;
			}
			regionOwners.clear();
			
			regionMembers.addPlayer(ownerID);
			region.setMembers(regionMembers);
			region.setOwners(regionOwners);

			try {
				regions.save();
			} catch (StorageException e1) {
				e1.printStackTrace();
			}

		}
	}


}
