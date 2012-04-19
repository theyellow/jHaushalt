/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */
public class GemerkteBuchungenGlassPane extends JComponent {

	private static final long serialVersionUID = 1L;
	private String text = "";
	private int x = 0;
	private int y = 0;

	@Override
	public void paint(final Graphics g) {
		if (this.text != "") {
			final FontMetrics fontMetrics = g.getFontMetrics();
			final int dx = fontMetrics.stringWidth(this.text) + 1;
			final int dy = fontMetrics.getAscent() + fontMetrics.getDescent();
			g.setColor(Color.red);
			g.fillRect(this.x, this.y - fontMetrics.getHeight(), dx, dy);
			g.setColor(Color.black);
			g.drawString(this.text, this.x + 1, this.y + fontMetrics.getAscent() - fontMetrics.getHeight());
		}
	}

	/**
	 * @param text
	 */
	public void setText(final String text) {
		this.text = text;
		repaint();
	}

	/**
	 * @param xy
	 */
	public void setTextKoordinaten(final Point xy) {
		this.x = xy.x;
		this.y = xy.y;
		repaint();
	}

}
