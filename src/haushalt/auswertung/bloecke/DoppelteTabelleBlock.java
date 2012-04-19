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

import haushalt.auswertung.FarbPaletten;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2009.07.27
 * @since 2.5.4
 */

/*
 * 2009.07.28 BugFix:Leere Tabelle wurde nicht abgefangen
 */
public class DoppelteTabelleBlock extends AbstractTabelleBlock {

	private static final boolean DEBUG = false;

	private final String[][] tabelleLinks;
	private final String[][] tabelleRechts;
	private final int spaltenLinks;
	private final int spaltenRechts;
	private double[] relTabsLinks;
	private double[] relTabsRechts;
	private Ausrichtung[] ausrichtungLinks;
	private Ausrichtung[] ausrichtungRechts;
	private final double relBreiteSpalteLinks = 48.0D;
	private final double relBreiteSpalteRechts = 48.0D;
	private final double relSpaltenAbstand = 100.0D - this.relBreiteSpalteLinks - this.relBreiteSpalteRechts;

	public DoppelteTabelleBlock(final String[][] tabelleLinks, final String[][] tabelleRechts) {
		super(tabelleLinks.length > tabelleRechts.length ? tabelleLinks.length : tabelleRechts.length);
		this.tabelleLinks = tabelleLinks;
		this.tabelleRechts = tabelleRechts;
		this.spaltenLinks = (tabelleLinks.length == 0) ? 0 : tabelleLinks[0].length;
		this.spaltenRechts = (tabelleRechts.length == 0) ? 0 : tabelleRechts[0].length;

		// Standard-Tabs gleichmäßig verteilen
		this.relTabsLinks = new double[this.spaltenLinks];
		double abstand = this.relBreiteSpalteLinks / this.spaltenLinks;
		for (int i = 0; i < this.spaltenLinks; i++) {
			this.relTabsLinks[i] = i * abstand;
		}
		this.relTabsRechts = new double[this.spaltenRechts];
		abstand = this.relBreiteSpalteRechts / this.spaltenRechts;
		for (int i = 0; i < this.spaltenRechts; i++) {
			this.relTabsRechts[i] = i * abstand;
		}

		// Standard-Ausrichtung setzen
		this.ausrichtungLinks = new Ausrichtung[this.spaltenLinks];
		Arrays.fill(this.ausrichtungLinks, Ausrichtung.LINKS);
		this.ausrichtungRechts = new Ausrichtung[this.spaltenRechts];
		Arrays.fill(this.ausrichtungRechts, Ausrichtung.LINKS);
	}

	@Override
	protected void zeichneZeile(final int zeile, final Graphics g, final int xStart, final int yStart, final int breite) {
		final int tabellenBreiteLinks = (int) ((breite - 2 * getAbsRand(breite)) * this.relBreiteSpalteLinks / 100.0D);
		final int tabellenBreiteRechts = (int) ((breite - 2 * getAbsRand(breite)) * this.relBreiteSpalteRechts / 100.0D);
		final int tabellenBreiteAbstand = (int) ((breite - 2 * getAbsRand(breite)) * this.relSpaltenAbstand / 100.0D);

		// Berechnung der absoluten Abstände (=Tabs) der linken Spalte
		final int[] absTabsLinks = new int[this.spaltenLinks + 1];
		for (int i = 0; i < this.spaltenLinks; i++) {
			absTabsLinks[i] = (int) (tabellenBreiteLinks * this.relTabsLinks[i] / 100.0D + getAbsRand(breite));
		}
		absTabsLinks[this.spaltenLinks] = tabellenBreiteLinks + getAbsRand(breite);

		// Berechnung der absoluten Abstände (=Tabs) der rechten Spalte
		final int[] absTabsRechts = new int[this.spaltenRechts + 1];
		for (int i = 0; i < this.spaltenRechts; i++) {
			absTabsRechts[i] = (int) (tabellenBreiteRechts * this.relTabsRechts[i] / 100.0D + tabellenBreiteAbstand
					+ tabellenBreiteLinks + getAbsRand(breite));
		}
		absTabsRechts[this.spaltenRechts] = breite - getAbsRand(breite);

		final int textHoehe = g.getFontMetrics().getHeight();

		// Tabelle 1 (links):
		if ((zeile < this.tabelleLinks.length) && (this.tabelleLinks[zeile][0] != null)) {
			for (int x = 0; x < this.spaltenLinks; x++) {
				int zellenBreite = absTabsLinks[x + 1] - absTabsLinks[x];
				g.setColor(FarbPaletten.getFarbe(zeile, this.hgFarbe));
				g.fillRect(xStart + absTabsLinks[x], yStart, zellenBreite, textHoehe);
				g.setColor(FarbPaletten.getFarbe(zeile, this.linienFarbe));
				g.drawRect(xStart + absTabsLinks[x], yStart, zellenBreite, textHoehe);

				zellenBreite -= 4;
				String wort = this.tabelleLinks[zeile][x];
				while ((g.getFontMetrics().stringWidth(wort) > zellenBreite) && (wort.length() > 1)) {
					wort = wort.substring(0, wort.length() - 1);
				}
				int delta = 0;
				switch (this.ausrichtungLinks[x]) {
				case LINKS:
					delta = 2;
					break;
				case CENTER:
					delta = (zellenBreite - g.getFontMetrics().stringWidth(wort)) / 2 + 2;
					break;
				case RECHTS:
					delta = zellenBreite - g.getFontMetrics().stringWidth(wort) + 2;
					break;
				}
				g.setColor(Color.black);
				g.drawString(wort, xStart + absTabsLinks[x] + delta, yStart + textHoehe
						- g.getFontMetrics().getDescent());
			}
			if (DEBUG) {
				System.out.println("Z" + zeile + ": " + this.tabelleLinks[zeile][0]);
			}
		}

		// Tabelle 2 (rechts):
		if ((zeile < this.tabelleRechts.length) && (this.tabelleRechts[zeile][0] != null)) {
			for (int x = 0; x < this.spaltenRechts; x++) {
				int zellenBreite = absTabsRechts[x + 1] - absTabsRechts[x];
				g.setColor(FarbPaletten.getFarbe(zeile, this.hgFarbe));
				g.fillRect(xStart + absTabsRechts[x], yStart, zellenBreite, textHoehe);
				g.setColor(FarbPaletten.getFarbe(zeile, this.linienFarbe));
				g.drawRect(xStart + absTabsRechts[x], yStart, zellenBreite, textHoehe);

				zellenBreite -= 4;
				String wort = this.tabelleRechts[zeile][x];
				while ((g.getFontMetrics().stringWidth(wort) > zellenBreite) && (wort.length() > 1)) {
					wort = wort.substring(0, wort.length() - 1);
				}
				int delta = 0;
				switch (this.ausrichtungRechts[x]) {
				case LINKS:
					delta = 2;
					break;
				case CENTER:
					delta = (zellenBreite - g.getFontMetrics().stringWidth(wort)) / 2 + 2;
					break;
				case RECHTS:
					delta = zellenBreite - g.getFontMetrics().stringWidth(wort) + 2;
					break;
				}
				g.setColor(Color.black);
				g.drawString(wort, xStart + absTabsRechts[x] + delta, yStart + textHoehe
						- g.getFontMetrics().getDescent());
			}
			if (DEBUG) {
				System.out.println("Z" + zeile + ": " + this.tabelleRechts[zeile][0]);
			}
		}
	}

	public void setRelTabs(final double[] relTabs, final boolean linkeTabelle) {
		if (linkeTabelle) {
			if (relTabs.length == this.spaltenLinks) {
				this.relTabsLinks = relTabs;
			}
			else if (DEBUG) {
				System.out.println("DoppelteTabelleBlock: Falsche Anzahl Tabulatoren.");
			}
		}
		else {
			if (relTabs.length == this.spaltenRechts) {
				this.relTabsRechts = relTabs;
			}
			else if (DEBUG) {
				System.out.println("DoppelteTabelleBlock: Falsche Anzahl Tabulatoren.");
			}
		}
	}

	public void setAusrichtung(final Ausrichtung[] ausrichtung, final boolean linkeTabelle) {
		if (linkeTabelle) {
			if (ausrichtung.length == this.spaltenLinks) {
				this.ausrichtungLinks = ausrichtung;
			}
			else if (DEBUG) {
				System.out.println("DoppelteTabelleBlock: Ausrichtung - Falsche Anzahl.");
			}
		}
		else {
			if (ausrichtung.length == this.spaltenRechts) {
				this.ausrichtungRechts = ausrichtung;
			}
			else if (DEBUG) {
				System.out.println("DoppelteTabelleBlock: Ausrichtung - Falsche Anzahl.");
			}
		}
	}

}
