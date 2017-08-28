package blusunrize.discordstreamcompanion.systemtray;

import blusunrize.discordstreamcompanion.DiscordStreamCompanion;
import blusunrize.discordstreamcompanion.modules.IModule;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Copyright 2017 BluSunrize
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author BluSunrize
 * @since 28.08.2017
 */
public class SystemTrayHandler
{
	private boolean active = false;
	private TrayIcon systemTray;

	public SystemTrayHandler(DiscordStreamCompanion dsc)
	{
		if(SystemTray.isSupported())
			dsc.getLogger().info("Setting up SystemTray");
		else
		{
			dsc.getLogger().info("SystemTray is not supported, tray icon cannot be set up");
			return;
		}

		try
		{
			URL imageURL = DiscordStreamCompanion.class.getResource("images/icon.png");

			if(imageURL==null)
			{
				dsc.getLogger().severe("SystemTray Icon not found");
				return;
			}

			systemTray = new TrayIcon(new ImageIcon(imageURL, "tray icon").getImage());
			systemTray.setImageAutoSize(true);
			systemTray.setToolTip("Discord Stream Companion");
			final SystemTray tray = SystemTray.getSystemTray();
			final PopupMenu popup = new PopupMenu();

			Menu about = new Menu("About");
			about.add("Author: BluSunrize");
			about.add("Libraries: JDA + Dependencies");
			popup.add(about);

			List<MenuItem> priorityMethods = new ArrayList();

			for(IModule module : dsc.getModules())
			{
				List<MenuItem> moduleMethods = new ArrayList();
				for(Method method : module.getClass().getDeclaredMethods())
					if(method.isAnnotationPresent(SystemTrayMethod.class))
					{
						if(!Modifier.isPublic(method.getModifiers()))
							throw new RuntimeException("Error accessing method "+method.getName()+", systemtray methodes must be public");
						Class[] parameters = method.getParameterTypes();
						if(parameters.length!=0)
							throw new RuntimeException("Error handling method "+method.getName()+", systemtray methodes musn't have parameters");

						SystemTrayMethod annotation = method.getAnnotation(SystemTrayMethod.class);
						MenuItem item = new MenuItem(annotation.name());
						item.addActionListener(e -> {
							try
							{
								method.invoke(module);
							} catch(Exception e1)
							{
							}
						});
						if(annotation.priorityCommand())
							priorityMethods.add(item);
						else
							moduleMethods.add(item);
					}
				if(!moduleMethods.isEmpty())
				{
					Menu menu = new Menu(module.getName());
					for(MenuItem item : moduleMethods)
						menu.add(item);
					popup.add(menu);
				}
			}

			popup.addSeparator();

			if(!priorityMethods.isEmpty())
				for(MenuItem item : priorityMethods)
					popup.add(item);
			systemTray.setPopupMenu(popup);

			tray.add(systemTray);
			active = true;
		} catch(Exception e)
		{
			dsc.getLogger().log(Level.SEVERE, "Error setting up SystemTray", e);
		}
	}

	public boolean isActive()
	{
		return active;
	}

	public TrayIcon getSystemTray()
	{
		return systemTray;
	}

	public void displayMessage(String message, int mesasgeType)
	{
		if(isActive())
			this.systemTray.displayMessage("DiscordStreamCompanion", message, TrayIcon.MessageType.values()[mesasgeType]);
	}
}
