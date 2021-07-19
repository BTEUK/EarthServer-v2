package me.elgamer.earthserver.gui.claim;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;

public class StaffRequests {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Jr.Builder Requests";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		ResultSet results = RequestData.getRequests();

		u.gui_slot = (u.gui_page-1)*45 + 11;

		try {
	
			if (u.gui_page > 1) {
				
				for (int i = 0; i < (u.gui_page-1)*21; i++) {
					results.next();
				}
				
			}
			
			while (results.next()) {

				Utils.createItemByte(inv, Material.CONCRETE, 5, 1, (u.gui_slot % 45), ChatColor.AQUA + "" + ChatColor.BOLD + PlayerData.getName(results.getString("UUID") + ", " + results.getString("REGION_ID")), 
						Utils.chat("&fClick to review the request."));

				if ((u.gui_slot % 45) == 17 ) {
					u.gui_slot += 3;
				} else if ((u.gui_slot % 45) == 26) {
					u.gui_slot += 3;
				} else if ((u.gui_slot % 45) == 35) {
					
					Utils.createItem(inv, Material.ARROW, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page",
							Utils.chat("&fClick to go to the next page of requests."));
					
					break;
				} else {
					u.gui_slot += 1;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (u.gui_page > 1) {
			
			Utils.createItem(inv, Material.ARROW, 1, 19, ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page",
					Utils.chat("&fClick to return to the previous page of requests."));
			
		}

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 45, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the claim menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(ClaimGui.GUI(u));

		} else if (clicked.getType().equals(Material.BOOK_AND_QUILL)) {
			u.p.closeInventory();

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page")) {
		
			u.gui_page += 1;
			u.p.closeInventory();
			u.p.openInventory(StaffRequests.GUI(u));
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page")) {
			
			u.gui_page -= 1;
			u.p.closeInventory();
			u.p.openInventory(StaffRequests.GUI(u));
			
		} else {

			String[] info = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).replace(" ","").split(",");
			u.region_requester = info[0];
			u.region_name = info[1];

			u.p.closeInventory();
			
			if (RequestData.requestExists(u.region_name, u.region_requester, u.staff_request)) {
				u.previous_gui = "staff";
				u.p.openInventory(RequestReview.GUI(u));
			} else {
				u.p.sendMessage(ChatColor.RED + "This request does no longer exist!");
			}
		}

	}

}
