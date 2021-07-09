package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

		if (RequestData.count() > 0) {
			Utils.createItem(inv, Material.CHEST, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Jr.Builder Requests", 
					Utils.chat("&fClick to view all the join requests by Jr.Builders."),
					Utils.chat("&fThere are currently " + RequestData.count() + " requests"));
		}
		
		Utils.createItem(inv, Material.WORKBENCH, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Region",
					Utils.chat("&fClick to open the region edit menu."),
					Utils.chat("&fYou can edit accessiblity and members of the claim."));
		
		String owner;
		if (OwnerData.hasOwner(u.current_region)) {
			owner = PlayerData.getName(OwnerData.getOwner(u.current_region));
		} else {
			owner = "No Owner";
		}
		
		int members = MemberData.countMembers(u.current_region);
		
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
			u.p.openInventory(StaffRequests.GUI(u));
			
		} else {

			String[] info = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).replace(" ","").split(",");
			u.region_requester = info[0];
			u.region_name = info[1];

			u.p.closeInventory();
			
			if (RequestData.requestExists(u.region_name, u.region_requester)) {
				u.p.openInventory(RequestReview.GUI(u));
			} else {
				u.p.sendMessage(ChatColor.RED + "This request does no longer exist!");
			}
		}
	}

}
