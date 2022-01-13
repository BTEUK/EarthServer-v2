package me.elgamer.earthserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class CleanWorldGuard implements CommandExecutor {
	
	OwnerData ownerData;
	MemberData memberData;
	
	public CleanWorldGuard(OwnerData ownerData, MemberData memberData) {
		this.ownerData = ownerData;
		this.memberData = memberData;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		
		if (sender instanceof Player) {			
			sender.sendMessage(ChatColor.RED + "This command can not be run by a player!");
			return true;			
		} 
		
		ListMultimap<String,String> members = ArrayListMultimap.create();
		
		ownerData.getAll(members);
		memberData.getAll(members);
		
		WorldGuardFunctions.add(members);
		
		
		return true;
	}

}
