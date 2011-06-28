package fghjconner.DonationMeter;

import java.util.Collection;

import org.bukkit.block.Block;

public interface Meter
{
	public void update();
	public void destroy();
	public boolean has(SimpleLoc loc);
	public boolean has(Collection<Block> blockList);
}
