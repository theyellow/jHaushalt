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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2010.11.23
 */

/*
 * 2010.11.23 Graphics2D in der Methode 'print' verwendet
 * 2004.08.22 Erste Version (2.0)
 */
public class TextBlock extends AbstractBlock {

  private final String text;

  public TextBlock(String text) {
    this.text = text;
  }  

  private List<String> textUmbrechen(Graphics g, int breite) {
    ArrayList<String> zeilen = new ArrayList<String>();
    String zeile = "";
    StringTokenizer woerter = new StringTokenizer(text, " ", true);
    while(woerter.hasMoreTokens()) {
      String wort = woerter.nextToken();
      byte[] zeichen = (zeile+wort).getBytes();
      if(g.getFontMetrics().bytesWidth(zeichen,0,zeichen.length) > breite) {
        zeilen.add(zeile);
        zeile = "";
      }
      zeile += wort;
    }
    zeilen.add(zeile);
    return zeilen;
  }
  
  public int paint(Graphics g, int xStart, int yStart, int breite) {
    g.setFont(getFont());
    int textBreite = breite - 2 * getAbsRand(breite);
    List<String> zeilen = textUmbrechen(g, textBreite);
    int y = yStart;
    int x = xStart + getAbsRand(breite);
    for(int i=0; i<zeilen.size(); i++) {
      int yZeile = y + g.getFontMetrics().getHeight() - g.getFontMetrics().getDescent();
      g.drawString(""+zeilen.get(i), x, yZeile);
      y += g.getFontMetrics().getHeight();
    }
    return zeilen.size() * g.getFontMetrics().getHeight();
  }
  
  private List<String> printZeilen = null;
  
  public int print(Graphics2D g2d, int zeile, int xStart, int yStart, int hoehe, int breite) {
    g2d.setFont(getFont());
    int textBreite = breite - 2 * getAbsRand(breite);
    if(printZeilen == null)
      printZeilen = textUmbrechen(g2d, textBreite);
    if(zeile >= printZeilen.size())
      return -1;
    if(g2d.getFontMetrics().getHeight() > hoehe)
      return 0;
    int x = xStart + getAbsRand(breite);
    int y = yStart + g2d.getFontMetrics().getHeight() - g2d.getFontMetrics().getDescent();
    g2d.drawString(""+printZeilen.get(zeile), x, y);
    return g2d.getFontMetrics().getHeight();
  }

}
