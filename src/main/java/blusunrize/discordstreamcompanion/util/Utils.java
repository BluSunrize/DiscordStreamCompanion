package blusunrize.discordstreamcompanion.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

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
 * @since 26.08.2017
 */
public class Utils
{
	public static int parseInteger(String s, int fallback)
	{
		try
		{
			return Integer.parseInt(s.replaceAll("\\D", ""));
		} catch(Exception e)
		{
			return fallback;
		}
	}

	public static Enum parseEnum(String s, Enum fallback)
	{
		try
		{
			return Enum.valueOf(fallback.getClass(), s);
		} catch(Exception e)
		{
			return fallback;
		}
	}


	public static Color parseColor(String s, Color fallback)
	{
		Color c = Color.getColor(s);
		if(c!=null)
			return c;
		try
		{
			return new Color(Integer.decode(s));
		} catch(Exception e)
		{
		}
		return fallback;
	}

	public static void closeJFrame(JFrame frame)
	{
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		frame.setVisible(false);
		frame.dispose();
	}

	public static void threadSleep(int ms)
	{
		try
		{
			Thread.sleep(ms);
		} catch(InterruptedException e)
		{
		}
	}
}
