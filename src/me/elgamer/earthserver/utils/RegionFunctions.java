package me.elgamer.earthserver.utils;

import me.elgamer.UKnetUtilities.projections.ModifiedAirocean;
import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.sql.RequestData;
import net.md_5.bungee.api.ChatColor;

public class RegionFunctions {

	public static String joinRegion(User u) {

		if (u.p.hasPermission("group.builder")) {

			if (RegionData.isPublic(u.current_region)) {

				if (OwnerData.hasOwner(u.current_region)) {
					WorldGuardFunctions.addMember(u.current_region, u.uuid);
					MemberData.addMember(u.current_region, u.uuid);
					RegionLogs.newLog(u.current_region, u.uuid, "member");
					User.updatePerms(u, u.current_region);

					return (ChatColor.GREEN + "Joined region " + u.current_region + " as a member.");					
				} else {
					WorldGuardFunctions.addMember(u.current_region, u.uuid);
					OwnerData.addOwner(u.current_region, u.uuid);
					RegionLogs.newLog(u.current_region, u.uuid, "owner");
					User.updatePerms(u, u.current_region);

					return (ChatColor.GREEN + "Joined region " + u.current_region + " as an owner.");
				}


			} else if (OwnerData.hasOwner(u.current_region)) {

				RequestData.newRequest(u.current_region, u.uuid, true, false, u.p.getLocation());

				for (User us : Main.users) {

					if (OwnerData.isOwner(us.uuid, u.current_region)) {
						us.p.sendMessage(ChatColor.GREEN + "You have a new region join request, /claim to open the gui.");
					}

				}


				return (ChatColor.GREEN + "You have requested to join the region " + u.current_region + ", the region owner will need to accept the request.");				

			} else {

				WorldGuardFunctions.addMember(u.current_region, u.uuid);
				OwnerData.addOwner(u.current_region, u.uuid);
				RegionLogs.newLog(u.current_region, u.uuid, "owner");
				User.updatePerms(u, u.current_region);

				return (ChatColor.GREEN + "Joined region " + u.current_region + " as an owner.");

			}


		} else if (u.p.hasPermission("group.jrbuilder")) {

			if (RegionData.isPublic(u.current_region)) {

				RequestData.newRequest(u.current_region, u.uuid, false, true, u.p.getLocation());

				for (User us : Main.users) {

					if (us.p.hasPermission("earthserver.admin.review")) {
						us.p.sendMessage(ChatColor.GREEN + "A Jr.Builder has made a new region join request, /claim to open the gui.");
					}

				}

				return (ChatColor.GREEN + "You have requested to join the region " + u.current_region + ", a staff member will need to accept thed request.");

			} else if (OwnerData.hasOwner(u.current_region)) {

				RequestData.newRequest(u.current_region, u.uuid, false, false, u.p.getLocation());

				for (User us : Main.users) {

					if (OwnerData.isOwner(us.uuid, u.current_region)) {
						us.p.sendMessage(ChatColor.GREEN + "You have a new region join request, /claim to open the gui.");
					}

					if (us.p.hasPermission("earthserver.admin.review")) {
						us.p.sendMessage(ChatColor.GREEN + "A Jr.Builder has made a new region join request, /claim to open the gui.");
					}

				}

				return (ChatColor.GREEN + "You have requested to join the region " + u.current_region + ", the region owner and a staff member will need to accept the request.");				

			} else {

				RequestData.newRequest(u.current_region, u.uuid, false, true, u.p.getLocation());

				for (User us : Main.users) {

					if (us.p.hasPermission("earthserver.admin.review")) {
						us.p.sendMessage(ChatColor.GREEN + "A Jr.Builder has made a new region join request, /claim to open the gui.");
					}

				}

				return (ChatColor.GREEN + "You have requested to join the region " + u.current_region + ", a staff member will need to accept thed request.");

			}

		} else {

			return (ChatColor.RED + "You must be Jr.Builder or higher to join a region.");

		}

	}

	public static double[] getTeleport(String region) {

		String[] xz = region.split("");

		int rx = Integer.parseInt(xz[0]);
		int rz = Integer.parseInt(xz[0]);

		ModifiedAirocean projection = new ModifiedAirocean();

		int x = rx*512 + 256;
		int z = rz*512 + 256;

		double[] proj = projection.toGeo(x, z);

		return (proj);

	}

}
