package fghjconner.DonationMeter;

import java.util.Collection;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;

public interface Meter
{
	public void destroy();
	public boolean update();
	public boolean has(Location loc);
	public boolean has(Collection<Block> blockList);
	public Map<String,Object> toConfig();
}
