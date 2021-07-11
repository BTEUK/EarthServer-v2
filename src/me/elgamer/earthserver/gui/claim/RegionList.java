package me.elgamer.earthserver.gui.claim;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;

public class RegionList {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Region Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		ResultSet owners = OwnerData.getRegions(u.uuid);
		ResultSet members = MemberData.getRegions(u.uuid);

		u.gui_slot = (u.gui_page-1)*45 + 11;

		try {

			if (u.gui_page > 1) {

				for (int i = 0; i < (u.gui_page-1)*21; i++) {
					if (owners.next()) {

					} else {
						members.next();
					}
				}

			}

			while (owners.next()) {

				Utils.createItemByte(inv, Material.CONCRETE, 5, 1, u.gui_slot, ChatColor.AQUA + "" + ChatColor.BOLD + owners.getString("REGION_ID"), 
						Utils.chat("&fYou are the owner of this region."),
						Utils.chat("&fClick to edit this region."));

				if ((u.gui_slot & 45) == 17 ) {
					u.gui_slot += 3;
				} else if ((u.gui_slot & 45) == 26) {
					u.gui_slot += 3;
				} else if ((u.gui_slot & 45) == 35) {

					Utils.createItem(inv, Material.ARROW, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page",
							Utils.chat("&fClick to go to the next page of regions."));

					break;
				} else {
					u.gui_slot += 1;
				}

			}

			while (members.next()) {

				Utils.createItemByte(inv, Material.CONCRETE, 4, 1, u.gui_slot, ChatColor.AQUA + "" + ChatColor.BOLD + members.getString("REGION_ID"), 
						Utils.chat("&fYou are a member of this region."));

				if ((u.gui_slot & 45) == 17 ) {
					u.gui_slot += 3;
				} else if ((u.gui_slot & 45) == 26) {
					u.gui_slot += 3;
				} else if ((u.gui_slot & 45) == 35) {

					Utils.createItem(inv, Material.ARROW, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page",
							Utils.chat("&fClick to go to the next page of regions."));

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
					Utils.chat("&fClick to return to the previous page of regions."));

		}

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 45, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the claim menu."));

		if (RequestData.hasRequest(u.uuid)) {

			Utils.createItem(inv, Material.CHEST, 1, 41, ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Requests",
					Utils.chat("&fClick to edit any region join requests that have not yet been reviewed."));
			
		}




		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(ClaimGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page")) {

			u.gui_page += 1;
			u.p.closeInventory();
			u.p.openInventory(RegionList.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page")) {

			u.gui_page -= 1;
			u.p.closeInventory();
			u.p.openInventory(RegionList.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Requests")) {
			
			if (RequestData.hasRequest(u.uuid)) {
				
				u.gui_page = 1;
				u.p.closeInventory();
				u.p.openInventory(EditRequests.GUI(u));
				
			} else {
				u.p.sendMessage(ChatColor.RED + "You have no outstanding region requests.");
				u.p.openInventory(RegionList.GUI(u));
			}
			
		} else {

			u.region_name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

			u.p.closeInventory();

			if (OwnerData.isOwner(u.uuid, u.region_name)) {

				u.previous_gui = "region";
				u.p.openInventory(RegionOptions.GUI(u));

			} else if (MemberData.isMember(u.uuid, u.region_name)) {

				u.previous_gui = "region";
				u.p.openInventory(RegionOptions.GUI(u));

			} else {

				u.p.sendMessage(ChatColor.RED + "An error occured, please try again!");

			}
		}

	}

}
