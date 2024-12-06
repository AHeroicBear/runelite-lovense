package com.lovense;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("lovense")
public interface LovenseConfig extends Config
{
	@ConfigItem(
			keyName = "lovense_local_ip",
			name = "Local IP",
			description = "Your Local IP, shown in the Game Mode section of the Lovense app",
			position = 1
	)
	default String localIp()
	{
		return "";
	}

	@ConfigItem(
			keyName = "lovense_port",
			name = "Port",
			description = "Your Port (not SSL Port), shown in the Game Mode section of the Lovense app",
			position = 2
	)
	default String httpPort()
	{
		return "";
	}

	@ConfigItem(
			keyName = "lovense_show_side_panel",
			name = "Show the Lovense side panel",
			description = "Show the Lovense side panel",
			position = 3
	)
	default boolean showLovenseSidePanel()
	{
		return true;
	}

}
