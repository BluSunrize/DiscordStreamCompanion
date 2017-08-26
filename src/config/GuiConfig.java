package config;

import util.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * @author BluSunrize
 * @since 26.08.2017
 */
public class GuiConfig
{
	private Config config;

	private JFrame frame;
	private JPanel window;

	private JButton button_save;
	private JButton button_exit;
	private JPasswordField field_token;
	private JCheckBox checkbox_token;
	private JTextField field_frequency;
	private JTextField field_color;
	private JComboBox select_alignment;
	private JCheckBox checkbox_avatars;
	private JCheckBox checkbox_name;
	private JTextField field_bgcolor;
	private JComboBox select_channelName;

	public GuiConfig()
	{
		checkbox_token.addActionListener(e -> field_token.setEchoChar(checkbox_token.isSelected()?0: '•'));

		button_exit.addActionListener(e -> {
			Utils.closeJFrame(frame);
		});

		button_save.addActionListener(e -> {
			this.saveConfig();
			frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			Utils.closeJFrame(frame);
		});
	}

	public GuiConfig setFrame(JFrame frame)
	{
		this.frame = frame;
		return this;
	}

	public JPanel getWindow()
	{
		return window;
	}

	public GuiConfig setConfig(Config config)
	{
		this.config = config;

		this.field_token.setText(this.config.getToken());
		this.field_frequency.setText(""+this.config.getUpdateFrequency());
		this.field_color.setText("#"+Integer.toHexString(this.config.getTextColor().getRGB()));
		this.field_bgcolor.setText("#"+Integer.toHexString(this.config.getBgColor().getRGB()));
		this.select_alignment.setSelectedIndex(this.config.getAlignment().ordinal());
		this.select_channelName.setSelectedIndex(this.config.getShowChannelName().ordinal());
		this.checkbox_avatars.setSelected(this.config.getShowAvatars());
		return this;
	}

	private void saveConfig()
	{
		if(this.config!=null)
		{
			this.config.setToken(new String(field_token.getPassword()));
			this.config.setUpdateFrequency(Utils.parseInteger(field_frequency.getText(), 500));
			this.config.setTextColor(Utils.parseColor(field_color.getText(), Color.lightGray));
			this.config.setBgColor(Utils.parseColor(field_bgcolor.getText(), Color.darkGray));
			this.config.setAlignment(AlignmentStyle.values()[select_alignment.getSelectedIndex()]);
			this.config.setShowChannelName(ChannelNameStyle.values()[select_channelName.getSelectedIndex()]);
			this.config.setShowAvatars(checkbox_avatars.isSelected());

			this.config.saveConfig();

			if(!this.config.isLoaded())
				this.config.setLoaded(true);
		}
	}

	private void createUIComponents()
	{
		select_alignment = new JComboBox(AlignmentStyle.values());
		select_channelName = new JComboBox(ChannelNameStyle.values());
	}
}
