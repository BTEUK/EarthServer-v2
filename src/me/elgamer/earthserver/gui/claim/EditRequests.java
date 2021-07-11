package me.elgamer.earthserver.gui.claim;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;

public class EditRequests {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Requests";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		ResultSet results = RequestData.getRequests(u.uuid);

		u.gui_slot = 11;

		try {
			
			while (results.next()) {

				Utils.createItemByte(inv, Material.CONCRETE, 5, 1, u.gui_slot, ChatColor.AQUA + "" + ChatColor.BOLD + results.getString("REGION_ID"), 
						Utils.chat("&fClick to cancel the request."));

				if ((u.gui_slot & 45) == 17 ) {
					u.gui_slot += 3;
				} else if ((u.gui_slot & 45) == 26) {
					u.gui_slot += 3;
				} else if ((u.gui_slot & 45) == 35) {
										
					break;
				} else {
					u.gui_slot += 1;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 45, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the region menu."));

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.gui_page = 1;
			u.p.closeInventory();
			u.p.openInventory(RegionList.GUI(u));
			
		} else {

			String[] info = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).split(",");
			u.region_name = info[0] + "," + info[1];

			RequestData.closeRequest(u.uuid, u.region_name);
			
			u.p.closeInventory();
			u.gui_page = 1;
			
			if (RequestData.hasRequest(u.uuid)) {
				
				u.p.openInventory(EditRequests.GUI(u));
			} else {
				
				u.p.openInventory(RegionList.GUI(u));
				
			}
		}

	}

}
