package blusunrize.discordstreamcompanion.modules;

import blusunrize.discordstreamcompanion.DiscordStreamCompanion;
import net.dv8tion.jda.core.JDA;

import java.util.logging.Logger;

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
public interface IModule
{
	String getName();

	void setDSC(DiscordStreamCompanion dsc);

	DiscordStreamCompanion getDSC();

	default Logger logger()
	{
		return getDSC().getLogger();
	}

	default JDA jda()
	{
		return getDSC().getJDAInstance();
	}

	default String userId()
	{
		return getDSC().getUserId();
	}

	void onConfigChanged();
}
