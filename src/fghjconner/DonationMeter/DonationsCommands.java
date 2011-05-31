package fghjconner.DonationMeter;


import java.util.Calendar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonationsCommands implements CommandExecutor
{
	private DonationMeter plugin;
	public DonationsCommands(DonationMeter plugin)
	{
		this.plugin=plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		boolean advancedPermissions = DonationMeter.permissionHandler.has((Player)sender, "DonationMeter.admin");
		//boolean canNotify = DonationMeter.permissionHandler.has((Player)sender, "DonationMeter.notify");

		switch(args.length)
		{
		case 0: return info(sender, command);
		case 1: return oneArg(sender, command, args[0], advancedPermissions);
		case 2: return twoArgs(sender, command, args, advancedPermissions);
		case 3: return threeArgs(sender, command, args, advancedPermissions);
		}
		return false;
	}

	//basic no arg response
	public boolean info(CommandSender sender, Command command)
	{
		if (DonationMeter.currentDonations<DonationMeter.requiredDonations)
			sender.sendMessage("Donation Status: "+DonationMeter.currentDonations+" out of "+DonationMeter.requiredDonations+" "+DonationMeter.currency+String.format(" donated. (%3.1f%%)", (double)DonationMeter.currentDonations/(double)DonationMeter.requiredDonations));
		else
			sender.sendMessage("Donation Status: Complete! "+(DonationMeter.currentDonations-DonationMeter.requiredDonations)+DonationMeter.currency+" Surplus!");
		printTimeTill(sender);
		return true;
	}

	//one arg responses
	public  boolean oneArg(CommandSender sender, Command command, String arg, boolean advancedPermissions)
	{
		arg = arg.toLowerCase();
		if (arg.equals("vips") || arg.equals("vip"))
		{
			String out="Current "+DonationMeter.vipName+"s ";
			for (String player:DonationMeter.vips)
				out+=player+", ";
			sender.sendMessage(out.substring(0,out.length()-3));
			return true;
		}
		if (arg.equals("help") || arg.equals("?"))
		{
			sender.sendMessage("----------Available Commands----------");
			sender.sendMessage("------------------------------------");
			sender.sendMessage("/DonationMeter                             *Displays donation info");
			sender.sendMessage("/Donations                                  *Alias for /DonationMeter");
			sender.sendMessage("/DonationMeter VIPs                      *Lists "+DonationMeter.vipName+"s");
			if (advancedPermissions)
			{
				sender.sendMessage("/DonationMeter addVIP <player>       *Adds a "+DonationMeter.vipName);
				sender.sendMessage("/DonationMeter removeVIP <player>   *Removes a "+DonationMeter.vipName);
				sender.sendMessage("/DonationMeter addDonation <amount> *Adds donation to total");
				sender.sendMessage("/DonationMeter setGoal <amount>       *Sets the monthly goal");
				sender.sendMessage("/DonationMeter setCurrency <name>   *Sets currency used");
				sender.sendMessage("/DonationMeter meterHelp                *Displays meter help");
				sender.sendMessage("/DonationMeter pay                        *Pays goal");
				sender.sendMessage("/DonationMeter save                       *Saves data");
			}
			sender.sendMessage("Note:"+DonationMeter.vipName+" may be substituted for VIP");
			return true;
		}
		
		if (arg.equals("meterhelp") && advancedPermissions)
		{
			sender.sendMessage("-----------Creating Meters-----------");
			sender.sendMessage("------------------------------------");
			sender.sendMessage("Meters are built of adjacent wool blocks");
			sender.sendMessage("Placing a sign starting \"Donations\" onto wool forms a meter");
			sender.sendMessage("Meters visually display your donation status \"filling\"");
			sender.sendMessage("The meter starts the \"empty\" color");
			sender.sendMessage("The meter then fills with the \"has\" color");
			sender.sendMessage("Once full the meter fills again with the \"extra\" color");
			sender.sendMessage("------------------------------------");
			sender.sendMessage("------------Modifications------------");
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
				sender.sendMessage("You're a bit short! Current total set to zero :'(");
			}
			else if (DonationMeter.currentDonations == 0)
			{
				sender.sendMessage("You have exactly enough money! (You didn't do that on purpose did you?)");
			}
			else
			{
				sender.sendMessage("You have more than enough money! Theres "+DonationMeter.currentDonations+" "+DonationMeter.currency+" left!");
			}
			plugin.updateMeters();
			return true;
		}
		if (arg.equals("save") && advancedPermissions)
		{
			plugin.saveAll();
			sender.sendMessage("Data Saved!");
			return true;
		}
		return false;
	}

	//two arg responses
	public  boolean twoArgs(CommandSender sender, Command command, String[] args, boolean advancedPermissions)
	{
		args[0]=args[0].toLowerCase();
		if ((args[0].equals("vips") || args[0].equals("vip") || args[0].equals("addvip") || args[0].equals(DonationMeter.vipName)) && advancedPermissions)
		{
			if (!DonationMeter.vips.contains(args[1]))
			{
				DonationMeter.vips.add(args[1]);
				sender.sendMessage(args[1]+" added to "+DonationMeter.vipName+" list.");
			}
			else
			{
				sender.sendMessage(args[1]+" is already on the "+DonationMeter.vipName+" list!");
			}
			return true;
		}
		if ((args[0].equals("removevip") || args[0].equals("remvip") || args[0].equals("viprem") || args[0].equals("remove"+DonationMeter.vipName) || args[0].equals("remvip"+DonationMeter.vipName) || args[0].equals("viprem"+DonationMeter.vipName)) && advancedPermissions)
		{
			if (DonationMeter.vips.remove(args[1]))
			{
				sender.sendMessage(args[1]+" removed from "+DonationMeter.vipName+" list.");
				return true;
			}
			sender.sendMessage(DonationMeter.vipName+" not found. Check your spelling and capitalization!");
			return false;
		}
		if ((args[0].equals("adddonation") || args[0].equals("donation") || args[0].equals("donate") || args[0].equals("add")) && advancedPermissions)
		{
			try {
				DonationMeter.currentDonations+=Integer.parseInt(args[1]);
				if (Integer.parseInt(args[1])<0)
					sender.sendMessage("Negative donations? That sucks... "+DonationMeter.currentDonations+" "+DonationMeter.currency+" remaining.");
				else
					sender.sendMessage("Donation added. You are at "+DonationMeter.currentDonations+" "+DonationMeter.currency+".");
				plugin.updateMeters();
				return true;
			} catch (NumberFormatException e)
			{
				sender.sendMessage("Invalid number");
				return false;
			}
		}
		if ((args[0].equals("setgoal") || args[0].equals("goal") || args[0].equals("setbill") || args[0].equals("bill")) && advancedPermissions)
		{
			int amount;
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e)
			{
				sender.sendMessage("Invalid number");
				return false;
			}
			if (amount>=0)
			{
				DonationMeter.requiredDonations=amount;
				sender.sendMessage("Goal set to "+amount);
				plugin.updateMeters();
				return true;
			}
			else
			{
				sender.sendMessage("Negative goals indicate self esteem issues.");
				sender.sendMessage("Goal not set");
				return false;
			}
		}
		if ((args[0].equals("currency") || args[0].equals("setcurrency")) && advancedPermissions)
		{
			DonationMeter.currency=args[1];
			sender.sendMessage("Now tracking donations in "+args[1]+"!");
			return true;
		}
		if ((args[0].equals("setvipname") || args[0].equals("vipname")) && advancedPermissions)
		{
			sender.sendMessage(DonationMeter.vipName+"s are now called "+args[1]+"s!");
			DonationMeter.vipName=args[1];
			return true;
		}
		return false;
	}

	//tree arg responses
	public  boolean threeArgs(CommandSender sender, Command command, String[] args, boolean advancedPermissions)
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
		sender.sendMessage(daysTill+" Days, "+hoursTill+" Hours, "+minutesTill+" Minutes, and "+secondsTill+" Seconds till server bill.");
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