package fghjconner.DonationMeter;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Wool;

public class VisualMeter
{
	private ArrayList<Location> locList;
	private int maxX,minX,maxY,minY,maxZ,minZ,distX,distY,distZ;
	private byte greatestDir;
	private boolean reversed;
	private byte hasColor = DyeColor.GREEN.getData(),neededColor = DyeColor.WHITE.getData();
	
	public VisualMeter(Block source, boolean backwards)
	{
		reversed = backwards;
		addBlock(source.getLocation());
		distX=maxX-minX;
		distY=maxY-minY;
		distZ=maxZ-minZ;
		if(distX>distY && distX>distZ)
		{
			//if X is greatest
			greatestDir=0;
		}
		else if (distZ>distY)
		{
			//if Z is greatest
			greatestDir=2;
		}
		else
		{
			//if Y is greatest
			greatestDir=1;
		}
	}
	
	private void addBlock(Location loc)
	{
		if (locList.contains(loc) || !loc.getBlock().getType().equals(Material.WOOL))
		{
			return;
		}
		locList.add(loc);
		int x=loc.getBlockX(),y=loc.getBlockY(),z=loc.getBlockZ();
		if (x>maxX)
			maxX=x;
		if (x<minX)
			minX=x;
		if (y>maxY)
			maxY=y;
		if (y<minY)
			minY=y;
		if (z>maxZ)
			maxZ=z;
		if (z<minZ)
			minZ=z;
		addBlock(new Location(loc.getWorld(),loc.getX()+1,loc.getY(),loc.getZ()));
		addBlock(new Location(loc.getWorld(),loc.getX()-1,loc.getY(),loc.getZ()));
		addBlock(new Location(loc.getWorld(),loc.getX(),loc.getY()+1,loc.getZ()));
		addBlock(new Location(loc.getWorld(),loc.getX(),loc.getY()-1,loc.getZ()));
		addBlock(new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()+1));
		addBlock(new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()-1));
	}

	public void update()
	{
		switch(greatestDir)
		{
		case 0: setByX();	break;
		case 1: setByY();	break;
		case 2: setByZ();	break;
		}
	}
	
	private void setByX()
	{
		int cutoff;
		if (!reversed)
			cutoff = minX + (DonationMeter.currentDonations/DonationMeter.requiredDonations)/distX;
		else
			cutoff = maxX - (DonationMeter.currentDonations/DonationMeter.requiredDonations)/distX;
		for (Location loc: locList)
		{
			if ((loc.getBlockX()<cutoff) ^ reversed)
				((Wool)loc.getBlock()).setData(hasColor);
			else
				((Wool)loc.getBlock()).setData(neededColor);
		}
	}
	
	private void setByY()
	{
		int cutoff;
		if (!reversed)
			cutoff = minY + (DonationMeter.currentDonations/DonationMeter.requiredDonations)/distY;
		else
			cutoff = maxY - (DonationMeter.currentDonations/DonationMeter.requiredDonations)/distY;
		for (Location loc: locList)
		{
			if ((loc.getBlockY()<cutoff) ^ reversed)
				((Wool)loc.getBlock()).setData(hasColor);
			else
				((Wool)loc.getBlock()).setData(neededColor);
		}
	}
	
	private void setByZ()
	{
		int cutoff;
		if (!reversed)
			cutoff = minZ + (DonationMeter.currentDonations/DonationMeter.requiredDonations)/distZ;
		else
			cutoff = maxZ - (DonationMeter.currentDonations/DonationMeter.requiredDonations)/distZ;
		for (Location loc: locList)
		{
			if ((loc.getBlockZ()<cutoff) ^ reversed)
				((Wool)loc.getBlock()).setData(hasColor);
			else
				((Wool)loc.getBlock()).setData(neededColor);
		}
	}
	
	public void destroy()
	{
		for (Location loc: locList)
		{
			((Wool)loc.getBlock()).setData((byte) 0);
		}
	}
}
