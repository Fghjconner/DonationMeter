package fghjconner.DonationMeter;

import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class DMEntityListener extends EntityListener
{
	private DonationMeter plugin;
	public DMEntityListener (DonationMeter donationMeter)
	{
		plugin = donationMeter;
	}
	public void onEntityExplode(EntityExplodeEvent event)
	{
		for (Meter meter: plugin.meterList)
		{
			if (meter.has(event.blockList()))
				meter.destroy();
		}
	}
}
