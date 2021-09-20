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

		Utils.createItem(inv, Material.BARRIER, 1, 13, ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Member",
				Utils.chat("&fClick to remove " + u.member_name + " from this region."));
		
		Utils.createItem(inv, Material.MINECART, 1, 15, ChatColor.AQUA + "" + ChatColor.BOLD + "Transfer Ownership",
				Utils.chat("&fClick to make " + u.member_name + " owner of this region."),
				Utils.chat("&fYou will lose ownership of the region,"),
				Utils.chat("&fhowever you will remain a member."));


		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the region members menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		MemberData memberData = Main.getInstance().memberData;
		OwnerData ownerData = Main.getInstance().ownerData;
		PlayerData playerData = Main.getInstance().playerData;
		RegionLogs regionLogs = Main.getInstance().regionLogs;
		
		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(MembersGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Member")) {
			
			String uuid = playerData.getUUID(u.member_name);
			regionLogs.closeLog(u.region_name, uuid);
			WorldGuardFunctions.removeMember(u.region_name, uuid);
			memberData.removeMember(u.region_name, uuid);
			
			u.p.closeInventory();
			u.p.sendMessage(ChatColor.RED + "Removed " + u.member_name + " from the region " + u.region_name);

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Transfer Ownership")) {

			String uuid = playerData.getUUID(u.member_name);
			
			//Change owner to member
			regionLogs.closeLog(u.region_name, u.uuid);
			regionLogs.newLog(u.region_name, u.uuid, "member");
			ownerData.removeOwner(u.uuid, u.region_name);
			memberData.addMember(u.region_name, u.uuid);
			
			//Change member to owner
			regionLogs.closeLog(u.region_name, uuid);
			regionLogs.newLog(u.region_name, uuid, "owner");
			memberData.removeMember(u.region_name, uuid);
			ownerData.addOwner(u.region_name, uuid);

			u.p.closeInventory();
			u.p.sendMessage(ChatColor.GREEN + "Transferred ownership of the region " + u.region_name + " to " + u.member_name);

		} else {


		}
	}

}
