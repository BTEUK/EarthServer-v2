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
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;

public class StaffGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Staff Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		MemberData memberData = Main.getInstance().memberData;
		OwnerData ownerData = Main.getInstance().ownerData;
		PlayerData playerData = Main.getInstance().playerData;
		RequestData requestData = Main.getInstance().requestData;
		
		if (u.p.hasPermission("earthserver.admin.review")) {

			if (requestData.count() > 0) {
				Utils.createItem(inv, Material.CHEST, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Jr.Builder Requests", 
						Utils.chat("&fClick to view all the join requests by Jr.Builders."),
						Utils.chat("&fThere are currently " + requestData.count() + " requests"));
			}
		}

		if (u.p.hasPermission("earthserver.admin.edit")) {
			Utils.createItem(inv, Material.WORKBENCH, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Region",
					Utils.chat("&fClick to open the region edit menu."),
					Utils.chat("&fYou can edit accessiblity and members of the claim."));
		}
		
		String owner;
		if (ownerData.hasOwner(u.current_region)) {
			owner = playerData.getName(ownerData.getOwner(u.current_region));
		} else {
			owner = "No Owner";
		}

		int members = memberData.countMembers(u.current_region);

		Utils.createItem(inv, Material.BOOK, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Region Info",
				Utils.chat("&fRegion: " + u.current_region),
				Utils.chat("&fOwner: " + owner),
				Utils.chat("&fNumber of Members: " + members));

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the claim menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(ClaimGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Jr.Builder Requests")) {

			u.gui_page = 1;
			u.staff_request = true;
			u.p.closeInventory();
			u.p.openInventory(StaffRequests.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Region")) {

			u.p.closeInventory();
			u.p.openInventory(StaffOptions.GUI(u));

		}
	}

}
