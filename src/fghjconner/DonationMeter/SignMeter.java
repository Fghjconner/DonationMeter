package fghjconner.DonationMeter;

import java.io.Serializable;
import java.util.Collection;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class SignMeter implements Meter,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7034297413333927291L;
	private SimpleLoc loc;
	private int line, index, type, valueLength;
	public static final int MONEY_HAVE=0,MONEY_NEED=1,MONEY_EXTRA=2,GOAL=3,PERCENT=4;
	
	public SignMeter (SimpleLoc location, int lineNum, int lineIndex, int typeNum, SignChangeEvent event)
	{
		loc = location;
		line = lineNum;
		index = lineIndex;
		type = typeNum;
		valueLength = 6;
	}

	@Override
	public void update()
	{
		Sign sign = (Sign) (loc.getBlock().getState());
		try
		{
			sign.setLine(line, sign.getLine(line).substring(0,index) + value()+ sign.getLine(line).substring(index+valueLength));
		}
		catch (StringIndexOutOfBoundsException e)
		{
			sign.setLine(line, sign.getLine(line).substring(0,index) + value());
		}
		valueLength = value().length();
		sign.update();
	}

	@Override
	public void destroy()
	{
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
	
	public String value()
	{
		switch (type)
		{
		case MONEY_HAVE: return Integer.toString(DonationMeter.currentDonations);
		case MONEY_NEED: return moneyNeeded();
		case MONEY_EXTRA: return moneyExtra();
		case PERCENT: return String.format("%.2f",(double)DonationMeter.currentDonations/(double)DonationMeter.requiredDonations*100)+"%";
		default: return "error";
		}
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
}
