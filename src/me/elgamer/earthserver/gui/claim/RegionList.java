package me.elgamer.earthserver.gui.claim;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.Main;
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

		MemberData memberData = Main.getInstance().memberData;
		OwnerData ownerData = Main.getInstance().ownerData;
		RequestData requestData = Main.getInstance().requestData;

		ArrayList<String> owners = ownerData.getRegions(u.uuid);
		ArrayList<String> members = memberData.getRegions(u.uuid);

		u.gui_slot = (u.gui_page-1)*45 + 11;

		for (int i = (u.gui_page-1)*21; i < (owners.size() + members.size()); i++) {

			if (i < owners.size()) {

				Utils.createItemByte(inv, Material.CONCRETE, 5, 1, (u.gui_slot % 45), ChatColor.AQUA + "" + ChatColor.BOLD + owners.get(i), 
						Utils.chat("&fYou are the owner of this region."),
						Utils.chat("&fClick to edit this region."));

			} else if ((i - owners.size()) < members.size()) {

				Utils.createItemByte(inv, Material.CONCRETE, 4, 1, (u.gui_slot % 45), ChatColor.AQUA + "" + ChatColor.BOLD + members.get(i-owners.size()), 
						Utils.chat("&fYou are a member of this region."));
			}

			if ((u.gui_slot % 45) == 17 ) {
				u.gui_slot += 3;
			} else if ((u.gui_slot % 45) == 26) {
				u.gui_slot += 3;
			} else if ((u.gui_slot % 45) == 35) {

				if ((owners.size() + members.size() - 1) > i) {
					Utils.createItem(inv, Material.ARROW, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page",
							Utils.chat("&fClick to go to the next page of regions."));
				}

				break;
			} else {
				u.gui_slot += 1;
			}
		}

		if (u.gui_page > 1) {

			Utils.createItem(inv, Material.ARROW, 1, 19, ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page",
					Utils.chat("&fClick to return to the previous page of regions."));

		}

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 45, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the claim menu."));

		if (requestData.hasRequest(u.uuid)) {

			Utils.createItem(inv, Material.CHEST, 1, 41, ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Requests",
					Utils.chat("&fClick to edit any region join requests that have not yet been reviewed."));

		}




		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		RequestData requestData = Main.getInstance().requestData;
		
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

			if (requestData.hasRequest(u.uuid)) {

				u.gui_page = 1;
				u.p.closeInventory();
				u.p.openInventory(EditRequests.GUI(u));

			} else {
				u.p.sendMessage(ChatColor.RED + "You have no outstanding region requests.");
				u.p.openInventory(RegionList.GUI(u));
			}

		} else {
			
			MemberData memberData = Main.getInstance().memberData;
			OwnerData ownerData = Main.getInstance().ownerData;
			
			u.region_name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

			u.p.closeInventory();

			if (ownerData.isOwner(u.uuid, u.region_name)) {

				u.previous_gui = "region";
				u.p.openInventory(RegionOptions.GUI(u));

			} else if (memberData.isMember(u.uuid, u.region_name)) {

				u.previous_gui = "region";
				u.p.openInventory(RegionOptions.GUI(u));

			} else {

				u.p.sendMessage(ChatColor.RED + "An error occured, please try again!");

			}
		}

	}

}
