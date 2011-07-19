package fghjconner.DonationMeter;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.SignChangeEvent;

public class WoolMeter implements Meter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1424688872564251842L;
	private ArrayList<SimpleLoc> locList;
	private double maxX,minX,maxY,minY,maxZ,minZ,distX,distY,distZ;
	private byte greatestDir;
	private boolean reversed;
	private byte hasColor = DyeColor.GREEN.getData(),neededColor = DyeColor.WHITE.getData(),surplusColor = DyeColor.BLUE.getData();
	
	public WoolMeter(Block source, boolean backwards, SignChangeEvent event)
	{
		maxX = maxY = maxZ = -Double.MAX_VALUE;
		minX = minY = minZ = Double.MAX_VALUE;
		reversed = backwards;
		locList = new ArrayList<SimpleLoc>();
		addBlock(SimpleLoc.simplify(source.getLocation()));
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

		if (event.getLine(0).toLowerCase().contains("-x"))
			setDir((byte)0);
		if (event.getLine(0).toLowerCase().contains("-y"))
			setDir((byte)1);
		if (event.getLine(0).toLowerCase().contains("-z"))
			setDir((byte)2);
		handleLine(event.getLine(1));
		handleLine(event.getLine(2));
		handleLine(event.getLine(3));
		event.getPlayer().sendMessage(ChatColor.GREEN.toString()+"WoolMeter Created!");
		update();
	}
	
	public void handleLine(String line)
	{
		line=line.toLowerCase();
		if (line.contains("has"))
			setColor(line,(byte) 2);
		else if (line.contains("needs") || line.contains("need"))
			setColor(line,(byte)1);
		else if (line.contains("surplus") || line.contains("extra"))
			setColor(line,(byte)3);
	}
	
	public void setColor(String arg, byte mode)
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
			return;
		
		switch (mode)
		{
		case 1: setNeedColor(setColor);	break;
		case 2: setHasColor(setColor);	break;
		case 3: setSurplusColor(setColor);	break;
		}
	}
	
	private void addBlock(SimpleLoc loc)
	{
		if (locList.contains(loc) || !loc.getBlock().getType().equals(Material.WOOL))
		{
			return;
		}
		locList.add(loc);
		int x=loc.getX(),y=loc.getY(),z=loc.getZ();
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
		addBlock(new SimpleLoc(loc.getWorld(),loc.getX()+1,loc.getY(),loc.getZ()));
		addBlock(new SimpleLoc(loc.getWorld(),loc.getX()-1,loc.getY(),loc.getZ()));
		addBlock(new SimpleLoc(loc.getWorld(),loc.getX(),loc.getY()+1,loc.getZ()));
		addBlock(new SimpleLoc(loc.getWorld(),loc.getX(),loc.getY()-1,loc.getZ()));
		addBlock(new SimpleLoc(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()+1));
		addBlock(new SimpleLoc(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()-1));
	}

	public boolean update()
	{
		switch(greatestDir)
		{
		case 0: setByX();	break;
		case 1: setByY();	break;
		case 2: setByZ();	break;
		}
		return true;
	}
	
	private void setByX()
	{
		double cutoff, surplusCutoff;
		if (!reversed)
		{
			cutoff = (minX + ((double)DonationMeter.currentDonations/DonationMeter.requiredDonations)*distX);
			surplusCutoff =  (minX + (((double)DonationMeter.currentDonations - DonationMeter.requiredDonations)/DonationMeter.requiredDonations)*distX);
		}
		else
		{
			cutoff = (maxX - ((double)DonationMeter.currentDonations/DonationMeter.requiredDonations)*distX);
			surplusCutoff = (maxX - (((double)DonationMeter.currentDonations - DonationMeter.requiredDonations)/DonationMeter.requiredDonations)*distX);
		}
		for (SimpleLoc loc: locList)
		{
			if (!reversed)
			{
				if ((loc.getX()<=cutoff && cutoff>minX))
					loc.getBlock().setData(hasColor);
				else
					loc.getBlock().setData(neededColor);
				
				if ((loc.getX()<=surplusCutoff && surplusCutoff>minX))
					loc.getBlock().setData(surplusColor);
			}
			else
			{
				if ((loc.getX()>=cutoff && cutoff<maxX))
					loc.getBlock().setData(hasColor);
				else
					loc.getBlock().setData(neededColor);
				
				if ((loc.getX()>=surplusCutoff && surplusCutoff<maxX))
					loc.getBlock().setData(surplusColor);
			}
		}
	}
	
	private void setByY()
	{
		double cutoff, surplusCutoff;
		if (!reversed)
		{
			cutoff = (minY + ((double)DonationMeter.currentDonations/DonationMeter.requiredDonations)*distY);
			surplusCutoff =  (minY + (((double)DonationMeter.currentDonations - DonationMeter.requiredDonations)/DonationMeter.requiredDonations)*distY);
		}
		else
		{
			cutoff = (maxY - ((double)DonationMeter.currentDonations/DonationMeter.requiredDonations)*distY);
			surplusCutoff = (maxY - (((double)DonationMeter.currentDonations - DonationMeter.requiredDonations)/DonationMeter.requiredDonations)*distY);
		}
		for (SimpleLoc loc: locList)
		{
			if (!reversed)
			{
				if ((loc.getY()<=cutoff && cutoff>minY))
					loc.getBlock().setData(hasColor);
				else
					loc.getBlock().setData(neededColor);
				
				if ((loc.getY()<=surplusCutoff && surplusCutoff>minY))
					loc.getBlock().setData(surplusColor);
			}
			else
			{
				if ((loc.getY()>=cutoff && cutoff<maxY))
					loc.getBlock().setData(hasColor);
				else
					loc.getBlock().setData(neededColor);
				
				if ((loc.getY()>=surplusCutoff && surplusCutoff<maxY))
					loc.getBlock().setData(surplusColor);
			}
		}
	}
	
	private void setByZ()
	{
		double cutoff, surplusCutoff;
		if (!reversed)
		{
			cutoff = (minZ + ((double)DonationMeter.currentDonations/DonationMeter.requiredDonations)*distZ);
			surplusCutoff =  (minZ + (((double)DonationMeter.currentDonations - DonationMeter.requiredDonations)/DonationMeter.requiredDonations)*distZ);
		}
		else
		{
			cutoff = (maxZ - ((double)DonationMeter.currentDonations/DonationMeter.requiredDonations)*distZ);
			surplusCutoff = (maxZ - (((double)DonationMeter.currentDonations - DonationMeter.requiredDonations)/DonationMeter.requiredDonations)*distZ);
		}
		for (SimpleLoc loc: locList)
		{
			if (!reversed)
			{
				if ((loc.getZ()<=cutoff && cutoff>minZ))
					loc.getBlock().setData(hasColor);
				else
					loc.getBlock().setData(neededColor);
				
				if ((loc.getZ()<=surplusCutoff && surplusCutoff>minZ))
					loc.getBlock().setData(surplusColor);
			}
			else
			{
				if ((loc.getZ()>=cutoff && cutoff<maxZ))
					loc.getBlock().setData(hasColor);
				else
					loc.getBlock().setData(neededColor);
				
				if ((loc.getZ()>=surplusCutoff && surplusCutoff<maxZ))
					loc.getBlock().setData(surplusColor);
			}
		}
	}
	
	public void setDir(byte dir)
	{
		greatestDir=(byte)dir;
	}
	
	public void setHasColor(byte has)
	{
		hasColor=has;
	}
	
	public void setNeedColor(byte needed)
	{
		neededColor=needed;
	}
	
	public void setSurplusColor(byte surplus)
	{
		surplusColor=surplus;
	}
	
	public void destroy()
	{
		for (SimpleLoc loc: locList)
		{
			(loc.getBlock()).setData((byte) 0);
		}
		DonationMeter.plugin.meterList.remove(this);
	}
	
	public boolean has(SimpleLoc loc)
	{
		return locList.contains(loc);
	}
	
	public boolean has(Collection<Block> blockList)
	{
		for (Block block: blockList)
			if (has(SimpleLoc.simplify(block.getLocation())))
				return true;
		return false;
	}
}
