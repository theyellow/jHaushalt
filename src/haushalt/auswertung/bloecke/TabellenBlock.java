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

import haushalt.auswertung.FarbPaletten;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.4/2008.04.15
 * @since 2.0
 */

/*
 * 2008.04.15 "Zwischenschicht" AbstractTabelleBlock
 * 2004.08.22 Erste Version
 */
public class TabellenBlock extends AbstractTabelleBlock {
	private static final boolean DEBUG = false;

  private final String[][] tabelle;
  private final int spalten;
  private double[] relTabs;
  private Ausrichtung[] ausrichtung;

  public TabellenBlock(String[][] tabelle) {
    super(tabelle.length);
    this.tabelle = tabelle;
    spalten = tabelle[0].length;
    berechneTabs();
    ausrichtung = new Ausrichtung[spalten];
    Arrays.fill(ausrichtung, Ausrichtung.LINKS);
    if(DEBUG)
      System.out.println("Tabellen-Block erzeugt ("+tabelle.length+"x"+spalten+")");
  }  
  
  protected void zeichneZeile(int zeile, Graphics g, int xStart, int yStart, int breite) {
    final int textHoehe = g.getFontMetrics().getHeight();
    final int[] absTabs = getAbsTabs(breite);
    for(int x = 0; x < spalten; x++) {
      int zellenBreite = absTabs[x+1] - absTabs[x];      
      g.setColor(FarbPaletten.getFarbe(zeile, hgFarbe));
      g.fillRect(xStart + absTabs[x], yStart, zellenBreite, textHoehe);
      g.setColor(FarbPaletten.getFarbe(zeile,linienFarbe));
      g.drawRect(xStart + absTabs[x], yStart, zellenBreite, textHoehe);

      zellenBreite -= 4;
      String wort = tabelle[zeile][x];
      while((g.getFontMetrics().stringWidth(wort) > zellenBreite) && (wort.length() > 1))
        wort = wort.substring(0, wort.length()-1);
      int delta = 0;
      switch(ausrichtung[x]) {
        case LINKS :
          delta = 2;
          break;
        case CENTER :
          delta = (zellenBreite-g.getFontMetrics().stringWidth(wort))/2 + 2;
          break;
        case RECHTS :
          delta = zellenBreite-g.getFontMetrics().stringWidth(wort) + 2;
          break;
      }
      g.setColor(Color.black);
      g.drawString(wort, xStart+absTabs[x]+delta, yStart+textHoehe-g.getFontMetrics().getDescent());
      if(DEBUG)
    	  System.out.println(wort+" @ "+ (xStart+absTabs[x]+delta) + ", " + (yStart+textHoehe-g.getFontMetrics().getDescent()));
    }
    if(DEBUG)
      System.out.println("Z"+zeile+": "+tabelle[zeile][0]);
  }
  
  private void berechneTabs() {
    int anzahl = tabelle[0].length;
    relTabs = new double[anzahl];
    double abstand = 100.0D / anzahl;
    for(int i=0; i<anzahl; i++)
      relTabs[i] = i*abstand;
  }
  
  private int[] getAbsTabs(int seitenBreite) {
    int tabellenBreite = seitenBreite - 2*getAbsRand(seitenBreite);
    int[] absTabs = new int[spalten+1];
    for(int i=0; i<spalten; i++)
      absTabs[i] = (int)(tabellenBreite * relTabs[i] / 100.0D + getAbsRand(seitenBreite));
    absTabs[spalten] = tabellenBreite + getAbsRand(seitenBreite);
    return absTabs;
  }
  
  public void setRelTabs(double[] relTabs) {
    if(relTabs.length == spalten)
      this.relTabs = relTabs;
    else if(DEBUG)
      System.out.println("TabellenBlock: Falsche Anzahl Tabulatoren.");
  }

  public void setAusrichtung(Ausrichtung[] ausrichtung) {
    if(ausrichtung.length == spalten)
      this.ausrichtung = ausrichtung;
    else if(DEBUG)
      System.out.println("TabellenBlock: Ausrichtung - Falsche Anzahl.");
  }

}
