/*
 * Copyright (c) 2020, Truth Forger <http://github.com/Blackberry0Pie>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package bbp.equipmentscreenshot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Point;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

public class EquipmentBlowpipeOverlay
{
	private final Client client;

	private final int MAX_CHARGES = 16383;

	@Inject
	private EquipmentScreenshotPlugin plugin;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private EquipmentScreenshotConfig config;

	@Inject
	EquipmentBlowpipeOverlay(Client client)
	{
		this.client = client;
	}

	public void renderBlowpipeOverlay(Graphics2D graphics, Point location)
	{
		BufferedImage dartImage = null;
		Integer dartMat = plugin.DART_MATERIALS.get(plugin.getDartID());
		if (dartMat != null)
		{
			if (dartMat == -1)
			{
				dartImage = ImageUtil.getResourceStreamFromClass(EquipmentBlowpipeOverlay.class, "Dragon_bar.png");
			}
			else if (dartMat == -2)
			{
				dartImage = ImageUtil.getResourceStreamFromClass(EquipmentBlowpipeOverlay.class, "Black_bar.png");
			}
			else if (itemManager.getImage(dartMat) != null)
			{
				dartImage = itemManager.getImage(dartMat);
			}
		}
		BufferedImage scaleImage = itemManager.getImage(ItemID.ZULRAHS_SCALES, 5, false);
		BufferedImage bi;
		for (int i = 2; i >= 0; i -= 2)
		{
			int amount;
			int imageSize;

			if (i == 0)
			{
				graphics.setFont(FontManager.getRunescapeFont());
				imageSize = 23;
				amount = plugin.getDartCount();
				bi = dartImage;
			}
			else
			{
				graphics.setFont(FontManager.getRunescapeSmallFont());
				imageSize = 17;
				amount = plugin.getScaleCount();
				bi = scaleImage;
			}
			if (bi != null && amount != 0)
			{
				BufferedImage resizedImg = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = resizedImg.createGraphics();
				g.drawImage(bi, 0, 0, imageSize, imageSize, null);
				g.dispose();
				OverlayUtil.renderImageLocation(graphics,
						new Point(location.getX() - 4, location.getY() + graphics.getFontMetrics().getHeight() * (i == 2 ? 1 : 0) - 7 + (i == 2 ? 11 : 0)),
						resizedImg);

				graphics.setColor(Color.black);
				graphics.drawString("" + formatNumber(amount), location.getX() + 14,
						location.getY() + 13 - (i == 2 ? 3 : 0) + (graphics.getFontMetrics().getHeight() - 1) * i);

				graphics.setColor(config.textColor());
				graphics.drawString("" + formatNumber(amount), location.getX() + 13,
						location.getY() + 12 - (i == 2 ? 3 : 0) + (graphics.getFontMetrics().getHeight() - 1) * i);
			}
		}
	}

	private static String formatNumber(int amount)
	{
		return amount < 1000 ? String.valueOf(amount) : amount / 1000 + "K";
	}
}

