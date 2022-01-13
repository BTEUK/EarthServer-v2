package me.elgamer.earthserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.sql.DataSource;

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
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import me.elgamer.earthserver.commands.CleanWorldGuard;
import me.elgamer.earthserver.commands.Spawn;
import me.elgamer.earthserver.commands.TPBlock;
import me.elgamer.earthserver.commands.claim.Claim;
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
import me.elgamer.earthserver.listeners.InventoryClicked;
import me.elgamer.earthserver.listeners.JoinEvent;
import me.elgamer.earthserver.listeners.LeaveEvent;
import me.elgamer.earthserver.listeners.MoveEvent;
import me.elgamer.earthserver.listeners.PlayerInteract;
import me.elgamer.earthserver.listeners.TeleportEvent;
import me.elgamer.earthserver.sql.LocationSQL;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.MessageData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.Inactive;
import me.elgamer.earthserver.utils.Permissions;
import me.elgamer.earthserver.utils.User;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

	//MySQL
	public DataSource dataSource;
	public String host, database, username, password;
	
	public RegionData regionData;
	public OwnerData ownerData;
	public MemberData memberData;
	public PlayerData playerData;
	public RequestData requestData;
	public RegionLogs regionLogs;
	public MessageData messageData;
	
	public LocationSQL locationData;
	

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
		try {
			dataSource = mysqlSetup();
			initDb();

			//Claim data
			regionData = new RegionData(dataSource);
			ownerData = new OwnerData(dataSource);
			memberData = new MemberData(dataSource);
			playerData = new PlayerData(dataSource);
			requestData = new RequestData(dataSource);
			regionLogs = new RegionLogs(dataSource);
			messageData = new MessageData(dataSource);

			//Navigation menu
			locationData = new LocationSQL(dataSource);

		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		users = new ArrayList<User>();

		//Points setup
		ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		interval = 10*60;
		console = Bukkit.getServer().getConsoleSender();

		//Spawn
		spawn = new Location(Bukkit.getWorld(config.getString("World_Name")),config.getDouble("Spawn.x"), config.getDouble("Spawn.y"), config.getDouble("Spawn.z"), config.getLong("Spawn.yaw"), config.getLong("Spawn.pitch"));

		//World
		buildWorld = Bukkit.getWorld(config.getString("World_Name"));

		//Listeners
		new InventoryClicked(this);
		new JoinEvent(this, messageData, playerData, requestData);
		new LeaveEvent(this, memberData, ownerData, playerData);
		new PlayerInteract(this);
		new MoveEvent(this, regionData);
		new TeleportEvent(this, regionData);

		//Bungeecord
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		//Commands for claiming
		getCommand("claim").setExecutor(new Claim());

		//Utility command
		getCommand("tpblock").setExecutor(new TPBlock());
		getCommand("spawn").setExecutor(new Spawn());
		getCommand("cleanworldguard").setExecutor(new CleanWorldGuard(ownerData, memberData));

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
		StaffMembers.initialize();
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

							//Use BTEPoints plugin
							//Bukkit.dispatchCommand(console, "addpoints " + u.name + " 1");
							me.elgamer.btepoints.utils.Points.addPoints(u.uuid, 1);
						}


					}

				}

			}
		}, 0L, 20L);

		//1 minute timer.
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {

				Inactive.members();
				Inactive.owners();

				for (User u : users) {
					if (messageData.hasMessage(u.uuid)) {

						ArrayList<String> messages = messageData.getMessages(u.uuid);
						messageData.removeMessages(u.uuid);

						for (String message : messages) {
							u.p.sendMessage(message);
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

			playerData.updatePlayer(u);		

			if (ownerData.isOwner(u.uuid, u.current_region)) {
				ownerData.updateTime(u.uuid, u.current_region);
			} else if (memberData.isMember(u.uuid, u.current_region)) {
				memberData.updateTime(u.uuid, u.current_region);
			}
		}
		
		Bukkit.getConsoleSender().sendMessage("Disabled EarthServer");
		
	}

	//Creates the mysql connection.
	private DataSource mysqlSetup() throws SQLException {

		host = config.getString("MySQL_host");
		port = config.getInt("MySQL_port");
		database = config.getString("MySQL_database");
		username = config.getString("MySQL_username");
		password = config.getString("MySQL_password");

		MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();

		dataSource.setServerName(host);
		dataSource.setPortNumber(port);
		dataSource.setDatabaseName(database + "?&useSSL=false&");
		dataSource.setUser(username);
		dataSource.setPassword(password);

		testDataSource(dataSource);
		return dataSource;

	}

	private void testDataSource(DataSource dataSource) throws SQLException{
		try (Connection connection = dataSource.getConnection()) {
			if (!connection.isValid(1000)) {
				throw new SQLException("Could not establish database connection.");
			}
		}
	}

	private void initDb() throws SQLException, IOException {
		// first lets read our setup file.
		// This file contains statements to create our inital tables.
		// it is located in the resources.
		String setup;
		try (InputStream in = getClassLoader().getResourceAsStream("dbsetup.sql")) {
			// Legacy way
			setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
			throw e;
		}
		// Mariadb can only handle a single query per statement. We need to split at ;.
		String[] queries = setup.split(";");
		// execute each query to the database.
		for (String query : queries) {
			// If you use the legacy way you have to check for empty queries here.
			if (query.trim().isEmpty()) continue;
			try (Connection conn = dataSource.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.execute();
			}
		}
		getLogger().info("§2Database setup complete.");
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