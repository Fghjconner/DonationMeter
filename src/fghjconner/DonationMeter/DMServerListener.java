package fghjconner.DonationMeter;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.nijikokun.bukkit.Permissions.Permissions;

public class DMServerListener extends ServerListener
{
	DonationMeter plugin;
	
	public DMServerListener(DonationMeter donationMeter)
	{
		plugin = donationMeter;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event)
	{
		if (event.getPlugin().getDescription().getName().equalsIgnoreCase("Permissions"))
			DonationMeter.permissionHandler = ((Permissions)event.getPlugin()).getHandler();
	}
}