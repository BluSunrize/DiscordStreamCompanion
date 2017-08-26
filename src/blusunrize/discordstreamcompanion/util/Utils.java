package blusunrize.discordstreamcompanion.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
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
			e.printStackTrace();
		}
	}
}
