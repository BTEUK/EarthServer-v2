package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.Permissions;
import me.elgamer.earthserver.utils.User;

public class MoveEvent implements Listener {

	public MoveEvent(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

		Player p = e.getPlayer();
		User u = Main.getUser(p);

		if (!(p.getWorld().equals(u.current_world))) {
			u.current_world = p.getWorld();
			if (!(u.current_world.getName().equals(Main.getInstance().getConfig().getString("World_Name")))) {
				Permissions.removeWorldedit(u.uuid);
				u.current_region = "buildhub";
				return;
			}
		}
		
		if (!(u.current_world.getName().equals(Main.getInstance().getConfig().getString("World_Name")))) {
			return;
		}
		
		if (!(u.current_region.equals(User.getRegion(p)))) {

			if (u.builder_role.equals("builder") || u.builder_role.equals("jrbuilder")) {
				User.updatePerms(u, User.getRegion(p));	
			}
			u.current_region = User.getRegion(p);
			
		}

	}
}
