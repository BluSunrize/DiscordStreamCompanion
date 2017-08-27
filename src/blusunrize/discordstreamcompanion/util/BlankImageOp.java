package blusunrize.discordstreamcompanion.util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

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
