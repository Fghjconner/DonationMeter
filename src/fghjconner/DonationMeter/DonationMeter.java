package fghjconner.DonationMeter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class DonationMeter extends JavaPlugin
{
	private final DMBlockListener BlockListener = new DMBlockListener(this);
	private final DMEntityListener EntityListener = new DMEntityListener(this);
	private final DMPlayerListener PlayerListener = new DMPlayerListener(this);
	private Logger log = Logger.getLogger("Minecraft");
	public ArrayList<Meter> meterList;
	public HashMap<String, Short> notificationMap;
	public ArrayList<String> vips;
	public static DonationMeter plugin;
	public static short requiredDonations, currentDonations;
	public static String currency, vipName;
	public boolean showTime, explosionVulnerable, opPermissions;
	public static Server server;

	public void onDisable()
	{
		saveAll();
		plugin = null;
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " disabled!");
	}

	public void onEnable()
	{
		plugin = this;
		server = getServer();
		PluginManager pm = server.getPluginManager();

		// registers main events
		pm.registerEvent(Event.Type.SIGN_CHANGE, BlockListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, BlockListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_BURN, BlockListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, PlayerListener, Event.Priority.Monitor, this);

		// registers command
		PluginCommand command = getCommand("DonationMeter");

		command.setExecutor(new DonationsCommands(this));

		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled!");

		loadSettings();
		loadMeters();
		loadNotifications();

		// registers entity explode if explosion vulnerable is true
		if (explosionVulnerable)
			pm.registerEvent(Event.Type.ENTITY_EXPLODE, EntityListener, Event.Priority.Normal, this);
	}

	private void loadMeters()
	{
		System.out.println("Loading Meters...");
		Configuration config = getConfiguration();
		meterList = new ArrayList<Meter>();
		for (ConfigurationNode node : config.getNodes("Meters").values())
		{
			System.out.println(node.getString("Type"));
			if (node.getString("Type").equals("WoolMeter"))
				meterList.add(new WoolMeter(node));
			else if (node.getString("Type").equals("SignMeter"))
				meterList.add(new SignMeter(node));
		}
	}

	private void loadNotifications()
	{
		Configuration config = getConfiguration();
		notificationMap = new HashMap<String, Short>();
		ConfigurationNode node = config.getNode("Notifications");
		if (node != null)
			for (String key : node.getKeys())
				notificationMap.put(key, (short) config.getInt("Notifications." + key, 0));
	}

	private void loadSettings()
	{
		Configuration config = getConfiguration();
		opPermissions = config.getBoolean("OpPermissions", false);
		currency = config.getString("Currency", "dollars");
		explosionVulnerable = config.getBoolean("ExplosionVulnerable", false);
		currentDonations = (short) config.getInt("CurrentDonations", 0);
		vipName = config.getString("VIPname", "VIP");
		showTime = config.getBoolean("DisplayTime", true);
		requiredDonations = (short) config.getInt("DonationGoal", 100);
		loadVIPs(config.getString("VIPs", ""));
	}

	private void loadVIPs(String vipString)
	{
		vips = new ArrayList<String>();
		Scanner vipList = new Scanner(vipString);
		vipList.useDelimiter("[\\[\\],]");
		while (vipList.hasNext())
		{
			vips.add(vipList.next());
		}
	}

	private void saveSettings()
	{
		Configuration config = getConfiguration();
		config.setProperty("OpPermissions", opPermissions);
		config.setProperty("Currency", currency);
		config.setProperty("ExplosionVulnerable", explosionVulnerable);
		config.setProperty("CurrentDonations", currentDonations);
		config.setProperty("VIPname", vipName);
		config.setProperty("DisplayTime", showTime);
		config.setProperty("DonationGoal", requiredDonations);
		config.setProperty("VIPs", vips.toString());
	}

	private void saveMeters()
	{
		Configuration config = getConfiguration();
		for (int i = 0; i < meterList.size(); i++)
			config.setProperty("Meters." + i, meterList.get(i).toConfig());
	}

	private void saveNotificationsFile()
	{
		Configuration config = getConfiguration();
		for (String player : notificationMap.keySet())
			config.setProperty(player, notificationMap.get(player));
	}

	// updates meters
	public void updateMeters()
	{
		for (int i = 0; i < meterList.size(); i++)
		{
			if (!meterList.get(i).update())
				i--;
		}
		saveMeters();
	}

	public void saveAll()
	{
		saveSettings();
		saveMeters();
		saveNotificationsFile();
		getConfiguration().save();
	}

	public void addNotification(short amount, String player)
	{
		if (notificationMap.containsKey(player))
			notificationMap.put(player, (short) (notificationMap.get(player) + amount));
		else
			notificationMap.put(player, amount);
	}

	public boolean hasPermission(Player player, String permission)
	{
		if (opPermissions)
			return player.isOp();
		if (player.hasPermission(permission))
			return true;
		String[] parts = permission.split(".");
		String subPerm = "";
		for (int i = 0; i < parts.length - 1; i++)
		{
			subPerm += parts[i] + ".";
			if (player.hasPermission(subPerm + "*"))
				return true;
		}
		return false;
	}
}
