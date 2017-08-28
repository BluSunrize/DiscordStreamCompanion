package blusunrize.discordstreamcompanion.config;

import blusunrize.discordstreamcompanion.DiscordStreamCompanion;
import blusunrize.discordstreamcompanion.modules.IModule;
import blusunrize.discordstreamcompanion.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Map.Entry;

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
 * @since 27.08.2017
 */
public class GuiConfig extends JFrame
{
	public GuiConfig(DiscordStreamCompanion dsc, Config config)
	{
		super("Configure DiscordStreamCompanion");

//		JPanel content = new JPanel();
//		this.setLayout(new GridLayout(2,1));
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(400, 200));

		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel base = new JPanel();
		base.add(new JLabel("Token"));
		JPanel token = new JPanel();
		JPasswordField jpf = new JPasswordField(config.getToken());
		JCheckBox checkbox_token = new JCheckBox("Show");
		checkbox_token.addActionListener(e -> jpf.setEchoChar(checkbox_token.isSelected()?0: '•'));
		token.add(jpf);
		token.add(checkbox_token);
		base.add(token);

		tabbedPane.add("Base", base);

		for(Entry<IModule, ConfigValue[]> entry : config.getModuleValues().entrySet())
			tabbedPane.add(entry.getKey().getName(), createTab(entry.getValue()));

		this.add(tabbedPane, BorderLayout.NORTH);

		JButton button_save = new JButton("Save");
		button_save.addActionListener(e -> {
			saveConfig(dsc, config);
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			Utils.closeJFrame(this);
		});

		JButton button_exit = new JButton("Close");
		button_exit.addActionListener(e -> {
			Utils.closeJFrame(this);
		});
		JPanel buttons = new JPanel();
		buttons.add(button_save);
		buttons.add(button_exit);
		this.add(buttons, BorderLayout.SOUTH);

//		this.setContentPane(content);
		this.pack();
		this.setVisible(true);
	}

	private JPanel createTab(ConfigValue... options)
	{
		JPanel keys = new JPanel();
		keys.setLayout(new GridLayout(options.length, 1));
		JPanel values = new JPanel();
		values.setLayout(new GridLayout(options.length, 1));
		for(ConfigValue cfgEntry : options)
		{
			keys.add(new JLabel(cfgEntry.getGuiName()));
			values.add(cfgEntry.getGuiComponent());
		}
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.add(keys);
		panel.add(values);
		return panel;
	}
//
//	private JPanel createTab(Map<String, JComponent> options)
//	{
//		JPanel keys = new JPanel();
//		keys.setLayout(new GridLayout(options.size(),1));
//		JPanel values = new JPanel();
//		values.setLayout(new GridLayout(options.size(),1));
//		for(Entry<String, JComponent> entry : options.entrySet())
//		{
//			keys.add(new JLabel(entry.getKey()));
//			values.add(entry.getValue());
//		}
//		JPanel panel = new JPanel();
//		panel.setLayout(new GridLayout(1,2));
//		panel.add(keys);
//		panel.add(values);
//		return panel;
//	}

	private void saveConfig(DiscordStreamCompanion dsc, Config config)
	{
		config.saveConfig(dsc);
	}
}
