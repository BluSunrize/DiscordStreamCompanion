package blusunrize.discordstreamcompanion.config;

import blusunrize.discordstreamcompanion.util.Utils;

import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Function;

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
public enum ConfigFileAdapters
{
	BOOLEAN(
			(value, def) -> "false".equalsIgnoreCase(value)?false: def,
			value -> String.valueOf((boolean)value)
	),
	INTEGER(
			(value, def) -> Utils.parseInteger(value, (int)def),
			value -> String.valueOf((int)value)
	),
	ENUM(
			(value, def) -> Utils.parseEnum(value.toUpperCase(), (Enum)def),
			value -> ((Enum)value).name().toLowerCase()
	),
	COLOR(
			(value, def) -> Utils.parseColor(value, (Color)def),
			value -> "#"+Integer.toHexString(((Color)value).getRGB())
	);

	private final BiFunction<String, Object, Object> valueReader;
	private final Function<Object, String> valueWriter;

	/**
	 * @param valueReader Function to read the value from the String in the config and a default value
	 * @param valueWriter Function to write the value to a String in the config file
	 */
	ConfigFileAdapters(BiFunction<String, Object, Object> valueReader, Function<Object, String> valueWriter)
	{
		this.valueReader = valueReader;
		this.valueWriter = valueWriter;
	}

	public BiFunction<String, Object, Object> getValueReader()
	{
		return valueReader;
	}

	public Function<Object, String> getValueWriter()
	{
		return valueWriter;
	}
}
