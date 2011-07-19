package fghjconner.DonationMeter;

import java.util.Collection;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignMeter implements Meter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4230504763249614293L;
	private SimpleLoc loc;
	private String[] base;
	
	public SignMeter (SimpleLoc location, String[] lines)
	{
		loc = location;
		base = lines;
		System.out.println("base[0]: "+base[0]);
	}

	@Override
	public boolean update()
	{
		Sign sign;
		try
		{
			sign=(Sign) loc.getBlock().getState();
		}
		catch (ClassCastException e)
		{
			destroy();
			return false;
		}
		String[] newLines = new String[4];
		System.arraycopy(base, 0, newLines, 0, 4);
		for (int i = 0; i<4; i++)
		{
			newLines[i] = newLines[i].replaceAll("\\[have\\]", Short.toString(DonationMeter.currentDonations));
			newLines[i] = newLines[i].replaceAll("\\[goal\\]", Short.toString(DonationMeter.requiredDonations));
			newLines[i] = newLines[i].replaceAll("\\[perc\\]", moneyPercent());
			newLines[i] = newLines[i].replaceAll("\\[need\\]", moneyNeeded());
			newLines[i] = newLines[i].replaceAll("\\[extr\\]", moneyExtra());
		}
		for (int i=0;i<4;i++)
		{
			sign.setLine(i, newLines[i]);
		}
		sign.update();
		return true;
	}

	@Override
	public void destroy()
	{
		DonationMeter.plugin.meterList.remove(this);
	}

	@Override
	public boolean has(SimpleLoc location)
	{
		return location.equals(loc);
	}

	@Override
	public boolean has(Collection<Block> blockList)
	{
		return false;
	}

	private String moneyExtra()
	{
		int goal = DonationMeter.requiredDonations;
		int have = DonationMeter.currentDonations;
		if (goal > have)
			return "0";
		return Integer.toString(have - goal);
	}

	private String moneyNeeded()
	{
		int goal = DonationMeter.requiredDonations;
		int have = DonationMeter.currentDonations;
		if (have > goal)
			return "0";
		return Integer.toString(goal - have);
	}
	
	private String moneyPercent()
	{
		double goal = DonationMeter.requiredDonations;
		double have = DonationMeter.currentDonations;
		return String.format("%.0f", (have/goal)*100);
	}
}
