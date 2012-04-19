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

import haushalt.auswertung.FarbPaletten;
import haushalt.daten.Euro;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2004.08.22
 */
public class TortenBlock extends AbstractGraphikBlock {

	private final Euro[] einnahmen;
	private final Euro[] ausgaben;
	private final Euro gesamtEinnahmen;
	private final Euro gesamtAusgaben;
	private final String farbschema;

	public TortenBlock(final String farbschema, final Euro[] einnahmen,
			final Euro gesamtEinnahmen, final Euro[] ausgaben, final Euro gesamtAusgaben) {
		this.farbschema = farbschema;
		this.einnahmen = einnahmen;
		this.gesamtEinnahmen = gesamtEinnahmen;
		this.ausgaben = ausgaben;
		this.gesamtAusgaben = gesamtAusgaben;
	}

	@Override
	public int paint(final Graphics g, final int xStart, final int yStart, final int breite) {
		final Graphics2D g2 = (Graphics2D) g;
		final int rand = getAbsRand(breite);
		final int durchmesser = getHoehe(breite);
		final int xEinnahmen = xStart + rand + (breite - 2 * getAbsRand(breite)) / 40;
		final int xAusgaben = xStart + rand + (breite - 2 * getAbsRand(breite)) * 21 / 40;
		Euro[] werte;
		Euro gesamt;
		int x;
		double winkel;
		double winkelSumme;

		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				werte = this.einnahmen;
				gesamt = this.gesamtEinnahmen;
				x = xEinnahmen;
			}
			else {
				werte = this.ausgaben;
				gesamt = this.gesamtAusgaben;
				x = xAusgaben;
			}

			winkelSumme = 0.0D;

			for (int j = 0; (j < werte.length) && (werte[j] != null); j++) {
				g2.setPaint(FarbPaletten.getFarbe(j, this.farbschema));
				winkel = werte[j].toDouble() / gesamt.toDouble() * 360.0D;
				g2.fill(new Arc2D.Double(x, yStart,
									durchmesser, durchmesser,
									winkelSumme, winkel, Arc2D.PIE));
				winkelSumme += winkel;
			}
		}
		return durchmesser;
	}

	@Override
	protected int getHoehe(final int breite) {
		return (breite - 2 * getAbsRand(breite)) * 18 / 40;
	}

}
