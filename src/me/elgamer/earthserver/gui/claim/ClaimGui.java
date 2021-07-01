package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.RegionFunctions;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;

public class ClaimGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Claim Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		if (OwnerData.isOwner(u.uuid, u.current_region)) {
			Utils.createItem(inv, Material.BOOK_AND_QUILL, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
					Utils.chat("&fYou are the owner of this region, click to open the settings menu."));
		} else if (MemberData.isMember(u.uuid, u.current_region)) {
			Utils.createItem(inv, Material.BOOK_AND_QUILL, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
					Utils.chat("&fYou are a member of this region, click to open the settings menu."));
		} else if (OwnerData.hasOwner(u.current_region)) {

			if (RegionData.isOpen(u.current_region)) {
				Utils.createItem(inv, Material.BOOK, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
						Utils.chat("&fThis region is open, you can build here without being a member of the claim."));
			} else if (RegionData.isPublic(u.current_region)) {
				Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
						Utils.chat("&fThis region is public, click to join the region."));
			} else {
				Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
						Utils.chat("&fThis region is claimed, click to request access to build."));
			}

		} else {
			Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
					Utils.chat("&fThis region is does not have an owner, click to claim the region."));
		}

		if ((OwnerData.count(u.uuid) + MemberData.count(u.uuid)) > 0) {
		Utils.createItem(inv, Material.CHEST, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Region List", 
				Utils.chat("&fClick to view all regions you are owner or member of."),
				Utils.chat("&fYou are the owner of " + OwnerData.count(u.uuid) + " regions"),
				Utils.chat("&fand a member of " + MemberData.count(u.uuid) + " regions."));
		}
		
		if (RequestData.count(u.uuid) > 0) {
			Utils.createItem(inv, Material.CHEST, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Join Requests", 
					Utils.chat("&fClick to view all the join requests for regions you own."),
					Utils.chat("&fThere are currently " + RequestData.count(u.uuid) + " requests"));
		}

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR)) {

			u.p.closeInventory();
			u.p.sendMessage(RegionFunctions.joinRegion(u));

		} else if (clicked.getType().equals(Material.BOOK_AND_QUILL)) {
			
			u.region_name = u.current_region;
			
			u.p.closeInventory();
			
			u.p.openInventory(RegionOptions.GUI(u));

		} else if (clicked.getType().equals(Material.CHEST)) {
			
			if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Region List")) {
				u.gui_page = 1;
				u.p.openInventory(RegionList.GUI(u));
			} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Join Requests")) {
				u.gui_page = 1;
				u.staff_request = false;
				u.p.openInventory(RequestGui.GUI(u));
			}
			
			
		}

	}

}
