package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.utils.RegionFunctions;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class RegionOptions {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Region";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		if (OwnerData.isOwner(u.uuid, u.region_name)) {

			if (MemberData.hasMember(u.region_name)) {
				Utils.createItem(inv, Material.MAGENTA_GLAZED_TERRACOTTA, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Members",
						Utils.chat("&fClick to open the region members menu."),
						Utils.chat("&fFrom here you can see information about the members"),
						Utils.chat("&fand you can remove members and transfer ownership or the region."));
			}

			Utils.createItem(inv, Material.BARRIER, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Leave Region",
					Utils.chat("&fClick to leave the region."),
					Utils.chat("&fThe most recent member will take over ownership."),
					Utils.chat("&fIf there are no members then it can be claimed by anyone."));


			if (RegionData.isPublic(u.region_name)) {
				Utils.createItem(inv, Material.IRON_DOOR, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Private Region",
						Utils.chat("&fClick to make the region private."),
						Utils.chat("&fA private region is the default region."),
						Utils.chat("&fAll new members need to be accepted by the region owner."));
			} else {
				Utils.createItem(inv, Material.BIRCH_DOOR_ITEM, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Public Region",
						Utils.chat("&fClick to make the region public."),
						Utils.chat("&fA public region implies that new members can join"),
						Utils.chat("&fwithout needing approval from the region owner."));
			}

		} else {

			Utils.createItem(inv, Material.BARRIER, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Leave Region",
					Utils.chat("&fClick to leave the region."));
		}

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the region menu."));
		
		Utils.createItem(inv, Material.EYE_OF_ENDER, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Teleport",
				Utils.chat("&fTeleport to the centre of the region."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();

			if (u.previous_gui.equals("main")) {

				u.p.openInventory(ClaimGui.GUI(u));

			} else {

				u.p.openInventory(RegionList.GUI(u));
				
			}

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Leave Region")) {
			
			u.p.closeInventory();

			if (OwnerData.isOwner(u.uuid, u.region_name)) {

				if ((!MemberData.hasMember(u.region_name)) && RegionData.isPublic(u.region_name)) {
					
					RegionData.setPrivate(u.region_name);
					
				}
				
				RegionLogs.closeLog(u.region_name,u.uuid);
				OwnerData.addNewOwner(u.region_name);
				OwnerData.removeOwner(u.uuid, u.region_name);
				WorldGuardFunctions.removeMember(u.region_name, u.uuid);
				
				User.updatePerms(u, u.current_region);
				u.p.sendMessage(ChatColor.GREEN + "You have left the region " + u.region_name);

			} else if (MemberData.isMember(u.uuid, u.region_name)) {
				
				RegionLogs.closeLog(u.region_name,u.uuid);
				MemberData.removeMember(u.region_name, u.uuid);
				WorldGuardFunctions.removeMember(u.region_name, u.uuid);
				
				User.updatePerms(u, u.current_region);
				u.p.sendMessage(ChatColor.GREEN + "You have left the region " + u.region_name);
				
			} else {
				
				u.p.sendMessage(ChatColor.RED + "You are not part of this region.");
				
			}
			
			if (u.previous_gui.equals("main")) {
				u.p.openInventory(ClaimGui.GUI(u));
			} else if (OwnerData.count(u.uuid) + MemberData.count(u.uuid) > 0)  {
				u.gui_page = 1;
				u.p.openInventory(RegionList.GUI(u));
			} else {
				u.p.openInventory(ClaimGui.GUI(u));
			}

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Members")) {

			u.gui_page = 1;
			u.p.closeInventory();
			u.p.openInventory(MembersGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Private Region")) {

			RegionData.setPrivate(u.region_name);

			u.p.closeInventory();
			u.p.openInventory(RegionOptions.GUI(u));
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.region_name + " is now private!");


		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Public Region")) {

			RegionData.setPublic(u.region_name);

			u.p.closeInventory();
			u.p.openInventory(RegionOptions.GUI(u));
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.region_name + " is now public!");

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Teleport")) {
			
			double[] proj = RegionFunctions.getTeleport(u.region_name);
			u.p.performCommand("tpll " + proj[1] + ", " + proj[0]);
			u.p.closeInventory();

		}
	}

}
