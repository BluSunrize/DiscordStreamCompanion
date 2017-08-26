package blusunrize.discordstreamcompanion;

import blusunrize.discordstreamcompanion.config.AlignmentStyle;
import blusunrize.discordstreamcompanion.config.ChannelNameStyle;
import blusunrize.discordstreamcompanion.config.Config;
import blusunrize.discordstreamcompanion.util.Utils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author BluSunrize
 * @since 25.08.2017
 */
public class DiscordStreamCompanion extends ListenerAdapter
{
	public final Logger logger = Logger.getLogger("DSC");
	private final File dataFolder;
	private final Config config;
	private JDA jdaInstance;

	private String userId;

	public DiscordStreamCompanion() throws Exception
	{
		this.dataFolder = new File(System.getenv("APPDATA")+"/DiscordStreamCompanion");
		this.dataFolder.mkdirs();
		this.setupLogger();
		this.config = new Config(this, dataFolder);
		this.setupTray();

		while(!this.config.isLoaded())
			Utils.threadSleep(100);

		try
		{
			this.jdaInstance = new JDABuilder(AccountType.CLIENT)
					.setToken(config.getToken())
					.addEventListener(this)
					.setStatus(OnlineStatus.IDLE)
					.setIdle(true)
					.buildAsync();
		} catch(LoginException|IllegalArgumentException|RateLimitedException e)
		{
			logger.severe(e.getLocalizedMessage());
		}
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

			final TrayIcon trayIcon = new TrayIcon(new ImageIcon(imageURL, "tray icon").getImage());
			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip("Discord Stream Companion");
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
			trayIcon.setPopupMenu(popup);

			tray.add(trayIcon);
		} catch(Exception e)
		{
			logger.log(Level.SEVERE, "Error setting up SystemTray", e);
		}
	}

	@Override
	public void onReady(ReadyEvent event)
	{
		SelfUser self = jdaInstance.getSelfUser();
		userId = getUserId(self);

		search:
		for(Guild guild : self.getMutualGuilds())
			for(VoiceChannel vc : guild.getVoiceChannels())
				for(Member m : vc.getMembers())
					if(m.getUser().getId().equals(self.getId()))
					{
						joinVoice(vc);
						break search;
					}

		logger.info("Connection has been established, Selfbot active for "+userId);
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
		this.config.openConfigGui().addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				resetVoiceHandlers();
			}
		});
	}

	HashMap<String, VoiceHandler> voiceHandlers = new HashMap<>();

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
	{
		if(userId!=null&&userId.equals(getUserId(event.getMember().getUser())))
			joinVoice(event.getChannelJoined());
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event)
	{
		if(userId!=null&&userId.equals(getUserId(event.getMember().getUser())))
		{
			leaveVoice(event.getChannelLeft());
			joinVoice(event.getChannelJoined());
		}
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
	{
		if(userId!=null&&userId.equals(getUserId(event.getMember().getUser())))
			leaveVoice(event.getChannelLeft());
	}

	private void joinVoice(VoiceChannel channel)
	{
		String key = channel.getGuild().getId()+":"+channel.getId();
		voiceHandlers.put(key, new VoiceHandler(this, channel, config.getUpdateFrequency()));
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
			ChannelNameStyle nameStyle = config.getShowChannelName();
			int width = 400;
			int height = (nameStyle==ChannelNameStyle.EXTENDED?112: nameStyle==ChannelNameStyle.SIMPLE?80: 32)+users.length*128;
			boolean right = config.getAlignment()==AlignmentStyle.RIGHT;
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D ig2 = bi.createGraphics();
			ig2.setFont(new Font("Helvetica", Font.BOLD, 36));
			FontMetrics fm = ig2.getFontMetrics();
			int fontHeight = fm.getHeight();
			int yBase = 32;
			if(nameStyle!=ChannelNameStyle.NONE)
			{
				yBase += 16;
				String name = channel.getName();
				String guild = channel.getGuild().getName();

				int w = Math.max(fm.stringWidth(name), fm.stringWidth(guild));

				//Positions
				int x = right?width-24-w: 24;
				//Background
				ig2.setPaint(config.getBgColor());
				int h = nameStyle==ChannelNameStyle.EXTENDED?fontHeight*2: fontHeight;
				ig2.fill(new RoundRectangle2D.Float(x-6, yBase-fm.getAscent()-3, w+12, h+6, 16, 16));
				//Channel Name
				ig2.setPaint(config.getTextColor());
				if(nameStyle==ChannelNameStyle.EXTENDED)
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

					if(config.getShowAvatars())
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
					ig2.setPaint(config.getBgColor());
					ig2.fill(new RoundRectangle2D.Float(x-6, y-fm.getAscent()-3, w+12, fm.getHeight()+6, 16, 16));
					//User Name
					ig2.setPaint(config.getTextColor());
					ig2.drawString(split[0], x, y);
				}

			ImageIO.write(bi, "PNG", new File(dataFolder, "audiochannel.png"));

		} catch(IOException e)
		{
			logger.log(Level.SEVERE, "Error writing voicechannel", e);
		}
	}

	private String getUserId(User user)
	{
		return user.getName()+"#"+user.getDiscriminator();
	}
}
