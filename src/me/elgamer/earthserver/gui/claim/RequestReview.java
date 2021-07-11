package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.MessageData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

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
				Utils.chat("&fRegion: " + u.region_name),
				Utils.chat("&fRequested by: " + PlayerData.getName(u.region_requester)));		

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the review menu."));

		Utils.createItem(inv, Material.EYE_OF_ENDER, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Teleport to Region", 
				Utils.chat("&fTeleport to the location in the region"),
				Utils.chat("&fwhere the request was created."));

		Utils.createItemByte(inv, Material.CONCRETE, 5, 1, 11, ChatColor.AQUA + "" + ChatColor.BOLD + "Accept Request",
				Utils.chat("&fAccepts the request to join the region."),
				Utils.chat("&fIf the user is a Jr.Builder then"),
				Utils.chat("&fstaff may need to accept it also."));

		Utils.createItemByte(inv, Material.CONCRETE, 14, 1, 17, ChatColor.AQUA + "" + ChatColor.BOLD + "Deny Request",
				Utils.chat("&fDenies the request to join the region."));

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.gui_page = 1;

			u.p.closeInventory();
			
			if (u.previous_gui.equals("request")) {
				u.p.openInventory(RequestGui.GUI(u));
			} else {
				u.p.openInventory(StaffRequests.GUI(u));
			}

		} else if (clicked.getType().equals(Material.EYE_OF_ENDER)) {

			Location l = RequestData.getRequestLocation(u.region_name, u.region_requester);

			if (l == null) {
				u.p.sendMessage(ChatColor.RED + "An error occured, please try again!");
			} else {
				u.p.teleport(l);
			}

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Accept Request")) {

			if (u.staff_request) {

				if (RequestData.ownerAccept(u.region_name, u.region_requester)) {

					if (OwnerData.hasOwner(u.region_name)) {

						MemberData.addMember(u.region_name, u.region_requester);
						WorldGuardFunctions.addMember(u.region_name, u.region_requester);
						RegionLogs.newLog(u.region_name, u.region_requester, "member");
						RequestData.closeRequest(u.region_name, u.region_requester);

						if (Main.isOnline(u.region_requester)) {

							Main.updatePerms(u.region_requester, u.region_name);

						}

					} else {

						OwnerData.addOwner(u.region_name, u.region_requester);
						WorldGuardFunctions.addMember(u.region_name, u.region_requester);
						RegionLogs.newLog(u.region_name, u.region_requester, "owner");
						RequestData.closeRequest(u.region_name, u.region_requester);

						if (Main.isOnline(u.region_requester)) {

							Main.updatePerms(u.region_requester, u.region_name);

						}

					}

					MessageData.newMessage(u.region_requester, "Your region join request for " + u.region_name + " has been accepted!", "green");
					
					u.gui_page = 1;

					u.p.closeInventory();
					
					if (u.previous_gui.equals("request")) {
						u.p.openInventory(RequestGui.GUI(u));
					} else {
						u.p.openInventory(StaffRequests.GUI(u));
					}
					
					u.p.sendMessage(ChatColor.GREEN + "You have accepted the request, " + PlayerData.getName(u.region_requester) + " is now a member of " + u.region_name + ".") ;

				} else {

					RequestData.setStaffAccept(u.region_name, u.region_requester, true);
					u.p.closeInventory();
					u.p.sendMessage(ChatColor.GREEN + "You have accepted the request, now the owner has to review it.");

				}


			} else {
				if (RequestData.staffAccept(u.region_name, u.region_requester)) {

					MemberData.addMember(u.region_name, u.region_requester);
					WorldGuardFunctions.addMember(u.region_name, u.region_requester);
					RegionLogs.newLog(u.region_name, u.region_requester, "member");
					RequestData.closeRequest(u.region_name, u.region_requester);
					
					if (Main.isOnline(u.region_requester)) {
						
						Main.updatePerms(u.region_requester, u.region_name);
						
					}

					MessageData.newMessage(u.region_requester, "Your region join request for " + u.region_name + " has been accepted!", "green");
					
					u.gui_page = 1;

					u.p.closeInventory();
					
					if (u.previous_gui.equals("request")) {
						u.p.openInventory(RequestGui.GUI(u));
					} else {
						u.p.openInventory(StaffRequests.GUI(u));
					}
					
					u.p.sendMessage(ChatColor.GREEN + "You have accepted the request, " + PlayerData.getName(u.region_requester) + " is now a member of " + u.region_name + ".") ;
					

				} else {

					RequestData.setOwnerAccept(u.region_name, u.region_requester, true);
					u.p.closeInventory();
					u.p.sendMessage(ChatColor.GREEN + "You have accepted the request, now staff has to review it.");

				}
			}

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Deny Request")) {

			if (u.staff_request) {

				RequestData.closeRequest(u.region_name, u.region_requester);
				MessageData.newMessage(u.region_requester, "Your region join request for " + u.region_name + " has been denied by staff.", "red");

			} else {
				RequestData.closeRequest(u.region_name, u.region_requester);
				MessageData.newMessage(u.region_requester, "Your region join request for " + u.region_name + " has been denied by the owner.", "red");
			}

		} else {
		}

	}

}
