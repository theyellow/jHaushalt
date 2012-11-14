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

import haushalt.daten.Euro;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.06.26
 */

/*
 * 2007.06.26 Erweiterung: Anzeigen des Betrags der Durchschnittswerte
 * 2006.02.03 BugFix: Farbwahl auch für Durchschnitt gültig
 * 2006.02.02 Betrag immer oberhalb der X-Achse
 */
public class BalkenBlock extends AbstractGraphikBlock {

	private Color farbePos = Color.blue;
	private Color farbeNeg = Color.red;
	private final Euro[] einnahmen;
	private final Euro[] ausgaben;
	private final String[] zeitraumNamen;
	private final int anzahl;
	private Euro schnittEinnahmen = null;
	private Euro schnittAusgaben = null;
	private Euro grWert = new Euro();

	public BalkenBlock(final String[] zeitraumNamen, final Euro[] einnahmen, final Euro[] ausgaben,
			final boolean durchschnitt) {
		this.zeitraumNamen = zeitraumNamen;
		this.einnahmen = einnahmen;
		this.ausgaben = ausgaben;
		this.anzahl = zeitraumNamen.length;
		final Euro summeEinnahmen = new Euro();
		final Euro summeAusgaben = new Euro();

		for (int i = 0; i < this.anzahl; i++) {
			if (this.grWert.compareTo(einnahmen[i]) < 0) {
				this.grWert = einnahmen[i];
			}
			if (this.grWert.compareTo(ausgaben[i]) < 0) {
				this.grWert = ausgaben[i];
			}
			summeEinnahmen.sum(einnahmen[i]);
			summeAusgaben.sum(ausgaben[i]);
		}
		if (durchschnitt) {
			this.schnittEinnahmen = summeEinnahmen.durch(this.anzahl);
			this.schnittAusgaben = summeAusgaben.durch(this.anzahl);
		}
	}

	@Override
	protected int getHoehe(final int breite) {
		// enspricht dem Standard-Bildschirmverhältnis 4:3
		return (int) (breite * 0.75 + 0.5);
	}

	@Override
	public int paint(final Graphics g, final int xStart, final int yStart, final int breite) {
		final Graphics2D g2 = (Graphics2D) g;
		g2.setFont(getFont()); // WICHTIG: Font setzen vor der Definition der
								// FontMetrics
		final FontMetrics fontMetrics = g2.getFontMetrics();
		final int breiteYAchse = fontMetrics.stringWidth("" + this.grWert) + 5;
		final int rand = getAbsRand(breite);
		final int hoehe = getHoehe(breite);
		final int textHoehe = fontMetrics.getHeight();
		final int graphikBreite = breite - breiteYAchse - 2 * rand;
		final int intervallBreite = graphikBreite / this.anzahl;
		final int balkenBreite = intervallBreite * 6 / 15;
		final int xEinnahmen = intervallBreite * 2 / 15;
		final int xEinnahmenText = intervallBreite * 5 / 15 - textHoehe / 2;
		final int xAusgaben = intervallBreite * 9 / 15;
		final int xAusgabenText = intervallBreite * 12 / 15 - textHoehe / 2;
		final int yBalken = hoehe - textHoehe + yStart;
		final int maxBalkenHoehe = hoehe - 2 * textHoehe;

		// 9 graue horizontale Linien zeichnen:
		int y;
		int x = xStart + rand + breiteYAchse;
		for (int i = 0; i <= 8; i++) {
			final double faktor = i / 8.0D;
			final Euro euro = new Euro(this.grWert.toDouble() * faktor);
			y = yBalken - (int) (maxBalkenHoehe * faktor);
			g2.setPaint(Color.gray);
			g2.drawLine(x, y, x + graphikBreite, y);
			g2.setPaint(Color.black);
			g2.drawString("" + euro, x - fontMetrics.stringWidth("" + euro) - 3, y);
		}
		// Durchschnittliche Einnahmen und Ausgaben als Linien einzeichnen:
		if (this.schnittAusgaben != null) {
			y = yBalken - (int) (maxBalkenHoehe * this.schnittEinnahmen.toDouble() / this.grWert.toDouble());
			g2.setPaint(this.farbePos);
			g2.drawRect(x, y, graphikBreite, 1);
			g2.drawString("" + this.schnittEinnahmen, x - fontMetrics.stringWidth("" + this.schnittEinnahmen) - 3, y);
			y = yBalken - (int) (maxBalkenHoehe * this.schnittAusgaben.toDouble() / this.grWert.toDouble());
			g2.setPaint(this.farbeNeg);
			g2.drawRect(x, y, graphikBreite, 1);
			g2.drawString("" + this.schnittAusgaben, x - fontMetrics.stringWidth("" + this.schnittAusgaben) - 3, y);
		}

		final GradientPaint verlaufPos = new GradientPaint(0, yBalken, this.farbePos, 0, yStart, Color.lightGray);
		final GradientPaint verlaufNeg = new GradientPaint(0, yBalken, this.farbeNeg, 0, yStart, Color.lightGray);
		for (int i = 0; i < this.anzahl; i++) {
			final int hoeheEinnahmen = (int) (maxBalkenHoehe * this.einnahmen[i].toDouble() / this.grWert.toDouble());
			g2.setPaint(verlaufPos);
			g2.fill(new Rectangle2D.Double(x + xEinnahmen, yBalken - hoeheEinnahmen, balkenBreite, hoeheEinnahmen));

			final int hoeheAusgaben = (int) (maxBalkenHoehe * this.ausgaben[i].toDouble() / this.grWert.toDouble());
			g2.setPaint(verlaufNeg);
			g2.fill(new Rectangle2D.Double(x + xAusgaben, yBalken - hoeheAusgaben, balkenBreite, hoeheAusgaben));

			g2.setPaint(Color.black);
			g2.drawString(this.zeitraumNamen[i], x + xEinnahmen, hoehe + yStart - 5);

			final AffineTransform oldAt = g2.getTransform();
			if (hoeheEinnahmen > fontMetrics.stringWidth("" + this.einnahmen[i])) {
				// Prima! Der Balken ist hoch genug für den Text
				g2.translate(x + xEinnahmenText, yBalken - hoeheEinnahmen + 5);
			}
			else {
				// Pech! Der Text ragt über den Balken hinaus, ist aber sichtbar
				g2.translate(x + xEinnahmenText, yBalken - fontMetrics.stringWidth("" + this.einnahmen[i]) - 5);
			}
			g2.rotate(Math.toRadians(90));
			g2.drawString("" + this.einnahmen[i], 0, 0);
			g2.setTransform(oldAt);

			if (hoeheAusgaben > fontMetrics.stringWidth("" + this.ausgaben[i])) {
				// Prima! Der Balken ist hoch genug für den Text
				g2.translate(x + xAusgabenText, yBalken - hoeheAusgaben + 5);
			}
			else {
				// Pech! Der Text ragt über den Balken hinaus, ist aber sichtbar
				g2.translate(x + xAusgabenText, yBalken - fontMetrics.stringWidth("" + this.ausgaben[i]) - 5);
			}
			g2.rotate(Math.toRadians(90));
			g2.drawString("" + this.ausgaben[i], 0, 0);
			g2.setTransform(oldAt);

			x += intervallBreite;
		}
		return hoehe;
	}

	public void setFarbeEinnahmen(final Color farbe) {
		this.farbePos = farbe;
	}

	public void setFarbeAusgaben(final Color farbe) {
		this.farbeNeg = farbe;
	}
}
