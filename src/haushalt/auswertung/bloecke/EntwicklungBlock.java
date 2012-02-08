/*

This file is part of jHaushalt.

jHaushalt is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

jHaushalt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.

(C)opyright 2002-2010 Dr. Lars H. Hahn

*/

package haushalt.auswertung.bloecke;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import haushalt.auswertung.FarbPaletten;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.0/2008.02.13
 * @since 2.0
 */

/* 2008.02.13 Ausgaben können wahlweise mit positiven oder negativen Werten angezeigt werden
 * 2004.08.22 Erste Version
 */
public class EntwicklungBlock extends AbstractGraphikBlock {
	private static final boolean DEBUG = false;

  private Euro grWert = new Euro();
  private Euro klWert = new Euro();
  private final EinzelKategorie[] kategorien;
  private final String[] zeitraumNamen;
  private final Euro[][] werte;
  private final String farbschema;
  private final Boolean negativeWerte;
  
  public EntwicklungBlock(EinzelKategorie[] kategorien, String[] zeitraumNamen, Euro[][] werte, String farbschema, Boolean negativeWerte) {
    this.kategorien = kategorien;
    this.zeitraumNamen = zeitraumNamen;
    this.werte = werte;
    this.farbschema = farbschema;
    this.negativeWerte = negativeWerte;
    
    int anzahlKategorien = kategorien.length;
    int anzahlZeitraeume = zeitraumNamen.length;
    for(int k=0; k<anzahlKategorien; k++) {
      for(int z=0; z<anzahlZeitraeume; z++) {
        if(negativeWerte) {
          if(grWert.compareTo(werte[z][k]) < 0)
            grWert = werte[z][k];
          if(klWert.compareTo(werte[z][k]) > 0)
            klWert = werte[z][k];
        }
        else {
          if(grWert.compareTo(werte[z][k]) < 0)
            grWert = werte[z][k];
          if(grWert.compareTo(werte[z][k].mal(-1.0D)) < 0)
            grWert = werte[z][k].mal(-1.0D);
        }
      }
    }
    if(DEBUG)
      System.out.println("EntwicklungBlock: MIN/MAX = "+klWert+"/"+grWert);
  }
  
  public int paint(Graphics g, int xStart, int yStart, int breite) {
    g.setFont(getFont()); // WICHTIG: Font setzen vor der Definition der FontMetrics
    FontMetrics fontMetrics = g.getFontMetrics();
    int anzahlZeitraeume = zeitraumNamen.length;
    int breiteYAchse = fontMetrics.stringWidth(""+grWert) + 5;
    if((fontMetrics.stringWidth(""+klWert) + 5) > breiteYAchse)
      breiteYAchse = fontMetrics.stringWidth(""+klWert) + 5;
    int rand = getAbsRand(breite);
    int spaltenBreite = (breite - breiteYAchse - 2*rand) / (anzahlZeitraeume - 1);
    // kompliziert, um den Rundungs-Fehler zu vermeiden
    int graphikBreite = spaltenBreite * (anzahlZeitraeume - 1);
    int hoehe = getHoehe(breite) - fontMetrics.getHeight();
    // Mit der folgenden Formel wird ein Euro-Wert in eine Y-Koordinate umgerechnet: 
    // y = yStart + rand + (grWert - wert) * hoehe / (grWert - klWert)
    //   = yOffset - yFaktor * wert
    Euro deltaWert = grWert.sub(klWert);
    double yFaktor = hoehe / deltaWert.toDouble();
    double yOffset = yStart + grWert.toDouble() * yFaktor;
    
    // Werte für die Y-Achse berechnen und zeichnen
    for(int i = 0; i <= 8; i++) {
      Euro wert = deltaWert.mal(i / 8.0D).add(klWert);
      int y = (int)(yOffset - yFaktor * wert.toDouble());
      int x = xStart + rand + breiteYAchse;
      g.setColor(Color.gray);
      g.drawLine(x, y, x + graphikBreite, y);
      g.setColor(Color.black);
      g.drawString(""+wert, x - fontMetrics.stringWidth(""+wert) - 5, y + fontMetrics.getDescent());
    }

    // Y-Null-Linie zeichnen
    g.drawLine(xStart + rand + breiteYAchse, (int)yOffset, xStart + rand + breiteYAchse + graphikBreite, (int)yOffset);
    
    // Werte für die X-Achse berechnen und zeichnen
    for(int i = 0; i < anzahlZeitraeume; i++) {
      int y = yStart + getHoehe(breite);
      int x = xStart + rand + breiteYAchse + spaltenBreite * i;
      g.setColor(Color.gray);
      g.drawLine(x, yStart, x, yStart + hoehe);
      g.setColor(Color.black);
      g.drawString(zeitraumNamen[i], x - fontMetrics.stringWidth(zeitraumNamen[i]) / 2, y);
    }
    
    ((Graphics2D)g).setStroke(new BasicStroke(2.0f));
    for(int k = 0; k < kategorien.length; k++) {
      g.setColor(FarbPaletten.getFarbe(k, farbschema));
      int[] x = new int[anzahlZeitraeume];
      int[] y = new int[anzahlZeitraeume];
      for(int z = 0; z < anzahlZeitraeume; z++) {
        x[z] = xStart + rand + breiteYAchse + spaltenBreite * z;
        if(!negativeWerte && (werte[z][k].compareTo(Euro.NULL_EURO) < 0))
          y[z] = (int)(yOffset - yFaktor * -werte[z][k].toDouble());
        else
          y[z] = (int)(yOffset - yFaktor * werte[z][k].toDouble());
        g.fillOval(x[z]-4, y[z]-4, 8, 8);
      }
      g.drawPolyline(x, y, anzahlZeitraeume);
      
    }

    return getHoehe(breite);
  }

  protected int getHoehe(int breite) {
    // enspricht dem Standard-Bildschirmverhältnis 4:3
    return (int)(breite * 0.75 + 0.5);
  }
  
}
