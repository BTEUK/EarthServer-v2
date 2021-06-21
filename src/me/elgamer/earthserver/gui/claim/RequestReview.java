package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;

public class RequestReview {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;
	
	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Review Request";
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (User u) {
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
			
		Utils.createItem(inv, Material.BOOK, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Request Info",
				Utils.chat("&fRegion: " + u.request_region),
				Utils.chat("&fRequested by: " + u.request_name));		
		
		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the review menu."));
		
		Utils.createItem(inv, Material.EYE_OF_ENDER, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Teleport to Region", 
				Utils.chat("&fTeleport to the location in the region"),
				Utils.chat("&fwhere the request was created."));
		
		Utils.createItemByte(inv, Material.CONCRETE, 1, 5, 11, ChatColor.AQUA + "" + ChatColor.BOLD + "Accept Request",
				Utils.chat("&fAccepts the request to join the region."),
				Utils.chat("&fIf the user is a Jr.Builder then"),
				Utils.chat("&fstaff may need to accept it also."));
		
		Utils.createItemByte(inv, Material.CONCRETE, 1, 14, 11, ChatColor.AQUA + "" + ChatColor.BOLD + "Deny Request",
				Utils.chat("&fDenies the request to join the region."));
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {
		
		if (clicked.getType().equals(Material.SPRUCE_DOOR)) {

			u.request_page = 1;
			
			u.p.closeInventory();
			u.p.openInventory(RequestGui.GUI(u));

		} else if (clicked.getType().equals(Material.BOOK_AND_QUILL)) {
			u.p.closeInventory();
			
		} else if (clicked.getType().equals(Material.CHEST)) {
		} else {
			
			String[] info = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).replace(" ","").split(",");
			u.request_name = info[0];
			u.request_region = info[1];
			
			u.p.closeInventory();
			u.p.openInventory(RequestReview.GUI(u));
			
		}
		
	}

}
