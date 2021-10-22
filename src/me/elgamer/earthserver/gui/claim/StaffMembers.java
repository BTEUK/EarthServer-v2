package me.elgamer.earthserver.gui.claim;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.utils.Time;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class StaffMembers {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Region Members";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory Gui (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		OwnerData ownerData = Main.getInstance().ownerData;
		MemberData memberData = Main.getInstance().memberData;
		PlayerData playerData = Main.getInstance().playerData;

		ArrayList<String> members = memberData.getMembers(u.current_region);

		if (ownerData.hasOwner(u.current_region)) {
			members.add(0, ownerData.getOwner(u.current_region));
		}		

		u.gui_slot = (u.gui_page-1)*45 + 11;

		String uuid;
		String member;
		Player p;

		for (int i = 0; i < members.size(); i++) {


			uuid = members.get(i);
			member =  playerData.getName(uuid);

			if (member == null) {

				p = Bukkit.getPlayer(UUID.fromString(uuid));

				if (p == null) {
					member = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
				} else {
					member = p.getName();
				}

				playerData.addPlayer(uuid, member);
			}

			if (ownerData.isOwner(uuid, u.current_region)) {
				Utils.createItemByte(inv, Material.CONCRETE, 5, 1, u.gui_slot, ChatColor.AQUA + "" + ChatColor.BOLD + member, 
						Utils.chat("&fLast Entered Region: " + Time.getDate(ownerData.lastEnter(u.current_region, uuid))),
						Utils.chat("&fRight click to kick them from the region."));

			} else {
				Utils.createItemByte(inv, Material.CONCRETE, 4, 1, u.gui_slot, ChatColor.AQUA + "" + ChatColor.BOLD + member, 
						Utils.chat("&fLast Entered Region: " + Time.getDate(memberData.lastEnter(u.current_region, uuid))),
						Utils.chat("&fLeft click to transfer ownership to this member, right click to kick them from the region."));
			}

			if ((u.gui_slot % 45) == 17 ) {
				u.gui_slot += 3;
			} else if ((u.gui_slot % 45) == 26) {
				u.gui_slot += 3;
			} else if ((u.gui_slot % 45) == 35) {

				Utils.createItem(inv, Material.ARROW, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page",
						Utils.chat("&fClick to go to the next page of members."));

				break;
			} else {
				u.gui_slot += 1;
			}

		}

		if (u.gui_page > 1) {

			Utils.createItem(inv, Material.ARROW, 1, 19, ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page",
					Utils.chat("&fClick to return to the previous page of members."));

		}

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 45, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to return to the region edit menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv, ClickType clickType) {

		MemberData memberData = Main.getInstance().memberData;
		OwnerData ownerData = Main.getInstance().ownerData;
		PlayerData playerData = Main.getInstance().playerData;
		RegionLogs regionLogs = Main.getInstance().regionLogs;
		RegionData regionData = Main.getInstance().regionData;
		
		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(StaffMembers.Gui(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Next Page")) {

			u.gui_page += 1;
			u.p.closeInventory();
			u.p.openInventory(StaffMembers.Gui(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Previous Page")) {

			u.gui_page -= 1;
			u.p.closeInventory();
			u.p.openInventory(StaffMembers.Gui(u));


		} else if (clickType.equals(ClickType.LEFT)) {
			
			String uuid = playerData.getUUID(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));

			if (ownerData.isOwner(uuid, u.current_region)) {
				return;
			}
			
			//Change owner to member
			if (ownerData.hasOwner(u.current_region)) {
				String owner = ownerData.getOwner(u.current_region);
				regionLogs.closeLog(u.current_region, owner);
				regionLogs.newLog(u.current_region, owner, "member");
				ownerData.removeOwner(owner, u.current_region);
				memberData.addMember(u.current_region, owner);
			}

			//Change member to owner
			regionLogs.closeLog(u.current_region, uuid);
			regionLogs.newLog(u.current_region, uuid, "owner");
			memberData.removeMember(u.current_region, uuid);
			ownerData.addOwner(u.current_region, uuid);

			u.p.closeInventory();
			u.p.sendMessage(ChatColor.GREEN + "Transferred ownership of the region " + u.current_region + " to " + ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));


		} else if (clickType.equals(ClickType.RIGHT)) {
			
			String uuid = playerData.getUUID(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
			
			if (ownerData.isOwner(uuid, u.current_region)) {

				if ((!memberData.hasMember(u.current_region)) && regionData.isPublic(u.current_region)) {
					
					regionData.setPrivate(u.current_region);
					
				}
				
				regionLogs.closeLog(u.current_region, uuid);
				ownerData.addNewOwner(u.current_region);
				ownerData.removeOwner(uuid, u.current_region);
				WorldGuardFunctions.removeMember(u.current_region, uuid);
				
				Player p = Bukkit.getPlayer(UUID.fromString(uuid));
				
				//If the player is online, update perms.
				if (p != null) {
					User.updatePerms(Main.getUser(p), u.current_region);
				}

			} else {
				
				regionLogs.closeLog(u.current_region,uuid);
				memberData.removeMember(u.current_region, uuid);
				WorldGuardFunctions.removeMember(u.current_region, uuid);
				
				Player p = Bukkit.getPlayer(UUID.fromString(uuid));
				
				//If the player is online, update perms.
				if (p != null) {
					User.updatePerms(Main.getUser(p), u.current_region);
				}
			}
			
			u.p.closeInventory();
			u.p.sendMessage(ChatColor.RED + "Removed " + ChatColor.stripColor(clicked.getItemMeta().getDisplayName()) + " from the region " + u.current_region);
			
		}
	}

}
