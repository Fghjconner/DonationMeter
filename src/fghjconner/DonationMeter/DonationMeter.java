package fghjconner.DonationMeter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class DonationMeter extends JavaPlugin
{
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	private final DMBlockListener BlockListener = new DMBlockListener(this);
	public static PermissionHandler permissionHandler;
	private Logger log = Logger.getLogger("Minecraft");
	public static HashMap<Location, VisualMeter> meterList;
	public static ArrayList<String> vips;
	public static int requiredDonations, currentDonations;
	public static String currency;
	protected static Calendar date;

	//file IO stuff
	static String mainDirectory = "plugins/DonationMeter"; //sets the main directory for easy reference
	static File Meters = new File(mainDirectory + File.separator + "Meters.dat");
	static File Donations = new File(mainDirectory + File.separator + "Donations.dat");
	static Properties prop = new Properties(); //creates a new properties file
	
	public void onDisable()
	{
		saveDonationsFile();
		saveMetersFile();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " disabled!" );
	}

	public void onEnable()
	{
		date = Calendar.getInstance();
		setupPermissions();
		PluginManager pm = this.getServer().getPluginManager();

		pm.registerEvent(Event.Type.SIGN_CHANGE, BlockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, BlockListener, Event.Priority.Normal, this);

		PluginCommand command = getCommand("DonationMeter");

		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("Donations");

		command.setExecutor(new DonationsCommands(this));
		command.setAliases(aliases);

		PluginDescriptionFile pdfFile = this.getDescription();
		log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled!" );


		//loads properties
		new File(mainDirectory).mkdir();
		if(!Meters.exists())
			createMetersFile();
		else
		{
			loadMeters();
		}

		if(!Donations.exists())
		{
			createDonationsFile();
		} else
		{
			loadDonations();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadMeters()
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mainDirectory + File.separator + "Meters.dat"));
			Object result = ois.readObject();
			meterList = (HashMap<Location,VisualMeter>)result;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private void loadDonations()
	{
		try
		{
			FileInputStream in = new FileInputStream(Donations);
			prop.load(in);
			requiredDonations = Integer.parseInt(prop.getProperty("requiredDonations"));
			currentDonations = Integer.parseInt(prop.getProperty("currentDonations"));
			currency = prop.getProperty("currency");
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void createMetersFile()
	{
		try
		{
			Meters.createNewFile();
			FileOutputStream out = new FileOutputStream(Meters);
			prop.store(out, "Do NOT edit this config!");
			out.flush();
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void createDonationsFile()
	{
		try
		{
			Donations.createNewFile();
			FileOutputStream out = new FileOutputStream(Donations);
			prop.put("requiredDonations", "1");
			prop.put("currentDonations", "0");
			prop.put("currency", "Dollars");
			prop.store(out, "Do NOT edit this config!");
			out.flush();
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace(); //explained below.
		}
	}

	private void saveDonationsFile()
	{
		try
		{
			FileOutputStream out = new FileOutputStream(Donations);
			prop.put("requiredDonations", Integer.toString(requiredDonations));
			prop.put("currentDonations", Integer.toString(currentDonations));
			prop.put("currency", currency);
			prop.store(out, "Do NOT edit this config!");
			out.flush();
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void saveMetersFile()
	{
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mainDirectory + File.separator + "Meters.dat"));
			oos.writeObject(meterList);
			oos.flush();
			oos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean isDebugging(final Player player)
	{
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	public void setDebugging(final Player player, final boolean value)
	{
		debugees.put(player, value);
	}

	private void setupPermissions()
	{
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

		if (DonationMeter.permissionHandler == null) {
			if (permissionsPlugin != null) {
				DonationMeter.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
			} else {
				log.info("Permission system not detected, defaulting to OP");
			}
		}
	}

	//updates visual meters
	public static void updateMeters()
	{
		for (VisualMeter meter: meterList.values())
		{
			meter.update();
		}
	}
}
