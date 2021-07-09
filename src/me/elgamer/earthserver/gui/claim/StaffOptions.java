package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class StaffOptions {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Region";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();
		
		RegionData.createRegionIfNotExists(u.current_region);
		
		if (RegionData.isOpen(u.current_region)) {
			Utils.createItem(inv, Material.REDSTONE_LAMP_OFF, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Close Region",
					Utils.chat("&fClick to make the region closed."),
					Utils.chat("&fA closed region is just a default region."),
					Utils.chat("&fWhere you need to join to be able to build."));
		} else {
			Utils.createItem(inv, Material.REDSTONE_LAMP_ON, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Open Region",
					Utils.chat("&fClick to make the region open."),
					Utils.chat("&fOpen regions don't require Jr.Builders and Builders."),
					Utils.chat("&fTo join to start building."));
		}
		
		if (RegionData.isLocked(u.current_region)) {
			Utils.createItem(inv, Material.LEVER, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Unlock Region",
					Utils.chat("&fClick to unlock this region."),
					Utils.chat("&fThe region will no longer be locked and resume previous functions."));
		} else {
			Utils.createItem(inv, Material.IRON_BARDING, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Lock Region",
					Utils.chat("&fClick to lock this region."),
					Utils.chat("&fLocked regions can not be edited."));
		}
		
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
				Utils.chat("&fClick to go back to the staff menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(StaffGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Jr.Builder Requests")) {
		
			u.gui_page = 1;
			u.staff_request = true;
			u.p.openInventory(StaffRequests.GUI(u));
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Open Region")) {
			
			RegionData.setOpen(u.current_region);
			WorldGuardFunctions.setOpen(u.current_region);
			
			u.p.closeInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now open!");
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Close Region")) {
			
			RegionData.setClosed(u.current_region);
			WorldGuardFunctions.setClosed(u.current_region);
			
			u.p.closeInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now closed!");
			
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Lock Region")) {
			
			RegionData.setLocked(u.current_region);
			WorldGuardFunctions.setLocked(u.current_region);
			
			u.p.closeInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now open!");
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Unlock Region")) {
			
			RegionData.setUnlocked(u.current_region);
			WorldGuardFunctions.setUnlocked(u.current_region);
			
			u.p.closeInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now closed!");
			
			
		} else {

			
		}
	}

}
