package me.elgamer.earthserver.gui.claim;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class StaffOptions {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Region Settings";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();
		
		MemberData memberData = Main.getInstance().memberData;
		OwnerData ownerData = Main.getInstance().ownerData;
		PlayerData playerData = Main.getInstance().playerData;
		RegionData regionData = Main.getInstance().regionData;
		
		regionData.createRegionIfNotExists(u.current_region);
		
		if (regionData.isOpen(u.current_region)) {
			Utils.createItem(inv, Material.REDSTONE_LAMP_OFF, 1, 21, ChatColor.AQUA + "" + ChatColor.BOLD + "Close Region",
					Utils.chat("&fClick to make the region closed."),
					Utils.chat("&fA closed region is just a default region."),
					Utils.chat("&fWhere you need to join to be able to build."));
		} else {
			Utils.createItem(inv, Material.REDSTONE_TORCH_ON, 1, 21, ChatColor.AQUA + "" + ChatColor.BOLD + "Open Region",
					Utils.chat("&fClick to make the region open."),
					Utils.chat("&fOpen regions don't require Jr.Builders and Builders."),
					Utils.chat("&fTo join to start building."));
		}
		
		if (regionData.isPublic(u.current_region)) {
			Utils.createItem(inv, Material.IRON_DOOR, 1, 23, ChatColor.AQUA + "" + ChatColor.BOLD + "Private Region",
					Utils.chat("&fClick to make the region private."),
					Utils.chat("&fA private region means any join requests"),
					Utils.chat("&fhas to be accepted by the current region owner."));
		} else {
			Utils.createItem(inv, Material.BIRCH_DOOR_ITEM, 1, 23, ChatColor.AQUA + "" + ChatColor.BOLD + "Public Region",
					Utils.chat("&fClick to make the region public."),
					Utils.chat("&fA public region means anyone can join the region"),
					Utils.chat("&fwithout the region owner needing to accept."));
		}
		
		if (regionData.isLocked(u.current_region)) {
			Utils.createItem(inv, Material.LEVER, 1, 25, ChatColor.AQUA + "" + ChatColor.BOLD + "Unlock Region",
					Utils.chat("&fClick to unlock this region."),
					Utils.chat("&fThe region will no longer be locked and resume previous functions."));
		} else {
			Utils.createItem(inv, Material.IRON_FENCE, 1, 25, ChatColor.AQUA + "" + ChatColor.BOLD + "Lock Region",
					Utils.chat("&fClick to lock this region."),
					Utils.chat("&fLocked regions can not be edited."));
		}
		
		String owner;
		String uuid;
		
		if (ownerData.hasOwner(u.current_region)) {
			uuid = ownerData.getOwner(u.current_region);
			owner = playerData.getName(uuid);
			Player p;
			
			if (owner == null) {
				
				p = Bukkit.getPlayer(UUID.fromString(uuid));
					
					if (p == null) {
						owner = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
					} else {
						owner = p.getName();
					}
				
				playerData.addPlayer(uuid, owner);
			}
		} else {
			owner = "No Owner";
		}

		
		int members = memberData.countMembers(u.current_region);
		
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

		RegionData regionData = Main.getInstance().regionData;
		RequestData requestData = Main.getInstance().requestData;
		
		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.p.closeInventory();
			u.p.openInventory(StaffGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Open Region")) {
			
			regionData.setOpen(u.current_region);
			WorldGuardFunctions.setOpen(u.current_region);
			
			requestData.closeRequests(u.current_region);
			
			for (User us : Main.users) {				
				User.updatePerms(us, us.current_region);
			}
			
			u.p.getOpenInventory().getTopInventory().setContents(StaffOptions.GUI(u).getContents());
			u.p.updateInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now open!");
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Close Region")) {
			
			regionData.setClosed(u.current_region);
			WorldGuardFunctions.setClosed(u.current_region);
			
			for (User us : Main.users) {				
				User.updatePerms(us, us.current_region);
			}
			
			u.p.getOpenInventory().getTopInventory().setContents(StaffOptions.GUI(u).getContents());
			u.p.updateInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now closed!");
			
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Lock Region")) {
			
			regionData.setLocked(u.current_region);
			WorldGuardFunctions.setLocked(u.current_region);
			
			for (User us : Main.users) {				
				User.updatePerms(us, us.current_region);
			}
			
			u.p.getOpenInventory().getTopInventory().setContents(StaffOptions.GUI(u).getContents());
			u.p.updateInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now locked!");
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Unlock Region")) {
			
			regionData.setUnlocked(u.current_region);
			WorldGuardFunctions.setUnlocked(u.current_region);
			
			for (User us : Main.users) {				
				User.updatePerms(us, us.current_region);
			}
			
			u.p.getOpenInventory().getTopInventory().setContents(StaffOptions.GUI(u).getContents());
			u.p.updateInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now unlocked!");
			
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Private Region")) {
			
			regionData.setPrivate(u.current_region);
			
			u.p.getOpenInventory().getTopInventory().setContents(StaffOptions.GUI(u).getContents());
			u.p.updateInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now unlocked!");
			
			
		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Public Region")) {
			
			regionData.setPublic(u.current_region);
			
			u.p.getOpenInventory().getTopInventory().setContents(StaffOptions.GUI(u).getContents());
			u.p.updateInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now unlocked!");
			
			
		} else {

			
		}
	}

}
