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
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package haushalt.auswertung.bloecke;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.11.23
 */

/*
 * 2010.11.23 Graphics2D in der Methode 'print' verwendet
 * 2004.08.22 Erste Version (2.0)
 */
public class LeererBlock extends AbstractBlock {

	private final int anzahlZeilen;

	public LeererBlock(final int anzahlZeilen) {
		this.anzahlZeilen = anzahlZeilen;
	}

	@Override
	public int paint(final Graphics g, final int xStart, final int yStart, final int breite) {
		g.setFont(getFont());
		return this.anzahlZeilen * g.getFontMetrics().getHeight();
	}

	@Override
	public int print(final Graphics2D g2d, final int zeile, final int xStart, final int yStart, final int hoehe,
			final int breite) {
		g2d.setFont(getFont());
		if (zeile >= this.anzahlZeilen) {
			return -1;
		}
		if (g2d.getFontMetrics().getHeight() > hoehe) {
			return 0;
		}
		return g2d.getFontMetrics().getHeight();
	}

}
