package blusunrize.discordstreamcompanion;

import blusunrize.discordstreamcompanion.config.Config;
import blusunrize.discordstreamcompanion.modules.IModule;
import blusunrize.discordstreamcompanion.modules.voicechannel.ModuleVoiceChannel;
import blusunrize.discordstreamcompanion.util.Utils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
 * @since 25.08.2017
 */
public class DiscordStreamCompanion extends ListenerAdapter
{
	public final Logger logger = Logger.getLogger("DSC");
	private final File dataFolder;
	private final Config config;
	private JDA jdaInstance;

	private IModule[] modules = {new ModuleVoiceChannel()};

	private String userId;
	private TrayIcon systemTray;

	public DiscordStreamCompanion() throws Exception
	{
		this.dataFolder = new File(System.getenv("APPDATA")+"/DiscordStreamCompanion");
		this.dataFolder.mkdirs();
		this.setupLogger();
		this.config = new Config(this, dataFolder);
		this.setupTray();

		for(IModule module : modules)
			module.setDSC(this);

		while(!this.config.isLoaded())
			Utils.threadSleep(100);

		try
		{
			JDABuilder builder = new JDABuilder(AccountType.CLIENT).setToken(config.getToken()).setStatus(OnlineStatus.IDLE).setIdle(true);
			builder.addEventListener(this);
			for(IModule module : modules)
				if(module instanceof EventListener)
					builder.addEventListener(module);
			this.jdaInstance = builder.buildAsync();
		} catch(LoginException|IllegalArgumentException|RateLimitedException e)
		{
			logger.severe(e.getLocalizedMessage());
		}
	}

	public Logger getLogger()
	{
		return logger;
	}

	public JDA getJDAInstance()
	{
		return jdaInstance;
	}

	public String getUserId()
	{
		return userId;
	}

	public boolean isMe(User user)
	{
		return userId!=null&&userId.equals(getUserId(user));
	}

	public IModule[] getModules()
	{
		return modules;
	}

	public File getDataFolder()
	{
		return dataFolder;
	}

	private static final String[] LOGFILES = {"3", "2", "1", "latest"};

	private void setupLogger()
	{
		for(int i = 1; i < LOGFILES.length; i++)
		{
			File f = new File(this.dataFolder, "log_"+LOGFILES[i]+".log");
			f.renameTo(new File(this.dataFolder, "log_"+LOGFILES[i-1]+".log"));
		}
		try
		{
			FileHandler fh = new FileHandler(this.dataFolder+"/log_"+LOGFILES[3]+".log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setupTray()
	{
		if(SystemTray.isSupported())
			logger.info("Setting up SystemTray");
		else
		{
			logger.info("SystemTray is not supported, tray icon cannot be set up");
			return;
		}

		try
		{
			URL imageURL = DiscordStreamCompanion.class.getResource("images/icon.png");

			if(imageURL==null)
			{
				logger.severe("SystemTray Icon not found");
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

			popup.addSeparator();

			MenuItem item = new MenuItem("Config");
			item.addActionListener(e -> cmdConfig());
			popup.add(item);

			item = new MenuItem("Exit");
			item.addActionListener(e -> cmdShutdown());
			popup.add(item);
			systemTray.setPopupMenu(popup);

			tray.add(systemTray);
		} catch(Exception e)
		{
			logger.log(Level.SEVERE, "Error setting up SystemTray", e);
		}
	}

	@Override
	public void onReady(ReadyEvent event)
	{
		userId = getUserId(jdaInstance.getSelfUser());

		logger.info("Connection has been established, Selfbot active for "+userId);
		if(systemTray!=null)
			systemTray.displayMessage("DiscordStreamCompanion", "Connection has been established, Selfbot active for "+userId, TrayIcon.MessageType.NONE);
	}

	//==============================================
	//COMMANDS
	//==============================================
	private static final String PREFIX = "!dsc.";

	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if(event.getMessage().getContent().startsWith(PREFIX)&&userId!=null&&userId.equals(getUserId(event.getAuthor())))
		{
			Message message = event.getMessage();
			String content = message.getContent();

			switch(content.substring(PREFIX.length()))
			{
				case "shutdown":
					message.delete().complete();
					cmdShutdown();
					break;
				case "ping":
					logger.info("Requesting ping");
					new Thread(() -> {
						message.editMessage("Pong! :three:").complete();
						Utils.threadSleep(1000);
						message.editMessage("Pong! :two:").complete();
						Utils.threadSleep(1000);
						message.editMessage("Pong! :one:").complete();
						Utils.threadSleep(1000);
					}).run();
					break;
				case "config":
				case "cfg":
					cmdConfig();
					break;
			}

			message.delete().complete();
		}
	}

	private void cmdShutdown()
	{
		logger.info("Shutting down");
		this.jdaInstance.shutdown();
		System.exit(0);
	}

	private void cmdConfig()
	{
		logger.info("Opening config");
		this.config.openConfigGui(this).addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				for(IModule module : getModules())
					module.onConfigChanged();
			}
		});
	}


	private String getUserId(User user)
	{
		return user.getName()+"#"+user.getDiscriminator();
	}
}
