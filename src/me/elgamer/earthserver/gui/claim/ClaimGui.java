package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.ClaimLimit;
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

		MemberData memberData = Main.getInstance().memberData;
		OwnerData ownerData = Main.getInstance().ownerData;
		PlayerData playerData = Main.getInstance().playerData;
		RegionData regionData = Main.getInstance().regionData;
		RequestData requestData = Main.getInstance().requestData;
		
		regionData.createRegionIfNotExists(u.current_region);

		if (regionData.isLocked(u.current_region)) {
			Utils.createItem(inv, Material.IRON_FENCE, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region,
					Utils.chat("&fThis region is locked, it can not be edited by anyone."));
		} else if (regionData.isOpen(u.current_region)) {
			Utils.createItem(inv, Material.BOOK, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
					Utils.chat("&fThis region is open, you can build here without being a member of the claim."));
		} else if (ownerData.isOwner(u.uuid, u.current_region)) {
			Utils.createItem(inv, Material.BOOK_AND_QUILL, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
					Utils.chat("&fYou are the owner of this region, click to open the settings menu."));
		} else if (memberData.isMember(u.uuid, u.current_region)) {
			Utils.createItem(inv, Material.BOOK_AND_QUILL, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
					Utils.chat("&fYou are a member of this region, click to open the settings menu."));
		} else if (requestData.hasRequested(u.current_region, u.uuid)) {
			Utils.createItem(inv, Material.BARRIER, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
					Utils.chat("&fYou have requested to join this region, the request is pending."),
					Utils.chat("&fClick to cancel the request."));	
		} else if (ownerData.hasOwner(u.current_region)) {

			if (regionData.isPublic(u.current_region)) {
				Utils.createItem(inv, Material.DARK_OAK_DOOR_ITEM, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
						Utils.chat("&fThis region is public, click to join the region."),
						Utils.chat("&f" + playerData.getName(ownerData.getOwner(u.current_region))) + " is the owner of this region.");
			} else {
				Utils.createItem(inv, Material.DARK_OAK_DOOR_ITEM, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
						Utils.chat("&fThis region is claimed, click to request access to build."),
						Utils.chat("&f" + playerData.getName(ownerData.getOwner(u.current_region))) + " is the owner of this region.");
			}

		} else {
			Utils.createItem(inv, Material.DARK_OAK_DOOR_ITEM, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region " + u.current_region, 
					Utils.chat("&fThis region does not have an owner, click to claim the region."));
		}

		if ((ownerData.count(u.uuid) + memberData.count(u.uuid)) > 0) {
			Utils.createItem(inv, Material.CHEST, 1, 21, ChatColor.AQUA + "" + ChatColor.BOLD + "Region List", 
					Utils.chat("&fClick to view all regions you are owner or member of."),
					Utils.chat("&fYou are the owner of " + ownerData.count(u.uuid) + " region(s)"),
					Utils.chat("&fand a member of " + memberData.count(u.uuid) + " region(s)."));
		}

		if (requestData.count(u.uuid) > 0) {
			Utils.createItem(inv, Material.CHEST, 1, 25, ChatColor.AQUA + "" + ChatColor.BOLD + "Join Requests", 
					Utils.chat("&fClick to view all the join requests for regions you own."),
					Utils.chat("&fThere are currently " + requestData.count(u.uuid) + " requests"));
		}

		if (u.p.hasPermission("earthserver.admin.review") || u.p.hasPermission("earthserver.admin.edit")) {

			Utils.createItem(inv, Material.EMERALD, 1, 23, ChatColor.AQUA + "" + ChatColor.BOLD + "Staff Menu",
					Utils.chat("&fClick to open the staff menu, functions will depend on permissions of your role."));

		}

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		RequestData requestData = Main.getInstance().requestData;
		
		if (clicked.getType().equals(Material.DARK_OAK_DOOR_ITEM)) {

			u.p.closeInventory();

			if (ClaimLimit.limitReached(u) && !(u.p.hasPermission("earthserver.claim.bypass"))) {

				u.p.sendMessage(ChatColor.RED + "You have reached your claim limit, leave another region or cancel a request to join this one.");

			} else {

				u.p.sendMessage(RegionFunctions.joinRegion(u));

			}

		} else if (clicked.getType().equals(Material.BOOK_AND_QUILL)) {

			u.region_name = u.current_region;

			u.p.closeInventory();

			u.previous_gui = "main";
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


		} else if (clicked.getType().equals(Material.BARRIER)) {

			if (requestData.hasRequested(u.current_region, u.uuid)) {
				requestData.closeRequest(u.current_region, u.uuid);
				u.p.sendMessage(ChatColor.GREEN + "You have cancelled to request to join " + u.current_region);
				u.p.closeInventory();

			} else {
				u.p.sendMessage(ChatColor.RED + "This request does no longer exist!");
				u.p.closeInventory();
			}

		} else if (clicked.getType().equals(Material.EMERALD)) {

			u.p.closeInventory();
			u.p.openInventory(StaffGui.GUI(u));		

		}

	}

}
