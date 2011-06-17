package fghjconner.DonationMeter;


import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Sign;

public class DMBlockListener extends BlockListener
{
	private DonationMeter plugin;
	public DMBlockListener (DonationMeter plugin)
	{
		this.plugin = plugin;
	}

	public void onBlockBreak(BlockBreakEvent event)
	{
		SimpleLoc loc = SimpleLoc.simplify(event.getBlock().getLocation());
		ArrayList<WoolMeter> affected = new ArrayList<WoolMeter>(); 
		for (WoolMeter meter:plugin.meterList.values())
		{
			if (meter.has(loc))
			{
				affected.add(meter);
			}
		}
		for (WoolMeter marked:affected)
		{
			marked.destroy();
			plugin.meterList.values().remove(marked);
			event.getPlayer().sendMessage(ChatColor.RED.toString()+"Meter destroyed");
		}
	}

	public void onBlockBurn(BlockBurnEvent event)
	{
		SimpleLoc loc = SimpleLoc.simplify(event.getBlock().getLocation());
		ArrayList<WoolMeter> affected = new ArrayList<WoolMeter>(); 
		for (WoolMeter meter:plugin.meterList.values())
		{
			if (meter.has(loc))
			{
				affected.add(meter);
			}
		}
		for (WoolMeter marked:affected)
		{
			marked.destroy();
			plugin.meterList.values().remove(marked);
		}
	}

	public void onSignChange(SignChangeEvent blockEvent)
	{
		Boolean reverse = false;
		SignChangeEvent event = blockEvent;
		Block sign = event.getBlock();
		Block base = sign.getFace(((Sign)sign.getState().getData()).getAttachedFace());
		if (!DonationMeter.permissionHandler.has(event.getPlayer(), "DonationMeter.admin") || !base.getType().equals(Material.WOOL) || isMeter(base))
			return;
		if (!event.getLine(0).toLowerCase().contains("dmeter") && !event.getLine(0).toLowerCase().contains("donations"))
		{
			return;
		}
		if (event.getLine(0).toLowerCase().contains("-r"))
			reverse=true;
		WoolMeter meter = new WoolMeter(base,reverse);
		plugin.meterList.put(SimpleLoc.simplify(sign.getLocation()), meter);
		if (event.getLine(0).toLowerCase().contains("-x"))
			meter.setDir((byte)0);
		if (event.getLine(0).toLowerCase().contains("-y"))
			meter.setDir((byte)1);
		if (event.getLine(0).toLowerCase().contains("-z"))
			meter.setDir((byte)2);
		handleLine(event.getLine(1),meter);
		handleLine(event.getLine(2),meter);
		handleLine(event.getLine(3),meter);
		blockEvent.getPlayer().sendMessage(ChatColor.GREEN.toString()+"Meter Created!");
		plugin.updateMeters();
	}
	
	public void handleLine(String line,WoolMeter meter)
	{
		line=line.toLowerCase();
		if (line.contains("has"))
			setColor(line,meter,(byte) 2);
		else if (line.contains("needs") || line.contains("need"))
			setColor(line,meter,(byte)1);
		else if (line.contains("surplus") || line.contains("extra"))
			setColor(line,meter,(byte)3);
	}
	
	public void setColor(String arg, WoolMeter meter,byte mode)
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
		case 1: meter.setNeedColor(setColor);	break;
		case 2: meter.setHasColor(setColor);	break;
		case 3: meter.setSurplusColor(setColor);	break;
		}
	}

	private boolean isMeter(Block base)
	{
		for (WoolMeter meter:plugin.meterList.values())
		{
			if (meter.has(SimpleLoc.simplify(base.getLocation())))
				return true;
		}
		return false;
	}
}
