package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.gui.claim.ClaimGui;
import me.elgamer.earthserver.gui.claim.EditMember;
import me.elgamer.earthserver.gui.claim.EditRequests;
import me.elgamer.earthserver.gui.claim.MembersGui;
import me.elgamer.earthserver.gui.claim.RegionList;
import me.elgamer.earthserver.gui.claim.RegionOptions;
import me.elgamer.earthserver.gui.claim.RequestGui;
import me.elgamer.earthserver.gui.claim.RequestReview;
import me.elgamer.earthserver.gui.claim.StaffGui;
import me.elgamer.earthserver.gui.claim.StaffMembers;
import me.elgamer.earthserver.gui.claim.StaffOptions;
import me.elgamer.earthserver.gui.claim.StaffRequests;
import me.elgamer.earthserver.gui.navigation.EnglandGui;
import me.elgamer.earthserver.gui.navigation.LocationGui;
import me.elgamer.earthserver.gui.navigation.LondonGui;
import me.elgamer.earthserver.gui.navigation.NavigationGui;
import me.elgamer.earthserver.gui.navigation.NorthernIrelandGui;
import me.elgamer.earthserver.gui.navigation.OtherGui;
import me.elgamer.earthserver.gui.navigation.ScotlandGui;
import me.elgamer.earthserver.gui.navigation.SwitchServerGui;
import me.elgamer.earthserver.gui.navigation.WalesGui;
import me.elgamer.earthserver.utils.User;

public class InventoryClicked implements Listener {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public InventoryClicked(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if (e.getCurrentItem() == null) {
			return;
		}
		
		if (e.getCurrentItem().hasItemMeta() == false) {
			return;
		}
		
		String title = e.getView().getTitle();
		User u = Main.getUser((Player) e.getWhoClicked());		
		
		//Claim Menu
		
		if (title.equals(ClaimGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ClaimGui.inventory_name)) {
				ClaimGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(EditMember.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(EditMember.inventory_name)) {
				EditMember.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(MembersGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(MembersGui.inventory_name)) {
				MembersGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(RegionList.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(RegionList.inventory_name)) {
				RegionList.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(RegionOptions.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(RegionOptions.inventory_name)) {
				RegionOptions.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(RequestGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(RequestGui.inventory_name)) {
				RequestGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(RequestReview.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(RequestReview.inventory_name)) {
				RequestReview.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(StaffGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(StaffGui.inventory_name)) {
				StaffGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(StaffOptions.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(StaffOptions.inventory_name)) {
				StaffOptions.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(StaffRequests.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(StaffRequests.inventory_name)) {
				StaffRequests.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(EditRequests.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(EditRequests.inventory_name)) {
				EditRequests.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(StaffMembers.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(StaffMembers.inventory_name)) {
					StaffMembers.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory(), e.getClick());
			}
		}
		
		
		
		//Navigation Menu
		
		else if (title.equals(NavigationGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(NavigationGui.inventory_name)) {
				NavigationGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(SwitchServerGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(SwitchServerGui.inventory_name)) {
				SwitchServerGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(LocationGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(LocationGui.inventory_name)) {
				LocationGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(EnglandGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(EnglandGui.inventory_name)) {
				EnglandGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(LondonGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(LondonGui.inventory_name)) {
				LondonGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(NorthernIrelandGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(NorthernIrelandGui.inventory_name)) {
				NorthernIrelandGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(ScotlandGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ScotlandGui.inventory_name)) {
				ScotlandGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(WalesGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(WalesGui.inventory_name)) {
				WalesGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(OtherGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(OtherGui.inventory_name)) {
				OtherGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} /*else if (title.equals(CustomGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ClaimGui.inventory_name)) {
				ClaimGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}*/
		
	}

}
