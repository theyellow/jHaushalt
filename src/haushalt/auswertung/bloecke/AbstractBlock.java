/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
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

import java.awt.Font;
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

public abstract class AbstractBlock {

	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private double rand = 0.02D;

	public abstract int paint(Graphics g, int xStart, int yStart, int breite);

	public abstract int print(Graphics2D g2d, int zeile, int xStart, int yStart, int hoehe, int breite);

	protected Font getFont() {
		return this.font;
	}

	public void setFont(final Font font) {
		this.font = font;
	}

	public void setRelRand(final double rand) {
		if (rand > 0.5D) {
			this.rand = 0.5D;
		} else if (rand < 0.0D) {
			this.rand = 0.0D;
		} else {
			this.rand = rand;
		}
	}

	protected int getAbsRand(final int breite) {
		return (int) (this.rand * breite);
	}

}
