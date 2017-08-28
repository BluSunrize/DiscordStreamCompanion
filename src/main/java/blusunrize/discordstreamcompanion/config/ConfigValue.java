package blusunrize.discordstreamcompanion.config;

import blusunrize.discordstreamcompanion.modules.IModule;

import javax.swing.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;

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
public class ConfigValue
{
	private final IModule module;
	private final Field field;
	private final String key;
	private final ConfigFileAdapters fileAdapter;
	private final String guiName;
	private final ConfigGUIAdapters guiAdapter;
	private final Object defaultValue;

	public ConfigValue(IModule module, Field field, ConfigValueFactory factory)
	{
		this.module = module;
		this.field = field;
		this.key = factory.key();
		this.fileAdapter = factory.fileAdapter();
		this.guiName = factory.guiName();
		this.guiAdapter = factory.guiAdapter();
		Object def = null;
		try
		{
			def = field.get(module);
		} catch(Exception e)
		{
		}
		this.defaultValue = def;
	}

	public IModule getModule()
	{
		return module;
	}

	public Field getField()
	{
		return field;
	}

	public String getKey()
	{
		return key;
	}

	public String getConfigFileValue()
	{
		return this.fileAdapter.getValueWriter().apply(getValue());
	}

	public void readConfigFileValue(String fileValue)
	{
		this.setValue(this.fileAdapter.getValueReader().apply(fileValue, getDefaultValue()));
	}

	public String getGuiName()
	{
		return guiName;
	}

	public JComponent getGuiComponent()
	{
		return this.guiAdapter.getComponentProvider().apply(getValue());
	}

	public void acceptGuiComponent(JComponent component)
	{
		this.setValue(this.guiAdapter.getComponentAcceptor().apply(component, getDefaultValue()));
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public void setValue(Object value)
	{
		try
		{
			this.field.set(this.module, value);
		} catch(IllegalAccessException e)
		{
		}
	}

	public Object getValue()
	{
		try
		{
			return this.field.get(this.module);
		} catch(IllegalAccessException e)
		{
			return getDefaultValue();
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {FIELD})
	public @interface ConfigValueFactory
	{
		String key();

		ConfigFileAdapters fileAdapter();

		String guiName();

		ConfigGUIAdapters guiAdapter();
	}
}
