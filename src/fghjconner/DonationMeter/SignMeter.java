package fghjconner.DonationMeter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.util.config.ConfigurationNode;

public class SignMeter implements Meter
{
	/**
	 * 
	 */
	private Location loc;
	private String[] base;
	
	public SignMeter (Location location, String[] lines)
	{
		loc = location;
		base = lines;
	}
	
	public SignMeter (ConfigurationNode node)
	{
		World world = DonationMeter.plugin.getServer().getWorld(node.getString("Loc.World","World"));
		loc = new Location(world,node.getInt("Loc.X",0),node.getInt("Loc.Y",0),node.getInt("Loc.Z",0));
		
		base = new String[4];
		for (int i=0; i<4; i++)
			base[i] = node.getString("Line"+i, "");
	}
	
	@Override
	public Map<String,Object> toConfig()
	{
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("Type", "SignMeter");
		HashMap<String,Object> locMap = new HashMap<String,Object>();
		locMap.put("World", loc.getWorld().getName());
		locMap.put("X", loc.getX());
		locMap.put("Y", loc.getY());
		locMap.put("Z", loc.getZ());
		map.put("Loc", locMap);
		
		for (int i=0; i<4; i++)
			map.put("Line"+i, base[i]);
		return map;
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
	public boolean has(Location location)
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
