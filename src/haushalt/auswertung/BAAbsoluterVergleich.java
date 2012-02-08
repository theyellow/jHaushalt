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

package haushalt.auswertung;

import java.awt.Font;

import haushalt.auswertung.bloecke.LeererBlock;
import haushalt.auswertung.bloecke.TabellenBlock;
import haushalt.auswertung.bloecke.TextBlock;
import haushalt.daten.Datenbasis;
import haushalt.daten.Euro;
import haushalt.daten.EinzelKategorie;
import haushalt.daten.zeitraum.AbstractZeitraum;
import haushalt.daten.zeitraum.Jahr;
import haushalt.gui.Haushalt;
import haushalt.gui.TextResource;
import haushalt.gui.generischerdialog.AbstractGDPane;
import haushalt.gui.generischerdialog.EinOderAlleRegisterGDP;
import haushalt.gui.generischerdialog.MehrereKategorienGDP;
import haushalt.gui.generischerdialog.ProzentGDP;
import haushalt.gui.generischerdialog.ZeitraumGDP;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.3/2008.03.19
 */

/*
 * 2008.03.19 BugFix: Breite erste Spalte jetzt Integer
 * 2007.05.30 Internationalisierung
 * 2007.02.20 Erweiterung: Festlegen der Breite der ersten Spalte
 * 2006.06.11 BugFix: Nur die ausgewählten Kategorien werden
 *            berechnet
 * 2004.08.22 Erste Version
 */

public class BAAbsoluterVergleich extends AbstractBlockAuswertung {
  private static final long serialVersionUID = 1L;
  private static final TextResource res = TextResource.get();

  public static final String ueberschrift = res.getString("headline_absolute_comparison");
  
  public BAAbsoluterVergleich(Haushalt haushalt, Datenbasis db, String name) {
    super(haushalt, db, name);
    AbstractGDPane[] panes = new AbstractGDPane[5];
		panes[0] = new ZeitraumGDP(res.getString("first_period")+":", new Jahr(2006));
		panes[1] = new ZeitraumGDP(res.getString("second_period")+":", new Jahr(2007));
		panes[2] = new EinOderAlleRegisterGDP(res.getString("register")+":", db, null);
    panes[3] = new MehrereKategorienGDP(res.getString("categories")+":", db);
    panes[4] = new ProzentGDP(res.getString("width_first_column")+":");
		erzeugeEigenschaften(haushalt.getFrame(), ueberschrift, panes);
  }

  protected String berechneAuswertung(Object[] werte) {
  	AbstractZeitraum zeitraum1 = (AbstractZeitraum) werte[0];
		AbstractZeitraum zeitraum2 = (AbstractZeitraum) werte[1];
    String register = (String) werte[2];
    EinzelKategorie[] kategorien = (EinzelKategorie[]) werte[3];
    double prozentErsteSpalte = ((Integer) werte[4]).doubleValue();
		int anzKat = kategorien.length;
    boolean unterkategorienVerwenden = ((MehrereKategorienGDP)panes[3]).getUnterkategorienVerwenden();
		Euro[] summen1 = db.getKategorieSalden(kategorien, zeitraum1, register, unterkategorienVerwenden);
		Euro[] summen2 = db.getKategorieSalden(kategorien, zeitraum2, register, unterkategorienVerwenden);
		tabelle = new String[anzKat+2][4];
		tabelle[0][0] = res.getString("category");
		tabelle[0][1] = ""+zeitraum1;
		tabelle[0][2] = ""+zeitraum2;
		tabelle[0][3] = res.getString("difference");
		tabelle[anzKat+1][0] = res.getString("total");

		Euro summe1 = new Euro();
		Euro summe2 = new Euro();		
		for(int i=0;i<anzKat;i++) {
			tabelle[i+1][0] = ""+kategorien[i];
			tabelle[i+1][1] = ""+summen1[i];
			tabelle[i+1][2] = ""+summen2[i];
			tabelle[i+1][3] = ""+summen1[i].sub(summen2[i]);
			summe1.sum(summen1[i]);
			summe2.sum(summen2[i]);
		}
		tabelle[anzKat+1][1] = ""+summe1;
		tabelle[anzKat+1][2] = ""+summe2;
		tabelle[anzKat+1][3] = ""+summe1.sub(summe2);

		// Vorhandene Blöcke löschen und neu berechnete einfügen
		String titel = res.getString("absolute_comparison")+" ("+tabelle[0][1]+" "+res.getString("and")+" "+tabelle[0][2];
		if(register == null)
		  titel += ")";
		else
		  titel += ", " + register + ")";
		loescheBloecke();
		TextBlock block1 = new TextBlock(titel);
		block1.setFont(new Font(haushalt.getFontname(), Font.BOLD, haushalt.getFontgroesse()+6));
		addDokumentenBlock(block1);
		addDokumentenBlock(new LeererBlock(1));
		TabellenBlock block2 = new TabellenBlock(tabelle);
		block2.setFont(new Font(haushalt.getFontname(), Font.PLAIN, haushalt.getFontgroesse()));
    final TabellenBlock.Ausrichtung[] attribute = {
        TabellenBlock.Ausrichtung.LINKS, 
        TabellenBlock.Ausrichtung.RECHTS, 
        TabellenBlock.Ausrichtung.RECHTS, 
        TabellenBlock.Ausrichtung.RECHTS};
    block2.setAusrichtung(attribute);
    if(prozentErsteSpalte > 0.0D) {
      final double rest = (100.0D - prozentErsteSpalte) / 3.0D;
      final double[] relTabs = {
          0.0D,
          prozentErsteSpalte, 
          prozentErsteSpalte+rest, 
          prozentErsteSpalte+rest * 2.0D,
          };
      block2.setRelTabs(relTabs);
    }
		addDokumentenBlock(block2);
    return titel;
  }

}
