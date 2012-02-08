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

package haushalt.auswertung;

import haushalt.auswertung.bloecke.*;
import haushalt.daten.Datenbasis;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.BooleanGDP;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.MehrereKategorienGDP;
import haushalt.gui.generischerdialog.TextArrayGDP;
import haushalt.gui.generischerdialog.ZahlGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;

import java.awt.Font;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5/2007.07.02
 * @since 2.0
 */

/*
 * 2007.07.02 Internationalisierung
 * 2004.08.22 Erste Version
 */
public class BATortenDiagramm extends AbstractBlockAuswertung { 
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  public static final String ueberschrift = res.getString("headline_pie_chart");
  
  public BATortenDiagramm(Haushalt haushalt, Datenbasis db, String name) {
    super(haushalt, db, name);
    AbstractGDPane[] panes = new AbstractGDPane[6];
    panes[0] = new ZeitraumGDP(res.getString("first_period")+":", new Jahr(2007));
    panes[1] = new ZahlGDP(res.getString("number_of_categories")+":", new Integer(10));
    panes[2] = new EinOderAlleRegisterGDP(res.getString("register")+":", db, null);
    panes[3] = new MehrereKategorienGDP(res.getString("categories")+":", db);
    panes[4] = new TextArrayGDP(res.getString("color_scheme")+":", FarbPaletten.palettenNamen, "Standard");
    panes[5] = new BooleanGDP(res.getString("display_values"), Boolean.FALSE, res.getString("in_percent"));
		erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
  }

  protected String berechneAuswertung(Object[] werte) {
  	AbstractZeitraum zeitraum = (AbstractZeitraum) werte[0];
  	final int maxAnzahlWerte = ((Integer) werte[1]).intValue();
    String register = (String) werte[2];
    EinzelKategorie[] kategorien = (EinzelKategorie[]) werte[3];
    int anzahlKategorien = kategorien.length;
    String farbschema = (String) werte[4];
    Boolean prozent = (Boolean) werte[5];
    
    // ---------------------------------------------------------------------------
    
    Euro[] einnahmen = new Euro[maxAnzahlWerte];
    EinzelKategorie[] kategorienEinnahmen = new EinzelKategorie[maxAnzahlWerte];
    Euro[] ausgaben = new Euro[maxAnzahlWerte];
    EinzelKategorie[] kategorienAusgaben = new EinzelKategorie[maxAnzahlWerte];
    Euro gesamtEinnahmen = new Euro();
    Euro gesamtAusgaben = new Euro();
    Euro kategorieSumme;
    boolean unterkategorienVerwenden = ((MehrereKategorienGDP)panes[3]).getUnterkategorienVerwenden();
    int anzahlEinnahmen = 0;
    int anzahlAusgaben = 0;
    
    for(int i=0; i<anzahlKategorien; i++) {
      kategorieSumme = db.getKategorieSaldo(kategorien[i], zeitraum, register, unterkategorienVerwenden);
      if(kategorieSumme.compareTo(Euro.NULL_EURO)>0) { // Einnahmen:
        gesamtEinnahmen.sum(kategorieSumme);
        for(int j=0; j<maxAnzahlWerte; j++) {
          if(einnahmen[j] == null) {
            einnahmen[j] = kategorieSumme; // Prima! Noch ein Platz frei!
            kategorienEinnahmen[j] = kategorien[i];
            j = maxAnzahlWerte;
            anzahlEinnahmen ++;
          }
          else if(kategorieSumme.compareTo(einnahmen[j]) > 0) {
            for(int k=maxAnzahlWerte-1; k>j; k--) {
              einnahmen[k] = einnahmen[k-1];
              kategorienEinnahmen[k] = kategorienEinnahmen[k-1];
            }
            einnahmen[j] = kategorieSumme;
            kategorienEinnahmen[j] = kategorien[i];
            j = maxAnzahlWerte;
            anzahlEinnahmen ++;
          }
        }
      }
      else if(kategorieSumme.compareTo(Euro.NULL_EURO) < 0) { // Ausgaben:
        kategorieSumme = Euro.NULL_EURO.sub(kategorieSumme);
        gesamtAusgaben.sum(kategorieSumme);
        for(int j=0; j<maxAnzahlWerte; j++) {
          if(ausgaben[j] == null) {
            ausgaben[j] = kategorieSumme; // Prima! Noch ein Platz frei!
            kategorienAusgaben[j] = kategorien[i];
            j = maxAnzahlWerte;
            anzahlAusgaben ++;
          }
          else if(kategorieSumme.compareTo(ausgaben[j]) > 0) {
            for(int k=maxAnzahlWerte-1; k>j; k--) {
              ausgaben[k] = ausgaben[k-1];
              kategorienAusgaben[k] = kategorienAusgaben[k-1];
            }
            ausgaben[j] = kategorieSumme;
            kategorienAusgaben[j] = kategorien[i];
            j = maxAnzahlWerte;
            anzahlAusgaben ++;
          }
        }
      }
    }

		// Vorhandene Blöcke löschen und neu berechnete einfügen
    String titel = res.getString("distribution_income_expenditure")+" ("+zeitraum;
		loescheBloecke();
    if(register == null)
      titel += ")";
    else
      titel += ", "+register+")";
    AbstractBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(haushalt.getFontname(), Font.BOLD, haushalt.getFontgroesse()+6));
    addDokumentenBlock(block1);
    addDokumentenBlock(new LeererBlock(1));
    AbstractBlock block2 = new TortenBlock(
      farbschema, einnahmen, gesamtEinnahmen, ausgaben, gesamtAusgaben
    );
		block2.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()));
    addDokumentenBlock(block2);
    addDokumentenBlock(new LeererBlock(1));

    anzahlEinnahmen = (maxAnzahlWerte<anzahlEinnahmen)?maxAnzahlWerte:anzahlEinnahmen;
    anzahlAusgaben = (maxAnzahlWerte<anzahlAusgaben)?maxAnzahlWerte:anzahlAusgaben;
    String[][] tabelleEinnahmen = new String[anzahlEinnahmen][2];
    String[][] tabelleAusgaben = new String[anzahlAusgaben][2];
    for(int j=0; j<anzahlEinnahmen; j++) {
      tabelleEinnahmen[j][0] = ""+kategorienEinnahmen[j];
      if(prozent)
        tabelleEinnahmen[j][1] = String.format("%1$.1f%%",einnahmen[j].toDouble()*100.0/gesamtEinnahmen.toDouble());
      else
        tabelleEinnahmen[j][1] = ""+einnahmen[j];
    }

    for(int j=0; j<anzahlAusgaben; j++) {
      tabelleAusgaben[j][0] = ""+kategorienAusgaben[j];
      if(prozent)
        tabelleAusgaben[j][1] = ""+String.format("%1$.1f%%",ausgaben[j].toDouble()*100.0/gesamtAusgaben.toDouble());
      else
        tabelleAusgaben[j][1] = ""+ausgaben[j];
    }
    
    final double[] relTabs = {0.0D, 70.0D};
    AbstractTabelleBlock.Ausrichtung[] attribute2 = {
        AbstractTabelleBlock.Ausrichtung.LINKS, 
        AbstractTabelleBlock.Ausrichtung.RECHTS};
    DoppelteTabelleBlock block3 = new DoppelteTabelleBlock(tabelleEinnahmen, tabelleAusgaben);
    block3.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()));
    block3.setRelTabs(relTabs, true);
    block3.setRelTabs(relTabs, false);
    block3.setAusrichtung(attribute2, true);
    block3.setAusrichtung(attribute2, false);
    block3.setHgFarbe(farbschema);
    block3.setLinienFarbe("Weiß");
    block3.setRelRand(0.05D);
    addDokumentenBlock(block3);

    return titel;
  }

}
