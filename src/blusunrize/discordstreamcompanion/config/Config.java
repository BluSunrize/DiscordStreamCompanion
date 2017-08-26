package blusunrize.discordstreamcompanion.config;

import blusunrize.discordstreamcompanion.DiscordStreamCompanion;
import blusunrize.discordstreamcompanion.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;

/**
 * @author BluSunrize
 * @since 25.08.2017
 */
public class Config
{
	private File configFile;

	private String token;
	private int updateFrequency = 500;
	private Color textColor = Color.lightGray;
	private Color bgColor = Color.darkGray;
	private AlignmentStyle alignment = AlignmentStyle.LEFT;
	private boolean showAvatars = true;
	private ChannelNameStyle channelNameStyle = ChannelNameStyle.EXTENDED;

	private boolean isLoaded = false;

	public Config(DiscordStreamCompanion dsc, File dataFolder) throws Exception
	{
		configFile = new File(dataFolder, "config.cfg");
		if(configFile.exists())
		{
			dsc.logger.info("Loading Config");
			load();
		}
		else
		{
			dsc.logger.info("No Config Present, creating new one");
			createConfig();
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

	public void setUpdateFrequency(int updateFrequency)
	{
		this.updateFrequency = updateFrequency;
	}

	public int getUpdateFrequency()
	{
		return updateFrequency;
	}

	public Color getTextColor()
	{
		return textColor;
	}

	public void setTextColor(Color textColor)
	{
		this.textColor = textColor;
	}

	public Color getBgColor()
	{
		return bgColor;
	}

	public void setBgColor(Color bgColor)
	{
		this.bgColor = bgColor;
	}

	public AlignmentStyle getAlignment()
	{
		return alignment;
	}

	public void setAlignment(AlignmentStyle alignment)
	{
		this.alignment = alignment;
	}

	public boolean getShowAvatars()
	{
		return showAvatars;
	}

	public void setShowAvatars(boolean showAvatars)
	{
		this.showAvatars = showAvatars;
	}

	public ChannelNameStyle getShowChannelName()
	{
		return channelNameStyle;
	}

	public void setShowChannelName(ChannelNameStyle channelNameStyle)
	{
		this.channelNameStyle = channelNameStyle;
	}

	private void load() throws Exception
	{
		List<String> lines = Files.readAllLines(configFile.toPath());
		for(String line : lines)
		{
			String[] parts = line.split("=", 2);
			String key = parts[0].trim().toLowerCase();
			String value = parts.length > 1?parts[1].trim(): null;
			switch(key)
			{
				case "token":
					token = value;
					break;
				case "update_voice":
					updateFrequency = Integer.parseInt(value.replaceAll("\\D", ""));
					break;
				case "text_color":
					textColor = Utils.parseColor(value, Color.lightGray);
					break;
				case "bg_color":
					bgColor = Utils.parseColor(value, Color.darkGray);
					break;
				case "alignment":
					alignment = (AlignmentStyle)Utils.parseEnum(value.toUpperCase(), AlignmentStyle.LEFT);
					break;
				case "show_avatars":
					showAvatars = "false".equalsIgnoreCase(value)?false: true;
					break;
				case "style_channelname":
					channelNameStyle = (ChannelNameStyle)Utils.parseEnum(value.toUpperCase(), ChannelNameStyle.EXTENDED);
					break;
			}
		}
		this.isLoaded = true;
	}

	private void createConfig()
	{
		openConfigGui().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public JFrame openConfigGui()
	{
		JFrame frame = new JFrame("Configure DiscordStreamCompanion");
		GuiConfig gui = new GuiConfig().setFrame(frame).setConfig(this);
		frame.setContentPane(gui.getWindow());
		frame.pack();
		frame.setVisible(true);
		return frame;
	}

	public void saveConfig()
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
			writer.write("token="+token);
			writer.newLine();
			writer.write("update_voice="+updateFrequency);
			writer.newLine();
			writer.write("text_color=#"+Integer.toHexString(textColor.getRGB()));
			writer.newLine();
			writer.write("bg_color=#"+Integer.toHexString(bgColor.getRGB()));
			writer.newLine();
			writer.write("alignment="+alignment.name().toLowerCase());
			writer.newLine();
			writer.write("show_avatars="+showAvatars);
			writer.newLine();
			writer.write("style_channelname="+channelNameStyle.name().toLowerCase());
			writer.close();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
