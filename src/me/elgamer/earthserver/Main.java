package me.elgamer.earthserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

import me.elgamer.earthserver.commands.TPBlock;
import me.elgamer.earthserver.commands.claim.Claim;
import me.elgamer.earthserver.commands.claim.ConvertClaimData;
import me.elgamer.earthserver.commands.navigation.AddLocation;
import me.elgamer.earthserver.commands.navigation.DenyLocation;
import me.elgamer.earthserver.commands.navigation.GotoRequest;
import me.elgamer.earthserver.commands.navigation.OpenGui;
import me.elgamer.earthserver.commands.navigation.RemoveLocation;
import me.elgamer.earthserver.commands.navigation.RequestLocation;
import me.elgamer.earthserver.commands.navigation.Requests;
import me.elgamer.earthserver.gui.claim.ClaimGui;
import me.elgamer.earthserver.gui.claim.EditMember;
import me.elgamer.earthserver.gui.claim.EditRequests;
import me.elgamer.earthserver.gui.claim.MembersGui;
import me.elgamer.earthserver.gui.claim.RegionList;
import me.elgamer.earthserver.gui.claim.RegionOptions;
import me.elgamer.earthserver.gui.claim.RequestGui;
import me.elgamer.earthserver.gui.claim.RequestReview;
import me.elgamer.earthserver.gui.claim.StaffGui;
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
import me.elgamer.earthserver.listeners.InventoryClicked;
import me.elgamer.earthserver.listeners.JoinEvent;
import me.elgamer.earthserver.listeners.LeaveEvent;
import me.elgamer.earthserver.listeners.MoveEvent;
import me.elgamer.earthserver.listeners.PlayerInteract;
import me.elgamer.earthserver.listeners.TeleportEvent;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.MessageData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.SQLTables;
import me.elgamer.earthserver.utils.Permissions;
import me.elgamer.earthserver.utils.User;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

	//MySQL
	private Connection connection;
	public String host, database, username, password, claimData, permissionData, locationData, locationRequestData;

	public String regionData, ownerData, memberData, playerData, requestData, regionLogs, messageData;

	public int port;

	//Other
	public static Permission perms = null;

	static Main instance;
	static FileConfiguration config;

	public static Location spawn;

	public ItemStack slot5;
	public static ItemStack gui;

	public static ArrayList<User> users;

	static Essentials ess;	
	static int interval;
	ConsoleCommandSender console;

	public static World buildWorld;

	@Override
	public void onEnable() {

		//Config Setup
		Main.instance = this;
		Main.config = this.getConfig();

		saveDefaultConfig();

		//MySQL		
		mysqlSetup();

		users = new ArrayList<User>();

		//Points setup
		ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		interval = 10*60;
		console = Bukkit.getServer().getConsoleSender();

		//Spawn
		spawn = new Location(Bukkit.getWorld(config.getString("World_Name")),config.getDouble("Spawn.x"), config.getDouble("Spawn.y"), config.getDouble("Spawn.z"), config.getLong("Spawn.yaw"), config.getLong("Spawn.pitch"));

		//World
		buildWorld = Bukkit.getWorld(config.getString("World_Name"));

		//Creates the mysql table if not existing
		SQLTables.location(this, locationData);
		SQLTables.locationRequest(this, locationRequestData);

		//Region SQL
		SQLTables.region(instance, regionData);
		SQLTables.owner(instance, ownerData);
		SQLTables.member(instance, memberData);
		SQLTables.player(instance, playerData);
		SQLTables.request(instance, requestData);
		SQLTables.logs(instance, regionLogs);
		SQLTables.messages(instance, messageData);

		//Listeners
		new InventoryClicked(this);
		new JoinEvent(this);
		new LeaveEvent(this);
		new PlayerInteract(this);
		new MoveEvent(this);
		new TeleportEvent(this);

		//Bungeecord
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		//Commands for claiming
		getCommand("claim").setExecutor(new Claim());
		getCommand("convertclaims").setExecutor(new ConvertClaimData());

		//Utility command
		getCommand("tpblock").setExecutor(new TPBlock());

		//Commands for the navigation menu
		getCommand("locationrequest").setExecutor(new RequestLocation());
		getCommand("addlocation").setExecutor(new AddLocation());
		getCommand("removelocation").setExecutor(new RemoveLocation());
		getCommand("denyrequest").setExecutor(new DenyLocation());
		getCommand("requests").setExecutor(new Requests());
		getCommand("navigator").setExecutor(new OpenGui());
		getCommand("torequest").setExecutor(new GotoRequest());

		//GUI
		ClaimGui.initialize();
		EditMember.initialize();
		MembersGui.initialize();
		RegionList.initialize();
		RegionOptions.initialize();
		RequestGui.initialize();
		RequestReview.initialize();
		StaffGui.initialize();
		StaffOptions.initialize();
		StaffRequests.initialize();
		EditRequests.initialize();

		//GUI's for the navigation menu
		EnglandGui.initialize();
		LocationGui.initialize();
		LondonGui.initialize();
		NavigationGui.initialize();
		NorthernIrelandGui.initialize();
		OtherGui.initialize();
		ScotlandGui.initialize();
		SwitchServerGui.initialize();
		WalesGui.initialize();

		//Create gui item				
		gui = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = gui.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Navigation Menu");
		gui.setItemMeta(meta);

		/*
		1 second timer.
		If the player is below Jr.Build in role,
		then give them the navigation menu in slot 5 of their hotbar at all times.
		 */
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {

				for (Player p : Bukkit.getOnlinePlayers()) {

					if (!(p.hasPermission("group.jrbuilder"))) {
						slot5 = p.getInventory().getItem(4);

						if (!(slot5 == null)) {
							if (slot5.equals(gui)) {

							} else {
								p.getInventory().setItem(4, gui);
							}
						} else {
							p.getInventory().setItem(4, gui);
						}
					}

				}

				//Increase buildingTime for each second the player is in a buildable claim and is not AFK
				for (User u : users) {

					if (ess.getUser(u.p).isAfk() == false && u.hasWorldEdit) {

						u.buildingTime += 1;

						if (u.buildingTime >= interval) {
							u.buildingTime -= interval;

							Bukkit.dispatchCommand(console, "addpoints " + u.name + " 1");
						}


					}

				}

			}
		}, 0L, 20L);

		//1 minute timer.
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {

				getConnection();

				for (User u : users) {
					if (MessageData.hasMessage(u.uuid)) {

						ResultSet results = MessageData.getMessages(u.uuid);
						MessageData.removeMessages(u.uuid);

						try {
							while (results.next()) {
								u.p.sendMessage(ChatColor.valueOf(results.getString("COLOUR"))  + results.getString("MESSAGE"));
							}
						} catch (IllegalArgumentException | SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				}

			}
		}, 0L, 1200L);
	}

	public void onDisable() {

		for (User u : users) {

			if (u.hasWorldEdit) {
				Permissions.removeWorldedit(u.uuid);
			}

			PlayerData.updatePlayer(u);		

			if (OwnerData.isOwner(u.uuid, u.current_region)) {
				OwnerData.updateTime(u.uuid, u.current_region);
			} else if (MemberData.isMember(u.uuid, u.current_region)) {
				MemberData.updateTime(u.uuid, u.current_region);
			}
		}

		//MySQL
		try {
			if (connection != null && !connection.isClosed()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL disconnected from " + config.getString("MySQL_database"));
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mysqlSetup() {

		//Login info
		host = config.getString("MySQL_host");
		port = config.getInt("MySQL_port");
		username = config.getString("MySQL_username");
		password = config.getString("MySQL_password");

		//Database name
		database = config.getString("MySQL_database");

		//Table names
		//Old claim data
		claimData = config.getString("MySQL_claimData");
		permissionData = config.getString("MySQL_permissionData");

		//New claim data
		regionData = config.getString("region_data");
		ownerData = config.getString("owner_data");
		memberData = config.getString("member_data");
		playerData = config.getString("player_data");
		requestData = config.getString("request_data");
		regionLogs = config.getString("region_logs");
		messageData = config.getString("message_data");


		//Navigation menu
		locationData = config.getString("MySQL_locationData");
		locationRequestData = config.getString("MySQL_locationRequestData");


		try {

			synchronized (this) {
				if (connection != null && !connection.isClosed()) {
					return;
				}

				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" 
						+ this.port + "/" + this.database, this.username, this.password));

				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL connected to " + config.getString("MySQL_database"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public Connection getConnection() {

		try {
			if (connection == null || connection.isClosed()) {
				mysqlSetup();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public static Main getInstance() {
		return instance;
	}

	public static User addUser(Player p) {
		User u = new User(p);
		users.add(u);
		return u;
	}

	public static User getUser(Player p) {
		for (User u : users) {
			if (u.p.equals(p)) {
				return (u);
			}
		}

		return null;
	}

	public static void removeUser(User u) {
		users.remove(u);
	}

	public static boolean isOnline(String uuid) {

		for (User u : users) {
			if (u.uuid.equals(uuid)) {
				return true;
			}
		}

		return false;

	}

	public static void updatePerms(String uuid, String region) {

		for (User u : users) {
			if (u.uuid.equals(uuid)) {
				User.updatePerms(u, region);
			}
		}

	}
}