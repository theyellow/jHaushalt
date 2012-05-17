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

package haushalt.auswertung.bloecke;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.11.23
 */

/*
 * 2010.11.23 Graphics2D in der Methode 'print' verwendet
 * 2004.08.22 Erste Version (2.0)
 */
public class TextBlock extends AbstractBlock {

	private final String text;
	private List<String> printZeilen = null;

	public TextBlock(final String text) {
		this.text = text;
	}

	private List<String> textUmbrechen(final Graphics g, final int breite) {
		final ArrayList<String> zeilen = new ArrayList<String>();
		String zeile = "";
		final StringTokenizer woerter = new StringTokenizer(this.text, " ", true);
		while (woerter.hasMoreTokens()) {
			final String wort = woerter.nextToken();
			final byte[] zeichen = (zeile + wort).getBytes();
			if (g.getFontMetrics().bytesWidth(zeichen, 0, zeichen.length) > breite) {
				zeilen.add(zeile);
				zeile = "";
			}
			zeile += wort;
		}
		zeilen.add(zeile);
		return zeilen;
	}

	@Override
	public int paint(final Graphics g, final int xStart, final int yStart, final int breite) {
		g.setFont(getFont());
		final int textBreite = breite - 2 * getAbsRand(breite);
		final List<String> zeilen = textUmbrechen(g, textBreite);
		int y = yStart;
		final int x = xStart + getAbsRand(breite);
		for (int i = 0; i < zeilen.size(); i++) {
			final int yZeile = y + g.getFontMetrics().getHeight() - g.getFontMetrics().getDescent();
			g.drawString("" + zeilen.get(i), x, yZeile);
			y += g.getFontMetrics().getHeight();
		}
		return zeilen.size() * g.getFontMetrics().getHeight();
	}

	@Override
	public int print(final Graphics2D g2d, final int zeile, final int xStart, final int yStart, final int hoehe, final int breite) {
		g2d.setFont(getFont());
		final int textBreite = breite - 2 * getAbsRand(breite);
		if (this.printZeilen == null) {
			this.printZeilen = textUmbrechen(g2d, textBreite);
		}
		if (zeile >= this.printZeilen.size()) {
			return -1;
		}
		if (g2d.getFontMetrics().getHeight() > hoehe) {
			return 0;
		}
		final int x = xStart + getAbsRand(breite);
		final int y = yStart + g2d.getFontMetrics().getHeight() - g2d.getFontMetrics().getDescent();
		g2d.drawString("" + this.printZeilen.get(zeile), x, y);
		return g2d.getFontMetrics().getHeight();
	}

}
