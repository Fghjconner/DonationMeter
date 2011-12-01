package fghjconner.DonationMeter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.config.ConfigurationNode;

public class WoolMeter implements Meter
{
	/**
	 * 
	 */
	private ArrayList<Location> locList;
	private double maxX = -1*Double.MAX_VALUE, minX = Double.MAX_VALUE, maxY = -1*Double.MAX_VALUE, minY = Double.MAX_VALUE, maxZ = -1*Double.MAX_VALUE, minZ = Double.MAX_VALUE, distX, distY, distZ;
	private byte greatestDir;
	private boolean reversed;
	private int hasBlock = Material.WOOL.getId(), neededBlock = Material.WOOL.getId(), surplusBlock = Material.WOOL.getId();
	private byte hasData = DyeColor.GREEN.getData(), neededData = DyeColor.WHITE.getData(), surplusData = DyeColor.BLUE.getData();

	public WoolMeter(Block source, boolean backwards, SignChangeEvent event)
	{
		maxX = maxY = maxZ = -Double.MAX_VALUE;
		minX = minY = minZ = Double.MAX_VALUE;
		reversed = backwards;
		locList = new ArrayList<Location>();
		addBlock(source.getLocation());
		distX = maxX - minX;
		distY = maxY - minY;
		distZ = maxZ - minZ;
		if (distX > distY && distX > distZ)
		{
			// if X is greatest
			greatestDir = 0;
		} else if (distZ > distY)
		{
			// if Z is greatest
			greatestDir = 2;
		} else
		{
			// if Y is greatest
			greatestDir = 1;
		}

		if (event.getLine(0).toLowerCase().contains("-x"))
			setDir((byte) 0);
		if (event.getLine(0).toLowerCase().contains("-y"))
			setDir((byte) 1);
		if (event.getLine(0).toLowerCase().contains("-z"))
			setDir((byte) 2);
		handleLine(event.getLine(1));
		handleLine(event.getLine(2));
		handleLine(event.getLine(3));
		event.getPlayer().sendMessage(ChatColor.GREEN.toString() + "WoolMeter Created!");
		update();
	}
	
	public WoolMeter(ConfigurationNode node)
	{
		reversed = node.getBoolean("Reversed", false);
		greatestDir = (byte) node.getInt("GreatestDirection", 0);
		
		hasBlock = node.getInt("HasBlock", Material.WOOL.getId());
		neededBlock = node.getInt("NeededBlock", Material.WOOL.getId());
		surplusBlock = node.getInt("SurplusBlock", Material.WOOL.getId());
		
		hasData = (byte) node.getInt("HasData", DyeColor.GREEN.getData());
		neededData = (byte) node.getInt("NeededData", DyeColor.WHITE.getData());
		surplusData = (byte) node.getInt("SurplusData", DyeColor.BLUE.getData());
		
		locList = new ArrayList<Location>();
		int size = node.getNodes("Locs").size();
		World world = DonationMeter.plugin.getServer().getWorld(node.getString("Locs.World"));
		for (int i=0; i<size; i++)
		{
			Location loc = new Location(world,node.getInt("Locs."+i+".X", 0),node.getInt("Locs."+i+".Y", 0),node.getInt("Locs."+i+".Z", 0)); 
			locList.add(loc);
			if (loc.getX() > maxX)
				maxX = loc.getX();
			if (loc.getX() < minX)
				minX = loc.getX();
			if (loc.getY() > maxY)
				maxY = loc.getY();
			if (loc.getY() < minY)
				minY = loc.getY();
			if (loc.getZ() > maxZ)
				maxZ = loc.getZ();
			if (loc.getZ() < minZ)
				minZ = loc.getZ();
		}
		distX = maxX-minX;
		distY = maxY-minY;
		distZ = maxZ-minZ;
		
		if (distX > distY && distX > distZ)
		{
			// if X is greatest
			greatestDir = 0;
		} else if (distZ > distY)
		{
			// if Z is greatest
			greatestDir = 2;
		} else
		{
			// if Y is greatest
			greatestDir = 1;
		}
	}

	public Map<String, Object> toConfig()
	{
		HashMap<String, Object> map = new HashMap<String,Object>();
		map.put("Type", "WoolMeter");
		map.put("Reversed", reversed);
		map.put("GreatestDirection", greatestDir);
		
		map.put("HasBlock", hasBlock);
		map.put("NeededBlock", neededBlock);
		map.put("SurplusBlock", surplusBlock);
		
		map.put("HasData", hasData);
		map.put("NeededData", neededData);
		map.put("SurplusData", surplusData);
		
		HashMap<String, Object> locs = new HashMap<String, Object>();
		locs.put("World",locList.get(0).getWorld().getName());
		for (int i=0; i<locList.size(); i++)
		{
			HashMap<String,Object> location = new HashMap<String, Object>();
			location.put("X", locList.get(i).getX());
			location.put("Y", locList.get(i).getY());
			location.put("Z", locList.get(i).getZ());
			locs.put(Integer.toString(i), location);
		}
		map.put("Locs", locs);
		return map;
	}

	public void handleLine(String line)
	{
		line = line.toLowerCase();
		if (line.contains("has"))
			setBlock(line.substring(3), (byte) 2);
		else if (line.contains("have"))
			setBlock(line.substring(4), (byte) 2);
		else if (line.contains("needs"))
			setBlock(line.substring(5), (byte) 1);
		else if (line.contains("need"))
			setBlock(line.substring(4), (byte) 1);
		else if (line.contains("surplus"))
			setBlock(line.substring(7), (byte) 3);
		else if (line.contains("extra"))
			setBlock(line.substring(5), (byte) 3);
	}

	public void setBlock(String arg, byte mode)
	{
		byte setColor;
		if (arg.contains("black"))
			setColor = (DyeColor.BLACK.getData());
		else if (arg.contains("red"))
			setColor = (DyeColor.RED.getData());
		else if (arg.contains("dark green") || arg.contains("green"))
			setColor = (DyeColor.GREEN.getData());
		else if (arg.contains("brown"))
			setColor = (DyeColor.BROWN.getData());
		else if (arg.contains("blue"))
			setColor = (DyeColor.BLUE.getData());
		else if (arg.contains("purple"))
			setColor = (DyeColor.PURPLE.getData());
		else if (arg.contains("cyan"))
			setColor = (DyeColor.CYAN.getData());
		else if (arg.contains("light gray") || arg.contains("silver"))
			setColor = (DyeColor.SILVER.getData());
		else if (arg.contains("gray"))
			setColor = (DyeColor.GRAY.getData());
		else if (arg.contains("pink"))
			setColor = (DyeColor.PINK.getData());
		else if (arg.contains("light green") || arg.contains("lime"))
			setColor = (DyeColor.LIME.getData());
		else if (arg.contains("yellow"))
			setColor = (DyeColor.YELLOW.getData());
		else if (arg.contains("light blue"))
			setColor = (DyeColor.LIGHT_BLUE.getData());
		else if (arg.contains("magenta"))
			setColor = (DyeColor.MAGENTA.getData());
		else if (arg.contains("orange"))
			setColor = (DyeColor.ORANGE.getData());
		else if (arg.contains("white"))
			setColor = (DyeColor.WHITE.getData());
		else
		{
			checkBlock(arg, mode);
			return;
		}

		switch (mode)
		{
		case 1:
			setNeedData(setColor);
			break;
		case 2:
			setHasData(setColor);
			break;
		case 3:
			setSurplusData(setColor);
			break;
		}
	}

	public void checkBlock(String line, int mode)
	{
		line = line.replace(" ", "");
		String[] bits = line.split(":");
		int block;
		byte data = 0;
		try
		{
			if (bits.length > 0)
				block = Integer.parseInt(bits[0]);
			else
				return;
			if (bits.length > 1)
				data = Byte.parseByte(bits[1]);
		} catch (NumberFormatException e)
		{
			return;
		}
		switch (mode)
		{
		case 1:
			setNeedData(data);
			setNeedBlock(block);
			break;
		case 2:
			setHasData(data);
			setHasBlock(block);
			break;
		case 3:
			setSurplusData(data);
			setSurplusBlock(block);
			break;
		}
	}

	private void addBlock(Location loc)
	{
		if (locList.contains(loc) || !loc.getBlock().getType().equals(Material.WOOL))
		{
			return;
		}
		locList.add(loc);
		int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
		if (x > maxX)
			maxX = x;
		if (x < minX)
			minX = x;
		if (y > maxY)
			maxY = y;
		if (y < minY)
			minY = y;
		if (z > maxZ)
			maxZ = z;
		if (z < minZ)
			minZ = z;
		addBlock(new Location(loc.getWorld(),loc.getX()+1,loc.getY(),loc.getZ()));
		addBlock(new Location(loc.getWorld(),loc.getX()-1,loc.getY(),loc.getZ()));
		addBlock(new Location(loc.getWorld(),loc.getX(),loc.getY()+1,loc.getZ()));
		addBlock(new Location(loc.getWorld(),loc.getX(),loc.getY()-1,loc.getZ()));
		addBlock(new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()+1));
		addBlock(new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()-1));
	}

	public boolean update()
	{
		switch (greatestDir)
		{
		case 0:
			setByX();
			break;
		case 1:
			setByY();
			break;
		case 2:
			setByZ();
			break;
		}
		return true;
	}

	private void setByX()
	{
		double cutoff, surplusCutoff;
		if (!reversed)
		{
			cutoff = (minX + ((double) DonationMeter.currentDonations / DonationMeter.requiredDonations) * distX);
			surplusCutoff = (minX + (((double) DonationMeter.currentDonations - DonationMeter.requiredDonations) / DonationMeter.requiredDonations) * distX);
		} else
		{
			cutoff = (maxX - ((double) DonationMeter.currentDonations / DonationMeter.requiredDonations) * distX);
			surplusCutoff = (maxX - (((double) DonationMeter.currentDonations - DonationMeter.requiredDonations) / DonationMeter.requiredDonations) * distX);
		}
		for (Location loc : locList)
		{
			if (!reversed)
			{
				if ((loc.getX() <= cutoff && cutoff > minX))
				{
					loc.getBlock().setTypeId(hasBlock);
					loc.getBlock().setData(hasData);
				} else
				{
					loc.getBlock().setTypeId(neededBlock);
					loc.getBlock().setData(neededData);
				}

				if ((loc.getX() <= surplusCutoff && surplusCutoff > minX))
				{
					loc.getBlock().setTypeId(surplusBlock);
					loc.getBlock().setData(surplusData);
				}
			} else
			{
				if ((loc.getX() >= cutoff && cutoff < maxX))
				{
					loc.getBlock().setTypeId(hasBlock);
					loc.getBlock().setData(hasData);
				} else
				{
					loc.getBlock().setTypeId(neededBlock);
					loc.getBlock().setData(neededData);
				}

				if ((loc.getX() >= surplusCutoff && surplusCutoff < maxX))
				{
					loc.getBlock().setTypeId(surplusBlock);
					loc.getBlock().setData(surplusData);
				}
			}
		}
	}

	private void setByY()
	{
		double cutoff, surplusCutoff;
		if (!reversed)
		{
			cutoff = (minY + ((double) DonationMeter.currentDonations / DonationMeter.requiredDonations) * distY);
			surplusCutoff = (minY + (((double) DonationMeter.currentDonations - DonationMeter.requiredDonations) / DonationMeter.requiredDonations) * distY);
		} else
		{
			cutoff = (maxY - ((double) DonationMeter.currentDonations / DonationMeter.requiredDonations) * distY);
			surplusCutoff = (maxY - (((double) DonationMeter.currentDonations - DonationMeter.requiredDonations) / DonationMeter.requiredDonations) * distY);
		}
		for (Location loc : locList)
		{
			if (!reversed)
			{
				if ((loc.getY() <= cutoff && cutoff > minY))
				{
					loc.getBlock().setTypeId(hasBlock);
					loc.getBlock().setData(hasData);
				} else
				{
					loc.getBlock().setTypeId(neededBlock);
					loc.getBlock().setData(neededData);
				}

				if ((loc.getY() <= surplusCutoff && surplusCutoff > minY))
				{
					loc.getBlock().setTypeId(surplusBlock);
					loc.getBlock().setData(surplusData);
				}
			} else
			{
				if ((loc.getY() >= cutoff && cutoff < maxY))
				{
					loc.getBlock().setTypeId(hasBlock);
					loc.getBlock().setData(hasData);
				} else
				{
					loc.getBlock().setTypeId(neededBlock);
					loc.getBlock().setData(neededData);
				}

				if ((loc.getY() >= surplusCutoff && surplusCutoff < maxY))
				{
					loc.getBlock().setTypeId(surplusBlock);
					loc.getBlock().setData(surplusData);
				}
			}
		}
	}

	private void setByZ()
	{
		double cutoff, surplusCutoff;
		if (!reversed)
		{
			cutoff = (minZ + ((double) DonationMeter.currentDonations / DonationMeter.requiredDonations) * distZ);
			surplusCutoff = (minZ + (((double) DonationMeter.currentDonations - DonationMeter.requiredDonations) / DonationMeter.requiredDonations) * distZ);
		} else
		{
			cutoff = (maxZ - ((double) DonationMeter.currentDonations / DonationMeter.requiredDonations) * distZ);
			surplusCutoff = (maxZ - (((double) DonationMeter.currentDonations - DonationMeter.requiredDonations) / DonationMeter.requiredDonations) * distZ);
		}
		for (Location loc : locList)
		{
			if (!reversed)
			{
				if ((loc.getZ() <= cutoff && cutoff > minZ))
				{
					loc.getBlock().setTypeId(hasBlock);
					loc.getBlock().setData(hasData);
				} else
				{
					loc.getBlock().setTypeId(neededBlock);
					loc.getBlock().setData(neededData);
				}

				if ((loc.getZ() <= surplusCutoff && surplusCutoff > minZ))
				{
					loc.getBlock().setTypeId(surplusBlock);
					loc.getBlock().setData(surplusData);
				}
			} else
			{
				if ((loc.getZ() >= cutoff && cutoff < maxZ))
				{
					loc.getBlock().setTypeId(hasBlock);
					loc.getBlock().setData(hasData);
				} else
				{
					loc.getBlock().setTypeId(neededBlock);
					loc.getBlock().setData(neededData);
				}

				if ((loc.getZ() >= surplusCutoff && surplusCutoff < maxZ))
				{
					loc.getBlock().setTypeId(surplusBlock);
					loc.getBlock().setData(surplusData);
				}
			}
		}
	}

	public void setDir(byte dir)
	{
		greatestDir = (byte) dir;
	}

	public void setHasData(byte has)
	{
		hasData = has;
	}

	public void setNeedData(byte needed)
	{
		neededData = needed;
	}

	public void setSurplusData(byte surplus)
	{
		surplusData = surplus;
	}

	public void setHasBlock(int has)
	{
		hasBlock = has;
	}

	public void setNeedBlock(int needed)
	{
		neededBlock = needed;
	}

	public void setSurplusBlock(int surplus)
	{
		surplusBlock = surplus;
	}

	public void destroy()
	{
		for (Location loc : locList)
		{
			loc.getBlock().setTypeId(Material.WOOL.getId());
			loc.getBlock().setData((byte) 0);
		}
		DonationMeter.plugin.meterList.remove(this);
	}

	public boolean has(Location loc)
	{
		return locList.contains(loc);
	}

	public boolean has(Collection<Block> blockList)
	{
		for (Block block : blockList)
			if (has(block.getLocation()))
				return true;
		return false;
	}
}
