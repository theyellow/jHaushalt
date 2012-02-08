/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.auswertung.bloecke;

import haushalt.daten.Datum;
import haushalt.daten.Euro;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.04.03
 */

/*
 * 2008.04.03 BugFix: Ausgabe des Wert "0 €" an der Nulllinie
 * 2008.03.05 BugFix: pos. Betrag immer oberhalb der X-Achse; 
 *            neg. Betrag immer oberhalb des kleinsten Wertes
 * 2004.08.22 Version 2.0
 */

public class VermoegenBlock extends AbstractGraphikBlock {
	private static final boolean DEBUG = false;

  private final Datum[] zeitpunkte;
  private final Euro[] salden;
  private Color farbePos = Color.blue; 
  private Color farbeNeg = Color.red; 
  private final int anzahl;
  private Euro grWert = new Euro();
  private Euro klWert = new Euro();

  public VermoegenBlock(Datum[] zeitpunkte, Euro[] salden) {
    this.zeitpunkte = zeitpunkte;
    this.salden = salden;
    anzahl = zeitpunkte.length;

    for(int i=0; i<salden.length; i++) {
      if(salden[i].compareTo(grWert) > 0)
        grWert = salden[i];
      if(salden[i].compareTo(klWert) < 0)
        klWert = salden[i];
    }
    if(DEBUG)
      System.out.println("VermoegenBlock: MIN/MAX = "+klWert+"/"+grWert);
  }

  protected int getHoehe(int breite) {
    return (int)(breite * 0.75 + 0.5);
  }
  
  public int paint(Graphics g, int xStart, int yStart, int breite) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(getFont()); // WICHTIG: Font setzen vor der Definition der FontMetrics
    FontMetrics fontMetrics = g2.getFontMetrics();
    int breiteYAchse = fontMetrics.stringWidth(""+grWert) + 5;
    if((fontMetrics.stringWidth(""+klWert) + 5) > breiteYAchse)
      breiteYAchse = fontMetrics.stringWidth(""+klWert) + 5;
    int rand = getAbsRand(breite);
    int hoehe = getHoehe(breite);
    int textHoehe = fontMetrics.getHeight();
    int graphikBreite = breite - breiteYAchse - 2*rand;
    int intervallBreite = graphikBreite / anzahl;
    int balkenBreite = intervallBreite * 6 / 8;
    int xSaldoText = (intervallBreite - textHoehe/2) * 4 / 8;
    int xBalken = intervallBreite * 1 / 8;
    Euro deltaWert = grWert.sub(klWert);
    if(deltaWert.equals(Euro.NULL_EURO))
      return 0; // keine Veränderungen im Vermögen -> keine Anzeige
    // Mit der folgenden Formel wird ein Euro-Wert in eine Y-Koordinate umgerechnet: 
    // y = yStart + rand + (grWert - wert) * hoehe / (grWert - klWert)
    //   = yOffset - yFaktor * wert
    double yFaktor = (hoehe - 2 * textHoehe) / deltaWert.toDouble();
    double yOffset = yStart + grWert.toDouble() * yFaktor; // = Nullline

    // Werte für die Y-Achse berechnen und zeichnen
    g.setColor(Color.gray);
    for(int i = 0; i <= 8; i++) {
      Euro wert = deltaWert.mal(i / 8.0D).add(klWert);
      int y = (int)(yOffset - yFaktor * wert.toDouble());
      int x = xStart + rand + breiteYAchse;
      g.drawLine(x, y, x + graphikBreite, y);
      g.drawString(""+wert, x - fontMetrics.stringWidth(""+wert) - 5, y + fontMetrics.getDescent());
    }

    // Y-Null-Linie zeichnen
    g.setColor(Color.black);
    int x0 = xStart + rand + breiteYAchse;
    g.drawLine(x0, (int)yOffset, xStart + rand + breiteYAchse + graphikBreite, (int)yOffset);
    g.drawString(""+Euro.NULL_EURO, x0 - fontMetrics.stringWidth(""+Euro.NULL_EURO) - 5, (int) (yOffset + fontMetrics.getDescent()));
    
    GradientPaint verlaufPos = new GradientPaint(0, (int)yOffset, farbePos, 0, (int)(yOffset-yFaktor*grWert.toDouble()), Color.lightGray);
    GradientPaint verlaufNeg = new GradientPaint(0, (int)yOffset, farbeNeg, 0, (int)(yOffset-yFaktor*klWert.toDouble()), Color.lightGray);
    int x = xStart + breiteYAchse + rand;
    for(int i=0; i<anzahl; i++) {
      double y = yOffset - yFaktor * salden[i].toDouble();
      g2.setPaint(verlaufPos);
      if(salden[i].compareTo(Euro.NULL_EURO) >= 0) {
        g2.setPaint(verlaufPos);
        g2.fill(new Rectangle2D.Double(x+xBalken, y, balkenBreite, yOffset-y));
      }
      else {
        g2.setPaint(verlaufNeg);
        g2.fill(new Rectangle2D.Double(x+xBalken, yOffset, balkenBreite, y-yOffset));
      }
      
      g2.setPaint(Color.black);
      g2.drawString(""+zeitpunkte[i], x+xBalken, hoehe + yStart - 5);

      AffineTransform oldAt = g2.getTransform();
      int textBreite = fontMetrics.stringWidth(""+salden[i]);
      if(salden[i].compareTo(Euro.NULL_EURO) >= 0) {
        // -- Positiver Wert -----------------------------------------------------------------
        if(textBreite+10 <= yFaktor * salden[i].toDouble()) {
          // Prima! Der Balken ist hoch genug für den Text
          g2.rotate(Math.toRadians(90), x+xSaldoText, y+5);
          g2.drawString(""+salden[i], x+xSaldoText, (int)(y+5));
        }
        else {
          // Pech! Der Text ragt über den Balken hinaus, ist aber sichtbar
          g2.rotate(Math.toRadians(90), x+xSaldoText, yOffset - textBreite - 5);
          g2.drawString(""+salden[i], x+xSaldoText, (int)(yOffset - textBreite - 5));
        }
      }
      else {
        // -- Negativer Wert -----------------------------------------------------------------
        if(textBreite+10 <= yFaktor * -salden[i].toDouble()) {
          // Prima! Der Balken ist hoch genug für den Text
          g2.rotate(Math.toRadians(90), x+xSaldoText, y - textBreite - 5);
          g2.drawString(""+salden[i], x+xSaldoText, (int) y - textBreite - 5);
        }
        else {
          // Pech! Der Text ragt über den Balken hinaus, ist aber sichtbar
          g2.rotate(Math.toRadians(90), x+xSaldoText, yOffset+5);
          g2.drawString(""+salden[i], x+xSaldoText, (int)(yOffset+5));
        }
      }
      g2.setTransform(oldAt);

      x += intervallBreite;
    }
    return hoehe;
  }
  
  public void setFarbePos(Color farbe) {
    this.farbePos = farbe;
  }
  
  public void setFarbeNeg(Color farbe) {
    this.farbeNeg = farbe;
  }
}
