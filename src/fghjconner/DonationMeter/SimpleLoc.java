package fghjconner.DonationMeter;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SimpleLoc implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2835528180226548355L;
	private String world;
	private int xPos,yPos,zPos;
	public SimpleLoc(World worldIn, int x, int y, int z)
	{
		world = worldIn.getName();
		xPos=x;
		yPos=y;
		zPos=z;
	}

	public static SimpleLoc simplify(Location loc)
	{
		return new SimpleLoc(loc.getWorld(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
	}

	public World getWorld()
	{
		return DonationMeter.server.getWorld(world);
	}

	public Block getBlock()
	{
		return DonationMeter.server.getWorld(world).getBlockAt(xPos,yPos,zPos);
	}

	public int getX()
	{
		return xPos;
	}

	public int getY()
	{
		return yPos;
	}

	public int getZ()
	{
		return zPos;
	}

	public boolean equals(Object o)
	{
		if (o instanceof SimpleLoc)
		{
			SimpleLoc loc = (SimpleLoc)o;
			return (loc.getWorld().getName().equals(world) && loc.getX()==xPos && loc.getY()==yPos && loc.getZ()==zPos);
		}
		else if (o instanceof Location)
		{
			return SimpleLoc.simplify((Location)o).equals(this);
		}
		return false;
	}
}