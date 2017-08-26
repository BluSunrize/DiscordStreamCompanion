package util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * @author BluSunrize
 * @since 26.08.2017
 */
public class BlankImageOp implements BufferedImageOp
{
	public static final BlankImageOp INSTANCE = new BlankImageOp();

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest)
	{
		return src;
	}

	@Override
	public Rectangle2D getBounds2D(BufferedImage src)
	{
		return src.getRaster().getBounds();
	}

	@Override
	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM)
	{
		return src;
	}

	@Override
	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt)
	{
		return srcPt;
	}

	@Override
	public RenderingHints getRenderingHints()
	{
		return null;
	}
}
