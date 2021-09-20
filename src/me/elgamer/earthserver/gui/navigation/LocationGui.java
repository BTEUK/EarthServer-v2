package me.elgamer.earthserver.gui.navigation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.LocationSQL;
import me.elgamer.earthserver.utils.Utils;

public class LocationGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Location Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		LocationSQL locationSQL = Main.getInstance().locationData;
		
		if (locationSQL.CategoryCount("london") + locationSQL.CategoryCount("england") > 21) {

			if (locationSQL.CategoryCount("england") != 0) {
				Utils.createItem(inv, Material.GREEN_GLAZED_TERRACOTTA, 1, 11, ChatColor.AQUA + "" + ChatColor.BOLD + "England", Utils.chat("&fLocations in England excluding London"));
			}

			if (locationSQL.CategoryCount("london") != 0) {
				Utils.createItem(inv, Material.GRAY_GLAZED_TERRACOTTA, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "London", Utils.chat("&fLocations in London"));	
			}

		} else {

			if (locationSQL.CategoryCount("london") != 0 || locationSQL.CategoryCount("england") != 0) {
				Utils.createItem(inv, Material.GREEN_GLAZED_TERRACOTTA, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "England", Utils.chat("&fLocations in England"));
			}

		}

		if (locationSQL.CategoryCount("wales") + locationSQL.CategoryCount("scotland") + locationSQL.CategoryCount("northern-ireland") + locationSQL.CategoryCount("other") > 21) {

			if (locationSQL.CategoryCount("other") != 0) {
				Utils.createItem(inv, Material.ORANGE_GLAZED_TERRACOTTA, 1, 13, ChatColor.AQUA + "" + ChatColor.BOLD + "Other", Utils.chat("&fLocations not in the 4 main countries of the UK"));
			}
			
			if (locationSQL.CategoryCount("scotland") != 0) {
				Utils.createItem(inv, Material.MAGENTA_GLAZED_TERRACOTTA, 1, 15, ChatColor.AQUA + "" + ChatColor.BOLD + "Scotland", Utils.chat("&fLocations in Scotland"));
			}
			
			if (locationSQL.CategoryCount("wales") != 0) {
				Utils.createItem(inv, Material.RED_GLAZED_TERRACOTTA, 1, 17, ChatColor.AQUA + "" + ChatColor.BOLD + "Wales", Utils.chat("&fLocations in Wales"));
			}
			
			if (locationSQL.CategoryCount("northern-ireland") != 0) {
				Utils.createItem(inv, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Northern Ireland", Utils.chat("&fLocations in Northern Ireland"));
			}
			
		} else {

			if (locationSQL.CategoryCount("scotland") != 0 || locationSQL.CategoryCount("wales") != 0 || locationSQL.CategoryCount("northern-ireland") != 0 || locationSQL.CategoryCount("other") != 0) {
				Utils.createItem(inv, Material.ORANGE_GLAZED_TERRACOTTA, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Other", Utils.chat("&fLocations not in England"));
			}

		}

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
				Utils.chat("&fGo back to the navigation menu."));	

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		if (clicked == null) {return;}
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "England")) {

			//Will open the build location gui.
			p.closeInventory();
			p.openInventory(EnglandGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "London")) {
			p.closeInventory();
			p.openInventory(LondonGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Scotland")) {
			p.closeInventory();
			p.openInventory(ScotlandGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Northern Ireland")) {
			p.closeInventory();
			p.openInventory(NorthernIrelandGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Wales")) {
			p.closeInventory();
			p.openInventory(WalesGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Other")) {
			p.closeInventory();
			p.openInventory(OtherGui.GUI(p));


		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server")) {
			p.closeInventory();
			p.openInventory(SwitchServerGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(NavigationGui.GUI(p));}
	}

}
