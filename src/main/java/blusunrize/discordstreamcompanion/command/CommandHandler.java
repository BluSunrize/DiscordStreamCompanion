package blusunrize.discordstreamcompanion.command;

import blusunrize.discordstreamcompanion.DiscordStreamCompanion;
import blusunrize.discordstreamcompanion.modules.IModule;
import net.dv8tion.jda.core.entities.Message;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.function.BiConsumer;

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
public class CommandHandler
{
	public final String commandPrefix = "!dsc.";

	private final DiscordStreamCompanion dsc;
	private HashMap<String, BiConsumer<Message, String[]>> commands = new HashMap<>();

	public CommandHandler(DiscordStreamCompanion dsc)
	{
		this.dsc = dsc;

		for(IModule module : dsc.getModules())
			for(Method method : module.getClass().getDeclaredMethods())
				if(method.isAnnotationPresent(CommandMethod.class))
				{
					if(!Modifier.isPublic(method.getModifiers()))
						throw new RuntimeException("Error accessing method "+method.getName()+", command methodes must be public");
					Class[] parameters = method.getParameterTypes();
					if(parameters.length!=2||parameters[0]!=Message.class||parameters[1]!=String[].class)
						throw new RuntimeException("Error handling method "+method.getName()+", command methodes must have two parameters, Message and String[]");

					CommandMethod annotation = method.getAnnotation(CommandMethod.class);
					BiConsumer<Message, String[]> consumer = (message, strings) -> {
						try
						{
							method.invoke(module, message, strings);
						} catch(Exception e)
						{
						}
					};
//					BiConsumer<Message, String[]> consumer = (message, strings) -> method;

					this.commands.put(annotation.command(), consumer);
					if(annotation.altCommands()!=null&&annotation.altCommands().length > 0)
						for(String alt : annotation.altCommands())
							this.commands.put(alt, consumer);
				}
	}

	public void onCommand(Message message, String content)
	{
		String[] split = content.substring(commandPrefix.length()).split(" ", 2);
		if(!commands.containsKey(split[0]))
			this.dsc.getLogger().info("Unknown command "+split[0]);
		else
			this.commands.get(split[0]).accept(message, split);
		message.delete().complete();
	}
}
