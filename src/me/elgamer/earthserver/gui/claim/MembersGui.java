package me.elgamer.earthserver.gui.claim;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.utils.Time;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;


public class MembersGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Members Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		ResultSet members = MemberData.getMembers(u.region_name);

		u.gui_slot = (u.gui_page-1)*45 + 11;

		String uuid;
		String member;
		
		try {
	
			if (u.gui_page > 1) {
				
				for (int i = 0; i < (u.gui_page-1)*21; i++) {
					members.next();
				}
				
			}
			
			while (members.next()) {
				
				uuid = members.getString("UUID");
				member =  PlayerData.getName(uuid);
				
				if (member == null) {
					
					member = Bukkit.getPlayer(UUID.fromString(uuid)).getName();
					
					if (member == null) {
						member = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
					}
					
					PlayerData.addPlayer(uuid, member);
				}

				Utils.createItemByte(inv, Material.CONCRETE, 4, 1, u.gui_slot, ChatColor.AQUA + "" + ChatColor.BOLD + member, 
						Utils.chat("&fLast Entered Region: " + Time.getDate(members.getLong("LAST_ENTER"))),
						Utils.chat("&fClick to edit member, you can remove member or transfer ownership."));

				if ((u.gui_slot & 45) == 17 ) {
					u.gui_slot += 3;
				} else if ((u.gui_slot & 45) == 26) {
					u.gui_slot += 3;
				} else if ((u.gui_slot & 45) == 35) {
					
					Utils.createItem(inv, Material.ARROW, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page",
							Utils.chat("&fClick to go to the next page of members."));
					
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
				Utils.chat("&fClick to go back to the edit region menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(RegionOptions.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page")) {
		
			u.gui_page += 1;
			u.p.closeInventory();
			u.p.openInventory(MembersGui.GUI(u));
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page")) {
			
			u.gui_page -= 1;
			u.p.closeInventory();
			u.p.openInventory(MembersGui.GUI(u));
			

		} else {
			
			u.member_name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

			u.p.closeInventory();
			
			u.p.openInventory(EditMember.GUI(u));
			
		}
	}

}
