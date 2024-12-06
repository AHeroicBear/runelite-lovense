package com.lovense;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Lovense"
)
public class LovensePlugin extends Plugin
{
	private static final String CONFIG_GROUP = "lovense";
	private static final String CONFIG_SHOW_PANEL = "lovense_show_side_panel";

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Client client;

	@Inject
	private LovenseConfig config;

	@Inject
	private LovensePanel panel;
	private NavigationButton navigationButton;

	private boolean panelEnabled = false;

	@Override
	protected void startUp() throws Exception
	{
		panel.init();

		BufferedImage icon = ImageUtil.loadImageResource(LovensePlugin.class, "/hitsplat.png");
		navigationButton = NavigationButton.builder()
				.tooltip("Lovense")
				.icon(icon)
				.priority(20)
				.panel(panel)
				.build();

		if (config.showLovenseSidePanel())
		{
			clientToolbar.addNavigation(navigationButton);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(CONFIG_GROUP))
		{
			return;
		}

		if (configChanged.getKey().equals(CONFIG_SHOW_PANEL))
		{
			clientToolbar.removeNavigation(navigationButton);
			if (config.showLovenseSidePanel())
			{
				clientToolbar.addNavigation(navigationButton);
			}
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navigationButton);
	}
	@Provides
	LovenseConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LovenseConfig.class);
	}
}
