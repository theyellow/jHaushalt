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

import haushalt.daten.Datum;
import haushalt.daten.Euro;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.04.03
 */

/*
 * 2008.04.03 BugFix: Ausgabe des Wert "0 €" an der Nulllinie
 * 2008.03.05 BugFix: pos. Betrag immer oberhalb der X-Achse;
 * neg. Betrag immer oberhalb des kleinsten Wertes
 * 2004.08.22 Version 2.0
 */

public class VermoegenBlock extends AbstractGraphikBlock {

	private static final boolean DEBUG = false;
	private static final Logger LOGGER = Logger.getLogger(VermoegenBlock.class.getName());

	private final Datum[] zeitpunkte;
	private final Euro[] salden;
	private Color farbePos = Color.blue;
	private Color farbeNeg = Color.red;
	private final int anzahl;
	private Euro grWert = new Euro();
	private Euro klWert = new Euro();

	public VermoegenBlock(final Datum[] zeitpunkte, final Euro[] salden) {
		this.zeitpunkte = zeitpunkte;
		this.salden = salden;
		this.anzahl = zeitpunkte.length;

		for (int i = 0; i < salden.length; i++) {
			if (salden[i].compareTo(this.grWert) > 0) {
				this.grWert = salden[i];
			}
			if (salden[i].compareTo(this.klWert) < 0) {
				this.klWert = salden[i];
			}
		}
		if (DEBUG) {
			LOGGER.info("VermoegenBlock: MIN/MAX = " + this.klWert + "/" + this.grWert);
		}
	}

	@Override
	protected int getHoehe(final int breite) {
		return (int) (breite * 0.75 + 0.5);
	}

	@Override
	public int paint(final Graphics g, final int xStart, final int yStart, final int breite) {
		final Graphics2D g2 = (Graphics2D) g;
		g2.setFont(getFont()); // WICHTIG: Font setzen vor der Definition der
								// FontMetrics
		final FontMetrics fontMetrics = g2.getFontMetrics();
		int breiteYAchse = fontMetrics.stringWidth("" + this.grWert) + 5;
		if ((fontMetrics.stringWidth("" + this.klWert) + 5) > breiteYAchse) {
			breiteYAchse = fontMetrics.stringWidth("" + this.klWert) + 5;
		}
		final int rand = getAbsRand(breite);
		final int hoehe = getHoehe(breite);
		final int textHoehe = fontMetrics.getHeight();
		final int graphikBreite = breite - breiteYAchse - 2 * rand;
		final int intervallBreite = graphikBreite / this.anzahl;
		final int balkenBreite = intervallBreite * 6 / 8;
		final int xSaldoText = (intervallBreite - textHoehe / 2) * 4 / 8;
		final int xBalken = intervallBreite * 1 / 8;
		final Euro deltaWert = this.grWert.sub(this.klWert);
		if (deltaWert.equals(Euro.NULL_EURO)) {
			return 0; // keine Veränderungen im Vermögen -> keine Anzeige
		}
		// Mit der folgenden Formel wird ein Euro-Wert in eine Y-Koordinate
		// umgerechnet:
		// y = yStart + rand + (grWert - wert) * hoehe / (grWert - klWert)
		// = yOffset - yFaktor * wert
		final double yFaktor = (hoehe - 2 * textHoehe) / deltaWert.toDouble();
		final double yOffset = yStart + this.grWert.toDouble() * yFaktor; // =
																			// Nullline

		// Werte für die Y-Achse berechnen und zeichnen
		g.setColor(Color.gray);
		for (int i = 0; i <= 8; i++) {
			final Euro wert = deltaWert.mal(i / 8.0D).add(this.klWert);
			final int y = (int) (yOffset - yFaktor * wert.toDouble());
			final int x = xStart + rand + breiteYAchse;
			g.drawLine(x, y, x + graphikBreite, y);
			g.drawString("" + wert, x - fontMetrics.stringWidth("" + wert) - 5, y + fontMetrics.getDescent());
		}

		// Y-Null-Linie zeichnen
		g.setColor(Color.black);
		final int x0 = xStart + rand + breiteYAchse;
		g.drawLine(x0, (int) yOffset, xStart + rand + breiteYAchse + graphikBreite, (int) yOffset);
		g.drawString(
				"" + Euro.NULL_EURO,
				x0 - fontMetrics.stringWidth("" + Euro.NULL_EURO) - 5,
				(int) (yOffset + fontMetrics.getDescent()));

		final GradientPaint verlaufPos = new GradientPaint(0, (int) yOffset, this.farbePos, 0, (int) (yOffset - yFaktor
			* this.grWert.toDouble()), Color.lightGray);
		final GradientPaint verlaufNeg = new GradientPaint(0, (int) yOffset, this.farbeNeg, 0, (int) (yOffset - yFaktor
			* this.klWert.toDouble()), Color.lightGray);
		int x = xStart + breiteYAchse + rand;
		for (int i = 0; i < this.anzahl; i++) {
			final double y = yOffset - yFaktor * this.salden[i].toDouble();
			g2.setPaint(verlaufPos);
			if (this.salden[i].compareTo(Euro.NULL_EURO) >= 0) {
				g2.setPaint(verlaufPos);
				g2.fill(new Rectangle2D.Double(x + xBalken, y, balkenBreite, yOffset - y));
			} else {
				g2.setPaint(verlaufNeg);
				g2.fill(new Rectangle2D.Double(x + xBalken, yOffset, balkenBreite, y - yOffset));
			}

			g2.setPaint(Color.black);
			g2.drawString("" + this.zeitpunkte[i], x + xBalken, hoehe + yStart - 5);

			final AffineTransform oldAt = g2.getTransform();
			final int textBreite = fontMetrics.stringWidth("" + this.salden[i]);
			if (this.salden[i].compareTo(Euro.NULL_EURO) >= 0) {
				// -- Positiver Wert
				// -----------------------------------------------------------------
				if (textBreite + 10 <= yFaktor * this.salden[i].toDouble()) {
					// Prima! Der Balken ist hoch genug für den Text
					g2.rotate(Math.toRadians(90), x + xSaldoText, y + 5);
					g2.drawString("" + this.salden[i], x + xSaldoText, (int) (y + 5));
				} else {
					// Pech! Der Text ragt über den Balken hinaus, ist aber
					// sichtbar
					g2.rotate(Math.toRadians(90), x + xSaldoText, yOffset - textBreite - 5);
					g2.drawString("" + this.salden[i], x + xSaldoText, (int) (yOffset - textBreite - 5));
				}
			} else {
				// -- Negativer Wert
				// -----------------------------------------------------------------
				if (textBreite + 10 <= yFaktor * -this.salden[i].toDouble()) {
					// Prima! Der Balken ist hoch genug für den Text
					g2.rotate(Math.toRadians(90), x + xSaldoText, y - textBreite - 5);
					g2.drawString("" + this.salden[i], x + xSaldoText, (int) y - textBreite - 5);
				} else {
					// Pech! Der Text ragt über den Balken hinaus, ist aber
					// sichtbar
					g2.rotate(Math.toRadians(90), x + xSaldoText, yOffset + 5);
					g2.drawString("" + this.salden[i], x + xSaldoText, (int) (yOffset + 5));
				}
			}
			g2.setTransform(oldAt);

			x += intervallBreite;
		}
		return hoehe;
	}

	public void setFarbePos(final Color farbe) {
		this.farbePos = farbe;
	}

	public void setFarbeNeg(final Color farbe) {
		this.farbeNeg = farbe;
	}
}
