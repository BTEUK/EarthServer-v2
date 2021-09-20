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
		
		PlayerData playerData = Main.getInstance().playerData;

		Utils.createItem(inv, Material.BOOK, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Request Info",
				Utils.chat("&fRegion: " + u.region_name),
				Utils.chat("&fRequested by: " + playerData.getName(u.region_requester)));		

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

		MemberData memberData = Main.getInstance().memberData;
		MessageData messageData = Main.getInstance().messageData;
		OwnerData ownerData = Main.getInstance().ownerData;
		PlayerData playerData = Main.getInstance().playerData;
		RegionLogs regionLogs = Main.getInstance().regionLogs;
		RequestData requestData = Main.getInstance().requestData;
		
		if (clicked.getType().equals(Material.SPRUCE_DOOR_ITEM)) {

			u.gui_page = 1;

			u.p.closeInventory();
			
			if (u.previous_gui.equals("request")) {
				u.p.openInventory(RequestGui.GUI(u));
			} else {
				u.p.openInventory(StaffRequests.GUI(u));
			}

		} else if (clicked.getType().equals(Material.EYE_OF_ENDER)) {

			Location l = requestData.getRequestLocation(u.region_name, u.region_requester);

			if (l == null) {
				u.p.sendMessage(ChatColor.RED + "An error occured, please try again!");
			} else {
				u.p.teleport(l);
			}

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Accept Request")) {

			if (u.staff_request) {

				if (requestData.ownerAccept(u.region_name, u.region_requester)) {

					if (ownerData.hasOwner(u.region_name)) {

						memberData.addMember(u.region_name, u.region_requester);
						WorldGuardFunctions.addMember(u.region_name, u.region_requester);
						regionLogs.newLog(u.region_name, u.region_requester, "member");
						requestData.closeRequest(u.region_name, u.region_requester);

						if (Main.isOnline(u.region_requester)) {

							Main.updatePerms(u.region_requester, u.region_name);

						}

					} else {

						ownerData.addOwner(u.region_name, u.region_requester);
						WorldGuardFunctions.addMember(u.region_name, u.region_requester);
						regionLogs.newLog(u.region_name, u.region_requester, "owner");
						requestData.closeRequest(u.region_name, u.region_requester);

						if (Main.isOnline(u.region_requester)) {

							Main.updatePerms(u.region_requester, u.region_name);

						}

					}

					messageData.newMessage(u.region_requester, "Your region join request for " + u.region_name + " has been accepted!", "GREEN");
					
					u.gui_page = 1;

					u.p.closeInventory();
					
					if (u.previous_gui.equals("request")) {
						u.p.openInventory(RequestGui.GUI(u));
					} else {
						u.p.openInventory(StaffRequests.GUI(u));
					}
					
					u.p.sendMessage(ChatColor.GREEN + "You have accepted the request, " + playerData.getName(u.region_requester) + " is now a member of " + u.region_name + ".") ;

				} else {

					requestData.setStaffAccept(u.region_name, u.region_requester, true);
					u.p.closeInventory();
					u.p.sendMessage(ChatColor.GREEN + "You have accepted the request, now the owner has to review it.");

				}


			} else {
				if (requestData.staffAccept(u.region_name, u.region_requester)) {

					memberData.addMember(u.region_name, u.region_requester);
					WorldGuardFunctions.addMember(u.region_name, u.region_requester);
					regionLogs.newLog(u.region_name, u.region_requester, "member");
					requestData.closeRequest(u.region_name, u.region_requester);
					
					if (Main.isOnline(u.region_requester)) {
						
						Main.updatePerms(u.region_requester, u.region_name);
						
					}

					messageData.newMessage(u.region_requester, "Your region join request for " + u.region_name + " has been accepted!", "GREEN");
					
					u.gui_page = 1;

					u.p.closeInventory();
					
					if (u.previous_gui.equals("request")) {
						u.p.openInventory(RequestGui.GUI(u));
					} else {
						u.p.openInventory(StaffRequests.GUI(u));
					}
					
					u.p.sendMessage(ChatColor.GREEN + "You have accepted the request, " + playerData.getName(u.region_requester) + " is now a member of " + u.region_name + ".") ;
					

				} else {

					requestData.setOwnerAccept(u.region_name, u.region_requester, true);
					u.p.closeInventory();
					u.p.sendMessage(ChatColor.GREEN + "You have accepted the request, now staff has to review it.");

				}
			}

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Deny Request")) {

			if (u.staff_request) {

				requestData.closeRequest(u.region_name, u.region_requester);
				messageData.newMessage(u.region_requester, "Your region join request for " + u.region_name + " has been denied by staff.", "RED");

			} else {
				requestData.closeRequest(u.region_name, u.region_requester);
				messageData.newMessage(u.region_requester, "Your region join request for " + u.region_name + " has been denied by the owner.", "RED");
			}

		} else {
		}

	}

}
