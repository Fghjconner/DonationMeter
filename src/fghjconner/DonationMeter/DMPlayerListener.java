package fghjconner.DonationMeter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class DMPlayerListener extends PlayerListener
{
	DonationMeter plugin;

	public DMPlayerListener(DonationMeter donationMeter)
	{
		plugin = donationMeter;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if ((plugin.hasPermission(player, "DonationMeter.admin")) && plugin.notificationMap.size() > 0)
		{
			player.sendMessage(ChatColor.GREEN.toString() + "Outstanding Donation Notifications!");
			player.sendMessage(ChatColor.BLUE.toString() + "/Donations notifications" + ChatColor.GREEN.toString() + " for more info");
		}
	}
}
