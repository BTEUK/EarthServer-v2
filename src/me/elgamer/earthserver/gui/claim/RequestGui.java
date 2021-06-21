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

public class RequestGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Request Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		ResultSet results = RequestData.getRequests(u.uuid);

		u.request_slot = (u.request_page-1)*45 + 11;

		try {
	
			if (u.request_page > 1) {
				
				for (int i = 0; i < (u.request_page-1)*21; i++) {
					results.next();
				}
				
			}
			
			while (results.next()) {

				Utils.createItemByte(inv, Material.CONCRETE, 5, 1, u.request_slot, ChatColor.AQUA + "" + ChatColor.BOLD + PlayerData.getName(results.getString("UUID") + ", " + results.getString("REGION_ID")), 
						Utils.chat("&fClick to review the request."));

				if ((u.request_slot & 45) == 17 ) {
					u.request_slot += 3;
				} else if ((u.request_slot & 45) == 26) {
					u.request_slot += 3;
				} else if ((u.request_slot & 45) == 35) {
					
					Utils.createItem(inv, Material.ARROW, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page",
							Utils.chat("&fClick to go to the next page of requests."));
					
					break;
				} else {
					u.request_slot += 1;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (u.request_page > 1) {
			
			Utils.createItem(inv, Material.ARROW, 1, 19, ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page",
					Utils.chat("&fClick to return to the previous page of requests."));
			
		}

		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 45, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the claim menu."));





		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR)) {

			u.p.closeInventory();
			u.p.openInventory(ClaimGui.GUI(u));

		} else if (clicked.getType().equals(Material.BOOK_AND_QUILL)) {
			u.p.closeInventory();

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page")) {
		
			u.request_page += 1;
			u.p.closeInventory();
			u.p.openInventory(RequestGui.GUI(u));
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page")) {
			
			u.request_page -= 1;
			u.p.closeInventory();
			u.p.openInventory(RequestGui.GUI(u));
			
		} else {

			String[] info = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).replace(" ","").split(",");
			u.request_name = info[0];
			u.request_region = info[1];

			u.p.closeInventory();
			u.p.openInventory(RequestReview.GUI(u));

		}

	}

}
