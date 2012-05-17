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
 * @since 2.5.4
 */

/*
 * 2010.11.23 Graphics2D in der Methode 'print' verwendet
 * 2008.04.15 Erste Version (2.5.4)
 */
public abstract class AbstractTabelleBlock extends AbstractBlock {

	public enum Ausrichtung {
		LINKS,
		CENTER,
		RECHTS
	};

	private String hgFarbe = "Weiß";
	private String linienFarbe = "Schwarz";
	private final int zeilen;

	public AbstractTabelleBlock(final int zeilen) {
		this.zeilen = zeilen;
	}

	@Override
	public int paint(final Graphics g, final int xStart, final int yStart, final int breite) {
		g.setFont(getFont());
		int y = yStart;
		for (int i = 0; i < this.zeilen; i++) {
			zeichneZeile(i, g, xStart, y, breite);
			y += g.getFontMetrics().getHeight();
		}
		return this.zeilen * g.getFontMetrics().getHeight();
	}

	/**
	 * Druckt eine Zeile der Tabelle
	 * 
	 * @return Hoehe der gedruckten Zeile; 0 = Zeile passt nicht; -1 = keine
	 *         weiteren Zeilen
	 */
	@Override
	public int print(final Graphics2D g2d, final int zeile, final int xStart, final int yStart, final int hoehe, final int breite) {
		g2d.setFont(getFont());
		if (g2d.getFontMetrics().getHeight() > hoehe) {
			return 0;
		}
		if (zeile >= this.zeilen) {
			return -1;
		}
		zeichneZeile(zeile, g2d, xStart, yStart, breite);
		return g2d.getFontMetrics().getHeight();
	}

	protected abstract void zeichneZeile(int zeile, Graphics g, int xStart, int yStart, int breite);

	public void setHgFarbe(final String hgFarbe) {
		this.hgFarbe = hgFarbe;
	}

	public void setLinienFarbe(final String linienFarbe) {
		this.linienFarbe = linienFarbe;
	}

	public String getHgFarbe() {
		return hgFarbe;
	}

	public String getLinienFarbe() {
		return linienFarbe;
	}

}
