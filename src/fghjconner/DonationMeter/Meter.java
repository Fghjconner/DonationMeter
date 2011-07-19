package fghjconner.DonationMeter;

import java.io.Serializable;
import java.util.Collection;

import org.bukkit.block.Block;

public interface Meter extends Serializable
{
	public void destroy();
	public boolean update();
	public boolean has(SimpleLoc loc);
	public boolean has(Collection<Block> blockList);
}
