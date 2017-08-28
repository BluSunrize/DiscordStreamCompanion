package blusunrize.discordstreamcompanion.modules.voicechannel;

import blusunrize.discordstreamcompanion.DiscordStreamCompanion;
import blusunrize.discordstreamcompanion.config.ConfigFileAdapters;
import blusunrize.discordstreamcompanion.config.ConfigGUIAdapters;
import blusunrize.discordstreamcompanion.config.ConfigValue.ConfigValueFactory;
import blusunrize.discordstreamcompanion.modules.IModule;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
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
 * @since 27.08.2017
 */
public class ModuleVoiceChannel extends ListenerAdapter implements IModule
{
	private DiscordStreamCompanion dsc;

	@Override
	public String getName()
	{
		return "Voice Channel";
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

	@ConfigValueFactory(key = "update_voice", fileAdapter = ConfigFileAdapters.INTEGER, guiName = "Refresh Frequency (ms)", guiAdapter = ConfigGUIAdapters.INTEGER)
	public int cfg_updateFrequency = 500;
	@ConfigValueFactory(key = "text_color", fileAdapter = ConfigFileAdapters.COLOR, guiName = "Text Color", guiAdapter = ConfigGUIAdapters.COLOR)
	public Color cfg_textColor = Color.lightGray;
	@ConfigValueFactory(key = "bg_color", fileAdapter = ConfigFileAdapters.COLOR, guiName = "Background Color", guiAdapter = ConfigGUIAdapters.COLOR)
	public Color cfg_bgColor = Color.darkGray;
	@ConfigValueFactory(key = "alignment", fileAdapter = ConfigFileAdapters.ENUM, guiName = "Alignment", guiAdapter = ConfigGUIAdapters.ENUM)
	public AlignmentStyle cfg_alignment = AlignmentStyle.LEFT;
	@ConfigValueFactory(key = "style_channelname", fileAdapter = ConfigFileAdapters.ENUM, guiName = "Channel Name", guiAdapter = ConfigGUIAdapters.ENUM)
	public ChannelNameStyle cfg_channelNameStyle = ChannelNameStyle.EXTENDED;
	@ConfigValueFactory(key = "show_avatars", fileAdapter = ConfigFileAdapters.BOOLEAN, guiName = "Show Avatars", guiAdapter = ConfigGUIAdapters.BOOLEAN)
	public boolean cfg_showAvatars = true;

	@Override
	public void onConfigChanged()
	{
		resetVoiceHandlers();
	}


	@Override
	public void onReady(ReadyEvent event)
	{
		SelfUser self = jda().getSelfUser();
		search:
		{
			for(Guild guild : self.getMutualGuilds())
				for(VoiceChannel vc : guild.getVoiceChannels())
					for(Member m : vc.getMembers())
						if(m.getUser().getId().equals(self.getId()))
						{
							logger().info("Found user in voicechannel "+guild.getName()+"-"+vc.getName());
							joinVoice(vc);
							break search;
						}
			logger().info("User was not found in any voicechannel");

		}
	}

	HashMap<String, VoiceHandler> voiceHandlers = new HashMap<>();

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
	{
		if(getDSC().isMe(event.getMember().getUser()))
			joinVoice(event.getChannelJoined());
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event)
	{
		if(getDSC().isMe(event.getMember().getUser()))
		{
			leaveVoice(event.getChannelLeft());
			joinVoice(event.getChannelJoined());
		}
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
	{
		if(getDSC().isMe(event.getMember().getUser()))
			leaveVoice(event.getChannelLeft());
	}

	private void joinVoice(VoiceChannel channel)
	{
		String key = channel.getGuild().getId()+":"+channel.getId();
		voiceHandlers.put(key, new VoiceHandler(this, channel, cfg_updateFrequency));
	}

	private void leaveVoice(VoiceChannel channel)
	{
		String key = channel.getGuild().getId()+":"+channel.getId();
		if(voiceHandlers.containsKey(key))
		{
			VoiceHandler vh = voiceHandlers.remove(key);
			vh.active = false;
		}
	}

	private void resetVoiceHandlers()
	{
		for(VoiceHandler vh : voiceHandlers.values())
			vh.last = null;
	}

	HashMap<String, BufferedImage> aviCache = new HashMap<>();

	public void writeVoiceChannel(VoiceChannel channel, String[] users)
	{
		try
		{
			int width = 400;
			int height = (cfg_channelNameStyle==ChannelNameStyle.EXTENDED?112: cfg_channelNameStyle==ChannelNameStyle.SIMPLE?80: 32)+users.length*128;
			boolean right = cfg_alignment==AlignmentStyle.RIGHT;
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D ig2 = bi.createGraphics();
			ig2.setFont(new Font("Helvetica", Font.BOLD, 36));
			FontMetrics fm = ig2.getFontMetrics();
			int fontHeight = fm.getHeight();
			int yBase = 32;
			if(cfg_channelNameStyle!=ChannelNameStyle.NONE)
			{
				yBase += 16;
				String name = channel.getName();
				String guild = channel.getGuild().getName();

				int w = Math.max(fm.stringWidth(name), fm.stringWidth(guild));

				//Positions
				int x = right?width-24-w: 24;
				//Background
				ig2.setPaint(cfg_bgColor);
				int h = cfg_channelNameStyle==ChannelNameStyle.EXTENDED?fontHeight*2: fontHeight;
				ig2.fill(new RoundRectangle2D.Float(x-6, yBase-fm.getAscent()-3, w+12, h+6, 16, 16));
				//Channel Name
				ig2.setPaint(cfg_textColor);
				if(cfg_channelNameStyle==ChannelNameStyle.EXTENDED)
				{
					ig2.drawString(guild, x, yBase);
					yBase += 32;
				}
				ig2.drawString(name, x, yBase);
				//Shift Offset
				yBase += 32;
			}
			int stringHeight = fm.getAscent();
			int pad = 24;
			for(int iUser = 0; iUser < users.length; iUser++)
				if(users[iUser]!=null&&users[iUser].indexOf(";") > 0)
				{
					String[] split = users[iUser].split(";");
					int y = yBase+iUser*128;

					if(cfg_showAvatars)
					{
						int x = right?width-pad-96: pad;
						//Get Avatar, cached to avoid unnecessary requests
						BufferedImage avi = aviCache.get(split[1]);
						if(avi==null)
						{
							URLConnection conn = new URL(split[1]).openConnection();
							conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
							conn.connect();
							avi = ImageIO.read(conn.getInputStream());
							aviCache.put(split[1], avi);
						}
						ig2.setClip(new Ellipse2D.Float(x, y, 96, 96));
						ig2.drawImage(avi, new AffineTransformOp(new AffineTransform(.75f, 0, 0, .75f, 1, 1), AffineTransformOp.TYPE_BICUBIC), x, y);
						ig2.setClip(null);
					}

					//Positions
					int w = fm.stringWidth(split[0]);
					int x = right?(width-pad-144-w): (pad+144);
					y += 48+stringHeight/4;
					//Background
					ig2.setPaint(cfg_bgColor);
					ig2.fill(new RoundRectangle2D.Float(x-6, y-fm.getAscent()-3, w+12, fm.getHeight()+6, 16, 16));
					//User Name
					ig2.setPaint(cfg_textColor);
					ig2.drawString(split[0], x, y);
				}

			ImageIO.write(bi, "PNG", new File(getDSC().getDataFolder(), "audiochannel.png"));

		} catch(IOException e)
		{
			logger().log(Level.SEVERE, "Error writing voicechannel", e);
		}
	}
}
