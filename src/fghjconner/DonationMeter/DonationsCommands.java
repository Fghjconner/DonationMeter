package fghjconner.DonationMeter;


import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonationsCommands implements CommandExecutor
{
	private DonationMeter plugin;
	private boolean advancedPermissions, canNotify;
	public DonationsCommands(DonationMeter plugin)
	{
		this.plugin=plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		advancedPermissions = DonationMeter.permissionHandler.has((Player)sender, "DonationMeter.admin");
		canNotify = DonationMeter.permissionHandler.has((Player)sender, "DonationMeter.notify");

		switch(args.length)
		{
		case 0: return info(sender, command);
		case 1: return oneArg(sender, command, args[0]);
		case 2: return twoArgs(sender, command, args);
		case 3: return threeArgs(sender, command, args);
		}
		return false;
	}

	//basic no arg response
	public boolean info(CommandSender sender, Command command)
	{
		if (DonationMeter.currentDonations<DonationMeter.requiredDonations)
			sender.sendMessage(ChatColor.BLUE.toString()+"Donation Status: "+DonationMeter.currentDonations+" out of "+DonationMeter.requiredDonations+" "+DonationMeter.currency+String.format(" donated. (%3.1f%%)", (double)DonationMeter.currentDonations/(double)DonationMeter.requiredDonations*100d));
		else
			sender.sendMessage(ChatColor.GREEN.toString()+"Donation Status: Complete! All "+DonationMeter.requiredDonations+" "+DonationMeter.currency+" with "+(DonationMeter.currentDonations-DonationMeter.requiredDonations)+" "+DonationMeter.currency+" of Surplus!");
		printTimeTill(sender);
		return true;
	}

	//one arg responses
	public  boolean oneArg(CommandSender sender, Command command, String arg)
	{
		arg = arg.toLowerCase();
		if (arg.equals("vips") || arg.equals("vip"))
		{
			String out=ChatColor.BLUE.toString()+"Current "+DonationMeter.vipName+"s "+ChatColor.GREEN.toString();
			for (String player:plugin.vips)
				out+=player+", ";
			sender.sendMessage(out.substring(0,out.length()-2));
			return true;
		}
		if (arg.equals("help") || arg.equals("?"))
		{
			sender.sendMessage(ChatColor.BLUE.toString()+"----------Available Commands----------");
			sender.sendMessage(ChatColor.BLUE.toString()+"------------------------------------");
			sender.sendMessage(ChatColor.AQUA.toString()+"/DonationMeter   "+ChatColor.DARK_GREEN.toString()+"*Displays donation info");
			sender.sendMessage(ChatColor.AQUA.toString()+"/Donations   "+ChatColor.DARK_GREEN.toString()+"*Alias for /DonationMeter");
			sender.sendMessage(ChatColor.AQUA.toString()+"/DonationMeter VIPs   "+ChatColor.DARK_GREEN.toString()+"*Lists "+DonationMeter.vipName+"s");
			sender.sendMessage(ChatColor.AQUA.toString()+"/DonationMeter notify <amount>   "+ChatColor.DARK_GREEN.toString()+"*Notifys admin of donation");
			if (advancedPermissions)
			{
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter addVIP <player>   "+ChatColor.DARK_GREEN.toString()+"*Adds a "+DonationMeter.vipName);
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter removeVIP <player>   "+ChatColor.DARK_GREEN.toString()+"*Removes a "+DonationMeter.vipName);
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter addDonation <amount>   "+ChatColor.DARK_GREEN.toString()+"*Adds donation to total");
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter setGoal <amount>   "+ChatColor.DARK_GREEN.toString()+"*Sets the monthly goal");
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter setCurrency <name>   "+ChatColor.DARK_GREEN.toString()+"*Sets currency used");
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter meterHelp   "+ChatColor.DARK_GREEN.toString()+"*Displays meter help");
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter pay   "+ChatColor.DARK_GREEN.toString()+"*Pays goal");
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter save   "+ChatColor.DARK_GREEN.toString()+"*Saves data");
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter time [on/off]   "+ChatColor.DARK_GREEN.toString()+"*Toggles time display");
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter notifications   "+ChatColor.DARK_GREEN.toString()+"*Displays notifications");
				sender.sendMessage(ChatColor.DARK_AQUA.toString()+"/DonationMeter accept <player>  "+ChatColor.DARK_GREEN.toString()+"*Accepts donation notification");
			}
			if (!DonationMeter.vipName.equals("VIP"))
				sender.sendMessage(ChatColor.GRAY.toString()+"Note:"+DonationMeter.vipName+" may be substituted for VIP");
			return true;
		}
		
		if (arg.equals("meterhelp") && advancedPermissions)
		{
			sender.sendMessage(ChatColor.BLUE.toString()+"-----------Creating Meters-----------");
			sender.sendMessage(ChatColor.BLUE.toString()+"------------------------------------");
			sender.sendMessage("Meters are built of adjacent wool blocks");
			sender.sendMessage("Placing a sign starting \"Donations\" onto wool forms a meter");
			sender.sendMessage("Meters visually display your donation status \"filling\"");
			sender.sendMessage("The meter starts the \"empty\" color");
			sender.sendMessage("The meter then fills with the \"has\" color");
			sender.sendMessage("Once full the meter fills again with the \"extra\" color");
			sender.sendMessage(ChatColor.BLUE.toString()+"------------------------------------");
			sender.sendMessage(ChatColor.BLUE.toString()+"------------Modifications------------");
			sender.sendMessage("Adding -r to the first line reverses the fill direction");
			sender.sendMessage("Adding -x, -y, or -z controlls the fill axis");
			sender.sendMessage("Placing \"need <color>\" on an empty line sets the empty color");
			sender.sendMessage("Placing \"has <color>\" on an empty line sets the filled color");
			sender.sendMessage("Placing \"extra <color>\" on an empty line sets the extra color");
			return true;
		}
		
		if (arg.equals("pay") && advancedPermissions)
		{
			DonationMeter.currentDonations -= DonationMeter.requiredDonations;
			if (DonationMeter.currentDonations<0)
			{
				DonationMeter.currentDonations=0;
				sender.sendMessage(ChatColor.RED.toString()+"You're a bit short! Current total set to zero :'(");
			}
			else if (DonationMeter.currentDonations == 0)
			{
				sender.sendMessage(ChatColor.BLUE.toString()+"You have exactly enough money! (You didn't do that on purpose did you?)");
			}
			else
			{
				sender.sendMessage(ChatColor.GREEN.toString()+"You have more than enough money! Theres "+DonationMeter.currentDonations+" "+DonationMeter.currency+" left!");
			}
			plugin.updateMeters();
			return true;
		}
		if (arg.equals("save") && advancedPermissions)
		{
			plugin.saveAll();
			sender.sendMessage(ChatColor.BLUE.toString()+"Data Saved!");
			return true;
		}
		if ((arg.equals("showtime") || arg.equals("time")) && advancedPermissions)
		{
			String active = plugin.showTime ? ChatColor.RED.toString()+"Off" : ChatColor.GREEN.toString()+"On";
			sender.sendMessage("Time till bill toggled. ("+active+")");
			plugin.showTime = !plugin.showTime;
			return true;
		}
		if ((arg.equals("notifications") || arg.equals("notification") || arg.equals("notify")) && advancedPermissions)
		{
			if (plugin.notificationList.size()==0)
			{
				sender.sendMessage(ChatColor.BLUE.toString()+"No notifications!");
				return true;
			}
			sender.sendMessage(ChatColor.GREEN.toString()+"-----Donation Notifications-----");
			sender.sendMessage(ChatColor.GREEN.toString()+"----------------------------");
			for (String player: plugin.notificationList.keySet())
			{
				sender.sendMessage(player+" claims a donation of "+plugin.notificationList.get(player)+" "+DonationMeter.currency);
			}
			return true;
		}
		return false;
	}

	//two arg responses
	public  boolean twoArgs(CommandSender sender, Command command, String[] args)
	{
		args[0]=args[0].toLowerCase();
		if ((args[0].equals("vips") || args[0].equals("vip") || args[0].equals("addvip") || args[0].equals(DonationMeter.vipName)) && advancedPermissions)
		{
			if (!plugin.vips.contains(args[1]))
			{
				plugin.vips.add(args[1]);
				sender.sendMessage(ChatColor.GREEN.toString()+args[1]+" added to "+DonationMeter.vipName+" list.");
			}
			else
			{
				sender.sendMessage(ChatColor.BLUE.toString()+args[1]+" is already on the "+DonationMeter.vipName+" list!");
			}
			return true;
		}
		if ((args[0].equals("removevip") || args[0].equals("remvip") || args[0].equals("viprem") || args[0].equals("remove"+DonationMeter.vipName) || args[0].equals("remvip"+DonationMeter.vipName) || args[0].equals("viprem"+DonationMeter.vipName)) && advancedPermissions)
		{
			if (plugin.vips.remove(args[1]))
			{
				sender.sendMessage(ChatColor.BLUE.toString()+args[1]+" removed from "+DonationMeter.vipName+" list.");
				return true;
			}
			sender.sendMessage(ChatColor.RED.toString()+DonationMeter.vipName+" not found. Check your spelling and capitalization!");
			return false;
		}
		if ((args[0].equals("adddonation") || args[0].equals("donation") || args[0].equals("donate") || args[0].equals("add")) && advancedPermissions)
		{
			try {
				Short donation = Short.parseShort(args[1]);
				DonationMeter.currentDonations+=donation;
				if (donation<0)
					sender.sendMessage(ChatColor.BLUE.toString()+"Negative donations? That sucks... "+DonationMeter.currentDonations+" "+DonationMeter.currency+" remaining.");
				else
					sender.sendMessage(ChatColor.GREEN.toString()+"Donation added. You are at "+DonationMeter.currentDonations+" "+DonationMeter.currency+".");
				plugin.updateMeters();
				return true;
			} catch (NumberFormatException e)
			{
				sender.sendMessage(ChatColor.RED.toString()+"Invalid number");
				return false;
			}
		}
		if ((args[0].equals("setgoal") || args[0].equals("goal") || args[0].equals("setbill") || args[0].equals("bill")) && advancedPermissions)
		{
			short amount;
			try {
				amount = Short.parseShort(args[1]);
			} catch (NumberFormatException e)
			{
				sender.sendMessage(ChatColor.RED.toString()+"Invalid number");
				return false;
			}
			if (amount>=0)
			{
				DonationMeter.requiredDonations=amount;
				sender.sendMessage(ChatColor.BLUE.toString()+"Goal set to "+amount);
				plugin.updateMeters();
				return true;
			}
			else
			{
				sender.sendMessage(ChatColor.RED.toString()+"Negative goals indicate self esteem issues.");
				sender.sendMessage(ChatColor.RED.toString()+"Goal not set");
				return false;
			}
		}
		if ((args[0].equals("currency") || args[0].equals("setcurrency")) && advancedPermissions)
		{
			DonationMeter.currency=args[1];
			sender.sendMessage(ChatColor.BLUE.toString()+"Now tracking donations in "+args[1]+"!");
			return true;
		}
		if ((args[0].equals("setvipname") || args[0].equals("vipname")) && advancedPermissions)
		{
			sender.sendMessage(ChatColor.BLUE.toString()+DonationMeter.vipName+"s are now called "+args[1]+"s!");
			DonationMeter.vipName=args[1];
			return true;
		}
		if ((args[0].equals("showtime") || args[0].equals("time")) && advancedPermissions)
		{
			args[1] = args[1].toLowerCase();
			if (args[1].equals("on") || args[1].equals("true") || args[1].equals("show"))
			{
				sender.sendMessage("Time till bill is now "+ChatColor.GREEN.toString()+"On.");
				plugin.showTime = true;
				return true;
			}
			if (args[1].equals("off") || args[1].equals("false") || args[1].equals("hide"))
			{
				sender.sendMessage("Time till bill is now "+ChatColor.RED.toString()+"Off.");
				plugin.showTime = false;
				return true;
			}
		}
		if ((args[0].equals("accept") || args[0].equals("acceptnotification")) && advancedPermissions)
		{
			if (plugin.notificationList.containsKey(args[1]))
			{
				DonationMeter.currentDonations+=plugin.notificationList.get(args[1]);
				if (!plugin.vips.contains(args[1]))
					plugin.vips.add(args[1]);
				sender.sendMessage(ChatColor.GREEN.toString()+"Donation accepted! "+plugin.notificationList.get(args[1])+" "+DonationMeter.currency+" added to total, "+args[1]+" is now a "+DonationMeter.vipName+"!");
				plugin.notificationList.remove(args[1]);
				plugin.updateMeters();
				return true;
			}
			sender.sendMessage("No notifications from that player!");
		}
		if (args[0].equals("notify") && canNotify)
		{
			try
			{
				plugin.addNotification(Short.parseShort(args[1]), ((Player)sender).getName());
				sender.sendMessage(ChatColor.GREEN.toString()+"Notification of donation sent!");
				return true;
			}
			catch (Exception e)
			{
				sender.sendMessage(ChatColor.RED.toString()+"Invalid number");
			}
		}
		return false;
	}

	//three arg responses
	public  boolean threeArgs(CommandSender sender, Command command, String[] args)
	{
		return false;
	}

	public void printTimeTill(CommandSender sender)
	{
		if (!plugin.showTime)
			return;
		Calendar date = Calendar.getInstance();
		int daysTill = getMonthLength(date)-date.get(Calendar.DAY_OF_MONTH);
		int hoursTill = 24-date.get(Calendar.HOUR_OF_DAY);
		int minutesTill = 60-date.get(Calendar.MINUTE);
		int secondsTill = 60-date.get(Calendar.SECOND);
		if (secondsTill==0)
			minutesTill++;
		if (minutesTill==0)
			hoursTill++;
		if (hoursTill==0)
			daysTill++;
		sender.sendMessage(ChatColor.YELLOW.toString()+daysTill+" Days, "+hoursTill+" Hours, "+minutesTill+" Minutes, and "+secondsTill+" Seconds till server bill.");
	}

	public int getMonthLength(Calendar date)
	{
		switch (date.get(Calendar.MONTH))
		{
		case 0:;
		case 2:;
		case 4:;
		case 6:;
		case 7:;
		case 9:;
		case 11: return 31;
		case 1: return 28;
		case 3:;
		case 5:;
		case 8:;
		case 10: return 30;
		}
		return 0;
	}
}