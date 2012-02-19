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
  private final double relBreiteSpalteLinks  = 48.0D;
  private final double relBreiteSpalteRechts  = 48.0D;
  private final double relSpaltenAbstand = 100.0D - relBreiteSpalteLinks - relBreiteSpalteRechts;

  public DoppelteTabelleBlock(String[][] tabelleLinks, String[][] tabelleRechts) {
    super(tabelleLinks.length>tabelleRechts.length?tabelleLinks.length:tabelleRechts.length);
    this.tabelleLinks = tabelleLinks;
    this.tabelleRechts = tabelleRechts;
    spaltenLinks = (tabelleLinks.length == 0)?0:tabelleLinks[0].length;
    spaltenRechts = (tabelleRechts.length == 0)?0:tabelleRechts[0].length;

    // Standard-Tabs gleichmäßig verteilen
    relTabsLinks = new double[spaltenLinks];
    double abstand = relBreiteSpalteLinks / spaltenLinks;
    for(int i=0; i<spaltenLinks; i++)
      relTabsLinks[i] = i*abstand;
    relTabsRechts = new double[spaltenRechts];
    abstand = relBreiteSpalteRechts / spaltenRechts;
    for(int i=0; i<spaltenRechts; i++)
      relTabsRechts[i] = i*abstand;

    // Standard-Ausrichtung setzen
    ausrichtungLinks = new Ausrichtung[spaltenLinks];
    Arrays.fill(ausrichtungLinks, Ausrichtung.LINKS);
    ausrichtungRechts = new Ausrichtung[spaltenRechts];
    Arrays.fill(ausrichtungRechts, Ausrichtung.LINKS);
  }  
  
  protected void zeichneZeile(int zeile, Graphics g, int xStart, int yStart, int breite) {
    int tabellenBreiteLinks = (int) ((breite - 2*getAbsRand(breite)) * relBreiteSpalteLinks / 100.0D );
    int tabellenBreiteRechts = (int) ((breite - 2*getAbsRand(breite)) * relBreiteSpalteRechts / 100.0D );
    int tabellenBreiteAbstand = (int) ((breite - 2*getAbsRand(breite)) * relSpaltenAbstand / 100.0D );
    
    // Berechnung der absoluten Abstände (=Tabs) der linken Spalte 
    final int[] absTabsLinks = new int[spaltenLinks+1];
    for(int i=0; i<spaltenLinks; i++)
      absTabsLinks[i] = (int)(tabellenBreiteLinks * relTabsLinks[i] / 100.0D + getAbsRand(breite));
    absTabsLinks[spaltenLinks] = tabellenBreiteLinks + getAbsRand(breite);
    
    // Berechnung der absoluten Abstände (=Tabs) der rechten Spalte 
    final int[] absTabsRechts = new int[spaltenRechts+1];
    for(int i=0; i<spaltenRechts; i++)
      absTabsRechts[i] = (int)(tabellenBreiteRechts * relTabsRechts[i] / 100.0D + tabellenBreiteAbstand + tabellenBreiteLinks + getAbsRand(breite));
    absTabsRechts[spaltenRechts] = breite - getAbsRand(breite);
    
    final int textHoehe = g.getFontMetrics().getHeight();
    
    // Tabelle 1 (links):
    if((zeile < tabelleLinks.length) && (tabelleLinks[zeile][0] != null)) {
      for(int x = 0; x < spaltenLinks; x++) {
        int zellenBreite = absTabsLinks[x+1] - absTabsLinks[x];      
        g.setColor(FarbPaletten.getFarbe(zeile, hgFarbe));
        g.fillRect(xStart + absTabsLinks[x], yStart, zellenBreite, textHoehe);
        g.setColor(FarbPaletten.getFarbe(zeile,linienFarbe));
        g.drawRect(xStart + absTabsLinks[x], yStart, zellenBreite, textHoehe);
  
        zellenBreite -= 4;
        String wort = tabelleLinks[zeile][x];
        while((g.getFontMetrics().stringWidth(wort) > zellenBreite) && (wort.length() > 1))
          wort = wort.substring(0, wort.length()-1);
        int delta = 0;
        switch(ausrichtungLinks[x]) {
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
        g.drawString(wort, xStart+absTabsLinks[x]+delta, yStart+textHoehe-g.getFontMetrics().getDescent());
      }
      if(DEBUG)
        System.out.println("Z"+zeile+": "+tabelleLinks[zeile][0]);
    }
    
    // Tabelle 2 (rechts):
    if((zeile < tabelleRechts.length) && (tabelleRechts[zeile][0] != null)) {
      for(int x = 0; x < spaltenRechts; x++) {
        int zellenBreite = absTabsRechts[x+1] - absTabsRechts[x];      
        g.setColor(FarbPaletten.getFarbe(zeile, hgFarbe));
        g.fillRect(xStart + absTabsRechts[x], yStart, zellenBreite, textHoehe);
        g.setColor(FarbPaletten.getFarbe(zeile,linienFarbe));
        g.drawRect(xStart + absTabsRechts[x], yStart, zellenBreite, textHoehe);
  
        zellenBreite -= 4;
        String wort = tabelleRechts[zeile][x];
        while((g.getFontMetrics().stringWidth(wort) > zellenBreite) && (wort.length() > 1))
          wort = wort.substring(0, wort.length()-1);
        int delta = 0;
        switch(ausrichtungRechts[x]) {
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
        g.drawString(wort, xStart+absTabsRechts[x]+delta, yStart+textHoehe-g.getFontMetrics().getDescent());
      }
      if(DEBUG)
        System.out.println("Z"+zeile+": "+tabelleRechts[zeile][0]);
    }
  }
    
  public void setRelTabs(double[] relTabs, boolean linkeTabelle) {
    if(linkeTabelle) {
      if(relTabs.length == spaltenLinks)
        relTabsLinks = relTabs;
      else if(DEBUG)
        System.out.println("DoppelteTabelleBlock: Falsche Anzahl Tabulatoren.");
    }
    else {
      if(relTabs.length == spaltenRechts)
        relTabsRechts = relTabs;
      else if(DEBUG)
        System.out.println("DoppelteTabelleBlock: Falsche Anzahl Tabulatoren.");
    }
  }

  public void setAusrichtung(Ausrichtung[] ausrichtung, boolean linkeTabelle) {
    if(linkeTabelle) {
      if(ausrichtung.length == spaltenLinks)
        ausrichtungLinks = ausrichtung;
      else if(DEBUG)
        System.out.println("DoppelteTabelleBlock: Ausrichtung - Falsche Anzahl.");
    }
    else {
      if(ausrichtung.length == spaltenRechts)
        ausrichtungRechts = ausrichtung;
      else if(DEBUG)
        System.out.println("DoppelteTabelleBlock: Ausrichtung - Falsche Anzahl.");
    }
  }

}
