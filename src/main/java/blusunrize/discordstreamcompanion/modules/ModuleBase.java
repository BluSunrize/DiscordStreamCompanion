package blusunrize.discordstreamcompanion.modules;

import blusunrize.discordstreamcompanion.DiscordStreamCompanion;
import blusunrize.discordstreamcompanion.command.CommandMethod;
import blusunrize.discordstreamcompanion.systemtray.SystemTrayMethod;
import blusunrize.discordstreamcompanion.util.Utils;
import net.dv8tion.jda.core.entities.Message;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
public class ModuleBase implements IModule
{
	private DiscordStreamCompanion dsc;

	@Override
	public String getName()
	{
		return "Base";
	}

	@Override
	public void setDSC(DiscordStreamCompanion dsc)
	{
		this.dsc = dsc;
	}

	@Override
	public DiscordStreamCompanion getDSC()
	{
		return dsc;
	}

	@Override
	public void onConfigChanged()
	{
	}

	@CommandMethod(command = "ping")
	public void cmdPing(Message message, String[] args)
	{
		logger().info("Requesting ping");
		new Thread(() -> {
			message.editMessage("Pong! :three:").complete();
			Utils.threadSleep(1000);
			message.editMessage("Pong! :two:").complete();
			Utils.threadSleep(1000);
			message.editMessage("Pong! :one:").complete();
			Utils.threadSleep(1000);
		}).run();
	}

	@CommandMethod(command = "config", altCommands = {"cfg"})
	public void cmdConfig(Message message, String[] args)
	{
		doConfig();
	}

	@SystemTrayMethod(name = "Config", priorityCommand = true)
	public void doConfig()
	{
		logger().info("Opening config");
		getDSC().getConfig().openConfigGui(getDSC()).addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				for(IModule module : getDSC().getModules())
					module.onConfigChanged();
			}
		});
	}

	@CommandMethod(command = "shutdown")
	public void cmdShutdown(Message message, String[] args)
	{
		message.delete().complete();
		doShutdown();
	}

	@SystemTrayMethod(name = "Exit", priorityCommand = true)
	public void doShutdown()
	{
		logger().info("Shutting down");
		jda().shutdown();
		System.exit(0);
	}
}
