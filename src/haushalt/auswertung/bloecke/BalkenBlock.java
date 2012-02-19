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

  public BalkenBlock(String[] zeitraumNamen, Euro[] einnahmen, Euro[] ausgaben, boolean durchschnitt) {
    this.zeitraumNamen = zeitraumNamen;
    this.einnahmen = einnahmen;
    this.ausgaben = ausgaben;
    anzahl = zeitraumNamen.length;
    Euro summeEinnahmen = new Euro();
    Euro summeAusgaben = new Euro();

    for(int i=0; i<anzahl; i++) {
      if(grWert.compareTo(einnahmen[i]) < 0)
        grWert = einnahmen[i];
      if(grWert.compareTo(ausgaben[i]) < 0)
        grWert = ausgaben[i];
      summeEinnahmen.sum(einnahmen[i]);
      summeAusgaben.sum(ausgaben[i]);
    }
    if(durchschnitt) {
      schnittEinnahmen = summeEinnahmen.durch(anzahl);
      schnittAusgaben = summeAusgaben.durch(anzahl);
    }
  }

  protected int getHoehe(int breite) {
    // enspricht dem Standard-Bildschirmverhältnis 4:3
    return (int)(breite * 0.75 + 0.5);
  }

  public int paint(Graphics g, int xStart, int yStart, int breite) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(getFont()); // WICHTIG: Font setzen vor der Definition der FontMetrics
    FontMetrics fontMetrics = g2.getFontMetrics();
    int breiteYAchse = fontMetrics.stringWidth(""+grWert) + 5;
    int rand = getAbsRand(breite);
    int hoehe = getHoehe(breite);
    int textHoehe = fontMetrics.getHeight();
    int graphikBreite = breite - breiteYAchse - 2*rand;
    int intervallBreite = graphikBreite / anzahl;
    int balkenBreite    = intervallBreite * 6 / 15;
    int xEinnahmen      = intervallBreite * 2 / 15;
    int xEinnahmenText  = intervallBreite * 5 / 15 - textHoehe / 2;
    int xAusgaben       = intervallBreite * 9 / 15;
    int xAusgabenText   = intervallBreite * 12 / 15 - textHoehe / 2;
    int yBalken         = hoehe - textHoehe + yStart;
    int maxBalkenHoehe  = hoehe - 2 * textHoehe;
    
    // 9 graue horizontale Linien zeichnen:
    int y;
    int x = xStart + rand + breiteYAchse;
    for(int i = 0; i <= 8; i++) {
      double faktor = i / 8.0D;
      Euro euro = new Euro(grWert.toDouble()*faktor);
      y = yBalken - (int)(maxBalkenHoehe * faktor);
      g2.setPaint(Color.gray);
      g2.drawLine(x, y, x+graphikBreite, y);
      g2.setPaint(Color.black);
      g2.drawString(""+euro, x - fontMetrics.stringWidth(""+euro) - 3, y);
    }
    // Durchschnittliche Einnahmen und Ausgaben als Linien einzeichnen:
    if(schnittAusgaben != null) {
      y = yBalken - (int)(maxBalkenHoehe * schnittEinnahmen.toDouble() / grWert.toDouble());
      g2.setPaint(farbePos);
      g2.drawRect(x, y, graphikBreite, 1);
      g2.drawString(""+schnittEinnahmen, x - fontMetrics.stringWidth(""+schnittEinnahmen) - 3, y);
      y = yBalken - (int)(maxBalkenHoehe * schnittAusgaben.toDouble() / grWert.toDouble());
      g2.setPaint(farbeNeg);
      g2.drawRect(x, y, graphikBreite, 1);
      g2.drawString(""+schnittAusgaben, x - fontMetrics.stringWidth(""+schnittAusgaben) - 3, y);
    }

    GradientPaint verlaufPos = new GradientPaint(0, yBalken, farbePos, 0, yStart, Color.lightGray);
    GradientPaint verlaufNeg = new GradientPaint(0, yBalken, farbeNeg, 0, yStart, Color.lightGray);
    for(int i=0; i<anzahl; i++) {
      int hoeheEinnahmen = (int)(maxBalkenHoehe * einnahmen[i].toDouble() / grWert.toDouble());
      g2.setPaint(verlaufPos);
      g2.fill(new Rectangle2D.Double(x+xEinnahmen, yBalken-hoeheEinnahmen, balkenBreite, hoeheEinnahmen));

      int hoeheAusgaben = (int)(maxBalkenHoehe * ausgaben[i].toDouble() / grWert.toDouble());
      g2.setPaint(verlaufNeg);
      g2.fill(new Rectangle2D.Double(x+xAusgaben, yBalken-hoeheAusgaben, balkenBreite, hoeheAusgaben));

      g2.setPaint(Color.black);
      g2.drawString(zeitraumNamen[i], x+xEinnahmen, hoehe + yStart - 5);

      AffineTransform oldAt = g2.getTransform();
      if(hoeheEinnahmen > fontMetrics.stringWidth(""+einnahmen[i]))
        // Prima! Der Balken ist hoch genug für den Text
        g2.translate(x+xEinnahmenText, yBalken-hoeheEinnahmen+5);
      else 
        // Pech! Der Text ragt über den Balken hinaus, ist aber sichtbar
        g2.translate(x+xEinnahmenText, yBalken-fontMetrics.stringWidth(""+einnahmen[i])-5);
      g2.rotate(Math.toRadians(90));
      g2.drawString(""+einnahmen[i], 0, 0);
      g2.setTransform(oldAt);
      
      if(hoeheAusgaben > fontMetrics.stringWidth(""+ausgaben[i]))
        // Prima! Der Balken ist hoch genug für den Text
        g2.translate(x+xAusgabenText, yBalken-hoeheAusgaben+5);
      else 
        // Pech! Der Text ragt über den Balken hinaus, ist aber sichtbar
        g2.translate(x+xAusgabenText, yBalken-fontMetrics.stringWidth(""+ausgaben[i])-5);
      g2.rotate(Math.toRadians(90));
      g2.drawString(""+ausgaben[i], 0, 0);
      g2.setTransform(oldAt);

      x += intervallBreite;
    }
    return hoehe;
  }

  public void setFarbeEinnahmen(Color farbe) {
    this.farbePos = farbe;
  }
  
  public void setFarbeAusgaben(Color farbe) {
    this.farbeNeg = farbe;
  }
}
