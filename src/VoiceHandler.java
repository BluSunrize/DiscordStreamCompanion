import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import util.Utils;

/**
 * @author BluSunrize
 * @since 26.08.2017
 */
public class VoiceHandler extends Thread
{
	private final DiscordStreamCompanion dsc;
	private final VoiceChannel channel;
	private final int updateFrequency;
	public boolean active = true;

	public VoiceHandler(final DiscordStreamCompanion dsc, final VoiceChannel channel, int updateFrequency)
	{
		this.dsc = dsc;
		this.channel = channel;
		this.updateFrequency = updateFrequency;
		this.setDaemon(true);
		this.start();
	}

	String last = "";

	@Override
	public void run()
	{
		while(active)
		{
			String state = "";
			for(Member member : channel.getMembers())
			{
				state += member.getEffectiveName()+";";
				state += member.getUser().getEffectiveAvatarUrl()+"\n";
			}
			if(!state.equals(last))
			{
				last = state;
				dsc.writeVoiceChannel(this.channel, state.split("\n"));
			}
			Utils.threadSleep(updateFrequency);
		}
	}
}
