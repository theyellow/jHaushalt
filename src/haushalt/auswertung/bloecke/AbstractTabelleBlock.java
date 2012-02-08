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
along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.

(C)opyright 2002-2010 Dr. Lars H. Hahn

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
abstract public class AbstractTabelleBlock extends AbstractBlock {

  public enum Ausrichtung {LINKS, CENTER, RECHTS};
  
  protected String hgFarbe = "Weiﬂ";
  protected String linienFarbe = "Schwarz";
  private final int zeilen;
  
  public AbstractTabelleBlock(int zeilen) {
    this.zeilen = zeilen;
  }

  public int paint(Graphics g, int xStart, int yStart, int breite) {
    g.setFont(getFont());
    int y = yStart;
    for(int i=0; i<zeilen; i++) {
      zeichneZeile(i, g, xStart, y, breite);
      y += g.getFontMetrics().getHeight();
    }
    return zeilen * g.getFontMetrics().getHeight();
  }
  
  /**
   * Druckt eine Zeile der Tabelle
   * @return Hoehe der gedruckten Zeile; 0 = Zeile passt nicht; -1 = keine weiteren Zeilen
   */
  public int print(Graphics2D g2d, int zeile, int xStart, int yStart, int hoehe, int breite) {
    g2d.setFont(getFont());
    if(g2d.getFontMetrics().getHeight() > hoehe)
      return 0;
    if(zeile >= zeilen)
      return -1;
    zeichneZeile(zeile, g2d, xStart, yStart, breite);
    return g2d.getFontMetrics().getHeight();
  }

  abstract protected void zeichneZeile(int zeile, Graphics g, int xStart, int yStart, int breite);
    
  public void setHgFarbe(String hgFarbe) {
      this.hgFarbe = hgFarbe;
  }

  public void setLinienFarbe(String linienFarbe) {
      this.linienFarbe = linienFarbe;
  }

}
