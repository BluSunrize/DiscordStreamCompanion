package blusunrize.discordstreamcompanion.config;

import blusunrize.discordstreamcompanion.DiscordStreamCompanion;
import blusunrize.discordstreamcompanion.config.ConfigValue.ConfigValueFactory;
import blusunrize.discordstreamcompanion.modules.IModule;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
public class Config
{
	private File configFile;

	private String token;

	private boolean isLoaded = false;

	private LinkedHashMap<IModule, ConfigValue[]> moduleValues = new LinkedHashMap<>();
	private LinkedHashMap<String, ConfigValue> allValues = new LinkedHashMap<>();

	public Config(DiscordStreamCompanion dsc, File dataFolder) throws Exception
	{
		configFile = new File(dataFolder, "config.cfg");

		for(IModule module : dsc.getModules())
		{
			List<ConfigValue> list = new ArrayList<>();
			for(Field field : module.getClass().getDeclaredFields())
				if(field.isAnnotationPresent(ConfigValueFactory.class))
				{
					ConfigValueFactory factory = field.getAnnotation(ConfigValueFactory.class);
					ConfigValue cfgValue = new ConfigValue(module, field, factory);
					allValues.put(cfgValue.getKey(), cfgValue);
					list.add(cfgValue);
				}
			moduleValues.put(module, list.toArray(new ConfigValue[list.size()]));
		}

		if(configFile.exists())
		{
			dsc.getLogger().info("Loading Config");
			load(dsc);
		} else
		{
			dsc.getLogger().info("No Config Present, creating new one");
			createConfig(dsc);
		}
	}

	public boolean isLoaded()
	{
		return isLoaded;
	}

	public void setLoaded(boolean loaded)
	{
		isLoaded = loaded;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public String getToken()
	{
		return token;
	}

	public HashMap<IModule, ConfigValue[]> getModuleValues()
	{
		return moduleValues;
	}

	private void load(DiscordStreamCompanion dsc) throws Exception
	{
		List<String> lines = Files.readAllLines(configFile.toPath());
		for(String line : lines)
			if(line.indexOf("=") >= 0)
			{
				String[] parts = line.split("=", 2);
				String key = parts[0].trim().toLowerCase();
				String value = parts.length > 1?parts[1].trim(): null;
				if("token".equals(key))
					token = value;
				else if(allValues.containsKey(key))
				{
					ConfigValue cfg = allValues.get(key);
					cfg.readConfigFileValue(value);
				}
			}
		this.isLoaded = true;
	}

	public void saveConfig(DiscordStreamCompanion dsc)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
			writer.write("token="+token);

			for(ConfigValue cfg : allValues.values())
			{
				writer.newLine();
				writer.write(cfg.getKey()+"="+cfg.getConfigFileValue());
			}

			writer.close();
		} catch(Exception e)
		{
		}
	}

	private void createConfig(DiscordStreamCompanion dsc)
	{
		openConfigGui(dsc).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public JFrame openConfigGui(DiscordStreamCompanion dsc)
	{
//		JFrame frame = new JFrame("Configure DiscordStreamCompanion");
//		GuiConfig_old gui = new GuiConfig_old().setFrame(frame).setConfig(this);
//		frame.setContentPane(gui.getWindow());
//		frame.pack();
//		frame.setVisible(true);
//		return frame;
		return new GuiConfig(dsc, this);
	}
}
