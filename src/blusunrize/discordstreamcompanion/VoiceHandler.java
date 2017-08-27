package blusunrize.discordstreamcompanion;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import blusunrize.discordstreamcompanion.util.Utils;

/**
 * Copyright 2017 BluSunrize
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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
