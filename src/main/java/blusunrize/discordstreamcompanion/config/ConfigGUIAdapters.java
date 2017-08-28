package blusunrize.discordstreamcompanion.config;

import blusunrize.discordstreamcompanion.util.Utils;

import javax.swing.*;
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
public enum ConfigGUIAdapters
{
	BOOLEAN(
			value -> new JCheckBox((String)null, (boolean)value),
			(component, def) -> ((JCheckBox)component).isSelected()
	),
	INTEGER(
			value -> new JTextField(String.valueOf((int)value)),
			(component, def) -> Utils.parseInteger(((JTextField)component).getText(), (int)def)
	),
	ENUM(
			value -> {
				JComboBox dropdown = new JComboBox(value.getClass().getEnumConstants());
				dropdown.setSelectedIndex(((Enum)value).ordinal());
				return dropdown;
			},
			(component, def) -> def.getClass().getEnumConstants()[((JComboBox)component).getSelectedIndex()]
	),
	COLOR(
			value -> new JTextField("#"+Integer.toHexString(((Color)value).getRGB())),
			(component, def) -> Utils.parseColor(((JTextField)component).getText(), (Color)def)
	);

	private final Function<Object, JComponent> componentProvider;
	private final BiFunction<JComponent, Object, Object> componentAcceptor;

	/**
	 * @param componentProvider Function to provide a JComponent for hte configuration GUI. Input is the value
	 * @param componentAcceptor Function to provide the value from a JComponent and a default value set in the ConfigValueFactory annotation
	 */
	ConfigGUIAdapters(Function<Object, JComponent> componentProvider, BiFunction<JComponent, Object, Object> componentAcceptor)
	{
		this.componentProvider = componentProvider;
		this.componentAcceptor = componentAcceptor;
	}


	public Function<Object, JComponent> getComponentProvider()
	{
		return componentProvider;
	}

	public BiFunction<JComponent, Object, Object> getComponentAcceptor()
	{
		return componentAcceptor;
	}
}
