package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class EditMember {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Member";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.WOODEN_DOOR, 1, 13, ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Member",
				Utils.chat("&fClick to remove " + u.member_name + " from this region."));
		
		Utils.createItem(inv, Material.WOODEN_DOOR, 1, 15, ChatColor.AQUA + "" + ChatColor.BOLD + "Transfer Ownership",
				Utils.chat("&fClick to make " + u.member_name + " owner of this region."),
				Utils.chat("&fYou will lose ownership of the region,"),
				Utils.chat("&fhowever you will remain a member."));


		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the region members menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(MembersGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Member")) {
			
			String uuid = PlayerData.getUUID(u.member_name);
			RegionLogs.closeLog(u.current_region, uuid);
			WorldGuardFunctions.removeMember(u.current_region, uuid);
			MemberData.removeMember(u.current_region, uuid);
			
			u.p.closeInventory();
			u.p.sendMessage(ChatColor.RED + "Removed " + u.member_name + " from the region " + u.current_region);

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Transfer Ownership")) {

			String uuid = PlayerData.getUUID(u.member_name);
			
			//Change owner to member
			RegionLogs.closeLog(u.current_region, u.uuid);
			RegionLogs.newLog(u.current_region, u.uuid, "member");
			OwnerData.removeOwner(u.uuid, u.current_region);
			MemberData.addMember(u.current_region, u.uuid);
			
			//Change member to owner
			RegionLogs.closeLog(u.current_region, uuid);
			RegionLogs.newLog(u.current_region, uuid, "owner");
			MemberData.removeMember(u.current_region, uuid);
			OwnerData.addOwner(u.current_region, uuid);

			u.p.sendMessage(ChatColor.GREEN + "Transferred ownership of the region " + u.current_region + " to " + u.member_name);

		} else {


		}
	}

}
