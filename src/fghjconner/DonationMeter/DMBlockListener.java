package fghjconner.DonationMeter;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
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
		Location loc = event.getBlock().getLocation();
		if (DonationMeter.meterList.containsKey(loc))
		{
			DonationMeter.meterList.get(loc).destroy();
			DonationMeter.meterList.remove(loc);
		}
	}
	
	public void onSignChange(SignChangeEvent blockEvent)
	{
		Boolean reverse = false;
		SignChangeEvent event = (SignChangeEvent)blockEvent;
		Block sign = event.getBlock();
		Block base = sign.getFace(((Sign) sign.getState()).getAttachedFace());
		if (DonationMeter.permissionHandler.has(event.getPlayer(), "DonationMeter.admin") || !event.getLine(0).equals("Donation Meter") || !base.getType().equals(Material.WOOL))
		{
			if (!event.getLine(0).equals("Donation Meter -r"))
				return;
			reverse = true;
		}
		DonationMeter.meterList.put(sign.getLocation(), new VisualMeter(base,reverse));
		DonationMeter.updateMeters();
	}
	

}
